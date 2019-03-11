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
package org.apache.hadoop.ozone.scm.pipeline;


import java.util.Map;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.openmbean.TabularData;
import org.apache.hadoop.ozone.MiniOzoneCluster;
import org.junit.Test;


/**
 * Test cases to verify the metrics exposed by SCMPipelineManager via MXBean.
 */
public class TestPipelineManagerMXBean {
    private MiniOzoneCluster cluster;

    private static MBeanServer mbs;

    /**
     * Verifies SCMPipelineManagerInfo metrics.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testPipelineInfo() throws Exception {
        ObjectName bean = new ObjectName("Hadoop:service=SCMPipelineManager,name=SCMPipelineManagerInfo");
        TabularData data = ((TabularData) (TestPipelineManagerMXBean.mbs.getAttribute(bean, "PipelineInfo")));
        Map<String, Integer> datanodeInfo = cluster.getStorageContainerManager().getPipelineManager().getPipelineInfo();
        verifyEquals(data, datanodeInfo);
    }
}

