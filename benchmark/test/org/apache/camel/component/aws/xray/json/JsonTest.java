/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.aws.xray.json;


import org.hamcrest.CoreMatchers;
import org.hamcrest.core.IsNull;
import org.junit.Assert;
import org.junit.Test;


public class JsonTest {
    @Test
    public void testJsonParse() {
        JsonStructure json = JsonParser.parse(("{\n" + (((((((((((((((((("  \"test\": \"some string\",\n" + "  \"otherKey\": true,\n") + "  \"nextKey\": 1234,\n") + "  \"doubleKey\": 1234.567,\n") + "  \"subElement\": {\n") + "    \"subKey\": \"some other string\",\n") + "    \"complexString\": \"String with JSON syntax elements like .,\\\" { or }\"\n") + "  },\n") + "  \"arrayElement\": [\n") + "    {\n") + "      \"id\": 1,\n") + "      \"name\": \"test1\"\n") + "    },\n") + "    {\n") + "      \"id\": 2,\n") + "      \"name\": \"test2\"\n") + "    }\n") + "  ]\n") + "}")));
        Assert.assertThat(json, CoreMatchers.is(IsNull.notNullValue()));
        Assert.assertThat(json, CoreMatchers.is(CoreMatchers.instanceOf(JsonObject.class)));
        JsonObject jsonObj = ((JsonObject) (json));
        Assert.assertThat(jsonObj.getKeys().size(), CoreMatchers.is(CoreMatchers.equalTo(6)));
        Assert.assertThat(jsonObj.getString("test"), CoreMatchers.is(CoreMatchers.equalTo("some string")));
        Assert.assertThat(jsonObj.getBoolean("otherKey"), CoreMatchers.is(CoreMatchers.equalTo(true)));
        Assert.assertThat(jsonObj.getInteger("nextKey"), CoreMatchers.is(CoreMatchers.equalTo(1234)));
        Assert.assertThat(jsonObj.getDouble("doubleKey"), CoreMatchers.is(CoreMatchers.equalTo(1234.567)));
        Assert.assertThat(jsonObj.get("subElement"), CoreMatchers.is(CoreMatchers.instanceOf(JsonObject.class)));
        JsonObject jsonSub = ((JsonObject) (jsonObj.get("subElement")));
        Assert.assertThat(jsonSub.getString("subKey"), CoreMatchers.is(CoreMatchers.equalTo("some other string")));
        Assert.assertThat(jsonSub.getString("complexString"), CoreMatchers.is(CoreMatchers.equalTo("String with JSON syntax elements like .,\\\" { or }")));
        Assert.assertThat(jsonObj.get("arrayElement"), CoreMatchers.is(CoreMatchers.instanceOf(JsonArray.class)));
        JsonArray jsonArr = ((JsonArray) (jsonObj.get("arrayElement")));
        Assert.assertThat(jsonArr.size(), CoreMatchers.is(CoreMatchers.equalTo(2)));
        Assert.assertThat(jsonArr.get(0), CoreMatchers.is(CoreMatchers.instanceOf(JsonObject.class)));
        JsonObject arrElem0 = ((JsonObject) (jsonArr.get(0)));
        Assert.assertThat(arrElem0.getInteger("id"), CoreMatchers.is(CoreMatchers.equalTo(1)));
        Assert.assertThat(arrElem0.getString("name"), CoreMatchers.is(CoreMatchers.equalTo("test1")));
        Assert.assertThat(jsonArr.get(1), CoreMatchers.is(CoreMatchers.instanceOf(JsonObject.class)));
        JsonObject arrElem1 = ((JsonObject) (jsonArr.get(1)));
        Assert.assertThat(arrElem1.getInteger("id"), CoreMatchers.is(CoreMatchers.equalTo(2)));
        Assert.assertThat(arrElem1.getString("name"), CoreMatchers.is(CoreMatchers.equalTo("test2")));
    }

    @Test
    public void testJsonParseSample() {
        JsonStructure json = JsonParser.parse(("{" + (((((((((((((((("  \"name\":\"b\"," + "  \"id\":\"6ae1778525198ce8\",") + "  \"start_time\":1.50947752281E9,") + "  \"trace_id\":\"1-59f8cc92-4819a77b4109de34405a5643\",") + "  \"end_time\":1.50947752442E9,") + "  \"aws\":{") + "    \"xray\":{") + "      \"sdk_version\":\"1.2.0\",") + "      \"sdk\":\"X-Ray for Java\"") + "    }") + "  },") + "  \"service\":{") + "    \"runtime\":\"Java HotSpot(TM) 64-Bit Server VM\",") + "    \"runtime_version\":\"1.8.0_144\"") + "  }") + "}") + "}")));
        Assert.assertThat(json, CoreMatchers.is(IsNull.notNullValue()));
        JsonObject jsonObj = ((JsonObject) (json));
        Assert.assertThat(jsonObj.getKeys().size(), CoreMatchers.is(CoreMatchers.equalTo(7)));
        Assert.assertThat(jsonObj.getString("name"), CoreMatchers.is(CoreMatchers.equalTo("b")));
        Assert.assertThat(jsonObj.getString("id"), CoreMatchers.is(CoreMatchers.equalTo("6ae1778525198ce8")));
        Assert.assertThat(jsonObj.getString("trace_id"), CoreMatchers.is(CoreMatchers.equalTo("1-59f8cc92-4819a77b4109de34405a5643")));
        Assert.assertThat(jsonObj.getDouble("start_time"), CoreMatchers.is(CoreMatchers.equalTo(1.50947752281E9)));
        Assert.assertThat(jsonObj.getDouble("end_time"), CoreMatchers.is(CoreMatchers.equalTo(1.50947752442E9)));
        Assert.assertThat(jsonObj.get("aws"), CoreMatchers.is(CoreMatchers.instanceOf(JsonObject.class)));
        JsonObject aws = ((JsonObject) (jsonObj.get("aws")));
        Assert.assertThat(aws.get("xray"), CoreMatchers.is(CoreMatchers.instanceOf(JsonObject.class)));
        JsonObject xray = ((JsonObject) (aws.get("xray")));
        Assert.assertThat(xray.getString("sdk_version"), CoreMatchers.is(CoreMatchers.equalTo("1.2.0")));
        Assert.assertThat(xray.getString("sdk"), CoreMatchers.is(CoreMatchers.equalTo("X-Ray for Java")));
        Assert.assertThat(jsonObj.get("service"), CoreMatchers.is(CoreMatchers.instanceOf(JsonObject.class)));
        JsonObject service = ((JsonObject) (jsonObj.get("service")));
        Assert.assertThat(service.getString("runtime"), CoreMatchers.is(CoreMatchers.equalTo("Java HotSpot(TM) 64-Bit Server VM")));
        Assert.assertThat(service.getString("runtime_version"), CoreMatchers.is(CoreMatchers.equalTo("1.8.0_144")));
    }

