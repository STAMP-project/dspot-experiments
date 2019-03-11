package com.fishercoder;


import _332.Solution1;
import com.fishercoder.common.utils.CommonUtils;
import java.util.List;
import org.junit.Test;


public class _332Test {
    private static Solution1 solution1;

    private static String[][] tickets;

    private static List<String> expected;

    @Test
    public void test1() {
        _332Test.tickets = new String[][]{ new String[]{ "MUC", "LHR" }, new String[]{ "JFK", "MUC" }, new String[]{ "SFO", "SJC" }, new String[]{ "LHR", "SFO" } };
        _332Test.expected = _332Test.solution1.findItinerary(_332Test.tickets);
        CommonUtils.print(_332Test.expected);
    }

    @Test
    public void test2() {
        _332Test.tickets = new String[][]{ new String[]{ "JFK", "SFO" }, new String[]{ "JFK", "ATL" }, new String[]{ "SFO", "ATL" }, new String[]{ "ATL", "JFK" }, new String[]{ "ATL", "SFO" } };
        _332Test.expected = _332Test.solution1.findItinerary(_332Test.tickets);
        CommonUtils.print(_332Test.expected);
    }
}

