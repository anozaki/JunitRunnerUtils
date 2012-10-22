package net.tanoshi.test.junit.internal;

import java.util.List;

import net.tanoshi.test.junit.JruRunner;

import org.junit.runners.model.Statement;

public class JruRunBefore extends Statement {

    private final Statement fNext;

    private final Object fTarget;

    private final List<JruRunner> fBefores;

    public JruRunBefore(Statement next, List<JruRunner> befores, Object target) {
        fNext = next;
        fBefores = befores;
        fTarget = target;

    }

    @Override
    public void evaluate() throws Throwable {
        for (JruRunner runner : fBefores) {
            runner.onBeforeMethod(fNext, fTarget);
        }

        fNext.evaluate();
    }

}
