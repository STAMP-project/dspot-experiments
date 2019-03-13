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
package org.apache.hadoop.tools;


import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.security.Credentials;
import org.junit.Test;


public class TestGlobbedCopyListing {
    private static MiniDFSCluster cluster;

    private static final Credentials CREDENTIALS = new Credentials();

    public static Map<String, String> expectedValues = new HashMap<String, String>();

    @Test
    public void testRun() throws Exception {
        final URI uri = TestGlobbedCopyListing.cluster.getFileSystem().getUri();
        final String pathString = uri.toString();
        Path fileSystemPath = new Path(pathString);
        Path source = new Path(((fileSystemPath.toString()) + "/tmp/source"));
        Path target = new Path(((fileSystemPath.toString()) + "/tmp/target"));
        Path listingPath = new Path(((fileSystemPath.toString()) + "/tmp/META/fileList.seq"));
        DistCpOptions options = build();
        DistCpContext context = new DistCpContext(options);
        context.setTargetPathExists(false);
        new GlobbedCopyListing(new Configuration(), TestGlobbedCopyListing.CREDENTIALS).buildListing(listingPath, context);
        verifyContents(listingPath);
    }
}

