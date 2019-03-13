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
package org.rapidoid.io;


import java.time.Duration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.test.AbstractCommonsTest;


@Authors("Nikolche Mihajlovski")
@Since("4.1.0")
public class ResTest extends AbstractCommonsTest {
    @Test
    public void testWithSingleLocation() {
        Res file = Res.from("abc.txt", "");
        eq(file.getName(), "abc.txt");
        isTrue(file.exists());
        eq(file.getBytes(), "ABC!".getBytes());
    }

    @Test
    public void testWithMultipleLocations1() {
        Res file = Res.from("abc.txt", "res1", "res2");
        eq(file.getName(), "abc.txt");
        isTrue(file.exists());
        eq(file.getContent(), "ABC1");
    }

    @Test
    public void testWithMultipleLocations2() {
        Res file = Res.from("abc.txt", "res1", "res2");
        eq(file.getName(), "abc.txt");
        isTrue(file.exists());
        eq(file.getContent(), "ABC1");
    }

    @Test
    public void shouldBeFast() {
        // typically 1M reads should take less than a second
        Assertions.assertTimeout(Duration.ofSeconds(5), () -> {
            for (int i = 0; i < 900; i++) {
                // fill-in the cache (with non-existing resources)
                Res.from("abc", "x-location");
            }
            // should be fast
            multiThreaded(100, 1000000, () -> {
                Res file = Res.from("abc.txt", "");
                notNull(file.getBytes());
            });
        });
    }

    @Test
    public void testWithNonexistingFiles() {
        Res file = Res.from("some-non-existing-file", "res1", "", "res2");
        eq(file.getName(), "some-non-existing-file");
        isFalse(file.exists());
    }

    @Test
    public void shouldNotReadFolders() {
        Res dir = Res.from("res1");
        isFalse(dir.exists());
        isFalse(dir.isHidden());
        eq(dir.getBytesOrNull(), null);
    }
}

