/**
 * *****************************************************************************
 * Copyright (c) 2015-2018 Skymind, Inc.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 * ****************************************************************************
 */
package org.deeplearning4j.spark.ml.impl;


import java.io.File;
import java.io.IOException;
import java.util.UUID;
import org.apache.commons.io.FileUtils;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.ml.Pipeline;
import org.apache.spark.ml.PipelineStage;
import org.apache.spark.sql.SQLContext;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.spark.impl.paramavg.ParameterAveragingTrainingMaster;
import org.deeplearning4j.spark.ml.utils.DatasetFacade;
import org.deeplearning4j.spark.ml.utils.ParamSerializer;
import org.junit.Assert;
import org.junit.Test;


public class AutoEncoderNetworkTest {
    private SparkConf sparkConf = new SparkConf().setAppName("testing").setMaster("local[4]");

    private JavaSparkContext sparkContext = new JavaSparkContext(sparkConf);

    private SQLContext sqlContext = new SQLContext(sparkContext);

    @Test
    public void testNetwork() {
        DatasetFacade df = DatasetFacade.dataRows(sqlContext.read().json("src/test/resources/autoencoders"));
        Pipeline p = new Pipeline().setStages(new PipelineStage[]{ AutoEncoderNetworkTest.getAssembler(new String[]{ "a", "b", "c", "d", "e", "f", "g", "h", "i", "j" }, "features") });
        DatasetFacade part2 = DatasetFacade.dataRows(p.fit(df.get()).transform(df.get()).select("features"));
        AutoEncoder sparkDl4jNetwork = new AutoEncoder().setInputCol("features").setOutputCol("auto_encoded").setCompressedLayer(2).setTrainingMaster(new AutoEncoderNetworkTest.ParamHelper()).setMultiLayerConfiguration(getNNConfiguration());
        AutoEncoderModel sm = sparkDl4jNetwork.fit(part2.get());
        MultiLayerNetwork mln = sm.getNetwork();
        Assert.assertNotNull(mln);
    }

    @Test
    public void testAutoencoderSave() throws IOException {
        DatasetFacade df = DatasetFacade.dataRows(sqlContext.read().json("src/test/resources/autoencoders"));
        Pipeline p = new Pipeline().setStages(new PipelineStage[]{ AutoEncoderNetworkTest.getAssembler(new String[]{ "a", "b", "c", "d", "e", "f", "g", "h", "i", "j" }, "features") });
        DatasetFacade part2 = DatasetFacade.dataRows(p.fit(df.get()).transform(df.get()).select("features"));
        AutoEncoder sparkDl4jNetwork = new AutoEncoder().setInputCol("features").setOutputCol("auto_encoded").setCompressedLayer(2).setTrainingMaster(new AutoEncoderNetworkTest.ParamHelper()).setMultiLayerConfiguration(getNNConfiguration());
        AutoEncoderModel sm = sparkDl4jNetwork.fit(part2.get());
        String fileName = UUID.randomUUID().toString();
        sm.write().save(fileName);
        AutoEncoderModel spdm = AutoEncoderModel.load(fileName);
        Assert.assertNotNull(spdm);
        Assert.assertNotNull(spdm.transform(part2.get()));
        File file = new File(fileName);
        File file2 = new File((fileName + "_metadata"));
        FileUtils.deleteDirectory(file);
        FileUtils.deleteDirectory(file2);
    }

    public static class ParamHelper implements ParamSerializer {
        public ParameterAveragingTrainingMaster apply() {
            return new ParameterAveragingTrainingMaster.Builder(3).averagingFrequency(2).workerPrefetchNumBatches(2).batchSizePerWorker(2).build();
        }
    }
}

