package net.tanoshi.test.junit.internal;

import java.util.ArrayList;
import java.util.List;

import net.tanoshi.test.junit.IsExcuteDelegate;
import net.tanoshi.test.junit.JruRunner;
import net.tanoshi.test.junit.annotation.JruRunWith;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.internal.AssumptionViolatedException;
import org.junit.internal.runners.model.EachTestNotifier;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

public class JruRunnerImpl extends BlockJUnit4ClassRunner {

    private List<JruRunner> runners = new ArrayList<JruRunner>();
    private Description cachedDescription;

    public JruRunnerImpl(Class<?> klass) throws InitializationError, InstantiationException, IllegalAccessException {
        super(klass);

        init();
    }

    public void init() throws InstantiationException, IllegalAccessException {
        JruRunWith annotation = getTestClass().getJavaClass().getAnnotation(JruRunWith.class);
        if (annotation == null) {
            throw new IllegalStateException("You must define @JruRunWith when using JruRunner.");
        }

        for (Class<? extends JruRunner> runnerClass : annotation.value()) {
            runners.add(runnerClass.newInstance());
        }

        for (JruRunner runner : runners) {
            runner.init(getTestClass());
        }
    }

    public void run(final RunNotifier notifier) {
        for (JruRunner runner : runners) {
            runner.onRun(notifier);
        }

        super.run(notifier);
    }

    @Override
    protected void collectInitializationErrors(List<Throwable> errors) {
        super.collectInitializationErrors(errors);

        // TODO should do there own error checking...

        for (Throwable t : errors) {
            t.printStackTrace();
        }
        errors.clear();
    }

    @Override
    protected List<FrameworkMethod> computeTestMethods() {
        List<FrameworkMethod> methods = new ArrayList<FrameworkMethod>();

        methods.addAll(getTestClass().getAnnotatedMethods(Test.class));

        // this null check is here since validation is run at construct :(
        if (runners != null) {
            for (JruRunner runner : runners) {
                runner.computeTestMethods(methods);
            }
        }

        return methods;
    }

    @Override
    protected Description describeChild(FrameworkMethod method) {
        for (JruRunner runner : runners) {
            Description d = runner.getDescription(method);
            if (d != null) {
                return d;
            }
        }
        return super.describeChild(method);
    }

    @Override
    public Description getDescription() {
        if (cachedDescription == null) {
            cachedDescription = Description.createSuiteDescription(getName(), getTestClass().getAnnotations());

            List<FrameworkMethod> child = new ArrayList<FrameworkMethod>();
            child.addAll(super.getChildren());

            // we will give the runners some chance to modify the list.
            for (JruRunner runner : runners) {
                runner.updateChildren(child);
            }

            // create the description after the list is scrubbed.
            for (FrameworkMethod method : child) {
                cachedDescription.addChild(describeChild(method));
            }
        }

        return cachedDescription;
    }

    @Override
    protected Statement methodInvoker(FrameworkMethod method, Object test) {
        Statement methodInvoker = null;
        for (JruRunner runner : runners) {
            if (runner instanceof IsExcuteDelegate) {
                methodInvoker = ((IsExcuteDelegate) runner).methodInvoker(method, test);
                if (methodInvoker != null) {
                    return methodInvoker;
                }
            }
        }

        return super.methodInvoker(method, test);
    }

    @Override
    protected void runChild(FrameworkMethod method, RunNotifier notifier) {
        runTest(method, notifier);

        for (JruRunner runner : runners) {
            if (runner instanceof IsExcuteDelegate) {
                if (((IsExcuteDelegate) runner).postRun(method, notifier)) {
                    return;
                }
            }
        }
    }

    protected void runTest(FrameworkMethod method, RunNotifier notifier) {
        Description description = describeChild(method);
        if (method.getAnnotation(Ignore.class) != null) {
            notifier.fireTestIgnored(description);
        } else {
            for (JruRunner runner : runners) {
                if (runner instanceof IsExcuteDelegate) {
                    if (((IsExcuteDelegate) runner).runTest(methodBlock(method), method, notifier)) {
                        return;
                    }
                }
            }

            Statement statement = methodBlock(method);
            EachTestNotifier eachNotifier = new EachTestNotifier(notifier, description);
            eachNotifier.fireTestStarted();
            try {
                statement.evaluate();
            } catch (AssumptionViolatedException e) {
                eachNotifier.addFailedAssumption(e);
            } catch (Throwable e) {
                eachNotifier.addFailure(e);
            } finally {
                eachNotifier.fireTestFinished();
            }
        }
    }

    @Override
    @Deprecated
    protected Statement withAfters(FrameworkMethod method, Object target, Statement statement) {
        return super.withAfters(method, target, new JruRunAfter(statement, runners, target));
    }

    @SuppressWarnings("deprecation")
    @Override
    protected Statement withBefores(FrameworkMethod method, Object target, Statement statement) {

        /*
         * As far as I can tell the default runner doesn't have a way to get to the actual object. So we'll just take
         * over this function and add statement h andler.
         */
        return super.withBefores(method, target, new JruRunBefore(statement, runners, target));
    }

}
