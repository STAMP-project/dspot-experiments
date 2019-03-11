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
package org.apache.hadoop.fs.s3a.auth.delegation;


import AWSPolicyProvider.AccessLevel;
import AWSPolicyProvider.AccessLevel.READ;
import AWSPolicyProvider.AccessLevel.WRITE;
import DelegationConstants.DELEGATION_TOKEN_ROLE_ARN;
import RoleModel.Statement;
import java.util.EnumSet;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.s3a.S3AFileSystem;
import org.apache.hadoop.fs.s3a.auth.RoleModel;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Rerun the session token tests with a role binding.
 * Some tests will fail as role bindings prevent certain operations.
 */
public class ITestRoleDelegationTokens extends ITestSessionDelegationTokens {
    private static final Logger LOG = LoggerFactory.getLogger(ITestRoleDelegationTokens.class);

    @Test
    public void testBindingWithoutARN() throws Throwable {
        describe(("verify that a role binding only needs a role ARN when creating" + " a new token"));
        Configuration conf = new Configuration(getConfiguration());
        conf.unset(DELEGATION_TOKEN_ROLE_ARN);
        try (S3ADelegationTokens delegationTokens2 = new S3ADelegationTokens()) {
            final S3AFileSystem fs = getFileSystem();
            delegationTokens2.bindToFileSystem(fs.getUri(), fs);
            delegationTokens2.init(conf);
            delegationTokens2.start();
            // cannot create a DT at this point
            intercept(IllegalStateException.class, RoleTokenBinding.E_NO_ARN, () -> delegationTokens2.createDelegationToken(new EncryptionSecrets()));
        }
    }

    @Test
    public void testCreateRoleModel() throws Throwable {
        describe("self contained role model retrieval");
        EnumSet<AWSPolicyProvider.AccessLevel> access = EnumSet.of(READ, WRITE);
        S3AFileSystem fs = getFileSystem();
        List<RoleModel.Statement> rules = fs.listAWSPolicyRules(access);
        assertTrue("No AWS policy rules from FS", (!(rules.isEmpty())));
        String ruleset = new RoleModel().toJson(new RoleModel.Policy(rules));
        ITestRoleDelegationTokens.LOG.info("Access policy for {}\n{}", fs.getUri(), ruleset);
    }
}

