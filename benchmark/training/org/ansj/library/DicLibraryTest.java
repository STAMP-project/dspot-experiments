package org.ansj.library;


import DicLibrary.DEFAULT;
import org.ansj.domain.Result;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.DicAnalysis;
import org.junit.Assert;
import org.junit.Test;
import org.nlpcn.commons.lang.tire.domain.Forest;


public class DicLibraryTest {
    /**
     * ?????
     *
     * @param keyword
     * 		????????
     * @param nature
     * 		??????
     * @param freq
     * 		??????
     */
    @Test
    public void insertTest() {
        DicLibrary.insert(DEFAULT, "????", "????", 1000);
        Result parse = DicAnalysis.parse("????????????????");
        System.out.println(parse);
        boolean flag = false;
        for (Term term : parse) {
            flag = flag || ("????".equals(term.getName()));
        }
        Assert.assertTrue(flag);
    }

    /**
     * ?????
     *
     * @param keyword
     * 		
     */
    @Test
    public void insertTest2() {
        DicLibrary.insert(DEFAULT, "????");
        Result parse = DicAnalysis.parse("????????????????");
        System.out.println(parse);
        boolean flag = false;
        for (Term term : parse) {
            flag = flag || ("????".equals(term.getName()));
        }
        Assert.assertTrue(flag);
    }

    /**
     * ?????
     */
    @Test
    public void delete() {
        insertTest();
        DicLibrary.delete(DEFAULT, "????");
        Result parse = DicAnalysis.parse("????????????????");
        System.out.println(parse);
        boolean flag = false;
        for (Term term : parse) {
            flag = flag || ("????".equals(term.getName()));
        }
        Assert.assertFalse(flag);
    }

    /**
     * ???key
     */
    @Test
    public void keyTest() {
        String key = "dic_mykey";
        DicLibrary.put(key, key, new Forest());
        DicLibrary.insert(key, "????", "????", 1000);
        Result parse = DicAnalysis.parse("????????????????", DicLibrary.gets(key));
        System.out.println(parse);
        boolean flag = false;
        for (Term term : parse) {
            flag = flag || ("????".equals(term.getName()));
        }
        Assert.assertTrue(flag);
    }
}

