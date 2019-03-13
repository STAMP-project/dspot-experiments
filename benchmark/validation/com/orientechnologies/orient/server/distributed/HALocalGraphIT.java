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


import com.orientechnologies.orient.core.db.ODatabasePool;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.Test;


/**
 * Test case to check the right management of distributed exception while a server is starting. Derived from the test provided by
 * Gino John for issue http://www.prjhub.com/#/issues/6449.
 * <p>
 * 3 nodes, the test is started after the 1st node is up & running. The test is composed by multiple (8) parallel threads that
 * update the same records 20,000 times.
 *
 * @author Luca Garulli (l.garulli--(at)--orientdb.com)
 */
public class HALocalGraphIT extends AbstractServerClusterTxTest {
    protected static final int SERVERS = 3;

    protected static final int CONCURRENCY_LEVEL = 4;

    protected static final int TOTAL_CYCLES_PER_THREAD = 300000;

    protected final AtomicBoolean serverDown = new AtomicBoolean(false);

    protected final AtomicBoolean serverRestarting = new AtomicBoolean(false);

    protected final AtomicBoolean serverRestarted = new AtomicBoolean(false);

    protected ODatabasePool graphReadFactory;

    protected ExecutorService executorService;

    protected int serverStarted = 0;

    protected AtomicLong operations = new AtomicLong();

    List<Future<?>> ths = new ArrayList<Future<?>>();

    private TimerTask task;

    private volatile long sleep = 0;

    @Test
    public void test() throws Exception {
        useTransactions = false;
        init(HALocalGraphIT.SERVERS);
        prepare(false);
        execute();
    }
}

