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
package org.apache.zookeeper.test;


import org.apache.zookeeper.ZKTestCase;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Tests learners configured to use local sessions only. Expected
 * behavior is that sessions created on the learner will never be
 * made global.  Operations requiring a global session (e.g.
 * creation of ephemeral nodes) will fail with an error.
 */
public class LocalSessionsOnlyTest extends ZKTestCase {
    protected static final Logger LOG = LoggerFactory.getLogger(LocalSessionsOnlyTest.class);

    public static final int CONNECTION_TIMEOUT = ClientBase.CONNECTION_TIMEOUT;

    private final QuorumBase qb = new QuorumBase();

    @Test
    public void testLocalSessionsOnFollower() throws Exception {
        testLocalSessions(false);
    }

    @Test
    public void testLocalSessionsOnLeader() throws Exception {
        testLocalSessions(true);
    }
}

