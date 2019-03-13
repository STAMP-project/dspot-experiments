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
package org.deeplearning4j.nn.modelexport.solr.ltr.model;


import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.solr.ltr.feature.Feature;
import org.apache.solr.ltr.feature.FeatureException;
import org.apache.solr.request.SolrQueryRequest;
import org.deeplearning4j.nn.api.Model;
import org.junit.Test;


public class ScoringModelTest {
    protected static class DummyFeature extends Feature {
        public DummyFeature(String name) {
            super(name, Collections.EMPTY_MAP);
        }

        @Override
        protected void validate() throws FeatureException {
        }

        @Override
        public FeatureWeight createWeight(IndexSearcher searcher, boolean needsScores, SolrQueryRequest request, Query originalQuery, Map<String, String[]> efi) throws IOException {
            return null;
        }

        @Override
        public LinkedHashMap<String, Object> paramsToMap() {
            return null;
        }
    }

    @Test
    public void test() throws Exception {
        for (int numFeatures = 3; numFeatures <= 5; ++numFeatures) {
            for (Model model : new Model[]{ buildMultiLayerNetworkModel(numFeatures), buildComputationGraphModel(numFeatures) }) {
                doTest(model, numFeatures);
            }
        }
    }
}

