/**
 * -
 * #%L
 * rapidoid-watch
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
package org.rapidoid.io.watch;


import java.time.Duration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.test.TestCommons;
import org.rapidoid.test.TestIO;


@Authors("Nikolche Mihajlovski")
@Since("5.3.0")
public class MultiWatchTest extends TestCommons {
    @Test
    public void shouldSupportMultipleWatchCalls() {
        String dir = TestIO.createTempDir("watch-service-test");
        Assertions.assertTimeout(Duration.ofSeconds(60), () -> {
            if (!(TestCommons.RAPIDOID_CI)) {
                for (int i = 0; i < 10; i++) {
                    exerciseMultiWatch(dir);
                }
            }
        });
    }
}

