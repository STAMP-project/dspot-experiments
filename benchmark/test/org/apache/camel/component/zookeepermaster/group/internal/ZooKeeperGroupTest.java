/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.zookeepermaster.group.internal;


import java.util.List;
import java.util.Map;
import org.apache.camel.component.zookeepermaster.group.NodeState;
import org.apache.curator.framework.CuratorFramework;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class ZooKeeperGroupTest {
    private static final String PATH = "/singletons/test/" + (ZooKeeperGroupTest.class.getSimpleName());

    private CuratorFramework curator;

    private ZooKeeperGroup<NodeState> group;

    @Test
    public void testMembers() throws Exception {
        ZooKeeperGroupTest.putChildData(group, ((ZooKeeperGroupTest.PATH) + "/001"), "container1");
        ZooKeeperGroupTest.putChildData(group, ((ZooKeeperGroupTest.PATH) + "/002"), "container2");
        ZooKeeperGroupTest.putChildData(group, ((ZooKeeperGroupTest.PATH) + "/003"), "container3");
        Map<String, NodeState> members = group.members();
        Assert.assertThat(members.size(), CoreMatchers.equalTo(3));
        Assert.assertThat(members.get(((ZooKeeperGroupTest.PATH) + "/001")).getContainer(), CoreMatchers.equalTo("container1"));
        Assert.assertThat(members.get(((ZooKeeperGroupTest.PATH) + "/002")).getContainer(), CoreMatchers.equalTo("container2"));
        Assert.assertThat(members.get(((ZooKeeperGroupTest.PATH) + "/003")).getContainer(), CoreMatchers.equalTo("container3"));
    }

    @Test
    public void testMembersWithStaleNodes() throws Exception {
        ZooKeeperGroupTest.putChildData(group, ((ZooKeeperGroupTest.PATH) + "/001"), "container1");// stale

        ZooKeeperGroupTest.putChildData(group, ((ZooKeeperGroupTest.PATH) + "/002"), "container1");
        ZooKeeperGroupTest.putChildData(group, ((ZooKeeperGroupTest.PATH) + "/003"), "container2");// stale

        ZooKeeperGroupTest.putChildData(group, ((ZooKeeperGroupTest.PATH) + "/004"), "container3");// stale

        ZooKeeperGroupTest.putChildData(group, ((ZooKeeperGroupTest.PATH) + "/005"), "container2");
        ZooKeeperGroupTest.putChildData(group, ((ZooKeeperGroupTest.PATH) + "/006"), "container3");
        Map<String, NodeState> members = group.members();
        Assert.assertThat(members.size(), CoreMatchers.equalTo(3));
        Assert.assertThat(members.get(((ZooKeeperGroupTest.PATH) + "/002")).getContainer(), CoreMatchers.equalTo("container1"));
        Assert.assertThat(members.get(((ZooKeeperGroupTest.PATH) + "/005")).getContainer(), CoreMatchers.equalTo("container2"));
        Assert.assertThat(members.get(((ZooKeeperGroupTest.PATH) + "/006")).getContainer(), CoreMatchers.equalTo("container3"));
    }

    @Test
    public void testIsMaster() throws Exception {
        ZooKeeperGroupTest.putChildData(group, ((ZooKeeperGroupTest.PATH) + "/001"), "container1");
        ZooKeeperGroupTest.putChildData(group, ((ZooKeeperGroupTest.PATH) + "/002"), "container2");
        ZooKeeperGroupTest.putChildData(group, ((ZooKeeperGroupTest.PATH) + "/003"), "container3");
        group.setId(((ZooKeeperGroupTest.PATH) + "/001"));
        Assert.assertThat(group.isMaster(), CoreMatchers.equalTo(true));
        group.setId(((ZooKeeperGroupTest.PATH) + "/002"));
        Assert.assertThat(group.isMaster(), CoreMatchers.equalTo(false));
    }

    @Test
    public void testIsMasterWithStaleNodes1() throws Exception {
        ZooKeeperGroupTest.putChildData(group, ((ZooKeeperGroupTest.PATH) + "/001"), "container1");// stale

        ZooKeeperGroupTest.putChildData(group, ((ZooKeeperGroupTest.PATH) + "/002"), "container1");
        ZooKeeperGroupTest.putChildData(group, ((ZooKeeperGroupTest.PATH) + "/003"), "container2");// stale

        ZooKeeperGroupTest.putChildData(group, ((ZooKeeperGroupTest.PATH) + "/004"), "container3");// stale

        ZooKeeperGroupTest.putChildData(group, ((ZooKeeperGroupTest.PATH) + "/005"), "container2");
        ZooKeeperGroupTest.putChildData(group, ((ZooKeeperGroupTest.PATH) + "/006"), "container3");
        group.setId(((ZooKeeperGroupTest.PATH) + "/002"));
        Assert.assertThat(group.isMaster(), CoreMatchers.equalTo(true));
        group.setId(((ZooKeeperGroupTest.PATH) + "/005"));
        Assert.assertThat(group.isMaster(), CoreMatchers.equalTo(false));
    }

    @Test
    public void testIsMasterWithStaleNodes2() throws Exception {
        ZooKeeperGroupTest.putChildData(group, ((ZooKeeperGroupTest.PATH) + "/001"), "container1");// stale

        ZooKeeperGroupTest.putChildData(group, ((ZooKeeperGroupTest.PATH) + "/002"), "container2");
        ZooKeeperGroupTest.putChildData(group, ((ZooKeeperGroupTest.PATH) + "/003"), "container1");
        ZooKeeperGroupTest.putChildData(group, ((ZooKeeperGroupTest.PATH) + "/004"), "container3");// stale

        ZooKeeperGroupTest.putChildData(group, ((ZooKeeperGroupTest.PATH) + "/005"), "container3");
        group.setId(((ZooKeeperGroupTest.PATH) + "/002"));
        Assert.assertThat(group.isMaster(), CoreMatchers.equalTo(true));
        group.setId(((ZooKeeperGroupTest.PATH) + "/003"));
        Assert.assertThat(group.isMaster(), CoreMatchers.equalTo(false));
    }

    @Test
    public void testMaster() throws Exception {
        ZooKeeperGroupTest.putChildData(group, ((ZooKeeperGroupTest.PATH) + "/001"), "container1");
        ZooKeeperGroupTest.putChildData(group, ((ZooKeeperGroupTest.PATH) + "/002"), "container2");
        ZooKeeperGroupTest.putChildData(group, ((ZooKeeperGroupTest.PATH) + "/003"), "container3");
        NodeState master = group.master();
        Assert.assertThat(master, CoreMatchers.notNullValue());
        Assert.assertThat(master.getContainer(), CoreMatchers.equalTo("container1"));
    }

    @Test
    public void testMasterWithStaleNodes1() throws Exception {
        ZooKeeperGroupTest.putChildData(group, ((ZooKeeperGroupTest.PATH) + "/001"), "container1");// stale

        ZooKeeperGroupTest.putChildData(group, ((ZooKeeperGroupTest.PATH) + "/002"), "container1");
        ZooKeeperGroupTest.putChildData(group, ((ZooKeeperGroupTest.PATH) + "/003"), "container2");// stale

        ZooKeeperGroupTest.putChildData(group, ((ZooKeeperGroupTest.PATH) + "/004"), "container3");// stale

        ZooKeeperGroupTest.putChildData(group, ((ZooKeeperGroupTest.PATH) + "/005"), "container2");
        ZooKeeperGroupTest.putChildData(group, ((ZooKeeperGroupTest.PATH) + "/006"), "container3");
        NodeState master = group.master();
        Assert.assertThat(master, CoreMatchers.notNullValue());
        Assert.assertThat(master.getContainer(), CoreMatchers.equalTo("container1"));
    }

    @Test
    public void testMasterWithStaleNodes2() throws Exception {
        ZooKeeperGroupTest.putChildData(group, ((ZooKeeperGroupTest.PATH) + "/001"), "container1");// stale

        ZooKeeperGroupTest.putChildData(group, ((ZooKeeperGroupTest.PATH) + "/002"), "container2");
        ZooKeeperGroupTest.putChildData(group, ((ZooKeeperGroupTest.PATH) + "/003"), "container1");
        ZooKeeperGroupTest.putChildData(group, ((ZooKeeperGroupTest.PATH) + "/004"), "container3");// stale

        ZooKeeperGroupTest.putChildData(group, ((ZooKeeperGroupTest.PATH) + "/005"), "container3");
        NodeState master = group.master();
        Assert.assertThat(master, CoreMatchers.notNullValue());
        Assert.assertThat(master.getContainer(), CoreMatchers.equalTo("container2"));
    }

    @Test
    public void testSlaves() throws Exception {
        ZooKeeperGroupTest.putChildData(group, ((ZooKeeperGroupTest.PATH) + "/001"), "container1");
        ZooKeeperGroupTest.putChildData(group, ((ZooKeeperGroupTest.PATH) + "/002"), "container2");
        ZooKeeperGroupTest.putChildData(group, ((ZooKeeperGroupTest.PATH) + "/003"), "container3");
        List<NodeState> slaves = group.slaves();
        Assert.assertThat(slaves.size(), CoreMatchers.equalTo(2));
        Assert.assertThat(slaves.get(0).getContainer(), CoreMatchers.equalTo("container2"));
        Assert.assertThat(slaves.get(1).getContainer(), CoreMatchers.equalTo("container3"));
    }

    @Test
    public void testSlavesWithStaleNodes1() throws Exception {
        ZooKeeperGroupTest.putChildData(group, ((ZooKeeperGroupTest.PATH) + "/001"), "container1");// stale

        ZooKeeperGroupTest.putChildData(group, ((ZooKeeperGroupTest.PATH) + "/002"), "container1");
        ZooKeeperGroupTest.putChildData(group, ((ZooKeeperGroupTest.PATH) + "/003"), "container2");// stale

        ZooKeeperGroupTest.putChildData(group, ((ZooKeeperGroupTest.PATH) + "/004"), "container3");// stale

        ZooKeeperGroupTest.putChildData(group, ((ZooKeeperGroupTest.PATH) + "/005"), "container2");
        ZooKeeperGroupTest.putChildData(group, ((ZooKeeperGroupTest.PATH) + "/006"), "container3");
        List<NodeState> slaves = group.slaves();
        Assert.assertThat(slaves.size(), CoreMatchers.equalTo(2));
        Assert.assertThat(slaves.get(0).getContainer(), CoreMatchers.equalTo("container2"));
        Assert.assertThat(slaves.get(1).getContainer(), CoreMatchers.equalTo("container3"));
    }

    @Test
    public void testSlavesWithStaleNodes2() throws Exception {
        ZooKeeperGroupTest.putChildData(group, ((ZooKeeperGroupTest.PATH) + "/001"), "container1");// stale

        ZooKeeperGroupTest.putChildData(group, ((ZooKeeperGroupTest.PATH) + "/002"), "container2");
        ZooKeeperGroupTest.putChildData(group, ((ZooKeeperGroupTest.PATH) + "/003"), "container1");
        ZooKeeperGroupTest.putChildData(group, ((ZooKeeperGroupTest.PATH) + "/004"), "container3");// stale

        ZooKeeperGroupTest.putChildData(group, ((ZooKeeperGroupTest.PATH) + "/005"), "container3");
        List<NodeState> slaves = group.slaves();
        Assert.assertThat(slaves.size(), CoreMatchers.equalTo(2));
        Assert.assertThat(slaves.get(0).getContainer(), CoreMatchers.equalTo("container1"));
        Assert.assertThat(slaves.get(1).getContainer(), CoreMatchers.equalTo("container3"));
    }
}

