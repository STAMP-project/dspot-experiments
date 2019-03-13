package com.hankcs.hanlp.dictionary.other;


import CharType.CT_NUM;
import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.utility.TextUtility;
import junit.framework.TestCase;

import static CharType.CT_DELIMITER;


public class CharTypeTest extends TestCase {
    public void testNumber() throws Exception {
        // for (int i = 0; i <= Character.MAX_VALUE; ++i)
        // {
        // if (CharType.get((char) i) == CharType.CT_NUM)
        // System.out.println((char) i);
        // }
        TestCase.assertEquals(CT_NUM, CharType.get('1'));
    }

    public void testWhiteSpace() throws Exception {
        // CharType.type[' '] = CharType.CT_OTHER;
        String text = "1 + 2 = 3; a+b= a + b";
        TestCase.assertEquals("[1/m,  /w, +/w,  /w, 2/m,  /w, =/w,  /w, 3/m, ;/w,  /w, a/nx, +/w, b/nx, =/w,  /w, a/nx,  /w, +/w,  /w, b/nx]", HanLP.segment(text).toString());
    }

    public void testTab() throws Exception {
        TestCase.assertTrue(((TextUtility.charType('\t')) == (CT_DELIMITER)));
        TestCase.assertTrue(((TextUtility.charType('\r')) == (CT_DELIMITER)));
        TestCase.assertTrue(((TextUtility.charType('\u0000')) == (CT_DELIMITER)));
        // System.out.println(HanLP.segment("\t"));
    }
}

