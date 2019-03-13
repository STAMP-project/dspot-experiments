/**
 * Copyright 2010-2013 Luca Garulli (l.garulli--at--orientechnologies.com)
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
package com.orientechnologies.orient.server.distributed;


import org.junit.Test;


/**
 * Distributed test with 3 servers running and after a while the server 2 is isolated from the network (using a proxy) and then it
 * re-merges the cluster again.
 */
public class SplitBraiNetworkIT extends AbstractHARemoveNode {
    static final int SERVERS = 3;

    @Test
    public void test() throws Exception {
        useTransactions = false;
        count = 10;
        startupNodesInSequence = true;
        init(SplitBraiNetworkIT.SERVERS);
        prepare(false);
        execute();
    }
}

