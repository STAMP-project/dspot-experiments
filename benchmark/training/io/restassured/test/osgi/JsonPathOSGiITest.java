/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package io.restassured.test.osgi;


import io.restassured.path.json.JsonPath;
import org.hamcrest.core.IsEqual;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;


/**
 * This test aims to prove that json-path is available as a valid OSGi bundle.
 */
@RunWith(PaxExam.class)
public class JsonPathOSGiITest {
    @Test
    public void jsonPath() {
        final String JSON = "{\n" + ((("\"lotto\":{\n" + " \"lottoId\":5,\n") + "}\n") + "}");
        JsonPath jsonPath = new JsonPath(JSON).setRoot("lotto");
        Assert.assertThat(jsonPath.getInt("lottoId"), IsEqual.equalTo(5));
    }
}

