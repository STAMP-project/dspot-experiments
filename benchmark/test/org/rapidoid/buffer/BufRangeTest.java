/**
 * -
 * #%L
 * rapidoid-networking
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
package org.rapidoid.buffer;


import org.junit.jupiter.api.Test;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.data.BufRange;
import org.rapidoid.test.TestRnd;


@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class BufRangeTest extends BufferTestCommons {
    @Test
    public void shouldBackupAndRestore() {
        BufRange range = new BufRange(10, 3);
        eq(range, 10, 3);
        long backup = range.backup();
        range.set(7, 17);
        eq(range, 7, 17);
        range.restore(backup);
        eq(range, 10, 3);
    }

    @Test
    public void statisticalTest() {
        BufRange rng = new BufRange();
        isTrue(rng.isEmpty());
        int[] borders = new int[]{ Integer.MIN_VALUE, -1111, -1, 0, 1, 1111, Integer.MAX_VALUE };
        for (int i = 0; i < (borders.length); i++) {
            for (int j = 0; j < (borders.length); j++) {
                check(rng, borders[i], borders[j]);
            }
        }
        for (int i = 0; i < 1000000; i++) {
            check(rng, TestRnd.rnd(), TestRnd.rnd());
        }
    }
}

