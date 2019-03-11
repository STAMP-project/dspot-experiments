package aima.test.core.unit.probability.bayes.model;


import aima.core.probability.bayes.BayesInference;
import aima.core.probability.example.BayesNetExampleFactory;
import aima.test.core.unit.probability.CommonFiniteProbabilityModelTests;
import org.junit.Test;


public class FiniteBayesModelTest extends CommonFiniteProbabilityModelTests {
    // 
    // ProbabilityModel Tests
    @Test
    public void test_RollingPairFairDiceModel() {
        for (BayesInference bi : getBayesInferenceImplementations()) {
            test_RollingPairFairDiceModel(new aima.core.probability.bayes.model.FiniteBayesModel(BayesNetExampleFactory.construct2FairDiceNetwor(), bi));
        }
    }

    @Test
    public void test_ToothacheCavityCatchModel() {
        for (BayesInference bi : getBayesInferenceImplementations()) {
            test_ToothacheCavityCatchModel(new aima.core.probability.bayes.model.FiniteBayesModel(BayesNetExampleFactory.constructToothacheCavityCatchNetwork(), bi));
        }
    }

    @Test
    public void test_ToothacheCavityCatchWeatherModel() {
        for (BayesInference bi : getBayesInferenceImplementations()) {
            test_ToothacheCavityCatchWeatherModel(new aima.core.probability.bayes.model.FiniteBayesModel(BayesNetExampleFactory.constructToothacheCavityCatchWeatherNetwork(), bi));
        }
    }

    @Test
    public void test_MeningitisStiffNeckModel() {
        for (BayesInference bi : getBayesInferenceImplementations()) {
            test_MeningitisStiffNeckModel(new aima.core.probability.bayes.model.FiniteBayesModel(BayesNetExampleFactory.constructMeningitisStiffNeckNetwork(), bi));
        }
    }

    @Test
    public void test_BurglaryAlarmModel() {
        for (BayesInference bi : getBayesInferenceImplementations()) {
            test_BurglaryAlarmModel(new aima.core.probability.bayes.model.FiniteBayesModel(BayesNetExampleFactory.constructBurglaryAlarmNetwork(), bi));
        }
    }

    // 
    // FiniteProbabilityModel Tests
    @Test
    public void test_RollingPairFairDiceModel_Distributions() {
        for (BayesInference bi : getBayesInferenceImplementations()) {
            test_RollingPairFairDiceModel_Distributions(new aima.core.probability.bayes.model.FiniteBayesModel(BayesNetExampleFactory.construct2FairDiceNetwor(), bi));
        }
    }

    @Test
    public void test_ToothacheCavityCatchModel_Distributions() {
        for (BayesInference bi : getBayesInferenceImplementations()) {
            test_ToothacheCavityCatchModel_Distributions(new aima.core.probability.bayes.model.FiniteBayesModel(BayesNetExampleFactory.constructToothacheCavityCatchNetwork(), bi));
        }
    }

    @Test
    public void test_ToothacheCavityCatchWeatherModel_Distributions() {
        for (BayesInference bi : getBayesInferenceImplementations()) {
            test_ToothacheCavityCatchWeatherModel_Distributions(new aima.core.probability.bayes.model.FiniteBayesModel(BayesNetExampleFactory.constructToothacheCavityCatchWeatherNetwork(), bi));
        }
    }

    @Test
    public void test_MeningitisStiffNeckModel_Distributions() {
        for (BayesInference bi : getBayesInferenceImplementations()) {
            test_MeningitisStiffNeckModel_Distributions(new aima.core.probability.bayes.model.FiniteBayesModel(BayesNetExampleFactory.constructMeningitisStiffNeckNetwork(), bi));
        }
    }

    @Test
    public void test_BurglaryAlarmModel_Distributions() {
        for (BayesInference bi : getBayesInferenceImplementations()) {
            test_BurglaryAlarmModel_Distributions(new aima.core.probability.bayes.model.FiniteBayesModel(BayesNetExampleFactory.constructBurglaryAlarmNetwork(), bi));
        }
    }
}

