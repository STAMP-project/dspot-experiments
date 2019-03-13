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
package org.apache.druid.server;


import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.BiFunction;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import org.apache.commons.io.FileUtils;
import org.apache.druid.java.util.common.FileUtils.FileCopyResult;
import org.apache.druid.java.util.common.StringUtils;
import org.apache.druid.segment.IndexIO;
import org.apache.druid.segment.QueryableIndex;
import org.apache.druid.segment.Segment;
import org.apache.druid.segment.StorageAdapter;
import org.apache.druid.segment.loading.LocalDataSegmentPuller;
import org.apache.druid.segment.loading.SegmentLoaderLocalCacheManager;
import org.apache.druid.segment.loading.SegmentLoadingException;
import org.apache.druid.segment.loading.SegmentizerFactory;
import org.apache.druid.timeline.DataSegment;
import org.apache.druid.timeline.SegmentId;
import org.joda.time.Interval;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class SegmentManagerThreadSafetyTest {
    private static final int NUM_THREAD = 4;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private SegmentManagerThreadSafetyTest.TestSegmentPuller segmentPuller;

    private ObjectMapper objectMapper;

    private IndexIO indexIO;

    private File segmentCacheDir;

    private File segmentDeepStorageDir;

    private SegmentLoaderLocalCacheManager segmentLoader;

    private SegmentManager segmentManager;

    private ExecutorService exec;

    @Test(timeout = 5000L)
    public void testLoadSameSegment() throws IOException, InterruptedException, ExecutionException {
        final DataSegment segment = createSegment("2019-01-01/2019-01-02");
        final List<Future> futures = IntStream.range(0, 16).mapToObj(( i) -> exec.submit(() -> segmentManager.loadSegment(segment))).collect(Collectors.toList());
        for (Future future : futures) {
            future.get();
        }
        Assert.assertEquals(1, segmentPuller.numFileLoaded.size());
        Assert.assertEquals(1, segmentPuller.numFileLoaded.values().iterator().next().intValue());
        Assert.assertEquals(0, segmentLoader.getSegmentLocks().size());
    }

    @Test(timeout = 5000L)
    public void testLoadMultipleSegments() throws IOException, InterruptedException, ExecutionException {
        final List<DataSegment> segments = new ArrayList<>(88);
        for (int i = 0; i < 11; i++) {
            for (int j = 0; j < 8; j++) {
                segments.add(createSegment(StringUtils.format("2019-%02d-01/2019-%02d-01", (i + 1), (i + 2))));
            }
        }
        final List<Future> futures = IntStream.range(0, 16).mapToObj(( i) -> exec.submit(() -> {
            for (DataSegment segment : segments) {
                try {
                    segmentManager.loadSegment(segment);
                } catch (SegmentLoadingException e) {
                    throw new RuntimeException(e);
                }
            }
        })).collect(Collectors.toList());
        for (Future future : futures) {
            future.get();
        }
        Assert.assertEquals(11, segmentPuller.numFileLoaded.size());
        Assert.assertEquals(1, segmentPuller.numFileLoaded.values().iterator().next().intValue());
        Assert.assertEquals(0, segmentLoader.getSegmentLocks().size());
    }

    private static class TestSegmentPuller extends LocalDataSegmentPuller {
        private final Map<File, Integer> numFileLoaded = new HashMap<>();

        @Override
        public FileCopyResult getSegmentFiles(final File sourceFile, final File dir) {
            numFileLoaded.compute(sourceFile, ( f, numLoaded) -> numLoaded == null ? 1 : numLoaded + 1);
            try {
                FileUtils.copyDirectory(sourceFile, dir);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return new FileCopyResult() {
                @Override
                public long size() {
                    return 100L;
                }
            };
        }
    }

    private static class TestSegmentizerFactory implements SegmentizerFactory {
        @Override
        public Segment factorize(DataSegment segment, File parentDir) {
            return new Segment() {
                @Override
                public SegmentId getId() {
                    return segment.getId();
                }

                @Override
                public Interval getDataInterval() {
                    return segment.getInterval();
                }

                @Nullable
                @Override
                public QueryableIndex asQueryableIndex() {
                    throw new UnsupportedOperationException();
                }

                @Override
                public StorageAdapter asStorageAdapter() {
                    throw new UnsupportedOperationException();
                }

                @Override
                public <T> T as(Class<T> clazz) {
                    throw new UnsupportedOperationException();
                }

                @Override
                public void close() {
                }
            };
        }
    }
}

