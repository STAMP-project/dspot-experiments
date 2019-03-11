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
package org.apache.hadoop.security;


import java.util.Arrays;
import javax.naming.NamingException;
import org.junit.Test;


/**
 * Test LdapGroupsMapping with one-query lookup enabled.
 * Mockito is used to simulate the LDAP server response.
 */
@SuppressWarnings("unchecked")
public class TestLdapGroupsMappingWithOneQuery extends TestLdapGroupsMappingBase {
    @Test
    public void testGetGroups() throws NamingException {
        // given a user whose ldap query returns a user object with three "memberOf"
        // properties, return an array of strings representing its groups.
        String[] testGroups = new String[]{ "abc", "xyz", "sss" };
        doTestGetGroups(Arrays.asList(testGroups));
    }
}

