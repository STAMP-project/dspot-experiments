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
package org.apache.hadoop.lib.lang;


import java.util.concurrent.Callable;
import org.apache.hadoop.test.HTestCase;
import org.junit.Assert;
import org.junit.Test;


public class TestRunnableCallable extends HTestCase {
    public static class R implements Runnable {
        boolean RUN;

        @Override
        public void run() {
            RUN = true;
        }
    }

    public static class C implements Callable {
        boolean RUN;

        @Override
        public Object call() throws Exception {
            RUN = true;
            return null;
        }
    }

    public static class CEx implements Callable {
        @Override
        public Object call() throws Exception {
            throw new Exception();
        }
    }

    @Test
    public void runnable() throws Exception {
        TestRunnableCallable.R r = new TestRunnableCallable.R();
        RunnableCallable rc = new RunnableCallable(r);
        rc.run();
        Assert.assertTrue(r.RUN);
        r = new TestRunnableCallable.R();
        rc = new RunnableCallable(r);
        rc.call();
        Assert.assertTrue(r.RUN);
        Assert.assertEquals(rc.toString(), "R");
    }

    @Test
    public void callable() throws Exception {
        TestRunnableCallable.C c = new TestRunnableCallable.C();
        RunnableCallable rc = new RunnableCallable(c);
        rc.run();
        Assert.assertTrue(c.RUN);
        c = new TestRunnableCallable.C();
        rc = new RunnableCallable(c);
        rc.call();
        Assert.assertTrue(c.RUN);
        Assert.assertEquals(rc.toString(), "C");
    }

    @Test(expected = RuntimeException.class)
    public void callableExRun() throws Exception {
        TestRunnableCallable.CEx c = new TestRunnableCallable.CEx();
        RunnableCallable rc = new RunnableCallable(c);
        rc.run();
    }
}

