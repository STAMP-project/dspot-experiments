/**
 * Copyright 2017 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.navercorp.pinpoint.common.server.bo.stat.join;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author minwoo.jung
 */
public class JoinApplicationStatBoTest {
    @Test
    public void joinApplicationStatBoByTimeSliceTest() {
        final long currentTime = 1487149800000L;// 18:10:00 15 2 2017

        List<JoinApplicationStatBo> joinApplicationStatBoList = new ArrayList<JoinApplicationStatBo>();
        joinApplicationStatBoList.add(createJoinApplicationStatBo("id1", currentTime, 1));
        joinApplicationStatBoList.add(createJoinApplicationStatBo("id2", (currentTime + 1000), (-4)));
        joinApplicationStatBoList.add(createJoinApplicationStatBo("id3", (currentTime + 2000), (-3)));
        joinApplicationStatBoList.add(createJoinApplicationStatBo("id4", (currentTime + 3000), 4));
        joinApplicationStatBoList.add(createJoinApplicationStatBo("id5", (currentTime + 4000), (-5)));
        JoinApplicationStatBo resultJoinApplicationStatBo = JoinApplicationStatBo.joinApplicationStatBoByTimeSlice(joinApplicationStatBoList);
        List<JoinCpuLoadBo> joinCpuLoadBoList = resultJoinApplicationStatBo.getJoinCpuLoadBoList();
        Collections.sort(joinCpuLoadBoList, new JoinApplicationStatBoTest.ComparatorImpl());
        assertJoinCpuLoadBoList(joinCpuLoadBoList);
    }

    private class ComparatorImpl implements Comparator<JoinCpuLoadBo> {
        @Override
        public int compare(JoinCpuLoadBo bo1, JoinCpuLoadBo bo2) {
            return (bo1.getTimestamp()) < (bo2.getTimestamp()) ? -1 : 1;
        }
    }

    @Test
    public void joinApplicationStatBoByTimeSlice2Test() {
        final long currentTime = 1487149800000L;// 18:10:00 15 2 2017

        List<JoinApplicationStatBo> joinApplicationStatBoList = new ArrayList<JoinApplicationStatBo>();
        joinApplicationStatBoList.add(createJoinApplicationStatBo2("id1", currentTime, 10));
        joinApplicationStatBoList.add(createJoinApplicationStatBo2("id2", (currentTime + 1000), (-40)));
        joinApplicationStatBoList.add(createJoinApplicationStatBo2("id3", (currentTime + 2000), (-30)));
        joinApplicationStatBoList.add(createJoinApplicationStatBo2("id4", (currentTime + 3000), 40));
        joinApplicationStatBoList.add(createJoinApplicationStatBo2("id5", (currentTime + 4000), (-50)));
        JoinApplicationStatBo resultJoinApplicationStatBo = JoinApplicationStatBo.joinApplicationStatBoByTimeSlice(joinApplicationStatBoList);
        List<JoinMemoryBo> joinMemoryBoList = resultJoinApplicationStatBo.getJoinMemoryBoList();
        Collections.sort(joinMemoryBoList, new JoinApplicationStatBoTest.ComparatorImpl2());
        assertJoinMemoryBoList(joinMemoryBoList);
    }

    private class ComparatorImpl2 implements Comparator<JoinMemoryBo> {
        @Override
        public int compare(JoinMemoryBo bo1, JoinMemoryBo bo2) {
            return (bo1.getTimestamp()) < (bo2.getTimestamp()) ? -1 : 1;
        }
    }

    @Test
    public void joinApplicationStatBoByTimeSlice3Test() {
        List<JoinApplicationStatBo> joinApplicationStatBoList = new ArrayList<JoinApplicationStatBo>();
        List<JoinCpuLoadBo> joinCpuLoadBoList1 = new ArrayList<JoinCpuLoadBo>();
        JoinCpuLoadBo joinCpuLoadBo1_1 = new JoinCpuLoadBo("agent1", 44, 70, "agent1", 30, "agent1", 50, 60, "agent1", 33, "agent1", 1498462545000L);
        JoinCpuLoadBo joinCpuLoadBo1_2 = new JoinCpuLoadBo("agent1", 33, 40, "agent1", 10, "agent1", 20, 78, "agent1", 12, "agent1", 1498462550000L);
        JoinCpuLoadBo joinCpuLoadBo1_3 = new JoinCpuLoadBo("agent1", 55, 60, "agent1", 7, "agent1", 30, 39, "agent1", 30, "agent1", 1498462555000L);
        joinCpuLoadBoList1.add(joinCpuLoadBo1_1);
        joinCpuLoadBoList1.add(joinCpuLoadBo1_2);
        joinCpuLoadBoList1.add(joinCpuLoadBo1_3);
        JoinApplicationStatBo joinApplicationStatBo1 = new JoinApplicationStatBo();
        joinApplicationStatBo1.setId("test_app");
        joinApplicationStatBo1.setJoinCpuLoadBoList(joinCpuLoadBoList1);
        joinApplicationStatBo1.setTimestamp(1498462545000L);
        joinApplicationStatBoList.add(joinApplicationStatBo1);
        List<JoinCpuLoadBo> joinCpuLoadBoList2 = new ArrayList<JoinCpuLoadBo>();
        JoinCpuLoadBo joinCpuLoadBo2_1 = new JoinCpuLoadBo("agent1", 33, 70, "agent1", 30, "agent1", 50, 60, "agent1", 33, "agent1", 1498462545000L);
        JoinCpuLoadBo joinCpuLoadBo2_2 = new JoinCpuLoadBo("agent1", 22, 40, "agent1", 10, "agent1", 20, 78, "agent1", 12, "agent1", 1498462550000L);
        JoinCpuLoadBo joinCpuLoadBo2_3 = new JoinCpuLoadBo("agent1", 11, 60, "agent1", 7, "agent1", 30, 39, "agent1", 30, "agent1", 1498462555000L);
        JoinCpuLoadBo joinCpuLoadBo2_4 = new JoinCpuLoadBo("agent1", 77, 60, "agent1", 7, "agent1", 30, 39, "agent1", 30, "agent1", 1498462560000L);
        joinCpuLoadBoList2.add(joinCpuLoadBo2_1);
        joinCpuLoadBoList2.add(joinCpuLoadBo2_2);
        joinCpuLoadBoList2.add(joinCpuLoadBo2_3);
        joinCpuLoadBoList2.add(joinCpuLoadBo2_4);
        JoinApplicationStatBo joinApplicationStatBo2 = new JoinApplicationStatBo();
        joinApplicationStatBo2.setId("test_app");
        joinApplicationStatBo2.setJoinCpuLoadBoList(joinCpuLoadBoList2);
        joinApplicationStatBo2.setTimestamp(1498462545000L);
        joinApplicationStatBoList.add(joinApplicationStatBo2);
        List<JoinCpuLoadBo> joinCpuLoadBoList3 = new ArrayList<JoinCpuLoadBo>();
        JoinCpuLoadBo joinCpuLoadBo3_1 = new JoinCpuLoadBo("agent1", 22, 70, "agent1", 30, "agent1", 50, 60, "agent1", 33, "agent1", 1498462545000L);
        JoinCpuLoadBo joinCpuLoadBo3_2 = new JoinCpuLoadBo("agent1", 11, 40, "agent1", 10, "agent1", 20, 78, "agent1", 12, "agent1", 1498462550000L);
        JoinCpuLoadBo joinCpuLoadBo3_3 = new JoinCpuLoadBo("agent1", 88, 60, "agent1", 7, "agent1", 30, 39, "agent1", 30, "agent1", 1498462565000L);
        joinCpuLoadBoList3.add(joinCpuLoadBo3_1);
        joinCpuLoadBoList3.add(joinCpuLoadBo3_2);
        joinCpuLoadBoList3.add(joinCpuLoadBo3_3);
        JoinApplicationStatBo joinApplicationStatBo3 = new JoinApplicationStatBo();
        joinApplicationStatBo3.setId("test_app");
        joinApplicationStatBo3.setJoinCpuLoadBoList(joinCpuLoadBoList3);
        joinApplicationStatBo3.setTimestamp(1498462545000L);
        joinApplicationStatBoList.add(joinApplicationStatBo3);
        JoinApplicationStatBo joinApplicationStatBo = JoinApplicationStatBo.joinApplicationStatBoByTimeSlice(joinApplicationStatBoList);
        Assert.assertEquals(joinApplicationStatBo.getId(), "test_app");
        Assert.assertEquals(joinApplicationStatBo.getTimestamp(), 1498462545000L);
        List<JoinCpuLoadBo> joinCpuLoadBoList = joinApplicationStatBo.getJoinCpuLoadBoList();
        Collections.sort(joinCpuLoadBoList, new JoinApplicationStatBoTest.ComparatorImpl());
        Assert.assertEquals(joinCpuLoadBoList.size(), 5);
        Assert.assertEquals(joinCpuLoadBoList.get(0).getJvmCpuLoad(), 33, 0);
        Assert.assertEquals(joinCpuLoadBoList.get(1).getJvmCpuLoad(), 22, 0);
        Assert.assertEquals(joinCpuLoadBoList.get(2).getJvmCpuLoad(), 33, 0);
        Assert.assertEquals(joinCpuLoadBoList.get(3).getJvmCpuLoad(), 77, 0);
        Assert.assertEquals(joinCpuLoadBoList.get(4).getJvmCpuLoad(), 88, 0);
    }

    @Test
    public void joinApplicationStatBoByTimeSlice4Test() {
        List<JoinApplicationStatBo> joinApplicationStatBoList = new ArrayList<JoinApplicationStatBo>();
        List<JoinMemoryBo> joinMemoryBoList1 = new ArrayList<JoinMemoryBo>();
        JoinMemoryBo joinMemoryBo1_1 = new JoinMemoryBo("agent1", 1498462545000L, 3000, 2000, 5000, "agent1", "agent1", 500, 50, 600, "agent1", "agent1");
        JoinMemoryBo joinMemoryBo1_2 = new JoinMemoryBo("agent2", 1498462550000L, 4000, 1000, 7000, "agent2", "agent2", 400, 150, 600, "agent2", "agent2");
        JoinMemoryBo joinMemoryBo1_3 = new JoinMemoryBo("agent3", 1498462555000L, 5000, 3000, 8000, "agent3", "agent3", 200, 100, 200, "agent3", "agent3");
        joinMemoryBoList1.add(joinMemoryBo1_1);
        joinMemoryBoList1.add(joinMemoryBo1_2);
        joinMemoryBoList1.add(joinMemoryBo1_3);
        JoinApplicationStatBo joinApplicationStatBo1 = new JoinApplicationStatBo();
        joinApplicationStatBo1.setId("test_app");
        joinApplicationStatBo1.setJoinMemoryBoList(joinMemoryBoList1);
        joinApplicationStatBo1.setTimestamp(1498462545000L);
        joinApplicationStatBoList.add(joinApplicationStatBo1);
        List<JoinMemoryBo> joinMemoryBoList2 = new ArrayList<JoinMemoryBo>();
        JoinMemoryBo joinMemoryBo2_1 = new JoinMemoryBo("agent1", 1498462545000L, 4000, 2000, 5000, "agent1", "agent1", 500, 50, 600, "agent1", "agent1");
        JoinMemoryBo joinMemoryBo2_2 = new JoinMemoryBo("agent2", 1498462550000L, 1000, 1000, 7000, "agent2", "agent2", 400, 150, 600, "agent2", "agent2");
        JoinMemoryBo joinMemoryBo2_3 = new JoinMemoryBo("agent3", 1498462555000L, 3000, 3000, 8000, "agent3", "agent3", 200, 100, 200, "agent3", "agent3");
        JoinMemoryBo joinMemoryBo2_4 = new JoinMemoryBo("agent3", 1498462560000L, 8800, 3000, 8000, "agent3", "agent3", 200, 100, 200, "agent3", "agent3");
        joinMemoryBoList2.add(joinMemoryBo2_1);
        joinMemoryBoList2.add(joinMemoryBo2_2);
        joinMemoryBoList2.add(joinMemoryBo2_3);
        joinMemoryBoList2.add(joinMemoryBo2_4);
        JoinApplicationStatBo joinApplicationStatBo2 = new JoinApplicationStatBo();
        joinApplicationStatBo2.setId("test_app");
        joinApplicationStatBo2.setJoinMemoryBoList(joinMemoryBoList2);
        joinApplicationStatBo2.setTimestamp(1498462545000L);
        joinApplicationStatBoList.add(joinApplicationStatBo2);
        List<JoinMemoryBo> joinMemoryBoList3 = new ArrayList<JoinMemoryBo>();
        JoinMemoryBo joinMemoryBo3_1 = new JoinMemoryBo("agent1", 1498462545000L, 5000, 2000, 5000, "agent1", "agent1", 500, 50, 600, "agent1", "agent1");
        JoinMemoryBo joinMemoryBo3_2 = new JoinMemoryBo("agent2", 1498462550000L, 1000, 1000, 7000, "agent2", "agent2", 400, 150, 600, "agent2", "agent2");
        JoinMemoryBo joinMemoryBo3_3 = new JoinMemoryBo("agent3", 1498462565000L, 7800, 3000, 8000, "agent3", "agent3", 200, 100, 200, "agent3", "agent3");
        joinMemoryBoList3.add(joinMemoryBo3_1);
        joinMemoryBoList3.add(joinMemoryBo3_2);
        joinMemoryBoList3.add(joinMemoryBo3_3);
        JoinApplicationStatBo joinApplicationStatBo3 = new JoinApplicationStatBo();
        joinApplicationStatBo3.setId("test_app");
        joinApplicationStatBo3.setJoinMemoryBoList(joinMemoryBoList3);
        joinApplicationStatBo3.setTimestamp(1498462545000L);
        joinApplicationStatBoList.add(joinApplicationStatBo3);
        JoinApplicationStatBo joinApplicationStatBo = JoinApplicationStatBo.joinApplicationStatBoByTimeSlice(joinApplicationStatBoList);
        Assert.assertEquals(joinApplicationStatBo.getId(), "test_app");
        Assert.assertEquals(joinApplicationStatBo.getTimestamp(), 1498462545000L);
        List<JoinMemoryBo> joinMemoryBoList = joinApplicationStatBo.getJoinMemoryBoList();
        Collections.sort(joinMemoryBoList, new JoinApplicationStatBoTest.ComparatorImpl2());
        Assert.assertEquals(joinMemoryBoList.size(), 5);
        Assert.assertEquals(joinMemoryBoList.get(0).getHeapUsed(), 4000);
        Assert.assertEquals(joinMemoryBoList.get(1).getHeapUsed(), 2000);
        Assert.assertEquals(joinMemoryBoList.get(2).getHeapUsed(), 4000);
        Assert.assertEquals(joinMemoryBoList.get(3).getHeapUsed(), 8800);
        Assert.assertEquals(joinMemoryBoList.get(4).getHeapUsed(), 7800);
    }

