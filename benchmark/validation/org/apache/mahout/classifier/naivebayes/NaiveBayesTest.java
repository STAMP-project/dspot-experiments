/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.mahout.classifier.naivebayes;


import Vector.Element;
import java.io.File;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.mahout.classifier.AbstractVectorClassifier;
import org.apache.mahout.classifier.naivebayes.training.TrainNaiveBayesJob;
import org.apache.mahout.common.MahoutTestCase;
import org.apache.mahout.math.Vector;
import org.apache.mahout.math.hadoop.MathHelper;
import org.junit.Test;


public class NaiveBayesTest extends MahoutTestCase {
    private Configuration conf;

    private File inputFile;

    private File outputDir;

    private File tempDir;

    static final Text LABEL_STOLEN = new Text("/stolen/");

    static final Text LABEL_NOT_STOLEN = new Text("/not_stolen/");

    static final Element COLOR_RED = MathHelper.elem(0, 1);

    static final Element COLOR_YELLOW = MathHelper.elem(1, 1);

    static final Element TYPE_SPORTS = MathHelper.elem(2, 1);

    static final Element TYPE_SUV = MathHelper.elem(3, 1);

    static final Element ORIGIN_DOMESTIC = MathHelper.elem(4, 1);

    static final Element ORIGIN_IMPORTED = MathHelper.elem(5, 1);

    @Test
    public void toyData() throws Exception {
        TrainNaiveBayesJob trainNaiveBayes = new TrainNaiveBayesJob();
        trainNaiveBayes.setConf(conf);
        trainNaiveBayes.run(new String[]{ "--input", inputFile.getAbsolutePath(), "--output", outputDir.getAbsolutePath(), "--tempDir", tempDir.getAbsolutePath() });
        NaiveBayesModel naiveBayesModel = NaiveBayesModel.materialize(new Path(outputDir.getAbsolutePath()), conf);
        AbstractVectorClassifier classifier = new StandardNaiveBayesClassifier(naiveBayesModel);
        assertEquals(2, classifier.numCategories());
        Vector prediction = classifier.classifyFull(NaiveBayesTest.trainingInstance(NaiveBayesTest.COLOR_RED, NaiveBayesTest.TYPE_SUV, NaiveBayesTest.ORIGIN_DOMESTIC).get());
        // should be classified as not stolen
        assertTrue(((prediction.get(0)) < (prediction.get(1))));
    }

    @Test
    public void toyDataComplementary() throws Exception {
        TrainNaiveBayesJob trainNaiveBayes = new TrainNaiveBayesJob();
        trainNaiveBayes.setConf(conf);
        trainNaiveBayes.run(new String[]{ "--input", inputFile.getAbsolutePath(), "--output", outputDir.getAbsolutePath(), "--trainComplementary", "--tempDir", tempDir.getAbsolutePath() });
        NaiveBayesModel naiveBayesModel = NaiveBayesModel.materialize(new Path(outputDir.getAbsolutePath()), conf);
        AbstractVectorClassifier classifier = new ComplementaryNaiveBayesClassifier(naiveBayesModel);
        assertEquals(2, classifier.numCategories());
        Vector prediction = classifier.classifyFull(NaiveBayesTest.trainingInstance(NaiveBayesTest.COLOR_RED, NaiveBayesTest.TYPE_SUV, NaiveBayesTest.ORIGIN_DOMESTIC).get());
        // should be classified as not stolen
        assertTrue(((prediction.get(0)) < (prediction.get(1))));
    }
}

