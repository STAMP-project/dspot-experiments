/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.atlas;


import java.util.Map;
import org.apache.atlas.model.instance.AtlasObjectId;
import org.apache.nifi.controller.status.ProcessGroupStatus;
import org.apache.nifi.controller.status.ProcessorStatus;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test {@link NiFiFlowAnalyzer} with simple mock code.
 * More complex and detailed tests are available in {@link ITReportLineageToAtlas}.
 */
public class TestNiFiFlowAnalyzer {
    private int componentId = 0;

    @Test
    public void testEmptyFlow() throws Exception {
        ProcessGroupStatus rootPG = createEmptyProcessGroupStatus();
        final NiFiFlowAnalyzer analyzer = new NiFiFlowAnalyzer();
        final NiFiFlow nifiFlow = new NiFiFlow(rootPG.getId());
        nifiFlow.setClusterName("cluster1");
        analyzer.analyzeProcessGroup(nifiFlow, rootPG);
        Assert.assertEquals("1234-5678-0000-0000@cluster1", nifiFlow.getQualifiedName());
    }

    @Test
    public void testSingleProcessor() throws Exception {
        ProcessGroupStatus rootPG = createEmptyProcessGroupStatus();
        final ProcessorStatus pr0 = createProcessor(rootPG, "GenerateFlowFile");
        final NiFiFlowAnalyzer analyzer = new NiFiFlowAnalyzer();
        final NiFiFlow nifiFlow = new NiFiFlow(rootPG.getId());
        analyzer.analyzeProcessGroup(nifiFlow, rootPG);
        Assert.assertEquals(1, nifiFlow.getProcessors().size());
        analyzer.analyzePaths(nifiFlow);
        final Map<String, NiFiFlowPath> paths = nifiFlow.getFlowPaths();
        Assert.assertEquals(1, paths.size());
        // first path
        final NiFiFlowPath path0 = paths.get(pr0.getId());
        Assert.assertEquals(path0.getId(), path0.getProcessComponentIds().get(0));
        Assert.assertEquals(rootPG.getId(), path0.getGroupId());
        // Should be able to find a path from a given processor GUID.
        final NiFiFlowPath pathForPr0 = nifiFlow.findPath(pr0.getId());
        Assert.assertEquals(path0, pathForPr0);
    }

    @Test
    public void testProcessorsWithinSinglePath() throws Exception {
        ProcessGroupStatus rootPG = createEmptyProcessGroupStatus();
        final ProcessorStatus pr0 = createProcessor(rootPG, "GenerateFlowFile");
        final ProcessorStatus pr1 = createProcessor(rootPG, "UpdateAttribute");
        connect(rootPG, pr0, pr1);
        final NiFiFlowAnalyzer analyzer = new NiFiFlowAnalyzer();
        final NiFiFlow nifiFlow = new NiFiFlow(rootPG.getId());
        analyzer.analyzeProcessGroup(nifiFlow, rootPG);
        Assert.assertEquals(2, nifiFlow.getProcessors().size());
        analyzer.analyzePaths(nifiFlow);
        final Map<String, NiFiFlowPath> paths = nifiFlow.getFlowPaths();
        Assert.assertEquals(1, paths.size());
        // Should be able to find a path from a given processor GUID.
        final NiFiFlowPath pathForPr0 = nifiFlow.findPath(pr0.getId());
        final NiFiFlowPath pathForPr1 = nifiFlow.findPath(pr1.getId());
        final NiFiFlowPath path0 = paths.get(pr0.getId());
        Assert.assertEquals(path0, pathForPr0);
        Assert.assertEquals(path0, pathForPr1);
    }

