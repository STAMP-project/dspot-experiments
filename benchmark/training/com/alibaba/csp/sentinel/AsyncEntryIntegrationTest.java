/**
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.csp.sentinel;


import com.alibaba.csp.sentinel.context.ContextUtil;
import com.alibaba.csp.sentinel.node.DefaultNode;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.hamcrest.CoreMatchers;
import org.junit.Test;


/**
 * Integration test for asynchronous entry, including common scenarios.
 *
 * @author Eric Zhao
 */
public class AsyncEntryIntegrationTest {
    private final ExecutorService pool = Executors.newFixedThreadPool(10);

    @Test
    public void testAsyncEntryUnderSyncEntry() throws Exception {
        // Expected invocation chain:
        // EntranceNode: machine-root
        // -EntranceNode: async-context
        // --test-top
        // ---test-async
        // ----test-sync-in-async
        // ----test-another-async
        // -----test-another-in-async
        // ---test-sync
        ContextUtil.enter(contextName, origin);
        Entry entry = null;
        try {
            entry = SphU.entry("test-top");
            doAsyncThenSync();
        } catch (BlockException ex) {
            ex.printStackTrace();
        } finally {
            if (entry != null) {
                entry.exit();
            }
            ContextUtil.exit();
        }
        // we keep the original timeout of 15 seconds although the test should
        // complete in less than 3 seconds
        await().timeout(15, TimeUnit.SECONDS).until(new Callable<DefaultNode>() {
            @Override
            public DefaultNode call() throws Exception {
                return queryInvocationTree(false);
            }
        }, CoreMatchers.notNullValue());
        queryInvocationTree(true);
    }

    private interface Consumer<T> {
        void accept(T t);
    }

    private final String contextName = "async-context";

    private final String origin = "originA";
}

