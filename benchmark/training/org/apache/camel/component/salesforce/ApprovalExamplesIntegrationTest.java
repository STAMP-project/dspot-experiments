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
package org.apache.camel.component.salesforce;


import java.util.HashMap;
import java.util.Map;
import org.apache.camel.component.salesforce.api.dto.approval.ApprovalResult;
import org.junit.Test;


public class ApprovalExamplesIntegrationTest extends AbstractApprovalIntegrationTest {
    public ApprovalExamplesIntegrationTest() {
        super(3);
    }

    @Test
    public void example1() {
        // tag::example1Usage
        final Map<String, String> body = new HashMap<>();
        body.put("contextId", accountIds.iterator().next());
        body.put("nextApproverIds", userId);
        final ApprovalResult result = template.requestBody("direct:example1", body, ApprovalResult.class);
        // end::example1Usage
        assertNotNull("Result should be received", result);
    }

    @Test
    public void example2() {
        // tag::example2Usage
        final Map<String, String> body = new HashMap<>();
        body.put("contextId", accountIds.iterator().next());
        body.put("nextApproverIds", userId);
        final ApprovalResult result = template.requestBody("direct:example2", body, ApprovalResult.class);
        // end::example2Usage
        assertNotNull("Result should be received", result);
    }
}

