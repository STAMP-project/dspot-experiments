/**
 * Copyright (c) 2015, Cloudera and Intel, Inc. All Rights Reserved.
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
package com.cloudera.oryx.app.serving.kmeans;


import Response.Status.NO_CONTENT;
import com.cloudera.oryx.common.collection.Pair;
import com.cloudera.oryx.lambda.serving.MockTopicProducer;
import java.util.List;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import org.junit.Assert;
import org.junit.Test;


public final class AddTest extends AbstractKMeansServingTest {
    static final String ADD_DATA = "1.0,0.0,20.0\n1.0,-4.0,30.0\n0.0,0.0,40.0\n0.0,-4.0,50.0";

    private static final String[][] EXPECTED_TOPIC = new String[][]{ new String[]{ "1.0", "0.0", "20.0" }, new String[]{ "1.0", "-4.0", "30.0" }, new String[]{ "0.0", "0.0", "40.0" }, new String[]{ "0.0", "-4.0", "50.0" } };

    @Test
    public void testSimpleAdd() {
        try (Response response = target("/add").request().post(Entity.text(AddTest.ADD_DATA))) {
            AddTest.checkResponse(response);
        }
    }

    @Test
    public void testFormAdd() throws Exception {
        try (Response response = getFormPostResponse(AddTest.ADD_DATA, "/add", null, null)) {
            AddTest.checkResponse(response);
        }
    }

    @Test
    public void testURIAdd() {
        try (Response response = target(("/add/" + (AddTest.ADD_DATA.split("\n")[0]))).request().post(Entity.text(""))) {
            Assert.assertEquals(NO_CONTENT.getStatusCode(), response.getStatus());
        }
        List<Pair<String, String>> data = MockTopicProducer.getData();
        Assert.assertEquals(1, data.size());
        Assert.assertArrayEquals(AddTest.EXPECTED_TOPIC[0], data.get(0).getSecond().split(","));
    }
}

