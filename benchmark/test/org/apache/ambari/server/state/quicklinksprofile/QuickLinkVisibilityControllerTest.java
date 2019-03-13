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
import java.util.Optional;
import org.apache.ambari.server.state.quicklinks.Link;
import org.junit.Assert;
import org.junit.Test;


public class QuickLinkVisibilityControllerTest {
    static final String AUTHENTICATED = "authenticated";

    static final String SSO = "sso";

    static final String NAMENODE = "NAMENODE";

    static final String HDFS = "HDFS";

    public static final String YARN = "YARN";

    static final String NAMENODE_UI = "namenode_ui";

    static final String NAMENODE_LOGS = "namenode_logs";

    static final String NAMENODE_JMX = "namenode_jmx";

    static final String THREAD_STACKS = "Thread Stacks";

    static final String LINK_URL_1 = "www.overridden.org/1";

    static final String LINK_URL_2 = "www.overridden.org/2";

    static final String LINK_URL_3 = "www.overridden.org/3";

    private Link namenodeUi;

    private Link namenodeLogs;

    private Link namenodeJmx;

    private Link threadStacks;

    public QuickLinkVisibilityControllerTest() {
        namenodeUi = QuickLinkVisibilityControllerTest.link(QuickLinkVisibilityControllerTest.NAMENODE_UI, QuickLinkVisibilityControllerTest.NAMENODE, ImmutableList.of(QuickLinkVisibilityControllerTest.AUTHENTICATED));
        namenodeLogs = QuickLinkVisibilityControllerTest.link(QuickLinkVisibilityControllerTest.NAMENODE_LOGS, QuickLinkVisibilityControllerTest.NAMENODE, null);
        namenodeJmx = QuickLinkVisibilityControllerTest.link(QuickLinkVisibilityControllerTest.NAMENODE_JMX, QuickLinkVisibilityControllerTest.NAMENODE, null);
        threadStacks = QuickLinkVisibilityControllerTest.link(QuickLinkVisibilityControllerTest.THREAD_STACKS, QuickLinkVisibilityControllerTest.NAMENODE, null);
    }

    /**
     * Test to prove that {@link DefaultQuickLinkVisibilityController} can accept quicklink profiles with null values.
     */
    @Test
    public void testNullsAreAccepted() throws Exception {
        QuickLinksProfile profile = QuickLinksProfile.create(ImmutableList.of(Filter.acceptAllFilter(true)), null);
        DefaultQuickLinkVisibilityController evaluator = new DefaultQuickLinkVisibilityController(profile);
        evaluator.isVisible(QuickLinkVisibilityControllerTest.HDFS, namenodeUi);// should not throw NPE

        Service service = Service.create(QuickLinkVisibilityControllerTest.HDFS, ImmutableList.of(Filter.acceptAllFilter(true)), null);
        profile = QuickLinksProfile.create(null, ImmutableList.of(service));
        evaluator = new DefaultQuickLinkVisibilityController(profile);
        evaluator.isVisible(QuickLinkVisibilityControllerTest.HDFS, namenodeUi);// should not throw NPE

    }

    /**
     * Quicklinks profile must contain at least one filter (can be on any level: global/component/service), otherwise
     * an exception is thrown.
     */
    @Test(expected = QuickLinksProfileEvaluationException.class)
    public void testProfileMustContainAtLeastOneFilter() throws Exception {
        Component component = Component.create("NAMENODE", null);
        Service service = Service.create(QuickLinkVisibilityControllerTest.HDFS, null, ImmutableList.of(component));
        QuickLinksProfile profile = QuickLinksProfile.create(null, ImmutableList.of(service));
        QuickLinkVisibilityController evaluator = new DefaultQuickLinkVisibilityController(profile);
    }

    /**
     * Test to prove that {@link Link}'s with unset {@code componentName} fields are handled properly.
     */
    @Test
    public void testLinkWithNoComponentField() throws Exception {
        Component component = Component.create(QuickLinkVisibilityControllerTest.NAMENODE, ImmutableList.of(Filter.linkNameFilter(QuickLinkVisibilityControllerTest.NAMENODE_UI, true)));
        Service service = Service.create(QuickLinkVisibilityControllerTest.HDFS, ImmutableList.of(), ImmutableList.of(component));
        QuickLinksProfile profile = QuickLinksProfile.create(ImmutableList.of(), ImmutableList.of(service));
        DefaultQuickLinkVisibilityController evaluator = new DefaultQuickLinkVisibilityController(profile);
        namenodeUi.setComponentName(null);
        Assert.assertFalse("Link should be hidden as there are no applicable filters", evaluator.isVisible(QuickLinkVisibilityControllerTest.HDFS, namenodeUi));
    }

