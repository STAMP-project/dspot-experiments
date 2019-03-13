/**
 * Copyright 2017 LinkedIn Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package azkaban.flow;


import azkaban.executor.ExecutableFlow;
import azkaban.project.Project;
import azkaban.utils.Props;
import org.junit.Assert;
import org.junit.Test;


public class FlowUtilsTest {
    private Project project;

    private Props props;

    @Test
    public void testGetFlow() throws Exception {
        final Flow flow = FlowUtils.getFlow(this.project, "jobe");
        Assert.assertEquals(flow.getId(), "jobe");
        assertThatThrownBy(() -> FlowUtils.getFlow(this.project, "nonexisting")).isInstanceOf(RuntimeException.class);
    }

    @Test
    public void testCreateExecutableFlow() throws Exception {
        final Flow flow = FlowUtils.getFlow(this.project, "jobe");
        final ExecutableFlow exFlow = FlowUtils.createExecutableFlow(this.project, flow);
        Assert.assertEquals(exFlow.getProjectId(), this.project.getId());
        Assert.assertEquals(exFlow.getFlowId(), flow.getId());
        Assert.assertEquals(exFlow.getProxyUsers(), this.project.getProxyUsers());
    }
}

