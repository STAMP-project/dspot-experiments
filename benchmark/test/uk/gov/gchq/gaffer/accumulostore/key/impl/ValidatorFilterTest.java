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


import AccumuloStoreConstants.ACCUMULO_ELEMENT_CONVERTER_CLASS;
import AccumuloStoreConstants.SCHEMA;
import TestGroups.EDGE;
import java.util.HashMap;
import java.util.Map;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Value;
import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.accumulostore.key.core.impl.byteEntity.ByteEntityAccumuloElementConverter;
import uk.gov.gchq.gaffer.commonutil.pair.Pair;
import uk.gov.gchq.gaffer.data.element.Edge;
import uk.gov.gchq.gaffer.data.element.Element;


public class ValidatorFilterTest {
    @Test
    public void shouldThrowIllegalArgumentExceptionWhenValidateOptionsWithNoSchema() throws Exception {
        // Given
        final ValidatorFilter filter = new ValidatorFilter();
        final Map<String, String> options = new HashMap<>();
        options.put(ACCUMULO_ELEMENT_CONVERTER_CLASS, ByteEntityAccumuloElementConverter.class.getName());
        // When / Then
        try {
            filter.validateOptions(options);
        } catch (final IllegalArgumentException e) {
            Assert.assertTrue(e.getMessage().contains(SCHEMA));
        }
    }

    @Test
    public void shouldThrowIllegalArgumentExceptionWhenValidateOptionsWithElementConverterClass() throws Exception {
        // Given
        final ValidatorFilter filter = new ValidatorFilter();
        final Map<String, String> options = new HashMap<>();
        options.put(SCHEMA, getSchemaJson());
        // When / Then
        try {
            filter.validateOptions(options);
        } catch (final IllegalArgumentException e) {
            Assert.assertTrue(e.getMessage().contains(ACCUMULO_ELEMENT_CONVERTER_CLASS));
        }
    }

    @Test
    public void shouldReturnTrueWhenValidOptions() throws Exception {
        // Given
        final ValidatorFilter filter = new ValidatorFilter();
        final Map<String, String> options = new HashMap<>();
        options.put(SCHEMA, getSchemaJson());
        options.put(ACCUMULO_ELEMENT_CONVERTER_CLASS, ByteEntityAccumuloElementConverter.class.getName());
        // When
        final boolean isValid = filter.validateOptions(options);
        // Then
        Assert.assertTrue(isValid);
    }

    @Test
    public void shouldAcceptElementWhenSchemaValidatorAcceptsElement() throws Exception {
        // Given
        final ValidatorFilter filter = new ValidatorFilter();
        final Map<String, String> options = new HashMap<>();
        options.put(SCHEMA, getSchemaJson());
        options.put(ACCUMULO_ELEMENT_CONVERTER_CLASS, ByteEntityAccumuloElementConverter.class.getName());
        filter.init(null, options, null);
        final ByteEntityAccumuloElementConverter converter = new ByteEntityAccumuloElementConverter(getSchema());
        final Element element = new Edge.Builder().group(EDGE).source("source").dest("dest").directed(true).build();
        final Pair<Key, Key> key = converter.getKeysFromElement(element);
        final Value value = converter.getValueFromElement(element);
        // When
        final boolean accept = filter.accept(key.getFirst(), value);
        // Then
        Assert.assertTrue(accept);
    }

    @Test
    public void shouldNotAcceptElementWhenSchemaValidatorDoesNotAcceptElement() throws Exception {
        // Given
        final ValidatorFilter filter = new ValidatorFilter();
        final Map<String, String> options = new HashMap<>();
        options.put(SCHEMA, getSchemaJson());
        options.put(ACCUMULO_ELEMENT_CONVERTER_CLASS, ByteEntityAccumuloElementConverter.class.getName());
        filter.init(null, options, null);
        final ByteEntityAccumuloElementConverter converter = new ByteEntityAccumuloElementConverter(getSchema());
        final Element element = new Edge.Builder().group(EDGE).source("invalid").dest("dest").directed(true).build();
        final Pair<Key, Key> key = converter.getKeysFromElement(element);
        final Value value = converter.getValueFromElement(element);
        // When
        final boolean accept = filter.accept(key.getFirst(), value);
        // Then
        Assert.assertFalse(accept);
    }
}

