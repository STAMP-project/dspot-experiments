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
package com.datumbox.framework.core.machinelearning.topicmodeling;


import Constants.DOUBLE_ACCURACY_HIGH;
import Dataframe.Builder;
import LatentDirichletAllocation.TrainingParameters;
import com.datumbox.framework.common.Configuration;
import com.datumbox.framework.core.Datasets;
import com.datumbox.framework.core.common.dataobjects.Dataframe;
import com.datumbox.framework.core.common.dataobjects.Record;
import com.datumbox.framework.core.common.text.extractors.UniqueWordSequenceExtractor;
import com.datumbox.framework.core.machinelearning.MLBuilder;
import com.datumbox.framework.core.machinelearning.classification.SoftMaxRegression;
import com.datumbox.framework.core.machinelearning.modelselection.metrics.ClassificationMetrics;
import com.datumbox.framework.core.machinelearning.modelselection.splitters.KFoldSplitter;
import com.datumbox.framework.tests.abstracts.AbstractTest;
import java.net.URI;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test cases for LatentDirichletAllocation.
 *
 * @author Vasilis Vryniotis <bbriniotis@datumbox.com>
 */
public class LatentDirichletAllocationTest extends AbstractTest {
    /**
     * Test of predict method, of class LatentDirichletAllocation.
     */
    @Test
    public void testPredict() {
        logger.info("testPredict");
        Configuration configuration = getConfiguration();
        String storageName = this.getClass().getSimpleName();
        Map<Object, URI> dataset = Datasets.sentimentAnalysis();
        UniqueWordSequenceExtractor wsExtractor = new UniqueWordSequenceExtractor(new UniqueWordSequenceExtractor.Parameters());
        Dataframe trainingData = Builder.parseTextFiles(dataset, wsExtractor, configuration);
        LatentDirichletAllocation.TrainingParameters trainingParameters = new LatentDirichletAllocation.TrainingParameters();
        trainingParameters.setMaxIterations(15);
        trainingParameters.setAlpha(0.01);
        trainingParameters.setBeta(0.01);
        trainingParameters.setK(25);
        LatentDirichletAllocation lda = MLBuilder.create(trainingParameters, configuration);
        lda.fit(trainingData);
        lda.save(storageName);
        lda.close();
        lda = MLBuilder.load(LatentDirichletAllocation.class, storageName, configuration);
        lda.predict(trainingData);
        Dataframe reducedTrainingData = new Dataframe(configuration);
        for (Record r : trainingData) {
            // take the topic assignments and convert them into a new Record
            reducedTrainingData.add(new Record(r.getYPredictedProbabilities(), r.getY()));
        }
        SoftMaxRegression.TrainingParameters tp = new SoftMaxRegression.TrainingParameters();
        tp.setLearningRate(1.0);
        tp.setTotalIterations(50);
        ClassificationMetrics vm = new com.datumbox.framework.core.machinelearning.modelselection.Validator(ClassificationMetrics.class, configuration).validate(new KFoldSplitter(1).split(reducedTrainingData), tp);
        double expResult = 0.6843125117743629;
        double result = vm.getMacroF1();
        Assert.assertEquals(expResult, result, DOUBLE_ACCURACY_HIGH);
        lda.delete();
        reducedTrainingData.close();
        trainingData.close();
    }
}

