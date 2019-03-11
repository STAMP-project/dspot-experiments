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
package org.apache.ambari.server.state.quicklinksprofile;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.apache.ambari.server.state.quicklinks.Link;
import org.junit.Assert;
import org.junit.Test;


public class FilterEvaluatorTest {
    static final String NAMENODE = "NAMENODE";

    static final String NAMENODE_UI = "namenode_ui";

    static final String AUTHENTICATED = "authenticated";

    static final String NAMENODE_JMX = "namenode_jmx";

    static final String SSO = "sso";

    private Link namenodeUi;

    private Link nameNodeJmx;

    public FilterEvaluatorTest() {
        namenodeUi = new Link();
        namenodeUi.setComponentName(FilterEvaluatorTest.NAMENODE);
        namenodeUi.setName(FilterEvaluatorTest.NAMENODE_UI);
        namenodeUi.setAttributes(ImmutableList.of(FilterEvaluatorTest.AUTHENTICATED));
        // this is a "legacy" link with no attributes defined
        nameNodeJmx = new Link();
        nameNodeJmx.setComponentName(FilterEvaluatorTest.NAMENODE);
        nameNodeJmx.setName(FilterEvaluatorTest.NAMENODE_JMX);
    }

    /**
     * Evaluators should work when initialized with {@code null} or an empty list of filters.
     */
    @Test
    public void testWithEmptyFilters() throws Exception {
        FilterEvaluator evaluator = new FilterEvaluator(new ArrayList());
        Assert.assertEquals(Optional.empty(), evaluator.isVisible(namenodeUi));
        FilterEvaluator evaluator2 = new FilterEvaluator(null);
        Assert.assertEquals(Optional.empty(), evaluator2.isVisible(namenodeUi));
    }

    /**
     * FilterEvaluator should return {@link Optional.empty()} when the link doesn't match any filters
     */
    @Test
    public void testNoMatchingFilter() throws Exception {
        List<Filter> filters = Lists.newArrayList(Filter.linkNameFilter(FilterEvaluatorTest.NAMENODE_JMX, true), Filter.linkAttributeFilter(FilterEvaluatorTest.SSO, false));
        FilterEvaluator evaluator = new FilterEvaluator(filters);
        Assert.assertEquals(Optional.empty(), evaluator.isVisible(namenodeUi));
    }

    /**
     * Link name filters should be evaluated first
     */
    @Test
    public void testLinkNameFiltersEvaluatedFirst() throws Exception {
        List<Filter> filters = Lists.newArrayList(Filter.acceptAllFilter(false), Filter.linkNameFilter(FilterEvaluatorTest.NAMENODE_UI, true), Filter.linkNameFilter(FilterEvaluatorTest.NAMENODE_JMX, false), Filter.linkAttributeFilter(FilterEvaluatorTest.AUTHENTICATED, false), Filter.linkAttributeFilter(FilterEvaluatorTest.SSO, false));
        FilterEvaluator evaluator = new FilterEvaluator(filters);
        Assert.assertEquals(Optional.of(true), evaluator.isVisible(namenodeUi));
    }

    /**
     * Link attribute filters should be evaluated only if the link does not match any link name filters.
     */
    @Test
    public void testLinkAttributeFiltersEvaluatedSecondly() throws Exception {
        List<Filter> filters = Lists.newArrayList(Filter.acceptAllFilter(false), Filter.linkNameFilter(FilterEvaluatorTest.NAMENODE_JMX, false), Filter.linkAttributeFilter(FilterEvaluatorTest.AUTHENTICATED, true), Filter.linkAttributeFilter(FilterEvaluatorTest.SSO, true));
        FilterEvaluator evaluator = new FilterEvaluator(filters);
        Assert.assertEquals(Optional.of(true), evaluator.isVisible(namenodeUi));
    }

    /**
     * Link attribute filters work with links with null attributes. (No NPE is thrown)
     */
    @Test
    public void testLinkAttributeFiltersWorkWithNullAttributes() throws Exception {
        List<Filter> filters = Lists.newArrayList(Filter.acceptAllFilter(true), Filter.linkAttributeFilter(FilterEvaluatorTest.AUTHENTICATED, false), Filter.linkAttributeFilter(FilterEvaluatorTest.SSO, false));
        FilterEvaluator evaluator = new FilterEvaluator(filters);
        Assert.assertEquals(Optional.of(true), evaluator.isVisible(nameNodeJmx));
    }

