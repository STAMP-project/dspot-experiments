/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.drill.exec.resourcemgr.config.selectors;


import ExecConstants.RM_QUERY_TAGS_KEY;
import OptionValue.AccessibleScopes.SESSION_AND_QUERY;
import OptionValue.OptionScope.SESSION;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValueFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.drill.categories.ResourceManagerTest;
import org.apache.drill.exec.ops.QueryContext;
import org.apache.drill.exec.resourcemgr.config.exception.RMConfigException;
import org.apache.drill.exec.server.options.OptionValue;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;


@Category(ResourceManagerTest.class)
public final class TestNotEqualSelector {
    @Test
    public void testValidNotEqualTagSelector() throws Exception {
        Map<String, String> tagSelectorConfig = new HashMap<>();
        tagSelectorConfig.put("tag", "marketing");
        NotEqualSelector testSelector = ((NotEqualSelector) (testCommonHelper(tagSelectorConfig)));
        Assert.assertTrue("Expected child selector type to be TagSelector", ((testSelector.getPoolSelector()) instanceof TagSelector));
        QueryContext mockContext = Mockito.mock(QueryContext.class);
        // Test successful selection
        OptionValue testOption = OptionValue.create(SESSION_AND_QUERY, RM_QUERY_TAGS_KEY, "small,large", SESSION);
        Mockito.when(mockContext.getOption(RM_QUERY_TAGS_KEY)).thenReturn(testOption);
        Assert.assertTrue(testSelector.isQuerySelected(mockContext));
        // Test unsuccessful selection
        testOption = OptionValue.create(SESSION_AND_QUERY, RM_QUERY_TAGS_KEY, "marketing", SESSION);
        Mockito.when(mockContext.getOption(RM_QUERY_TAGS_KEY)).thenReturn(testOption);
        Assert.assertFalse(testSelector.isQuerySelected(mockContext));
    }

    @Test
    public void testValidNotEqualAclSelector() throws Exception {
        Map<String, List<String>> aclSelectorValue = new HashMap<>();
        List<String> usersValue = new ArrayList<>();
        usersValue.add("user1");
        usersValue.add("user2");
        List<String> groupsValue = new ArrayList<>();
        groupsValue.add("group1");
        groupsValue.add("group2");
        aclSelectorValue.put("users", usersValue);
        aclSelectorValue.put("groups", groupsValue);
        Map<String, Map<String, List<String>>> aclSelectorConfig = new HashMap<>();
        aclSelectorConfig.put("acl", aclSelectorValue);
        NotEqualSelector testSelector = ((NotEqualSelector) (testCommonHelper(aclSelectorConfig)));
        Assert.assertTrue("Expected child selector type to be TagSelector", ((testSelector.getPoolSelector()) instanceof AclSelector));
    }

    @Test(expected = RMConfigException.class)
    public void testInValidNotEqualAclSelector() throws Exception {
        Map<String, List<String>> aclSelectorValue = new HashMap<>();
        aclSelectorValue.put("users", new ArrayList<>());
        aclSelectorValue.put("groups", new ArrayList<>());
        Map<String, Map<String, List<String>>> aclSelectorConfig = new HashMap<>();
        aclSelectorConfig.put("acl", aclSelectorValue);
        testCommonHelper(aclSelectorConfig);
    }

    @Test(expected = RMConfigException.class)
    public void testNotEqualSelectorEmptyValue() throws Exception {
        Map<String, String> notEqualSelectorValue = new HashMap<>();
        testCommonHelper(notEqualSelectorValue);
    }

    @Test(expected = RMConfigException.class)
    public void testNotEqualSelectorStringValue() throws Exception {
        Config testConfig = ConfigFactory.empty().withValue("not_equal", ConfigValueFactory.fromAnyRef("null"));
        ResourcePoolSelectorFactory.createSelector(testConfig);
    }
}

