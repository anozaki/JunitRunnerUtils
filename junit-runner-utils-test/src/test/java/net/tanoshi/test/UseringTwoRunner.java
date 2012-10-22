package net.tanoshi.test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import junitparams.Parameters;
import net.tanoshi.test.junit.JruJunitRunner;
import net.tanoshi.test.junit.annotation.JruRunWith;
import net.tanoshi.test.junit.runner.JruJUnitParamsRunner;
import net.tanoshi.test.junit.runner.JruMockitoRunner;
import net.tanoshi.test.sample.MockableInterface;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

@RunWith(JruJunitRunner.class)
@JruRunWith({ JruMockitoRunner.class, JruJUnitParamsRunner.class })
public class UseringTwoRunner {

    private static int count = 0;

    @Mock
    MockableInterface testMock;

    @Test
    @Parameters({ "17, false", "22, true" })
    public void testParamRunner(int age, boolean valid) {
        if (age == 17) {
            count++;
            assertEquals(false, valid);
            assertEquals(1, count);
            System.out.println("g1");
        } else if (age == 22) {
            count++;
            assertEquals(true, valid);
            assertEquals(2, count);
            System.out.println("g1");
        }
    }

    @Test
    public void testMockWithParamRunner() {
        when(testMock.test()).thenReturn("This is a test");

        assertEquals("This is a test", testMock.test());
    }
}
