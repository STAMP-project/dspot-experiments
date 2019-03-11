/**
 * *  Copyright 2010-2016 OrientDB LTD (http://orientdb.com)
 *  *
 *  *  Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  *  You may obtain a copy of the License at
 *  *
 *  *       http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  *  Unless required by applicable law or agreed to in writing, software
 *  *  distributed under the License is distributed on an "AS IS" BASIS,
 *  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *  See the License for the specific language governing permissions and
 *  *  limitations under the License.
 *  *
 *  * For more information: http://orientdb.com
 */
package com.orientechnologies.orient.server.distributed;


import com.orientechnologies.common.util.OPair;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.Test;


/**
 * Tests the behavior of hooks in distributed configuration.
 */
public class DistributedLifecycleListenerIT extends AbstractServerClusterTest implements ODistributedLifecycleListener {
    private static final int SERVERS = 2;

    private final AtomicLong beforeNodeJoin = new AtomicLong();

    private final AtomicLong afterNodeJoin = new AtomicLong();

    private final AtomicLong nodeLeft = new AtomicLong();

    private final List<OPair<String, ODistributedServerManager.DB_STATUS>> changeStatus = Collections.synchronizedList(new ArrayList<OPair<String, ODistributedServerManager.DB_STATUS>>());

    @Test
    public void test() throws Exception {
        this.startupNodesInSequence = true;
        this.terminateAtShutdown = false;
        init(DistributedLifecycleListenerIT.SERVERS);
        prepare(false);
        execute();
    }
}

