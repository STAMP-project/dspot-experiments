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


public class AccumuloIDWithinSetRetrieverTest {
    private static View defaultView;

    private static AccumuloStore byteEntityStore;

    private static AccumuloStore gaffer1KeyStore;

    private static final Schema schema = Schema.fromJson(StreamUtil.schemas(AccumuloIDWithinSetRetrieverTest.class));

    private static final AccumuloProperties PROPERTIES = AccumuloProperties.loadStoreProperties(StreamUtil.storeProps(AccumuloIDWithinSetRetrieverTest.class));

    private static final AccumuloProperties CLASSIC_PROPERTIES = AccumuloProperties.loadStoreProperties(StreamUtil.openStream(AccumuloIDWithinSetRetrieverTest.class, "/accumuloStoreClassicKeys.properties"));

    /**
     * Tests that the correct {@link uk.gov.gchq.gaffer.data.element.Edge}s are returned. Tests that {@link uk.gov.gchq.gaffer.data.element.Entity}s are also returned
     * (unless the return edges only option has been set on the {@link GetElementsWithinSet}). It is desirable
     * for {@link uk.gov.gchq.gaffer.data.element.Entity}s to be returned as a common use-case is to use this method to complete the "half-hop"
     * in a breadth-first search, and then getting all the information about the nodes is often required.
     */
    @Test
    public void shouldGetCorrectEdgesInMemoryFromByteEntityStore() throws StoreException {
        shouldGetCorrectEdges(true, AccumuloIDWithinSetRetrieverTest.byteEntityStore);
    }

    @Test
    public void shouldGetCorrectEdgesInMemoryFromGaffer1Store() throws StoreException {
        shouldGetCorrectEdges(true, AccumuloIDWithinSetRetrieverTest.gaffer1KeyStore);
    }

    @Test
    public void shouldGetCorrectEdgesFromByteEntityStore() throws StoreException {
        shouldGetCorrectEdges(false, AccumuloIDWithinSetRetrieverTest.byteEntityStore);
    }

    @Test
    public void shouldGetCorrectEdgesFromGaffer1Store() throws StoreException {
        shouldGetCorrectEdges(false, AccumuloIDWithinSetRetrieverTest.gaffer1KeyStore);
    }

    /**
     * Tests that the subtle case of setting outgoing or incoming edges only option is dealt with correctly.
     * When querying for edges within a set, the outgoing or incoming edges only needs to be turned off, for
     * two reasons. First, it doesn't make conceptual sense. If the each is from a member of set X to another
     * member of set X, what would it mean for it to be "outgoing"? (It makes sense to ask for directed edges
     * only, or undirected edges only.) Second, if the option is left on then results can be missed. For example,
     * suppose we have a graph with an edge A->B and we ask for all edges with both ends in the set {A,B}. Consider
     * what happens using the batching mechanism, with A in the first batch and B in the second batch. When the
     * first batch is queried for, the Bloom filter will consist solely of {A}. Thus the edge A->B will not be
     * returned. When the next batch is queried for, the Bloom filter will consist of A and B, so normally the
     * edge A to B will be returned. But if the outgoing edges only option is turned on then the edge will not be
     * returned, as it is not an edge out of B.
     */
    @Test
    public void shouldDealWithOutgoingEdgesOnlyOptionGaffer1KeyStore() {
        shouldDealWithOutgoingEdgesOnlyOption(AccumuloIDWithinSetRetrieverTest.gaffer1KeyStore);
    }

    @Test
    public void shouldDealWithOutgoingEdgesOnlyOptionByteEntityStore() {
        shouldDealWithOutgoingEdgesOnlyOption(AccumuloIDWithinSetRetrieverTest.byteEntityStore);
    }

    /**
     * Tests that the directed edges only and undirected edges only options are respected.
     */
    @Test
    public void shouldDealWithDirectedEdgesOnlyInMemoryByteEntityStore() throws StoreException {
        shouldDealWithDirectedEdgesOnlyOption(true, AccumuloIDWithinSetRetrieverTest.byteEntityStore);
    }

