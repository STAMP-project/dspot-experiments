/**
 * Licensed to Crate under one or more contributor license agreements.
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.  Crate licenses this file
 * to you under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * However, if you have executed another commercial license agreement
 * with Crate these terms will supersede the license and you may use the
 * software solely pursuant to the terms of the relevant commercial
 * agreement.
 */
package io.crate.plugin;


import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class PipelineRegistryTest {
    @Test
    public void testAddSortedToPipline() throws Exception {
        PipelineRegistry pipelineRegistry = new PipelineRegistry();
        pipelineRegistry.addBefore(PipelineRegistryTest.channelPipelineItem("c", "d"));
        pipelineRegistry.addBefore(PipelineRegistryTest.channelPipelineItem("a", "b"));
        pipelineRegistry.addBefore(PipelineRegistryTest.channelPipelineItem("d", "e"));
        pipelineRegistry.addBefore(PipelineRegistryTest.channelPipelineItem("b", "c"));
        int size = pipelineRegistry.addBeforeList().size();
        Assert.assertThat(size, Matchers.is(4));
        String[] names = new String[pipelineRegistry.addBeforeList().size()];
        for (int i = 0; i < size; i++) {
            names[i] = pipelineRegistry.addBeforeList().get(i).name;
        }
        Assert.assertThat(names, Matchers.is(new String[]{ "b", "c", "d", "e" }));
    }
}

