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
public final class TestComplexSelectors {
    private static final Map<String, String> tagSelectorConfig1 = new HashMap<>();

    private static final Map<String, String> tagSelectorConfig2 = new HashMap<>();

    private static final QueryContext mockContext = Mockito.mock(QueryContext.class);

    private static final List<Object> complexSelectorValue = new ArrayList<>();

    @Test(expected = RMConfigException.class)
    public void testOrSelectorSingleValidSelector() throws Exception {
        // setup complexSelectorValue
        TestComplexSelectors.complexSelectorValue.add(TestComplexSelectors.tagSelectorConfig1);
        testOrCommonHelper();
    }

    @Test(expected = RMConfigException.class)
    public void testOrSelectorEmptyValue() throws Exception {
        testOrCommonHelper();
    }

    @Test(expected = RMConfigException.class)
    public void testAndSelectorEmptyValue() throws Exception {
        testAndCommonHelper();
    }

    @Test(expected = RMConfigException.class)
    public void testOrSelectorStringValue() throws Exception {
        Config testConfig = ConfigFactory.empty().withValue("or", ConfigValueFactory.fromAnyRef("dummy"));
        ResourcePoolSelectorFactory.createSelector(testConfig);
    }

    @Test(expected = RMConfigException.class)
    public void testAndSelectorStringValue() throws Exception {
        Config testConfig = ConfigFactory.empty().withValue("and", ConfigValueFactory.fromAnyRef("dummy"));
        ResourcePoolSelectorFactory.createSelector(testConfig);
    }

    @Test
    public void testOrSelectorWithTwoValidSelector() throws Exception {
        // setup complexSelectorValue
        TestComplexSelectors.complexSelectorValue.add(TestComplexSelectors.tagSelectorConfig1);
        TestComplexSelectors.complexSelectorValue.add(TestComplexSelectors.tagSelectorConfig2);
        // Test for OrSelector
        ResourcePoolSelector testSelector = testOrCommonHelper();
        // Test successful selection with OrSelector
        OptionValue testOption = OptionValue.create(SESSION_AND_QUERY, RM_QUERY_TAGS_KEY, "small", SESSION);
        Mockito.when(TestComplexSelectors.mockContext.getOption(RM_QUERY_TAGS_KEY)).thenReturn(testOption);
        Assert.assertTrue(testSelector.isQuerySelected(TestComplexSelectors.mockContext));
        // Test unsuccessful selection with OrSelector
        testOption = OptionValue.create(SESSION_AND_QUERY, RM_QUERY_TAGS_KEY, "medium", SESSION);
        Mockito.when(TestComplexSelectors.mockContext.getOption(RM_QUERY_TAGS_KEY)).thenReturn(testOption);
        Assert.assertFalse(testSelector.isQuerySelected(TestComplexSelectors.mockContext));
    }

    @Test
    public void testAndSelectorWithTwoValidSelector() throws Exception {
        // setup complexSelectorValue
        TestComplexSelectors.complexSelectorValue.add(TestComplexSelectors.tagSelectorConfig1);
        TestComplexSelectors.complexSelectorValue.add(TestComplexSelectors.tagSelectorConfig2);
        // Test for AndSelector
        ResourcePoolSelector testSelector = testAndCommonHelper();
        // Test successful selection with AndSelector
        OptionValue testOption = OptionValue.create(SESSION_AND_QUERY, RM_QUERY_TAGS_KEY, "small,large", SESSION);
        Mockito.when(TestComplexSelectors.mockContext.getOption(RM_QUERY_TAGS_KEY)).thenReturn(testOption);
        Assert.assertTrue(testSelector.isQuerySelected(TestComplexSelectors.mockContext));
        // Test unsuccessful selection with AndSelector
        testOption = OptionValue.create(SESSION_AND_QUERY, RM_QUERY_TAGS_KEY, "small", SESSION);
        Mockito.when(TestComplexSelectors.mockContext.getOption(RM_QUERY_TAGS_KEY)).thenReturn(testOption);
        Assert.assertFalse(testSelector.isQuerySelected(TestComplexSelectors.mockContext));
    }

