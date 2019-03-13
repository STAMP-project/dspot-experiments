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
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.apache.druid.data.input.InputRow;
import org.apache.druid.data.input.MapBasedInputRow;
import org.apache.druid.java.util.common.DateTimes;
import org.apache.druid.query.aggregation.AggregatorFactory;
import org.apache.druid.query.aggregation.CountAggregatorFactory;
import org.apache.druid.segment.data.ConciseBitmapSerdeFactory;
import org.apache.druid.segment.incremental.IncrementalIndex;
import org.apache.druid.segment.writeout.SegmentWriteOutMediumFactory;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class IndexMergerV9CompatibilityTest {
    private static final long TIMESTAMP = DateTimes.of("2014-01-01").getMillis();

    private static final AggregatorFactory[] DEFAULT_AGG_FACTORIES = new AggregatorFactory[]{ new CountAggregatorFactory("count") };

    private static final IndexSpec INDEX_SPEC = IndexMergerTestBase.makeIndexSpec(new ConciseBitmapSerdeFactory(), LZ4, LZ4, LONGS);

    private static final List<String> DIMS = ImmutableList.of("dim0", "dim1");

    private final Collection<InputRow> events;

    @Rule
    public final CloserRule closer = new CloserRule(false);

    private final IndexMerger indexMerger;

    private final IndexIO indexIO;

    public IndexMergerV9CompatibilityTest(SegmentWriteOutMediumFactory segmentWriteOutMediumFactory) {
        indexMerger = TestHelper.getTestIndexMergerV9(segmentWriteOutMediumFactory);
        indexIO = TestHelper.getTestIndexIO();
        events = new ArrayList();
        final Map<String, Object> map1 = ImmutableMap.of(IndexMergerV9CompatibilityTest.DIMS.get(0), ImmutableList.of("dim00", "dim01"), IndexMergerV9CompatibilityTest.DIMS.get(1), "dim10");
        final List<String> nullList = Collections.singletonList(null);
        final Map<String, Object> map2 = ImmutableMap.of(IndexMergerV9CompatibilityTest.DIMS.get(0), nullList, IndexMergerV9CompatibilityTest.DIMS.get(1), "dim10");
        final Map<String, Object> map3 = ImmutableMap.of(IndexMergerV9CompatibilityTest.DIMS.get(0), ImmutableList.of("dim00", "dim01"));
        final Map<String, Object> map4 = ImmutableMap.of();
        final Map<String, Object> map5 = ImmutableMap.of(IndexMergerV9CompatibilityTest.DIMS.get(1), "dim10");
        final Map<String, Object> map6 = new HashMap<>();
        map6.put(IndexMergerV9CompatibilityTest.DIMS.get(1), null);// ImmutableMap cannot take null

        int i = 0;
        for (final Map<String, Object> map : Arrays.asList(map1, map2, map3, map4, map5, map6)) {
            events.add(new MapBasedInputRow(((IndexMergerV9CompatibilityTest.TIMESTAMP) + (i++)), IndexMergerV9CompatibilityTest.DIMS, map));
        }
    }

    IncrementalIndex toPersist;

    File tmpDir;

    File persistTmpDir;

    @Test
    public void testPersistWithSegmentMetadata() throws IOException {
        File outDir = Files.createTempDir();
        QueryableIndex index = null;
        try {
            outDir = Files.createTempDir();
            index = indexIO.loadIndex(indexMerger.persist(toPersist, outDir, IndexMergerV9CompatibilityTest.INDEX_SPEC, null));
            Assert.assertEquals("value", index.getMetadata().get("key"));
        } finally {
            if (index != null) {
                index.close();
            }
            if (outDir != null) {
                FileUtils.deleteDirectory(outDir);
            }
        }
    }

    @Test
    public void testSimpleReprocess() throws IOException {
        final IndexableAdapter adapter = new QueryableIndexIndexableAdapter(closer.closeLater(indexIO.loadIndex(persistTmpDir)));
        Assert.assertEquals(events.size(), adapter.getNumRows());
        reprocessAndValidate(persistTmpDir, new File(tmpDir, "reprocessed"));
    }

    @Test
    public void testIdempotentReprocess() throws IOException {
        final IndexableAdapter adapter = new QueryableIndexIndexableAdapter(closer.closeLater(indexIO.loadIndex(persistTmpDir)));
        Assert.assertEquals(events.size(), adapter.getNumRows());
        final File tmpDir1 = new File(tmpDir, "reprocessed1");
        reprocessAndValidate(persistTmpDir, tmpDir1);
        final File tmpDir2 = new File(tmpDir, "reprocessed2");
        final IndexableAdapter adapter2 = new QueryableIndexIndexableAdapter(closer.closeLater(indexIO.loadIndex(tmpDir1)));
        Assert.assertEquals(events.size(), adapter2.getNumRows());
        reprocessAndValidate(tmpDir1, tmpDir2);
        final File tmpDir3 = new File(tmpDir, "reprocessed3");
        final IndexableAdapter adapter3 = new QueryableIndexIndexableAdapter(closer.closeLater(indexIO.loadIndex(tmpDir2)));
        Assert.assertEquals(events.size(), adapter3.getNumRows());
        reprocessAndValidate(tmpDir2, tmpDir3);
    }
}

