/**
 * Copyright (C) 2013-2018 Vasilis Vryniotis <bbriniotis@datumbox.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.datumbox.framework.applications.datamodeling;


import Constants.DOUBLE_ACCURACY_HIGH;
import Modeler.TrainingParameters;
import com.datumbox.framework.common.Configuration;
import com.datumbox.framework.core.Datasets;
import com.datumbox.framework.core.common.dataobjects.Dataframe;
import com.datumbox.framework.core.common.dataobjects.Record;
import com.datumbox.framework.core.machinelearning.MLBuilder;
import com.datumbox.framework.core.machinelearning.classification.SoftMaxRegression;
import com.datumbox.framework.core.machinelearning.featureselection.ChisquareSelect;
import com.datumbox.framework.core.machinelearning.featureselection.PCA;
import com.datumbox.framework.core.machinelearning.modelselection.metrics.ClassificationMetrics;
import com.datumbox.framework.core.machinelearning.preprocessing.MinMaxScaler;
import com.datumbox.framework.core.machinelearning.preprocessing.OneHotEncoder;
import com.datumbox.framework.tests.abstracts.AbstractTest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test cases for Modeler.
 *
 * @author Vasilis Vryniotis <bbriniotis@datumbox.com>
 */
public class ModelerTest extends AbstractTest {
    /**
     * Test of train and predict method, of class Modeler.
     */
    @Test
    public void testTrainAndValidate() {
        logger.info("testTrainAndValidate");
        Configuration configuration = getConfiguration();
        Dataframe[] data = Datasets.heartDiseaseClusters(configuration);
        Dataframe trainingData = data[0];
        Dataframe validationData = data[0].copy();
        Dataframe testData = data[1];
        String storageName = this.getClass().getSimpleName();
        Modeler.TrainingParameters trainingParameters = new Modeler.TrainingParameters();
        // numerical scaling configuration
        MinMaxScaler.TrainingParameters nsParams = new MinMaxScaler.TrainingParameters();
        trainingParameters.setNumericalScalerTrainingParameters(nsParams);
        // categorical encoding configuration
        OneHotEncoder.TrainingParameters ceParams = new OneHotEncoder.TrainingParameters();
        trainingParameters.setCategoricalEncoderTrainingParameters(ceParams);
        // feature selection configuration
        PCA.TrainingParameters pcaParams = new PCA.TrainingParameters();
        pcaParams.setVariancePercentageThreshold(0.99999995);
        trainingParameters.setFeatureSelectorTrainingParametersList(Arrays.asList(new ChisquareSelect.TrainingParameters(), pcaParams));
        // model Configuration
        SoftMaxRegression.TrainingParameters modelTrainingParameters = new SoftMaxRegression.TrainingParameters();
        modelTrainingParameters.setL1(1.0E-4);
        modelTrainingParameters.setL2(1.0E-4);
        modelTrainingParameters.setTotalIterations(100);
        trainingParameters.setModelerTrainingParameters(modelTrainingParameters);
        Modeler instance = MLBuilder.create(trainingParameters, configuration);
        instance.fit(trainingData);
        instance.save(storageName);
        instance.close();
        trainingData.close();
        instance = MLBuilder.load(Modeler.class, storageName, configuration);
        instance.predict(validationData);
        ClassificationMetrics vm = new ClassificationMetrics(validationData);
        double expResult2 = 0.8428731762065095;
        Assert.assertEquals(expResult2, vm.getMacroF1(), DOUBLE_ACCURACY_HIGH);
        validationData.close();
        instance.close();
        instance = MLBuilder.load(Modeler.class, storageName, configuration);
        instance.predict(testData);
        Map<Integer, Object> expResult = new HashMap<>();
        Map<Integer, Object> result = new HashMap<>();
        for (Map.Entry<Integer, Record> e : testData.entries()) {
            Integer rId = e.getKey();
            Record r = e.getValue();
            expResult.put(rId, r.getY());
            result.put(rId, r.getYPredicted());
        }
        Assert.assertEquals(expResult, result);
        instance.delete();
        testData.close();
    }
}

