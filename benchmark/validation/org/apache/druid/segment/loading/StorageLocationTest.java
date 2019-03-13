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
package org.apache.druid.segment.loading;


import java.io.File;
import org.apache.druid.timeline.DataSegment;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 */
public class StorageLocationTest {
    @Test
    public void testStorageLocationFreePercent() {
        // free space ignored only maxSize matters
        StorageLocation locationPlain = fakeLocation(100000, 5000, 10000, null);
        Assert.assertTrue(locationPlain.canHandle(makeSegment("2012/2013", 9000)));
        Assert.assertFalse(locationPlain.canHandle(makeSegment("2012/2013", 11000)));
        // enough space available maxSize is the limit
        StorageLocation locationFree = fakeLocation(100000, 25000, 10000, 10.0);
        Assert.assertTrue(locationFree.canHandle(makeSegment("2012/2013", 9000)));
        Assert.assertFalse(locationFree.canHandle(makeSegment("2012/2013", 11000)));
        // disk almost full percentage is the limit
        StorageLocation locationFull = fakeLocation(100000, 15000, 10000, 10.0);
        Assert.assertTrue(locationFull.canHandle(makeSegment("2012/2013", 4000)));
        Assert.assertFalse(locationFull.canHandle(makeSegment("2012/2013", 6000)));
    }

    @Test
    public void testStorageLocation() {
        long expectedAvail = 1000L;
        StorageLocation loc = new StorageLocation(new File("/tmp"), expectedAvail, null);
        verifyLoc(expectedAvail, loc);
        final DataSegment secondSegment = makeSegment("2012-01-02/2012-01-03", 23);
        loc.addSegment(makeSegment("2012-01-01/2012-01-02", 10));
        expectedAvail -= 10;
        verifyLoc(expectedAvail, loc);
        loc.addSegment(makeSegment("2012-01-01/2012-01-02", 10));
        verifyLoc(expectedAvail, loc);
        loc.addSegment(secondSegment);
        expectedAvail -= 23;
        verifyLoc(expectedAvail, loc);
        loc.removeSegment(makeSegment("2012-01-01/2012-01-02", 10));
        expectedAvail += 10;
        verifyLoc(expectedAvail, loc);
        loc.removeSegment(makeSegment("2012-01-01/2012-01-02", 10));
        verifyLoc(expectedAvail, loc);
        loc.removeSegment(secondSegment);
        expectedAvail += 23;
        verifyLoc(expectedAvail, loc);
    }
}

