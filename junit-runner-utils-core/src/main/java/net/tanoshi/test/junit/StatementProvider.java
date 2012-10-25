package net.tanoshi.test.junit;

import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;


public interface StatementProvider {
    Statement get(FrameworkMethod method);
}
