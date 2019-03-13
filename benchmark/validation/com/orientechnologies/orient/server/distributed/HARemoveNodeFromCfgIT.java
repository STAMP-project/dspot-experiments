/**
 * Copyright 2010-2016 OrientDB LTD (http://orientdb.com)
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


import OGlobalConfiguration.DISTRIBUTED_AUTO_REMOVE_OFFLINE_SERVERS;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Test;


/**
 * Distributed TX test against "plocal" protocol + shutdown and restart of a node.
 */
public class HARemoveNodeFromCfgIT extends AbstractServerClusterTxTest {
    static final int SERVERS = 3;

    private AtomicBoolean lastNodeIsUp = new AtomicBoolean(true);

    @Test
    public void test() throws Exception {
        DISTRIBUTED_AUTO_REMOVE_OFFLINE_SERVERS.setValue(100);
        try {
            useTransactions = true;
            count = 10;
            init(HARemoveNodeFromCfgIT.SERVERS);
            prepare(false);
            execute();
        } finally {
            DISTRIBUTED_AUTO_REMOVE_OFFLINE_SERVERS.setValue(100);
        }
    }
}

