package com.fishercoder;


import com.fishercoder.common.utils.CommonUtils;
import com.fishercoder.solutions._609;
import java.util.List;
import org.junit.Test;


public class _609Test {
    private static _609 test;

    private static String[] paths;

    private static List<List<String>> actual;

    @Test
    public void test1() {
        _609Test.paths = new String[]{ "root/a 1.txt(abcd) 2.txt(efgh)", "root/c 3.txt(abcd)", "root/c/d 4.txt(efgh)", "root 4.txt(efgh)" };
        _609Test.actual = _609Test.test.findDuplicate(_609Test.paths);
        CommonUtils.printListList(_609Test.actual);
    }
}

