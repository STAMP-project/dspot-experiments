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
import javax.naming.directory.SearchControls;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


@SuppressWarnings("unchecked")
public class TestLdapGroupsMappingWithPosixGroup extends TestLdapGroupsMappingBase {
    @Test
    public void testGetGroups() throws NamingException {
        // The search functionality of the mock context is reused, so we will
        // return the user NamingEnumeration first, and then the group
        Mockito.when(getContext().search(ArgumentMatchers.anyString(), ArgumentMatchers.contains("posix"), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(SearchControls.class))).thenReturn(getUserNames(), getGroupNames());
        doTestGetGroups(Arrays.asList(getTestGroups()), 2);
    }
}

