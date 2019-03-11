package org.jabref.logic.layout.format;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class RisMonthTest {
    @Test
    public void testEmpty() {
        Assertions.assertEquals("", new RisMonth().format(""));
    }

    @Test
    public void testNull() {
        Assertions.assertEquals("", new RisMonth().format(null));
    }

    @Test
    public void testMonth() {
        Assertions.assertEquals("12", new RisMonth().format("dec"));
    }

    @Test
    public void testInvalidMonth() {
        Assertions.assertEquals("abcd", new RisMonth().format("abcd"));
    }
}

