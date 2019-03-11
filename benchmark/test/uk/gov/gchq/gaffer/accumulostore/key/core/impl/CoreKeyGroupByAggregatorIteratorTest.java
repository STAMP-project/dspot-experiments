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
package uk.gov.gchq.gaffer.accumulostore.key.core.impl;


import org.junit.Test;
import uk.gov.gchq.gaffer.accumulostore.AccumuloProperties;
import uk.gov.gchq.gaffer.accumulostore.AccumuloStore;
import uk.gov.gchq.gaffer.accumulostore.key.AccumuloElementConverter;
import uk.gov.gchq.gaffer.commonutil.StreamUtil;
import uk.gov.gchq.gaffer.store.StoreException;
import uk.gov.gchq.gaffer.store.schema.Schema;


public class CoreKeyGroupByAggregatorIteratorTest {
    private static AccumuloStore byteEntityStore;

    private static AccumuloStore gaffer1KeyStore;

    private static final Schema schema = Schema.fromJson(StreamUtil.schemas(CoreKeyGroupByAggregatorIteratorTest.class));

    private static final AccumuloProperties PROPERTIES = AccumuloProperties.loadStoreProperties(StreamUtil.storeProps(CoreKeyGroupByAggregatorIteratorTest.class));

    private static final AccumuloProperties CLASSIC_PROPERTIES = AccumuloProperties.loadStoreProperties(StreamUtil.openStream(CoreKeyGroupByAggregatorIteratorTest.class, "/accumuloStoreClassicKeys.properties"));

    private static AccumuloElementConverter byteEntityElementConverter;

    private static AccumuloElementConverter gaffer1ElementConverter;

    @Test
    public void shouldMultiplePropertySetsAggregateInByteEntityStore() throws StoreException {
        testAggregatingMultiplePropertySets(CoreKeyGroupByAggregatorIteratorTest.byteEntityStore, CoreKeyGroupByAggregatorIteratorTest.byteEntityElementConverter);
    }

    @Test
    public void shouldMultiplePropertySetsAggregateInGafferOneStore() throws StoreException {
        testAggregatingMultiplePropertySets(CoreKeyGroupByAggregatorIteratorTest.gaffer1KeyStore, CoreKeyGroupByAggregatorIteratorTest.gaffer1ElementConverter);
    }

    @Test
    public void shouldSinglePropertySetAggregateInByteEntityStore() throws StoreException {
        testAggregatingSinglePropertySet(CoreKeyGroupByAggregatorIteratorTest.byteEntityStore, CoreKeyGroupByAggregatorIteratorTest.byteEntityElementConverter);
    }

    @Test
    public void shouldSinglePropertySetAggregateInGafferOneStore() throws StoreException {
        testAggregatingSinglePropertySet(CoreKeyGroupByAggregatorIteratorTest.gaffer1KeyStore, CoreKeyGroupByAggregatorIteratorTest.gaffer1ElementConverter);
    }

    @Test
    public void shouldEmptyColumnQualifierAggregateInByteEntityStore() throws StoreException {
        testAggregatingEmptyColumnQualifier(CoreKeyGroupByAggregatorIteratorTest.byteEntityStore, CoreKeyGroupByAggregatorIteratorTest.byteEntityElementConverter);
    }

    @Test
    public void shouldEmptyColumnQualifierAggregateInGafferOneStore() throws StoreException {
        testAggregatingEmptyColumnQualifier(CoreKeyGroupByAggregatorIteratorTest.gaffer1KeyStore, CoreKeyGroupByAggregatorIteratorTest.gaffer1ElementConverter);
    }

    @Test
    public void shouldPartiallyAggregateColumnQualifierOverCQ1GroupByInByteEntityStore() throws StoreException {
        shouldPartiallyAggregateColumnQualifierOverCQ1GroupBy(CoreKeyGroupByAggregatorIteratorTest.byteEntityStore, CoreKeyGroupByAggregatorIteratorTest.byteEntityElementConverter);
    }

    @Test
    public void shouldPartiallyAggregateColumnQualifierOverCQ1GroupByInGafferOneStore() throws StoreException {
        shouldPartiallyAggregateColumnQualifierOverCQ1GroupBy(CoreKeyGroupByAggregatorIteratorTest.gaffer1KeyStore, CoreKeyGroupByAggregatorIteratorTest.gaffer1ElementConverter);
    }

    @Test
    public void shouldAggregatePropertiesOnlyWhenGroupByIsSetToCQ1CQ2InByteEntityStore() throws StoreException {
        shouldAggregatePropertiesOnlyWhenGroupByIsSetToCQ1CQ2(CoreKeyGroupByAggregatorIteratorTest.byteEntityStore, CoreKeyGroupByAggregatorIteratorTest.byteEntityElementConverter);
    }

    @Test
    public void shouldAggregatePropertiesOnlyWhenGroupByIsSetToCQ1CQ2InGafferOneStore() throws StoreException {
        shouldAggregatePropertiesOnlyWhenGroupByIsSetToCQ1CQ2(CoreKeyGroupByAggregatorIteratorTest.gaffer1KeyStore, CoreKeyGroupByAggregatorIteratorTest.gaffer1ElementConverter);
    }

    @Test
    public void shouldAggregateEverythingWhenGroupByIsSetToBlankInByteEntityStore() throws StoreException {
        shouldAggregateEverythingWhenGroupByIsSetToBlank(CoreKeyGroupByAggregatorIteratorTest.byteEntityStore, CoreKeyGroupByAggregatorIteratorTest.byteEntityElementConverter);
    }

    @Test
    public void shouldAggregateEverythingWhenGroupByIsSetToBlankInGafferOneStore() throws StoreException {
        shouldAggregateEverythingWhenGroupByIsSetToBlank(CoreKeyGroupByAggregatorIteratorTest.gaffer1KeyStore, CoreKeyGroupByAggregatorIteratorTest.gaffer1ElementConverter);
    }
}

