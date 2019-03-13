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


import com.hazelcast.test.HazelcastParametersRunnerFactory;
import com.hazelcast.test.annotation.NightlyTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * Checks Hazelcast for {@link ThreadLocal} leaks after the shutdown.
 */
@RunWith(Parameterized.class)
@Parameterized.UseParametersRunnerFactory(HazelcastParametersRunnerFactory.class)
@Category(NightlyTest.class)
public class ThreadLocalLeakTest {
    private enum ClassLoaderType {

        FILTERING,
        OWN;}

    @Parameterized.Parameter
    public ThreadLocalLeakTest.ClassLoaderType classLoaderType;

    private Class<?> applicationClazz;

    /**
     * Checks the detection code with an example application which uses {@link ThreadLocal} with a proper cleanup.
     */
    @Test
    public void testLeakingApplication_withThreadLocalCleanup() throws Exception {
        ClassLoader cl = getClassLoader(LeakingApplication.class.getPackage().getName());
        applicationClazz = ThreadLocalLeakTest.startLeakingApplication(cl, true);
        ThreadLocalLeakTestUtils.checkThreadLocalsForLeaks(cl);
    }

    /**
     * Checks the detection code with an example application which leaks {@link ThreadLocal} instances.
     */
    @Test(expected = AssertionError.class)
    public void testLeakingApplication_withoutThreadLocalCleanup() throws Exception {
        ClassLoader cl = getClassLoader(LeakingApplication.class.getPackage().getName());
        applicationClazz = ThreadLocalLeakTest.startLeakingApplication(cl, false);
        ThreadLocalLeakTestUtils.checkThreadLocalsForLeaks(cl);
    }

    /**
     * Tests Hazelcast for {@link ThreadLocal} leakages.
     */
    @Test
    public void testHazelcast() throws Exception {
        ClassLoader cl = getClassLoader("com.hazelcast");
        Object isolatedNode = ThreadLocalLeakTest.startIsolatedNode(cl);
        Assert.assertTrue(((ThreadLocalLeakTest.getClusterTime(isolatedNode)) > 0));
        ThreadLocalLeakTest.shutdownIsolatedNode(isolatedNode);
        ThreadLocalLeakTestUtils.checkThreadLocalsForLeaks(cl);
    }
}

