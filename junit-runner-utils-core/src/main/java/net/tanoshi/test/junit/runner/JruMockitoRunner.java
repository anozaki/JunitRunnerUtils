package net.tanoshi.test.junit.runner;

import java.util.List;

import net.tanoshi.test.junit.JruRunner;

import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;
import org.junit.runners.model.TestClass;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.runners.util.FrameworkUsageValidator;

public class JruMockitoRunner implements JruRunner {

    public void computeTestMethods(List<FrameworkMethod> testMethods) {
    }

    public Description getDescription(FrameworkMethod method) {
        return null;
    }

    public void init(TestClass testClass) {
    }

    public void onAfter(Statement statement, Object testObj) {
    }

    public void onAfterMethod(Statement statement, Object testObj) {
    }

    public void onBefore(Statement statement, Object testObj) {
    }

    public void onBeforeMethod(Statement statement, Object testObj) {
        MockitoAnnotations.initMocks(testObj);

    }

    public void onRun(RunNotifier notifier) {
        notifier.addListener(new FrameworkUsageValidator(notifier));
    }

    @Override
    public void updateChildren(List<FrameworkMethod> child) {

    }

}
