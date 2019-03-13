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
package org.rapidoid.config;


import Conf.ROOT;
import org.junit.jupiter.api.Test;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.env.Env;
import org.rapidoid.io.IO;
import org.rapidoid.test.AbstractCommonsTest;
import org.rapidoid.test.TestIO;
import org.rapidoid.util.Msc;


@Authors("Nikolche Mihajlovski")
@Since("5.2.5")
public class RootCustomizationTest extends AbstractCommonsTest {
    @Test
    public void testConfigRootSetup() {
        String dir = TestIO.createTempDir("app");
        IO.save(Msc.path(dir, "config.yml"), "id: abc1");
        Env.setArgs(("root=" + dir), "config=config");
        eq(ROOT.entry("id").getOrNull(), "abc1");
    }

    @Test
    public void testConfigRootAndFileSetup() {
        String dir = TestIO.createTempDir("app");
        IO.save(Msc.path(dir, "the-config.yml"), "id: abc2");
        Env.setArgs(("root=" + dir), "config=the-config");
        eq(ROOT.entry("id").getOrNull(), "abc2");
    }
}

