/**
 * Copyright (c) 2014, Cloudera, Inc. and Intel Corp. All Rights Reserved.
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
import java.util.List;
import javax.ws.rs.NotFoundException;
import org.junit.Assert;
import org.junit.Test;


public final class SimilarityToItemTest extends AbstractALSServingTest {
    @Test(expected = NotFoundException.class)
    public void testNoArg() {
        target("/similarityToItem").request().get(String.class);
    }

    @Test
    public void testSimilarityToItem() {
        List<Double> items = target("/similarityToItem/I0/I1/I2").request().accept(APPLICATION_JSON_TYPE).get(LIST_DOUBLE_TYPE);
        Assert.assertEquals(2, items.size());
        Assert.assertEquals(0.9042603F, items.get(0), FLOAT_EPSILON);
        Assert.assertEquals((-0.26486862F), items.get(1), FLOAT_EPSILON);
    }

    @Test
    public void testSimilarityToItemCSV() {
        String response = target("/similarityToItem/I0/I1/I2").request().get(String.class);
        AbstractALSServingTest.testCSVScores(2, response);
    }

    @Test
    public void testZeroSimilarityToItem() {
        List<Double> items = target("/similarityToItem/I1/I10").request().accept(APPLICATION_JSON_TYPE).get(LIST_DOUBLE_TYPE);
        Assert.assertEquals(1, items.size());
        Assert.assertEquals(0.0F, items.get(0), FLOAT_EPSILON);
    }
}

