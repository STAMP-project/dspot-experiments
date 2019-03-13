/**
 * -
 * #%L
 * rapidoid-commons
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
package org.rapidoid.ctx;


import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.job.Jobs;
import org.rapidoid.test.AbstractCommonsTest;
import org.rapidoid.test.TestRnd;
import org.rapidoid.u.U;


@Authors("Nikolche Mihajlovski")
@Since("4.1.0")
public class JobsTest extends AbstractCommonsTest {
    @Test
    public void testJobsExecution() {
        int total = 100000;
        final AtomicInteger counter = new AtomicInteger();
        Assertions.assertTimeout(Duration.ofSeconds(30), () -> {
            multiThreaded(1000, total, () -> {
                Ctxs.open("test-job");
                final UserInfo user = new UserInfo(TestRnd.rndStr(50), U.set("role1"));
                Ctxs.required().setUser(user);
                ensureProperContext(user);
                ScheduledFuture<?> future = Jobs.after(100, TimeUnit.MILLISECONDS).run(() -> {
                    ensureProperContext(user);
                    counter.incrementAndGet();
                });
                try {
                    future.get();
                } catch ( e) {
                    e.printStackTrace();
                    fail("The job throwed an exception!");
                }
                Ctxs.close();
            });
        });
        eq(counter.get(), total);
    }
}

