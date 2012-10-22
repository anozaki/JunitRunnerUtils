package net.tanoshi.test.junit;

import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;


public interface IsExcuteDelegate {
    public boolean runTest(Statement statement, FrameworkMethod method, RunNotifier notifier);
    
    Statement methodInvoker(FrameworkMethod method, Object test);

    boolean postRun(FrameworkMethod method, RunNotifier notifier);
}
