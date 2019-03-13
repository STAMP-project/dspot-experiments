/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.fs.s3a.s3guard;


import S3Guard.ITtlTimeProvider;
import java.util.Arrays;
import java.util.List;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;
import org.junit.Assert;
import org.junit.Test;

import static org.apache.hadoop.fs.s3a.Constants.DEFAULT_METADATASTORE_AUTHORITATIVE_DIR_TTL;


/**
 * Tests for the {@link S3Guard} utility class.
 */
public class TestS3Guard extends Assert {
    /**
     * Basic test to ensure results from S3 and MetadataStore are merged
     * correctly.
     */
    @Test
    public void testDirListingUnion() throws Exception {
        MetadataStore ms = new LocalMetadataStore();
        Path dirPath = new Path("s3a://bucket/dir");
        // Two files in metadata store listing
        PathMetadata m1 = makePathMeta("s3a://bucket/dir/ms-file1", false);
        PathMetadata m2 = makePathMeta("s3a://bucket/dir/ms-file2", false);
        DirListingMetadata dirMeta = new DirListingMetadata(dirPath, Arrays.asList(m1, m2), false);
        // Two other files in s3
        List<FileStatus> s3Listing = Arrays.asList(makeFileStatus("s3a://bucket/dir/s3-file3", false), makeFileStatus("s3a://bucket/dir/s3-file4", false));
        S3Guard.ITtlTimeProvider timeProvider = new S3Guard.TtlTimeProvider(DEFAULT_METADATASTORE_AUTHORITATIVE_DIR_TTL);
        FileStatus[] result = S3Guard.dirListingUnion(ms, dirPath, s3Listing, dirMeta, false, timeProvider);
        Assert.assertEquals("listing length", 4, result.length);
        assertContainsPath(result, "s3a://bucket/dir/ms-file1");
        assertContainsPath(result, "s3a://bucket/dir/ms-file2");
        assertContainsPath(result, "s3a://bucket/dir/s3-file3");
        assertContainsPath(result, "s3a://bucket/dir/s3-file4");
    }
}

