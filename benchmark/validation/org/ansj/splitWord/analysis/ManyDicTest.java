package org.ansj.splitWord.analysis;


import org.ansj.library.DicLibrary;
import org.junit.Test;
import org.nlpcn.commons.lang.tire.domain.Forest;


public class ManyDicTest {
    @Test
    public void test() {
        Forest f1 = new Forest();
        f1.addBranch("ansj??", new String[]{ "f1", "1000" });
        Forest f2 = new Forest();
        f2.addBranch("ansj???", new String[]{ "f2", "1000" });
        Forest f3 = new Forest();
        f3.addBranch("ansj???", new String[]{ "f3", "1000" });
        String content = "ansj??ansj???ansj???";
        System.out.println(ToAnalysis.parse(content, DicLibrary.get(), f1, f2));
        System.out.println(ToAnalysis.parse(content, f3, f2));
    }
}

