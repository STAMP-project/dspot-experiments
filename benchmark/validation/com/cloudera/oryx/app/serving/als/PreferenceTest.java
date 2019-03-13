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


import MediaType.APPLICATION_JSON;
import Response.Status.BAD_REQUEST;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import org.junit.Assert;
import org.junit.Test;


public final class PreferenceTest extends AbstractALSServingTest {
    private static final String PREFERENCE_DATA = "2.5\n";

    @Test
    public void testPostJson() {
        try (Response response = target("/pref/U1/I1").request().post(Entity.entity(PreferenceTest.PREFERENCE_DATA, APPLICATION_JSON))) {
            PreferenceTest.checkResponse(response, "U1", "I1", "2.5");
        }
    }

    @Test
    public void testPostWithEmptyItemValue() {
        try (Response response = target("/pref/U2/I2").request().post(Entity.text(""))) {
            PreferenceTest.checkResponse(response, "U2", "I2", "1");
        }
    }

    @Test
    public void testPostWithBadItemValue() {
        try (Response response = target("/pref/U2/I2").request().post(Entity.text("aBc!"))) {
            Assert.assertEquals(BAD_REQUEST.getStatusCode(), response.getStatus());
        }
    }

    @Test
    public void testPostCSV() {
        try (Response response = target("/pref/U1/I1").request().post(Entity.entity(PreferenceTest.PREFERENCE_DATA, "text/csv"))) {
            PreferenceTest.checkResponse(response, "U1", "I1", "2.5");
        }
    }

    @Test
    public void testDelete() {
        Response response = target("/pref/U1/I2").request().delete();
        PreferenceTest.checkResponse(response, "U1", "I2", "");
    }
}

