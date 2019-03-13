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
package uk.gov.gchq.gaffer.accumulostore.key.impl;


import org.junit.Test;
import uk.gov.gchq.gaffer.accumulostore.AccumuloProperties;
import uk.gov.gchq.gaffer.accumulostore.AccumuloStore;
import uk.gov.gchq.gaffer.accumulostore.key.AccumuloElementConverter;
import uk.gov.gchq.gaffer.accumulostore.key.exception.RangeFactoryException;
import uk.gov.gchq.gaffer.commonutil.StreamUtil;
import uk.gov.gchq.gaffer.store.StoreException;
import uk.gov.gchq.gaffer.store.schema.Schema;


public class RowIdAggregatorTest {
    private static AccumuloStore byteEntityStore;

    private static AccumuloStore gaffer1KeyStore;

    private static final Schema schema = Schema.fromJson(StreamUtil.schemas(RowIdAggregatorTest.class));

    private static final AccumuloProperties PROPERTIES = AccumuloProperties.loadStoreProperties(StreamUtil.storeProps(RowIdAggregatorTest.class));

    private static final AccumuloProperties CLASSIC_PROPERTIES = AccumuloProperties.loadStoreProperties(StreamUtil.openStream(RowIdAggregatorTest.class, "/accumuloStoreClassicKeys.properties"));

    private static AccumuloElementConverter byteEntityElementConverter;

    private static AccumuloElementConverter gaffer1ElementConverter;

    @Test
    public void testMultiplePropertySetsAggregateAcrossRowIDInByteEntityStore() throws RangeFactoryException, StoreException {
        testAggregatingMultiplePropertySetsAcrossRowIDRange(RowIdAggregatorTest.byteEntityStore, RowIdAggregatorTest.byteEntityElementConverter);
    }

    @Test
    public void testMultiplePropertySetsAggregateAcrossRowIDInGafferOneStore() throws RangeFactoryException, StoreException {
        testAggregatingMultiplePropertySetsAcrossRowIDRange(RowIdAggregatorTest.gaffer1KeyStore, RowIdAggregatorTest.gaffer1ElementConverter);
    }
}

