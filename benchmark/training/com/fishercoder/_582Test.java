package com.fishercoder;


import com.fishercoder.solutions._582;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by fishercoder on 5/18/17.
 */
public class _582Test {
    private static _582 test;

    private static List<Integer> pid;

    private static List<Integer> ppid;

    private static List<Integer> expected;

    private static Integer kill;

    @Test
    public void test1() {
        _582Test.pid = Arrays.asList(1, 3, 10, 5);
        _582Test.ppid = Arrays.asList(3, 0, 5, 3);
        _582Test.kill = 5;
        _582Test.expected = Arrays.asList(5, 10);
        Assert.assertEquals(_582Test.expected, _582Test.test.killProcess(_582Test.pid, _582Test.ppid, _582Test.kill));
    }

    @Test
    public void test2() {
        _582Test.pid = Arrays.asList(1, 3, 10, 5);
        _582Test.ppid = Arrays.asList(3, 0, 5, 3);
        _582Test.kill = 3;
        _582Test.expected = Arrays.asList(3, 1, 5, 10);
        Assert.assertEquals(_582Test.expected, _582Test.test.killProcess(_582Test.pid, _582Test.ppid, _582Test.kill));
    }
}

