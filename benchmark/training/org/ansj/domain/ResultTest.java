package org.ansj.domain;


import org.ansj.splitWord.analysis.ToAnalysis;
import org.junit.Test;


/**
 * ???????
 *
 * @author ansj
 */
public class ResultTest {
    @Test
    public void toStringTest() {
        System.out.println(ToAnalysis.parse("?????").toString());
    }

    @Test
    public void toStringSplitTest() {
        System.out.println(ToAnalysis.parse("?????").toString("\t"));
    }

    @Test
    public void toStringWithOutNatureTest() {
        System.out.println(ToAnalysis.parse("?????").toStringWithOutNature());
    }

    @Test
    public void toStringWithOutNatureSplitTest() {
        System.out.println(ToAnalysis.parse("?????").toStringWithOutNature("\t"));
    }
}

