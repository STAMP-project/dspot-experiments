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
package com.datumbox.framework.core.machinelearning.clustering;


import Constants.DOUBLE_ACCURACY_HIGH;
import Kmeans.TrainingParameters.Distance.EUCLIDIAN;
import Kmeans.TrainingParameters.Initialization.FORGY;
import MinMaxScaler.TrainingParameters;
import com.datumbox.framework.common.Configuration;
import com.datumbox.framework.core.Datasets;
import com.datumbox.framework.core.common.dataobjects.Dataframe;
import com.datumbox.framework.core.machinelearning.MLBuilder;
import com.datumbox.framework.core.machinelearning.modelselection.metrics.ClusteringMetrics;
import com.datumbox.framework.core.machinelearning.modelselection.splitters.KFoldSplitter;
import com.datumbox.framework.core.machinelearning.preprocessing.CornerConstraintsEncoder;
import com.datumbox.framework.core.machinelearning.preprocessing.MinMaxScaler;
import com.datumbox.framework.tests.abstracts.AbstractTest;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test cases for Kmeans.
 *
 * @author Vasilis Vryniotis <bbriniotis@datumbox.com>
 */
public class KmeansTest extends AbstractTest {
    /**
     * Test of predict method, of class Kmeans.
     */
    @Test
    public void testPredict() {
        logger.info("testPredict");
        Configuration configuration = getConfiguration();
        Dataframe[] data = Datasets.heartDiseaseClusters(configuration);
        Dataframe trainingData = data[0];
        Dataframe validationData = data[1];
        String storageName = this.getClass().getSimpleName();
        MinMaxScaler.TrainingParameters nsParams = new MinMaxScaler.TrainingParameters();
        MinMaxScaler numericalScaler = MLBuilder.create(nsParams, configuration);
        numericalScaler.fit_transform(trainingData);
        numericalScaler.save(storageName);
        CornerConstraintsEncoder.TrainingParameters ceParams = new CornerConstraintsEncoder.TrainingParameters();
        CornerConstraintsEncoder categoricalEncoder = MLBuilder.create(ceParams, configuration);
        categoricalEncoder.fit_transform(trainingData);
        categoricalEncoder.save(storageName);
        Kmeans.TrainingParameters param = new Kmeans.TrainingParameters();
        param.setK(2);
        param.setMaxIterations(200);
        param.setInitializationMethod(FORGY);
        param.setDistanceMethod(EUCLIDIAN);
        param.setWeighted(false);
        param.setCategoricalGamaMultiplier(1.0);
        param.setSubsetFurthestFirstcValue(2.0);
        Kmeans instance = MLBuilder.create(param, configuration);
        instance.fit(trainingData);
        instance.save(storageName);
        trainingData.close();
        instance.close();
        numericalScaler.close();
        categoricalEncoder.close();
        numericalScaler = MLBuilder.load(MinMaxScaler.class, storageName, configuration);
        categoricalEncoder = MLBuilder.load(CornerConstraintsEncoder.class, storageName, configuration);
        instance = MLBuilder.load(Kmeans.class, storageName, configuration);
        numericalScaler.transform(validationData);
        categoricalEncoder.transform(validationData);
        instance.predict(validationData);
        ClusteringMetrics vm = new ClusteringMetrics(validationData);
        double expResult = 1.0;
        double result = vm.getPurity();
        Assert.assertEquals(expResult, result, DOUBLE_ACCURACY_HIGH);
        numericalScaler.delete();
        categoricalEncoder.delete();
        instance.delete();
        validationData.close();
    }

    /**
     * Test of validate method, of class Kmeans.
     */
    @Test
    public void testKFoldCrossValidation() {
        logger.info("testKFoldCrossValidation");
        Configuration configuration = getConfiguration();
        int k = 5;
        Dataframe[] data = Datasets.heartDiseaseClusters(configuration);
        Dataframe trainingData = data[0];
        data[1].close();
        MinMaxScaler.TrainingParameters nsParams = new MinMaxScaler.TrainingParameters();
        MinMaxScaler numericalScaler = MLBuilder.create(nsParams, configuration);
        numericalScaler.fit_transform(trainingData);
        CornerConstraintsEncoder.TrainingParameters ceParams = new CornerConstraintsEncoder.TrainingParameters();
        CornerConstraintsEncoder categoricalEncoder = MLBuilder.create(ceParams, configuration);
        categoricalEncoder.fit_transform(trainingData);
        Kmeans.TrainingParameters param = new Kmeans.TrainingParameters();
        param.setK(2);
        param.setMaxIterations(200);
        param.setInitializationMethod(FORGY);
        param.setDistanceMethod(EUCLIDIAN);
        param.setWeighted(false);
        param.setCategoricalGamaMultiplier(1.0);
        param.setSubsetFurthestFirstcValue(2.0);
        ClusteringMetrics vm = new com.datumbox.framework.core.machinelearning.modelselection.Validator(ClusteringMetrics.class, configuration).validate(new KFoldSplitter(k).split(trainingData), param);
        double expResult = 0.7888888888888889;
        double result = vm.getPurity();
        Assert.assertEquals(expResult, result, DOUBLE_ACCURACY_HIGH);
        numericalScaler.close();
        categoricalEncoder.close();
        trainingData.close();
    }
}

