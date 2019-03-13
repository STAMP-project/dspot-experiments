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
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.apache.camel.component.salesforce.api.dto.approval.Approvals.Info;
import org.apache.camel.component.salesforce.api.utils.JsonUtils;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Assert;
import org.junit.Test;


public class ApprovalsTest {
    @Test
    public void shouldDeserialize() throws JsonProcessingException, IOException {
        final ObjectMapper mapper = JsonUtils.createObjectMapper();
        final Object read = mapper.readerFor(Approvals.class).readValue(("{\n"// 
         + ((((((((("  \"approvals\" : {\n"// 
         + "   \"Account\" : [ {\n")// 
         + "     \"description\" : null,\n")// 
         + "     \"id\" : \"04aD00000008Py9\",\n")// 
         + "     \"name\" : \"Account Approval Process\",\n")// 
         + "     \"object\" : \"Account\",\n")// 
         + "     \"sortOrder\" : 1\n")// 
         + "   } ]\n")// 
         + "  }\n")// 
         + "}")));
        Assert.assertThat("Should deserialize Approvals", read, IsInstanceOf.instanceOf(Approvals.class));
        final Approvals approvals = ((Approvals) (read));
        final Map<String, List<Info>> approvalsMap = approvals.getApprovals();
        Assert.assertEquals("Deserialized approvals should have one entry", 1, approvalsMap.size());
        final List<Info> accountApprovals = approvalsMap.get("Account");
        Assert.assertNotNull("Deserialized approvals should contain list of `Account` type approvals", accountApprovals);
        Assert.assertEquals("There should be one approval of `Account` type", 1, accountApprovals.size());
        final Info accountInfo = accountApprovals.get(0);
        Assert.assertNull("Deserialized `Account` approval should have null description", accountInfo.getDescription());
        Assert.assertEquals("Deserialized `Account` approval should have defined id", "04aD00000008Py9", accountInfo.getId());
        Assert.assertEquals("Deserialized `Account` approval should have defined name", "Account Approval Process", accountInfo.getName());
        Assert.assertEquals("Deserialized `Account` approval should have defined object", "Account", accountInfo.getObject());
        Assert.assertEquals("Deserialized `Account` approval should have defined sortOrder", 1, accountInfo.getSortOrder());
    }
}

