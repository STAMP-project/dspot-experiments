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
package org.apache.camel.component.salesforce.api.dto.approval;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoughtworks.xstream.XStream;
import java.io.IOException;
import org.apache.camel.component.salesforce.api.utils.JsonUtils;
import org.apache.camel.component.salesforce.api.utils.XStreamUtils;
import org.junit.Test;


public class ApprovalResultTest {
    @Test
    public void shouldDeserializeFromJson() throws JsonProcessingException, IOException {
        final String json = "["// 
         + ((((((((("{"// 
         + "\"actorIds\":[\"0050Y000000u5NOQAY\"],")// 
         + "\"entityId\":\"0010Y000005BYrZQAW\",")// 
         + "\"errors\":null,")// 
         + "\"instanceId\":\"04g0Y000000PL53QAG\",")// 
         + "\"instanceStatus\":\"Pending\",")// 
         + "\"newWorkitemIds\":[\"04i0Y000000L0fkQAC\"],")// 
         + "\"success\":true")// 
         + "}")// 
         + "]");
        final ObjectMapper mapper = JsonUtils.createObjectMapper();
        final ApprovalResult results = mapper.readerFor(ApprovalResult.class).readValue(json);
        ApprovalResultTest.assertResponseReadCorrectly(results);
    }

    @Test
    public void shouldDeserializeFromXml() throws IllegalAccessException, InstantiationException {
        final ApprovalResult results = new ApprovalResult();
        final XStream xStream = XStreamUtils.createXStream(ApprovalResult.class);
        xStream.fromXML(("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"// 
         + ((((((((("<ProcessApprovalResult>"// 
         + "<ProcessApprovalResult>")// 
         + "<actorIds>0050Y000000u5NOQAY</actorIds>")// 
         + "<entityId>0010Y000005BYrZQAW</entityId>")// 
         + "<instanceId>04g0Y000000PL53QAG</instanceId>")// 
         + "<instanceStatus>Pending</instanceStatus>")// 
         + "<newWorkitemIds>04i0Y000000L0fkQAC</newWorkitemIds>")// 
         + "<success>true</success>")// 
         + "</ProcessApprovalResult>")// 
         + "</ProcessApprovalResult>")), results);
        ApprovalResultTest.assertResponseReadCorrectly(results);
    }
}

