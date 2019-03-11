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
package org.apache.hadoop.fs.http.client;


import java.security.PrivilegedExceptionAction;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.test.HFSTestCase;
import org.apache.hadoop.test.HadoopUsersConfTestHelper;
import org.apache.hadoop.test.TestDir;
import org.apache.hadoop.test.TestHdfs;
import org.apache.hadoop.test.TestJetty;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public abstract class BaseTestHttpFSWith extends HFSTestCase {
    protected enum Operation {

        GET,
        OPEN,
        CREATE,
        APPEND,
        TRUNCATE,
        CONCAT,
        RENAME,
        DELETE,
        LIST_STATUS,
        WORKING_DIRECTORY,
        MKDIRS,
        SET_TIMES,
        SET_PERMISSION,
        SET_OWNER,
        SET_REPLICATION,
        CHECKSUM,
        CONTENT_SUMMARY,
        FILEACLS,
        DIRACLS,
        SET_XATTR,
        GET_XATTRS,
        REMOVE_XATTR,
        LIST_XATTRS,
        ENCRYPTION,
        LIST_STATUS_BATCH,
        GETTRASHROOT,
        STORAGEPOLICY,
        ERASURE_CODING,
        CREATE_SNAPSHOT,
        RENAME_SNAPSHOT,
        DELETE_SNAPSHOT,
        ALLOW_SNAPSHOT,
        DISALLOW_SNAPSHOT,
        DISALLOW_SNAPSHOT_EXCEPTION,
        FILE_STATUS_ATTR,
        GET_SNAPSHOT_DIFF,
        GET_SNAPSHOTTABLE_DIRECTORY_LIST;}

    private BaseTestHttpFSWith.Operation operation;

    public BaseTestHttpFSWith(BaseTestHttpFSWith.Operation operation) {
        this.operation = operation;
    }

    @Test
    @TestDir
    @TestJetty
    @TestHdfs
    public void testOperation() throws Exception {
        createHttpFSServer();
        operation(operation);
    }

    @Test
    @TestDir
    @TestJetty
    @TestHdfs
    public void testOperationDoAs() throws Exception {
        createHttpFSServer();
        UserGroupInformation ugi = UserGroupInformation.createProxyUser(HadoopUsersConfTestHelper.getHadoopUsers()[0], UserGroupInformation.getCurrentUser());
        ugi.doAs(new PrivilegedExceptionAction<Void>() {
            @Override
            public Void run() throws Exception {
                operation(operation);
                return null;
            }
        });
    }
}

