/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.drill.exec.testing;


import ExecConstants.HTTP_ENABLE;
import ExecConstants.SYS_STORE_PROVIDER_LOCAL_ENABLE_WRITE;
import io.netty.buffer.DrillBuf;
import java.util.Properties;
import javax.inject.Inject;
import org.apache.drill.common.config.DrillConfig;
import org.apache.drill.common.exceptions.UserRemoteException;
import org.apache.drill.exec.client.DrillClient;
import org.apache.drill.exec.expr.DrillSimpleFunc;
import org.apache.drill.exec.expr.annotations.FunctionTemplate;
import org.apache.drill.exec.expr.annotations.FunctionTemplate.FunctionScope;
import org.apache.drill.exec.expr.annotations.FunctionTemplate.NullHandling;
import org.apache.drill.exec.expr.annotations.Output;
import org.apache.drill.exec.expr.annotations.Param;
import org.apache.drill.exec.expr.holders.Float8Holder;
import org.apache.drill.exec.memory.BufferAllocator;
import org.apache.drill.exec.server.Drillbit;
import org.apache.drill.exec.server.RemoteServiceSet;
import org.apache.drill.test.DrillTest;
import org.apache.drill.test.QueryTestUtil;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;


/* TODO(DRILL-3170)
This test had to be ignored because while the test case tpch01 works, the test
fails overall because the final allocator closure again complains about
outstanding resources. This could be fixed if we introduced a means to force
cleanup of an allocator and all of its descendant resources. But that's a
non-trivial exercise in the face of the ability to transfer ownership of
slices of a buffer; we can't be sure it is safe to release an
UnsafeDirectLittleEndian that an allocator believes it owns if slices of that
have been transferred to another allocator.
 */
@Ignore
public class TestResourceLeak extends DrillTest {
    private static DrillClient client;

    private static Drillbit bit;

    private static RemoteServiceSet serviceSet;

    private static DrillConfig config;

    private static BufferAllocator allocator;

    @SuppressWarnings("serial")
    private static final Properties TEST_CONFIGURATIONS = new Properties() {
        {
            put(SYS_STORE_PROVIDER_LOCAL_ENABLE_WRITE, "false");
            put(HTTP_ENABLE, "false");
        }
    };

    @Test
    public void tpch01() throws Exception {
        final String query = TestResourceLeak.getFile("memory/tpch01_memory_leak.sql");
        try {
            QueryTestUtil.testRunAndLog(TestResourceLeak.client, ("alter session set `planner.slice_target` = 10; " + query));
        } catch (UserRemoteException e) {
            if (e.getMessage().contains("Allocator closed with outstanding buffers allocated")) {
                return;
            }
            throw e;
        }
        Assert.fail("Expected UserRemoteException indicating memory leak");
    }

    @FunctionTemplate(name = "leakResource", scope = FunctionScope.SIMPLE, nulls = NullHandling.NULL_IF_NULL)
    public static class Leak implements DrillSimpleFunc {
        @Param
        Float8Holder in;

        @Inject
        DrillBuf buf;

        @Output
        Float8Holder out;

        @Override
        public void setup() {
        }

        @Override
        public void eval() {
            buf.retain();
            out.value = in.value;
        }
    }
}

