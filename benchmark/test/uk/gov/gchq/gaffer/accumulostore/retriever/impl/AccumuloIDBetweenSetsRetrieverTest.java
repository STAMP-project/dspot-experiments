/**
 * Copyright 2016-2019 Crown Copyright
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
package uk.gov.gchq.gaffer.accumulostore.retriever.impl;


import org.junit.Test;
import uk.gov.gchq.gaffer.accumulostore.AccumuloProperties;
import uk.gov.gchq.gaffer.accumulostore.AccumuloStore;
import uk.gov.gchq.gaffer.commonutil.StreamUtil;
import uk.gov.gchq.gaffer.data.elementdefinition.view.View;
import uk.gov.gchq.gaffer.store.StoreException;
import uk.gov.gchq.gaffer.store.schema.Schema;


public class AccumuloIDBetweenSetsRetrieverTest {
    private static View defaultView;

    private static View edgeOnlyView;

    private static View entityOnlyView;

    private static AccumuloStore byteEntityStore;

    private static AccumuloStore gaffer1KeyStore;

    private static final Schema schema = Schema.fromJson(StreamUtil.schemas(AccumuloIDBetweenSetsRetrieverTest.class));

    private static final AccumuloProperties PROPERTIES = AccumuloProperties.loadStoreProperties(StreamUtil.storeProps(AccumuloIDBetweenSetsRetrieverTest.class));

    private static final AccumuloProperties CLASSIC_PROPERTIES = AccumuloProperties.loadStoreProperties(StreamUtil.openStream(AccumuloIDBetweenSetsRetrieverTest.class, "/accumuloStoreClassicKeys.properties"));

    @Test
    public void shouldGetCorrectEdgesInMemoryFromByteEntityStore() throws StoreException {
        shouldGetCorrectEdges(true, AccumuloIDBetweenSetsRetrieverTest.byteEntityStore);
    }

    @Test
    public void shouldGetCorrectEdgesInMemoryFromGaffer1Store() throws StoreException {
        shouldGetCorrectEdges(true, AccumuloIDBetweenSetsRetrieverTest.gaffer1KeyStore);
    }

    @Test
    public void shouldGetCorrectEdgesFromByteEntityStore() throws StoreException {
        shouldGetCorrectEdges(false, AccumuloIDBetweenSetsRetrieverTest.byteEntityStore);
    }

    @Test
    public void shouldGetCorrectEdgesFromGaffer1Store() throws StoreException {
        shouldGetCorrectEdges(false, AccumuloIDBetweenSetsRetrieverTest.gaffer1KeyStore);
    }

    @Test
    public void shouldDealWithOutgoingEdgesOnlyOptionGaffer1KeyStore() {
        shouldDealWithOutgoingEdgesOnlyOption(AccumuloIDBetweenSetsRetrieverTest.gaffer1KeyStore);
    }

    @Test
    public void shouldDealWithOutgoingEdgesOnlyOptionByteEntityStore() {
        shouldDealWithOutgoingEdgesOnlyOption(AccumuloIDBetweenSetsRetrieverTest.byteEntityStore);
    }

    /**
     * Tests that the directed edges only and undirected edges only options are respected.
     */
    @Test
    public void shouldDealWithDirectedEdgesOnlyInMemoryByteEntityStore() {
        shouldDealWithDirectedEdgesOnlyOption(true, AccumuloIDBetweenSetsRetrieverTest.byteEntityStore);
    }

    @Test
    public void shouldDealWithDirectedEdgesOnlyInMemoryGaffer1Store() {
        shouldDealWithDirectedEdgesOnlyOption(true, AccumuloIDBetweenSetsRetrieverTest.gaffer1KeyStore);
    }

    @Test
    public void shouldDealWithDirectedEdgesOnlyByteEntityStore() {
        shouldDealWithDirectedEdgesOnlyOption(false, AccumuloIDBetweenSetsRetrieverTest.byteEntityStore);
    }

    @Test
    public void shouldDealWithDirectedEdgesOnlyGaffer1Store() {
        shouldDealWithDirectedEdgesOnlyOption(false, AccumuloIDBetweenSetsRetrieverTest.gaffer1KeyStore);
    }

    /**
     * Tests that false positives are filtered out. It does this by explicitly finding a false positive (i.e. something
     * that matches the Bloom filter but that wasn't put into the filter) and adding that to the data, and then
     * checking that isn't returned.
     *
     * @throws uk.gov.gchq.gaffer.store.StoreException
     * 		if an error is encountered
     */
    @Test
    public void shouldDealWithFalsePositivesInMemoryByteEntityStore() throws StoreException {
        shouldDealWithFalsePositives(true, AccumuloIDBetweenSetsRetrieverTest.byteEntityStore);
    }

    @Test
    public void shouldDealWithFalsePositivesInMemoryGaffer1Store() throws StoreException {
        shouldDealWithFalsePositives(true, AccumuloIDBetweenSetsRetrieverTest.gaffer1KeyStore);
    }

    @Test
    public void shouldDealWithFalsePositivesByteEntityStore() throws StoreException {
        shouldDealWithFalsePositives(false, AccumuloIDBetweenSetsRetrieverTest.byteEntityStore);
    }

    @Test
    public void shouldDealWithFalsePositivesGaffer1Store() throws StoreException {
        shouldDealWithFalsePositives(false, AccumuloIDBetweenSetsRetrieverTest.gaffer1KeyStore);
    }

    /**
     * Tests that standard filtering (e.g. by summary type, or to only receive entities) is still
     * applied.
     *
     * @throws uk.gov.gchq.gaffer.store.StoreException
     * 		if an error is encountered
     */
    @Test
    public void shouldOtherFilteringStillAppliedByteEntityStoreInMemoryEntities() throws StoreException {
        shouldStillApplyOtherFilter(true, AccumuloIDBetweenSetsRetrieverTest.byteEntityStore);
    }

    @Test
    public void shouldOtherFilteringStillAppliedGaffer1StoreInMemoryEntities() throws StoreException {
        shouldStillApplyOtherFilter(true, AccumuloIDBetweenSetsRetrieverTest.gaffer1KeyStore);
    }

    @Test
    public void shouldOtherFilteringStillAppliedByteEntityStore() throws StoreException {
        shouldStillApplyOtherFilter(false, AccumuloIDBetweenSetsRetrieverTest.byteEntityStore);
    }

    @Test
    public void shouldOtherFilteringStillAppliedGaffer1Store() throws StoreException {
        shouldStillApplyOtherFilter(false, AccumuloIDBetweenSetsRetrieverTest.gaffer1KeyStore);
    }

    @Test
    public void shouldReturnMoreElementsThanFitInBatchScannerByteStoreInMemory() throws StoreException {
        shouldLoadElementsWhenMoreElementsThanFitInBatchScanner(true, AccumuloIDBetweenSetsRetrieverTest.byteEntityStore);
    }

    @Test
    public void shouldReturnMoreElementsThanFitInBatchScannerGaffer1StoreInMemory() throws StoreException {
        shouldLoadElementsWhenMoreElementsThanFitInBatchScanner(true, AccumuloIDBetweenSetsRetrieverTest.gaffer1KeyStore);
    }

    @Test
    public void shouldReturnMoreElementsThanFitInBatchScannerByteStore() throws StoreException {
        shouldLoadElementsWhenMoreElementsThanFitInBatchScanner(true, AccumuloIDBetweenSetsRetrieverTest.byteEntityStore);
    }

    @Test
    public void shouldReturnMoreElementsThanFitInBatchScannerGaffer1Store() throws StoreException {
        shouldLoadElementsWhenMoreElementsThanFitInBatchScanner(true, AccumuloIDBetweenSetsRetrieverTest.gaffer1KeyStore);
    }

    @Test
    public void testEdgesWithinSetAAreNotReturnedByteStoreInMemory() throws StoreException {
        testEdgesWithinSetAAreNotReturned(true, AccumuloIDBetweenSetsRetrieverTest.byteEntityStore);
    }

    @Test
    public void testEdgesWithinSetAAreNotReturnedByteStoreGaffer1StoreInMemory() throws StoreException {
        testEdgesWithinSetAAreNotReturned(true, AccumuloIDBetweenSetsRetrieverTest.gaffer1KeyStore);
    }

    @Test
    public void testEdgesWithinSetAAreNotReturnedByteStore() throws StoreException {
        testEdgesWithinSetAAreNotReturned(false, AccumuloIDBetweenSetsRetrieverTest.byteEntityStore);
    }

    @Test
    public void testEdgesWithinSetAAreNotReturnedByteStoreGaffer1Store() throws StoreException {
        testEdgesWithinSetAAreNotReturned(false, AccumuloIDBetweenSetsRetrieverTest.gaffer1KeyStore);
    }
}

