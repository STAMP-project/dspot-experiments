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
package com.hazelcast.internal.usercodedeployment.impl;


import com.hazelcast.config.UserCodeDeploymentConfig;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import static ProviderMode.LOCAL_AND_CACHED_CLASSES;
import static ProviderMode.LOCAL_CLASSES_ONLY;
import static ProviderMode.OFF;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ClassDataProviderTest {
    @Test
    public void givenProviderModeSetToOFF_whenMapClassContainsClass_thenReturnNull() throws Exception {
        UserCodeDeploymentConfig.ProviderMode providerMode = OFF;
        String className = "className";
        ClassSource classSource = ClassDataProviderTest.newMockClassSource();
        ClassLoader parent = getClass().getClassLoader();
        ClassDataProvider provider = createClassDataProvider(providerMode, className, classSource, parent);
        ClassData classData = provider.getClassDataOrNull(className);
        Assert.assertNull(classData);
    }

    @Test
    public void givenProviderModeSetToLOCAL_CLASSES_ONLY_whenMapClassContainsClass_thenReturnNull() throws Exception {
        UserCodeDeploymentConfig.ProviderMode providerMode = LOCAL_CLASSES_ONLY;
        String className = "className";
        ClassSource classSource = ClassDataProviderTest.newMockClassSource();
        ClassLoader parent = getClass().getClassLoader();
        ClassDataProvider provider = createClassDataProvider(providerMode, className, classSource, parent);
        ClassData classData = provider.getClassDataOrNull(className);
        Assert.assertNull(classData);
    }

    @Test
    public void givenProviderModeSetToLOCAL_AND_CACHED_CLASSES_whenMapClassContainsClass_thenReturnIt() throws Exception {
        UserCodeDeploymentConfig.ProviderMode providerMode = LOCAL_AND_CACHED_CLASSES;
        String className = "className";
        ClassSource classSource = ClassDataProviderTest.newMockClassSource();
        ClassLoader parent = getClass().getClassLoader();
        ClassDataProvider provider = createClassDataProvider(providerMode, className, classSource, parent);
        ClassData classData = provider.getClassDataOrNull(className);
        Assert.assertNotNull(classData.getInnerClassDefinitions());
    }
}