    @Test
    public void shouldDealWithDirectedEdgesOnlyInMemoryGaffer1Store() throws StoreException {
        shouldDealWithDirectedEdgesOnlyOption(true, AccumuloIDWithinSetRetrieverTest.gaffer1KeyStore);
    }

    @Test
    public void shouldDealWithDirectedEdgesOnlyByteEntityStore() throws StoreException {
        shouldDealWithDirectedEdgesOnlyOption(false, AccumuloIDWithinSetRetrieverTest.byteEntityStore);
    }

    @Test
    public void shouldDealWithDirectedEdgesOnlyGaffer1Store() throws StoreException {
        shouldDealWithDirectedEdgesOnlyOption(false, AccumuloIDWithinSetRetrieverTest.gaffer1KeyStore);
    }

    /**
     * Tests that false positives are filtered out. It does this by explicitly finding a false positive (i.e. something
     * that matches the Bloom filter but that wasn't put into the filter) and adding that to the data, and then
     * checking that isn't returned.
     */
    @Test
    public void shouldDealWithFalsePositivesInMemoryByteEntityStore() throws StoreException {
        shouldDealWithFalsePositives(true, AccumuloIDWithinSetRetrieverTest.byteEntityStore);
    }

    @Test
    public void shouldDealWithFalsePositivesInMemoryGaffer1Store() throws StoreException {
        shouldDealWithFalsePositives(true, AccumuloIDWithinSetRetrieverTest.gaffer1KeyStore);
    }

    @Test
    public void shouldDealWithFalsePositivesByteEntityStore() throws StoreException {
        shouldDealWithFalsePositives(false, AccumuloIDWithinSetRetrieverTest.byteEntityStore);
    }

    @Test
    public void shouldDealWithFalsePositivesGaffer1Store() throws StoreException {
        shouldDealWithFalsePositives(false, AccumuloIDWithinSetRetrieverTest.gaffer1KeyStore);
    }

    /**
     * Tests that standard filtering (e.g. by summary type, or by time window, or to only receive entities) is still
     * applied.
     */
    @Test
    public void shouldStillApplyOtherFilterByteEntityStoreInMemoryEntities() throws StoreException {
        shouldStillApplyOtherFilter(true, AccumuloIDWithinSetRetrieverTest.byteEntityStore);
    }

    @Test
    public void shouldStillApplyFilterGaffer1StoreInMemoryEntities() throws StoreException {
        shouldStillApplyOtherFilter(true, AccumuloIDWithinSetRetrieverTest.gaffer1KeyStore);
    }

    @Test
    public void shouldStillApplyOtherFilterByteEntityStore() throws StoreException {
        shouldStillApplyOtherFilter(false, AccumuloIDWithinSetRetrieverTest.byteEntityStore);
    }

    @Test
    public void shouldStillApplyFilterGaffer1Store() throws StoreException {
        shouldStillApplyOtherFilter(false, AccumuloIDWithinSetRetrieverTest.gaffer1KeyStore);
    }

    @Test
    public void shouldReturnMoreElementsThanFitInBatchScannerByteStoreInMemory() throws StoreException {
        shouldLoadElementsWhenMoreElementsThanFitInBatchScanner(true, AccumuloIDWithinSetRetrieverTest.byteEntityStore);
    }

    @Test
    public void shouldReturnMoreElementsThanFitInBatchScannerGaffer1StoreInMemory() throws StoreException {
        shouldLoadElementsWhenMoreElementsThanFitInBatchScanner(true, AccumuloIDWithinSetRetrieverTest.gaffer1KeyStore);
    }

    @Test
    public void shouldReturnMoreElementsThanFitInBatchScannerByteStore() throws StoreException {
        shouldLoadElementsWhenMoreElementsThanFitInBatchScanner(false, AccumuloIDWithinSetRetrieverTest.byteEntityStore);
    }

    @Test
    public void shouldReturnMoreElementsThanFitInBatchScannerGaffer1Store() throws StoreException {
        shouldLoadElementsWhenMoreElementsThanFitInBatchScanner(false, AccumuloIDWithinSetRetrieverTest.gaffer1KeyStore);
    }
}

