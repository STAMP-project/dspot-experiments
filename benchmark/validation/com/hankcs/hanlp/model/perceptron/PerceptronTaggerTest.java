package com.hankcs.hanlp.model.perceptron;


import java.util.ArrayList;
import junit.framework.TestCase;


public class PerceptronTaggerTest extends TestCase {
    public void testEmptyInput() throws Exception {
        PerceptronPOSTagger tagger = new PerceptronPOSTagger();
        tagger.tag(new ArrayList<String>());
    }
}