    @Test
    public void joinApplicationStatBoByTimeSlice5Test() {
        final long currentTime = 1487149800000L;// 18:10:00 15 2 2017

        List<JoinApplicationStatBo> joinApplicationStatBoList = new ArrayList<JoinApplicationStatBo>();
        joinApplicationStatBoList.add(createJoinApplicationStatBo3("id1", currentTime, 10));
        joinApplicationStatBoList.add(createJoinApplicationStatBo3("id2", (currentTime + 1000), (-40)));
        joinApplicationStatBoList.add(createJoinApplicationStatBo3("id3", (currentTime + 2000), (-30)));
        joinApplicationStatBoList.add(createJoinApplicationStatBo3("id4", (currentTime + 3000), 40));
        joinApplicationStatBoList.add(createJoinApplicationStatBo3("id5", (currentTime + 4000), (-50)));
        JoinApplicationStatBo resultJoinApplicationStatBo = JoinApplicationStatBo.joinApplicationStatBoByTimeSlice(joinApplicationStatBoList);
        List<JoinTransactionBo> joinTransactionBoList = resultJoinApplicationStatBo.getJoinTransactionBoList();
        Collections.sort(joinTransactionBoList, new JoinApplicationStatBoTest.ComparatorImpl3());
        assertJoinTransactionBoList(joinTransactionBoList);
    }

    private class ComparatorImpl3 implements Comparator<JoinTransactionBo> {
        @Override
        public int compare(JoinTransactionBo bo1, JoinTransactionBo bo2) {
            return (bo1.getTimestamp()) < (bo2.getTimestamp()) ? -1 : 1;
        }
    }

    @Test
    public void joinApplicationStatBoByTimeSlice6Test() {
        List<JoinApplicationStatBo> joinApplicationStatBoList = new ArrayList<JoinApplicationStatBo>();
        List<JoinTransactionBo> joinTransactionBoList1 = new ArrayList<JoinTransactionBo>();
        JoinTransactionBo joinTransactionBo1_1 = new JoinTransactionBo("agent1", 5000, 100, 60, "agent1", 200, "agent1", 1498462545000L);
        JoinTransactionBo joinTransactionBo1_2 = new JoinTransactionBo("agent2", 5000, 100, 60, "agent2", 200, "agent2", 1498462550000L);
        JoinTransactionBo joinTransactionBo1_3 = new JoinTransactionBo("agent3", 5000, 100, 60, "agent3", 200, "agent3", 1498462555000L);
        joinTransactionBoList1.add(joinTransactionBo1_1);
        joinTransactionBoList1.add(joinTransactionBo1_2);
        joinTransactionBoList1.add(joinTransactionBo1_3);
        JoinApplicationStatBo joinApplicationStatBo1 = new JoinApplicationStatBo();
        joinApplicationStatBo1.setId("test_app");
        joinApplicationStatBo1.setJoinTransactionBoList(joinTransactionBoList1);
        joinApplicationStatBo1.setTimestamp(1498462545000L);
        joinApplicationStatBoList.add(joinApplicationStatBo1);
        List<JoinTransactionBo> joinTransactionBoList2 = new ArrayList<JoinTransactionBo>();
        JoinTransactionBo joinTransactionBo2_1 = new JoinTransactionBo("agent1", 5000, 50, 20, "agent1", 230, "agent1", 1498462545000L);
        JoinTransactionBo joinTransactionBo2_2 = new JoinTransactionBo("agent2", 5000, 200, 60, "agent2", 400, "agent2", 1498462550000L);
        JoinTransactionBo joinTransactionBo2_3 = new JoinTransactionBo("agent3", 5000, 500, 10, "agent3", 100, "agent3", 1498462555000L);
        JoinTransactionBo joinTransactionBo2_4 = new JoinTransactionBo("agent3", 5000, 400, 60, "agent3", 500, "agent3", 1498462560000L);
        joinTransactionBoList2.add(joinTransactionBo2_1);
        joinTransactionBoList2.add(joinTransactionBo2_2);
        joinTransactionBoList2.add(joinTransactionBo2_3);
        joinTransactionBoList2.add(joinTransactionBo2_4);
        JoinApplicationStatBo joinApplicationStatBo2 = new JoinApplicationStatBo();
        joinApplicationStatBo2.setId("test_app");
        joinApplicationStatBo2.setJoinTransactionBoList(joinTransactionBoList2);
        joinApplicationStatBo2.setTimestamp(1498462545000L);
        joinApplicationStatBoList.add(joinApplicationStatBo2);
        List<JoinTransactionBo> joinTransactionBoList3 = new ArrayList<JoinTransactionBo>();
        JoinTransactionBo joinTransactionBo3_1 = new JoinTransactionBo("agent1", 5000, 150, 20, "agent1", 230, "agent1", 1498462545000L);
        JoinTransactionBo joinTransactionBo3_2 = new JoinTransactionBo("agent2", 5000, 300, 10, "agent2", 400, "agent2", 1498462550000L);
        JoinTransactionBo joinTransactionBo3_3 = new JoinTransactionBo("agent3", 5000, 30, 5, "agent3", 100, "agent3", 1498462565000L);
        joinTransactionBoList3.add(joinTransactionBo3_1);
        joinTransactionBoList3.add(joinTransactionBo3_2);
        joinTransactionBoList3.add(joinTransactionBo3_3);
        JoinApplicationStatBo joinApplicationStatBo3 = new JoinApplicationStatBo();
        joinApplicationStatBo3.setId("test_app");
        joinApplicationStatBo3.setJoinTransactionBoList(joinTransactionBoList3);
        joinApplicationStatBo3.setTimestamp(1498462545000L);
        joinApplicationStatBoList.add(joinApplicationStatBo3);
        JoinApplicationStatBo joinApplicationStatBo = JoinApplicationStatBo.joinApplicationStatBoByTimeSlice(joinApplicationStatBoList);
        Assert.assertEquals(joinApplicationStatBo.getId(), "test_app");
        Assert.assertEquals(joinApplicationStatBo.getTimestamp(), 1498462545000L);
        List<JoinTransactionBo> joinTransactionBoList = joinApplicationStatBo.getJoinTransactionBoList();
        Collections.sort(joinTransactionBoList, new JoinApplicationStatBoTest.ComparatorImpl3());
        Assert.assertEquals(joinTransactionBoList.size(), 5);
        Assert.assertEquals(joinTransactionBoList.get(0).getTotalCount(), 100);
        Assert.assertEquals(joinTransactionBoList.get(1).getTotalCount(), 200);
        Assert.assertEquals(joinTransactionBoList.get(2).getTotalCount(), 300);
        Assert.assertEquals(joinTransactionBoList.get(3).getTotalCount(), 400);
        Assert.assertEquals(joinTransactionBoList.get(4).getTotalCount(), 30);
    }

    @Test
    public void joinApplicationStatBoByTimeSlice7Test() {
        final long currentTime = 1487149800000L;// 18:10:00 15 2 2017

        List<JoinApplicationStatBo> joinApplicationStatBoList = new ArrayList<JoinApplicationStatBo>();
        joinApplicationStatBoList.add(createJoinApplicationStatBo4("id1", currentTime, 10));
        joinApplicationStatBoList.add(createJoinApplicationStatBo4("id2", (currentTime + 1000), (-40)));
        joinApplicationStatBoList.add(createJoinApplicationStatBo4("id3", (currentTime + 2000), (-30)));
        joinApplicationStatBoList.add(createJoinApplicationStatBo4("id4", (currentTime + 3000), 40));
        joinApplicationStatBoList.add(createJoinApplicationStatBo4("id5", (currentTime + 4000), (-50)));
        JoinApplicationStatBo resultJoinApplicationStatBo = JoinApplicationStatBo.joinApplicationStatBoByTimeSlice(joinApplicationStatBoList);
        List<JoinActiveTraceBo> joinActiveTraceBoList = resultJoinApplicationStatBo.getJoinActiveTraceBoList();
        Collections.sort(joinActiveTraceBoList, new JoinApplicationStatBoTest.ComparatorImpl4());
        assertJoinActiveTraceBoList(joinActiveTraceBoList);
    }

    @Test
    public void joinApplicationStatBoByTimeSlice8Test() {
        List<JoinApplicationStatBo> joinApplicationStatBoList = new ArrayList<JoinApplicationStatBo>();
        List<JoinActiveTraceBo> joinActiveTraceBoList1 = new ArrayList<JoinActiveTraceBo>();
        JoinActiveTraceBo joinActiveTraceBo1_1 = new JoinActiveTraceBo("agent1", 1, ((short) (2)), 100, 60, "agent1", 200, "agent1", 1498462545000L);
        JoinActiveTraceBo joinActiveTraceBo1_2 = new JoinActiveTraceBo("agent2", 1, ((short) (2)), 100, 60, "agent1", 200, "agent1", 1498462550000L);
        JoinActiveTraceBo joinActiveTraceBo1_3 = new JoinActiveTraceBo("agent3", 1, ((short) (2)), 100, 60, "agent1", 200, "agent1", 1498462555000L);
        joinActiveTraceBoList1.add(joinActiveTraceBo1_1);
        joinActiveTraceBoList1.add(joinActiveTraceBo1_2);
        joinActiveTraceBoList1.add(joinActiveTraceBo1_3);
        JoinApplicationStatBo joinApplicationStatBo1 = new JoinApplicationStatBo();
        joinApplicationStatBo1.setId("test_app");
        joinApplicationStatBo1.setJoinActiveTraceBoList(joinActiveTraceBoList1);
        joinApplicationStatBo1.setTimestamp(1498462545000L);
        joinApplicationStatBoList.add(joinApplicationStatBo1);
        List<JoinActiveTraceBo> joinActiveTraceBoList2 = new ArrayList<JoinActiveTraceBo>();
        JoinActiveTraceBo joinActiveTraceBo2_1 = new JoinActiveTraceBo("agent1", 1, ((short) (2)), 50, 20, "agent1", 230, "agent1", 1498462545000L);
        JoinActiveTraceBo joinActiveTraceBo2_2 = new JoinActiveTraceBo("agent2", 1, ((short) (2)), 200, 60, "agent2", 400, "agent2", 1498462550000L);
        JoinActiveTraceBo joinActiveTraceBo2_3 = new JoinActiveTraceBo("agent3", 1, ((short) (2)), 500, 10, "agent3", 100, "agent3", 1498462555000L);
        JoinActiveTraceBo joinActiveTraceBo2_4 = new JoinActiveTraceBo("agent3", 1, ((short) (2)), 400, 60, "agent3", 500, "agent3", 1498462560000L);
        joinActiveTraceBoList2.add(joinActiveTraceBo2_1);
        joinActiveTraceBoList2.add(joinActiveTraceBo2_2);
        joinActiveTraceBoList2.add(joinActiveTraceBo2_3);
        joinActiveTraceBoList2.add(joinActiveTraceBo2_4);
        JoinApplicationStatBo joinApplicationStatBo2 = new JoinApplicationStatBo();
        joinApplicationStatBo2.setId("test_app");
        joinApplicationStatBo2.setJoinActiveTraceBoList(joinActiveTraceBoList2);
        joinApplicationStatBo2.setTimestamp(1498462545000L);
        joinApplicationStatBoList.add(joinApplicationStatBo2);
        List<JoinActiveTraceBo> joinActiveTraceBoList3 = new ArrayList<JoinActiveTraceBo>();
        JoinActiveTraceBo joinActiveTraceBo3_1 = new JoinActiveTraceBo("agent1", 1, ((short) (2)), 150, 20, "agent1", 230, "agent1", 1498462545000L);
        JoinActiveTraceBo joinActiveTraceBo3_2 = new JoinActiveTraceBo("agent2", 1, ((short) (2)), 300, 10, "agent2", 400, "agent2", 1498462550000L);
        JoinActiveTraceBo joinActiveTraceBo3_3 = new JoinActiveTraceBo("agent3", 1, ((short) (2)), 30, 5, "agent3", 100, "agent3", 1498462565000L);
        joinActiveTraceBoList3.add(joinActiveTraceBo3_1);
        joinActiveTraceBoList3.add(joinActiveTraceBo3_2);
        joinActiveTraceBoList3.add(joinActiveTraceBo3_3);
        JoinApplicationStatBo joinApplicationStatBo3 = new JoinApplicationStatBo();
        joinApplicationStatBo3.setId("test_app");
        joinApplicationStatBo3.setJoinActiveTraceBoList(joinActiveTraceBoList3);
        joinApplicationStatBo3.setTimestamp(1498462545000L);
        joinApplicationStatBoList.add(joinApplicationStatBo3);
        JoinApplicationStatBo joinApplicationStatBo = JoinApplicationStatBo.joinApplicationStatBoByTimeSlice(joinApplicationStatBoList);
        Assert.assertEquals(joinApplicationStatBo.getId(), "test_app");
        Assert.assertEquals(joinApplicationStatBo.getTimestamp(), 1498462545000L);
        List<JoinActiveTraceBo> joinActiveTraceBoList = joinApplicationStatBo.getJoinActiveTraceBoList();
        Collections.sort(joinActiveTraceBoList, new JoinApplicationStatBoTest.ComparatorImpl4());
        Assert.assertEquals(joinActiveTraceBoList.size(), 5);
        Assert.assertEquals(joinActiveTraceBoList.get(0).getTotalCount(), 100);
        Assert.assertEquals(joinActiveTraceBoList.get(1).getTotalCount(), 200);
        Assert.assertEquals(joinActiveTraceBoList.get(2).getTotalCount(), 300);
        Assert.assertEquals(joinActiveTraceBoList.get(3).getTotalCount(), 400);
        Assert.assertEquals(joinActiveTraceBoList.get(4).getTotalCount(), 30);
    }

    private class ComparatorImpl4 implements Comparator<JoinActiveTraceBo> {
        @Override
        public int compare(JoinActiveTraceBo bo1, JoinActiveTraceBo bo2) {
            return (bo1.getTimestamp()) < (bo2.getTimestamp()) ? -1 : 1;
        }
    }

