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


import AccumuloStoreConstants.BLOOM_FILTER;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.accumulostore.key.core.impl.CoreKeyBloomFilterIterator;
import uk.gov.gchq.gaffer.accumulostore.key.exception.BloomFilterIteratorException;
import uk.gov.gchq.gaffer.operation.OperationException;


public class CoreKeyBloomFilterIteratorTest {
    @Test
    public void shouldThrowExceptionWhenValidateOptionsWithoutBloomFilter() throws IOException, OperationException {
        // Given
        final CoreKeyBloomFilterIterator filter = new CoreKeyBloomFilterIterator();
        final Map<String, String> options = new HashMap<>();
        // When / Then
        try {
            filter.validateOptions(options);
            Assert.fail("Exception expected");
        } catch (final BloomFilterIteratorException e) {
            Assert.assertTrue(e.getMessage().contains(BLOOM_FILTER));
        }
    }

    @Test
    public void shouldValidateOptionsSuccessfully() throws IOException, OperationException {
        // Given
        final CoreKeyBloomFilterIterator filter = new CoreKeyBloomFilterIterator();
        final Map<String, String> options = new HashMap<>();
        options.put(BLOOM_FILTER, "some value");
        // When
        final boolean result = filter.validateOptions(options);
        // Then
        Assert.assertTrue(result);
    }
}

