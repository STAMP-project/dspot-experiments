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
package org.apache.hadoop.hdfs.server.federation.resolver;


import java.io.IOException;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import org.apache.hadoop.hdfs.server.federation.resolver.order.HashResolver;
import org.apache.hadoop.hdfs.server.federation.store.records.MountTable;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test the multiple destination resolver.
 */
public class TestMultipleDestinationResolver {
    private MultipleDestinationMountTableResolver resolver;

    @Test
    public void testHashEqualDistribution() throws IOException {
        // First level
        testEvenDistribution("/hash");
        testEvenDistribution("/hash/folder0", false);
        // All levels
        testEvenDistribution("/hashall");
        testEvenDistribution("/hashall/folder0");
    }

    @Test
    public void testHashAll() throws IOException {
        // Files should be spread across subclusters
        PathLocation dest0 = resolver.getDestinationForPath("/hashall/file0.txt");
        TestMultipleDestinationResolver.assertDest("subcluster0", dest0);
        PathLocation dest1 = resolver.getDestinationForPath("/hashall/file1.txt");
        TestMultipleDestinationResolver.assertDest("subcluster1", dest1);
        // Files within folder should be spread across subclusters
        PathLocation dest2 = resolver.getDestinationForPath("/hashall/folder0");
        TestMultipleDestinationResolver.assertDest("subcluster2", dest2);
        PathLocation dest3 = resolver.getDestinationForPath("/hashall/folder0/file0.txt");
        TestMultipleDestinationResolver.assertDest("subcluster1", dest3);
        PathLocation dest4 = resolver.getDestinationForPath("/hashall/folder0/file1.txt");
        TestMultipleDestinationResolver.assertDest("subcluster0", dest4);
        PathLocation dest5 = resolver.getDestinationForPath("/hashall/folder0/folder0/file0.txt");
        TestMultipleDestinationResolver.assertDest("subcluster1", dest5);
        PathLocation dest6 = resolver.getDestinationForPath("/hashall/folder0/folder0/file1.txt");
        TestMultipleDestinationResolver.assertDest("subcluster1", dest6);
        PathLocation dest7 = resolver.getDestinationForPath("/hashall/folder0/folder0/file2.txt");
        TestMultipleDestinationResolver.assertDest("subcluster0", dest7);
        PathLocation dest8 = resolver.getDestinationForPath("/hashall/folder1");
        TestMultipleDestinationResolver.assertDest("subcluster1", dest8);
        PathLocation dest9 = resolver.getDestinationForPath("/hashall/folder1/file0.txt");
        TestMultipleDestinationResolver.assertDest("subcluster0", dest9);
        PathLocation dest10 = resolver.getDestinationForPath("/hashall/folder1/file1.txt");
        TestMultipleDestinationResolver.assertDest("subcluster1", dest10);
        PathLocation dest11 = resolver.getDestinationForPath("/hashall/folder2");
        TestMultipleDestinationResolver.assertDest("subcluster2", dest11);
        PathLocation dest12 = resolver.getDestinationForPath("/hashall/folder2/file0.txt");
        TestMultipleDestinationResolver.assertDest("subcluster0", dest12);
        PathLocation dest13 = resolver.getDestinationForPath("/hashall/folder2/file1.txt");
        TestMultipleDestinationResolver.assertDest("subcluster0", dest13);
        PathLocation dest14 = resolver.getDestinationForPath("/hashall/folder2/file2.txt");
        TestMultipleDestinationResolver.assertDest("subcluster1", dest14);
    }