    @Test
    public void testAndSelectorWithNotEqualSelector() throws Exception {
        // setup NotEqualSelector config
        Map<String, Object> notEqualConfig = new HashMap<>();
        notEqualConfig.put("not_equal", TestComplexSelectors.tagSelectorConfig1);
        // setup complex selector value
        TestComplexSelectors.complexSelectorValue.add(notEqualConfig);
        TestComplexSelectors.complexSelectorValue.add(TestComplexSelectors.tagSelectorConfig2);
        // Test for AndSelector
        ResourcePoolSelector testSelector = testAndCommonHelper();
        // Test successful selection with AndSelector
        OptionValue testOption = OptionValue.create(SESSION_AND_QUERY, RM_QUERY_TAGS_KEY, "large", SESSION);
        Mockito.when(TestComplexSelectors.mockContext.getOption(RM_QUERY_TAGS_KEY)).thenReturn(testOption);
        Assert.assertTrue(testSelector.isQuerySelected(TestComplexSelectors.mockContext));
        // Test unsuccessful selection with AndSelector
        testOption = OptionValue.create(SESSION_AND_QUERY, RM_QUERY_TAGS_KEY, "small", SESSION);
        Mockito.when(TestComplexSelectors.mockContext.getOption(RM_QUERY_TAGS_KEY)).thenReturn(testOption);
        Assert.assertFalse(testSelector.isQuerySelected(TestComplexSelectors.mockContext));
    }

    @Test
    public void testORSelectorWithNotEqualSelector() throws Exception {
        // setup NotEqualSelector config
        Map<String, Object> notEqualConfig = new HashMap<>();
        notEqualConfig.put("not_equal", TestComplexSelectors.tagSelectorConfig1);
        // setup complex selector value
        TestComplexSelectors.complexSelectorValue.add(notEqualConfig);
        TestComplexSelectors.complexSelectorValue.add(TestComplexSelectors.tagSelectorConfig2);
        // Test for AndSelector
        ResourcePoolSelector testSelector = testOrCommonHelper();
        // Test successful selection with AndSelector
        OptionValue testOption = OptionValue.create(SESSION_AND_QUERY, RM_QUERY_TAGS_KEY, "medium", SESSION);
        Mockito.when(TestComplexSelectors.mockContext.getOption(RM_QUERY_TAGS_KEY)).thenReturn(testOption);
        Assert.assertTrue(testSelector.isQuerySelected(TestComplexSelectors.mockContext));
        testOption = OptionValue.create(SESSION_AND_QUERY, RM_QUERY_TAGS_KEY, "large", SESSION);
        Mockito.when(TestComplexSelectors.mockContext.getOption(RM_QUERY_TAGS_KEY)).thenReturn(testOption);
        Assert.assertTrue(testSelector.isQuerySelected(TestComplexSelectors.mockContext));
        // Test unsuccessful selection with AndSelector
        testOption = OptionValue.create(SESSION_AND_QUERY, RM_QUERY_TAGS_KEY, "small", SESSION);
        Mockito.when(TestComplexSelectors.mockContext.getOption(RM_QUERY_TAGS_KEY)).thenReturn(testOption);
        Assert.assertFalse(testSelector.isQuerySelected(TestComplexSelectors.mockContext));
    }

    @Test
    public void testAndSelectorWithOrSelector() throws Exception {
        // setup OrSelector config
        List<Object> orConfigValue = new ArrayList<>();
        orConfigValue.add(TestComplexSelectors.tagSelectorConfig1);
        orConfigValue.add(TestComplexSelectors.tagSelectorConfig2);
        Map<String, Object> orConfig = new HashMap<>();
        orConfig.put("or", orConfigValue);
        // get another TagSelector config
        Map<String, String> tagSelectorConfig3 = new HashMap<>();
        tagSelectorConfig3.put("tag", "medium");
        // setup complex selector value
        TestComplexSelectors.complexSelectorValue.add(orConfig);
        TestComplexSelectors.complexSelectorValue.add(tagSelectorConfig3);
        // Test for AndSelector
        ResourcePoolSelector testSelector = testAndCommonHelper();
        // Test successful selection with AndSelector
        OptionValue testOption = OptionValue.create(SESSION_AND_QUERY, RM_QUERY_TAGS_KEY, "small,medium", SESSION);
        Mockito.when(TestComplexSelectors.mockContext.getOption(RM_QUERY_TAGS_KEY)).thenReturn(testOption);
        Assert.assertTrue(testSelector.isQuerySelected(TestComplexSelectors.mockContext));
        testOption = OptionValue.create(SESSION_AND_QUERY, RM_QUERY_TAGS_KEY, "large,medium", SESSION);
        Mockito.when(TestComplexSelectors.mockContext.getOption(RM_QUERY_TAGS_KEY)).thenReturn(testOption);
        Assert.assertTrue(testSelector.isQuerySelected(TestComplexSelectors.mockContext));
        // Test unsuccessful selection with AndSelector
        testOption = OptionValue.create(SESSION_AND_QUERY, RM_QUERY_TAGS_KEY, "small,verylarge", SESSION);
        Mockito.when(TestComplexSelectors.mockContext.getOption(RM_QUERY_TAGS_KEY)).thenReturn(testOption);
        Assert.assertFalse(testSelector.isQuerySelected(TestComplexSelectors.mockContext));
    }
}

