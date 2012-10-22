package net.tanoshi.test.junit.internal;

import net.tanoshi.test.junit.JruRunnerFactory;

import org.junit.runner.Runner;

public class DefaultJruRunnerFactory implements JruRunnerFactory {

    /*
     * borrowing code from Mockito
     */
    private static boolean jUnit45OrHigher;

    static {
        try {
            Class.forName("org.junit.runners.BlockJUnit4ClassRunner");
            jUnit45OrHigher = true;
        } catch (Throwable t) {
            jUnit45OrHigher = false;
        }
    }

    public boolean isjUnit45OrHigher() {
        return jUnit45OrHigher;
    }

    @Override
    public Runner createRunner(Class<?> klass) throws Exception {
        if (jUnit45OrHigher) {
            return newInstance(JruRunnerImpl.class, klass);
        } else {
            throw new UnsupportedOperationException("Future implementation");
        }
    }

    private Runner newInstance(Class<?> runnerClassName, Class<?> constructorParam) throws Exception {
        return (Runner) runnerClassName.getConstructor(Class.class).newInstance(constructorParam);
    }

}
