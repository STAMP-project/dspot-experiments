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
package org.apache.druid.server.router;


import java.util.Collections;
import org.apache.druid.client.selector.Server;
import org.apache.druid.java.util.common.Intervals;
import org.apache.druid.query.TableDataSource;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 */
public class QueryHostFinderTest {
    private TieredBrokerHostSelector brokerSelector;

    private Server server;

    @Test
    public void testFindServer() {
        QueryHostFinder queryRunner = new QueryHostFinder(brokerSelector, new RendezvousHashAvaticaConnectionBalancer());
        Server server = queryRunner.findServer(new org.apache.druid.query.timeboundary.TimeBoundaryQuery(new TableDataSource("test"), new org.apache.druid.query.spec.MultipleIntervalSegmentSpec(Collections.singletonList(Intervals.of("2011-08-31/2011-09-01"))), null, null, null));
        Assert.assertEquals("foo", server.getHost());
    }
}

