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
package org.apache.ambari.server.stack;


import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Multiset;
import java.util.List;
import java.util.Set;
import org.apache.ambari.server.state.RepositoryInfo;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for StackModule
 */
public class StackModuleTest {
    @Test
    public void stackServiceReposAreRead() throws Exception {
        StackModule sm = StackModuleTest.createStackModule("FooBar", "2.4", Optional.of(Lists.newArrayList(repoInfo("foo", "1.0.1", "http://foo.org"))), Lists.newArrayList(repoInfo("bar", "2.0.1", "http://bar.org")));
        Set<String> repoIds = getIds(sm.getModuleInfo().getRepositories());
        Assert.assertEquals(ImmutableSet.of("foo:1.0.1", "bar:2.0.1"), repoIds);
    }

    /**
     * If more add-on services define the same repo, the duplicate repo definitions should be disregarded.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void duplicateStackServiceReposAreDiscarded() throws Exception {
        StackModule sm = // stack repos
        // stack service repos
        // These two should be preserved. even though duplicates, the contents are the same
        // These should be dropped as the names are the same but contents are different
        // The first one should be dropped (overrides a stack repo), the rest only generates warnings (duplicate urls)
        StackModuleTest.createStackModule("FooBar", "2.4", Optional.of(Lists.newArrayList(repoInfo("StackRepoA", "1.1.1", "http://repos.org/stackrepoA"), repoInfo("StackRepoB", "2.2.2", "http://repos.org/stackrepoB"))), Lists.newArrayList(repoInfo("serviceRepoA", "1.0.0", "http://bar.org/1_0_0")), Lists.newArrayList(repoInfo("serviceRepoA", "1.0.0", "http://bar.org/1_0_0")), Lists.newArrayList(repoInfo("serviceRepoB", "1.2.1", "http://bar.org/1_1_1")), Lists.newArrayList(repoInfo("serviceRepoB", "1.2.3", "http://bar.org/1_1_1")), Lists.newArrayList(repoInfo("StackRepoA", "2.0.0", "http://repos.org/stackrepoA_200"), repoInfo("ShouldBeJustAWarning1", "3.1.1", "http://repos.org/stackrepoA"), repoInfo("ShouldBeJustAWarning2", "1.0.0", "http://bar.org/1_0_0")));
        List<RepositoryInfo> repos = sm.getModuleInfo().getRepositories();
        Set<String> repoIds = getIds(repos);
        Assert.assertEquals("Unexpected number of repos. Each repo should be added only once", repoIds.size(), repos.size());
        Assert.assertEquals("Unexpected repositories", ImmutableSet.of("StackRepoA:1.1.1", "StackRepoB:2.2.2", "serviceRepoA:1.0.0", "ShouldBeJustAWarning1:3.1.1", "ShouldBeJustAWarning2:1.0.0"), repoIds);
    }

    @Test
    public void serviceReposAreProcessedEvenIfNoStackRepo() throws Exception {
        StackModule sm = StackModuleTest.createStackModule("FooBar", "2.4", Optional.absent(), Lists.newArrayList(repoInfo("bar", "2.0.1", "http://bar.org")));
        Set<String> repoIds = getIds(sm.getModuleInfo().getRepositories());
        Assert.assertEquals(ImmutableSet.of("bar:2.0.1"), repoIds);
    }

    /**
     * If two add-on services define the same repo, the repo should be disregarded.
     * This applies per os, so the same repo can be defined for multiple os'es (e.g redhat5 and redhat6)
     *
     * @throws Exception
     * 		
     */
    @Test
    public void duplicateStackServiceReposAreCheckedPerOs() throws Exception {
        StackModule sm = StackModuleTest.createStackModule("FooBar", "2.4", Optional.absent(), Lists.newArrayList(repoInfo("bar", "2.0.1", "http://bar.org", "centos6")), Lists.newArrayList(repoInfo("bar", "2.0.1", "http://bar.org", "centos7")));
        Multiset<String> repoIds = getIdsMultiple(sm.getModuleInfo().getRepositories());
        Assert.assertEquals("Repo should be occur exactly twice, once for each os type.", ImmutableMultiset.of("bar:2.0.1", "bar:2.0.1"), repoIds);
    }

    @Test
    public void removedServicesInitialValue() throws Exception {
        StackModule sm = StackModuleTest.createStackModule("FooBar", "2.4", Optional.absent(), Lists.newArrayList(repoInfo("bar", "2.0.1", "http://bar.org", "centos6")), Lists.newArrayList(repoInfo("bar", "2.0.1", "http://bar.org", "centos7")));
        List<String> removedServices = sm.getModuleInfo().getRemovedServices();
        Assert.assertEquals(removedServices.size(), 0);
    }

    @Test
    public void servicesWithNoConfigsInitialValue() throws Exception {
        StackModule sm = StackModuleTest.createStackModule("FooBar", "2.4", Optional.absent(), Lists.newArrayList(repoInfo("bar", "2.0.1", "http://bar.org", "centos6")), Lists.newArrayList(repoInfo("bar", "2.0.1", "http://bar.org", "centos7")));
        List<String> servicesWithNoConfigs = sm.getModuleInfo().getServicesWithNoConfigs();
        Assert.assertEquals(servicesWithNoConfigs.size(), 0);
    }
}

