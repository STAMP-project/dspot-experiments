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
package org.apache.druid.server.coordinator;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.druid.client.ImmutableDruidServer;
import org.apache.druid.timeline.DataSegment;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;


public class ReservoirSegmentSamplerTest {
    private ImmutableDruidServer druidServer1;

    private ImmutableDruidServer druidServer2;

    private ImmutableDruidServer druidServer3;

    private ImmutableDruidServer druidServer4;

    private ServerHolder holder1;

    private ServerHolder holder2;

    private ServerHolder holder3;

    private ServerHolder holder4;

    private DataSegment segment1;

    private DataSegment segment2;

    private DataSegment segment3;

    private DataSegment segment4;

    List<DataSegment> segments1;

    List<DataSegment> segments2;

    List<DataSegment> segments3;

    List<DataSegment> segments4;

    List<DataSegment> segments;

    // checks if every segment is selected at least once out of 5000 trials
    @Test
    public void getRandomBalancerSegmentHolderTest() {
        EasyMock.expect(druidServer1.getName()).andReturn("1").atLeastOnce();
        EasyMock.expect(druidServer1.getCurrSize()).andReturn(30L).atLeastOnce();
        EasyMock.expect(druidServer1.getMaxSize()).andReturn(100L).atLeastOnce();
        EasyMock.expect(druidServer1.getSegments()).andReturn(segments1).anyTimes();
        EasyMock.expect(druidServer1.getSegment(EasyMock.anyObject())).andReturn(null).anyTimes();
        EasyMock.replay(druidServer1);
        EasyMock.expect(druidServer2.getName()).andReturn("2").atLeastOnce();
        EasyMock.expect(druidServer2.getTier()).andReturn("normal").anyTimes();
        EasyMock.expect(druidServer2.getCurrSize()).andReturn(30L).atLeastOnce();
        EasyMock.expect(druidServer2.getMaxSize()).andReturn(100L).atLeastOnce();
        EasyMock.expect(druidServer2.getSegments()).andReturn(segments2).anyTimes();
        EasyMock.expect(druidServer2.getSegment(EasyMock.anyObject())).andReturn(null).anyTimes();
        EasyMock.replay(druidServer2);
        EasyMock.expect(druidServer3.getName()).andReturn("3").atLeastOnce();
        EasyMock.expect(druidServer3.getTier()).andReturn("normal").anyTimes();
        EasyMock.expect(druidServer3.getCurrSize()).andReturn(30L).atLeastOnce();
        EasyMock.expect(druidServer3.getMaxSize()).andReturn(100L).atLeastOnce();
        EasyMock.expect(druidServer3.getSegments()).andReturn(segments3).anyTimes();
        EasyMock.expect(druidServer3.getSegment(EasyMock.anyObject())).andReturn(null).anyTimes();
        EasyMock.replay(druidServer3);
        EasyMock.expect(druidServer4.getName()).andReturn("4").atLeastOnce();
        EasyMock.expect(druidServer4.getTier()).andReturn("normal").anyTimes();
        EasyMock.expect(druidServer4.getCurrSize()).andReturn(30L).atLeastOnce();
        EasyMock.expect(druidServer4.getMaxSize()).andReturn(100L).atLeastOnce();
        EasyMock.expect(druidServer4.getSegments()).andReturn(segments4).anyTimes();
        EasyMock.expect(druidServer4.getSegment(EasyMock.anyObject())).andReturn(null).anyTimes();
        EasyMock.replay(druidServer4);
        EasyMock.expect(holder1.getServer()).andReturn(druidServer1).anyTimes();
        EasyMock.replay(holder1);
        EasyMock.expect(holder2.getServer()).andReturn(druidServer2).anyTimes();
        EasyMock.replay(holder2);
        EasyMock.expect(holder3.getServer()).andReturn(druidServer3).anyTimes();
        EasyMock.replay(holder3);
        EasyMock.expect(holder4.getServer()).andReturn(druidServer4).anyTimes();
        EasyMock.replay(holder4);
        List<ServerHolder> holderList = new ArrayList<>();
        holderList.add(holder1);
        holderList.add(holder2);
        holderList.add(holder3);
        holderList.add(holder4);
        Map<DataSegment, Integer> segmentCountMap = new HashMap<>();
        for (int i = 0; i < 5000; i++) {
            segmentCountMap.put(ReservoirSegmentSampler.getRandomBalancerSegmentHolder(holderList).getSegment(), 1);
        }
        for (DataSegment segment : segments) {
            Assert.assertEquals(segmentCountMap.get(segment), new Integer(1));
        }
    }
}

