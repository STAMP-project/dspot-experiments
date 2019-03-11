package com.hankcs.hanlp.model.perceptron;


import java.util.Arrays;
import junit.framework.TestCase;


public class POSTrainerTest extends TestCase {
    public void testTrain() throws Exception {
        PerceptronTrainer trainer = new POSTrainer();
        trainer.train("data/test/pku98/199801.txt", Config.POS_MODEL_FILE);
    }

    public void testLoad() throws Exception {
        PerceptronPOSTagger tagger = new PerceptronPOSTagger(Config.POS_MODEL_FILE);
        System.out.println(Arrays.toString(tagger.tag("?? ???? ??? ? ???? ?? ??".split(" "))));
    }
}

