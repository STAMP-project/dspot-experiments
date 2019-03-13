/**
 * Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.hadoop.fs.swift;


import org.apache.hadoop.fs.BlockLocation;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.swift.util.SwiftTestUtils;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test block location logic.
 * The endpoint may or may not be location-aware
 */
public class TestSwiftFileSystemBlockLocation extends SwiftFileSystemBaseTest {
    @Test(timeout = SwiftTestConstants.SWIFT_TEST_TIMEOUT)
    public void testLocateSingleFileBlocks() throws Throwable {
        describe("verify that a file returns 1+ blocks");
        FileStatus fileStatus = createFileAndGetStatus();
        BlockLocation[] locations = getFs().getFileBlockLocations(fileStatus, 0, 1);
        assertNotEqual(("No block locations supplied for " + fileStatus), 0, locations.length);
        for (BlockLocation location : locations) {
            assertLocationValid(location);
        }
    }

    @Test(timeout = SwiftTestConstants.SWIFT_TEST_TIMEOUT)
    public void testLocateNullStatus() throws Throwable {
        describe("verify that a null filestatus maps to a null location array");
        BlockLocation[] locations = getFs().getFileBlockLocations(((FileStatus) (null)), 0, 1);
        Assert.assertNull(locations);
    }

    @Test(timeout = SwiftTestConstants.SWIFT_TEST_TIMEOUT)
    public void testLocateNegativeSeek() throws Throwable {
        describe("verify that a negative offset is illegal");
        try {
            BlockLocation[] locations = getFs().getFileBlockLocations(createFileAndGetStatus(), (-1), 1);
            Assert.fail((("Expected an exception, got " + (locations.length)) + " locations"));
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    @Test(timeout = SwiftTestConstants.SWIFT_TEST_TIMEOUT)
    public void testLocateNegativeLen() throws Throwable {
        describe("verify that a negative length is illegal");
        try {
            BlockLocation[] locations = getFs().getFileBlockLocations(createFileAndGetStatus(), 0, (-1));
            Assert.fail((("Expected an exception, got " + (locations.length)) + " locations"));
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    @Test(timeout = SwiftTestConstants.SWIFT_TEST_TIMEOUT)
    public void testLocateOutOfRangeLen() throws Throwable {
        describe(("overshooting the length is legal, as long as the" + " origin location is valid"));
        BlockLocation[] locations = getFs().getFileBlockLocations(createFileAndGetStatus(), 0, ((data.length) + 100));
        Assert.assertNotNull(locations);
        Assert.assertTrue(((locations.length) > 0));
    }

    @Test(timeout = SwiftTestConstants.SWIFT_TEST_TIMEOUT)
    public void testLocateOutOfRangeSrc() throws Throwable {
        describe("Seeking out of the file length returns an empty array");
        BlockLocation[] locations = getFs().getFileBlockLocations(createFileAndGetStatus(), ((data.length) + 100), 1);
        assertEmptyBlockLocations(locations);
    }

    @Test(timeout = SwiftTestConstants.SWIFT_TEST_TIMEOUT)
    public void testLocateDirectory() throws Throwable {
        describe("verify that locating a directory is an error");
        createFile(path("/test/filename"));
        FileStatus status = fs.getFileStatus(path("/test"));
        SwiftFileSystemBaseTest.LOG.info(((("Filesystem is " + (fs)) + "; target is ") + status));
        SwiftTestUtils.assertIsDirectory(status);
        BlockLocation[] locations;
        locations = getFs().getFileBlockLocations(status, 0, 1);
        assertEmptyBlockLocations(locations);
    }

    @Test(timeout = SwiftTestConstants.SWIFT_TEST_TIMEOUT)
    public void testLocateRootDirectory() throws Throwable {
        describe("verify that locating the root directory is an error");
        FileStatus status = fs.getFileStatus(path("/"));
        SwiftTestUtils.assertIsDirectory(status);
        BlockLocation[] locations;
        locations = getFs().getFileBlockLocations(status, 0, 1);
        assertEmptyBlockLocations(locations);
    }
}

