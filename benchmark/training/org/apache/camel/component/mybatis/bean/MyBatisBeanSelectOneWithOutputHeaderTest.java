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
package org.apache.camel.component.mybatis.bean;


import MyBatisConstants.MYBATIS_RESULT;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.component.mybatis.Account;
import org.apache.camel.component.mybatis.MyBatisTestSupport;
import org.junit.Test;


public class MyBatisBeanSelectOneWithOutputHeaderTest extends MyBatisTestSupport {
    private static final String TEST_CASE_HEADER_NAME = "testCaseHeader";

    private static final int TEST_ACCOUNT_ID = 456;

    @Test
    public void testSelectOneWithOutputHeader() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMessageCount(1);
        mock.message(0).header(MyBatisBeanSelectOneWithOutputHeaderTest.TEST_CASE_HEADER_NAME).isInstanceOf(Account.class);
        mock.message(0).body().isEqualTo(MyBatisBeanSelectOneWithOutputHeaderTest.TEST_ACCOUNT_ID);
        mock.message(0).header(MYBATIS_RESULT).isNull();
        template.sendBody("direct:start", MyBatisBeanSelectOneWithOutputHeaderTest.TEST_ACCOUNT_ID);
        assertMockEndpointsSatisfied();
        Account account = mock.getReceivedExchanges().get(0).getIn().getHeader(MyBatisBeanSelectOneWithOutputHeaderTest.TEST_CASE_HEADER_NAME, Account.class);
        assertEquals("Claus", account.getFirstName());
    }
}

