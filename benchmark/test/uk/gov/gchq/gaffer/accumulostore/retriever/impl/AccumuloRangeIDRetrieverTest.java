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
import uk.gov.gchq.gaffer.store.schema.Schema;


public class AccumuloRangeIDRetrieverTest {
    private static final int numEntries = 1000;

    private static View defaultView;

    private static AccumuloStore byteEntityStore;

    private static AccumuloStore gaffer1KeyStore;

    private static final Schema schema = Schema.fromJson(StreamUtil.schemas(AccumuloRangeIDRetrieverTest.class));

    private static final AccumuloProperties PROPERTIES = AccumuloProperties.loadStoreProperties(StreamUtil.storeProps(AccumuloRangeIDRetrieverTest.class));

    private static final AccumuloProperties CLASSIC_PROPERTIES = AccumuloProperties.loadStoreProperties(StreamUtil.openStream(AccumuloRangeIDRetrieverTest.class, "/accumuloStoreClassicKeys.properties"));

    @Test
    public void shouldRetrieveElementsInRangeBetweenSeedsByteEntityStore() throws Exception {
        shouldRetrieveElementsInRangeBetweenSeeds(AccumuloRangeIDRetrieverTest.byteEntityStore);
    }

    @Test
    public void shouldRetrieveElementsInRangeBetweenSeedsGaffer1Store() throws Exception {
        shouldRetrieveElementsInRangeBetweenSeeds(AccumuloRangeIDRetrieverTest.gaffer1KeyStore);
    }
}

