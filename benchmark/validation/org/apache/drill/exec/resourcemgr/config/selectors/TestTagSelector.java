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
import org.apache.drill.categories.ResourceManagerTest;
import org.apache.drill.exec.ops.QueryContext;
import org.apache.drill.exec.resourcemgr.config.exception.RMConfigException;
import org.apache.drill.exec.server.options.OptionValue;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;


@Category(ResourceManagerTest.class)
public final class TestTagSelector {
    // Tests for TagSelector
    @Test
    public void testValidTagSelector() throws Exception {
        testCommonHelper("marketing");
    }

    @Test(expected = RMConfigException.class)
    public void testInValidTagSelector() throws Exception {
        testCommonHelper("");
        testCommonHelper(null);
    }

    @Test
    public void testTagSelectorWithQueryTags() throws Exception {
        QueryContext mockContext = Mockito.mock(QueryContext.class);
        TagSelector testSelector = ((TagSelector) (testCommonHelper("small")));
        // Test successful selection
        OptionValue testOption = OptionValue.create(SESSION_AND_QUERY, RM_QUERY_TAGS_KEY, "small,large", SESSION);
        Mockito.when(mockContext.getOption(RM_QUERY_TAGS_KEY)).thenReturn(testOption);
        Assert.assertTrue(testSelector.isQuerySelected(mockContext));
        // Test empty query tags
        testOption = OptionValue.create(SESSION_AND_QUERY, RM_QUERY_TAGS_KEY, "", SESSION);
        Mockito.when(mockContext.getOption(RM_QUERY_TAGS_KEY)).thenReturn(testOption);
        Assert.assertFalse(testSelector.isQuerySelected(mockContext));
        // Test different query tags
        testOption = OptionValue.create(SESSION_AND_QUERY, RM_QUERY_TAGS_KEY, "medium", SESSION);
        Mockito.when(mockContext.getOption(RM_QUERY_TAGS_KEY)).thenReturn(testOption);
        Assert.assertFalse(testSelector.isQuerySelected(mockContext));
    }
}

