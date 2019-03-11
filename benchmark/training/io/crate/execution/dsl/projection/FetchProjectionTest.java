/**
 * Licensed to Crate under one or more contributor license agreements.
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.  Crate licenses this file
 * to you under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * However, if you have executed another commercial license agreement
 * with Crate these terms will supersede the license and you may use the
 * software solely pursuant to the terms of the relevant commercial
 * agreement.
 */
package io.crate.execution.dsl.projection;


import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class FetchProjectionTest {
    @Test
    public void testMaxFetchSize() throws Exception {
        Assert.assertThat(FetchProjection.maxFetchSize(56), Matchers.is(2000));
        Assert.assertThat(FetchProjection.maxFetchSize(256), Matchers.is(30000));
        Assert.assertThat(FetchProjection.maxFetchSize(512), Matchers.is(65840));
        Assert.assertThat(FetchProjection.maxFetchSize(1024), Matchers.is(137520));
        Assert.assertThat(FetchProjection.maxFetchSize(2048), Matchers.is(280880));
        Assert.assertThat(FetchProjection.maxFetchSize(4096), Matchers.is(500000));
    }

    @Test
    public void testBoundedFetchSize() throws Exception {
        Assert.assertThat(FetchProjection.boundedFetchSize(0, 65840), Matchers.is(65840));
    }
}

