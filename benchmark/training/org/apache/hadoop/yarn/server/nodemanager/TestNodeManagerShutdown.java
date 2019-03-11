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
package org.apache.hadoop.yarn.server.nodemanager;


import YarnConfiguration.NM_RECOVERY_DIR;
import YarnConfiguration.NM_RECOVERY_ENABLED;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import org.apache.hadoop.fs.FileContext;
import org.apache.hadoop.net.ServerSocketUtil;
import org.apache.hadoop.util.Shell;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.event.Dispatcher;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.factories.RecordFactory;
import org.apache.hadoop.yarn.factory.providers.RecordFactoryProvider;
import org.apache.hadoop.yarn.server.api.records.MasterKey;
import org.junit.Assert;
import org.junit.Test;


public class TestNodeManagerShutdown {
    static final File basedir = new File("target", TestNodeManagerShutdown.class.getName());

    static final File tmpDir = new File(TestNodeManagerShutdown.basedir, "tmpDir");

    static final File logsDir = new File(TestNodeManagerShutdown.basedir, "logs");

    static final File remoteLogsDir = new File(TestNodeManagerShutdown.basedir, "remotelogs");

    static final File nmLocalDir = new File(TestNodeManagerShutdown.basedir, "nm0");

    static final File processStartFile = new File(TestNodeManagerShutdown.tmpDir, "start_file.txt").getAbsoluteFile();

    static final RecordFactory recordFactory = RecordFactoryProvider.getRecordFactory(null);

    static final String user = "nobody";

    private FileContext localFS;

    private ContainerId cId;

    private NodeManager nm;

    @Test
    public void testStateStoreRemovalOnDecommission() throws IOException {
        final File recoveryDir = new File(TestNodeManagerShutdown.basedir, "nm-recovery");
        nm = new TestNodeManagerShutdown.TestNodeManager();
        YarnConfiguration conf = createNMConfig();
        conf.setBoolean(NM_RECOVERY_ENABLED, true);
        conf.set(NM_RECOVERY_DIR, recoveryDir.getAbsolutePath());
        // verify state store is not removed on normal shutdown
        nm.init(conf);
        nm.start();
        Assert.assertTrue(recoveryDir.exists());
        Assert.assertTrue(recoveryDir.isDirectory());
        nm.stop();
        nm = null;
        Assert.assertTrue(recoveryDir.exists());
        Assert.assertTrue(recoveryDir.isDirectory());
        // verify state store is removed on decommissioned shutdown
        nm = new TestNodeManagerShutdown.TestNodeManager();
        nm.init(conf);
        nm.start();
        Assert.assertTrue(recoveryDir.exists());
        Assert.assertTrue(recoveryDir.isDirectory());
        nm.getNMContext().setDecommissioned(true);
        nm.stop();
        nm = null;
        Assert.assertFalse(recoveryDir.exists());
    }

    @Test
    public void testKillContainersOnShutdown() throws IOException, YarnException {
        nm = new TestNodeManagerShutdown.TestNodeManager();
        int port = ServerSocketUtil.getPort(49157, 10);
        nm.init(createNMConfig(port));
        nm.start();
        TestNodeManagerShutdown.startContainer(nm, cId, localFS, TestNodeManagerShutdown.tmpDir, TestNodeManagerShutdown.processStartFile, port);
        final int MAX_TRIES = 20;
        int numTries = 0;
        while ((!(TestNodeManagerShutdown.processStartFile.exists())) && (numTries < MAX_TRIES)) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            numTries++;
        } 
        nm.stop();
        // Now verify the contents of the file.  Script generates a message when it
        // receives a sigterm so we look for that.  We cannot perform this check on
        // Windows, because the process is not notified when killed by winutils.
        // There is no way for the process to trap and respond.  Instead, we can
        // verify that the job object with ID matching container ID no longer exists.
        if (Shell.WINDOWS) {
            Assert.assertFalse("Process is still alive!", DefaultContainerExecutor.containerIsAlive(cId.toString()));
        } else {
            BufferedReader reader = new BufferedReader(new FileReader(TestNodeManagerShutdown.processStartFile));
            boolean foundSigTermMessage = false;
            while (true) {
                String line = reader.readLine();
                if (line == null) {
                    break;
                }
                if (line.contains("SIGTERM")) {
                    foundSigTermMessage = true;
                    break;
                }
            } 
            Assert.assertTrue("Did not find sigterm message", foundSigTermMessage);
            reader.close();
        }
    }

    class TestNodeManager extends NodeManager {
        @Override
        protected NodeStatusUpdater createNodeStatusUpdater(Context context, Dispatcher dispatcher, NodeHealthCheckerService healthChecker) {
            MockNodeStatusUpdater myNodeStatusUpdater = new MockNodeStatusUpdater(context, dispatcher, healthChecker, metrics);
            return myNodeStatusUpdater;
        }

        public void setMasterKey(MasterKey masterKey) {
            getNMContext().getContainerTokenSecretManager().setMasterKey(masterKey);
        }
    }
}

