package com.baeldung.hazelcast.jet;


import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class WordCounterUnitTest {
    @Test
    public void whenGivenSentencesAndWord_ThenReturnCountOfWord() {
        List<String> sentences = new ArrayList<>();
        sentences.add("The first second was alright, but the second second was tough.");
        WordCounter wordCounter = new WordCounter();
        long countSecond = wordCounter.countWord(sentences, "second");
        Assert.assertTrue((countSecond == 3));
    }
}

