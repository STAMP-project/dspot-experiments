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
package org.deeplearning4j.nn.dataimport.solr.client.solrj.io.stream;


import Activation.IDENTITY;
import BasicWorkspaceManager.WorkspaceDeallocatorThreadName;
import NativeRandomDeallocator.DeallocatorThreadNamePrefix;
import WeightInit.ONES;
import com.carrotsearch.randomizedtesting.ThreadFilter;
import com.carrotsearch.randomizedtesting.annotations.ThreadLeakFilters;
import org.apache.solr.cloud.SolrCloudTestCase;
import org.deeplearning4j.nn.api.Model;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.junit.Test;


@ThreadLeakFilters(defaultFilters = true, filters = { TupleStreamDataSetIteratorTest.PrivateDeallocatorThreadsFilter.class })
public class TupleStreamDataSetIteratorTest extends SolrCloudTestCase {
    public static class PrivateDeallocatorThreadsFilter implements ThreadFilter {
        /**
         * Reject deallocator threads over whose cleanup this test has no control.
         */
        @Override
        public boolean reject(Thread thread) {
            final ThreadGroup threadGroup = thread.getThreadGroup();
            final String threadGroupName = (threadGroup == null) ? null : threadGroup.getName();
            if ((threadGroupName != null) && (threadGroupName.endsWith(TupleStreamDataSetIteratorTest.class.getSimpleName()))) {
                final String threadName = thread.getName();
                if (((threadName.startsWith(DeallocatorThreadNamePrefix)) || (threadName.equals("JavaCPP Deallocator"))) || (threadName.equals(WorkspaceDeallocatorThreadName))) {
                    return true;
                }
            }
            return false;
        }
    }

    private static int numDocs = 0;

    private static class CountingIterationListener extends ScoreIterationListener {
        private int numIterationsDone = 0;

        public CountingIterationListener() {
            super(1);
        }

        public int numIterationsDone() {
            return numIterationsDone;
        }

        @Override
        public void iterationDone(Model model, int iteration, int epoch) {
            super.iterationDone(model, iteration, epoch);
            ++(numIterationsDone);
        }
    }

    @Test
    public void iterateTest() throws Exception {
        doIterateTest(true);
        doIterateTest(false);
    }

    @Test
    public void modelFitTest() throws Exception {
        final MultiLayerNetwork model = new MultiLayerNetwork(new NeuralNetConfiguration.Builder().list(nIn(3).nOut(1).weightInit(ONES).activation(IDENTITY).build()).build());
        model.init();
        int batch = 1;
        for (int ii = 1; ii <= 5; ++ii) {
            final TupleStreamDataSetIteratorTest.CountingIterationListener listener = new TupleStreamDataSetIteratorTest.CountingIterationListener();
            model.setListeners(listener);
            batch *= 2;
            try (final TupleStreamDataSetIterator tsdsi = /* idKey */
            new TupleStreamDataSetIterator(batch, "id", new String[]{ "channel_b_f", "channel_g_f", "channel_r_f" }, new String[]{ "luminance_f" }, ("search(mySolrCollection," + ((("q=\"id:*\"," + "fl=\"id,channel_b_f,channel_g_f,channel_r_f,luminance_f\",") + "sort=\"id asc\",") + "qt=\"/export\")")), cluster.getZkClient().getZkServerAddress())) {
                model.fit(tsdsi);
            }
            assertEquals(((((("numIterationsDone=" + (listener.numIterationsDone())) + " numDocs=") + (TupleStreamDataSetIteratorTest.numDocs)) + " batch=") + batch), (((TupleStreamDataSetIteratorTest.numDocs) + (batch - 1)) / batch), listener.numIterationsDone());
        }
    }
}

