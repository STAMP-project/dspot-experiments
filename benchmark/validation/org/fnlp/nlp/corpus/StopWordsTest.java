package org.fnlp.nlp.corpus;


import org.junit.Assert;
import org.junit.Test;


public class StopWordsTest {
    @Test
    public void testIsStopWordStringIntInt() {
        StopWords sw = new StopWords();
        sw.read("../models/stopwords/StopWords.txt");
        Assert.assertTrue((!(sw.isStopWord("???", 2, 4))));
        Assert.assertTrue(sw.isStopWord("?0", 2, 4));
        Assert.assertTrue(sw.isStopWord("?#", 2, 4));
        Assert.assertTrue(sw.isStopWord(" ", 2, 4));
    }

    @Test
    public void testIsStopWordString() {
        StopWords sw = new StopWords();
        sw.read("../models/stopwords/StopWords.txt");
        Assert.assertTrue((!(sw.isStopWord("???"))));
    }
}

