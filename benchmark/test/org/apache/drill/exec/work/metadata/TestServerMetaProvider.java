/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.drill.exec.work.metadata;


import Quoting.BACK_TICK.string;
import RequestStatus.OK;
import org.apache.drill.exec.proto.UserProtos.GetServerMetaResp;
import org.apache.drill.test.ClusterTest;
import org.junit.Assert;
import org.junit.Test;


public class TestServerMetaProvider extends ClusterTest {
    @Test
    public void testServerMeta() throws Exception {
        GetServerMetaResp response = ClusterTest.client.client().getServerMeta().get();
        Assert.assertNotNull(response);
        Assert.assertEquals(OK, response.getStatus());
        Assert.assertNotNull(response.getServerMeta());
        Assert.assertEquals(string, response.getServerMeta().getIdentifierQuoteString());
    }

    @Test
    public void testCurrentSchema() throws Exception {
        GetServerMetaResp response = ClusterTest.client.client().getServerMeta().get();
        Assert.assertEquals(OK, response.getStatus());
        Assert.assertEquals("", response.getServerMeta().getCurrentSchema());
        queryBuilder().sql("use dfs.tmp").run();
        response = ClusterTest.client.client().getServerMeta().get();
        Assert.assertEquals(OK, response.getStatus());
        Assert.assertEquals("dfs.tmp", response.getServerMeta().getCurrentSchema());
    }
}

