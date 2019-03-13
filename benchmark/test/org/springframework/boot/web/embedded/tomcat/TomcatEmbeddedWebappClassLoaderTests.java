/**
 * Copyright 2012-2017 the original author or authors.
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
package org.springframework.boot.web.embedded.tomcat;


import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.springframework.util.CollectionUtils;


/**
 * Tests for {@link TomcatEmbeddedWebappClassLoader}.
 *
 * @author Andy Wilkinson
 */
public class TomcatEmbeddedWebappClassLoaderTests {
    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    @Test
    public void getResourceFindsResourceFromParentClassLoader() throws Exception {
        File war = createWar();
        withWebappClassLoader(war, ( classLoader) -> assertThat(classLoader.getResource("test.txt")).isEqualTo(new URL(((webInfClassesUrlString(war)) + "test.txt"))));
    }

    @Test
    public void getResourcesOnlyFindsResourcesFromParentClassLoader() throws Exception {
        File warFile = createWar();
        withWebappClassLoader(warFile, ( classLoader) -> {
            List<URL> urls = new ArrayList<>();
            CollectionUtils.toIterator(classLoader.getResources("test.txt")).forEachRemaining(urls::add);
            assertThat(urls).containsExactly(new URL(((webInfClassesUrlString(warFile)) + "test.txt")));
        });
    }

    private interface ClassLoaderConsumer {
        void accept(ClassLoader classLoader) throws Exception;
    }
}

