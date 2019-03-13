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
package org.apache.hadoop.hbase.ipc;


import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.exceptions.ClientExceptionsUtil;
import org.apache.hadoop.hbase.exceptions.TimeoutIOException;
import org.apache.hadoop.hbase.testclassification.ClientTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ ClientTests.class, SmallTests.class })
public class TestIPCUtil {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestIPCUtil.class);

    /**
     * See HBASE-21862, it is very important to keep the original exception type for connection
     * exceptions.
     */
    @Test
    public void testWrapConnectionException() throws Exception {
        List<Throwable> exceptions = new ArrayList<>();
        for (Class<? extends Throwable> clazz : ClientExceptionsUtil.getConnectionExceptionTypes()) {
            exceptions.add(TestIPCUtil.create(clazz));
        }
        InetSocketAddress addr = InetSocketAddress.createUnresolved("127.0.0.1", 12345);
        for (Throwable exception : exceptions) {
            if (exception instanceof TimeoutException) {
                Assert.assertThat(IPCUtil.wrapException(addr, exception), CoreMatchers.instanceOf(TimeoutIOException.class));
            } else {
                Assert.assertThat(IPCUtil.wrapException(addr, exception), CoreMatchers.instanceOf(exception.getClass()));
            }
        }
    }
}

