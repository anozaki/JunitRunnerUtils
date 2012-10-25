package net.tanoshi.test.junit.runner;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import junitparams.Parameters;
import junitparams.internal.ParameterisedTestClassRunner;
import junitparams.internal.TestMethod;
import net.tanoshi.test.junit.IsExcuteDelegate;
import net.tanoshi.test.junit.JruRunner;
import net.tanoshi.test.junit.StatementProvider;

import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;
import org.junit.runners.model.TestClass;

public class JruJUnitParamsRunner implements JruRunner, IsExcuteDelegate {

    public class ParameterizedTestExtention extends ParameterisedTestClassRunner {

        public ParameterizedTestExtention(TestClass testClass) {
            super(testClass);
        }

        public List<FrameworkMethod> computeFrameworkMethods() {
            List<FrameworkMethod> resultMethods = new ArrayList<FrameworkMethod>();

            for (TestMethod testMethod : testMethodsList) {
                if (testMethod.isParameterised()) {
                    addTestMethodForEachParamSet(resultMethods, testMethod);
                }
            }

            return resultMethods;
        }
        
        public List<FrameworkMethod> getParametizedMethods() {
            List<FrameworkMethod> resultMethods = new ArrayList<FrameworkMethod>();
            for(TestMethod m : testMethodsList) {
                if(m.isParameterised()) {
                    resultMethods.add(m.frameworkMethod());
                }
            }
            return resultMethods;
        }

        protected void addTestMethodForEachParamSet(List<FrameworkMethod> resultMethods, TestMethod testMethod) {
            if (testMethod.isNotIgnored()) {
                int paramSetSize = testMethod.parametersSets().length;
                for (int i = 0; i < paramSetSize; i++)
                    addTestMethodOnce(resultMethods, testMethod);
            } else {
                addTestMethodOnce(resultMethods, testMethod);
            }
        }

        protected void addTestMethodOnce(List<FrameworkMethod> resultMethods, TestMethod testMethod) {
            resultMethods.add(testMethod.frameworkMethod());
        }
    };

    private ParameterizedTestExtention parameterisedRunner;
    private TestClass testClass;

    public void computeTestMethods(List<FrameworkMethod> testMethods) {
        Iterator<FrameworkMethod> iterator = testMethods.iterator();
        while (iterator.hasNext()) {
            FrameworkMethod method = iterator.next();
            if (method.getAnnotation(Parameters.class) != null) {
                iterator.remove();
            }
        }

        testMethods.addAll(parameterisedRunner.computeFrameworkMethods());
    }

    public Description getDescription(FrameworkMethod method) {
        
        return describeMethod(method);                
    }

    public void init(TestClass testClass) {
        this.testClass = testClass;
        parameterisedRunner = new ParameterizedTestExtention(testClass);
    }

    public Statement methodInvoker(FrameworkMethod method, Object test) {
        return parameterisedRunner.parameterisedMethodInvoker(method, test);
    }

    public void onAfter(Statement statement, Object testObj) {
    }

    public void onAfterMethod(Statement statement, Object testObj) {
    }

    public void onBefore(Statement statement, Object testObj) {
    }

    public void onBeforeMethod(Statement statement, Object testObj) {
    }

    public void onRun(RunNotifier notifier) {
    }

    public boolean runTest(StatementProvider statementProvider, FrameworkMethod method, RunNotifier notifier) {
        if (handleIgnored(method, notifier)) {
            return false;
        }

        TestMethod testMethod = parameterisedRunner.testMethodFor(method);
        if (parameterisedRunner.shouldRun(testMethod)) {
            System.out.println("Running this test.");
            parameterisedRunner.runParameterisedTest(testMethod, statementProvider.get(method), notifier);
            return true;
        }

        return false;
    }

    protected Description describeChild(FrameworkMethod method) {
        return Description.createTestDescription(testClass.getJavaClass(), method.getName(), method.getAnnotations());
    }

    private Description describeMethod(FrameworkMethod method) {
        Description child = parameterisedRunner.describeParameterisedMethod(method);

        if (child == null) {
            child = describeChild(method);
        }

        return child;
    }

    private boolean handleIgnored(FrameworkMethod method, RunNotifier notifier) {
        TestMethod testMethod = parameterisedRunner.testMethodFor(method);
        if (testMethod.isIgnored()) {
            notifier.fireTestIgnored(describeMethod(method));
        }

        return testMethod.isIgnored();
    }

    @Override
    public void updateChildren(List<FrameworkMethod> child) {
        parameterisedRunner.returnListOfMethods();
        
        Iterator<FrameworkMethod> iterator = child.iterator();
        List<FrameworkMethod> computeFrameworkMethods = parameterisedRunner.computeFrameworkMethods();
        while (iterator.hasNext()) {
            FrameworkMethod method = iterator.next();
            if (computeFrameworkMethods.contains(method)) {
                iterator.remove();
            }
        }
        
        child.addAll(parameterisedRunner.getParametizedMethods());
    }

    @Override
    public boolean postRun(FrameworkMethod method, RunNotifier notifier) {
        return false;
    }

}
