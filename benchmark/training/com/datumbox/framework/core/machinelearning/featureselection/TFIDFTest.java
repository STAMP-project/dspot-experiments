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
package com.datumbox.framework.core.machinelearning.featureselection;


import TFIDF.TrainingParameters;
import com.datumbox.framework.common.Configuration;
import com.datumbox.framework.core.Datasets;
import com.datumbox.framework.core.common.dataobjects.Dataframe;
import com.datumbox.framework.core.machinelearning.MLBuilder;
import com.datumbox.framework.tests.abstracts.AbstractTest;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test cases for TFIDF.
 *
 * @author Vasilis Vryniotis <bbriniotis@datumbox.com>
 */
public class TFIDFTest extends AbstractTest {
    /**
     * Test of shortMethodName method, of class TFIDF.
     */
    @Test
    public void testSelectFeatures() {
        logger.info("selectFeatures");
        Configuration configuration = getConfiguration();
        Dataframe[] data = Datasets.featureSelectorTFIDF(configuration);
        Dataframe trainingData = data[0];
        Dataframe validationData = data[1];
        String storageName = this.getClass().getSimpleName();
        TFIDF.TrainingParameters param = new TFIDF.TrainingParameters();
        param.setBinarized(false);
        param.setMaxFeatures(3);
        TFIDF instance = MLBuilder.create(param, configuration);
        instance.fit_transform(trainingData);
        instance.save(storageName);
        trainingData.close();
        instance.close();
        instance = MLBuilder.load(TFIDF.class, storageName, configuration);
        instance.transform(validationData);
        Set<Object> expResult = new HashSet<>(Arrays.asList("important1", "important2", "important3"));
        Set<Object> result = validationData.getXDataTypes().keySet();
        Assert.assertEquals(expResult, result);
        instance.delete();
        validationData.close();
    }
}

