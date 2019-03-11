package com.alibaba.json.test;


import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import junit.framework.TestCase;


public class SymbolTableDupTest extends TestCase {
    private HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();

    private Set<Integer> dupHashCodes = new HashSet<Integer>();

    private HashMap<Integer, List<String>> dupList = new HashMap<Integer, List<String>>();

    private final int VALUE = 114788;

    public void test_0() throws Exception {
        int len = 3;
        char[] chars = new char[len];
        tryBit(chars, len);
        tryBit2(chars, len);
        // tryBit3(chars, len);
        // for (Map.Entry<Integer, List<String>> entry : dupList.entrySet()) {
        // System.out.println(entry.getKey() + " : " + entry.getValue());
        // }
    }
}