    /**
     * If the link matches both a show and hide type link attribute filter, then it will be evaluated as hidden.
     */
    @Test
    public void testHideFilterTakesPrecedence() throws Exception {
        List<Filter> filters = Lists.newArrayList(Filter.linkAttributeFilter(FilterEvaluatorTest.AUTHENTICATED, false), Filter.linkAttributeFilter(FilterEvaluatorTest.SSO, true));
        FilterEvaluator evaluator = new FilterEvaluator(filters);
        namenodeUi.setAttributes(ImmutableList.of(FilterEvaluatorTest.AUTHENTICATED, FilterEvaluatorTest.SSO));
        Assert.assertEquals(Optional.of(false), evaluator.isVisible(namenodeUi));
    }

    /**
     * Accept-all filters are only evaluated if the link does not match any link name or link attribute filters.
     */
    @Test
    public void acceptAllFilterEvaluatedLast() throws Exception {
        List<Filter> filters = Lists.newArrayList(Filter.acceptAllFilter(false), Filter.linkNameFilter(FilterEvaluatorTest.NAMENODE_JMX, true), Filter.linkAttributeFilter(FilterEvaluatorTest.SSO, true));
        FilterEvaluator evaluator = new FilterEvaluator(filters);
        Assert.assertEquals(Optional.of(false), evaluator.isVisible(namenodeUi));
    }

    /**
     * Contradicting link name filters should result in {@link QuickLinksProfileEvaluationException}.
     */
    @Test(expected = QuickLinksProfileEvaluationException.class)
    public void contradictingLinkNameFiltersRejected() throws Exception {
        List<Filter> filters = Lists.newArrayList(Filter.linkNameFilter(FilterEvaluatorTest.NAMENODE_JMX, true), Filter.linkNameFilter(FilterEvaluatorTest.NAMENODE_JMX, false), Filter.linkAttributeFilter(FilterEvaluatorTest.SSO, true));
        new FilterEvaluator(filters);
    }

    /**
     * Contradicting property filters should result in {@link QuickLinksProfileEvaluationException}.
     *
     * @throws Exception
     * 		
     */
    @Test(expected = QuickLinksProfileEvaluationException.class)
    public void contradictingPropertyFiltersRejected() throws Exception {
        List<Filter> filters = Lists.newArrayList(Filter.linkAttributeFilter(FilterEvaluatorTest.SSO, true), Filter.linkAttributeFilter(FilterEvaluatorTest.SSO, false));
        new FilterEvaluator(filters);
    }

    /**
     * Contradicting link attribute filters should result in {@link QuickLinksProfileEvaluationException}.
     */
    @Test(expected = QuickLinksProfileEvaluationException.class)
    public void contradictingLinkAttributeFiltersRejected() throws Exception {
        List<Filter> filters = Lists.newArrayList(Filter.linkAttributeFilter(FilterEvaluatorTest.SSO, true), Filter.linkAttributeFilter(FilterEvaluatorTest.SSO, false));
        new FilterEvaluator(filters);
    }

    /**
     * Contradicting accept-all filters should result in {@link QuickLinksProfileEvaluationException}.
     */
    @Test(expected = QuickLinksProfileEvaluationException.class)
    public void contradictingAcceptAllFiltersRejected() throws Exception {
        List<Filter> filters = Lists.newArrayList(Filter.linkNameFilter(FilterEvaluatorTest.NAMENODE_JMX, true), Filter.linkAttributeFilter(FilterEvaluatorTest.SSO, true), Filter.acceptAllFilter(true), Filter.acceptAllFilter(false));
        new FilterEvaluator(filters);
    }

    /**
     * Duplicate filter declarations are ok if their visibility rule is the same
     */
    @Test
    public void duplicateFiltersAreOkIfDoNotContradict() throws Exception {
        List<Filter> filters = Lists.newArrayList(Filter.acceptAllFilter(true), Filter.acceptAllFilter(true), Filter.linkNameFilter(FilterEvaluatorTest.NAMENODE_JMX, false), Filter.linkNameFilter(FilterEvaluatorTest.NAMENODE_JMX, false), Filter.linkAttributeFilter(FilterEvaluatorTest.SSO, false), Filter.linkAttributeFilter(FilterEvaluatorTest.SSO, false));
        FilterEvaluator evaluator = new FilterEvaluator(filters);
        Assert.assertEquals(Optional.of(true), evaluator.isVisible(namenodeUi));
    }
}

