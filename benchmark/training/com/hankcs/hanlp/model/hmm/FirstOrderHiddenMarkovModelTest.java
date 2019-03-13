package com.hankcs.hanlp.model.hmm;


import junit.framework.TestCase;


public class FirstOrderHiddenMarkovModelTest extends TestCase {
    /**
     * ???
     */
    enum Status {

        Healthy,
        Fever;}

    /**
     * ???
     */
    enum Feel {

        normal,
        cold,
        dizzy;}

    /**
     * ????????
     */
    static float[] start_probability = new float[]{ 0.6F, 0.4F };

    /**
     * ????????
     */
    static float[][] transition_probability = new float[][]{ new float[]{ 0.7F, 0.3F }, new float[]{ 0.4F, 0.6F } };

    /**
     * ??????
     */
    static float[][] emission_probability = new float[][]{ new float[]{ 0.5F, 0.4F, 0.1F }, new float[]{ 0.1F, 0.3F, 0.6F } };

    /**
     * ?????????
     */
    static int[] observations = new int[]{ FirstOrderHiddenMarkovModelTest.Feel.normal.ordinal(), FirstOrderHiddenMarkovModelTest.Feel.cold.ordinal(), FirstOrderHiddenMarkovModelTest.Feel.dizzy.ordinal() };

    public void testGenerate() throws Exception {
        FirstOrderHiddenMarkovModel givenModel = new FirstOrderHiddenMarkovModel(FirstOrderHiddenMarkovModelTest.start_probability, FirstOrderHiddenMarkovModelTest.transition_probability, FirstOrderHiddenMarkovModelTest.emission_probability);
        for (int[][] sample : givenModel.generate(3, 5, 2)) {
            for (int t = 0; t < (sample[0].length); t++)
                System.out.printf("%s/%s ", FirstOrderHiddenMarkovModelTest.Feel.values()[sample[0][t]], FirstOrderHiddenMarkovModelTest.Status.values()[sample[1][t]]);

            System.out.println();
        }
    }

    public void testTrain() throws Exception {
        FirstOrderHiddenMarkovModel givenModel = new FirstOrderHiddenMarkovModel(FirstOrderHiddenMarkovModelTest.start_probability, FirstOrderHiddenMarkovModelTest.transition_probability, FirstOrderHiddenMarkovModelTest.emission_probability);
        FirstOrderHiddenMarkovModel trainedModel = new FirstOrderHiddenMarkovModel();
        trainedModel.train(givenModel.generate(3, 10, 100000));
        TestCase.assertTrue(trainedModel.similar(givenModel));
    }

    public void testPredict() throws Exception {
        FirstOrderHiddenMarkovModel model = new FirstOrderHiddenMarkovModel(FirstOrderHiddenMarkovModelTest.start_probability, FirstOrderHiddenMarkovModelTest.transition_probability, FirstOrderHiddenMarkovModelTest.emission_probability);
        evaluateModel(model);
    }
}

