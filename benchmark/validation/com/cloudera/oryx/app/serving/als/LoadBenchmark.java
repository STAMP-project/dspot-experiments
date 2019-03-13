/**
 * Copyright (c) 2014, Cloudera, Inc. All Rights Reserved.
 *
 * Cloudera, Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"). You may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * This software is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for
 * the specific language governing permissions and limitations under the
 * License.
 */
package com.cloudera.oryx.app.serving.als;


import com.cloudera.oryx.app.serving.als.model.ALSServingModel;
import com.cloudera.oryx.app.serving.als.model.LoadTestALSModelFactory;
import com.cloudera.oryx.common.lang.ExecUtils;
import com.cloudera.oryx.common.random.RandomManager;
import com.cloudera.oryx.common.settings.ConfigUtils;
import com.typesafe.config.Config;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * <p>This can be run from the top level of the project with
 * {@code mvn -Pbenchmark -Pnetlib \
 *   -Doryx.test.als.benchmark.users=1000000 \
 *   -Doryx.test.als.benchmark.items=5000000 \
 *   -Doryx.test.als.benchmark.features=250 \
 *   -Doryx.test.als.benchmark.lshSampleRate=0.3 \
 *   -Doryx.test.als.benchmark.workers=2 \
 *   integration-test \
 *   -pl app/oryx-app-serving}</p>
 *
 * <p>Additional parameters defined in {@link LoadTestALSModelFactory} like
 * {@link LoadTestALSModelFactory#USERS} can be set by adding args like
 * {@code -Doryx.test.als.benchmark.users=1000000}.</p>
 *
 * <p>Note this test isn't run by default by surefire or failsafe. It is activated by a profile.</p>
 */
public final class LoadBenchmark extends AbstractALSServingTest {
    private static final Logger log = LoggerFactory.getLogger(LoadBenchmark.class);

    @Test
    public void testRecommendLoad() throws Exception {
        AtomicLong count = new AtomicLong();
        Mean meanReqTimeNanos = new Mean();
        long start = System.nanoTime();
        int workers = LoadTestALSModelFactory.WORKERS;
        ExecUtils.doInParallel(workers, workers, true, ( i) -> {
            RandomGenerator random = RandomManager.getRandom(((Integer.toString(i).hashCode()) ^ (System.nanoTime())));
            for (int j = 0; j < LoadTestALSModelFactory.REQS_PER_WORKER; j++) {
                String userID = "U" + (random.nextInt(LoadTestALSModelFactory.USERS));
                long callStart = System.nanoTime();
                target(("/recommend/" + userID)).request().accept(MediaType.APPLICATION_JSON_TYPE).get(AbstractALSServingTest.LIST_ID_VALUE_TYPE);
                long timeNanos = (System.nanoTime()) - callStart;
                if (j > 0) {
                    // Ignore first iteration's time as 'burn in'
                    synchronized(meanReqTimeNanos) {
                        meanReqTimeNanos.increment(timeNanos);
                    }
                }
                long currentCount = count.incrementAndGet();
                if ((currentCount % 100) == 0) {
                    log(currentCount, meanReqTimeNanos, start);
                }
            }
        });
        int totalRequests = workers * (LoadTestALSModelFactory.REQS_PER_WORKER);
        LoadBenchmark.log(totalRequests, meanReqTimeNanos, start);
    }

    public static final class MockLoadTestManagerInitListener extends AbstractALSServingTest.MockManagerInitListener {
        @Override
        protected AbstractALSServingTest.MockServingModelManager getModelManager() {
            return new LoadBenchmark.MockLoadTestServingModelManager(ConfigUtils.getDefault());
        }
    }

    private static final class MockLoadTestServingModelManager extends AbstractALSServingTest.MockServingModelManager {
        private final ALSServingModel model = LoadTestALSModelFactory.buildTestModel();

        private MockLoadTestServingModelManager(Config config) {
            super(config);
        }

        @Override
        public ALSServingModel getModel() {
            return model;
        }
    }
}