    @Test
    public void testHashFirst() throws IOException {
        PathLocation dest0 = resolver.getDestinationForPath("/hashall/file0.txt");
        TestMultipleDestinationResolver.assertDest("subcluster0", dest0);
        PathLocation dest1 = resolver.getDestinationForPath("/hashall/file1.txt");
        TestMultipleDestinationResolver.assertDest("subcluster1", dest1);
        // All these must be in the same location: subcluster0
        PathLocation dest2 = resolver.getDestinationForPath("/hash/folder0");
        TestMultipleDestinationResolver.assertDest("subcluster0", dest2);
        PathLocation dest3 = resolver.getDestinationForPath("/hash/folder0/file0.txt");
        TestMultipleDestinationResolver.assertDest("subcluster0", dest3);
        PathLocation dest4 = resolver.getDestinationForPath("/hash/folder0/file1.txt");
        TestMultipleDestinationResolver.assertDest("subcluster0", dest4);
        PathLocation dest5 = resolver.getDestinationForPath("/hash/folder0/folder0/file0.txt");
        TestMultipleDestinationResolver.assertDest("subcluster0", dest5);
        PathLocation dest6 = resolver.getDestinationForPath("/hash/folder0/folder0/file1.txt");
        TestMultipleDestinationResolver.assertDest("subcluster0", dest6);
        // All these must be in the same location: subcluster2
        PathLocation dest7 = resolver.getDestinationForPath("/hash/folder1");
        TestMultipleDestinationResolver.assertDest("subcluster2", dest7);
        PathLocation dest8 = resolver.getDestinationForPath("/hash/folder1/file0.txt");
        TestMultipleDestinationResolver.assertDest("subcluster2", dest8);
        PathLocation dest9 = resolver.getDestinationForPath("/hash/folder1/file1.txt");
        TestMultipleDestinationResolver.assertDest("subcluster2", dest9);
        // All these must be in the same location: subcluster2
        PathLocation dest10 = resolver.getDestinationForPath("/hash/folder2");
        TestMultipleDestinationResolver.assertDest("subcluster2", dest10);
        PathLocation dest11 = resolver.getDestinationForPath("/hash/folder2/file0.txt");
        TestMultipleDestinationResolver.assertDest("subcluster2", dest11);
        PathLocation dest12 = resolver.getDestinationForPath("/hash/folder2/file1.txt");
        TestMultipleDestinationResolver.assertDest("subcluster2", dest12);
    }

    @Test
    public void testRandomEqualDistribution() throws IOException {
        testEvenDistribution("/random");
    }

    @Test
    public void testSingleDestination() throws IOException {
        // All the files in /tmp should be in subcluster0
        for (int f = 0; f < 100; f++) {
            String filename = ("/tmp/b/c/file" + f) + ".txt";
            PathLocation destination = resolver.getDestinationForPath(filename);
            RemoteLocation loc = destination.getDefaultLocation();
            Assert.assertEquals("subcluster0", loc.getNameserviceId());
            Assert.assertEquals(filename, loc.getDest());
        }
    }

    @Test
    public void testResolveSubdirectories() throws Exception {
        // Simulate a testdir under a multi-destination mount.
        Random r = new Random();
        String testDir = "/sort/testdir" + (r.nextInt());
        String file1 = (testDir + "/file1") + (r.nextInt());
        String file2 = (testDir + "/file2") + (r.nextInt());
        // Verify both files resolve to the same namespace as the parent dir.
        PathLocation testDirLocation = resolver.getDestinationForPath(testDir);
        RemoteLocation defaultLoc = testDirLocation.getDefaultLocation();
        String testDirNamespace = defaultLoc.getNameserviceId();
        PathLocation file1Location = resolver.getDestinationForPath(file1);
        RemoteLocation defaultLoc1 = file1Location.getDefaultLocation();
        Assert.assertEquals(testDirNamespace, defaultLoc1.getNameserviceId());
        PathLocation file2Location = resolver.getDestinationForPath(file2);
        RemoteLocation defaultLoc2 = file2Location.getDefaultLocation();
        Assert.assertEquals(testDirNamespace, defaultLoc2.getNameserviceId());
    }

