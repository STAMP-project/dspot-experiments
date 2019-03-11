/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.mapreduce.v2;


import java.security.PrivilegedExceptionAction;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.ipc.RemoteException;
import org.apache.hadoop.mapred.MiniMRCluster;
import org.apache.hadoop.security.UserGroupInformation;
import org.junit.Assert;
import org.junit.Test;


public class TestMiniMRProxyUser {
    private MiniDFSCluster dfsCluster = null;

    private MiniMRCluster mrCluster = null;

    @Test
    public void __testCurrentUser() throws Exception {
        mrRun();
    }

    @Test
    public void testValidProxyUser() throws Exception {
        UserGroupInformation ugi = UserGroupInformation.createProxyUser("u1", UserGroupInformation.getLoginUser());
        ugi.doAs(new PrivilegedExceptionAction<Void>() {
            public Void run() throws Exception {
                mrRun();
                return null;
            }
        });
    }

    @Test
    public void ___testInvalidProxyUser() throws Exception {
        UserGroupInformation ugi = UserGroupInformation.createProxyUser("u2", UserGroupInformation.getLoginUser());
        ugi.doAs(new PrivilegedExceptionAction<Void>() {
            public Void run() throws Exception {
                try {
                    mrRun();
                    Assert.fail();
                } catch (RemoteException ex) {
                    // nop
                } catch (Exception ex) {
                    Assert.fail();
                }
                return null;
            }
        });
    }
}