    /**
     * Test to prove that component level filters are evaluated first.
     */
    @Test
    public void testComponentLevelFiltersEvaluatedFirst() throws Exception {
        Component component = Component.create(QuickLinkVisibilityControllerTest.NAMENODE, ImmutableList.of(Filter.linkAttributeFilter(QuickLinkVisibilityControllerTest.AUTHENTICATED, true)));
        Service service = Service.create(QuickLinkVisibilityControllerTest.HDFS, ImmutableList.of(Filter.linkAttributeFilter(QuickLinkVisibilityControllerTest.AUTHENTICATED, false)), ImmutableList.of(component));
        QuickLinksProfile profile = QuickLinksProfile.create(ImmutableList.of(Filter.acceptAllFilter(false)), ImmutableList.of(service));
        DefaultQuickLinkVisibilityController evaluator = new DefaultQuickLinkVisibilityController(profile);
        Assert.assertTrue("Component level filter should have been applied.", evaluator.isVisible(QuickLinkVisibilityControllerTest.HDFS, namenodeUi));
    }

    /**
     * Test to prove that service level filters are evaluated secondly.
     */
    @Test
    public void testServiceLevelFiltersEvaluatedSecondly() throws Exception {
        Component component = Component.create(QuickLinkVisibilityControllerTest.NAMENODE, ImmutableList.of(Filter.linkAttributeFilter(QuickLinkVisibilityControllerTest.SSO, false)));
        Service service = Service.create(QuickLinkVisibilityControllerTest.HDFS, ImmutableList.of(Filter.linkAttributeFilter(QuickLinkVisibilityControllerTest.AUTHENTICATED, true)), ImmutableList.of(component));
        QuickLinksProfile profile = QuickLinksProfile.create(ImmutableList.of(Filter.acceptAllFilter(false)), ImmutableList.of(service));
        DefaultQuickLinkVisibilityController evaluator = new DefaultQuickLinkVisibilityController(profile);
        Assert.assertTrue("Component level filter should have been applied.", evaluator.isVisible(QuickLinkVisibilityControllerTest.HDFS, namenodeUi));
    }

    /**
     * Test to prove that global filters are evaluated last.
     */
    @Test
    public void testGlobalFiltersEvaluatedLast() throws Exception {
        Component component = Component.create(QuickLinkVisibilityControllerTest.NAMENODE, ImmutableList.of(Filter.linkAttributeFilter(QuickLinkVisibilityControllerTest.SSO, false)));
        Service service = Service.create(QuickLinkVisibilityControllerTest.HDFS, ImmutableList.of(Filter.linkAttributeFilter(QuickLinkVisibilityControllerTest.SSO, false)), ImmutableList.of(component));
        QuickLinksProfile profile = QuickLinksProfile.create(ImmutableList.of(Filter.acceptAllFilter(true)), ImmutableList.of(service));
        DefaultQuickLinkVisibilityController evaluator = new DefaultQuickLinkVisibilityController(profile);
        Assert.assertTrue("Global filter should have been applied.", evaluator.isVisible(QuickLinkVisibilityControllerTest.HDFS, namenodeUi));
    }

    /**
     * Test to prove that the link is hidden if no filters apply.
     */
    @Test
    public void testNoMatchingRule() throws Exception {
        Component component1 = Component.create(QuickLinkVisibilityControllerTest.NAMENODE, ImmutableList.of(Filter.linkAttributeFilter(QuickLinkVisibilityControllerTest.SSO, true)));
        Component component2 = Component.create("DATANODE", ImmutableList.of(Filter.acceptAllFilter(true)));
        Service service1 = Service.create(QuickLinkVisibilityControllerTest.HDFS, ImmutableList.of(Filter.linkAttributeFilter(QuickLinkVisibilityControllerTest.SSO, true)), ImmutableList.of(component1, component2));
        Service service2 = Service.create("YARN", ImmutableList.of(Filter.acceptAllFilter(true)), ImmutableList.of());
        QuickLinksProfile profile = QuickLinksProfile.create(ImmutableList.of(Filter.linkAttributeFilter(QuickLinkVisibilityControllerTest.SSO, true)), ImmutableList.of(service1, service2));
        DefaultQuickLinkVisibilityController evaluator = new DefaultQuickLinkVisibilityController(profile);
        Assert.assertFalse("No filters should have been applied, so default false should have been returned.", evaluator.isVisible(QuickLinkVisibilityControllerTest.HDFS, namenodeUi));
    }

