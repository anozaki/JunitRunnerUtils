package net.tanoshi.test.junit;

import net.tanoshi.test.junit.annotation.JruRunnerFactoryDelegate;
import net.tanoshi.test.junit.internal.DefaultJruRunnerFactory;

import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.manipulation.Filterable;
import org.junit.runner.manipulation.NoTestsRemainException;
import org.junit.runner.notification.RunNotifier;

public class JruJunitRunner extends Runner implements Filterable {

    private final Runner runner;

    public JruJunitRunner(Class<?> klass) throws Exception {
        JruRunnerFactoryDelegate factoryAnnon = klass.getAnnotation(JruRunnerFactoryDelegate.class);
        if(factoryAnnon != null) {
            JruRunnerFactory factory = (JruRunnerFactory) factoryAnnon.value().newInstance();
            runner = factory.createRunner(klass);
        } else {
            runner = new DefaultJruRunnerFactory().createRunner(klass);
        }
    }

    public void filter(Filter filter) throws NoTestsRemainException {
        if (Filterable.class.isInstance(runner)) {
            ((Filterable) runner).filter(filter);
        }
    }

    @Override
    public Description getDescription() {
        return runner.getDescription();
    }

    @Override
    public void run(RunNotifier notifier) {
        runner.run(notifier);
    }


}
