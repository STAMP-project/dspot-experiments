package com.orientechnologies.orient.server.distributed;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.Test;


/* *  Copyright 2015 Orient Technologies LTD (info(at)orientechnologies.com)
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 * For more information: http://www.orientechnologies.com
 */
/**
 *
 *
 * @author Enrico Risa
 */
public class HaSyncClusterIT extends AbstractServerClusterTest {
    private static final int SERVERS = 2;

    public static final int NUM_RECORDS = 1000;

    ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Test
    public void test() throws Exception {
        init(HaSyncClusterIT.SERVERS);
        prepare(false);
        execute();
    }
}

