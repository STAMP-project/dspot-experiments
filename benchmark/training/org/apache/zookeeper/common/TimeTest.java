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
package org.apache.zookeeper.common;


import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.zookeeper.test.ClientBase;
import org.junit.Assert;
import org.junit.Test;


/**
 * Command line program for demonstrating robustness to clock
 * changes.
 * <p/>
 * How to run:
 * ant clean compile-test
 * echo build/test/lib/*.jar build/lib/*.jar build/classes build/test/classes | sed -e 's/ /:/g' > cp
 * java -cp $(cat cp) org.apache.zookeeper.common.TimeTest | tee log-without-patch
 * <p/>
 * After test program starts, in another window, do commands:
 * date -s '+1hour'
 * date -s '-1hour'
 * <p/>
 * As long as there isn't any expired event, the experiment is successful.
 */
public class TimeTest extends ClientBase {
    private static final long mt0 = System.currentTimeMillis();

    private static final long nt0 = Time.currentElapsedTime();

    private static AtomicInteger watchCount = new AtomicInteger(0);

    @Test
    public void testElapsedTimeToDate() throws Exception {
        long walltime = Time.currentWallTime();
        long elapsedTime = Time.currentElapsedTime();
        Thread.sleep(200);
        Calendar cal = Calendar.getInstance();
        cal.setTime(Time.elapsedTimeToDate(elapsedTime));
        int calculatedDate = cal.get(Calendar.HOUR_OF_DAY);
        cal.setTime(new Date(walltime));
        int realDate = cal.get(Calendar.HOUR_OF_DAY);
        Assert.assertEquals(calculatedDate, realDate);
    }
}

