package com.insightfullogic.java8.examples.chapter2;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.swing.JButton;
import junit.framework.Assert;
import org.junit.Test;


public class LambdaExercises {
    @Test
    public void _1a() {
        Assert.assertTrue("Shown in the next chapter", true);
    }

    @Test
    public void _1b() {
        // If you were to model each operation on a calculator as a function.
        Function<Double, Double> negate = ( x) -> (-1) * x;
        Function<Double, Double> square = ( x) -> x * x;
        Function<Double, Double> percent = ( x) -> 100 * x;
    }

    @Test
    public void _1c() {
        Function<Integer, Integer> one = ( x) -> x + 1;
        // 2 isn't
        Function<Integer, Boolean> three = ( x) -> x == 1;
    }

    @Test
    public void _2a() {
        Assert.assertTrue("ThreadLocal.withInitial", true);
    }

    @Test
    public void _2b() {
        ThreadLocal<DateFormat> threadSafeFormatter = ThreadLocal.withInitial(() -> DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.UK));
        DateFormat formatter = threadSafeFormatter.get();
        Assert.assertEquals("01-Jan-1970", formatter.format(new Date(0)));
    }

    @Test
    public void _3a() {
        // yes
        Runnable helloWorld = () -> System.out.println("hello world");
    }

    @Test
    public void _3b() {
        // Yes
        JButton button = new JButton();
        button.addActionListener(( event) -> System.out.println(event.getActionCommand()));
    }

    @Test
    public void _3c() {
        // No
        // check(x -> x > 5);
    }

    interface IntPred {
        boolean test(Integer value);
    }
}