    @Test
    public void testMultiPaths() throws Exception {
        ProcessGroupStatus rootPG = createEmptyProcessGroupStatus();
        final ProcessorStatus pr0 = createProcessor(rootPG, "GenerateFlowFile");
        final ProcessorStatus pr1 = createProcessor(rootPG, "UpdateAttribute");
        final ProcessorStatus pr2 = createProcessor(rootPG, "ListenTCP");
        final ProcessorStatus pr3 = createProcessor(rootPG, "LogAttribute");
        connect(rootPG, pr0, pr1);
        connect(rootPG, pr2, pr3);
        final NiFiFlowAnalyzer analyzer = new NiFiFlowAnalyzer();
        final NiFiFlow nifiFlow = new NiFiFlow(rootPG.getId());
        analyzer.analyzeProcessGroup(nifiFlow, rootPG);
        Assert.assertEquals(4, nifiFlow.getProcessors().size());
        analyzer.analyzePaths(nifiFlow);
        final Map<String, NiFiFlowPath> paths = nifiFlow.getFlowPaths();
        Assert.assertEquals(2, paths.size());
        // Order is not guaranteed
        final NiFiFlowPath pathA = paths.get(pr0.getId());
        final NiFiFlowPath pathB = paths.get(pr2.getId());
        Assert.assertEquals(2, pathA.getProcessComponentIds().size());
        Assert.assertEquals(2, pathB.getProcessComponentIds().size());
        // Should be able to find a path from a given processor GUID.
        final NiFiFlowPath pathForPr0 = nifiFlow.findPath(pr0.getId());
        final NiFiFlowPath pathForPr1 = nifiFlow.findPath(pr1.getId());
        final NiFiFlowPath pathForPr2 = nifiFlow.findPath(pr2.getId());
        final NiFiFlowPath pathForPr3 = nifiFlow.findPath(pr3.getId());
        Assert.assertEquals(pathA, pathForPr0);
        Assert.assertEquals(pathA, pathForPr1);
        Assert.assertEquals(pathB, pathForPr2);
        Assert.assertEquals(pathB, pathForPr3);
    }

    @Test
    public void testMultiPathsJoint() throws Exception {
        ProcessGroupStatus rootPG = createEmptyProcessGroupStatus();
        final ProcessorStatus pr0 = createProcessor(rootPG, "org.apache.nifi.processors.standard.GenerateFlowFile");
        final ProcessorStatus pr1 = createProcessor(rootPG, "org.apache.nifi.processors.standard.UpdateAttribute");
        final ProcessorStatus pr2 = createProcessor(rootPG, "org.apache.nifi.processors.standard.ListenTCP");
        final ProcessorStatus pr3 = createProcessor(rootPG, "org.apache.nifi.processors.standard.LogAttribute");
        // Result should be as follows:
        // pathA = 0 -> 1 (-> 3)
        // pathB = 2 (-> 3)
        // pathC = 3
        connect(rootPG, pr0, pr1);
        connect(rootPG, pr1, pr3);
        connect(rootPG, pr2, pr3);
        final NiFiFlowAnalyzer analyzer = new NiFiFlowAnalyzer();
        final NiFiFlow nifiFlow = new NiFiFlow(rootPG.getId());
        nifiFlow.setClusterName("cluster1");
        analyzer.analyzeProcessGroup(nifiFlow, rootPG);
        Assert.assertEquals(4, nifiFlow.getProcessors().size());
        analyzer.analyzePaths(nifiFlow);
        final Map<String, NiFiFlowPath> paths = nifiFlow.getFlowPaths();
        Assert.assertEquals(3, paths.size());
        // Order is not guaranteed
        final NiFiFlowPath pathA = paths.get(pr0.getId());
        final NiFiFlowPath pathB = paths.get(pr2.getId());
        final NiFiFlowPath pathC = paths.get(pr3.getId());
        Assert.assertEquals(2, pathA.getProcessComponentIds().size());
        Assert.assertEquals(1, pathB.getProcessComponentIds().size());
        Assert.assertEquals(1, pathC.getProcessComponentIds().size());
        // A queue is added as input for the joint point.
        Assert.assertEquals(1, pathC.getInputs().size());
        final AtlasObjectId queue = pathC.getInputs().iterator().next();
        Assert.assertEquals(NiFiTypes.TYPE_NIFI_QUEUE, queue.getTypeName());
        Assert.assertEquals(AtlasUtils.toQualifiedName("cluster1", pathC.getId()), queue.getUniqueAttributes().get(NiFiTypes.ATTR_QUALIFIED_NAME));
        // Should be able to find a path from a given processor GUID.
        final NiFiFlowPath pathForPr0 = nifiFlow.findPath(pr0.getId());
        final NiFiFlowPath pathForPr1 = nifiFlow.findPath(pr1.getId());
        final NiFiFlowPath pathForPr2 = nifiFlow.findPath(pr2.getId());
        final NiFiFlowPath pathForPr3 = nifiFlow.findPath(pr3.getId());
        Assert.assertEquals(pathA, pathForPr0);
        Assert.assertEquals(pathA, pathForPr1);
        Assert.assertEquals(pathB, pathForPr2);
        Assert.assertEquals(pathC, pathForPr3);
    }
}

