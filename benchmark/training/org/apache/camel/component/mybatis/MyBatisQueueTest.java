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
package org.apache.camel.component.mybatis;


import java.util.List;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;


public class MyBatisQueueTest extends MyBatisTestSupport {
    @Test
    public void testConsume() throws Exception {
        MockEndpoint endpoint = getMockEndpoint("mock:results");
        endpoint.expectedMinimumMessageCount(2);
        Account account1 = new Account();
        account1.setId(1);
        account1.setFirstName("Bob");
        account1.setLastName("Denver");
        account1.setEmailAddress("TryGuessingGilligan@gmail.com");
        Account account2 = new Account();
        account2.setId(2);
        account2.setFirstName("Alan");
        account2.setLastName("Hale");
        account2.setEmailAddress("TryGuessingSkipper@gmail.com");
        template.sendBody("direct:start", new Account[]{ account1, account2 });
        assertMockEndpointsSatisfied();
        // need a delay here on slower machines
        Thread.sleep(1000);
        // now lets poll that the account has been inserted
        List<?> body = template.requestBody("mybatis:selectProcessedAccounts?statementType=SelectList", null, List.class);
        assertEquals(("Wrong size: " + body), 2, body.size());
        Account actual = assertIsInstanceOf(Account.class, body.get(0));
        assertEquals("Account.getFirstName()", "Bob", actual.getFirstName());
        assertEquals("Account.getLastName()", "Denver", actual.getLastName());
        body = template.requestBody("mybatis:selectUnprocessedAccounts?statementType=SelectList", null, List.class);
        assertEquals(("Wrong size: " + body), 0, body.size());
    }
}

