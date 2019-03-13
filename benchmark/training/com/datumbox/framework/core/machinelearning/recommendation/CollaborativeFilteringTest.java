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
package com.datumbox.framework.core.machinelearning.recommendation;


import CollaborativeFiltering.TrainingParameters;
import CollaborativeFiltering.TrainingParameters.SimilarityMeasure.PEARSONS_CORRELATION;
import Constants.DOUBLE_ACCURACY_HIGH;
import com.datumbox.framework.common.Configuration;
import com.datumbox.framework.common.dataobjects.AssociativeArray;
import com.datumbox.framework.common.dataobjects.TypeInference;
import com.datumbox.framework.core.Datasets;
import com.datumbox.framework.core.common.dataobjects.Dataframe;
import com.datumbox.framework.core.machinelearning.MLBuilder;
import com.datumbox.framework.core.machinelearning.modelselection.metrics.RecommendationMetrics;
import com.datumbox.framework.tests.abstracts.AbstractTest;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test cases for CollaborativeFiltering.
 *
 * @author Vasilis Vryniotis <bbriniotis@datumbox.com>
 */
public class CollaborativeFilteringTest extends AbstractTest {
    /**
     * Test of predict method, of class CollaborativeFiltering.
     */
    @Test
    public void testPredict() {
        logger.info("testPredict");
        Configuration configuration = getConfiguration();
        Dataframe[] data = Datasets.recommenderSystemFood(configuration);
        Dataframe trainingData = data[0];
        Dataframe validationData = data[1];
        String storageName = this.getClass().getSimpleName();
        CollaborativeFiltering.TrainingParameters param = new CollaborativeFiltering.TrainingParameters();
        param.setSimilarityMethod(PEARSONS_CORRELATION);
        CollaborativeFiltering instance = MLBuilder.create(param, configuration);
        instance.fit(trainingData);
        instance.save(storageName);
        instance.close();
        instance = MLBuilder.load(CollaborativeFiltering.class, storageName, configuration);
        instance.predict(validationData);
        RecommendationMetrics vm = new RecommendationMetrics(validationData);
        System.out.println(vm);
        Map<Object, Double> expResult = new HashMap<>();
        expResult.put("pitta", 4.686394033077408);
        expResult.put("burger", 4.68408210680137);
        expResult.put("pizza", 4.6194430718558745);
        expResult.put("chocolate", 4.580630241051733);
        expResult.put("potato", 4.291658734729706);
        expResult.put("beer", 4.264285969929414);
        expResult.put("sparklewatter", 2.8034325749458997);
        expResult.put("salad", 1.496493323119103);
        expResult.put("risecookie", 1.372309723394662);
        expResult.put("tea", 1.3577402217087802);
        expResult.put("rise", 1.2243050068650592);
        AssociativeArray result = validationData.iterator().next().getYPredictedProbabilities();
        for (Map.Entry<Object, Object> entry : result.entrySet()) {
            Assert.assertEquals(expResult.get(entry.getKey()), TypeInference.toDouble(entry.getValue()), DOUBLE_ACCURACY_HIGH);
        }
        Assert.assertEquals(vm.getRMSE(), 0.7184568473420477, DOUBLE_ACCURACY_HIGH);
        instance.delete();
        trainingData.close();
        validationData.close();
    }
}

