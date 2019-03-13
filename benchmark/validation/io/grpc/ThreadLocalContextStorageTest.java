/**
 * Copyright 2019 The gRPC Authors
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
package io.grpc;


import Context.ROOT;
import ThreadLocalContextStorage.localContext;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static Context.ROOT;


@RunWith(JUnit4.class)
public final class ThreadLocalContextStorageTest {
    private static final Context.Key<Object> KEY = Context.key("test-key");

    private static final ThreadLocalContextStorage storage = new ThreadLocalContextStorage();

    private Context contextBeforeTest;

    @Test
    public void detach_threadLocalClearedOnRoot() {
        Context context = ROOT.withValue(io.grpc.KEY, new Object());
        Context old = ThreadLocalContextStorageTest.storage.doAttach(context);
        assertThat(ThreadLocalContextStorageTest.storage.current()).isSameAs(context);
        assertThat(localContext.get()).isSameAs(context);
        ThreadLocalContextStorageTest.storage.detach(context, old);
        // thread local must contain null to avoid leaking our ClassLoader via ROOT
        assertThat(localContext.get()).isNull();
    }

    @Test
    public void detach_detachRoot() {
        final List<LogRecord> logs = new ArrayList<>();
        Handler handler = new Handler() {
            @Override
            public void publish(LogRecord record) {
                logs.add(record);
            }

            @Override
            public void flush() {
            }

            @Override
            public void close() {
            }
        };
        // Explicitly choose ROOT as the current context
        Context context = ROOT;
        Context old = ThreadLocalContextStorageTest.storage.doAttach(context);
        // Attach and detach a random context
        Context innerContext = ROOT.withValue(io.grpc.KEY, new Object());
        ThreadLocalContextStorageTest.storage.detach(innerContext, ThreadLocalContextStorageTest.storage.doAttach(innerContext));
        Logger logger = Logger.getLogger(ThreadLocalContextStorage.class.getName());
        logger.addHandler(handler);
        try {
            // Make sure detaching ROOT doesn't log a warning
            ThreadLocalContextStorageTest.storage.detach(context, old);
        } finally {
            logger.removeHandler(handler);
        }
        assertThat(logs).isEmpty();
    }
}

