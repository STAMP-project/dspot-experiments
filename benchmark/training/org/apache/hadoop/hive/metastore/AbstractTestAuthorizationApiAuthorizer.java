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
package org.apache.hadoop.hive.metastore;


import PrincipalType.USER;
import java.util.ArrayList;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.metastore.api.HiveObjectRef;
import org.apache.hadoop.hive.metastore.api.PrivilegeBag;
import org.apache.hadoop.hive.metastore.api.Role;
import org.junit.Test;


/**
 * Test case for {@link MetaStoreAuthzAPIAuthorizerEmbedOnly} The authorizer is
 * supposed to allow api calls for metastore in embedded mode while disallowing
 * them in remote metastore mode. Note that this is an abstract class, the
 * subclasses that set the mode and the tests here get run as part of their
 * testing.
 */
public abstract class AbstractTestAuthorizationApiAuthorizer {
    protected static boolean isRemoteMetastoreMode;

    private static HiveConf hiveConf;

    private static HiveMetaStoreClient msc;

    interface FunctionInvoker {
        public void invoke() throws Exception;
    }

    @Test
    public void testGrantPriv() throws Exception {
        AbstractTestAuthorizationApiAuthorizer.FunctionInvoker invoker = new AbstractTestAuthorizationApiAuthorizer.FunctionInvoker() {
            @Override
            public void invoke() throws Exception {
                AbstractTestAuthorizationApiAuthorizer.msc.grant_privileges(new PrivilegeBag(new ArrayList<org.apache.hadoop.hive.metastore.api.HiveObjectPrivilege>()));
            }
        };
        testFunction(invoker);
    }

    @Test
    public void testRevokePriv() throws Exception {
        AbstractTestAuthorizationApiAuthorizer.FunctionInvoker invoker = new AbstractTestAuthorizationApiAuthorizer.FunctionInvoker() {
            @Override
            public void invoke() throws Exception {
                AbstractTestAuthorizationApiAuthorizer.msc.revoke_privileges(new PrivilegeBag(new ArrayList<org.apache.hadoop.hive.metastore.api.HiveObjectPrivilege>()), false);
            }
        };
        testFunction(invoker);
    }

    @Test
    public void testGrantRole() throws Exception {
        AbstractTestAuthorizationApiAuthorizer.FunctionInvoker invoker = new AbstractTestAuthorizationApiAuthorizer.FunctionInvoker() {
            @Override
            public void invoke() throws Exception {
                AbstractTestAuthorizationApiAuthorizer.msc.grant_role(null, null, null, null, null, true);
            }
        };
        testFunction(invoker);
    }

    @Test
    public void testRevokeRole() throws Exception {
        AbstractTestAuthorizationApiAuthorizer.FunctionInvoker invoker = new AbstractTestAuthorizationApiAuthorizer.FunctionInvoker() {
            @Override
            public void invoke() throws Exception {
                AbstractTestAuthorizationApiAuthorizer.msc.revoke_role(null, null, null, false);
            }
        };
        testFunction(invoker);
    }

    @Test
    public void testCreateRole() throws Exception {
        AbstractTestAuthorizationApiAuthorizer.FunctionInvoker invoker = new AbstractTestAuthorizationApiAuthorizer.FunctionInvoker() {
            @Override
            public void invoke() throws Exception {
                AbstractTestAuthorizationApiAuthorizer.msc.create_role(new Role("role1", 0, "owner"));
            }
        };
        testFunction(invoker);
    }

    @Test
    public void testDropRole() throws Exception {
        AbstractTestAuthorizationApiAuthorizer.FunctionInvoker invoker = new AbstractTestAuthorizationApiAuthorizer.FunctionInvoker() {
            @Override
            public void invoke() throws Exception {
                AbstractTestAuthorizationApiAuthorizer.msc.drop_role(null);
            }
        };
        testFunction(invoker);
    }

    @Test
    public void testListRoles() throws Exception {
        AbstractTestAuthorizationApiAuthorizer.FunctionInvoker invoker = new AbstractTestAuthorizationApiAuthorizer.FunctionInvoker() {
            @Override
            public void invoke() throws Exception {
                AbstractTestAuthorizationApiAuthorizer.msc.list_roles(null, null);
            }
        };
        testFunction(invoker);
    }

    @Test
    public void testGetPrivSet() throws Exception {
        AbstractTestAuthorizationApiAuthorizer.FunctionInvoker invoker = new AbstractTestAuthorizationApiAuthorizer.FunctionInvoker() {
            @Override
            public void invoke() throws Exception {
                AbstractTestAuthorizationApiAuthorizer.msc.get_privilege_set(new HiveObjectRef(), null, new ArrayList<String>());
            }
        };
        testFunction(invoker);
    }

    @Test
    public void testListPriv() throws Exception {
        AbstractTestAuthorizationApiAuthorizer.FunctionInvoker invoker = new AbstractTestAuthorizationApiAuthorizer.FunctionInvoker() {
            @Override
            public void invoke() throws Exception {
                AbstractTestAuthorizationApiAuthorizer.msc.list_privileges(null, USER, new HiveObjectRef());
            }
        };
        testFunction(invoker);
    }
}