    @Test
    public void testExtractTempFileName() {
        for (String teststring : new String[]{ "testfile1.txt.COPYING", "testfile1.txt._COPYING_", "testfile1.txt._COPYING_.attempt_1486662804109_0055_m_000042_0", "testfile1.txt.tmp", "_temp/testfile1.txt", "_temporary/testfile1.txt.af77e2ab-4bc5-4959-ae08-299c880ee6b8", "_temporary/0/_temporary/attempt_201706281636_0007_m_000003_46/" + "testfile1.txt" }) {
            String finalName = HashResolver.extractTempFileName(teststring);
            Assert.assertEquals("testfile1.txt", finalName);
        }
        // False cases
        Assert.assertEquals("file1.txt.COPYING1", HashResolver.extractTempFileName("file1.txt.COPYING1"));
        Assert.assertEquals("file1.txt.tmp2", HashResolver.extractTempFileName("file1.txt.tmp2"));
        // Speculation patterns
        String finalName = HashResolver.extractTempFileName("_temporary/part-00007.af77e2ab-4bc5-4959-ae08-299c880ee6b8");
        Assert.assertEquals("part-00007", finalName);
        finalName = HashResolver.extractTempFileName(("_temporary/0/_temporary/attempt_201706281636_0007_m_000003_46/" + "part-00003"));
        Assert.assertEquals("part-00003", finalName);
        // Subfolders
        finalName = HashResolver.extractTempFileName("folder0/testfile1.txt._COPYING_");
        Assert.assertEquals("folder0/testfile1.txt", finalName);
        finalName = HashResolver.extractTempFileName("folder0/folder1/testfile1.txt._COPYING_");
        Assert.assertEquals("folder0/folder1/testfile1.txt", finalName);
        finalName = HashResolver.extractTempFileName(("processedHrsData.txt/_temporary/0/_temporary/" + "attempt_201706281636_0007_m_000003_46/part-00003"));
        Assert.assertEquals("processedHrsData.txt/part-00003", finalName);
    }

    @Test
    public void testReadOnly() throws IOException {
        MountTable mount = resolver.getMountPoint("/readonly");
        Assert.assertTrue(mount.isReadOnly());
        PathLocation dest0 = resolver.getDestinationForPath("/readonly/file0.txt");
        TestMultipleDestinationResolver.assertDest("subcluster1", dest0);
        PathLocation dest1 = resolver.getDestinationForPath("/readonly/file1.txt");
        TestMultipleDestinationResolver.assertDest("subcluster2", dest1);
        // All these must be in the same location: subcluster0
        PathLocation dest2 = resolver.getDestinationForPath("/readonly/folder0");
        TestMultipleDestinationResolver.assertDest("subcluster1", dest2);
        PathLocation dest3 = resolver.getDestinationForPath("/readonly/folder0/file0.txt");
        TestMultipleDestinationResolver.assertDest("subcluster1", dest3);
        PathLocation dest4 = resolver.getDestinationForPath("/readonly/folder0/file1.txt");
        TestMultipleDestinationResolver.assertDest("subcluster1", dest4);
        PathLocation dest5 = resolver.getDestinationForPath("/readonly/folder0/folder0/file0.txt");
        TestMultipleDestinationResolver.assertDest("subcluster1", dest5);
        PathLocation dest6 = resolver.getDestinationForPath("/readonly/folder0/folder0/file1.txt");
        TestMultipleDestinationResolver.assertDest("subcluster1", dest6);
        // All these must be in the same location: subcluster2
        PathLocation dest7 = resolver.getDestinationForPath("/readonly/folder1");
        TestMultipleDestinationResolver.assertDest("subcluster2", dest7);
        PathLocation dest8 = resolver.getDestinationForPath("/readonly/folder1/file0.txt");
        TestMultipleDestinationResolver.assertDest("subcluster2", dest8);
        PathLocation dest9 = resolver.getDestinationForPath("/readonly/folder1/file1.txt");
        TestMultipleDestinationResolver.assertDest("subcluster2", dest9);
        // All these must be in the same location: subcluster2
        PathLocation dest10 = resolver.getDestinationForPath("/readonly/folder2");
        TestMultipleDestinationResolver.assertDest("subcluster1", dest10);
        PathLocation dest11 = resolver.getDestinationForPath("/readonly/folder2/file0.txt");
        TestMultipleDestinationResolver.assertDest("subcluster1", dest11);
        PathLocation dest12 = resolver.getDestinationForPath("/readonly/folder2/file1.txt");
        TestMultipleDestinationResolver.assertDest("subcluster1", dest12);
    }

    @Test
    public void testLocalResolver() throws IOException {
        PathLocation dest0 = resolver.getDestinationForPath("/local/folder0/file0.txt");
        TestMultipleDestinationResolver.assertDest("subcluster0", dest0);
    }

    @Test
    public void testRandomResolver() throws IOException {
        Set<String> destinations = new HashSet<>();
        for (int i = 0; i < 30; i++) {
            PathLocation dest = resolver.getDestinationForPath("/random/folder0/file0.txt");
            RemoteLocation firstDest = dest.getDestinations().get(0);
            String nsId = firstDest.getNameserviceId();
            destinations.add(nsId);
        }
        Assert.assertEquals(3, destinations.size());
    }
}

