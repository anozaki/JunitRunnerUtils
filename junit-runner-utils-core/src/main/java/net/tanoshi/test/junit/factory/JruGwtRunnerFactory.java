package net.tanoshi.test.junit.factory;

import java.lang.reflect.Constructor;

import net.tanoshi.test.junit.JruRunnerFactory;
import net.tanoshi.test.junit.internal.JruRunnerImpl;

import org.junit.runner.Runner;

import com.googlecode.gwt.test.internal.GwtFactory;

public class JruGwtRunnerFactory implements JruRunnerFactory {

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

    public JruGwtRunnerFactory() {
        GwtFactory.initializeIfNeeded();
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
        Constructor<?> constructor;

        Class<?> runnerClass = GwtFactory.get().getClassLoader().loadClass(runnerClassName.getName());
        Class<?> testedClass = GwtFactory.get().getClassLoader().loadClass(constructorParam.getName());
        constructor = runnerClass.getConstructor(Class.class.getClass());
        return (Runner) constructor.newInstance(testedClass);
    }

}
