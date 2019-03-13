/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.druid.segment;


import CompressionFactory.LongEncodingStrategy.LONGS;
import CompressionStrategy.LZ4;
import java.io.File;
import org.apache.druid.segment.data.BitmapValues;
import org.apache.druid.segment.data.CloseableIndexed;
import org.apache.druid.segment.data.ConciseBitmapSerdeFactory;
import org.apache.druid.segment.data.IncrementalIndexTest;
import org.apache.druid.segment.incremental.IncrementalIndex;
import org.apache.druid.segment.writeout.SegmentWriteOutMediumFactory;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class QueryableIndexIndexableAdapterTest {
    private static final IndexSpec INDEX_SPEC = IndexMergerTestBase.makeIndexSpec(new ConciseBitmapSerdeFactory(), LZ4, LZ4, LONGS);

    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Rule
    public final CloserRule closer = new CloserRule(false);

    private final IndexMerger indexMerger;

    private final IndexIO indexIO;

    public QueryableIndexIndexableAdapterTest(SegmentWriteOutMediumFactory segmentWriteOutMediumFactory) {
        indexMerger = TestHelper.getTestIndexMergerV9(segmentWriteOutMediumFactory);
        indexIO = TestHelper.getTestIndexIO();
    }

    @Test
    public void testGetBitmapIndex() throws Exception {
        final long timestamp = System.currentTimeMillis();
        IncrementalIndex toPersist = IncrementalIndexTest.createIndex(null);
        IncrementalIndexTest.populateIndex(timestamp, toPersist);
        final File tempDir = temporaryFolder.newFolder();
        QueryableIndex index = closer.closeLater(indexIO.loadIndex(indexMerger.persist(toPersist, tempDir, QueryableIndexIndexableAdapterTest.INDEX_SPEC, null)));
        IndexableAdapter adapter = new QueryableIndexIndexableAdapter(index);
        String dimension = "dim1";
        // null is added to all dimensions with value
        @SuppressWarnings("UnusedAssignment")
        BitmapValues bitmapValues = adapter.getBitmapValues(dimension, 0);
        try (CloseableIndexed<String> dimValueLookup = adapter.getDimValueLookup(dimension)) {
            for (int i = 0; i < (dimValueLookup.size()); i++) {
                bitmapValues = adapter.getBitmapValues(dimension, i);
                Assert.assertEquals(1, bitmapValues.size());
            }
        }
    }
}

