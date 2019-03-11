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
package org.apache.beam.sdk.util;


import java.util.List;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Functionality tests for ApiSurface.
 */
@RunWith(JUnit4.class)
public class ApiSurfaceTest {
    private interface Exposed {}

    private interface ExposedReturnType {
        ApiSurfaceTest.Exposed zero();
    }

    @Test
    public void testExposedReturnType() throws Exception {
        assertExposed(ApiSurfaceTest.ExposedReturnType.class, ApiSurfaceTest.Exposed.class);
    }

    private interface ExposedParameterTypeVarBound {
        <T extends ApiSurfaceTest.Exposed> void getList(T whatever);
    }

    @Test
    public void testExposedParameterTypeVarBound() throws Exception {
        assertExposed(ApiSurfaceTest.ExposedParameterTypeVarBound.class, ApiSurfaceTest.Exposed.class);
    }

    private interface ExposedWildcardBound {
        void acceptList(List<? extends ApiSurfaceTest.Exposed> arg);
    }

    @Test
    public void testExposedWildcardBound() throws Exception {
        assertExposed(ApiSurfaceTest.ExposedWildcardBound.class, ApiSurfaceTest.Exposed.class);
    }

    private interface ExposedActualTypeArgument extends List<ApiSurfaceTest.Exposed> {}

    @Test
    public void testExposedActualTypeArgument() throws Exception {
        assertExposed(ApiSurfaceTest.ExposedActualTypeArgument.class, ApiSurfaceTest.Exposed.class);
    }

    @Test
    public void testIgnoreAll() throws Exception {
        ApiSurface apiSurface = ApiSurface.ofClass(ApiSurfaceTest.ExposedWildcardBound.class).includingClass(Object.class).includingClass(ApiSurface.class).pruningPattern(".*");
        Assert.assertThat(apiSurface.getExposedClasses(), Matchers.emptyIterable());
    }

    private interface PrunedPattern {}

    private interface NotPruned extends ApiSurfaceTest.PrunedPattern {}

    @Test
    public void testprunedPattern() throws Exception {
        ApiSurface apiSurface = ApiSurface.ofClass(ApiSurfaceTest.NotPruned.class).pruningClass(ApiSurfaceTest.PrunedPattern.class);
        Assert.assertThat(apiSurface.getExposedClasses(), Matchers.containsInAnyOrder(((Class) (ApiSurfaceTest.NotPruned.class))));
    }

    private interface ExposedTwice {
        ApiSurfaceTest.Exposed zero();

        ApiSurfaceTest.Exposed one();
    }

    @Test
    public void testExposedTwice() throws Exception {
        assertExposed(ApiSurfaceTest.ExposedTwice.class, ApiSurfaceTest.Exposed.class);
    }

    private interface ExposedCycle {
        ApiSurfaceTest.ExposedCycle zero(ApiSurfaceTest.Exposed foo);
    }

    @Test
    public void testExposedCycle() throws Exception {
        assertExposed(ApiSurfaceTest.ExposedCycle.class, ApiSurfaceTest.Exposed.class);
    }

    private interface ExposedGenericCycle {
        ApiSurfaceTest.Exposed zero(List<ApiSurfaceTest.ExposedGenericCycle> foo);
    }

    @Test
    public void testExposedGenericCycle() throws Exception {
        assertExposed(ApiSurfaceTest.ExposedGenericCycle.class, ApiSurfaceTest.Exposed.class);
    }

    private interface ExposedArrayCycle {
        ApiSurfaceTest.Exposed zero(ApiSurfaceTest.ExposedArrayCycle[] foo);
    }

    @Test
    public void testExposedArrayCycle() throws Exception {
        assertExposed(ApiSurfaceTest.ExposedArrayCycle.class, ApiSurfaceTest.Exposed.class);
    }
}

