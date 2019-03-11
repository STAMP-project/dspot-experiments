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
package org.apache.ambari.view.pig.test;


import UDFService.UDFRequest;
import javax.ws.rs.core.Response;
import org.apache.ambari.view.pig.BasePigTest;
import org.apache.ambari.view.pig.resources.udf.UDFService;
import org.apache.ambari.view.pig.resources.udf.models.UDF;
import org.apache.ambari.view.pig.utils.NotFoundFormattedException;
import org.json.simple.JSONObject;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class UDFTest extends BasePigTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private UDFService udfService;

    @Test
    public void createUDF() {
        Response response = doCreateUDF();
        Assert.assertEquals(201, response.getStatus());
        JSONObject obj = ((JSONObject) (response.getEntity()));
        Assert.assertTrue(obj.containsKey("udf"));
        Assert.assertNotNull(getId());
        Assert.assertFalse(getId().isEmpty());
    }

    @Test
    public void udfNotFound() {
        thrown.expect(NotFoundFormattedException.class);
        udfService.getUDF("4242");
    }

    @Test
    public void updateUDF() {
        Response createdUDF = doCreateUDF();
        String createdUdfId = ((UDF) (((JSONObject) (createdUDF.getEntity())).get("udf"))).getId();
        UDFService.UDFRequest request = new UDFService.UDFRequest();
        request.udf = new UDF();
        request.udf.setPath("/tmp/updatedUDF.jar");
        request.udf.setName("TestUDF2");
        Response response = udfService.updateUDF(request, createdUdfId);
        Assert.assertEquals(204, response.getStatus());
        Response response2 = udfService.getUDF(createdUdfId);
        Assert.assertEquals(200, response2.getStatus());
        JSONObject obj = ((JSONObject) (response2.getEntity()));
        Assert.assertTrue(obj.containsKey("udf"));
        Assert.assertEquals(getName(), request.udf.getName());
        Assert.assertEquals(getPath(), request.udf.getPath());
    }

    @Test
    public void deleteUDF() {
        Response createdUDF = doCreateUDF();
        String createdUdfId = ((UDF) (((JSONObject) (createdUDF.getEntity())).get("udf"))).getId();
        Response response = udfService.deleteUDF(createdUdfId);
        Assert.assertEquals(204, response.getStatus());
        thrown.expect(NotFoundFormattedException.class);
        udfService.getUDF(createdUdfId);
    }
}

