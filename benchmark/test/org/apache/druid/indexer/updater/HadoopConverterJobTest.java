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
package org.apache.druid.indexer.updater;


import CompressionFactory.LongEncodingStrategy;
import TestDerbyConnector.DerbyConnectorRule;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.apache.druid.java.util.common.Intervals;
import org.apache.druid.metadata.MetadataSegmentManagerConfig;
import org.apache.druid.metadata.MetadataStorageTablesConfig;
import org.apache.druid.metadata.SQLMetadataSegmentManager;
import org.apache.druid.metadata.TestDerbyConnector;
import org.apache.druid.metadata.storage.derby.DerbyConnector;
import org.apache.druid.segment.data.CompressionStrategy;
import org.apache.druid.segment.data.RoaringBitmapSerdeFactory;
import org.apache.druid.timeline.DataSegment;
import org.joda.time.Interval;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static HadoopDruidConverterConfig.jsonMapper;


public class HadoopConverterJobTest {
    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Rule
    public final DerbyConnectorRule derbyConnectorRule = new TestDerbyConnector.DerbyConnectorRule();

    private String storageLocProperty = null;

    private File tmpSegmentDir = null;

    private static final String DATASOURCE = "testDatasource";

    private static final String STORAGE_PROPERTY_KEY = "druid.storage.storageDirectory";

    private Supplier<MetadataStorageTablesConfig> metadataStorageTablesConfigSupplier;

    private DerbyConnector connector;

    private final Interval interval = Intervals.of("2011-01-01T00:00:00.000Z/2011-05-01T00:00:00.000Z");

    @Test
    public void testSimpleJob() throws IOException, InterruptedException {
        final SQLMetadataSegmentManager manager = new SQLMetadataSegmentManager(jsonMapper, new Supplier<MetadataSegmentManagerConfig>() {
            @Override
            public MetadataSegmentManagerConfig get() {
                return new MetadataSegmentManagerConfig();
            }
        }, metadataStorageTablesConfigSupplier, connector);
        final List<DataSegment> oldSemgments = getDataSegments(manager);
        final File tmpDir = temporaryFolder.newFolder();
        final HadoopConverterJob converterJob = new HadoopConverterJob(new HadoopDruidConverterConfig(HadoopConverterJobTest.DATASOURCE, interval, new org.apache.druid.segment.IndexSpec(new RoaringBitmapSerdeFactory(null), CompressionStrategy.UNCOMPRESSED, CompressionStrategy.UNCOMPRESSED, LongEncodingStrategy.LONGS), oldSemgments, true, tmpDir.toURI(), ImmutableMap.of(), null, tmpSegmentDir.toURI().toString()));
        final List<DataSegment> segments = Lists.newArrayList(converterJob.run());
        Assert.assertNotNull("bad result", segments);
        Assert.assertEquals("wrong segment count", 4, segments.size());
        Assert.assertTrue(((converterJob.getLoadedBytes()) > 0));
        Assert.assertTrue(((converterJob.getWrittenBytes()) > 0));
        Assert.assertTrue(((converterJob.getWrittenBytes()) > (converterJob.getLoadedBytes())));
        Assert.assertEquals(oldSemgments.size(), segments.size());
        final DataSegment segment = segments.get(0);
        Assert.assertTrue(interval.contains(segment.getInterval()));
        Assert.assertTrue(segment.getVersion().endsWith("_converted"));
        Assert.assertTrue(segment.getLoadSpec().get("path").toString().contains("_converted"));
        for (File file : tmpDir.listFiles()) {
            Assert.assertFalse(file.isDirectory());
            Assert.assertTrue(file.isFile());
        }
        final Comparator<DataSegment> segmentComparator = new Comparator<DataSegment>() {
            @Override
            public int compare(DataSegment o1, DataSegment o2) {
                return o1.getId().compareTo(o2.getId());
            }
        };
        Collections.sort(oldSemgments, segmentComparator);
        Collections.sort(segments, segmentComparator);
        for (int i = 0; i < (oldSemgments.size()); ++i) {
            final DataSegment oldSegment = oldSemgments.get(i);
            final DataSegment newSegment = segments.get(i);
            Assert.assertEquals(oldSegment.getDataSource(), newSegment.getDataSource());
            Assert.assertEquals(oldSegment.getInterval(), newSegment.getInterval());
            Assert.assertEquals(Sets.newHashSet(oldSegment.getMetrics()), Sets.newHashSet(newSegment.getMetrics()));
            Assert.assertEquals(Sets.newHashSet(oldSegment.getDimensions()), Sets.newHashSet(newSegment.getDimensions()));
            Assert.assertEquals(((oldSegment.getVersion()) + "_converted"), newSegment.getVersion());
            Assert.assertTrue(((oldSegment.getSize()) < (newSegment.getSize())));
            Assert.assertEquals(oldSegment.getBinaryVersion(), newSegment.getBinaryVersion());
        }
    }
}

