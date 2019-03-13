/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.beam.sdk.extensions.gcp;


import java.util.Set;
import org.apache.beam.sdk.util.ApiSurface;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableSet;
import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * API surface verification for Google Cloud Platform core components.
 */
@RunWith(JUnit4.class)
public class GcpCoreApiSurfaceTest {
    @Test
    public void testGcpCoreApiSurface() throws Exception {
        final Package thisPackage = getClass().getPackage();
        final ClassLoader thisClassLoader = getClass().getClassLoader();
        final ApiSurface apiSurface = ApiSurface.ofPackage(thisPackage, thisClassLoader).pruningPattern("org[.]apache[.]beam[.].*Test.*").pruningPattern("org[.]apache[.]beam[.].*IT").pruningPattern("java[.]lang.*").pruningPattern("java[.]util.*");
        @SuppressWarnings("unchecked")
        final Set<Matcher<Class<?>>> allowedClasses = ImmutableSet.of(ApiSurface.classesInPackage("com.google.api.client.googleapis"), ApiSurface.classesInPackage("com.google.api.client.http"), ApiSurface.classesInPackage("com.google.api.client.json"), ApiSurface.classesInPackage("com.google.api.client.util"), ApiSurface.classesInPackage("com.google.api.services.storage"), ApiSurface.classesInPackage("com.google.auth"), ApiSurface.classesInPackage("com.fasterxml.jackson.annotation"), ApiSurface.classesInPackage("java"), ApiSurface.classesInPackage("javax"), ApiSurface.classesInPackage("org.apache.beam.sdk"), ApiSurface.classesInPackage("org.joda.time"));
        MatcherAssert.assertThat(apiSurface, ApiSurface.containsOnlyClassesMatching(allowedClasses));
    }
}