    @Test
    public void testUrlOverride() throws Exception {
        Component nameNode = Component.create(QuickLinkVisibilityControllerTest.NAMENODE, ImmutableList.of(Filter.linkNameFilter(QuickLinkVisibilityControllerTest.NAMENODE_UI, true), Filter.linkNameFilter(QuickLinkVisibilityControllerTest.NAMENODE_LOGS, QuickLinkVisibilityControllerTest.LINK_URL_1, true)));
        Service hdfs = Service.create(QuickLinkVisibilityControllerTest.HDFS, ImmutableList.of(Filter.linkNameFilter(QuickLinkVisibilityControllerTest.NAMENODE_JMX, QuickLinkVisibilityControllerTest.LINK_URL_2, true)), ImmutableList.of(nameNode));
        QuickLinksProfile profile = QuickLinksProfile.create(ImmutableList.of(Filter.linkNameFilter(QuickLinkVisibilityControllerTest.THREAD_STACKS, QuickLinkVisibilityControllerTest.LINK_URL_3, true)), ImmutableList.of(hdfs));
        DefaultQuickLinkVisibilityController evaluator = new DefaultQuickLinkVisibilityController(profile);
        Assert.assertEquals(Optional.empty(), evaluator.getUrlOverride(QuickLinkVisibilityControllerTest.HDFS, namenodeUi));
        Assert.assertEquals(Optional.of(QuickLinkVisibilityControllerTest.LINK_URL_1), evaluator.getUrlOverride(QuickLinkVisibilityControllerTest.HDFS, namenodeLogs));
        Assert.assertEquals(Optional.of(QuickLinkVisibilityControllerTest.LINK_URL_2), evaluator.getUrlOverride(QuickLinkVisibilityControllerTest.HDFS, namenodeJmx));
        // component name doesn't matter
        namenodeLogs.setComponentName(null);
        Assert.assertEquals(Optional.of(QuickLinkVisibilityControllerTest.LINK_URL_1), evaluator.getUrlOverride(QuickLinkVisibilityControllerTest.HDFS, namenodeLogs));
        // no override for links not in the profile
        Assert.assertEquals(Optional.empty(), evaluator.getUrlOverride(QuickLinkVisibilityControllerTest.YARN, QuickLinkVisibilityControllerTest.link("resourcemanager_ui", "RESOURCEMANAGER", null)));
        // url overrides in global filters are ignored
        Assert.assertEquals(Optional.empty(), evaluator.getUrlOverride(QuickLinkVisibilityControllerTest.HDFS, threadStacks));
    }

    @Test
    public void testUrlOverride_duplicateDefinitions() throws Exception {
        // same link is defined twice for a service
        Component nameNode = Component.create(QuickLinkVisibilityControllerTest.NAMENODE, ImmutableList.of(Filter.linkNameFilter(QuickLinkVisibilityControllerTest.NAMENODE_UI, QuickLinkVisibilityControllerTest.LINK_URL_1, true)));// this will override service level setting for the same link

        Service hdfs = // same link on service level with different url
        Service.create(QuickLinkVisibilityControllerTest.HDFS, ImmutableList.of(Filter.linkNameFilter(QuickLinkVisibilityControllerTest.NAMENODE_UI, QuickLinkVisibilityControllerTest.LINK_URL_2, true)), ImmutableList.of(nameNode));
        Service yarn = // this belongs to an other service so doesn't affect outcome
        Service.create(QuickLinkVisibilityControllerTest.YARN, ImmutableList.of(Filter.linkNameFilter(QuickLinkVisibilityControllerTest.NAMENODE_UI, QuickLinkVisibilityControllerTest.LINK_URL_3, true)), ImmutableList.of(nameNode));
        QuickLinksProfile profile = QuickLinksProfile.create(ImmutableList.of(), ImmutableList.of(hdfs));
        DefaultQuickLinkVisibilityController evaluator = new DefaultQuickLinkVisibilityController(profile);
        Assert.assertEquals(Optional.of(QuickLinkVisibilityControllerTest.LINK_URL_1), evaluator.getUrlOverride(QuickLinkVisibilityControllerTest.HDFS, namenodeUi));
    }
}

