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
 * Tests load balancing at client side.
 */
public class ServerClusterRemoteInsertBalancedIT extends AbstractServerClusterTest {
    private static final int ITERATIONS = 10;

    @Test
    public void test() throws Exception {
        init(2);
        prepare(false);
        execute();
    }
}

