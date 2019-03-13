/**
 * -
 * #%L
 * rapidoid-integration-tests
 * %%
 * Copyright (C) 2014 - 2018 Nikolche Mihajlovski and contributors
 * %%
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
 * #L%
 */
package org.rapidoid.http;


import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.GET;
import org.rapidoid.annotation.Since;
import org.rapidoid.cache.Cached;
import org.rapidoid.setup.App;
import org.rapidoid.setup.On;


@Authors("Nikolche Mihajlovski")
@Since("5.3.0")
public class HttpCachingTest extends IsolatedIntegrationTest {
    @Test
    public void testHttpCaching() {
        // without caching
        AtomicInteger x = new AtomicInteger();
        On.get("/x").plain(x::incrementAndGet);
        // with caching
        AtomicInteger y = new AtomicInteger();
        On.get("/y").cacheTTL(1000).plain(y::incrementAndGet);
        exerciseCaching();
    }

    @Test
    public void testHttpCachingWithAnnotations() {
        App.beans(HttpCachingTest.CachingCtrl.class);
        exerciseCaching();
    }

    static class CachingCtrl {
        // without caching
        AtomicInteger x = new AtomicInteger();

        // with caching
        AtomicInteger y = new AtomicInteger();

        @GET
        public Object x() {
            return x.incrementAndGet();
        }

        @GET
        @Cached(ttl = 1000)
        public Object y() {
            return y.incrementAndGet();
        }
    }
}

