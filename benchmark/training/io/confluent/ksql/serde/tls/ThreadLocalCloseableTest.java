/**
 * Copyright 2018 Confluent Inc.
 *
 * Licensed under the Confluent Community License (the "License"); you may not use
 * this file except in compliance with the License.  You may obtain a copy of the
 * License at
 *
 * http://www.confluent.io/confluent-community-license
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 */
package io.confluent.ksql.serde.tls;


import java.io.Closeable;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class ThreadLocalCloseableTest {
    @Test
    public void shouldCloseAllInstances() {
        final Object lock = new Object();
        final List<Closeable> closeables = new LinkedList<>();
        final ThreadLocalCloseable<Closeable> testCloseable = new ThreadLocalCloseable(() -> {
            synchronized(lock) {
                final Closeable closeable = mock(.class);
                closeables.add(closeable);
                try {
                    closeable.close();
                } catch ( e) {
                    throw new <e>RuntimeException();
                }
                expectLastCall();
                replay(closeable);
                return closeable;
            }
        });
        final int iterations = 3;
        final List<Thread> threads = new LinkedList<>();
        for (int i = 0; i < iterations; i++) {
            threads.add(new Thread(testCloseable::get));
            threads.get(((threads.size()) - 1)).start();
        }
        threads.forEach(( t) -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        testCloseable.close();
        Assert.assertThat(closeables.size(), CoreMatchers.equalTo(iterations));
        closeables.forEach(EasyMock::verify);
    }

    @Test
    public void shouldThrowOnAccessAfterClose() {
        final ThreadLocalCloseable<Closeable> testCloseable = new ThreadLocalCloseable(() -> () -> {
        });
        testCloseable.close();
        try {
            testCloseable.get();
            Assert.fail("get() should throw IllegalStateException");
        } catch (final IllegalStateException e) {
        }
    }
}

