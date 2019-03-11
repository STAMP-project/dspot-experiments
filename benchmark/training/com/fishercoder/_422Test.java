package com.fishercoder;


import com.fishercoder.solutions._422;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import junit.framework.Assert;
import org.junit.Test;


public class _422Test {
    private static _422 test;

    private static boolean expected;

    private static boolean actual;

    private static List<String> words;

    @Test
    public void test1() {
        _422Test.words = new ArrayList<>(Arrays.asList("abcd", "bnrt", "crmy", "dtye"));
        _422Test.expected = true;
        _422Test.actual = _422Test.test.validWordSquare(_422Test.words);
        Assert.assertEquals(_422Test.expected, _422Test.actual);
    }

    @Test
    public void test2() {
        // abcd
        // bnrt
        // crm
        // dt
        _422Test.words = new ArrayList<>(Arrays.asList("abcd", "bnrt", "crm", "dt"));
        _422Test.expected = true;
        _422Test.actual = _422Test.test.validWordSquare(_422Test.words);
        Assert.assertEquals(_422Test.expected, _422Test.actual);
    }

    @Test
    public void test3() {
        // ball
        // asee
        // let
        // lep
        _422Test.words = new ArrayList<>(Arrays.asList("ball", "asee", "let", "lep"));
        _422Test.expected = false;
        _422Test.actual = _422Test.test.validWordSquare(_422Test.words);
        Assert.assertEquals(_422Test.expected, _422Test.actual);
    }
}

