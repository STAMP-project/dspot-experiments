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
package org.apache.camel.component.facebook;


import facebook4j.TestUser;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;


/**
 * Test methods in {@link facebook4j.api.TestUserMethods}
 */
public class TestUserMethodsTest extends CamelFacebookTestSupport {
    private static final String TEST_USER1 = "test one";

    private static final String TEST_USER2 = "test two";

    public TestUserMethodsTest() throws Exception {
    }

    @Test
    public void testTestUsers() {
        // create a test user with exchange properties
        final TestUser testUser1 = template().requestBody("direct:createTestUser", TestUserMethodsTest.TEST_USER1, TestUser.class);
        assertNotNull("Test User1", testUser1);
        // create a test user with exchange properties
        final TestUser testUser2 = template().requestBody("direct:createTestUser", TestUserMethodsTest.TEST_USER2, TestUser.class);
        assertNotNull("Test User2", testUser2);
        // make friends, not enemies
        final Map<String, Object> headers = new HashMap<>();
        headers.put("CamelFacebook.testUser2", testUser2);
        Boolean worked = template().requestBodyAndHeaders("direct:makeFriendTestUser", testUser1, headers, Boolean.class);
        assertTrue("Friends not made", worked);
        // get app test users
        final List testUsers = template().requestBody("direct:testUsers", null, List.class);
        assertNotNull("Test users", testUsers);
        assertFalse("Empty test user list", testUsers.isEmpty());
        // delete test users
        for (Object user : testUsers) {
            final TestUser testUser = ((TestUser) (user));
            if ((testUser.equals(testUser1)) || (testUser.equals(testUser2))) {
                final String id = testUser.getId();
                worked = template().requestBody("direct:deleteTestUser", id, Boolean.class);
                assertTrue(("Test user not deleted for id " + id), worked);
            }
        }
    }
}

