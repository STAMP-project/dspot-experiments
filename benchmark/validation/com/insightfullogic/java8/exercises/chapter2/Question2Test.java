package com.insightfullogic.java8.exercises.chapter2;


import Question2.formatter;
import java.util.Calendar;
import org.junit.Assert;
import org.junit.Test;


public class Question2Test {
    @Test
    public void exampleInB() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 1970);
        cal.set(Calendar.MONTH, Calendar.JANUARY);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        String formatted = formatter.get().getFormat().format(cal.getTime());
        Assert.assertEquals("01-Jan-1970", formatted);
    }
}

