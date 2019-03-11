/**
 * Copyright 2015 LinkedIn Corp. All rights reserved.
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
package com.github.ambry.router;


import Account.UNKNOWN_ACCOUNT_ID;
import Container.UNKNOWN_CONTAINER_ID;
import RouterErrorCode.InvalidBlobId;
import com.github.ambry.account.Account;
import com.github.ambry.account.AccountService;
import com.github.ambry.account.Container;
import com.github.ambry.account.InMemAccountService;
import com.github.ambry.clustermap.ClusterMap;
import com.github.ambry.clustermap.PartitionId;
import com.github.ambry.commons.BlobId;
import com.github.ambry.utils.Pair;
import com.github.ambry.utils.Utils;
import java.util.Arrays;
import java.util.Random;
import org.junit.Assert;
import org.junit.Test;


public class RouterUtilsTest {
    Random random = new Random();

    ClusterMap clusterMap;

    PartitionId partition;

    BlobId originalBlobId;

    String blobIdStr;

    @Test
    public void testInvalidInputString() {
        initialize();
        try {
            RouterUtils.getBlobIdFromString(null, clusterMap);
            Assert.fail("The input blob string is invalid. Should fail here");
        } catch (RouterException e) {
            Assert.assertEquals("The input blob string is invalid.", e.getErrorCode(), InvalidBlobId);
        }
        try {
            RouterUtils.getBlobIdFromString("", clusterMap);
            Assert.fail("The input blob string is invalid. Should fail here");
        } catch (RouterException e) {
            Assert.assertEquals("", e.getErrorCode(), InvalidBlobId);
        }
        try {
            RouterUtils.getBlobIdFromString("abc", clusterMap);
            Assert.fail("The input blob string is invalid. Should fail here");
        } catch (RouterException e) {
            Assert.assertEquals("", e.getErrorCode(), InvalidBlobId);
        }
    }

    @Test
    public void testGoodCase() throws Exception {
        initialize();
        BlobId convertedBlobId = RouterUtils.getBlobIdFromString(blobIdStr, clusterMap);
        Assert.assertEquals("The converted BlobId should be the same as the original.", originalBlobId, convertedBlobId);
    }

    /**
     * Test to ensure system health errors are interpreted correctly.
     */
    @Test
    public void testSystemHealthErrorInterpretation() {
        for (RouterErrorCode errorCode : RouterErrorCode.values()) {
            switch (errorCode) {
                case InvalidBlobId :
                case InvalidPutArgument :
                case BlobTooLarge :
                case BadInputChannel :
                case BlobDeleted :
                case BlobDoesNotExist :
                case BlobAuthorizationFailure :
                case BlobExpired :
                case RangeNotSatisfiable :
                case ChannelClosed :
                case BlobUpdateNotAllowed :
                    Assert.assertFalse(RouterUtils.isSystemHealthError(new RouterException("", errorCode)));
                    break;
                default :
                    Assert.assertTrue(RouterUtils.isSystemHealthError(new RouterException("", errorCode)));
                    break;
            }
        }
        Assert.assertTrue(RouterUtils.isSystemHealthError(new Exception()));
        Assert.assertFalse(RouterUtils.isSystemHealthError(Utils.convertToClientTerminationException(new Exception())));
    }

    /**
     * Test {@link RouterUtils#getAccountContainer(AccountService, short, short)}.
     */
    @Test
    public void testGetAccountContainer() {
        AccountService accountService = new InMemAccountService(false, false);
        // Both accountId and containerId are not tracked by AccountService.
        Pair<Account, Container> accountContainer = RouterUtils.getAccountContainer(accountService, UNKNOWN_ACCOUNT_ID, UNKNOWN_CONTAINER_ID);
        Assert.assertEquals("Account should be null", null, accountContainer.getFirst());
        Assert.assertEquals("Container should be null", null, accountContainer.getSecond());
        accountContainer = RouterUtils.getAccountContainer(accountService, Utils.getRandomShort(random), Utils.getRandomShort(random));
        Assert.assertEquals("Account should be null", null, accountContainer.getFirst());
        Assert.assertEquals("Container should be null", null, accountContainer.getSecond());
        // accountId is tracked by AccountService but containerId not.
        short accountId = Utils.getRandomShort(random);
        short containerId = Utils.getRandomShort(random);
        Account account = build();
        accountService.updateAccounts(Arrays.asList(account));
        accountContainer = RouterUtils.getAccountContainer(accountService, accountId, containerId);
        Assert.assertEquals("Account doesn't match", account, accountContainer.getFirst());
        Assert.assertEquals("Container should be null", null, accountContainer.getSecond());
        // Both accountId and containerId are tracked by AccountService.
        Container container = new com.github.ambry.account.ContainerBuilder(containerId, ("ContainerNameOf" + containerId), Container.ContainerStatus.ACTIVE, "description", accountId).build();
        account = new com.github.ambry.account.AccountBuilder(accountId, ("AccountNameOf" + accountId), Account.AccountStatus.ACTIVE).addOrUpdateContainer(container).build();
        accountService.updateAccounts(Arrays.asList(account));
        accountContainer = RouterUtils.getAccountContainer(accountService, accountId, containerId);
        Assert.assertEquals("Account doesn't match", account, accountContainer.getFirst());
        Assert.assertEquals("Container doesn't match", container, accountContainer.getSecond());
    }
}

