/**
 * -
 * -\-\-
 * Helios Services
 * --
 * Copyright (C) 2016 Spotify AB
 * --
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * -/-/-
 */
package com.spotify.helios.servicescommon.coordination;


import com.spotify.helios.Parallelized;
import com.spotify.helios.ZooKeeperTestingServerManager;
import java.nio.file.Path;
import org.apache.curator.utils.ZKPaths;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(Parallelized.class)
public class ZooKeeperUpdatingPersistentDirectoryTest {
    private static final String PARENT_PATH = "/foobar";

    private Path stateFile;

    private Path backupDir;

    private static final byte[] BAR1_DATA = "bar1".getBytes();

    private static final byte[] BAR2_DATA = "bar2".getBytes();

    private static final byte[] BAR3_DATA = "bar2".getBytes();

    private static final String FOO_NODE = "foo";

    private static final String BAZ_NODE = "baz";

    private static final String FOO_PATH = ZKPaths.makePath(ZooKeeperUpdatingPersistentDirectoryTest.PARENT_PATH, ZooKeeperUpdatingPersistentDirectoryTest.FOO_NODE);

    private static final String BAZ_PATH = ZKPaths.makePath(ZooKeeperUpdatingPersistentDirectoryTest.PARENT_PATH, ZooKeeperUpdatingPersistentDirectoryTest.BAZ_NODE);

    private ZooKeeperTestingServerManager zk = new ZooKeeperTestingServerManager();

    private ZooKeeperUpdatingPersistentDirectory sut;

    @Test
    public void verifyCreatesAndRemovesNode() throws Exception {
        sut.put(ZooKeeperUpdatingPersistentDirectoryTest.FOO_NODE, ZooKeeperUpdatingPersistentDirectoryTest.BAR1_DATA);
        final byte[] remote = awaitNode(ZooKeeperUpdatingPersistentDirectoryTest.FOO_PATH);
        Assert.assertArrayEquals(ZooKeeperUpdatingPersistentDirectoryTest.BAR1_DATA, remote);
        sut.remove(ZooKeeperUpdatingPersistentDirectoryTest.FOO_NODE);
        awaitNoNode(ZooKeeperUpdatingPersistentDirectoryTest.FOO_PATH);
    }

    @Test
    public void verifyUpdatesDifferingNode() throws Exception {
        try {
            zk.curatorWithSuperAuth().create().forPath(ZooKeeperUpdatingPersistentDirectoryTest.FOO_PATH, "old".getBytes());
        } catch (NodeExistsException ignore) {
            // ignored
        }
        sut.put(ZooKeeperUpdatingPersistentDirectoryTest.FOO_NODE, ZooKeeperUpdatingPersistentDirectoryTest.BAR1_DATA);
        awaitNodeWithData(ZooKeeperUpdatingPersistentDirectoryTest.FOO_PATH, ZooKeeperUpdatingPersistentDirectoryTest.BAR1_DATA);
    }

    @Test
    public void verifyRemovesUndesiredNode() throws Exception {
        zk.ensure(ZooKeeperUpdatingPersistentDirectoryTest.FOO_PATH);
        zk.stop();
        zk.start();
        awaitNoNode(ZooKeeperUpdatingPersistentDirectoryTest.FOO_PATH);
    }

