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
package org.apache.hadoop.yarn.server.resourcemanager;


import AuthMethod.KERBEROS;
import java.security.PrivilegedExceptionAction;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.security.authentication.util.KerberosName;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.server.resourcemanager.security.RMDelegationTokenSecretManager;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class TestTokenClientRMService {
    private static final String kerberosRule = "RULE:[1:$1@$0](.*@EXAMPLE.COM)s/@.*//\nDEFAULT";

    private static RMDelegationTokenSecretManager dtsm;

    static {
        KerberosName.setRules(TestTokenClientRMService.kerberosRule);
    }

    private static final UserGroupInformation owner = UserGroupInformation.createRemoteUser("owner", KERBEROS);

    private static final UserGroupInformation other = UserGroupInformation.createRemoteUser("other", KERBEROS);

    private static final UserGroupInformation tester = UserGroupInformation.createRemoteUser("tester", KERBEROS);

    private static final String testerPrincipal = "tester@EXAMPLE.COM";

    private static final String ownerPrincipal = "owner@EXAMPLE.COM";

    private static final String otherPrincipal = "other@EXAMPLE.COM";

    private static final UserGroupInformation testerKerb = UserGroupInformation.createRemoteUser(TestTokenClientRMService.testerPrincipal, KERBEROS);

    private static final UserGroupInformation ownerKerb = UserGroupInformation.createRemoteUser(TestTokenClientRMService.ownerPrincipal, KERBEROS);

    private static final UserGroupInformation otherKerb = UserGroupInformation.createRemoteUser(TestTokenClientRMService.otherPrincipal, KERBEROS);

    @Test
    public void testTokenCancellationByOwner() throws Exception {
        // two tests required - one with a kerberos name
        // and with a short name
        RMContext rmContext = Mockito.mock(RMContext.class);
        final ClientRMService rmService = new ClientRMService(rmContext, null, null, null, null, TestTokenClientRMService.dtsm);
        TestTokenClientRMService.testerKerb.doAs(new PrivilegedExceptionAction<Void>() {
            @Override
            public Void run() throws Exception {
                checkTokenCancellation(rmService, TestTokenClientRMService.testerKerb, TestTokenClientRMService.other);
                return null;
            }
        });
        TestTokenClientRMService.owner.doAs(new PrivilegedExceptionAction<Void>() {
            @Override
            public Void run() throws Exception {
                checkTokenCancellation(TestTokenClientRMService.owner, TestTokenClientRMService.other);
                return null;
            }
        });
    }

    @Test
    public void testTokenRenewalWrongUser() throws Exception {
        try {
            TestTokenClientRMService.owner.doAs(new PrivilegedExceptionAction<Void>() {
                @Override
                public Void run() throws Exception {
                    try {
                        checkTokenRenewal(TestTokenClientRMService.owner, TestTokenClientRMService.other);
                        return null;
                    } catch (YarnException ex) {
                        Assert.assertTrue(ex.getMessage().contains(((TestTokenClientRMService.owner.getUserName()) + " tries to renew a token")));
                        Assert.assertTrue(ex.getMessage().contains(("with non-matching renewer " + (TestTokenClientRMService.other.getUserName()))));
                        throw ex;
                    }
                }
            });
        } catch (Exception e) {
            return;
        }
        Assert.fail("renew should have failed");
    }

    @Test
    public void testTokenRenewalByLoginUser() throws Exception {
        UserGroupInformation.getLoginUser().doAs(new PrivilegedExceptionAction<Void>() {
            @Override
            public Void run() throws Exception {
                checkTokenRenewal(TestTokenClientRMService.owner, TestTokenClientRMService.owner);
                checkTokenRenewal(TestTokenClientRMService.owner, TestTokenClientRMService.other);
                return null;
            }
        });
    }

    @Test
    public void testTokenCancellationByRenewer() throws Exception {
        // two tests required - one with a kerberos name
        // and with a short name
        RMContext rmContext = Mockito.mock(RMContext.class);
        final ClientRMService rmService = new ClientRMService(rmContext, null, null, null, null, TestTokenClientRMService.dtsm);
        TestTokenClientRMService.testerKerb.doAs(new PrivilegedExceptionAction<Void>() {
            @Override
            public Void run() throws Exception {
                checkTokenCancellation(rmService, TestTokenClientRMService.owner, TestTokenClientRMService.testerKerb);
                return null;
            }
        });
        TestTokenClientRMService.other.doAs(new PrivilegedExceptionAction<Void>() {
            @Override
            public Void run() throws Exception {
                checkTokenCancellation(TestTokenClientRMService.owner, TestTokenClientRMService.other);
                return null;
            }
        });
    }

    @Test
    public void testTokenCancellationByWrongUser() {
        // two sets to test -
        // 1. try to cancel tokens of short and kerberos users as a kerberos UGI
        // 2. try to cancel tokens of short and kerberos users as a simple auth UGI
        RMContext rmContext = Mockito.mock(RMContext.class);
        final ClientRMService rmService = new ClientRMService(rmContext, null, null, null, null, TestTokenClientRMService.dtsm);
        UserGroupInformation[] kerbTestOwners = new UserGroupInformation[]{ TestTokenClientRMService.owner, TestTokenClientRMService.other, TestTokenClientRMService.tester, TestTokenClientRMService.ownerKerb, TestTokenClientRMService.otherKerb };
        UserGroupInformation[] kerbTestRenewers = new UserGroupInformation[]{ TestTokenClientRMService.owner, TestTokenClientRMService.other, TestTokenClientRMService.ownerKerb, TestTokenClientRMService.otherKerb };
        for (final UserGroupInformation tokOwner : kerbTestOwners) {
            for (final UserGroupInformation tokRenewer : kerbTestRenewers) {
                try {
                    TestTokenClientRMService.testerKerb.doAs(new PrivilegedExceptionAction<Void>() {
                        @Override
                        public Void run() throws Exception {
                            try {
                                checkTokenCancellation(rmService, tokOwner, tokRenewer);
                                Assert.fail(((("We should not reach here; token owner = " + (tokOwner.getUserName())) + ", renewer = ") + (tokRenewer.getUserName())));
                                return null;
                            } catch (YarnException e) {
                                Assert.assertTrue(e.getMessage().contains(((TestTokenClientRMService.testerKerb.getUserName()) + " is not authorized to cancel the token")));
                                return null;
                            }
                        }
                    });
                } catch (Exception e) {
                    Assert.fail(("Unexpected exception; " + (e.getMessage())));
                }
            }
        }
        UserGroupInformation[] simpleTestOwners = new UserGroupInformation[]{ TestTokenClientRMService.owner, TestTokenClientRMService.other, TestTokenClientRMService.ownerKerb, TestTokenClientRMService.otherKerb, TestTokenClientRMService.testerKerb };
        UserGroupInformation[] simpleTestRenewers = new UserGroupInformation[]{ TestTokenClientRMService.owner, TestTokenClientRMService.other, TestTokenClientRMService.ownerKerb, TestTokenClientRMService.otherKerb };
        for (final UserGroupInformation tokOwner : simpleTestOwners) {
            for (final UserGroupInformation tokRenewer : simpleTestRenewers) {
                try {
                    TestTokenClientRMService.tester.doAs(new PrivilegedExceptionAction<Void>() {
                        @Override
                        public Void run() throws Exception {
                            try {
                                checkTokenCancellation(tokOwner, tokRenewer);
                                Assert.fail(((("We should not reach here; token owner = " + (tokOwner.getUserName())) + ", renewer = ") + (tokRenewer.getUserName())));
                                return null;
                            } catch (YarnException ex) {
                                Assert.assertTrue(ex.getMessage().contains(((TestTokenClientRMService.tester.getUserName()) + " is not authorized to cancel the token")));
                                return null;
                            }
                        }
                    });
                } catch (Exception e) {
                    Assert.fail(("Unexpected exception; " + (e.getMessage())));
                }
            }
        }
    }

    @Test
    public void testTokenRenewalByOwner() throws Exception {
        TestTokenClientRMService.owner.doAs(new PrivilegedExceptionAction<Void>() {
            @Override
            public Void run() throws Exception {
                checkTokenRenewal(TestTokenClientRMService.owner, TestTokenClientRMService.owner);
                return null;
            }
        });
    }
}

