/**
 * Copyright (c) 2014, Cloudera, Inc. All Rights Reserved.
 *
 * Cloudera, Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"). You may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * This software is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for
 * the specific language governing permissions and limitations under the
 * License.
 */
package com.cloudera.oryx.app.serving.als;


import MediaType.APPLICATION_JSON_TYPE;
import com.cloudera.oryx.app.serving.IDValue;
import java.util.List;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import org.junit.Assert;
import org.junit.Test;


public final class RecommendToAnonymousTest extends AbstractALSServingTest {
    @Test
    public void testRecommendToAnonymous() {
        List<IDValue> recs = target("/recommendToAnonymous/I4=1.0/I5=2.0").request().accept(APPLICATION_JSON_TYPE).get(AbstractALSServingTest.LIST_ID_VALUE_TYPE);
        AbstractALSServingTest.testTopByValue(7, recs, false);
        Assert.assertEquals("I7", recs.get(0).getID());
        Assert.assertEquals(0.35964763F, recs.get(0).getValue(), FLOAT_EPSILON);
    }

    @Test
    public void testRecommendToAnonymousCSV() {
        String response = target("/recommendToAnonymous/I4=1.0/I5=2.0").request().get(String.class);
        AbstractALSServingTest.testCSVTopByScore(7, response);
    }

    @Test
    public void testRecommendToAnonymousWithUnknown() {
        String response = target("/recommendToAnonymous/foo/I4=1.0/I5=2.0").request().get(String.class);
        AbstractALSServingTest.testCSVTopByScore(7, response);
    }

    @Test(expected = BadRequestException.class)
    public void testRecommendToAnonymousWithAllUnknown() {
        target("/recommendToAnonymous/foo").request().get(String.class);
    }

    @Test
    public void testHowMany() {
        testHowMany("/recommendToAnonymous/I1", 10, 8);
        testHowMany("/recommendToAnonymous/I1", 2, 2);
        testHowMany("/recommendToAnonymous/I1", 1, 1);
    }

    @Test(expected = BadRequestException.class)
    public void testBadHowMany() {
        testHowMany("/recommendToAnonymous/I1", (-1), 0);
    }

    @Test
    public void testOffset() {
        testOffset("/recommendToAnonymous/I1", 2, 1, 2);
        testOffset("/recommendToAnonymous/I1", 3, 7, 1);
        testOffset("/recommendToAnonymous/I1", 1, 1, 1);
        testOffset("/recommendToAnonymous/I1", 3, 8, 0);
    }

    @Test(expected = BadRequestException.class)
    public void testTooMany() {
        testHowMany("/recommendToAnonymous/I1", 100001, 0);
    }

    @Test(expected = BadRequestException.class)
    public void testBadOffset() {
        testOffset("/recommendToAnonymous/I1", 3, (-1), 0);
    }

    @Test(expected = NotFoundException.class)
    public void testNoArg() {
        target("/recommendToAnonymous").request().get(String.class);
    }

    @Test
    public void testRescorer() {
        List<IDValue> recs = target("/recommendToAnonymous/I4=1.0/I5=2.0").queryParam("rescorerParams", "foo").request().accept(APPLICATION_JSON_TYPE).get(AbstractALSServingTest.LIST_ID_VALUE_TYPE);
        AbstractALSServingTest.testTopByValue(3, recs, false);
        Assert.assertEquals("I7", recs.get(0).getID());
        Assert.assertEquals((2.0F * 0.35964763F), recs.get(0).getValue(), FLOAT_EPSILON);
    }
}

