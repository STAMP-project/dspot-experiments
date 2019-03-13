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
package com.navercorp.pinpoint.web.vo.stat.chart.application;


import ApplicationCpuLoadChart.ApplicationCpuLoadChartGroup.CpuLoadChartType.CPU_LOAD_JVM;
import ApplicationCpuLoadChart.ApplicationCpuLoadChartGroup.CpuLoadChartType.CPU_LOAD_SYSTEM;
import StatChartGroup.ChartType;
import com.navercorp.pinpoint.web.util.TimeWindow;
import com.navercorp.pinpoint.web.vo.Range;
import com.navercorp.pinpoint.web.vo.chart.Chart;
import com.navercorp.pinpoint.web.vo.chart.Point;
import com.navercorp.pinpoint.web.vo.stat.AggreJoinCpuLoadBo;
import com.navercorp.pinpoint.web.vo.stat.chart.StatChartGroup;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author minwoo.jung
 */
public class ApplicationCpuLoadChartGroupTest {
    @Test
    public void createApplicationCpuLoadChartGroupTest() throws Exception {
        long time = 1495418083250L;
        Range range = new Range((time - 240000), time);
        TimeWindow timeWindow = new TimeWindow(range);
        List<AggreJoinCpuLoadBo> aggreCpuLoadList = new ArrayList<>(5);
        AggreJoinCpuLoadBo aggreJoinCpuLoadBo1 = new AggreJoinCpuLoadBo("testApp", 0.11, 0.6, "agent1_1", 0.2, "agent1_2", 0.1, 0.6, "agent1_3", 0.47, "agent1_4", time);
        AggreJoinCpuLoadBo aggreJoinCpuLoadBo2 = new AggreJoinCpuLoadBo("testApp", 0.22, 0.52, "agent2_1", 0.1, "agent2_2", 0.2, 0.7, "agent2_3", 0.24, "agent2_4", (time - 60000));
        AggreJoinCpuLoadBo aggreJoinCpuLoadBo3 = new AggreJoinCpuLoadBo("testApp", 0.33, 0.39, "agent3_1", 0.9, "agent3_2", 0.3, 0.85, "agent3_3", 0.33, "agent3_4", (time - 120000));
        AggreJoinCpuLoadBo aggreJoinCpuLoadBo4 = new AggreJoinCpuLoadBo("testApp", 0.44, 0.42, "agent4_1", 0.25, "agent4_2", 0.4, 0.58, "agent4_3", 0.56, "agent4_4", (time - 180000));
        AggreJoinCpuLoadBo aggreJoinCpuLoadBo5 = new AggreJoinCpuLoadBo("testApp", 0.55, 0.55, "agent5_1", 0.54, "agent5_2", 0.5, 0.86, "agent5_3", 0.76, "agent5_4", (time - 240000));
        aggreCpuLoadList.add(aggreJoinCpuLoadBo1);
        aggreCpuLoadList.add(aggreJoinCpuLoadBo2);
        aggreCpuLoadList.add(aggreJoinCpuLoadBo3);
        aggreCpuLoadList.add(aggreJoinCpuLoadBo4);
        aggreCpuLoadList.add(aggreJoinCpuLoadBo5);
        StatChartGroup applicationCpuLoadChartGroup = new ApplicationCpuLoadChart.ApplicationCpuLoadChartGroup(timeWindow, aggreCpuLoadList);
        Map<StatChartGroup.ChartType, Chart<? extends Point>> charts = applicationCpuLoadChartGroup.getCharts();
        Assert.assertEquals(2, charts.size());
        Chart jvmCpuLodChart = charts.get(CPU_LOAD_JVM);
        List<Point> jvmCpuLoadPoints = jvmCpuLodChart.getPoints();
        Assert.assertEquals(5, jvmCpuLoadPoints.size());
        int index = jvmCpuLoadPoints.size();
        for (Point point : jvmCpuLoadPoints) {
            testJvmCpuLoad(((CpuLoadPoint) (point)), aggreCpuLoadList.get((--index)));
        }
        Chart sysCpuLoadChart = charts.get(CPU_LOAD_SYSTEM);
        List<Point> sysCpuLoadPoints = sysCpuLoadChart.getPoints();
        Assert.assertEquals(5, sysCpuLoadPoints.size());
        index = sysCpuLoadPoints.size();
        for (Point point : sysCpuLoadPoints) {
            testSysCpuLoad(((CpuLoadPoint) (point)), aggreCpuLoadList.get((--index)));
        }
    }
}

