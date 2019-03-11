package com.fishercoder;


import _127.Solution1;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class _127Test {
    private static Solution1 solution1;

    private static List<String> wordList;

    @Test
    public void test1() {
        _127Test.wordList = new ArrayList<>(Arrays.asList("hot", "dot", "dog", "lot", "log"));
        Assert.assertEquals(0, _127Test.solution1.ladderLength("hit", "cog", _127Test.wordList));
    }

    @Test
    public void test2() {
        _127Test.wordList = new ArrayList<>(Arrays.asList("hot", "dot", "dog", "lot", "log", "cog"));
        Assert.assertEquals(5, _127Test.solution1.ladderLength("hit", "cog", _127Test.wordList));
    }
}

