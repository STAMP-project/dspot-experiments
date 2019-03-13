package org.ansj.test;


import org.ansj.domain.Result;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.ansj.util.MyStaticValue;
import org.junit.Test;


public class RealNameTest {
    @Test
    public void test() {
        MyStaticValue.isRealName = true;
        Result parse = ToAnalysis.parse("????????????????????? ????????????123456 ???????");
        System.out.println(parse.get(1).getRealName());
        System.out.println(parse);
    }
}

