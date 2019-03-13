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
package org.apache.camel.spring.interceptor;


import Exchange.EXCEPTION_CAUGHT;
import Exchange.FILE_NAME;
import java.util.concurrent.TimeUnit;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;


public class TransactionalClientDataSourceTransactedWithFileOnExceptionTest extends TransactionClientDataSourceSupport {
    @Test
    public void testTransactionSuccess() throws Exception {
        template.sendBodyAndHeader("file://target/transacted/okay", "Hello World", FILE_NAME, "okay.txt");
        await().atMost(3, TimeUnit.SECONDS).untilAsserted(() -> {
            // wait for route to complete
            int count = jdbc.queryForObject("select count(*) from books", .class);
            assertEquals("Number of books", 3, count);
        });
    }

    @Test
    public void testTransactionRollback() throws Exception {
        MockEndpoint error = getMockEndpoint("mock:error");
        error.expectedMessageCount(1);
        error.message(0).header(EXCEPTION_CAUGHT).isNotNull();
        error.message(0).header(EXCEPTION_CAUGHT).isInstanceOf(IllegalArgumentException.class);
        error.expectedFileExists("target/transacted/failed/fail.txt");
        template.sendBodyAndHeader("file://target/transacted/fail", "Hello World", FILE_NAME, "fail.txt");
        // wait for route to complete
        await().atMost(3, TimeUnit.SECONDS).untilAsserted(() -> {
            // should not be able to process the file so we still got 1 book as we did from the start
            int count = jdbc.queryForObject("select count(*) from books", .class);
            assertEquals("Number of books", 1, count);
        });
        assertMockEndpointsSatisfied();
    }
}

