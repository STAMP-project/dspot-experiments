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


import java.io.File;
import java.util.Properties;
import org.apache.zookeeper.server.quorum.QuorumPeer;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class HierarchicalQuorumTest extends ClientBase {
    private static final Logger LOG = LoggerFactory.getLogger(QuorumBase.class);

    File s1dir;

    File s2dir;

    File s3dir;

    File s4dir;

    File s5dir;

    QuorumPeer s1;

    QuorumPeer s2;

    QuorumPeer s3;

    QuorumPeer s4;

    QuorumPeer s5;

    protected int port1;

    protected int port2;

    protected int port3;

    protected int port4;

    protected int port5;

    protected int leport1;

    protected int leport2;

    protected int leport3;

    protected int leport4;

    protected int leport5;

    protected int clientport1;

    protected int clientport2;

    protected int clientport3;

    protected int clientport4;

    protected int clientport5;

    Properties qp;

    protected final ClientHammerTest cht = new ClientHammerTest();

    @Test
    public void testHierarchicalQuorum() throws Throwable {
        cht.runHammer(5, 10);
    }
}

