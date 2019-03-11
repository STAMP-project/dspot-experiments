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
package org.apache.flink.runtime.rest.messages;


import JobVertexBackPressureInfo.VertexBackPressureLevel.HIGH;
import JobVertexBackPressureInfo.VertexBackPressureLevel.LOW;
import JobVertexBackPressureInfo.VertexBackPressureLevel.OK;
import org.apache.flink.runtime.rest.util.RestMapperUtils;
import org.apache.flink.util.TestLogger;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link JobVertexBackPressureInfo.VertexBackPressureLevel}.
 */
public class VertexBackPressureLevelTest extends TestLogger {
    /**
     * Tests that the enum values are serialized correctly.
     * Clients, such as the Web UI, expect values to be lower case.
     */
    @Test
    public void testJsonValue() throws Exception {
        Assert.assertEquals("\"ok\"", RestMapperUtils.getStrictObjectMapper().writeValueAsString(OK));
        Assert.assertEquals("\"low\"", RestMapperUtils.getStrictObjectMapper().writeValueAsString(LOW));
        Assert.assertEquals("\"high\"", RestMapperUtils.getStrictObjectMapper().writeValueAsString(HIGH));
    }
}

