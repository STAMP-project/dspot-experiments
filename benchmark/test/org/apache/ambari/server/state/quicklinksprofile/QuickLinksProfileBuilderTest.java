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


import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import java.util.Map;
import java.util.Set;
import org.junit.Test;


public class QuickLinksProfileBuilderTest {
    @Test
    public void testBuildProfileOnlyGlobalFilters() throws Exception {
        Set<Map<String, String>> filters = Sets.newHashSet(QuickLinksProfileBuilderTest.filter("namenode_ui", null, true), QuickLinksProfileBuilderTest.filter(null, "sso", true), QuickLinksProfileBuilderTest.filter(null, null, false));
        String profileJson = new QuickLinksProfileBuilder().buildQuickLinksProfile(filters, null);
        // verify
        QuickLinksProfile profile = new QuickLinksProfileParser().parse(profileJson.getBytes());
        QuickLinksProfileBuilderTest.assertFilterExists(profile, null, null, Filter.linkNameFilter("namenode_ui", true));
        QuickLinksProfileBuilderTest.assertFilterExists(profile, null, null, Filter.linkAttributeFilter("sso", true));
        QuickLinksProfileBuilderTest.assertFilterExists(profile, null, null, Filter.acceptAllFilter(false));
    }

    @Test
    public void testBuildProfileOnlyServiceFilters() throws Exception {
        Map<String, Object> nameNode = QuickLinksProfileBuilderTest.component("NAMENODE", Sets.newHashSet(QuickLinksProfileBuilderTest.filter("namenode_ui", null, false)));
        Map<String, Object> hdfs = QuickLinksProfileBuilderTest.service("HDFS", Sets.newHashSet(nameNode), Sets.newHashSet(QuickLinksProfileBuilderTest.filter(null, "sso", true)));
        Set<Map<String, Object>> services = Sets.newHashSet(hdfs);
        String profileJson = new QuickLinksProfileBuilder().buildQuickLinksProfile(null, services);
        // verify
        QuickLinksProfile profile = new QuickLinksProfileParser().parse(profileJson.getBytes());
        QuickLinksProfileBuilderTest.assertFilterExists(profile, "HDFS", "NAMENODE", Filter.linkNameFilter("namenode_ui", false));
        QuickLinksProfileBuilderTest.assertFilterExists(profile, "HDFS", null, Filter.linkAttributeFilter("sso", true));
    }

    @Test
    public void testBuildProfileBothGlobalAndServiceFilters() throws Exception {
        Set<Map<String, String>> globalFilters = Sets.newHashSet(QuickLinksProfileBuilderTest.filter(null, null, false));
        Map<String, Object> nameNode = QuickLinksProfileBuilderTest.component("NAMENODE", Sets.newHashSet(QuickLinksProfileBuilderTest.filter("namenode_ui", null, false), QuickLinksProfileBuilderTest.filter("namenode_logs", null, "http://customlink.org/namenode_logs", true)));
        Map<String, Object> hdfs = QuickLinksProfileBuilderTest.service("HDFS", Sets.newHashSet(nameNode), Sets.newHashSet(QuickLinksProfileBuilderTest.filter(null, "sso", true)));
        Set<Map<String, Object>> services = Sets.newHashSet(hdfs);
        String profileJson = new QuickLinksProfileBuilder().buildQuickLinksProfile(globalFilters, services);
        // verify
        QuickLinksProfile profile = new QuickLinksProfileParser().parse(profileJson.getBytes());
        QuickLinksProfileBuilderTest.assertFilterExists(profile, null, null, Filter.acceptAllFilter(false));
        QuickLinksProfileBuilderTest.assertFilterExists(profile, "HDFS", "NAMENODE", Filter.linkNameFilter("namenode_ui", false));
        QuickLinksProfileBuilderTest.assertFilterExists(profile, "HDFS", "NAMENODE", Filter.linkNameFilter("namenode_ui", false));
        QuickLinksProfileBuilderTest.assertFilterExists(profile, "HDFS", "NAMENODE", Filter.linkNameFilter("namenode_logs", "http://customlink.org/namenode_logs", true));
        QuickLinksProfileBuilderTest.assertFilterExists(profile, "HDFS", null, Filter.linkAttributeFilter("sso", true));
    }

    @Test(expected = QuickLinksProfileEvaluationException.class)
    public void testBuildProfileBadInputStructure() throws Exception {
        new QuickLinksProfileBuilder().buildQuickLinksProfile("Hello", "World");
    }

    @Test(expected = QuickLinksProfileEvaluationException.class)
    public void testBuildProfileMissingDataServiceName() throws Exception {
        Map<String, Object> nameNode = QuickLinksProfileBuilderTest.component("NAMENODE", Sets.newHashSet(QuickLinksProfileBuilderTest.filter("namenode_ui", null, false)));
        Map<String, Object> hdfs = // intentionally omitting service name
        QuickLinksProfileBuilderTest.service(null, Sets.newHashSet(nameNode), Sets.newHashSet(QuickLinksProfileBuilderTest.filter(null, "sso", true)));
        Set<Map<String, Object>> services = Sets.newHashSet(hdfs);
        new QuickLinksProfileBuilder().buildQuickLinksProfile(null, services);
    }

    @Test(expected = QuickLinksProfileEvaluationException.class)
    public void testBuildProfileMissingDataComponentName() throws Exception {
        Map<String, Object> nameNode = // intentionally omitting component name
        QuickLinksProfileBuilderTest.component(null, Sets.newHashSet(QuickLinksProfileBuilderTest.filter("namenode_ui", null, false)));
        Map<String, Object> hdfs = QuickLinksProfileBuilderTest.service("HDFS", Sets.newHashSet(nameNode), Sets.newHashSet(QuickLinksProfileBuilderTest.filter(null, "sso", true)));
        Set<Map<String, Object>> services = Sets.newHashSet(hdfs);
        new QuickLinksProfileBuilder().buildQuickLinksProfile(null, services);
    }

    @Test(expected = QuickLinksProfileEvaluationException.class)
    public void testBuildProfileInvalidProfileDefiniton_contradictingFilters() throws Exception {
        // Contradicting rules in the profile
        Set<Map<String, String>> filters = Sets.newHashSet(QuickLinksProfileBuilderTest.filter(null, "sso", true), QuickLinksProfileBuilderTest.filter(null, "sso", false));
        new QuickLinksProfileBuilder().buildQuickLinksProfile(filters, null);
    }

    @Test(expected = QuickLinksProfileEvaluationException.class)
    public void testBuildProfileInvalidProfileDefiniton_invalidAttribute() throws Exception {
        Map<String, String> badFilter = ImmutableMap.of("visible", "true", "linkkk_atirbuteee", "sso");
        Set<Map<String, String>> filters = Sets.newHashSet(badFilter);
        new QuickLinksProfileBuilder().buildQuickLinksProfile(filters, null);
    }
}

