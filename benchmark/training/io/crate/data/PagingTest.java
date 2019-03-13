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
package io.crate.data;


import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;


public class PagingTest {
    @Test
    public void testGetNodePageSize() throws Exception {
        double weight = 1.0 / 8.0;
        int pageSize = Paging.getWeightedPageSize(10000, weight);
        Assert.assertThat(pageSize, Is.is(1875));
    }

    @Test
    public void testSmallLimitWithManyExecutionNodes() throws Exception {
        int pageSize = Paging.getWeightedPageSize(10, (1.0 / 20.0));
        Assert.assertThat(pageSize, Is.is(10));
    }

    @Test
    public void testSmallLimitIsUnchanged() throws Exception {
        Assert.assertThat(Paging.getWeightedPageSize(10, (1.0 / 4)), Is.is(10));
    }
}

