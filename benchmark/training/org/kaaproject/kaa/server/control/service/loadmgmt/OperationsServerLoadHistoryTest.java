/**
 * Copyright 2014-2016 CyberVision, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kaaproject.kaa.server.control.service.loadmgmt;


import java.util.Random;
import org.junit.Assert;
import org.junit.Test;
import org.kaaproject.kaa.server.common.zk.gen.LoadInfo;
import org.kaaproject.kaa.server.control.service.loadmgmt.dynamicmgmt.OperationsServerLoadHistory;


/**
 *
 *
 * @author Andrey Panasenko
 */
public class OperationsServerLoadHistoryTest {
    private static long MAX_HISTORY_TIME_LIVE = 3000;

    private static Random rnd = new Random();

    /**
     * Test method for {@link org.kaaproject.kaa.server.control.service.loadmgmt.dynamicmgmt.OperationsServerLoadHistory#OperationsServerLoadHistory(long)}.
     */
    @Test
    public void testOperationsServerLoadHistory() {
        OperationsServerLoadHistory hist = new OperationsServerLoadHistory(OperationsServerLoadHistoryTest.MAX_HISTORY_TIME_LIVE);
        Assert.assertNotNull(hist);
    }

    /**
     * Test method for {@link org.kaaproject.kaa.server.control.service.loadmgmt.dynamicmgmt.OperationsServerLoadHistory#addOpsServerLoad(LoadInfo)}.
     */
    @Test
    public void testAddOpsServerLoad() {
        OperationsServerLoadHistory hist = new OperationsServerLoadHistory(OperationsServerLoadHistoryTest.MAX_HISTORY_TIME_LIVE);
        hist.addOpsServerLoad(new LoadInfo(2, 1.0));
        fillOutHistory(hist, 1000, 5);
        Assert.assertNotNull(hist.getHistory());
        if ((hist.getHistory().size()) >= 5) {
            Assert.fail(("testAddOpsServerLoad, removeOld history failed, size should be no more than 4, but " + (hist.getHistory().size())));
        }
    }

    /**
     * Test method for {@link org.kaaproject.kaa.server.control.service.loadmgmt.dynamicmgmt.OperationsServerLoadHistory#getHistory()}.
     */
    @Test
    public void testGetHistory() {
        OperationsServerLoadHistory hist = new OperationsServerLoadHistory(OperationsServerLoadHistoryTest.MAX_HISTORY_TIME_LIVE);
        hist.addOpsServerLoad(new LoadInfo(2, 1.0));
        Assert.assertNotNull(hist.getHistory());
        Assert.assertEquals(1, hist.getHistory().size());
        Assert.assertEquals(2, hist.getHistory().get(0).getLoadInfo().getEndpointCount().intValue());
    }

    /**
     * Test method for {@link org.kaaproject.kaa.server.control.service.loadmgmt.dynamicmgmt.OperationsServerLoadHistory#getMaxHistoryTimeLive()}.
     */
    @Test
    public void testGetMaxHistoryTimeLive() {
        OperationsServerLoadHistory hist = new OperationsServerLoadHistory(OperationsServerLoadHistoryTest.MAX_HISTORY_TIME_LIVE);
        Assert.assertNotNull(hist);
        Assert.assertEquals(OperationsServerLoadHistoryTest.MAX_HISTORY_TIME_LIVE, hist.getMaxHistoryTimeLive());
    }
}

