/**
 * Copyright 1999-2015 dangdang.com.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * </p>
 */
package io.elasticjob.lite.reg.zookeeper;


import io.elasticjob.lite.fixture.EmbedTestingServer;
import java.util.Arrays;
import java.util.Collections;
import junit.framework.TestCase;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public final class ZookeeperRegistryCenterQueryWithoutCacheTest {
    private static final ZookeeperConfiguration ZOOKEEPER_CONFIGURATION = new ZookeeperConfiguration(EmbedTestingServer.getConnectionString(), ZookeeperRegistryCenterQueryWithoutCacheTest.class.getName());

    private static ZookeeperRegistryCenter zkRegCenter;

    @Test
    public void assertGetFromServer() {
        Assert.assertThat(ZookeeperRegistryCenterQueryWithoutCacheTest.zkRegCenter.get("/test"), CoreMatchers.is("test"));
        Assert.assertThat(ZookeeperRegistryCenterQueryWithoutCacheTest.zkRegCenter.get("/test/deep/nested"), CoreMatchers.is("deepNested"));
    }

    @Test
    public void assertGetChildrenKeys() {
        Assert.assertThat(ZookeeperRegistryCenterQueryWithoutCacheTest.zkRegCenter.getChildrenKeys("/test"), CoreMatchers.is(Arrays.asList("deep", "child")));
        Assert.assertThat(ZookeeperRegistryCenterQueryWithoutCacheTest.zkRegCenter.getChildrenKeys("/test/deep"), CoreMatchers.is(Collections.singletonList("nested")));
        Assert.assertThat(ZookeeperRegistryCenterQueryWithoutCacheTest.zkRegCenter.getChildrenKeys("/test/child"), CoreMatchers.is(Collections.<String>emptyList()));
        Assert.assertThat(ZookeeperRegistryCenterQueryWithoutCacheTest.zkRegCenter.getChildrenKeys("/test/notExisted"), CoreMatchers.is(Collections.<String>emptyList()));
    }

    @Test
    public void assertGetNumChildren() {
        Assert.assertThat(ZookeeperRegistryCenterQueryWithoutCacheTest.zkRegCenter.getNumChildren("/test"), CoreMatchers.is(2));
        Assert.assertThat(ZookeeperRegistryCenterQueryWithoutCacheTest.zkRegCenter.getNumChildren("/test/deep"), CoreMatchers.is(1));
        Assert.assertThat(ZookeeperRegistryCenterQueryWithoutCacheTest.zkRegCenter.getNumChildren("/test/child"), CoreMatchers.is(0));
        Assert.assertThat(ZookeeperRegistryCenterQueryWithoutCacheTest.zkRegCenter.getNumChildren("/test/notExisted"), CoreMatchers.is(0));
    }

    @Test
    public void assertIsExisted() {
        Assert.assertTrue(ZookeeperRegistryCenterQueryWithoutCacheTest.zkRegCenter.isExisted("/test"));
        Assert.assertTrue(ZookeeperRegistryCenterQueryWithoutCacheTest.zkRegCenter.isExisted("/test/deep/nested"));
        Assert.assertFalse(ZookeeperRegistryCenterQueryWithoutCacheTest.zkRegCenter.isExisted("/notExisted"));
    }

    @Test
    public void assertGetRegistryCenterTime() {
        long regCenterTime = ZookeeperRegistryCenterQueryWithoutCacheTest.zkRegCenter.getRegistryCenterTime("/_systemTime/current");
        Assert.assertTrue((regCenterTime <= (System.currentTimeMillis())));
        long updatedRegCenterTime = ZookeeperRegistryCenterQueryWithoutCacheTest.zkRegCenter.getRegistryCenterTime("/_systemTime/current");
        System.out.println(((regCenterTime + ",") + updatedRegCenterTime));
        Assert.assertTrue((regCenterTime < updatedRegCenterTime));
    }

    @Test
    public void assertGetWithoutNode() {
        TestCase.assertNull(ZookeeperRegistryCenterQueryWithoutCacheTest.zkRegCenter.get("/notExisted"));
    }
}

