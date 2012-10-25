package net.tanoshi.test.junit.runner;

import java.util.List;

import net.tanoshi.test.junit.JruRunner;

import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;
import org.junit.runners.model.TestClass;

import com.googlecode.gwt.test.internal.GwtConfig;
import com.googlecode.gwt.test.internal.GwtFactory;
import com.googlecode.gwt.test.internal.junit.GwtRunListener;

public class JruGwtTestUtilRunner implements JruRunner {

    private TestClass testClass;

    public void computeTestMethods(List<FrameworkMethod> testMethods) {
        // TODO Auto-generated method stub

    }

    public Description getDescription(FrameworkMethod method) {
        return null;
    }

    public void init(TestClass testClass) {
        this.testClass = testClass;

        GwtFactory.initializeIfNeeded();
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
        notifier.addListener(new GwtRunListener());
        GwtConfig.get().setupGwtModule(testClass.getJavaClass());
    }

    @Override
    public void updateChildren(List<FrameworkMethod> child) {

    }

}
