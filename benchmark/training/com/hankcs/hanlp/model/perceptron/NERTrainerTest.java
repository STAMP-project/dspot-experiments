package com.hankcs.hanlp.model.perceptron;


import java.util.Arrays;
import junit.framework.TestCase;


public class NERTrainerTest extends TestCase {
    public void testTrain() throws Exception {
        PerceptronTrainer trainer = new NERTrainer();
        trainer.train("data/test/pku98/199801.txt", Config.NER_MODEL_FILE);
    }

    public void testTag() throws Exception {
        PerceptronNERecognizer recognizer = new PerceptronNERecognizer(Config.NER_MODEL_FILE);
        System.out.println(Arrays.toString(recognizer.recognize("??? ??? ?? ??? ?? ???? ??".split(" "), "ns n n nr p ns n".split(" "))));
    }
}