    @Test
    public void verifyRecoversFromBackupRestoreOnline() throws Exception {
        // Create backup
        try {
            zk.curatorWithSuperAuth().create().forPath("/version", "1".getBytes());
        } catch (NodeExistsException ignore) {
            // ignored
        }
        sut.put(ZooKeeperUpdatingPersistentDirectoryTest.FOO_NODE, ZooKeeperUpdatingPersistentDirectoryTest.BAR1_DATA);
        awaitNodeWithData(ZooKeeperUpdatingPersistentDirectoryTest.FOO_PATH, ZooKeeperUpdatingPersistentDirectoryTest.BAR1_DATA);
        zk.backup(backupDir);
        // Write data after backup
        zk.curatorWithSuperAuth().setData().forPath("/version", "2".getBytes());
        sut.put(ZooKeeperUpdatingPersistentDirectoryTest.FOO_NODE, ZooKeeperUpdatingPersistentDirectoryTest.BAR2_DATA);
        sut.put(ZooKeeperUpdatingPersistentDirectoryTest.BAZ_NODE, ZooKeeperUpdatingPersistentDirectoryTest.BAR3_DATA);
        awaitNodeWithData(ZooKeeperUpdatingPersistentDirectoryTest.FOO_PATH, ZooKeeperUpdatingPersistentDirectoryTest.BAR2_DATA);
        awaitNodeWithData(ZooKeeperUpdatingPersistentDirectoryTest.BAZ_PATH, ZooKeeperUpdatingPersistentDirectoryTest.BAR3_DATA);
        // Restore backup
        zk.stop();
        zk.restore(backupDir);
        zk.start();
        Assert.assertArrayEquals("1".getBytes(), zk.curatorWithSuperAuth().getData().forPath("/version"));
        // Verify that latest data is pushed
        awaitNodeWithData(ZooKeeperUpdatingPersistentDirectoryTest.FOO_PATH, ZooKeeperUpdatingPersistentDirectoryTest.BAR2_DATA);
        awaitNodeWithData(ZooKeeperUpdatingPersistentDirectoryTest.BAZ_PATH, ZooKeeperUpdatingPersistentDirectoryTest.BAR3_DATA);
    }

    @Test
    public void verifyRecoversFromBackupRestoreOffline() throws Exception {
        // Create backup
        try {
            zk.curatorWithSuperAuth().create().forPath("/version", "1".getBytes());
        } catch (NodeExistsException ignore) {
            // ignored
        }
        sut.put(ZooKeeperUpdatingPersistentDirectoryTest.FOO_NODE, ZooKeeperUpdatingPersistentDirectoryTest.BAR1_DATA);
        awaitNodeWithData(ZooKeeperUpdatingPersistentDirectoryTest.FOO_PATH, ZooKeeperUpdatingPersistentDirectoryTest.BAR1_DATA);
        zk.backup(backupDir);
        // Write data after backup
        zk.curatorWithSuperAuth().setData().forPath("/version", "2".getBytes());
        sut.put(ZooKeeperUpdatingPersistentDirectoryTest.FOO_NODE, ZooKeeperUpdatingPersistentDirectoryTest.BAR2_DATA);
        sut.put(ZooKeeperUpdatingPersistentDirectoryTest.BAZ_NODE, ZooKeeperUpdatingPersistentDirectoryTest.BAR3_DATA);
        awaitNodeWithData(ZooKeeperUpdatingPersistentDirectoryTest.FOO_PATH, ZooKeeperUpdatingPersistentDirectoryTest.BAR2_DATA);
        awaitNodeWithData(ZooKeeperUpdatingPersistentDirectoryTest.BAZ_PATH, ZooKeeperUpdatingPersistentDirectoryTest.BAR3_DATA);
        // Stop persistent directory
        sut.stopAsync().awaitTerminated();
        // Restore backup
        zk.stop();
        zk.restore(backupDir);
        zk.start();
        Assert.assertArrayEquals("1".getBytes(), zk.curatorWithSuperAuth().getData().forPath("/version"));
        // Start new persistent directory
        setupDirectory();
        // Verify that latest data is pushed
        awaitNodeWithData(ZooKeeperUpdatingPersistentDirectoryTest.FOO_PATH, ZooKeeperUpdatingPersistentDirectoryTest.BAR2_DATA);
        awaitNodeWithData(ZooKeeperUpdatingPersistentDirectoryTest.BAZ_PATH, ZooKeeperUpdatingPersistentDirectoryTest.BAR3_DATA);
    }
}

