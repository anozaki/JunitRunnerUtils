package net.tanoshi.test.junit;

import java.util.List;

import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;
import org.junit.runners.model.TestClass;

public interface JruRunner {

    void computeTestMethods(List<FrameworkMethod> testMethods);

    Description getDescription(FrameworkMethod method);

    /**
     * called right after all JruRunner has been instantiated in order.
     */
    void init(TestClass testClass);

    void onAfter(Statement statement, Object testObj);

    void onAfterMethod(Statement statement, Object testObj);

    void onBefore(Statement statement, Object testObj);

    void onBeforeMethod(Statement statement, Object testObj);

    void onRun(RunNotifier notifier);

    void updateChildren(List<FrameworkMethod> child);
}
