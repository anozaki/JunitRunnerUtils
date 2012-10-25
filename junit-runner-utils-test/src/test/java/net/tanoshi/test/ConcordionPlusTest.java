package net.tanoshi.test;

import net.tanoshi.test.junit.JruJunitRunner;
import net.tanoshi.test.junit.annotation.JruRunWith;
import net.tanoshi.test.junit.runner.JruConcordionPlusRunner;
import net.tanoshi.test.junit.runner.JruMockitoRunner;

import org.junit.runner.RunWith;

@RunWith(JruJunitRunner.class)
@JruRunWith({ JruMockitoRunner.class, JruConcordionPlusRunner.class })
public class ConcordionPlusTest {
   
    public String getText() {
        return "success";
    }

    public String getUnexpectedAssertionMessage() {
        return "unexpected message";
    }

    public int multiply(String row, String column, String expected) {
        return Integer.parseInt(row) * Integer.parseInt(column);
    }
}
