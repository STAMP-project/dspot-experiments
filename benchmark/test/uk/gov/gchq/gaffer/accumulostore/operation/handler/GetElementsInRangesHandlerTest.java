/**
 * Copyright 2016-2019 Crown Copyright
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.gov.gchq.gaffer.accumulostore.operation.handler;


import org.junit.Test;
import uk.gov.gchq.gaffer.accumulostore.AccumuloProperties;
import uk.gov.gchq.gaffer.accumulostore.AccumuloStore;
import uk.gov.gchq.gaffer.commonutil.StreamUtil;
import uk.gov.gchq.gaffer.data.elementdefinition.view.View;
import uk.gov.gchq.gaffer.operation.OperationException;
import uk.gov.gchq.gaffer.store.Context;
import uk.gov.gchq.gaffer.store.operation.handler.OutputOperationHandler;
import uk.gov.gchq.gaffer.store.schema.Schema;


public class GetElementsInRangesHandlerTest {
    private static final int NUM_ENTRIES = 1000;

    private static View defaultView;

    private static AccumuloStore byteEntityStore;

    private static AccumuloStore gaffer1KeyStore;

    private static final Schema schema = Schema.fromJson(StreamUtil.schemas(GetElementsInRangesHandlerTest.class));

    private static final AccumuloProperties PROPERTIES = AccumuloProperties.loadStoreProperties(StreamUtil.storeProps(GetElementsInRangesHandlerTest.class));

    private static final AccumuloProperties CLASSIC_PROPERTIES = AccumuloProperties.loadStoreProperties(StreamUtil.openStream(GetElementsInRangesHandlerTest.class, "/accumuloStoreClassicKeys.properties"));

    private static final Context context = new Context();

    private OutputOperationHandler handler;

    @Test
    public void testNoSummarisationByteEntityStore() throws OperationException {
        shouldReturnElementsNoSummarisation(GetElementsInRangesHandlerTest.byteEntityStore);
    }

    @Test
    public void testNoSummarisationGaffer1Store() throws OperationException {
        shouldReturnElementsNoSummarisation(GetElementsInRangesHandlerTest.gaffer1KeyStore);
    }

    @Test
    public void shouldSummariseByteEntityStore() throws OperationException {
        shouldSummarise(GetElementsInRangesHandlerTest.byteEntityStore);
    }

    @Test
    public void shouldSummariseGaffer2Store() throws OperationException {
        shouldSummarise(GetElementsInRangesHandlerTest.gaffer1KeyStore);
    }

    @Test
    public void shouldSummariseOutGoingEdgesOnlyByteEntityStore() throws OperationException {
        shouldSummariseOutGoingEdgesOnly(GetElementsInRangesHandlerTest.byteEntityStore);
    }

    @Test
    public void shouldSummariseOutGoingEdgesOnlyGaffer1Store() throws OperationException {
        shouldSummariseOutGoingEdgesOnly(GetElementsInRangesHandlerTest.gaffer1KeyStore);
    }

    @Test
    public void shouldHaveNoIncomingEdgesByteEntityStore() throws OperationException {
        shouldHaveNoIncomingEdges(GetElementsInRangesHandlerTest.byteEntityStore);
    }

    @Test
    public void shouldHaveNoIncomingEdgesGaffer1Store() throws OperationException {
        shouldHaveNoIncomingEdges(GetElementsInRangesHandlerTest.gaffer1KeyStore);
    }

    @Test
    public void shouldReturnNothingWhenNoEdgesSetByteEntityStore() throws OperationException {
        shouldReturnNothingWhenNoEdgesSet(GetElementsInRangesHandlerTest.byteEntityStore);
    }

    @Test
    public void shouldReturnNothingWhenNoEdgesSetGaffer1Store() throws OperationException {
        shouldReturnNothingWhenNoEdgesSet(GetElementsInRangesHandlerTest.gaffer1KeyStore);
    }
}

