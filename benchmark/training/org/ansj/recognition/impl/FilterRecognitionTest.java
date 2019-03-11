package org.ansj.recognition.impl;


import StopLibrary.DEFAULT;
import org.ansj.domain.Result;
import org.ansj.domain.Term;
import org.ansj.library.StopLibrary;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.junit.Assert;
import org.junit.Test;


/**
 * ?????
 *
 * @author Ansj
 */
public class FilterRecognitionTest {
    @Test
    public void test() {
        String str = "???????!";
        Result parse = ToAnalysis.parse(str);
        System.out.println(parse);
        StopRecognition fitler = new StopRecognition();
        fitler.insertStopNatures("uj");
        fitler.insertStopNatures("ul");
        fitler.insertStopNatures("null");
        fitler.insertStopWords("?");
        fitler.insertStopRegexes("?.*?");
        Result modifResult = parse.recognition(fitler);
        for (Term term : modifResult) {
            Assert.assertNotSame(term.getNatureStr(), "uj");
            Assert.assertNotSame(term.getNatureStr(), "ul");
            Assert.assertNotSame(term.getNatureStr(), "null");
            Assert.assertNotSame(term.getName(), "?");
            Assert.assertNotSame(term.getName(), "???");
        }
        System.out.println(modifResult);
    }

    @Test
    public void stopTest() {
        StopLibrary.insertStopWords(DEFAULT, "?", "??", "??", "?", "?");
        Result terms = ToAnalysis.parse("???????????");
        // ?????
        System.out.println(terms.recognition(StopLibrary.get()));
    }
}

