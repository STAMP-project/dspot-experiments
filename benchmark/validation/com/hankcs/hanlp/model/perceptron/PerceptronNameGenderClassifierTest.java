package com.hankcs.hanlp.model.perceptron;


import junit.framework.TestCase;


public class PerceptronNameGenderClassifierTest extends TestCase {
    public static String TRAINING_SET = "data/test/cnname/train.csv";

    public static String TESTING_SET = "data/test/cnname/test.csv";

    public static String MODEL = "data/test/cnname.bin";

    public void testTrain() throws Exception {
        PerceptronNameGenderClassifier classifier = new PerceptronNameGenderClassifier();
        System.out.println(classifier.train(PerceptronNameGenderClassifierTest.TRAINING_SET, 10, false));
        classifier.model.save(PerceptronNameGenderClassifierTest.MODEL, classifier.model.featureMap.entrySet(), 0, true);
        PerceptronNameGenderClassifierTest.predictNames(classifier);
    }

    public void testEvaluate() throws Exception {
        PerceptronNameGenderClassifier classifier = new PerceptronNameGenderClassifier(PerceptronNameGenderClassifierTest.MODEL);
        System.out.println(classifier.evaluate(PerceptronNameGenderClassifierTest.TESTING_SET));
    }

    public void testPrediction() throws Exception {
        PerceptronNameGenderClassifier classifier = new PerceptronNameGenderClassifier(PerceptronNameGenderClassifierTest.MODEL);
        PerceptronNameGenderClassifierTest.predictNames(classifier);
    }
}

