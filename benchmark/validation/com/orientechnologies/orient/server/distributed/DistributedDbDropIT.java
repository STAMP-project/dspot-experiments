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


import org.junit.Test;


/**
 * Distributed test on drop database.
 */
public class DistributedDbDropIT extends AbstractServerClusterTxTest {
    static final int SERVERS = 3;

    int serverStarted = 0;

    @Test
    public void test() throws Exception {
        count = 10;
        init(DistributedDbDropIT.SERVERS);
        prepare(false);
        execute();
    }
}

