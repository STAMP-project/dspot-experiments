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
package org.rapidoid.event;


import Events.LOG_TRACE;
import Events.LOG_WARN;
import java.time.Duration;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.rapidoid.log.Log;
import org.rapidoid.test.TestCommons;
import org.rapidoid.u.U;


/**
 *
 *
 * @author Nikolche Mihajlovski
 * @since 5.2.0
 */
public class EventsTest extends TestCommons {
    @Test
    public void testLogEvents() {
        final List<String> warnings = U.list();
        LOG_WARN.listener(( event, data) -> {
            isFalse(data.isEmpty());
            eq(event, Events.LOG_WARN);
            warnings.add(data.get("_").toString());
        });
        Log.warn("WRN!");
        eq(warnings, U.list("WRN!"));
    }

    @Test
    public void firingUnusedEventsMustBeFast() {
        Assertions.assertTimeout(Duration.ofSeconds(1), () -> {
            for (int i = 0; i < ((100 * 1000) * 1000); i++) {
                Fire.event(LOG_TRACE);
            }
        });
    }
}

