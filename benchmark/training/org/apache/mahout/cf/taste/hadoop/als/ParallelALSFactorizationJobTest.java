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
package org.apache.mahout.cf.taste.hadoop.als;


import java.io.File;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.util.ToolRunner;
import org.apache.mahout.cf.taste.hadoop.TasteHadoopUtils;
import org.apache.mahout.cf.taste.impl.TasteTestCase;
import org.apache.mahout.cf.taste.impl.common.FullRunningAverage;
import org.apache.mahout.cf.taste.impl.common.RunningAverage;
import org.apache.mahout.common.MahoutTestCase;
import org.apache.mahout.math.Vector;
import org.apache.mahout.math.hadoop.MathHelper;
import org.apache.mahout.math.map.OpenIntLongHashMap;
import org.apache.mahout.math.map.OpenIntObjectHashMap;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ParallelALSFactorizationJobTest extends TasteTestCase {
    private static final Logger log = LoggerFactory.getLogger(ParallelALSFactorizationJobTest.class);

    private File inputFile;

    private File intermediateDir;

    private File outputDir;

    private File tmpDir;

    private Configuration conf;

    @Test
    public void completeJobToyExample() throws Exception {
        explicitExample(1);
    }

    @Test
    public void completeJobToyExampleMultithreaded() throws Exception {
        explicitExample(2);
    }

    @Test
    public void completeJobImplicitToyExample() throws Exception {
        implicitExample(1);
    }

    @Test
    public void completeJobImplicitToyExampleMultithreaded() throws Exception {
        implicitExample(2);
    }

    @Test
    public void exampleWithIDMapping() throws Exception {
        String[] preferencesWithLongIDs = new String[]{ "5568227754922264005,-4758971626494767444,5.0", "5568227754922264005,3688396615879561990,5.0", "5568227754922264005,4594226737871995304,2.0", "550945997885173934,-4758971626494767444,2.0", "550945997885173934,4594226737871995304,3.0", "550945997885173934,706816485922781596,5.0", "2448095297482319463,3688396615879561990,5.0", "2448095297482319463,706816485922781596,3.0", "6839920411763636962,-4758971626494767444,3.0", "6839920411763636962,706816485922781596,5.0" };
        MahoutTestCase.writeLines(inputFile, preferencesWithLongIDs);
        ParallelALSFactorizationJob alsFactorization = new ParallelALSFactorizationJob();
        alsFactorization.setConf(conf);
        int numFeatures = 3;
        int numIterations = 5;
        double lambda = 0.065;
        alsFactorization.run(new String[]{ "--input", inputFile.getAbsolutePath(), "--output", outputDir.getAbsolutePath(), "--tempDir", tmpDir.getAbsolutePath(), "--lambda", String.valueOf(lambda), "--numFeatures", String.valueOf(numFeatures), "--numIterations", String.valueOf(numIterations), "--numThreadsPerSolver", String.valueOf(1), "--usesLongIDs", String.valueOf(true) });
        OpenIntLongHashMap userIDIndex = TasteHadoopUtils.readIDIndexMap(((outputDir.getAbsolutePath()) + "/userIDIndex/part-r-00000"), conf);
        assertEquals(4, userIDIndex.size());
        OpenIntLongHashMap itemIDIndex = TasteHadoopUtils.readIDIndexMap(((outputDir.getAbsolutePath()) + "/itemIDIndex/part-r-00000"), conf);
        assertEquals(4, itemIDIndex.size());
        OpenIntObjectHashMap<Vector> u = MathHelper.readMatrixRows(conf, new Path(outputDir.getAbsolutePath(), "U/part-m-00000"));
        OpenIntObjectHashMap<Vector> m = MathHelper.readMatrixRows(conf, new Path(outputDir.getAbsolutePath(), "M/part-m-00000"));
        assertEquals(4, u.size());
        assertEquals(4, m.size());
        RunningAverage avg = new FullRunningAverage();
        for (String line : preferencesWithLongIDs) {
            String[] tokens = TasteHadoopUtils.splitPrefTokens(line);
            long userID = Long.parseLong(tokens[TasteHadoopUtils.USER_ID_POS]);
            long itemID = Long.parseLong(tokens[TasteHadoopUtils.ITEM_ID_POS]);
            double rating = Double.parseDouble(tokens[2]);
            Vector userFeatures = u.get(TasteHadoopUtils.idToIndex(userID));
            Vector itemFeatures = m.get(TasteHadoopUtils.idToIndex(itemID));
            double estimate = userFeatures.dot(itemFeatures);
            double err = rating - estimate;
            avg.addDatum((err * err));
        }
        double rmse = Math.sqrt(avg.getAverage());
        ParallelALSFactorizationJobTest.log.info("RMSE: {}", rmse);
        assertTrue((rmse < 0.2));
    }

    @Test
    public void recommenderJobWithIDMapping() throws Exception {
        String[] preferencesWithLongIDs = new String[]{ "5568227754922264005,-4758971626494767444,5.0", "5568227754922264005,3688396615879561990,5.0", "5568227754922264005,4594226737871995304,2.0", "550945997885173934,-4758971626494767444,2.0", "550945997885173934,4594226737871995304,3.0", "550945997885173934,706816485922781596,5.0", "2448095297482319463,3688396615879561990,5.0", "2448095297482319463,706816485922781596,3.0", "6839920411763636962,-4758971626494767444,3.0", "6839920411763636962,706816485922781596,5.0" };
        MahoutTestCase.writeLines(inputFile, preferencesWithLongIDs);
        ParallelALSFactorizationJob alsFactorization = new ParallelALSFactorizationJob();
        alsFactorization.setConf(conf);
        int numFeatures = 3;
        int numIterations = 5;
        double lambda = 0.065;
        Configuration conf = getConfiguration();
        int success = ToolRunner.run(alsFactorization, new String[]{ "-Dhadoop.tmp.dir=" + (conf.get("hadoop.tmp.dir")), "--input", inputFile.getAbsolutePath(), "--output", intermediateDir.getAbsolutePath(), "--tempDir", tmpDir.getAbsolutePath(), "--lambda", String.valueOf(lambda), "--numFeatures", String.valueOf(numFeatures), "--numIterations", String.valueOf(numIterations), "--numThreadsPerSolver", String.valueOf(1), "--usesLongIDs", String.valueOf(true) });
        assertEquals(0, success);
        // reset as we run in the same JVM
        SharingMapper.reset();
        RecommenderJob recommender = new RecommenderJob();
        success = ToolRunner.run(recommender, new String[]{ "-Dhadoop.tmp.dir=" + (conf.get("hadoop.tmp.dir")), "--input", (intermediateDir.getAbsolutePath()) + "/userRatings/", "--userFeatures", (intermediateDir.getAbsolutePath()) + "/U/", "--itemFeatures", (intermediateDir.getAbsolutePath()) + "/M/", "--numRecommendations", String.valueOf(2), "--maxRating", String.valueOf(5.0), "--numThreads", String.valueOf(2), "--usesLongIDs", String.valueOf(true), "--userIDIndex", (intermediateDir.getAbsolutePath()) + "/userIDIndex/", "--itemIDIndex", (intermediateDir.getAbsolutePath()) + "/itemIDIndex/", "--output", outputDir.getAbsolutePath() });
        assertEquals(0, success);
    }
}

