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
package uk.gov.gchq.gaffer.accumulostore.test.bloom;


import AccumuloStoreConstants.BLOOM_FILTER_CHARSET;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import org.apache.accumulo.core.bloomfilter.BloomFilter;
import org.apache.hadoop.util.bloom.Key;
import org.apache.hadoop.util.hash.Hash;
import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.accumulostore.utils.AccumuloStoreConstants;


public class FilterWritabilityTest {
    @Test
    public void shouldAcceptValidFilter() {
        // Given
        final BloomFilter filter = new BloomFilter(100, 5, Hash.MURMUR_HASH);
        filter.add(new Key("ABC".getBytes()));
        filter.add(new Key("DEF".getBytes()));
        // Then
        Assert.assertTrue(filter.membershipTest(new Key("ABC".getBytes())));
        Assert.assertTrue(filter.membershipTest(new Key("DEF".getBytes())));
        Assert.assertFalse(filter.membershipTest(new Key("lkjhgfdsa".getBytes())));
    }

    @Test
    public void shouldWriteAndReadFilter() throws IOException {
        // Given
        final BloomFilter filter = new BloomFilter(100, 5, Hash.MURMUR_HASH);
        filter.add(new Key("ABC".getBytes()));
        filter.add(new Key("DEF".getBytes()));
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final DataOutputStream out = new DataOutputStream(baos);
        filter.write(out);
        String x = new String(baos.toByteArray(), AccumuloStoreConstants.BLOOM_FILTER_CHARSET);
        final ByteArrayInputStream bais = new ByteArrayInputStream(x.getBytes(BLOOM_FILTER_CHARSET));
        // When
        final DataInputStream in = new DataInputStream(bais);
        final BloomFilter read = new BloomFilter();
        read.readFields(in);
        // Then
        Assert.assertTrue(read.membershipTest(new Key("ABC".getBytes())));
        Assert.assertTrue(read.membershipTest(new Key("DEF".getBytes())));
        Assert.assertFalse(read.membershipTest(new Key("lkjhgfdsa".getBytes())));
    }
}

