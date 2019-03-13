package com.pushtorefresh.storio3.test;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.mockito.Mockito;


public class RepeatRuleTest {
    private Statement test = Mockito.mock(Statement.class);

    private Description description = Mockito.mock(Description.class);

    private Repeat repeat = Mockito.mock(Repeat.class);

    @Test
    public void repeats10Times() throws Throwable {
        Mockito.when(description.getAnnotation(Repeat.class)).thenReturn(repeat);
        Mockito.when(repeat.times()).thenReturn(10);
        new RepeatRule().apply(test, description).evaluate();
        Mockito.verify(test, Mockito.times(10)).evaluate();
    }

    @Test
    public void noAnnotation() throws Throwable {
        Mockito.when(description.getAnnotation(Repeat.class)).thenReturn(null);
        new RepeatRule().apply(test, description).evaluate();
        Mockito.verify(test).evaluate();
    }

    @Test
    public void lessThan1Time() throws Throwable {
        Mockito.when(description.getAnnotation(Repeat.class)).thenReturn(repeat);
        Mockito.when(repeat.times()).thenReturn(0);
        try {
            new RepeatRule().apply(test, description).evaluate();
            Assert.fail();
        } catch (IllegalArgumentException expected) {
            assertThat(expected).hasMessage("Repeat times should be >= 1, times = 0");
        }
    }

    @Test
    public void lazyWithoutAnnotation() throws Throwable {
        Mockito.when(description.getAnnotation(Repeat.class)).thenReturn(null);
        new RepeatRule().apply(test, description);
        Mockito.verify(test, Mockito.never()).evaluate();
    }

    @Test
    public void lazyWithAnnotation() throws Throwable {
        Mockito.when(description.getAnnotation(Repeat.class)).thenReturn(repeat);
        Mockito.when(repeat.times()).thenReturn(10);
        new RepeatRule().apply(test, description);
        Mockito.verify(test, Mockito.never()).evaluate();
    }
}

