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
package com.hazelcast.internal.util;


import JavaVersion.JAVA_9;
import com.hazelcast.internal.util.ModularJavaUtils.PackageAccessRequirement;
import com.hazelcast.logging.ILogger;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import static PackageAccessRequirement.createRequirement;


/**
 * Tests that {@link ModularJavaUtils} correctly logs warnings about missing package access on Java 9+.
 */
@Category({ QuickTest.class, ParallelTest.class })
public class ModularJavaUtilsTest {
    /**
     * Tests that the testsuite is running without missing-package-access warnings. I.e. the access to Java internal packages is
     * provided.
     */
    @Test
    public void testNoMissingPackageAccessInTestsuite() {
        ILogger logger = Mockito.mock(ILogger.class);
        ModularJavaUtils.checkJavaInternalAccess(logger);
        Mockito.verify(logger, Mockito.never()).warning(ArgumentMatchers.anyString());
    }

    /**
     * Tests that {@link ModularJavaUtils#checkPackageRequirements(ILogger, Map)} logs a warning with the missing
     * Java argument if a Java internal package access not provided to Hazelcast and the test is running on Java 9 or newer.
     */
    @Test
    public void testMissingAccess() {
        Assume.assumeTrue(JavaVersion.isAtLeast(JAVA_9));
        ILogger logger = Mockito.mock(ILogger.class);
        Map<String, PackageAccessRequirement[]> requirements = new HashMap<String, PackageAccessRequirement[]>();
        requirements.put("java.base", new PackageAccessRequirement[]{ createRequirement(true, "jdk.internal.misc") });
        ModularJavaUtils.checkPackageRequirements(logger, requirements);
        Mockito.verify(logger, Mockito.times(1)).warning(ArgumentMatchers.contains("--add-opens java.base/jdk.internal.misc=ALL-UNNAMED"));
    }

    @Test
    public void testHazelcastModuleName() {
        Assert.assertNull("Hazelcast module name should be null as the testsuite runs hazelcast on the classpath", ModularJavaUtils.getHazelcastModuleName());
    }

    @Test
    public void testNoExceptionWhenNullLogger() {
        ModularJavaUtils.checkJavaInternalAccess(null);
    }
}

