package net.tanoshi.test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import net.tanoshi.test.junit.JruJunitRunner;
import net.tanoshi.test.junit.annotation.JruRunWith;
import net.tanoshi.test.junit.runner.JruMockitoRunner;
import net.tanoshi.test.sample.MockableInterface;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

@RunWith(JruJunitRunner.class)
@JruRunWith({ JruMockitoRunner.class })
public class UsingRunnerTest {

    @Mock
    MockableInterface testMock;
    
    @Test
    public void testRunner() {
        when(testMock.test()).thenReturn("This is a test");
        
        assertEquals("This is a test", testMock.test());
    }

    @Test
    public void testRunner2() {
        assertEquals(null, testMock.test());
    }
}
