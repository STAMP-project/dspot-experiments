package com.fishercoder;


import _734.Solution1;
import junit.framework.TestCase;
import org.junit.Test;


public class _734Test {
    private static Solution1 solution1;

    private static String[] words1;

    private static String[] words2;

    private static String[][] pairs;

    @Test
    public void test1() {
        _734Test.words1 = new String[]{ "great", "acting", "skills" };
        _734Test.words2 = new String[]{ "fine", "drama", "talent" };
        _734Test.pairs = new String[][]{ new String[]{ "great", "fine" }, new String[]{ "acting", "drama" }, new String[]{ "skills", "talent" } };
        TestCase.assertEquals(true, _734Test.solution1.areSentencesSimilar(_734Test.words1, _734Test.words2, _734Test.pairs));
    }

    @Test
    public void test2() {
        String[] words1 = new String[]{ "one", "excellent", "meal" };
        String[] words2 = new String[]{ "one", "good", "dinner" };
        String[][] pairs = new String[][]{ new String[]{ "great", "good" }, new String[]{ "extraordinary", "good" }, new String[]{ "well", "good" }, new String[]{ "wonderful", "good" }, new String[]{ "excellent", "good" }, new String[]{ "dinner", "meal" }, new String[]{ "fine", "good" }, new String[]{ "nice", "good" }, new String[]{ "any", "one" }, new String[]{ "unique", "one" }, new String[]{ "some", "one" }, new String[]{ "the", "one" }, new String[]{ "an", "one" }, new String[]{ "single", "one" }, new String[]{ "a", "one" }, new String[]{ "keep", "own" }, new String[]{ "truck", "car" }, new String[]{ "super", "very" }, new String[]{ "really", "very" }, new String[]{ "actually", "very" }, new String[]{ "extremely", "very" }, new String[]{ "have", "own" }, new String[]{ "possess", "own" }, new String[]{ "lunch", "meal" }, new String[]{ "super", "meal" }, new String[]{ "food", "meal" }, new String[]{ "breakfast", "meal" }, new String[]{ "brunch", "meal" }, new String[]{ "wagon", "car" }, new String[]{ "automobile", "car" }, new String[]{ "auto", "car" }, new String[]{ "fruits", "meal" }, new String[]{ "vehicle", "car" }, new String[]{ "entertain", "have" }, new String[]{ "drink", "have" }, new String[]{ "eat", "have" }, new String[]{ "take", "have" } };
        TestCase.assertEquals(true, _734Test.solution1.areSentencesSimilar(words1, words2, pairs));
    }
}

