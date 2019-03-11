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
package org.apache.hadoop.registry.client.impl;


import CreateMode.EPHEMERAL;
import CreateMode.PERSISTENT;
import RegistrySecurity.WorldReadWriteACL;
import java.util.List;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileAlreadyExistsException;
import org.apache.hadoop.fs.PathIsNotEmptyDirectoryException;
import org.apache.hadoop.fs.PathNotFoundException;
import org.apache.hadoop.registry.AbstractZKRegistryTest;
import org.apache.hadoop.registry.client.impl.zk.CuratorService;
import org.apache.zookeeper.data.ACL;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test the curator service
 */
public class TestCuratorService extends AbstractZKRegistryTest {
    private static final Logger LOG = LoggerFactory.getLogger(TestCuratorService.class);

    protected CuratorService curatorService;

    public static final String MISSING = "/missing";

    private List<ACL> rootACL;

    @Test
    public void testLs() throws Throwable {
        curatorService.zkList("/");
    }

    @Test(expected = PathNotFoundException.class)
    public void testLsNotFound() throws Throwable {
        List<String> ls = curatorService.zkList(TestCuratorService.MISSING);
    }

    @Test
    public void testExists() throws Throwable {
        Assert.assertTrue(curatorService.zkPathExists("/"));
    }

    @Test
    public void testExistsMissing() throws Throwable {
        Assert.assertFalse(curatorService.zkPathExists(TestCuratorService.MISSING));
    }

    @Test
    public void testVerifyExists() throws Throwable {
        pathMustExist("/");
    }

    @Test(expected = PathNotFoundException.class)
    public void testVerifyExistsMissing() throws Throwable {
        pathMustExist("/file-not-found");
    }

    @Test
    public void testMkdirs() throws Throwable {
        mkPath("/p1", PERSISTENT);
        pathMustExist("/p1");
        mkPath("/p1/p2", EPHEMERAL);
        pathMustExist("/p1/p2");
    }

    @Test(expected = PathNotFoundException.class)
    public void testMkdirChild() throws Throwable {
        mkPath("/testMkdirChild/child", PERSISTENT);
    }

    @Test
    public void testMaybeCreate() throws Throwable {
        Assert.assertTrue(curatorService.maybeCreate("/p3", PERSISTENT, WorldReadWriteACL, false));
        Assert.assertFalse(curatorService.maybeCreate("/p3", PERSISTENT, WorldReadWriteACL, false));
    }

    @Test
    public void testRM() throws Throwable {
        mkPath("/rm", PERSISTENT);
        curatorService.zkDelete("/rm", false, null);
        verifyNotExists("/rm");
        curatorService.zkDelete("/rm", false, null);
    }

    @Test
    public void testRMNonRf() throws Throwable {
        mkPath("/rm", PERSISTENT);
        mkPath("/rm/child", PERSISTENT);
        try {
            curatorService.zkDelete("/rm", false, null);
            Assert.fail("expected a failure");
        } catch (PathIsNotEmptyDirectoryException expected) {
        }
    }

    @Test
    public void testRMRf() throws Throwable {
        mkPath("/rm", PERSISTENT);
        mkPath("/rm/child", PERSISTENT);
        curatorService.zkDelete("/rm", true, null);
        verifyNotExists("/rm");
        curatorService.zkDelete("/rm", true, null);
    }

    @Test
    public void testBackgroundDelete() throws Throwable {
        mkPath("/rm", PERSISTENT);
        mkPath("/rm/child", PERSISTENT);
        CuratorEventCatcher events = new CuratorEventCatcher();
        curatorService.zkDelete("/rm", true, events);
        CuratorEvent taken = events.take();
        TestCuratorService.LOG.info("took {}", taken);
        Assert.assertEquals(1, events.getCount());
    }

    @Test
    public void testCreate() throws Throwable {
        curatorService.zkCreate("/testcreate", PERSISTENT, getTestBuffer(), rootACL);
        pathMustExist("/testcreate");
    }

    @Test
    public void testCreateTwice() throws Throwable {
        byte[] buffer = getTestBuffer();
        curatorService.zkCreate("/testcreatetwice", PERSISTENT, buffer, rootACL);
        try {
            curatorService.zkCreate("/testcreatetwice", PERSISTENT, buffer, rootACL);
            Assert.fail();
        } catch (FileAlreadyExistsException e) {
        }
    }

    @Test
    public void testCreateUpdate() throws Throwable {
        byte[] buffer = getTestBuffer();
        curatorService.zkCreate("/testcreateupdate", PERSISTENT, buffer, rootACL);
        curatorService.zkUpdate("/testcreateupdate", buffer);
    }

    @Test(expected = PathNotFoundException.class)
    public void testUpdateMissing() throws Throwable {
        curatorService.zkUpdate("/testupdatemissing", getTestBuffer());
    }

    @Test
    public void testUpdateDirectory() throws Throwable {
        mkPath("/testupdatedirectory", PERSISTENT);
        curatorService.zkUpdate("/testupdatedirectory", getTestBuffer());
    }

    @Test
    public void testUpdateDirectorywithChild() throws Throwable {
        mkPath("/testupdatedirectorywithchild", PERSISTENT);
        mkPath("/testupdatedirectorywithchild/child", PERSISTENT);
        curatorService.zkUpdate("/testupdatedirectorywithchild", getTestBuffer());
    }

    @Test
    public void testUseZKServiceForBinding() throws Throwable {
        CuratorService cs2 = new CuratorService("curator", AbstractZKRegistryTest.zookeeper);
        cs2.init(new Configuration());
        cs2.start();
    }
}

