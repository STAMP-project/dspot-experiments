/**
 * Licensed to ObjectStyle LLC under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ObjectStyle LLC licenses
 * this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package io.bootique.meta.application;


import io.bootique.Bootique;
import org.junit.Assert;
import org.junit.Test;


public class ApplicationIntrospectorTest {
    @Test
    public void testMainClass() {
        Class<?> mainClass = ApplicationIntrospector.mainClass();
        // TODO: until https://github.com/bootique/bootique/issues/52 is available,
        // we can't make an exact assertion here, as tests can be started from different IDEs, etc.
        Assert.assertNotNull(mainClass);
        Assert.assertNotEquals(Bootique.class, mainClass);
    }

    @Test
    public void testAppNameFromRuntime() {
        String name = ApplicationIntrospector.appNameFromRuntime();
        // TODO: until https://github.com/bootique/bootique/issues/52 is available,
        // we can't make an exact assertion here, as tests can be started from different IDEs, etc.
        Assert.assertNotNull(name);
        Assert.assertNotEquals("Bootique", name);
    }
}

