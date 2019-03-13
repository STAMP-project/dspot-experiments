/**
 * Copyright (c) 2014, Cloudera, Inc. All Rights Reserved.
 *
 * Cloudera, Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"). You may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * This software is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for
 * the specific language governing permissions and limitations under the
 * License.
 */
package com.cloudera.oryx.app.serving.als;


import MediaType.APPLICATION_JSON_TYPE;
import com.cloudera.oryx.app.serving.IDCount;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public final class MostPopularItemsTest extends AbstractALSServingTest {
    @Test
    public void testMostPopular() {
        List<IDCount> top = target("/mostPopularItems").request().accept(APPLICATION_JSON_TYPE).get(AbstractALSServingTest.LIST_ID_COUNT_TYPE);
        AbstractALSServingTest.testTopCount(9, top);
        Assert.assertEquals(6, top.get(0).getCount());
        Assert.assertEquals(6, top.get(1).getCount());
    }

    @Test
    public void testMostPopularCSV() {
        String response = target("/mostPopularItems").request().get(String.class);
        AbstractALSServingTest.testCSVTopByCount(9, response);
    }

    @Test
    public void testRescorer() {
        List<IDCount> top = target("/mostPopularItems").queryParam("rescorerParams", "foo").request().accept(APPLICATION_JSON_TYPE).get(AbstractALSServingTest.LIST_ID_COUNT_TYPE);
        Assert.assertEquals(4, top.size());
        Assert.assertEquals(6, top.get(0).getCount());
        Assert.assertEquals(5, top.get(1).getCount());
    }
}

