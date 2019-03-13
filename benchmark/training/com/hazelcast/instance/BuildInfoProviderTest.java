/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.instance;


import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.QuickTest;
import com.hazelcast.util.StringUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastSerialClassRunner.class)
@Category(QuickTest.class)
public class BuildInfoProviderTest extends HazelcastTestSupport {
    @Test
    public void testConstructor() {
        HazelcastTestSupport.assertUtilityConstructor(BuildInfoProvider.class);
    }

    @Test
    public void testOverrideBuildNumber() {
        System.setProperty("hazelcast.build", "2");
        BuildInfo buildInfo = BuildInfoProvider.getBuildInfo();
        String version = buildInfo.getVersion();
        String build = buildInfo.getBuild();
        int buildNumber = buildInfo.getBuildNumber();
        Assert.assertTrue(buildInfo.toString(), StringUtil.VERSION_PATTERN.matcher(version).matches());
        Assert.assertEquals("2", build);
        Assert.assertEquals(2, buildNumber);
        Assert.assertFalse(buildInfo.toString(), buildInfo.isEnterprise());
    }

    @Test
    public void testReadValues() {
        BuildInfo buildInfo = BuildInfoProvider.getBuildInfo();
        String version = buildInfo.getVersion();
        String build = buildInfo.getBuild();
        int buildNumber = buildInfo.getBuildNumber();
        Assert.assertTrue(buildInfo.toString(), StringUtil.VERSION_PATTERN.matcher(version).matches());
        Assert.assertEquals(buildInfo.toString(), buildNumber, Integer.parseInt(build));
        Assert.assertFalse(buildInfo.toString(), buildInfo.isEnterprise());
    }

    @Test
    public void testCalculateVersion() {
        Assert.assertEquals((-1), BuildInfo.calculateVersion(null));
        Assert.assertEquals((-1), BuildInfo.calculateVersion(""));
        Assert.assertEquals((-1), BuildInfo.calculateVersion("a.3.7.5"));
        Assert.assertEquals((-1), BuildInfo.calculateVersion("3.a.5"));
        Assert.assertEquals((-1), BuildInfo.calculateVersion("3,7.5"));
        Assert.assertEquals((-1), BuildInfo.calculateVersion("3.7,5"));
        Assert.assertEquals((-1), BuildInfo.calculateVersion("10.99.RC1"));
        Assert.assertEquals(30700, BuildInfo.calculateVersion("3.7-SNAPSHOT"));
        Assert.assertEquals(30702, BuildInfo.calculateVersion("3.7.2"));
        Assert.assertEquals(30702, BuildInfo.calculateVersion("3.7.2-SNAPSHOT"));
        Assert.assertEquals(109902, BuildInfo.calculateVersion("10.99.2-SNAPSHOT"));
        Assert.assertEquals(19930, BuildInfo.calculateVersion("1.99.30"));
        Assert.assertEquals(109930, BuildInfo.calculateVersion("10.99.30-SNAPSHOT"));
        Assert.assertEquals(109900, BuildInfo.calculateVersion("10.99-RC1"));
    }

    @Test
    public void testOverrideBuildVersion() {
        System.setProperty(BuildInfoProvider.HAZELCAST_INTERNAL_OVERRIDE_VERSION, "99.99.99");
        BuildInfo buildInfo = BuildInfoProvider.getBuildInfo();
        Assert.assertEquals("99.99.99", buildInfo.getVersion());
        System.clearProperty(BuildInfoProvider.HAZELCAST_INTERNAL_OVERRIDE_VERSION);
    }

    @Test
    public void testOverrideEdition() {
        System.setProperty(BuildInfoProvider.HAZELCAST_INTERNAL_OVERRIDE_ENTERPRISE, "true");
        BuildInfo buildInfo = BuildInfoProvider.getBuildInfo();
        Assert.assertTrue(buildInfo.isEnterprise());
        System.clearProperty(BuildInfoProvider.HAZELCAST_INTERNAL_OVERRIDE_ENTERPRISE);
    }

    @Test
    public void testEdition_whenNotOverridden() {
        BuildInfo buildInfo = BuildInfoProvider.getBuildInfo();
        Assert.assertFalse(buildInfo.isEnterprise());
    }
}

