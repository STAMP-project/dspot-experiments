/**
 * Copyright 2017 LinkedIn Corp. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
package com.github.ambry.account;


import InMemoryUnknownAccountService.UNKNOWN_ACCOUNT;
import com.github.ambry.utils.Utils;
import com.github.ambry.utils.UtilsTest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit tests for {@link InMemoryUnknownAccountService} and {@link InMemoryUnknownAccountServiceFactory}.
 */
public class InMemoryUnknownAccountServiceTest {
    private static final Random random = new Random();

    private AccountService accountService = new InMemoryUnknownAccountServiceFactory(null, null).getAccountService();

    @Test
    public void testAllMethods() throws Exception {
        Assert.assertEquals("Wrong account", null, accountService.getAccountById(Utils.getRandomShort(InMemoryUnknownAccountServiceTest.random)));
        Assert.assertEquals("Wrong account", UNKNOWN_ACCOUNT, accountService.getAccountById(((short) (-1))));
        Assert.assertEquals("Wrong account", UNKNOWN_ACCOUNT, accountService.getAccountByName(UtilsTest.getRandomString(10)));
        Assert.assertEquals("Wrong size of account collection", 1, accountService.getAllAccounts().size());
        // updating the InMemoryUnknownAccountService should fail.
        Account account = build();
        Assert.assertFalse("Wrong return value from an unsuccessful update operation", accountService.updateAccounts(Collections.singletonList(account)));
        Assert.assertEquals("Wrong size of account collection", 1, accountService.getAllAccounts().size());
        try {
            accountService.getAllAccounts().add(account);
            Assert.fail("Should have thrown.");
        } catch (UnsupportedOperationException e) {
            // expected
        }
        accountService.close();
    }

    /**
     * Tests {@code null} inputs.
     */
    @Test
    public void testNullInputs() {
        try {
            accountService.updateAccounts(null);
            Assert.fail("should have thrown");
        } catch (NullPointerException e) {
            // expected
        }
        try {
            accountService.getAccountByName(null);
            Assert.fail("should have thrown");
        } catch (NullPointerException e) {
            // expected
        }
    }

    /**
     * Tests adding/removing {@link Consumer}.
     */
    @Test
    public void testAddRemoveConsumer() {
        List<Collection<Account>> updatedAccountsReceivedByConsumers = new ArrayList<>();
        // add consumers
        Consumer<Collection<Account>> accountUpdateConsumer = ( updatedAccounts) -> {
            updatedAccountsReceivedByConsumers.add(updatedAccounts);
        };
        accountService.addAccountUpdateConsumer(accountUpdateConsumer);
        Account updatedAccount = build();
        accountService.updateAccounts(Collections.singletonList(updatedAccount));
        Assert.assertEquals("Wrong number of updated accounts received by consumer.", 0, updatedAccountsReceivedByConsumers.size());
        Account newAccount = build();
        accountService.updateAccounts(Collections.singletonList(newAccount));
        Assert.assertEquals("Wrong number of updated accounts received by consumer.", 0, updatedAccountsReceivedByConsumers.size());
        accountService.removeAccountUpdateConsumer(accountUpdateConsumer);
    }
}