    @Test
    public void testJsonParseWithArray() {
        JsonStructure json = JsonParser.parse(("{" + ((((((((((((((((((((((("  \"name\":\"c\"," + "  \"id\":\"6ada7c7013b2c681\",") + "  \"start_time\":1.509484895232E9,") + "  \"trace_id\":\"1-59f8e935-11c64d09c90803f69534c9af\",") + "  \"end_time\":1.509484901458E9,") + "  \"subsegments\":[") + "    {") + "      \"name\":\"SendingTo_log_test\",") + "      \"id\":\"545118f5c69e2973\",") + "      \"start_time\":1.509484895813E9,") + "      \"end_time\":1.509484896709E9") + "    }") + "  ],") + "  \"aws\":{") + "    \"xray\":{") + "      \"sdk_version\":\"1.2.0\",") + "      \"sdk\":\"X-Ray for Java\"") + "    }") + "  },") + "  \"service\":{") + "    \"runtime\":\"Java HotSpot(TM) 64-Bit Server VM\",") + "    \"runtime_version\":\"1.8.0_144\"") + "  }") + "}\u0000\u0000")));
        Assert.assertThat(json, CoreMatchers.is(IsNull.notNullValue()));
        JsonObject jsonObj = ((JsonObject) (json));
        Assert.assertThat(jsonObj.getKeys().size(), CoreMatchers.is(CoreMatchers.equalTo(8)));
        Assert.assertThat(jsonObj.getString("name"), CoreMatchers.is(CoreMatchers.equalTo("c")));
        Assert.assertThat(jsonObj.getString("id"), CoreMatchers.is(CoreMatchers.equalTo("6ada7c7013b2c681")));
        Assert.assertThat(jsonObj.getString("trace_id"), CoreMatchers.is(CoreMatchers.equalTo("1-59f8e935-11c64d09c90803f69534c9af")));
        Assert.assertThat(jsonObj.getDouble("start_time"), CoreMatchers.is(CoreMatchers.equalTo(1.509484895232E9)));
        Assert.assertThat(jsonObj.getDouble("end_time"), CoreMatchers.is(CoreMatchers.equalTo(1.509484901458E9)));
        Assert.assertThat(jsonObj.get("aws"), CoreMatchers.is(CoreMatchers.instanceOf(JsonObject.class)));
        JsonObject aws = ((JsonObject) (jsonObj.get("aws")));
        Assert.assertThat(aws.get("xray"), CoreMatchers.is(CoreMatchers.instanceOf(JsonObject.class)));
        JsonObject xray = ((JsonObject) (aws.get("xray")));
        Assert.assertThat(xray.getString("sdk_version"), CoreMatchers.is(CoreMatchers.equalTo("1.2.0")));
        Assert.assertThat(xray.getString("sdk"), CoreMatchers.is(CoreMatchers.equalTo("X-Ray for Java")));
        Assert.assertThat(jsonObj.get("service"), CoreMatchers.is(CoreMatchers.instanceOf(JsonObject.class)));
        JsonObject service = ((JsonObject) (jsonObj.get("service")));
        Assert.assertThat(service.getString("runtime"), CoreMatchers.is(CoreMatchers.equalTo("Java HotSpot(TM) 64-Bit Server VM")));
        Assert.assertThat(service.getString("runtime_version"), CoreMatchers.is(CoreMatchers.equalTo("1.8.0_144")));
        Assert.assertThat(jsonObj.get("subsegments"), CoreMatchers.is(CoreMatchers.instanceOf(JsonArray.class)));
        JsonArray array = ((JsonArray) (jsonObj.get("subsegments")));
        Assert.assertThat(array.size(), CoreMatchers.is(CoreMatchers.equalTo(1)));
        Assert.assertThat(array.get(0), CoreMatchers.is(CoreMatchers.instanceOf(JsonObject.class)));
        JsonObject arrItem = ((JsonObject) (array.get(0)));
        Assert.assertThat(arrItem.getKeys().size(), CoreMatchers.is(CoreMatchers.equalTo(4)));
        Assert.assertThat(arrItem.getString("name"), CoreMatchers.is(CoreMatchers.equalTo("SendingTo_log_test")));
        Assert.assertThat(arrItem.getString("id"), CoreMatchers.is(CoreMatchers.equalTo("545118f5c69e2973")));
        Assert.assertThat(arrItem.getDouble("start_time"), CoreMatchers.is(CoreMatchers.equalTo(1.509484895813E9)));
        Assert.assertThat(arrItem.getDouble("end_time"), CoreMatchers.is(CoreMatchers.equalTo(1.509484896709E9)));
    }
}

