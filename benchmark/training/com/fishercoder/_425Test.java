package com.fishercoder;


import com.fishercoder.solutions._425;
import java.util.List;
import org.junit.Test;


/**
 * Created by stevesun on 6/3/17.
 */
public class _425Test {
    private static _425 test;

    private static String[] words;

    @Test
    public void test1() {
        _425Test.words = new String[]{ "area", "lead", "wall", "lady", "ball" };
        List<List<String>> result = _425Test.test.wordSquares(_425Test.words);
    }
}