    @Test
    public void joinApplicationStatBoByTimeSlice9Test() {
        final long currentTime = 1487149800000L;// 18:10:00 15 2 2017

        List<JoinApplicationStatBo> joinApplicationStatBoList = new ArrayList<JoinApplicationStatBo>();
        joinApplicationStatBoList.add(createJoinApplicationStatBo5("id1", currentTime, 10));
        joinApplicationStatBoList.add(createJoinApplicationStatBo5("id2", (currentTime + 1000), (-40)));
        joinApplicationStatBoList.add(createJoinApplicationStatBo5("id3", (currentTime + 2000), (-30)));
        joinApplicationStatBoList.add(createJoinApplicationStatBo5("id4", (currentTime + 3000), 40));
        joinApplicationStatBoList.add(createJoinApplicationStatBo5("id5", (currentTime + 4000), (-50)));
        JoinApplicationStatBo resultJoinApplicationStatBo = JoinApplicationStatBo.joinApplicationStatBoByTimeSlice(joinApplicationStatBoList);
        List<JoinResponseTimeBo> joinResponseTimeBoList = resultJoinApplicationStatBo.getJoinResponseTimeBoList();
        Collections.sort(joinResponseTimeBoList, new JoinApplicationStatBoTest.ComparatorImpl5());
        assertJoinResponseTimeBoList(joinResponseTimeBoList);
    }

    @Test
    public void joinApplicationStatBoByTimeSlice10Test() {
        List<JoinApplicationStatBo> joinApplicationStatBoList = new ArrayList<JoinApplicationStatBo>();
        List<JoinResponseTimeBo> joinResponseTimeBoList1 = new ArrayList<JoinResponseTimeBo>();
        JoinResponseTimeBo joinResponseTimeBo1_1 = new JoinResponseTimeBo("agent1", 1498462545000L, 100, 60, "agent1", 200, "agent1");
        JoinResponseTimeBo joinResponseTimeBo1_2 = new JoinResponseTimeBo("agent1", 1498462550000L, 100, 60, "agent1", 200, "agent1");
        JoinResponseTimeBo joinResponseTimeBo1_3 = new JoinResponseTimeBo("agent1", 1498462555000L, 100, 60, "agent1", 200, "agent1");
        joinResponseTimeBoList1.add(joinResponseTimeBo1_1);
        joinResponseTimeBoList1.add(joinResponseTimeBo1_2);
        joinResponseTimeBoList1.add(joinResponseTimeBo1_3);
        JoinApplicationStatBo joinApplicationStatBo1 = new JoinApplicationStatBo();
        joinApplicationStatBo1.setId("test_app");
        joinApplicationStatBo1.setJoinResponseTimeBoList(joinResponseTimeBoList1);
        joinApplicationStatBo1.setTimestamp(1498462545000L);
        joinApplicationStatBoList.add(joinApplicationStatBo1);
        List<JoinResponseTimeBo> joinResponseTimeBoList2 = new ArrayList<JoinResponseTimeBo>();
        JoinResponseTimeBo joinResponseTimeBo2_1 = new JoinResponseTimeBo("agent1", 1498462545000L, 50, 20, "agent1", 230, "agent1");
        JoinResponseTimeBo joinResponseTimeBo2_2 = new JoinResponseTimeBo("agent2", 1498462550000L, 200, 60, "agent2", 400, "agent2");
        JoinResponseTimeBo joinResponseTimeBo2_3 = new JoinResponseTimeBo("agent3", 1498462555000L, 500, 10, "agent3", 100, "agent3");
        JoinResponseTimeBo joinResponseTimeBo2_4 = new JoinResponseTimeBo("agent3", 1498462560000L, 400, 60, "agent3", 500, "agent3");
        joinResponseTimeBoList2.add(joinResponseTimeBo2_1);
        joinResponseTimeBoList2.add(joinResponseTimeBo2_2);
        joinResponseTimeBoList2.add(joinResponseTimeBo2_3);
        joinResponseTimeBoList2.add(joinResponseTimeBo2_4);
        JoinApplicationStatBo joinApplicationStatBo2 = new JoinApplicationStatBo();
        joinApplicationStatBo2.setId("test_app");
        joinApplicationStatBo2.setJoinResponseTimeBoList(joinResponseTimeBoList2);
        joinApplicationStatBo2.setTimestamp(1498462545000L);
        joinApplicationStatBoList.add(joinApplicationStatBo2);
        List<JoinResponseTimeBo> joinResponseTimeBoList3 = new ArrayList<JoinResponseTimeBo>();
        JoinResponseTimeBo joinResponseTimeBo3_1 = new JoinResponseTimeBo("agent1", 1498462545000L, 150, 20, "agent1", 230, "agent1");
        JoinResponseTimeBo joinResponseTimeBo3_2 = new JoinResponseTimeBo("agent2", 1498462550000L, 300, 10, "agent2", 400, "agent2");
        JoinResponseTimeBo joinResponseTimeBo3_3 = new JoinResponseTimeBo("agent3", 1498462565000L, 30, 5, "agent3", 100, "agent3");
        joinResponseTimeBoList3.add(joinResponseTimeBo3_1);
        joinResponseTimeBoList3.add(joinResponseTimeBo3_2);
        joinResponseTimeBoList3.add(joinResponseTimeBo3_3);
        JoinApplicationStatBo joinApplicationStatBo3 = new JoinApplicationStatBo();
        joinApplicationStatBo3.setId("test_app");
        joinApplicationStatBo3.setJoinResponseTimeBoList(joinResponseTimeBoList3);
        joinApplicationStatBo3.setTimestamp(1498462545000L);
        joinApplicationStatBoList.add(joinApplicationStatBo3);
        JoinApplicationStatBo joinApplicationStatBo = JoinApplicationStatBo.joinApplicationStatBoByTimeSlice(joinApplicationStatBoList);
        Assert.assertEquals(joinApplicationStatBo.getId(), "test_app");
        Assert.assertEquals(joinApplicationStatBo.getTimestamp(), 1498462545000L);
        List<JoinResponseTimeBo> joinResponseTimeBoList = joinApplicationStatBo.getJoinResponseTimeBoList();
        Collections.sort(joinResponseTimeBoList, new JoinApplicationStatBoTest.ComparatorImpl5());
        Assert.assertEquals(joinResponseTimeBoList.size(), 5);
        Assert.assertEquals(joinResponseTimeBoList.get(0).getAvg(), 100);
        Assert.assertEquals(joinResponseTimeBoList.get(1).getAvg(), 200);
        Assert.assertEquals(joinResponseTimeBoList.get(2).getAvg(), 300);
        Assert.assertEquals(joinResponseTimeBoList.get(3).getAvg(), 400);
        Assert.assertEquals(joinResponseTimeBoList.get(4).getAvg(), 30);
    }

    private class ComparatorImpl5 implements Comparator<JoinResponseTimeBo> {
        @Override
        public int compare(JoinResponseTimeBo bo1, JoinResponseTimeBo bo2) {
            return (bo1.getTimestamp()) < (bo2.getTimestamp()) ? -1 : 1;
        }
    }

    @Test
    public void joinApplicationStatBoByTimeSlice11Test() {
        final long currentTime = 1487149800000L;// 18:10:00 15 2 2017

        List<JoinApplicationStatBo> joinApplicationStatBoList = new ArrayList<JoinApplicationStatBo>();
        joinApplicationStatBoList.add(createJoinApplicationStatBo6("id1", currentTime, 10));
        joinApplicationStatBoList.add(createJoinApplicationStatBo6("id2", (currentTime + 1000), (-40)));
        joinApplicationStatBoList.add(createJoinApplicationStatBo6("id3", (currentTime + 2000), (-30)));
        joinApplicationStatBoList.add(createJoinApplicationStatBo6("id4", (currentTime + 3000), 40));
        joinApplicationStatBoList.add(createJoinApplicationStatBo6("id5", (currentTime + 4000), (-50)));
        JoinApplicationStatBo resultJoinApplicationStatBo = JoinApplicationStatBo.joinApplicationStatBoByTimeSlice(joinApplicationStatBoList);
        List<JoinDataSourceListBo> joinDataSourceListBoList = resultJoinApplicationStatBo.getJoinDataSourceListBoList();
        Collections.sort(joinDataSourceListBoList, new JoinApplicationStatBoTest.ComparatorImpl6());
        assertJoinDataSourceListBoList(joinDataSourceListBoList);
    }

    @Test
    public void joinApplicationStatBoByTimeSlice12Test() {
        List<JoinApplicationStatBo> joinApplicationStatBoList = new ArrayList<JoinApplicationStatBo>();
        List<JoinDataSourceListBo> joinDataSourceLIstBoList1 = new ArrayList<JoinDataSourceListBo>();
        List<JoinDataSourceBo> joinDataSourceBoList1 = new ArrayList<JoinDataSourceBo>();
        joinDataSourceBoList1.add(new JoinDataSourceBo(((short) (1000)), "jdbc:mysql", 100, 60, "agent1", 200, "agent1"));
        JoinDataSourceListBo joinDataSourceListBo1_1 = new JoinDataSourceListBo("agent1", joinDataSourceBoList1, 1498462545000L);
        JoinDataSourceListBo joinDataSourceListBo1_2 = new JoinDataSourceListBo("agent1", joinDataSourceBoList1, 1498462550000L);
        JoinDataSourceListBo joinDataSourceListBo1_3 = new JoinDataSourceListBo("agent1", joinDataSourceBoList1, 1498462555000L);
        joinDataSourceLIstBoList1.add(joinDataSourceListBo1_1);
        joinDataSourceLIstBoList1.add(joinDataSourceListBo1_2);
        joinDataSourceLIstBoList1.add(joinDataSourceListBo1_3);
        JoinApplicationStatBo joinApplicationStatBo1 = new JoinApplicationStatBo();
        joinApplicationStatBo1.setId("test_app");
        joinApplicationStatBo1.setJoinDataSourceListBoList(joinDataSourceLIstBoList1);
        joinApplicationStatBo1.setTimestamp(1498462545000L);
        joinApplicationStatBoList.add(joinApplicationStatBo1);
        List<JoinDataSourceListBo> joinDataSourceLIstBoList2 = new ArrayList<JoinDataSourceListBo>();
        List<JoinDataSourceBo> joinDataSourceBoList2_1 = new ArrayList<JoinDataSourceBo>();
        joinDataSourceBoList2_1.add(new JoinDataSourceBo(((short) (1000)), "jdbc:mysql", 50, 20, "agent1", 230, "agent1"));
        JoinDataSourceListBo joinResponseTimeBo2_1 = new JoinDataSourceListBo("agent1", joinDataSourceBoList2_1, 1498462545000L);
        List<JoinDataSourceBo> joinDataSourceBoList2_2 = new ArrayList<JoinDataSourceBo>();
        joinDataSourceBoList2_2.add(new JoinDataSourceBo(((short) (1000)), "jdbc:mysql", 200, 60, "agent2", 400, "agent2"));
        JoinDataSourceListBo joinResponseTimeBo2_2 = new JoinDataSourceListBo("agent2", joinDataSourceBoList2_2, 1498462550000L);
        List<JoinDataSourceBo> joinDataSourceBoList2_3 = new ArrayList<JoinDataSourceBo>();
        joinDataSourceBoList2_3.add(new JoinDataSourceBo(((short) (1000)), "jdbc:mysql", 500, 10, "agent3", 100, "agent3"));
        JoinDataSourceListBo joinResponseTimeBo2_3 = new JoinDataSourceListBo("agent3", joinDataSourceBoList2_3, 1498462555000L);
        List<JoinDataSourceBo> joinDataSourceBoList2_4 = new ArrayList<JoinDataSourceBo>();
        joinDataSourceBoList2_4.add(new JoinDataSourceBo(((short) (1000)), "jdbc:mysql", 400, 60, "agent3", 500, "agent3"));
        JoinDataSourceListBo joinResponseTimeBo2_4 = new JoinDataSourceListBo("agent3", joinDataSourceBoList2_4, 1498462560000L);
        joinDataSourceLIstBoList2.add(joinResponseTimeBo2_1);
        joinDataSourceLIstBoList2.add(joinResponseTimeBo2_2);
        joinDataSourceLIstBoList2.add(joinResponseTimeBo2_3);
        joinDataSourceLIstBoList2.add(joinResponseTimeBo2_4);
        JoinApplicationStatBo joinApplicationStatBo2 = new JoinApplicationStatBo();
        joinApplicationStatBo2.setId("test_app");
        joinApplicationStatBo2.setJoinDataSourceListBoList(joinDataSourceLIstBoList2);
        joinApplicationStatBo2.setTimestamp(1498462545000L);
        joinApplicationStatBoList.add(joinApplicationStatBo2);
        List<JoinDataSourceListBo> joinResponseTimeBoList3 = new ArrayList<JoinDataSourceListBo>();
        List<JoinDataSourceBo> joinDataSourceBoList3_1 = new ArrayList<JoinDataSourceBo>();
        joinDataSourceBoList3_1.add(new JoinDataSourceBo(((short) (1000)), "jdbc:mysql", 150, 20, "agent1", 230, "agent1"));
        JoinDataSourceListBo joinResponseTimeBo3_1 = new JoinDataSourceListBo("agent1", joinDataSourceBoList3_1, 1498462545000L);
        List<JoinDataSourceBo> joinDataSourceBoList3_2 = new ArrayList<JoinDataSourceBo>();
        joinDataSourceBoList3_2.add(new JoinDataSourceBo(((short) (1000)), "jdbc:mysql", 300, 10, "agent2", 400, "agent2"));
        JoinDataSourceListBo joinResponseTimeBo3_2 = new JoinDataSourceListBo("agent2", joinDataSourceBoList3_2, 1498462550000L);
        List<JoinDataSourceBo> joinDataSourceBoList3_3 = new ArrayList<JoinDataSourceBo>();
        joinDataSourceBoList3_3.add(new JoinDataSourceBo(((short) (1000)), "jdbc:mysql", 30, 5, "agent2", 100, "agent2"));
        JoinDataSourceListBo joinResponseTimeBo3_3 = new JoinDataSourceListBo("agent3", joinDataSourceBoList3_3, 1498462565000L);
        joinResponseTimeBoList3.add(joinResponseTimeBo3_1);
        joinResponseTimeBoList3.add(joinResponseTimeBo3_2);
        joinResponseTimeBoList3.add(joinResponseTimeBo3_3);
        JoinApplicationStatBo joinApplicationStatBo3 = new JoinApplicationStatBo();
        joinApplicationStatBo3.setId("test_app");
        joinApplicationStatBo3.setJoinDataSourceListBoList(joinResponseTimeBoList3);
        joinApplicationStatBo3.setTimestamp(1498462545000L);
        joinApplicationStatBoList.add(joinApplicationStatBo3);
        JoinApplicationStatBo joinApplicationStatBo = JoinApplicationStatBo.joinApplicationStatBoByTimeSlice(joinApplicationStatBoList);
        Assert.assertEquals(joinApplicationStatBo.getId(), "test_app");
        Assert.assertEquals(joinApplicationStatBo.getTimestamp(), 1498462545000L);
        List<JoinDataSourceListBo> joinDataSourceListBoList = joinApplicationStatBo.getJoinDataSourceListBoList();
        Collections.sort(joinDataSourceListBoList, new JoinApplicationStatBoTest.ComparatorImpl6());
        Assert.assertEquals(joinDataSourceListBoList.size(), 5);
        Assert.assertEquals(joinDataSourceListBoList.get(0).getJoinDataSourceBoList().get(0).getAvgActiveConnectionSize(), 100);
        Assert.assertEquals(joinDataSourceListBoList.get(0).getJoinDataSourceBoList().size(), 1);
        Assert.assertEquals(joinDataSourceListBoList.get(1).getJoinDataSourceBoList().get(0).getAvgActiveConnectionSize(), 200);
        Assert.assertEquals(joinDataSourceListBoList.get(1).getJoinDataSourceBoList().size(), 1);
        Assert.assertEquals(joinDataSourceListBoList.get(2).getJoinDataSourceBoList().get(0).getAvgActiveConnectionSize(), 300);
        Assert.assertEquals(joinDataSourceListBoList.get(2).getJoinDataSourceBoList().size(), 1);
        Assert.assertEquals(joinDataSourceListBoList.get(3).getJoinDataSourceBoList().get(0).getAvgActiveConnectionSize(), 400);
        Assert.assertEquals(joinDataSourceListBoList.get(3).getJoinDataSourceBoList().size(), 1);
        Assert.assertEquals(joinDataSourceListBoList.get(4).getJoinDataSourceBoList().get(0).getAvgActiveConnectionSize(), 30);
        Assert.assertEquals(joinDataSourceListBoList.get(4).getJoinDataSourceBoList().size(), 1);
    }

