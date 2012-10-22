package net.tanoshi.test.junit.runner;

import java.lang.annotation.Annotation;
import java.util.List;

import net.tanoshi.test.junit.IsExcuteDelegate;
import net.tanoshi.test.junit.JruRunner;

import org.concordion.api.ResultSummary;
import org.concordion.internal.FixtureRunner;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;
import org.junit.runners.model.TestClass;

public class JruConcordionRunner implements JruRunner, IsExcuteDelegate {

    static class FakeFrameworkMethod extends FrameworkMethod {

        public FakeFrameworkMethod() {
            super(null);
        }

        @Override
        public <T extends Annotation> T getAnnotation(Class<T> annotationType) {
            return null;
        }

        @Override
        public Annotation[] getAnnotations() {
            return new Annotation[0];
        }

        @Override
        public String getName() {
            return "[Concordion Specification]";
        }

        @Override
        public int hashCode() {
            return 1;
        }
    }

    private FakeFrameworkMethod fakeMethod;
    private Description fixtureDescription;
    private ResultSummary result;

    @Override
    public void computeTestMethods(List<FrameworkMethod> testMethods) {
        // if (testMethods != null && !testMethods.contains(fakeMethod)) {
        testMethods.add(fakeMethod);
        // }
    }

    @Override
    public Description getDescription(FrameworkMethod method) {
        if (method == fakeMethod) {
            return fixtureDescription;
        }
        return null;
    }

    @Override
    public void init(TestClass testClass) {
        String testDescription = ("[Concordion Specification for '" + testClass.getJavaClass().getSimpleName())
                .replaceAll("Test$", "']"); // Based on suggestion by Danny Guerrier
        fixtureDescription = Description.createTestDescription(testClass.getJavaClass(), testDescription);
        fakeMethod = new FakeFrameworkMethod();

    }

    @Override
    public Statement methodInvoker(FrameworkMethod method, final Object test) {
        if (method == fakeMethod) {
            return new Statement() {

                @Override
                public void evaluate() throws Throwable {
                    result = new FixtureRunner().run(test);
                }
            };
        }

        return null;
    }

    @Override
    public void onAfter(Statement statement, Object testObj) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onAfterMethod(Statement statement, Object testObj) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onBefore(Statement statement, Object testObj) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onBeforeMethod(Statement statement, Object testObj) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onRun(RunNotifier notifier) {
    }

    @Override
    public boolean postRun(FrameworkMethod method, RunNotifier notifier) {
        if (method == fakeMethod && result != null && result.getIgnoredCount() > 0) {
            notifier.fireTestIgnored(fixtureDescription);
            return true;
        }
        return false;
    }

    @Override
    public boolean runTest(Statement statement, FrameworkMethod method, RunNotifier notifier) {
        if (method == fakeMethod) {
        }
        return false;
    }

    @Override
    public void updateChildren(List<FrameworkMethod> child) {
    }

}
