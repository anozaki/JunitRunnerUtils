package net.tanoshi.test.junit;

import org.junit.runner.Runner;


public interface JruRunnerFactory {

    Runner createRunner(Class<?> klass) throws Exception;

}