    private class ComparatorImpl6 implements Comparator<JoinDataSourceListBo> {
        @Override
        public int compare(JoinDataSourceListBo bo1, JoinDataSourceListBo bo2) {
            return (bo1.getTimestamp()) < (bo2.getTimestamp()) ? -1 : 1;
        }
    }

    @Test
    public void joinApplicationStatBoByTimeSlice13Test() {
        final long currentTime = 1487149800000L;// 18:10:00 15 2 2017

        List<JoinApplicationStatBo> joinApplicationStatBoList = new ArrayList<JoinApplicationStatBo>();
        joinApplicationStatBoList.add(createJoinApplicationStatBo7("id1", currentTime, 10));
        joinApplicationStatBoList.add(createJoinApplicationStatBo7("id2", (currentTime + 1000), (-40)));
        joinApplicationStatBoList.add(createJoinApplicationStatBo7("id3", (currentTime + 2000), (-30)));
        joinApplicationStatBoList.add(createJoinApplicationStatBo7("id4", (currentTime + 3000), 40));
        joinApplicationStatBoList.add(createJoinApplicationStatBo7("id5", (currentTime + 4000), (-50)));
        JoinApplicationStatBo resultJoinApplicationStatBo = JoinApplicationStatBo.joinApplicationStatBoByTimeSlice(joinApplicationStatBoList);
        List<JoinFileDescriptorBo> joinFileDescriptorBoList = resultJoinApplicationStatBo.getJoinFileDescriptorBoList();
        Collections.sort(joinFileDescriptorBoList, new JoinApplicationStatBoTest.ComparatorImpl7());
        assertJoinFileDescriptorBoList(joinFileDescriptorBoList);
    }

    @Test
    public void joinApplicationStatBoByTimeSlice14Test() {
        List<JoinApplicationStatBo> joinApplicationStatBoList = new ArrayList<JoinApplicationStatBo>();
        List<JoinFileDescriptorBo> joinFileDescriptorBoList1 = new ArrayList<JoinFileDescriptorBo>();
        JoinFileDescriptorBo joinFileDescriptorBo1_1 = new JoinFileDescriptorBo("agent1", 440, 700, "agent1", 300, "agent1", 1498462545000L);
        JoinFileDescriptorBo joinFileDescriptorBo1_2 = new JoinFileDescriptorBo("agent1", 330, 400, "agent1", 100, "agent1", 1498462550000L);
        JoinFileDescriptorBo joinFileDescriptorBo1_3 = new JoinFileDescriptorBo("agent1", 550, 600, "agent1", 70, "agent1", 1498462555000L);
        joinFileDescriptorBoList1.add(joinFileDescriptorBo1_1);
        joinFileDescriptorBoList1.add(joinFileDescriptorBo1_2);
        joinFileDescriptorBoList1.add(joinFileDescriptorBo1_3);
        JoinApplicationStatBo joinApplicationStatBo1 = new JoinApplicationStatBo();
        joinApplicationStatBo1.setId("test_app");
        joinApplicationStatBo1.setJoinFileDescriptorBoList(joinFileDescriptorBoList1);
        joinApplicationStatBo1.setTimestamp(1498462545000L);
        joinApplicationStatBoList.add(joinApplicationStatBo1);
        List<JoinFileDescriptorBo> joinFileDescriptorBoList2 = new ArrayList<JoinFileDescriptorBo>();
        JoinFileDescriptorBo joinFileDescriptorBo2_1 = new JoinFileDescriptorBo("agent1", 330, 700, "agent1", 300, "agent1", 1498462545000L);
        JoinFileDescriptorBo joinFileDescriptorBo2_2 = new JoinFileDescriptorBo("agent1", 220, 400, "agent1", 100, "agent1", 1498462550000L);
        JoinFileDescriptorBo joinFileDescriptorBo2_3 = new JoinFileDescriptorBo("agent1", 110, 600, "agent1", 70, "agent1", 1498462555000L);
        JoinFileDescriptorBo joinFileDescriptorBo2_4 = new JoinFileDescriptorBo("agent1", 770, 600, "agent1", 70, "agent1", 1498462560000L);
        joinFileDescriptorBoList2.add(joinFileDescriptorBo2_1);
        joinFileDescriptorBoList2.add(joinFileDescriptorBo2_2);
        joinFileDescriptorBoList2.add(joinFileDescriptorBo2_3);
        joinFileDescriptorBoList2.add(joinFileDescriptorBo2_4);
        JoinApplicationStatBo joinApplicationStatBo2 = new JoinApplicationStatBo();
        joinApplicationStatBo2.setId("test_app");
        joinApplicationStatBo2.setJoinFileDescriptorBoList(joinFileDescriptorBoList2);
        joinApplicationStatBo2.setTimestamp(1498462545000L);
        joinApplicationStatBoList.add(joinApplicationStatBo2);
        List<JoinFileDescriptorBo> joinFileDescriptorBoList3 = new ArrayList<JoinFileDescriptorBo>();
        JoinFileDescriptorBo joinFileDescriptorBo3_1 = new JoinFileDescriptorBo("agent1", 220, 700, "agent1", 300, "agent1", 1498462545000L);
        JoinFileDescriptorBo joinFileDescriptorBo3_2 = new JoinFileDescriptorBo("agent1", 110, 400, "agent1", 100, "agent1", 1498462550000L);
        JoinFileDescriptorBo joinFileDescriptorBo3_3 = new JoinFileDescriptorBo("agent1", 880, 600, "agent1", 70, "agent1", 1498462565000L);
        joinFileDescriptorBoList3.add(joinFileDescriptorBo3_1);
        joinFileDescriptorBoList3.add(joinFileDescriptorBo3_2);
        joinFileDescriptorBoList3.add(joinFileDescriptorBo3_3);
        JoinApplicationStatBo joinApplicationStatBo3 = new JoinApplicationStatBo();
        joinApplicationStatBo3.setId("test_app");
        joinApplicationStatBo3.setJoinFileDescriptorBoList(joinFileDescriptorBoList3);
        joinApplicationStatBo3.setTimestamp(1498462545000L);
        joinApplicationStatBoList.add(joinApplicationStatBo3);
        JoinApplicationStatBo joinApplicationStatBo = JoinApplicationStatBo.joinApplicationStatBoByTimeSlice(joinApplicationStatBoList);
        Assert.assertEquals(joinApplicationStatBo.getId(), "test_app");
        Assert.assertEquals(joinApplicationStatBo.getTimestamp(), 1498462545000L);
        List<JoinFileDescriptorBo> joinFileDescriptorBoList = joinApplicationStatBo.getJoinFileDescriptorBoList();
        Collections.sort(joinFileDescriptorBoList, new JoinApplicationStatBoTest.ComparatorImpl7());
        Assert.assertEquals(joinFileDescriptorBoList.size(), 5);
        Assert.assertEquals(joinFileDescriptorBoList.get(0).getAvgOpenFDCount(), 330, 0);
        Assert.assertEquals(joinFileDescriptorBoList.get(1).getAvgOpenFDCount(), 220, 0);
        Assert.assertEquals(joinFileDescriptorBoList.get(2).getAvgOpenFDCount(), 330, 0);
        Assert.assertEquals(joinFileDescriptorBoList.get(3).getAvgOpenFDCount(), 770, 0);
        Assert.assertEquals(joinFileDescriptorBoList.get(4).getAvgOpenFDCount(), 880, 0);
    }

    private class ComparatorImpl7 implements Comparator<JoinFileDescriptorBo> {
        @Override
        public int compare(JoinFileDescriptorBo bo1, JoinFileDescriptorBo bo2) {
            return (bo1.getTimestamp()) < (bo2.getTimestamp()) ? -1 : 1;
        }
    }

    @Test
    public void joinApplicationStatBoByTimeSlice15Test() {
        final long currentTime = 1487149800000L;// 18:10:00 15 2 2017

        List<JoinApplicationStatBo> joinApplicationStatBoList = new ArrayList<JoinApplicationStatBo>();
        joinApplicationStatBoList.add(createJoinApplicationStatBo8("id1", currentTime, 10));
        joinApplicationStatBoList.add(createJoinApplicationStatBo8("id2", (currentTime + 1000), (-40)));
        joinApplicationStatBoList.add(createJoinApplicationStatBo8("id3", (currentTime + 2000), (-30)));
        joinApplicationStatBoList.add(createJoinApplicationStatBo8("id4", (currentTime + 3000), 40));
        joinApplicationStatBoList.add(createJoinApplicationStatBo8("id5", (currentTime + 4000), (-50)));
        JoinApplicationStatBo resultJoinApplicationStatBo = JoinApplicationStatBo.joinApplicationStatBoByTimeSlice(joinApplicationStatBoList);
        List<JoinDirectBufferBo> joinDirectBufferBoList = resultJoinApplicationStatBo.getJoinDirectBufferBoList();
        Collections.sort(joinDirectBufferBoList, new JoinApplicationStatBoTest.ComparatorImpl8());
        assertJoinDirectBufferBoList(joinDirectBufferBoList);
    }

