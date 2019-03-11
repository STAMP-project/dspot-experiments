package org.ansj.splitWord.analysis;


import DicLibrary.DEFAULT;
import TermNatures.M_ALB.nature;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import junit.framework.Assert;
import org.ansj.CorpusTest;
import org.ansj.domain.Term;
import org.ansj.library.DicLibrary;
import org.junit.Test;
import org.nlpcn.commons.lang.tire.domain.Value;
import org.nlpcn.commons.lang.tire.library.Library;


public class DicAnalysisTest extends CorpusTest {
    @Test
    public void test() throws IOException {
        for (String string : lines) {
            System.out.println(DicAnalysis.parse(string));
        }
    }

    @Test
    public void test2() {
        for (String string : lines) {
            System.out.println(DicAnalysis.parse(string));
        }
        System.out.println(nature);
        System.out.println(ToAnalysis.parse("ansj-sun@163.com??????"));
    }

    @Test
    public void test1() {
        DicLibrary.insert(DEFAULT, "???", "ad", 1000);
        DicLibrary.insert(DEFAULT, "???", "ad", 1000);
        DicLibrary.insert(DEFAULT, "???", "ad", 1000);
        DicLibrary.insert(DEFAULT, "??", "ad", 1000);
        DicLibrary.insert(DEFAULT, "???", "ab", 1000);
        DicLibrary.insert(DEFAULT, "???", "ab", 2000);
        DicLibrary.insert(DEFAULT, "???", "ab", 1000);
        System.out.println(DicAnalysis.parse("???????????????162"));
        System.out.println(DicAnalysis.parse("???????????????????????????????"));
        System.out.println(DicAnalysis.parse("??????????"));
        String newWord = "?????";
        String nature = "aaaaa";
        String str = "????2012???????????????";
        // ????
        DicLibrary.insert(DEFAULT, newWord, nature, 1000);
        DicLibrary.insert(DEFAULT, "????", nature, 1000);
        List<Term> parse = DicAnalysis.parse(str).getTerms();
        HashMap<String, Term> hs = new HashMap<String, Term>();
        for (Term term : parse) {
            hs.put(term.getName(), term);
        }
        Assert.assertTrue(hs.containsKey(newWord));
        Assert.assertEquals(hs.get(newWord).natrue().natureStr, nature);
        Library.insertWord(DicLibrary.get(), new Value("???", "UserDefined", "1000"));
        Assert.assertEquals(DicAnalysis.parse("???????").get(0).getName(), "???");
        // ???
        DicLibrary.delete(DEFAULT, newWord);
        parse = DicAnalysis.parse(str).getTerms();
        hs = new HashMap<String, Term>();
        for (Term term : parse) {
            hs.put(term.getName(), term);
        }
        Assert.assertTrue((!(hs.containsKey(newWord))));
    }
}

