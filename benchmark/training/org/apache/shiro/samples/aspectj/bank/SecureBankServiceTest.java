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
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.shiro.samples.aspectj.bank;


import junit.framework.Assert;
import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.subject.Subject;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SecureBankServiceTest {
    private static Logger logger = LoggerFactory.getLogger(SecureBankServiceTest.class);

    private static SecureBankService service;

    private static int testCounter;

    private Subject _subject;

    @Test
    public void testCreateAccount() throws Exception {
        loginAsUser();
        createAndValidateAccountFor("Bob Smith");
    }

    @Test
    public void testDepositInto_singleTx() throws Exception {
        loginAsUser();
        long accountId = createAndValidateAccountFor("Joe Smith");
        makeDepositAndValidateAccount(accountId, 250.0, "Joe Smith");
    }

    @Test
    public void testDepositInto_multiTxs() throws Exception {
        loginAsUser();
        long accountId = createAndValidateAccountFor("Everett Smith");
        makeDepositAndValidateAccount(accountId, 50.0, "Everett Smith");
        makeDepositAndValidateAccount(accountId, 300.0, "Everett Smith");
        makeDepositAndValidateAccount(accountId, 85.0, "Everett Smith");
        SecureBankServiceTest.assertAccount("Everett Smith", true, 435.0, 3, accountId);
    }

    @Test(expected = NotEnoughFundsException.class)
    public void testWithdrawFrom_emptyAccount() throws Exception {
        loginAsUser();
        long accountId = createAndValidateAccountFor("Wally Smith");
        SecureBankServiceTest.service.withdrawFrom(accountId, 100.0);
    }

    @Test(expected = NotEnoughFundsException.class)
    public void testWithdrawFrom_notEnoughFunds() throws Exception {
        loginAsUser();
        long accountId = createAndValidateAccountFor("Frank Smith");
        makeDepositAndValidateAccount(accountId, 50.0, "Frank Smith");
        SecureBankServiceTest.service.withdrawFrom(accountId, 100.0);
    }

    @Test
    public void testWithdrawFrom_singleTx() throws Exception {
        loginAsUser();
        long accountId = createAndValidateAccountFor("Al Smith");
        makeDepositAndValidateAccount(accountId, 500.0, "Al Smith");
        makeWithdrawalAndValidateAccount(accountId, 100.0, "Al Smith");
        SecureBankServiceTest.assertAccount("Al Smith", true, 400.0, 2, accountId);
    }

    @Test
    public void testWithdrawFrom_manyTxs() throws Exception {
        loginAsUser();
        long accountId = createAndValidateAccountFor("Zoe Smith");
        makeDepositAndValidateAccount(accountId, 500.0, "Zoe Smith");
        makeWithdrawalAndValidateAccount(accountId, 100.0, "Zoe Smith");
        makeWithdrawalAndValidateAccount(accountId, 75.0, "Zoe Smith");
        makeWithdrawalAndValidateAccount(accountId, 125.0, "Zoe Smith");
        SecureBankServiceTest.assertAccount("Zoe Smith", true, 200.0, 4, accountId);
    }

    @Test
    public void testWithdrawFrom_upToZero() throws Exception {
        loginAsUser();
        long accountId = createAndValidateAccountFor("Zoe Smith");
        makeDepositAndValidateAccount(accountId, 500.0, "Zoe Smith");
        makeWithdrawalAndValidateAccount(accountId, 500.0, "Zoe Smith");
        SecureBankServiceTest.assertAccount("Zoe Smith", true, 0.0, 2, accountId);
    }

    @Test
    public void testCloseAccount_zeroBalance() throws Exception {
        loginAsUser();
        long accountId = createAndValidateAccountFor("Chris Smith");
        logoutCurrentSubject();
        loginAsSuperviser();
        double closingBalance = SecureBankServiceTest.service.closeAccount(accountId);
        Assert.assertEquals(0.0, closingBalance);
        SecureBankServiceTest.assertAccount("Chris Smith", false, 0.0, 1, accountId);
    }

    @Test
    public void testCloseAccount_withBalance() throws Exception {
        loginAsUser();
        long accountId = createAndValidateAccountFor("Gerry Smith");
        makeDepositAndValidateAccount(accountId, 385.0, "Gerry Smith");
        logoutCurrentSubject();
        loginAsSuperviser();
        double closingBalance = SecureBankServiceTest.service.closeAccount(accountId);
        Assert.assertEquals(385.0, closingBalance);
        SecureBankServiceTest.assertAccount("Gerry Smith", false, 0.0, 2, accountId);
    }

    @Test(expected = InactiveAccountException.class)
    public void testCloseAccount_alreadyClosed() throws Exception {
        loginAsUser();
        long accountId = createAndValidateAccountFor("Chris Smith");
        logoutCurrentSubject();
        loginAsSuperviser();
        double closingBalance = SecureBankServiceTest.service.closeAccount(accountId);
        Assert.assertEquals(0.0, closingBalance);
        SecureBankServiceTest.assertAccount("Chris Smith", false, 0.0, 1, accountId);
        SecureBankServiceTest.service.closeAccount(accountId);
    }

    @Test(expected = UnauthorizedException.class)
    public void testCloseAccount_unauthorizedAttempt() throws Exception {
        loginAsUser();
        long accountId = createAndValidateAccountFor("Chris Smith");
        SecureBankServiceTest.service.closeAccount(accountId);
    }
}