    @Test
    public void joinApplicationStatBoByTimeSlice16Test() {
        List<JoinApplicationStatBo> joinApplicationStatBoList = new ArrayList<JoinApplicationStatBo>();
        List<JoinDirectBufferBo> joinDirectBufferBoList1 = new ArrayList<JoinDirectBufferBo>();
        JoinDirectBufferBo joinDirectBufferBo1_1 = new JoinDirectBufferBo("agent1", 440, 700, "agent1", 300, "agent1", 440, 700, "agent1", 300, "agent1", 440, 700, "agent1", 300, "agent1", 440, 700, "agent1", 300, "agent1", 1498462545000L);
        JoinDirectBufferBo joinDirectBufferBo1_2 = new JoinDirectBufferBo("agent1", 330, 400, "agent1", 100, "agent1", 330, 400, "agent1", 100, "agent1", 330, 400, "agent1", 100, "agent1", 330, 400, "agent1", 100, "agent1", 1498462550000L);
        JoinDirectBufferBo joinDirectBufferBo1_3 = new JoinDirectBufferBo("agent1", 550, 600, "agent1", 70, "agent1", 550, 600, "agent1", 70, "agent1", 550, 600, "agent1", 70, "agent1", 550, 600, "agent1", 70, "agent1", 1498462555000L);
        joinDirectBufferBoList1.add(joinDirectBufferBo1_1);
        joinDirectBufferBoList1.add(joinDirectBufferBo1_2);
        joinDirectBufferBoList1.add(joinDirectBufferBo1_3);
        JoinApplicationStatBo joinApplicationStatBo1 = new JoinApplicationStatBo();
        joinApplicationStatBo1.setId("test_app");
        joinApplicationStatBo1.setJoinDirectBufferBoList(joinDirectBufferBoList1);
        joinApplicationStatBo1.setTimestamp(1498462545000L);
        joinApplicationStatBoList.add(joinApplicationStatBo1);
        List<JoinDirectBufferBo> joinDirectBufferBoList2 = new ArrayList<JoinDirectBufferBo>();
        JoinDirectBufferBo joinDirectBufferBo2_1 = new JoinDirectBufferBo("agent1", 330, 700, "agent1", 300, "agent1", 330, 700, "agent1", 300, "agent1", 330, 700, "agent1", 300, "agent1", 330, 700, "agent1", 300, "agent1", 1498462545000L);
        JoinDirectBufferBo joinDirectBufferBo2_2 = new JoinDirectBufferBo("agent1", 220, 400, "agent1", 100, "agent1", 220, 400, "agent1", 100, "agent1", 220, 400, "agent1", 100, "agent1", 220, 400, "agent1", 100, "agent1", 1498462550000L);
        JoinDirectBufferBo joinDirectBufferBo2_3 = new JoinDirectBufferBo("agent1", 110, 600, "agent1", 70, "agent1", 110, 600, "agent1", 70, "agent1", 110, 600, "agent1", 70, "agent1", 110, 600, "agent1", 70, "agent1", 1498462555000L);
        JoinDirectBufferBo joinDirectBufferBo2_4 = new JoinDirectBufferBo("agent1", 770, 600, "agent1", 70, "agent1", 770, 600, "agent1", 70, "agent1", 770, 600, "agent1", 70, "agent1", 770, 600, "agent1", 70, "agent1", 1498462560000L);
        joinDirectBufferBoList2.add(joinDirectBufferBo2_1);
        joinDirectBufferBoList2.add(joinDirectBufferBo2_2);
        joinDirectBufferBoList2.add(joinDirectBufferBo2_3);
        joinDirectBufferBoList2.add(joinDirectBufferBo2_4);
        JoinApplicationStatBo joinApplicationStatBo2 = new JoinApplicationStatBo();
        joinApplicationStatBo2.setId("test_app");
        joinApplicationStatBo2.setJoinDirectBufferBoList(joinDirectBufferBoList2);
        joinApplicationStatBo2.setTimestamp(1498462545000L);
        joinApplicationStatBoList.add(joinApplicationStatBo2);
        List<JoinDirectBufferBo> joinDirectBufferBoList3 = new ArrayList<JoinDirectBufferBo>();
        JoinDirectBufferBo joinDirectBufferBo3_1 = new JoinDirectBufferBo("agent1", 220, 700, "agent1", 300, "agent1", 220, 700, "agent1", 300, "agent1", 220, 700, "agent1", 300, "agent1", 220, 700, "agent1", 300, "agent1", 1498462545000L);
        JoinDirectBufferBo joinDirectBufferBo3_2 = new JoinDirectBufferBo("agent1", 110, 400, "agent1", 100, "agent1", 110, 400, "agent1", 100, "agent1", 110, 400, "agent1", 100, "agent1", 110, 400, "agent1", 100, "agent1", 1498462550000L);
        JoinDirectBufferBo joinDirectBufferBo3_3 = new JoinDirectBufferBo("agent1", 880, 600, "agent1", 70, "agent1", 880, 600, "agent1", 70, "agent1", 880, 600, "agent1", 70, "agent1", 880, 600, "agent1", 70, "agent1", 1498462565000L);
        joinDirectBufferBoList3.add(joinDirectBufferBo3_1);
        joinDirectBufferBoList3.add(joinDirectBufferBo3_2);
        joinDirectBufferBoList3.add(joinDirectBufferBo3_3);
        JoinApplicationStatBo joinApplicationStatBo3 = new JoinApplicationStatBo();
        joinApplicationStatBo3.setId("test_app");
        joinApplicationStatBo3.setJoinDirectBufferBoList(joinDirectBufferBoList3);
        joinApplicationStatBo3.setTimestamp(1498462545000L);
        joinApplicationStatBoList.add(joinApplicationStatBo3);
        JoinApplicationStatBo joinApplicationStatBo = JoinApplicationStatBo.joinApplicationStatBoByTimeSlice(joinApplicationStatBoList);
        Assert.assertEquals(joinApplicationStatBo.getId(), "test_app");
        Assert.assertEquals(joinApplicationStatBo.getTimestamp(), 1498462545000L);
        List<JoinDirectBufferBo> joinDirectBufferBoList = joinApplicationStatBo.getJoinDirectBufferBoList();
        Collections.sort(joinDirectBufferBoList, new JoinApplicationStatBoTest.ComparatorImpl8());
        Assert.assertEquals(joinDirectBufferBoList.size(), 5);
        Assert.assertEquals(joinDirectBufferBoList.get(0).getAvgDirectCount(), 330, 0);
        Assert.assertEquals(joinDirectBufferBoList.get(0).getAvgDirectMemoryUsed(), 330, 0);
        Assert.assertEquals(joinDirectBufferBoList.get(0).getAvgMappedCount(), 330, 0);
        Assert.assertEquals(joinDirectBufferBoList.get(0).getAvgMappedMemoryUsed(), 330, 0);
        Assert.assertEquals(joinDirectBufferBoList.get(1).getAvgDirectCount(), 220, 0);
        Assert.assertEquals(joinDirectBufferBoList.get(1).getAvgDirectMemoryUsed(), 220, 0);
        Assert.assertEquals(joinDirectBufferBoList.get(1).getAvgMappedCount(), 220, 0);
        Assert.assertEquals(joinDirectBufferBoList.get(1).getAvgMappedMemoryUsed(), 220, 0);
        Assert.assertEquals(joinDirectBufferBoList.get(2).getAvgDirectCount(), 330, 0);
        Assert.assertEquals(joinDirectBufferBoList.get(2).getAvgDirectMemoryUsed(), 330, 0);
        Assert.assertEquals(joinDirectBufferBoList.get(2).getAvgMappedCount(), 330, 0);
        Assert.assertEquals(joinDirectBufferBoList.get(2).getAvgMappedMemoryUsed(), 330, 0);
        Assert.assertEquals(joinDirectBufferBoList.get(3).getAvgDirectCount(), 770, 0);
        Assert.assertEquals(joinDirectBufferBoList.get(3).getAvgDirectMemoryUsed(), 770, 0);
        Assert.assertEquals(joinDirectBufferBoList.get(3).getAvgMappedCount(), 770, 0);
        Assert.assertEquals(joinDirectBufferBoList.get(3).getAvgMappedMemoryUsed(), 770, 0);
        Assert.assertEquals(joinDirectBufferBoList.get(4).getAvgDirectCount(), 880, 0);
        Assert.assertEquals(joinDirectBufferBoList.get(4).getAvgDirectMemoryUsed(), 880, 0);
        Assert.assertEquals(joinDirectBufferBoList.get(4).getAvgMappedCount(), 880, 0);
        Assert.assertEquals(joinDirectBufferBoList.get(4).getAvgMappedMemoryUsed(), 880, 0);
    }

    private class ComparatorImpl8 implements Comparator<JoinDirectBufferBo> {
        @Override
        public int compare(JoinDirectBufferBo bo1, JoinDirectBufferBo bo2) {
            return (bo1.getTimestamp()) < (bo2.getTimestamp()) ? -1 : 1;
        }
    }

    @Test
    public void createJoinApplicationStatBoTest() {
        JoinAgentStatBo joinAgentStatBo = new JoinAgentStatBo();
        joinAgentStatBo.setTimestamp(1498462565000L);
        List<JoinCpuLoadBo> joinCpuLoadBoList = new ArrayList<JoinCpuLoadBo>();
        JoinCpuLoadBo joinCpuLoadBo1 = new JoinCpuLoadBo("agent1", 44, 70, "agent1", 30, "agent1", 50, 60, "agent1", 33, "agent1", 1498462565000L);
        JoinCpuLoadBo joinCpuLoadBo2 = new JoinCpuLoadBo("agent1", 33, 40, "agent1", 10, "agent1", 20, 78, "agent1", 12, "agent1", 1498462570000L);
        JoinCpuLoadBo joinCpuLoadBo3 = new JoinCpuLoadBo("agent1", 55, 60, "agent1", 7, "agent1", 30, 39, "agent1", 30, "agent1", 1498462575000L);
        JoinCpuLoadBo joinCpuLoadBo4 = new JoinCpuLoadBo("agent1", 11, 80, "agent1", 8, "agent1", 10, 50, "agent1", 14, "agent1", 1498462580000L);
        JoinCpuLoadBo joinCpuLoadBo5 = new JoinCpuLoadBo("agent1", 22, 70, "agent1", 12, "agent1", 40, 99, "agent1", 50, "agent1", 1498462585000L);
        joinCpuLoadBoList.add(joinCpuLoadBo1);
        joinCpuLoadBoList.add(joinCpuLoadBo2);
        joinCpuLoadBoList.add(joinCpuLoadBo3);
        joinCpuLoadBoList.add(joinCpuLoadBo4);
        joinCpuLoadBoList.add(joinCpuLoadBo5);
        joinAgentStatBo.setJoinCpuLoadBoList(joinCpuLoadBoList);
        List<JoinMemoryBo> joinMemoryBoList = new ArrayList<JoinMemoryBo>();
        JoinMemoryBo joinMemoryBo1 = new JoinMemoryBo("agent1", 1498462565000L, 3000, 2000, 5000, "agent1", "agent1", 500, 50, 600, "agent1", "agent1");
        JoinMemoryBo joinMemoryBo2 = new JoinMemoryBo("agent1", 1498462570000L, 4000, 1000, 7000, "agent1", "agent1", 400, 150, 600, "agent1", "agent1");
        JoinMemoryBo joinMemoryBo3 = new JoinMemoryBo("agent1", 1498462575000L, 5000, 3000, 8000, "agent1", "agent1", 200, 100, 200, "agent1", "agent1");
        JoinMemoryBo joinMemoryBo4 = new JoinMemoryBo("agent1", 1498462580000L, 1000, 100, 3000, "agent1", "agent1", 100, 900, 1000, "agent1", "agent1");
        JoinMemoryBo joinMemoryBo5 = new JoinMemoryBo("agent1", 1498462585000L, 2000, 1000, 6000, "agent1", "agent1", 300, 100, 2900, "agent1", "agent1");
        joinMemoryBoList.add(joinMemoryBo1);
        joinMemoryBoList.add(joinMemoryBo2);
        joinMemoryBoList.add(joinMemoryBo3);
        joinMemoryBoList.add(joinMemoryBo4);
        joinMemoryBoList.add(joinMemoryBo5);
        joinAgentStatBo.setJoinMemoryBoList(joinMemoryBoList);
        List<JoinTransactionBo> joinTransactionBoList = new ArrayList<JoinTransactionBo>();
        JoinTransactionBo joinTransactionBo1 = new JoinTransactionBo("agent1", 5000, 150, 20, "agent1", 230, "agent1", 1498462565000L);
        JoinTransactionBo joinTransactionBo2 = new JoinTransactionBo("agent2", 5000, 300, 10, "agent2", 400, "agent2", 1498462570000L);
        JoinTransactionBo joinTransactionBo3 = new JoinTransactionBo("agent3", 5000, 30, 5, "agent3", 100, "agent3", 1498462575000L);
        JoinTransactionBo joinTransactionBo4 = new JoinTransactionBo("agent4", 5000, 30, 5, "agent4", 100, "agent4", 1498462580000L);
        JoinTransactionBo joinTransactionBo5 = new JoinTransactionBo("agent5", 5000, 30, 5, "agent5", 100, "agent5", 1498462585000L);
        joinTransactionBoList.add(joinTransactionBo1);
        joinTransactionBoList.add(joinTransactionBo2);
        joinTransactionBoList.add(joinTransactionBo3);
        joinTransactionBoList.add(joinTransactionBo4);
        joinTransactionBoList.add(joinTransactionBo5);
        joinAgentStatBo.setJoinTransactionBoList(joinTransactionBoList);
        List<JoinActiveTraceBo> JoinActiveTraceBoList = new ArrayList<JoinActiveTraceBo>();
        JoinActiveTraceBo joinActiveTraceBo1 = new JoinActiveTraceBo("agent1", 1, ((short) (2)), 30, 15, "app_1_1", 40, "app_1_2", 1498462565000L);
        JoinActiveTraceBo joinActiveTraceBo2 = new JoinActiveTraceBo("agent1", 1, ((short) (2)), 30, 15, "app_1_1", 40, "app_1_2", 1498462570000L);
        JoinActiveTraceBo joinActiveTraceBo3 = new JoinActiveTraceBo("agent1", 1, ((short) (2)), 30, 15, "app_1_1", 40, "app_1_2", 1498462575000L);
        JoinActiveTraceBo joinActiveTraceBo4 = new JoinActiveTraceBo("agent1", 1, ((short) (2)), 30, 15, "app_1_1", 40, "app_1_2", 1498462580000L);
        JoinActiveTraceBo joinActiveTraceBo5 = new JoinActiveTraceBo("agent1", 1, ((short) (2)), 30, 15, "app_1_1", 40, "app_1_2", 1498462585000L);
        JoinActiveTraceBoList.add(joinActiveTraceBo1);
        JoinActiveTraceBoList.add(joinActiveTraceBo2);
        JoinActiveTraceBoList.add(joinActiveTraceBo3);
        JoinActiveTraceBoList.add(joinActiveTraceBo4);
        JoinActiveTraceBoList.add(joinActiveTraceBo5);
        joinAgentStatBo.setJoinActiveTraceBoList(JoinActiveTraceBoList);
        List<JoinResponseTimeBo> joinResponseTimeBoList = new ArrayList<JoinResponseTimeBo>();
        JoinResponseTimeBo joinResponseTimeBo1 = new JoinResponseTimeBo("agent1", 1498462565000L, 3000, 2, "app_1_1", 6000, "app_1_2");
        JoinResponseTimeBo joinResponseTimeBo2 = new JoinResponseTimeBo("agent1", 1498462570000L, 4000, 200, "app_2_1", 9000, "app_2_2");
        JoinResponseTimeBo joinResponseTimeBo3 = new JoinResponseTimeBo("agent1", 1498462575000L, 2000, 20, "app_3_1", 7000, "app_3_2");
        JoinResponseTimeBo joinResponseTimeBo4 = new JoinResponseTimeBo("agent1", 1498462580000L, 5000, 20, "app_4_1", 8000, "app_4_2");
        JoinResponseTimeBo joinResponseTimeBo5 = new JoinResponseTimeBo("agent1", 1498462585000L, 1000, 10, "app_5_1", 6600, "app_5_2");
        joinResponseTimeBoList.add(joinResponseTimeBo1);
        joinResponseTimeBoList.add(joinResponseTimeBo2);
        joinResponseTimeBoList.add(joinResponseTimeBo3);
        joinResponseTimeBoList.add(joinResponseTimeBo4);
        joinResponseTimeBoList.add(joinResponseTimeBo5);
        joinAgentStatBo.setJoinResponseTimeBoList(joinResponseTimeBoList);
        final List<JoinDataSourceListBo> joinDataSourceListBoList = new ArrayList<JoinDataSourceListBo>();
        List<JoinDataSourceBo> joinDataSourceBoList1 = new ArrayList<JoinDataSourceBo>();
        joinDataSourceBoList1.add(new JoinDataSourceBo(((short) (1000)), "jdbc:mysql", 300, 250, "agent_id_1", 600, "agent_id_6"));
        joinDataSourceBoList1.add(new JoinDataSourceBo(((short) (2000)), "jdbc:mssql", 400, 350, "agent_id_1", 700, "agent_id_6"));
        JoinDataSourceListBo joinDataSourceListBo1 = new JoinDataSourceListBo("agent1", joinDataSourceBoList1, 1498462565000L);
        List<JoinDataSourceBo> joinDataSourceBoList2 = new ArrayList<JoinDataSourceBo>();
        joinDataSourceBoList2.add(new JoinDataSourceBo(((short) (1000)), "jdbc:mysql", 200, 50, "agent_id_2", 700, "agent_id_7"));
        joinDataSourceBoList2.add(new JoinDataSourceBo(((short) (2000)), "jdbc:mssql", 300, 150, "agent_id_2", 800, "agent_id_7"));
        JoinDataSourceListBo joinDataSourceListBo2 = new JoinDataSourceListBo("agent1", joinDataSourceBoList2, 1498462570000L);
        List<JoinDataSourceBo> joinDataSourceBoList3 = new ArrayList<JoinDataSourceBo>();
        joinDataSourceBoList3.add(new JoinDataSourceBo(((short) (1000)), "jdbc:mysql", 500, 150, "agent_id_3", 900, "agent_id_8"));
        joinDataSourceBoList3.add(new JoinDataSourceBo(((short) (2000)), "jdbc:mssql", 600, 250, "agent_id_3", 1000, "agent_id_8"));
        JoinDataSourceListBo joinDataSourceListBo3 = new JoinDataSourceListBo("agent1", joinDataSourceBoList3, 1498462575000L);
        List<JoinDataSourceBo> joinDataSourceBoList4 = new ArrayList<JoinDataSourceBo>();
        joinDataSourceBoList4.add(new JoinDataSourceBo(((short) (1000)), "jdbc:mysql", 400, 550, "agent_id_4", 600, "agent_id_9"));
        joinDataSourceBoList4.add(new JoinDataSourceBo(((short) (2000)), "jdbc:mssql", 500, 650, "agent_id_4", 700, "agent_id_9"));
        JoinDataSourceListBo joinDataSourceListBo4 = new JoinDataSourceListBo("agent1", joinDataSourceBoList4, 1498462580000L);
        List<JoinDataSourceBo> joinDataSourceBoList5 = new ArrayList<JoinDataSourceBo>();
        joinDataSourceBoList5.add(new JoinDataSourceBo(((short) (1000)), "jdbc:mysql", 100, 750, "agent_id_5", 800, "agent_id_10"));
        joinDataSourceBoList5.add(new JoinDataSourceBo(((short) (2000)), "jdbc:mssql", 200, 850, "agent_id_5", 900, "agent_id_10"));
        JoinDataSourceListBo joinDataSourceListBo5 = new JoinDataSourceListBo("agent1", joinDataSourceBoList5, 1498462585000L);
        joinDataSourceListBoList.add(joinDataSourceListBo1);
        joinDataSourceListBoList.add(joinDataSourceListBo2);
        joinDataSourceListBoList.add(joinDataSourceListBo3);
        joinDataSourceListBoList.add(joinDataSourceListBo4);
        joinDataSourceListBoList.add(joinDataSourceListBo5);
        joinAgentStatBo.setJoinDataSourceListBoList(joinDataSourceListBoList);
        List<JoinFileDescriptorBo> joinFileDescriptorBoList = new ArrayList<JoinFileDescriptorBo>();
        JoinFileDescriptorBo joinFileDescriptorBo1 = new JoinFileDescriptorBo("agent1", 44, 70, "agent1", 30, "agent1", 1498462565000L);
        JoinFileDescriptorBo joinFileDescriptorBo2 = new JoinFileDescriptorBo("agent1", 33, 40, "agent1", 10, "agent1", 1498462570000L);
        JoinFileDescriptorBo joinFileDescriptorBo3 = new JoinFileDescriptorBo("agent1", 55, 60, "agent1", 7, "agent1", 1498462575000L);
        JoinFileDescriptorBo joinFileDescriptorBo4 = new JoinFileDescriptorBo("agent1", 11, 80, "agent1", 8, "agent1", 1498462580000L);
        JoinFileDescriptorBo joinFileDescriptorBo5 = new JoinFileDescriptorBo("agent1", 22, 70, "agent1", 12, "agent1", 1498462585000L);
        joinFileDescriptorBoList.add(joinFileDescriptorBo1);
        joinFileDescriptorBoList.add(joinFileDescriptorBo2);
        joinFileDescriptorBoList.add(joinFileDescriptorBo3);
        joinFileDescriptorBoList.add(joinFileDescriptorBo4);
        joinFileDescriptorBoList.add(joinFileDescriptorBo5);
        joinAgentStatBo.setJoinFileDescriptorBoList(joinFileDescriptorBoList);
        List<JoinDirectBufferBo> joinDirectBufferBoList = new ArrayList<JoinDirectBufferBo>();
        JoinDirectBufferBo joinDirectBufferBo1 = new JoinDirectBufferBo("agent1", 44, 70, "agent1", 30, "agent1", 44, 70, "agent1", 30, "agent1", 44, 70, "agent1", 30, "agent1", 44, 70, "agent1", 30, "agent1", 1498462565000L);
        JoinDirectBufferBo joinDirectBufferBo2 = new JoinDirectBufferBo("agent2", 33, 40, "agent2", 10, "agent2", 33, 40, "agent2", 10, "agent2", 33, 40, "agent2", 10, "agent2", 33, 40, "agent2", 10, "agent2", 1498462570000L);
        JoinDirectBufferBo joinDirectBufferBo3 = new JoinDirectBufferBo("agent3", 55, 60, "agent3", 7, "agent3", 55, 60, "agent3", 7, "agent3", 55, 60, "agent3", 7, "agent3", 55, 60, "agent3", 7, "agent3", 1498462575000L);
        JoinDirectBufferBo joinDirectBufferBo4 = new JoinDirectBufferBo("agent4", 11, 80, "agent4", 8, "agent4", 11, 80, "agent4", 8, "agent4", 11, 80, "agent4", 8, "agent4", 11, 80, "agent4", 8, "agent4", 1498462580000L);
        JoinDirectBufferBo joinDirectBufferBo5 = new JoinDirectBufferBo("agent5", 22, 70, "agent5", 12, "agent5", 22, 70, "agent5", 12, "agent5", 22, 70, "agent5", 12, "agent5", 22, 70, "agent5", 12, "agent5", 1498462585000L);
        joinDirectBufferBoList.add(joinDirectBufferBo1);
        joinDirectBufferBoList.add(joinDirectBufferBo2);
        joinDirectBufferBoList.add(joinDirectBufferBo3);
        joinDirectBufferBoList.add(joinDirectBufferBo4);
        joinDirectBufferBoList.add(joinDirectBufferBo5);
        joinAgentStatBo.setJoinDirectBufferBoList(joinDirectBufferBoList);
        List<JoinApplicationStatBo> joinApplicationStatBoList = JoinApplicationStatBo.createJoinApplicationStatBo("test_app", joinAgentStatBo, 60000);
        Assert.assertEquals(joinApplicationStatBoList.size(), 1);
        JoinApplicationStatBo joinApplicationStatBo = joinApplicationStatBoList.get(0);
        Assert.assertEquals(joinApplicationStatBo.getId(), "test_app");
        Assert.assertEquals(joinApplicationStatBo.getJoinCpuLoadBoList().size(), 5);
        Assert.assertEquals(joinApplicationStatBo.getJoinMemoryBoList().size(), 5);
        Assert.assertEquals(joinApplicationStatBo.getJoinTransactionBoList().size(), 5);
        Assert.assertEquals(joinApplicationStatBo.getJoinActiveTraceBoList().size(), 5);
        Assert.assertEquals(joinApplicationStatBo.getJoinResponseTimeBoList().size(), 5);
        Assert.assertEquals(joinApplicationStatBo.getJoinDataSourceListBoList().size(), 5);
        Assert.assertEquals(joinApplicationStatBo.getJoinFileDescriptorBoList().size(), 5);
    }

