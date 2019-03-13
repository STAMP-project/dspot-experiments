package com.fishercoder;


import _635.LogSystem;
import java.util.Arrays;
import java.util.List;
import junit.framework.TestCase;
import org.junit.Test;


/**
 * Created by fishercoder on 9/9/17.
 */
public class _635Test {
    private static LogSystem logSystem;

    private static List<Integer> expected;

    @Test
    public void test1() {
        _635Test.logSystem.put(1, "2017:01:01:23:59:59");
        _635Test.logSystem.put(2, "2017:01:01:22:59:59");
        _635Test.logSystem.put(3, "2016:01:01:00:00:00");
        _635Test.expected = Arrays.asList(1, 2, 3);
        TestCase.assertEquals(_635Test.expected, _635Test.logSystem.retrieve("2016:01:01:01:01:01", "2017:01:01:23:00:00", "Year"));
    }

    @Test
    public void test2() {
        _635Test.logSystem.put(1, "2017:01:01:23:59:59");
        _635Test.logSystem.put(2, "2017:01:01:22:59:59");
        _635Test.logSystem.put(3, "2016:01:01:00:00:00");
        _635Test.expected = Arrays.asList(1, 2);
        TestCase.assertEquals(_635Test.expected, _635Test.logSystem.retrieve("2016:01:01:01:01:01", "2017:01:01:23:00:00", "Hour"));
    }
}

