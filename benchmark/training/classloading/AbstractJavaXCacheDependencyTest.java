/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package classloading;


import com.hazelcast.util.FilteringClassLoader;
import java.util.Collections;
import java.util.List;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


/**
 * Creates a member or client {@link com.hazelcast.core.Hazelcast} instance with an explicit exclusion of {@code javax.cache}.
 * <p>
 * If the method {@link #createHazelcastInstance()} or {@link #createHazelcastInstance_getCacheManager()} fails with a
 * {@link ClassNotFoundException} with the cause "javax.cache.* - Package excluded explicitly!" we accidentally introduced
 * a runtime dependency on {@link javax.cache} with a default configuration.
 * <p>
 * The method {@link #createHazelcastInstance_getCache()} is expected to fail, since it actually tries to invoke
 * {@link com.hazelcast.core.ICacheManager#getCache(String)}.
 */
public abstract class AbstractJavaXCacheDependencyTest {
    private static final String EXPECTED_CAUSE = "javax.cache.Cache - Package excluded explicitly!";

    private static final ClassLoader CLASS_LOADER;

    static {
        List<String> excludes = Collections.singletonList("javax.cache");
        CLASS_LOADER = new FilteringClassLoader(excludes, "com.hazelcast");
    }

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void createHazelcastInstance() throws Exception {
        createHazelcastInstance(false, false);
        ThreadLocalLeakTestUtils.checkThreadLocalsForLeaks(AbstractJavaXCacheDependencyTest.CLASS_LOADER);
    }

    @Test
    public void createHazelcastInstance_getCacheManager() throws Exception {
        createHazelcastInstance(true, false);
        ThreadLocalLeakTestUtils.checkThreadLocalsForLeaks(AbstractJavaXCacheDependencyTest.CLASS_LOADER);
    }

    @Test
    public void createHazelcastInstance_getCache() throws Exception {
        createHazelcastInstance(true, true);
        ThreadLocalLeakTestUtils.checkThreadLocalsForLeaks(AbstractJavaXCacheDependencyTest.CLASS_LOADER);
    }
}