    @Test
    public void createJoinApplicationStatBo2Test() {
        JoinAgentStatBo joinAgentStatBo = new JoinAgentStatBo();
        joinAgentStatBo.setTimestamp(1498462545000L);
        List<JoinCpuLoadBo> joinCpuLoadBoList = new ArrayList<JoinCpuLoadBo>();
        JoinCpuLoadBo joinCpuLoadBo1 = new JoinCpuLoadBo("agent1", 44, 70, "agent1", 30, "agent1", 50, 60, "agent1", 33, "agent1", 1498462545000L);
        JoinCpuLoadBo joinCpuLoadBo2 = new JoinCpuLoadBo("agent1", 33, 40, "agent1", 10, "agent1", 20, 78, "agent1", 12, "agent1", 1498462550000L);
        JoinCpuLoadBo joinCpuLoadBo3 = new JoinCpuLoadBo("agent1", 55, 60, "agent1", 7, "agent1", 30, 39, "agent1", 30, "agent1", 1498462555000L);
        JoinCpuLoadBo joinCpuLoadBo4 = new JoinCpuLoadBo("agent1", 11, 80, "agent1", 8, "agent1", 10, 50, "agent1", 14, "agent1", 1498462560000L);
        JoinCpuLoadBo joinCpuLoadBo5 = new JoinCpuLoadBo("agent1", 22, 70, "agent1", 12, "agent1", 40, 99, "agent1", 50, "agent1", 1498462565000L);
        joinCpuLoadBoList.add(joinCpuLoadBo1);
        joinCpuLoadBoList.add(joinCpuLoadBo2);
        joinCpuLoadBoList.add(joinCpuLoadBo3);
        joinCpuLoadBoList.add(joinCpuLoadBo4);
        joinCpuLoadBoList.add(joinCpuLoadBo5);
        joinAgentStatBo.setJoinCpuLoadBoList(joinCpuLoadBoList);
        List<JoinMemoryBo> joinMemoryBoList = new ArrayList<JoinMemoryBo>();
        JoinMemoryBo joinMemoryBo1 = new JoinMemoryBo("agent1", 1498462545000L, 3000, 2000, 5000, "agent1", "agent1", 500, 50, 600, "agent1", "agent1");
        JoinMemoryBo joinMemoryBo2 = new JoinMemoryBo("agent1", 1498462550000L, 4000, 1000, 7000, "agent1", "agent1", 400, 150, 600, "agent1", "agent1");
        JoinMemoryBo joinMemoryBo3 = new JoinMemoryBo("agent1", 1498462555000L, 5000, 3000, 8000, "agent1", "agent1", 200, 100, 200, "agent1", "agent1");
        JoinMemoryBo joinMemoryBo4 = new JoinMemoryBo("agent1", 1498462560000L, 1000, 100, 3000, "agent1", "agent1", 100, 900, 1000, "agent1", "agent1");
        JoinMemoryBo joinMemoryBo5 = new JoinMemoryBo("agent1", 1498462565000L, 2000, 1000, 6000, "agent1", "agent1", 300, 100, 2900, "agent1", "agent1");
        joinMemoryBoList.add(joinMemoryBo1);
        joinMemoryBoList.add(joinMemoryBo2);
        joinMemoryBoList.add(joinMemoryBo3);
        joinMemoryBoList.add(joinMemoryBo4);
        joinMemoryBoList.add(joinMemoryBo5);
        joinAgentStatBo.setJoinMemoryBoList(joinMemoryBoList);
        List<JoinTransactionBo> joinTransactionBoList = new ArrayList<JoinTransactionBo>();
        JoinTransactionBo joinTransactionBo1 = new JoinTransactionBo("agent1", 5000, 150, 20, "agent1", 230, "agent1", 1498462545000L);
        JoinTransactionBo joinTransactionBo2 = new JoinTransactionBo("agent2", 5000, 300, 10, "agent2", 400, "agent1", 1498462550000L);
        JoinTransactionBo joinTransactionBo3 = new JoinTransactionBo("agent3", 5000, 30, 5, "agent3", 100, "agent3", 1498462555000L);
        JoinTransactionBo joinTransactionBo4 = new JoinTransactionBo("agent4", 5000, 30, 5, "agent4", 100, "agent4", 1498462560000L);
        JoinTransactionBo joinTransactionBo5 = new JoinTransactionBo("agent5", 5000, 30, 5, "agent5", 100, "agent5", 1498462565000L);
        joinTransactionBoList.add(joinTransactionBo1);
        joinTransactionBoList.add(joinTransactionBo2);
        joinTransactionBoList.add(joinTransactionBo3);
        joinTransactionBoList.add(joinTransactionBo4);
        joinTransactionBoList.add(joinTransactionBo5);
        joinAgentStatBo.setJoinTransactionBoList(joinTransactionBoList);
        List<JoinActiveTraceBo> JoinActiveTraceBoList = new ArrayList<JoinActiveTraceBo>();
        JoinActiveTraceBo joinActiveTraceBo1 = new JoinActiveTraceBo("agent1", 1, ((short) (2)), 30, 15, "app_1_1", 40, "app_1_2", 1498462545000L);
        JoinActiveTraceBo joinActiveTraceBo2 = new JoinActiveTraceBo("agent1", 1, ((short) (2)), 30, 15, "app_1_1", 40, "app_1_2", 1498462550000L);
        JoinActiveTraceBo joinActiveTraceBo3 = new JoinActiveTraceBo("agent1", 1, ((short) (2)), 30, 15, "app_1_1", 40, "app_1_2", 1498462555000L);
        JoinActiveTraceBo joinActiveTraceBo4 = new JoinActiveTraceBo("agent1", 1, ((short) (2)), 30, 15, "app_1_1", 40, "app_1_2", 1498462560000L);
        JoinActiveTraceBo joinActiveTraceBo5 = new JoinActiveTraceBo("agent1", 1, ((short) (2)), 30, 15, "app_1_1", 40, "app_1_2", 1498462565000L);
        JoinActiveTraceBoList.add(joinActiveTraceBo1);
        JoinActiveTraceBoList.add(joinActiveTraceBo2);
        JoinActiveTraceBoList.add(joinActiveTraceBo3);
        JoinActiveTraceBoList.add(joinActiveTraceBo4);
        JoinActiveTraceBoList.add(joinActiveTraceBo5);
        joinAgentStatBo.setJoinActiveTraceBoList(JoinActiveTraceBoList);
        List<JoinResponseTimeBo> joinResponseTimeBoList = new ArrayList<JoinResponseTimeBo>();
        JoinResponseTimeBo joinResponseTimeBo1 = new JoinResponseTimeBo("agent1", 1498462545000L, 3000, 2, "app_1_1", 6000, "app_1_2");
        JoinResponseTimeBo joinResponseTimeBo2 = new JoinResponseTimeBo("agent1", 1498462550000L, 4000, 200, "app_2_1", 9000, "app_2_2");
        JoinResponseTimeBo joinResponseTimeBo3 = new JoinResponseTimeBo("agent1", 1498462555000L, 2000, 20, "app_3_1", 7000, "app_3_2");
        JoinResponseTimeBo joinResponseTimeBo4 = new JoinResponseTimeBo("agent1", 1498462560000L, 5000, 20, "app_4_1", 8000, "app_4_2");
        JoinResponseTimeBo joinResponseTimeBo5 = new JoinResponseTimeBo("agent1", 1498462565000L, 1000, 10, "app_5_1", 6600, "app_5_2");
        joinResponseTimeBoList.add(joinResponseTimeBo1);
        joinResponseTimeBoList.add(joinResponseTimeBo2);
        joinResponseTimeBoList.add(joinResponseTimeBo3);
        joinResponseTimeBoList.add(joinResponseTimeBo4);
        joinResponseTimeBoList.add(joinResponseTimeBo5);
        joinAgentStatBo.setJoinResponseTimeBoList(joinResponseTimeBoList);
        final List<JoinDataSourceListBo> joinDataSourceListBoList = new ArrayList<JoinDataSourceListBo>();
        List<JoinDataSourceBo> joinDataSourceBoList1 = new ArrayList<JoinDataSourceBo>();
        joinDataSourceBoList1.add(new JoinDataSourceBo(((short) (1000)), "jdbc:mysql", 300, 250, "agent_id_1", 600, "agent_id_6"));
        joinDataSourceBoList1.add(new JoinDataSourceBo(((short) (2000)), "jdbc:mssql", 400, 350, "agent_id_1", 700, "agent_id_6"));
        JoinDataSourceListBo joinDataSourceListBo1 = new JoinDataSourceListBo("agent1", joinDataSourceBoList1, 1498462545000L);
        List<JoinDataSourceBo> joinDataSourceBoList2 = new ArrayList<JoinDataSourceBo>();
        joinDataSourceBoList2.add(new JoinDataSourceBo(((short) (1000)), "jdbc:mysql", 200, 50, "agent_id_2", 700, "agent_id_7"));
        joinDataSourceBoList2.add(new JoinDataSourceBo(((short) (2000)), "jdbc:mssql", 300, 150, "agent_id_2", 800, "agent_id_7"));
        JoinDataSourceListBo joinDataSourceListBo2 = new JoinDataSourceListBo("agent1", joinDataSourceBoList2, 1498462550000L);
        List<JoinDataSourceBo> joinDataSourceBoList3 = new ArrayList<JoinDataSourceBo>();
        joinDataSourceBoList3.add(new JoinDataSourceBo(((short) (1000)), "jdbc:mysql", 500, 150, "agent_id_3", 900, "agent_id_8"));
        joinDataSourceBoList3.add(new JoinDataSourceBo(((short) (2000)), "jdbc:mssql", 600, 250, "agent_id_3", 1000, "agent_id_8"));
        JoinDataSourceListBo joinDataSourceListBo3 = new JoinDataSourceListBo("agent1", joinDataSourceBoList3, 1498462555000L);
        List<JoinDataSourceBo> joinDataSourceBoList4 = new ArrayList<JoinDataSourceBo>();
        joinDataSourceBoList4.add(new JoinDataSourceBo(((short) (1000)), "jdbc:mysql", 400, 550, "agent_id_4", 600, "agent_id_9"));
        joinDataSourceBoList4.add(new JoinDataSourceBo(((short) (2000)), "jdbc:mssql", 500, 650, "agent_id_4", 700, "agent_id_9"));
        JoinDataSourceListBo joinDataSourceListBo4 = new JoinDataSourceListBo("agent1", joinDataSourceBoList4, 1498462560000L);
        List<JoinDataSourceBo> joinDataSourceBoList5 = new ArrayList<JoinDataSourceBo>();
        joinDataSourceBoList5.add(new JoinDataSourceBo(((short) (1000)), "jdbc:mysql", 100, 750, "agent_id_5", 800, "agent_id_10"));
        joinDataSourceBoList5.add(new JoinDataSourceBo(((short) (2000)), "jdbc:mssql", 200, 850, "agent_id_5", 900, "agent_id_10"));
        JoinDataSourceListBo joinDataSourceListBo5 = new JoinDataSourceListBo("agent1", joinDataSourceBoList5, 1498462565000L);
        joinDataSourceListBoList.add(joinDataSourceListBo1);
        joinDataSourceListBoList.add(joinDataSourceListBo2);
        joinDataSourceListBoList.add(joinDataSourceListBo3);
        joinDataSourceListBoList.add(joinDataSourceListBo4);
        joinDataSourceListBoList.add(joinDataSourceListBo5);
        joinAgentStatBo.setJoinDataSourceListBoList(joinDataSourceListBoList);
        List<JoinFileDescriptorBo> joinFileDescriptorBoList = new ArrayList<JoinFileDescriptorBo>();
        JoinFileDescriptorBo joinFileDescriptorBo1 = new JoinFileDescriptorBo("agent1", 44, 70, "agent1", 30, "agent1", 1498462545000L);
        JoinFileDescriptorBo joinFileDescriptorBo2 = new JoinFileDescriptorBo("agent1", 33, 40, "agent1", 10, "agent1", 1498462550000L);
        JoinFileDescriptorBo joinFileDescriptorBo3 = new JoinFileDescriptorBo("agent1", 55, 60, "agent1", 7, "agent1", 1498462555000L);
        JoinFileDescriptorBo joinFileDescriptorBo4 = new JoinFileDescriptorBo("agent1", 11, 80, "agent1", 8, "agent1", 1498462560000L);
        JoinFileDescriptorBo joinFileDescriptorBo5 = new JoinFileDescriptorBo("agent1", 22, 70, "agent1", 12, "agent1", 1498462565000L);
        joinFileDescriptorBoList.add(joinFileDescriptorBo1);
        joinFileDescriptorBoList.add(joinFileDescriptorBo2);
        joinFileDescriptorBoList.add(joinFileDescriptorBo3);
        joinFileDescriptorBoList.add(joinFileDescriptorBo4);
        joinFileDescriptorBoList.add(joinFileDescriptorBo5);
        joinAgentStatBo.setJoinFileDescriptorBoList(joinFileDescriptorBoList);
        List<JoinDirectBufferBo> joinDirectBufferBoList = new ArrayList<JoinDirectBufferBo>();
        JoinDirectBufferBo joinDirectBufferBo1 = new JoinDirectBufferBo("agent1", 44, 70, "agent1", 30, "agent1", 44, 70, "agent1", 30, "agent1", 44, 70, "agent1", 30, "agent1", 44, 70, "agent1", 30, "agent1", 1498462545000L);
        JoinDirectBufferBo joinDirectBufferBo2 = new JoinDirectBufferBo("agent1", 33, 40, "agent1", 10, "agent1", 33, 40, "agent1", 10, "agent1", 33, 40, "agent1", 10, "agent1", 33, 40, "agent1", 10, "agent1", 1498462550000L);
        JoinDirectBufferBo joinDirectBufferBo3 = new JoinDirectBufferBo("agent1", 55, 60, "agent1", 7, "agent1", 55, 60, "agent1", 7, "agent1", 55, 60, "agent1", 7, "agent1", 55, 60, "agent1", 7, "agent1", 1498462555000L);
        JoinDirectBufferBo joinDirectBufferBo4 = new JoinDirectBufferBo("agent1", 11, 80, "agent1", 8, "agent1", 11, 80, "agent1", 8, "agent1", 11, 80, "agent1", 8, "agent1", 11, 80, "agent1", 8, "agent1", 1498462560000L);
        JoinDirectBufferBo joinDirectBufferBo5 = new JoinDirectBufferBo("agent1", 22, 70, "agent1", 12, "agent1", 22, 70, "agent1", 12, "agent1", 22, 70, "agent1", 12, "agent1", 22, 70, "agent1", 12, "agent1", 1498462565000L);
        joinDirectBufferBoList.add(joinDirectBufferBo1);
        joinDirectBufferBoList.add(joinDirectBufferBo2);
        joinDirectBufferBoList.add(joinDirectBufferBo3);
        joinDirectBufferBoList.add(joinDirectBufferBo4);
        joinDirectBufferBoList.add(joinDirectBufferBo5);
        joinAgentStatBo.setJoinDirectBufferBoList(joinDirectBufferBoList);
        List<JoinApplicationStatBo> joinApplicationStatBoList = JoinApplicationStatBo.createJoinApplicationStatBo("test_app", joinAgentStatBo, 60000);
        Assert.assertEquals(joinApplicationStatBoList.size(), 2);
        for (JoinApplicationStatBo joinApplicationStatBo : joinApplicationStatBoList) {
            Assert.assertEquals(joinApplicationStatBo.getId(), "test_app");
            if ((joinApplicationStatBo.getTimestamp()) == 1498462560000L) {
                Assert.assertEquals(joinApplicationStatBo.getJoinCpuLoadBoList().size(), 2);
                Assert.assertEquals(joinApplicationStatBo.getJoinMemoryBoList().size(), 2);
                Assert.assertEquals(joinApplicationStatBo.getJoinTransactionBoList().size(), 2);
                Assert.assertEquals(joinApplicationStatBo.getJoinActiveTraceBoList().size(), 2);
                Assert.assertEquals(joinApplicationStatBo.getJoinResponseTimeBoList().size(), 2);
                Assert.assertEquals(joinApplicationStatBo.getJoinDataSourceListBoList().size(), 2);
                Assert.assertEquals(joinApplicationStatBo.getJoinFileDescriptorBoList().size(), 2);
                Assert.assertEquals(joinApplicationStatBo.getJoinDirectBufferBoList().size(), 2);
            } else
                if ((joinApplicationStatBo.getTimestamp()) == 1498462500000L) {
                    Assert.assertEquals(joinApplicationStatBo.getJoinCpuLoadBoList().size(), 3);
                    Assert.assertEquals(joinApplicationStatBo.getJoinMemoryBoList().size(), 3);
                    Assert.assertEquals(joinApplicationStatBo.getJoinTransactionBoList().size(), 3);
                    Assert.assertEquals(joinApplicationStatBo.getJoinActiveTraceBoList().size(), 3);
                    Assert.assertEquals(joinApplicationStatBo.getJoinResponseTimeBoList().size(), 3);
                    Assert.assertEquals(joinApplicationStatBo.getJoinDataSourceListBoList().size(), 3);
                    Assert.assertEquals(joinApplicationStatBo.getJoinFileDescriptorBoList().size(), 3);
                    Assert.assertEquals(joinApplicationStatBo.getJoinDirectBufferBoList().size(), 3);
                } else {
                    Assert.fail();
                }

        }
    }

