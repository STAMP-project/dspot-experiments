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


import javax.ws.rs.core.Response;
import org.apache.ambari.view.pig.HDFSTest;
import org.apache.ambari.view.pig.services.HelpService;
import org.json.simple.JSONObject;
import org.junit.Assert;
import org.junit.Test;


public class HelpTest extends HDFSTest {
    private HelpService helpService;

    @Test
    public void configTest() {
        Response response = helpService.config();
        Assert.assertEquals(200, response.getStatus());
        JSONObject obj = ((JSONObject) (response.getEntity()));
        Assert.assertTrue(obj.containsKey("webhdfs.url"));
        Assert.assertEquals(HDFSTest.hdfsURI, obj.get("webhdfs.url"));
    }
}