    @Test
    public void createJoinApplicationStatBo3Test() {
        JoinAgentStatBo joinAgentStatBo = new JoinAgentStatBo();
        joinAgentStatBo.setTimestamp(1498462545000L);
        List<JoinCpuLoadBo> joinCpuLoadBoList = new ArrayList<JoinCpuLoadBo>();
        JoinCpuLoadBo joinCpuLoadBo1 = new JoinCpuLoadBo("agent1", 44, 70, "agent1", 30, "agent1", 50, 60, "agent1", 33, "agent1", 1498462545000L);
        JoinCpuLoadBo joinCpuLoadBo2 = new JoinCpuLoadBo("agent1", 33, 40, "agent1", 10, "agent1", 20, 78, "agent1", 12, "agent1", 1498462550000L);
        JoinCpuLoadBo joinCpuLoadBo3 = new JoinCpuLoadBo("agent1", 55, 60, "agent1", 7, "agent1", 30, 39, "agent1", 30, "agent1", 1498462555000L);
        JoinCpuLoadBo joinCpuLoadBo4 = new JoinCpuLoadBo("agent1", 11, 80, "agent1", 8, "agent1", 10, 50, "agent1", 14, "agent1", 1498462560000L);
        JoinCpuLoadBo joinCpuLoadBo5 = new JoinCpuLoadBo("agent1", 22, 70, "agent1", 12, "agent1", 40, 99, "agent1", 50, "agent1", 1498462565000L);
        joinCpuLoadBoList.add(joinCpuLoadBo1);
        joinCpuLoadBoList.add(joinCpuLoadBo2);
        joinCpuLoadBoList.add(joinCpuLoadBo3);
        joinCpuLoadBoList.add(joinCpuLoadBo4);
        joinCpuLoadBoList.add(joinCpuLoadBo5);
        joinAgentStatBo.setJoinCpuLoadBoList(joinCpuLoadBoList);
        List<JoinMemoryBo> joinMemoryBoList = new ArrayList<JoinMemoryBo>();
        JoinMemoryBo joinMemoryBo1 = new JoinMemoryBo("agent1", 1498462545000L, 3000, 2000, 5000, "agent1", "agent1", 500, 50, 600, "agent1", "agent1");
        JoinMemoryBo joinMemoryBo2 = new JoinMemoryBo("agent1", 1498462550000L, 4000, 1000, 7000, "agent1", "agent1", 400, 150, 600, "agent1", "agent1");
        JoinMemoryBo joinMemoryBo3 = new JoinMemoryBo("agent1", 1498462555000L, 5000, 3000, 8000, "agent1", "agent1", 200, 100, 200, "agent1", "agent1");
        JoinMemoryBo joinMemoryBo4 = new JoinMemoryBo("agent1", 1498462560000L, 1000, 100, 3000, "agent1", "agent1", 100, 900, 1000, "agent1", "agent1");
        JoinMemoryBo joinMemoryBo5 = new JoinMemoryBo("agent1", 1498462565000L, 2000, 1000, 6000, "agent1", "agent1", 300, 100, 2900, "agent1", "agent1");
        joinMemoryBoList.add(joinMemoryBo1);
        joinMemoryBoList.add(joinMemoryBo2);
        joinMemoryBoList.add(joinMemoryBo3);
        joinMemoryBoList.add(joinMemoryBo4);
        joinMemoryBoList.add(joinMemoryBo5);
        joinAgentStatBo.setJoinMemoryBoList(joinMemoryBoList);
        List<JoinTransactionBo> joinTransactionBoList = new ArrayList<JoinTransactionBo>();
        JoinTransactionBo joinTransactionBo1 = new JoinTransactionBo("agent1", 5000, 150, 20, "agent1", 230, "agent1", 1498462545000L);
        JoinTransactionBo joinTransactionBo2 = new JoinTransactionBo("agent2", 5000, 300, 10, "agent2", 400, "agent2", 1498462550000L);
        JoinTransactionBo joinTransactionBo3 = new JoinTransactionBo("agent3", 5000, 30, 5, "agent3", 100, "agent3", 1498462555000L);
        JoinTransactionBo joinTransactionBo4 = new JoinTransactionBo("agent4", 5000, 30, 5, "agent4", 100, "agent4", 1498462560000L);
        JoinTransactionBo joinTransactionBo5 = new JoinTransactionBo("agent5", 5000, 30, 5, "agent5", 100, "agent5", 1498462565000L);
        joinTransactionBoList.add(joinTransactionBo1);
        joinTransactionBoList.add(joinTransactionBo2);
        joinTransactionBoList.add(joinTransactionBo3);
        joinTransactionBoList.add(joinTransactionBo4);
        joinTransactionBoList.add(joinTransactionBo5);
        joinAgentStatBo.setJoinTransactionBoList(joinTransactionBoList);
        List<JoinActiveTraceBo> JoinActiveTraceBoList = new ArrayList<JoinActiveTraceBo>();
        JoinActiveTraceBo joinActiveTraceBo1 = new JoinActiveTraceBo("agent1", 1, ((short) (2)), 30, 15, "app_1_1", 40, "app_1_2", 1498462545000L);
        JoinActiveTraceBo joinActiveTraceBo2 = new JoinActiveTraceBo("agent1", 1, ((short) (2)), 30, 15, "app_1_1", 40, "app_1_2", 1498462550000L);
        JoinActiveTraceBo joinActiveTraceBo3 = new JoinActiveTraceBo("agent1", 1, ((short) (2)), 30, 15, "app_1_1", 40, "app_1_2", 1498462555000L);
        JoinActiveTraceBo joinActiveTraceBo4 = new JoinActiveTraceBo("agent1", 1, ((short) (2)), 30, 15, "app_1_1", 40, "app_1_2", 1498462560000L);
        JoinActiveTraceBo joinActiveTraceBo5 = new JoinActiveTraceBo("agent1", 1, ((short) (2)), 30, 15, "app_1_1", 40, "app_1_2", 1498462565000L);
        JoinActiveTraceBoList.add(joinActiveTraceBo1);
        JoinActiveTraceBoList.add(joinActiveTraceBo2);
        JoinActiveTraceBoList.add(joinActiveTraceBo3);
        JoinActiveTraceBoList.add(joinActiveTraceBo4);
        JoinActiveTraceBoList.add(joinActiveTraceBo5);
        joinAgentStatBo.setJoinActiveTraceBoList(JoinActiveTraceBoList);
        List<JoinResponseTimeBo> joinResponseTimeBoList = new ArrayList<JoinResponseTimeBo>();
        JoinResponseTimeBo joinResponseTimeBo1 = new JoinResponseTimeBo("agent1", 1498462545000L, 3000, 2, "app_1_1", 6000, "app_1_2");
        JoinResponseTimeBo joinResponseTimeBo2 = new JoinResponseTimeBo("agent1", 1498462550000L, 4000, 200, "app_2_1", 9000, "app_2_2");
        JoinResponseTimeBo joinResponseTimeBo3 = new JoinResponseTimeBo("agent1", 1498462555000L, 2000, 20, "app_3_1", 7000, "app_3_2");
        JoinResponseTimeBo joinResponseTimeBo4 = new JoinResponseTimeBo("agent1", 1498462560000L, 5000, 20, "app_4_1", 8000, "app_4_2");
        JoinResponseTimeBo joinResponseTimeBo5 = new JoinResponseTimeBo("agent1", 1498462565000L, 1000, 10, "app_5_1", 6600, "app_5_2");
        joinResponseTimeBoList.add(joinResponseTimeBo1);
        joinResponseTimeBoList.add(joinResponseTimeBo2);
        joinResponseTimeBoList.add(joinResponseTimeBo3);
        joinResponseTimeBoList.add(joinResponseTimeBo4);
        joinResponseTimeBoList.add(joinResponseTimeBo5);
        joinAgentStatBo.setJoinResponseTimeBoList(joinResponseTimeBoList);
        final List<JoinDataSourceListBo> joinDataSourceListBoList = new ArrayList<JoinDataSourceListBo>();
        List<JoinDataSourceBo> joinDataSourceBoList1 = new ArrayList<JoinDataSourceBo>();
        joinDataSourceBoList1.add(new JoinDataSourceBo(((short) (1000)), "jdbc:mysql", 300, 250, "agent_id_1", 600, "agent_id_6"));
        joinDataSourceBoList1.add(new JoinDataSourceBo(((short) (2000)), "jdbc:mssql", 400, 350, "agent_id_1", 700, "agent_id_6"));
        JoinDataSourceListBo joinDataSourceListBo1 = new JoinDataSourceListBo("agent1", joinDataSourceBoList1, 1498462545000L);
        List<JoinDataSourceBo> joinDataSourceBoList2 = new ArrayList<JoinDataSourceBo>();
        joinDataSourceBoList2.add(new JoinDataSourceBo(((short) (1000)), "jdbc:mysql", 200, 50, "agent_id_2", 700, "agent_id_7"));
        joinDataSourceBoList2.add(new JoinDataSourceBo(((short) (2000)), "jdbc:mssql", 300, 150, "agent_id_2", 800, "agent_id_7"));
        JoinDataSourceListBo joinDataSourceListBo2 = new JoinDataSourceListBo("agent1", joinDataSourceBoList2, 1498462550000L);
        List<JoinDataSourceBo> joinDataSourceBoList3 = new ArrayList<JoinDataSourceBo>();
        joinDataSourceBoList3.add(new JoinDataSourceBo(((short) (1000)), "jdbc:mysql", 500, 150, "agent_id_3", 900, "agent_id_8"));
        joinDataSourceBoList3.add(new JoinDataSourceBo(((short) (2000)), "jdbc:mssql", 600, 250, "agent_id_3", 1000, "agent_id_8"));
        JoinDataSourceListBo joinDataSourceListBo3 = new JoinDataSourceListBo("agent1", joinDataSourceBoList3, 1498462555000L);
        List<JoinDataSourceBo> joinDataSourceBoList4 = new ArrayList<JoinDataSourceBo>();
        joinDataSourceBoList4.add(new JoinDataSourceBo(((short) (1000)), "jdbc:mysql", 400, 550, "agent_id_4", 600, "agent_id_9"));
        joinDataSourceBoList4.add(new JoinDataSourceBo(((short) (2000)), "jdbc:mssql", 500, 650, "agent_id_4", 700, "agent_id_9"));
        JoinDataSourceListBo joinDataSourceListBo4 = new JoinDataSourceListBo("agent1", joinDataSourceBoList4, 1498462560000L);
        List<JoinDataSourceBo> joinDataSourceBoList5 = new ArrayList<JoinDataSourceBo>();
        joinDataSourceBoList5.add(new JoinDataSourceBo(((short) (1000)), "jdbc:mysql", 100, 750, "agent_id_5", 800, "agent_id_10"));
        joinDataSourceBoList5.add(new JoinDataSourceBo(((short) (2000)), "jdbc:mssql", 200, 850, "agent_id_5", 900, "agent_id_10"));
        JoinDataSourceListBo joinDataSourceListBo5 = new JoinDataSourceListBo("agent1", joinDataSourceBoList5, 1498462565000L);
        joinDataSourceListBoList.add(joinDataSourceListBo1);
        joinDataSourceListBoList.add(joinDataSourceListBo2);
        joinDataSourceListBoList.add(joinDataSourceListBo3);
        joinDataSourceListBoList.add(joinDataSourceListBo4);
        joinDataSourceListBoList.add(joinDataSourceListBo5);
        joinAgentStatBo.setJoinDataSourceListBoList(joinDataSourceListBoList);
        List<JoinFileDescriptorBo> joinFileDescriptorBoList = new ArrayList<JoinFileDescriptorBo>();
        JoinFileDescriptorBo joinFileDescriptorBo1 = new JoinFileDescriptorBo("agent1", 44, 70, "agent1", 30, "agent1", 1498462545000L);
        JoinFileDescriptorBo joinFileDescriptorBo2 = new JoinFileDescriptorBo("agent1", 33, 40, "agent1", 10, "agent1", 1498462550000L);
        JoinFileDescriptorBo joinFileDescriptorBo3 = new JoinFileDescriptorBo("agent1", 55, 60, "agent1", 7, "agent1", 1498462555000L);
        JoinFileDescriptorBo joinFileDescriptorBo4 = new JoinFileDescriptorBo("agent1", 11, 80, "agent1", 8, "agent1", 1498462560000L);
        JoinFileDescriptorBo joinFileDescriptorBo5 = new JoinFileDescriptorBo("agent1", 22, 70, "agent1", 12, "agent1", 1498462565000L);
        joinFileDescriptorBoList.add(joinFileDescriptorBo1);
        joinFileDescriptorBoList.add(joinFileDescriptorBo2);
        joinFileDescriptorBoList.add(joinFileDescriptorBo3);
        joinFileDescriptorBoList.add(joinFileDescriptorBo4);
        joinFileDescriptorBoList.add(joinFileDescriptorBo5);
        joinAgentStatBo.setJoinFileDescriptorBoList(joinFileDescriptorBoList);
        List<JoinDirectBufferBo> joinDirectBufferBoList = new ArrayList<JoinDirectBufferBo>();
        JoinDirectBufferBo joinDirectBufferBo1 = new JoinDirectBufferBo("agent1", 44, 70, "agent1", 30, "agent1", 44, 70, "agent1", 30, "agent1", 44, 70, "agent1", 30, "agent1", 44, 70, "agent1", 30, "agent1", 1498462545000L);
        JoinDirectBufferBo joinDirectBufferBo2 = new JoinDirectBufferBo("agent1", 33, 40, "agent1", 10, "agent1", 33, 40, "agent1", 10, "agent1", 33, 40, "agent1", 10, "agent1", 33, 40, "agent1", 10, "agent1", 1498462550000L);
        JoinDirectBufferBo joinDirectBufferBo3 = new JoinDirectBufferBo("agent1", 55, 60, "agent1", 7, "agent1", 55, 60, "agent1", 7, "agent1", 55, 60, "agent1", 7, "agent1", 55, 60, "agent1", 7, "agent1", 1498462555000L);
        JoinDirectBufferBo joinDirectBufferBo4 = new JoinDirectBufferBo("agent1", 11, 80, "agent1", 8, "agent1", 11, 80, "agent1", 8, "agent1", 11, 80, "agent1", 8, "agent1", 11, 80, "agent1", 8, "agent1", 1498462560000L);
        JoinDirectBufferBo joinDirectBufferBo5 = new JoinDirectBufferBo("agent1", 22, 70, "agent1", 12, "agent1", 22, 70, "agent1", 12, "agent1", 22, 70, "agent1", 12, "agent1", 22, 70, "agent1", 12, "agent1", 1498462565000L);
        joinDirectBufferBoList.add(joinDirectBufferBo1);
        joinDirectBufferBoList.add(joinDirectBufferBo2);
        joinDirectBufferBoList.add(joinDirectBufferBo3);
        joinDirectBufferBoList.add(joinDirectBufferBo4);
        joinDirectBufferBoList.add(joinDirectBufferBo5);
        joinAgentStatBo.setJoinDirectBufferBoList(joinDirectBufferBoList);
        List<JoinApplicationStatBo> joinApplicationStatBoList = JoinApplicationStatBo.createJoinApplicationStatBo("test_app", joinAgentStatBo, 10000);
        Assert.assertEquals(joinApplicationStatBoList.size(), 3);
        for (JoinApplicationStatBo joinApplicationStatBo : joinApplicationStatBoList) {
            Assert.assertEquals(joinApplicationStatBo.getId(), "test_app");
            if ((joinApplicationStatBo.getTimestamp()) == 1498462560000L) {
                Assert.assertEquals(joinApplicationStatBo.getJoinCpuLoadBoList().size(), 2);
                Assert.assertEquals(joinApplicationStatBo.getJoinMemoryBoList().size(), 2);
                Assert.assertEquals(joinApplicationStatBo.getJoinTransactionBoList().size(), 2);
                Assert.assertEquals(joinApplicationStatBo.getJoinActiveTraceBoList().size(), 2);
                Assert.assertEquals(joinApplicationStatBo.getJoinResponseTimeBoList().size(), 2);
                Assert.assertEquals(joinApplicationStatBo.getJoinDataSourceListBoList().size(), 2);
                Assert.assertEquals(joinApplicationStatBo.getJoinFileDescriptorBoList().size(), 2);
                Assert.assertEquals(joinApplicationStatBo.getJoinDirectBufferBoList().size(), 2);
            } else
                if ((joinApplicationStatBo.getTimestamp()) == 1498462540000L) {
                    Assert.assertEquals(joinApplicationStatBo.getJoinCpuLoadBoList().size(), 1);
                    Assert.assertEquals(joinApplicationStatBo.getJoinMemoryBoList().size(), 1);
                    Assert.assertEquals(joinApplicationStatBo.getJoinTransactionBoList().size(), 1);
                    Assert.assertEquals(joinApplicationStatBo.getJoinActiveTraceBoList().size(), 1);
                    Assert.assertEquals(joinApplicationStatBo.getJoinResponseTimeBoList().size(), 1);
                    Assert.assertEquals(joinApplicationStatBo.getJoinDataSourceListBoList().size(), 1);
                    Assert.assertEquals(joinApplicationStatBo.getJoinFileDescriptorBoList().size(), 1);
                    Assert.assertEquals(joinApplicationStatBo.getJoinDirectBufferBoList().size(), 1);
                } else
                    if ((joinApplicationStatBo.getTimestamp()) == 1498462550000L) {
                        Assert.assertEquals(joinApplicationStatBo.getJoinCpuLoadBoList().size(), 2);
                        Assert.assertEquals(joinApplicationStatBo.getJoinMemoryBoList().size(), 2);
                        Assert.assertEquals(joinApplicationStatBo.getJoinTransactionBoList().size(), 2);
                        Assert.assertEquals(joinApplicationStatBo.getJoinActiveTraceBoList().size(), 2);
                        Assert.assertEquals(joinApplicationStatBo.getJoinResponseTimeBoList().size(), 2);
                        Assert.assertEquals(joinApplicationStatBo.getJoinDataSourceListBoList().size(), 2);
                        Assert.assertEquals(joinApplicationStatBo.getJoinFileDescriptorBoList().size(), 2);
                        Assert.assertEquals(joinApplicationStatBo.getJoinDirectBufferBoList().size(), 2);
                    } else {
                        Assert.fail();
                    }


        }
    }
}

