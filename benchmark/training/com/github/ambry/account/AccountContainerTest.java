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


import Account.JSON_VERSION_1;
import Account.JSON_VERSION_KEY;
import Account.STATUS_KEY;
import Account.UNKNOWN_ACCOUNT_ID;
import Account.UNKNOWN_ACCOUNT_NAME;
import AccountStatus.ACTIVE;
import Container.DEFAULT_PRIVATE_CONTAINER;
import Container.DEFAULT_PRIVATE_CONTAINER_CACHEABLE_SETTING;
import Container.DEFAULT_PRIVATE_CONTAINER_DESCRIPTION;
import Container.DEFAULT_PRIVATE_CONTAINER_ENCRYPTED_SETTING;
import Container.DEFAULT_PRIVATE_CONTAINER_ID;
import Container.DEFAULT_PRIVATE_CONTAINER_MEDIA_SCAN_DISABLED_SETTING;
import Container.DEFAULT_PRIVATE_CONTAINER_NAME;
import Container.DEFAULT_PRIVATE_CONTAINER_PARENT_ACCOUNT_ID;
import Container.DEFAULT_PRIVATE_CONTAINER_PREVIOUSLY_ENCRYPTED_SETTING;
import Container.DEFAULT_PRIVATE_CONTAINER_STATUS;
import Container.DEFAULT_PUBLIC_CONTAINER;
import Container.DEFAULT_PUBLIC_CONTAINER_CACHEABLE_SETTING;
import Container.DEFAULT_PUBLIC_CONTAINER_DESCRIPTION;
import Container.DEFAULT_PUBLIC_CONTAINER_ENCRYPTED_SETTING;
import Container.DEFAULT_PUBLIC_CONTAINER_ID;
import Container.DEFAULT_PUBLIC_CONTAINER_MEDIA_SCAN_DISABLED_SETTING;
import Container.DEFAULT_PUBLIC_CONTAINER_NAME;
import Container.DEFAULT_PUBLIC_CONTAINER_PARENT_ACCOUNT_ID;
import Container.DEFAULT_PUBLIC_CONTAINER_PREVIOUSLY_ENCRYPTED_SETTING;
import Container.DEFAULT_PUBLIC_CONTAINER_STATUS;
import Container.UNKNOWN_CONTAINER;
import Container.UNKNOWN_CONTAINER_CACHEABLE_SETTING;
import Container.UNKNOWN_CONTAINER_DESCRIPTION;
import Container.UNKNOWN_CONTAINER_ENCRYPTED_SETTING;
import Container.UNKNOWN_CONTAINER_ID;
import Container.UNKNOWN_CONTAINER_MEDIA_SCAN_DISABLED_SETTING;
import Container.UNKNOWN_CONTAINER_NAME;
import Container.UNKNOWN_CONTAINER_PARENT_ACCOUNT_ID;
import Container.UNKNOWN_CONTAINER_PREVIOUSLY_ENCRYPTED_SETTING;
import Container.UNKNOWN_CONTAINER_STATUS;
import com.github.ambry.utils.TestUtils;
import com.github.ambry.utils.Utils;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static AccountStatus.ACTIVE;
import static AccountStatus.INACTIVE;
import static com.github.ambry.account.Container.Container.DEFAULT_PRIVATE_CONTAINER;
import static com.github.ambry.account.Container.Container.DEFAULT_PUBLIC_CONTAINER;
import static com.github.ambry.account.Container.Container.JSON_VERSION_1;
import static com.github.ambry.account.Container.Container.JSON_VERSION_2;
import static com.github.ambry.account.Container.Container.UNKNOWN_CONTAINER;


/**
 * Unit tests for {@link Account}, {@link Container}, {@link AccountBuilder}, and {@link ContainerBuilder}.
 */
@RunWith(Parameterized.class)
public class AccountContainerTest {
    private static final Random random = new Random();

    private static final int CONTAINER_COUNT = 10;

    private static final short LATEST_CONTAINER_JSON_VERSION = JSON_VERSION_2;

    // Reference Account fields
    private short refAccountId;

    private String refAccountName;

    private AccountStatus refAccountStatus;

    private int refAccountSnapshotVersion = SNAPSHOT_VERSION_DEFAULT_VALUE;

    private JSONObject refAccountJson;

    // Reference Container fields
    private List<Short> refContainerIds;

    private List<String> refContainerNames;

    private List<String> refContainerDescriptions;

    private List<ContainerStatus> refContainerStatuses;

    private List<Boolean> refContainerEncryptionValues;

    private List<Boolean> refContainerPreviousEncryptionValues;

    private List<Boolean> refContainerCachingValues;

    private List<Boolean> refContainerBackupEnabledValues;

    private List<Boolean> refContainerMediaScanDisabledValues;

    private List<String> refContainerReplicationPolicyValues;

    private List<Boolean> refContainerTtlRequiredValues;

    private List<Boolean> refContainerSignedPathRequiredValues;

    private List<Set<String>> refContainerContentTypeWhitelistForFilenamesOnDownloadValues;

    private List<JSONObject> containerJsonList;

    private List<Container.Container> refContainers;

    /**
     * Initialize the metadata in JsonObject for account and container.
     *
     * @param containerJsonVersion
     * 		the container JSON version to use in the test.
     * @throws JSONException
     * 		
     */
    public AccountContainerTest(short containerJsonVersion) throws JSONException {
        Container.Container.setCurrentJsonVersion(containerJsonVersion);
        refAccountId = Utils.getRandomShort(AccountContainerTest.random);
        refAccountName = UUID.randomUUID().toString();
        refAccountStatus = (AccountContainerTest.random.nextBoolean()) ? ACTIVE : INACTIVE;
        refAccountSnapshotVersion = AccountContainerTest.random.nextInt();
        initializeRefContainers();
        refAccountJson = new JSONObject();
        refAccountJson.put(JSON_VERSION_KEY, JSON_VERSION_1);
        refAccountJson.put(ACCOUNT_ID_KEY, refAccountId);
        refAccountJson.put(ACCOUNT_NAME_KEY, refAccountName);
        refAccountJson.put(STATUS_KEY, refAccountStatus.name());
        refAccountJson.put(SNAPSHOT_VERSION_KEY, refAccountSnapshotVersion);
        refAccountJson.put(CONTAINERS_KEY, containerJsonList);
    }

    /**
     * Tests constructing an {@link Account} from Json metadata.
     */
    @Test
    public void testConstructAccountFromJson() {
        assertAccountAgainstReference(Account.Account.fromJson(refAccountJson), true, true);
    }

    /**
     * Tests constructing {@link Account} and {@link Container} using individual arguments.
     */
    @Test
    public void testConstructAccountAndContainerFromArguments() throws JSONException {
        Account.Account accountFromArguments = new Account.Account(refAccountId, refAccountName, refAccountStatus, refAccountSnapshotVersion, refContainers);
        assertAccountAgainstReference(accountFromArguments, true, true);
    }

    /**
     * Tests constructing {@link Account} when supplying a list of {@link Container}s with duplicated name.
     */
    @Test
    public void testDuplicateContainerName() throws Exception {
        ArrayList<Container.Container> containers = new ArrayList<>();
        // first container with (id=0, name="0")
        containers.add(setEncrypted(refContainerEncryptionValues.get(0)).setPreviouslyEncrypted(refContainerPreviousEncryptionValues.get(0)).setCacheable(refContainerCachingValues.get(0)).setBackupEnabled(refContainerBackupEnabledValues.get(0)).setMediaScanDisabled(refContainerMediaScanDisabledValues.get(0)).setReplicationPolicy(refContainerReplicationPolicyValues.get(0)).setTtlRequired(refContainerTtlRequiredValues.get(0)).setContentTypeWhitelistForFilenamesOnDownload(refContainerContentTypeWhitelistForFilenamesOnDownloadValues.get(0)).build());
        // second container with (id=1, name="0")
        containers.add(setEncrypted(refContainerEncryptionValues.get(0)).setPreviouslyEncrypted(refContainerPreviousEncryptionValues.get(0)).setCacheable(refContainerCachingValues.get(0)).setBackupEnabled(refContainerBackupEnabledValues.get(0)).setMediaScanDisabled(refContainerMediaScanDisabledValues.get(0)).setReplicationPolicy(refContainerReplicationPolicyValues.get(0)).setTtlRequired(refContainerTtlRequiredValues.get(0)).setContentTypeWhitelistForFilenamesOnDownload(refContainerContentTypeWhitelistForFilenamesOnDownloadValues.get(0)).build());
        createAccountWithBadContainersAndFail(containers, IllegalStateException.class);
    }

    /**
     * Tests constructing {@link Account} when supplying a list of {@link Container}s with duplicated id.
     */
    @Test
    public void testDuplicateContainerId() throws Exception {
        ArrayList<Container.Container> containers = new ArrayList<>();
        // first container with (id=0, name="0")
        containers.add(setEncrypted(refContainerEncryptionValues.get(0)).setPreviouslyEncrypted(refContainerPreviousEncryptionValues.get(0)).setCacheable(refContainerCachingValues.get(0)).setBackupEnabled(refContainerBackupEnabledValues.get(0)).setMediaScanDisabled(refContainerMediaScanDisabledValues.get(0)).setReplicationPolicy(refContainerReplicationPolicyValues.get(0)).setTtlRequired(refContainerTtlRequiredValues.get(0)).setContentTypeWhitelistForFilenamesOnDownload(refContainerContentTypeWhitelistForFilenamesOnDownloadValues.get(0)).build());
        // second container with (id=0, name="1")
        containers.add(setEncrypted(refContainerEncryptionValues.get(0)).setPreviouslyEncrypted(refContainerPreviousEncryptionValues.get(0)).setCacheable(refContainerCachingValues.get(0)).setBackupEnabled(refContainerBackupEnabledValues.get(0)).setMediaScanDisabled(refContainerMediaScanDisabledValues.get(0)).setReplicationPolicy(refContainerReplicationPolicyValues.get(0)).setTtlRequired(refContainerTtlRequiredValues.get(0)).setContentTypeWhitelistForFilenamesOnDownload(refContainerContentTypeWhitelistForFilenamesOnDownloadValues.get(0)).build());
        createAccountWithBadContainersAndFail(containers, IllegalStateException.class);
    }

    /**
     * Tests constructing {@link Account} when supplying a list of {@link Container}s with duplicated id and name.
     */
    @Test
    public void testDuplicateContainerNameAndId() throws Exception {
        ArrayList<Container.Container> containers = new ArrayList<>();
        // first container with (id=0, name="0")
        containers.add(setEncrypted(refContainerEncryptionValues.get(0)).setPreviouslyEncrypted(refContainerPreviousEncryptionValues.get(0)).setCacheable(refContainerCachingValues.get(0)).setBackupEnabled(refContainerBackupEnabledValues.get(0)).setMediaScanDisabled(refContainerMediaScanDisabledValues.get(0)).setReplicationPolicy(refContainerReplicationPolicyValues.get(0)).setTtlRequired(refContainerTtlRequiredValues.get(0)).setContentTypeWhitelistForFilenamesOnDownload(refContainerContentTypeWhitelistForFilenamesOnDownloadValues.get(0)).build());
        // second container with (id=1, name="0")
        containers.add(setEncrypted(refContainerEncryptionValues.get(0)).setPreviouslyEncrypted(refContainerPreviousEncryptionValues.get(0)).setCacheable(refContainerCachingValues.get(0)).setBackupEnabled(refContainerBackupEnabledValues.get(0)).setMediaScanDisabled(refContainerMediaScanDisabledValues.get(0)).setReplicationPolicy(refContainerReplicationPolicyValues.get(0)).setTtlRequired(refContainerTtlRequiredValues.get(0)).setContentTypeWhitelistForFilenamesOnDownload(refContainerContentTypeWhitelistForFilenamesOnDownloadValues.get(0)).build());
        // third container with (id=10, name="10")
        containers.add(setEncrypted(refContainerEncryptionValues.get(0)).setPreviouslyEncrypted(refContainerPreviousEncryptionValues.get(0)).setCacheable(refContainerCachingValues.get(0)).setBackupEnabled(refContainerBackupEnabledValues.get(0)).setMediaScanDisabled(refContainerMediaScanDisabledValues.get(0)).setReplicationPolicy(refContainerReplicationPolicyValues.get(0)).setTtlRequired(refContainerTtlRequiredValues.get(0)).setContentTypeWhitelistForFilenamesOnDownload(refContainerContentTypeWhitelistForFilenamesOnDownloadValues.get(0)).build());
        // second container with (id=10, name="11")
        containers.add(setEncrypted(refContainerEncryptionValues.get(0)).setPreviouslyEncrypted(refContainerPreviousEncryptionValues.get(0)).setCacheable(refContainerCachingValues.get(0)).setBackupEnabled(refContainerBackupEnabledValues.get(0)).setMediaScanDisabled(refContainerMediaScanDisabledValues.get(0)).setReplicationPolicy(refContainerReplicationPolicyValues.get(0)).setTtlRequired(refContainerTtlRequiredValues.get(0)).setContentTypeWhitelistForFilenamesOnDownload(refContainerContentTypeWhitelistForFilenamesOnDownloadValues.get(0)).build());
        createAccountWithBadContainersAndFail(containers, IllegalStateException.class);
    }

    /**
     * Tests constructing a {@link Container} from json object.
     */
    @Test
    public void testConstructContainerFromJson() throws JSONException {
        for (int i = 0; i < (AccountContainerTest.CONTAINER_COUNT); i++) {
            Container.Container containerFromJson = Container.Container.fromJson(containerJsonList.get(i), refAccountId);
            assertContainer(containerFromJson, i);
        }
    }

    /**
     * Tests in an {@link AccountBuilder} the account id mismatches with container id.
     */
    @Test
    public void testMismatchForAccountId() throws Exception {
        ArrayList<Container.Container> containers = new ArrayList<>();
        // container with parentAccountId = refAccountId + 1
        containers.add(setEncrypted(refContainerEncryptionValues.get(0)).setPreviouslyEncrypted(refContainerPreviousEncryptionValues.get(0)).setCacheable(refContainerCachingValues.get(0)).setBackupEnabled(refContainerBackupEnabledValues.get(0)).setMediaScanDisabled(refContainerMediaScanDisabledValues.get(0)).setReplicationPolicy(refContainerReplicationPolicyValues.get(0)).setTtlRequired(refContainerTtlRequiredValues.get(0)).setContentTypeWhitelistForFilenamesOnDownload(refContainerContentTypeWhitelistForFilenamesOnDownloadValues.get(0)).build());
        createAccountWithBadContainersAndFail(containers, IllegalStateException.class);
    }

    /**
     * Tests bad inputs for constructors or methods.
     *
     * @throws Exception
     * 		Any unexpected exceptions.
     */
    @Test
    public void badInputs() throws Exception {
        // null account metadata
        TestUtils.assertException(IllegalArgumentException.class, () -> Account.Account.fromJson(null), null);
        // account metadata in wrong format
        JSONObject badMetadata1 = new JSONObject().put("badKey", "badValue");
        TestUtils.assertException(JSONException.class, () -> Account.Account.fromJson(badMetadata1), null);
        // required fields are missing in the metadata
        JSONObject badMetadata2 = AccountContainerTest.deepCopy(refAccountJson);
        badMetadata2.remove(ACCOUNT_ID_KEY);
        TestUtils.assertException(JSONException.class, () -> Account.Account.fromJson(badMetadata2), null);
        // unsupported account json version
        JSONObject badMetadata3 = AccountContainerTest.deepCopy(refAccountJson).put(JSON_VERSION_KEY, 2);
        TestUtils.assertException(IllegalStateException.class, () -> Account.Account.fromJson(badMetadata3), null);
        // invalid account status
        JSONObject badMetadata4 = AccountContainerTest.deepCopy(refAccountJson).put(STATUS_KEY, "invalidAccountStatus");
        TestUtils.assertException(IllegalArgumentException.class, () -> Account.Account.fromJson(badMetadata4), null);
        // null container metadata
        TestUtils.assertException(IllegalArgumentException.class, () -> Container.Container.fromJson(null, refAccountId), null);
        // invalid container status
        JSONObject badMetadata5 = AccountContainerTest.deepCopy(containerJsonList.get(0)).put(Container.STATUS_KEY, "invalidContainerStatus");
        TestUtils.assertException(IllegalArgumentException.class, () -> Container.Container.fromJson(badMetadata5, refAccountId), null);
        // required fields are missing.
        JSONObject badMetadata6 = AccountContainerTest.deepCopy(containerJsonList.get(0));
        badMetadata6.remove(CONTAINER_ID_KEY);
        TestUtils.assertException(JSONException.class, () -> Container.Container.fromJson(badMetadata6, refAccountId), null);
        // unsupported container json version
        JSONObject badMetadata7 = AccountContainerTest.deepCopy(containerJsonList.get(0)).put(Container.JSON_VERSION_KEY, ((AccountContainerTest.LATEST_CONTAINER_JSON_VERSION) + 1));
        TestUtils.assertException(IllegalStateException.class, () -> Container.Container.fromJson(badMetadata7, refAccountId), null);
    }

    /**
     * Tests {@code toString()} methods.
     *
     * @throws JSONException
     * 		
     */
    @Test
    public void testToString() throws JSONException {
        Account.Account account = Account.Account.fromJson(refAccountJson);
        Assert.assertEquals((((("Account[" + (account.getId())) + ",") + (account.getSnapshotVersion())) + "]"), account.toString());
        for (int i = 0; i < (AccountContainerTest.CONTAINER_COUNT); i++) {
            Container.Container container = Container.Container.fromJson(containerJsonList.get(i), refAccountId);
            Assert.assertEquals((((("Container[" + (account.getId())) + ":") + (container.getId())) + "]"), container.toString());
        }
    }

    // Tests for builders
    /**
     * Tests building an {@link Account} using {@link AccountBuilder}.
     *
     * @throws JSONException
     * 		
     */
    @Test
    public void testAccountBuilder() throws JSONException {
        // build an account with arguments supplied
        AccountBuilder accountBuilder = snapshotVersion(refAccountSnapshotVersion);
        Account.Account accountByBuilder = accountBuilder.build();
        assertAccountAgainstReference(accountByBuilder, false, false);
        // set containers
        List<Container.Container> containers = new ArrayList<>();
        for (int i = 0; i < (AccountContainerTest.CONTAINER_COUNT); i++) {
            Container.Container container = Container.Container.fromJson(containerJsonList.get(i), refAccountId);
            containers.add(container);
            accountBuilder.addOrUpdateContainer(container);
        }
        accountByBuilder = accountBuilder.build();
        assertAccountAgainstReference(accountByBuilder, true, true);
        // build an account from existing account
        accountBuilder = new AccountBuilder(accountByBuilder);
        Account.Account account2ByBuilder = accountBuilder.build();
        assertAccountAgainstReference(account2ByBuilder, true, true);
        // clear containers
        Account.Account account3ByBuilder = build();
        assertAccountAgainstReference(account3ByBuilder, false, false);
        Assert.assertTrue("Container list should be empty.", account3ByBuilder.getAllContainers().isEmpty());
    }

    /**
     * Tests building a {@link Container} using {@link ContainerBuilder}.
     *
     * @throws JSONException
     * 		
     */
    @Test
    public void testContainerBuilder() throws JSONException {
        for (int i = 0; i < (AccountContainerTest.CONTAINER_COUNT); i++) {
            // build a container with arguments supplied
            ContainerBuilder containerBuilder = setEncrypted(refContainerEncryptionValues.get(i)).setPreviouslyEncrypted(refContainerPreviousEncryptionValues.get(i)).setCacheable(refContainerCachingValues.get(i)).setBackupEnabled(refContainerBackupEnabledValues.get(i)).setMediaScanDisabled(refContainerMediaScanDisabledValues.get(i)).setReplicationPolicy(refContainerReplicationPolicyValues.get(i)).setTtlRequired(refContainerTtlRequiredValues.get(i)).setSecurePathRequired(refContainerSignedPathRequiredValues.get(i)).setContentTypeWhitelistForFilenamesOnDownload(refContainerContentTypeWhitelistForFilenamesOnDownloadValues.get(i));
            Container.Container containerFromBuilder = containerBuilder.build();
            assertContainer(containerFromBuilder, i);
            // build a container from existing container
            containerBuilder = new ContainerBuilder(containerFromBuilder);
            containerFromBuilder = containerBuilder.build();
            assertContainer(containerFromBuilder, i);
            boolean previouslyEncrypted = containerFromBuilder.wasPreviouslyEncrypted();
            // turn off encryption, check that previouslyEncrypted is the same as the previous value.
            containerFromBuilder = new ContainerBuilder(containerFromBuilder).setEncrypted(false).build();
            assertEncryptionSettings(containerFromBuilder, false, previouslyEncrypted);
            // turn off encryption, by turning it on and off again.
            containerFromBuilder = new ContainerBuilder(containerFromBuilder).setEncrypted(true).setEncrypted(false).build();
            assertEncryptionSettings(containerFromBuilder, false, previouslyEncrypted);
            // turn it back on, previouslyEncrypted should be set.
            containerFromBuilder = new ContainerBuilder(containerFromBuilder).setEncrypted(true).build();
            assertEncryptionSettings(containerFromBuilder, true, true);
            // turn off again, previouslyEncrypted should still be set.
            containerFromBuilder = new ContainerBuilder(containerFromBuilder).setEncrypted(false).build();
            assertEncryptionSettings(containerFromBuilder, false, true);
        }
    }

    /**
     * Tests required fields are missing to build an account.
     */
    @Test
    public void testFieldMissingToBuildAccount() throws Exception {
        // test when required fields are null
        buildAccountWithMissingFieldsAndFail(null, refAccountStatus, IllegalStateException.class);
        buildAccountWithMissingFieldsAndFail(refAccountName, null, IllegalStateException.class);
    }

    /**
     * Tests required fields are missing to build an account.
     */
    @Test
    public void testBuildingContainerWithBadFields() throws Exception {
        // test when required fields are null
        String name = refContainerNames.get(0);
        ContainerStatus status = refContainerStatuses.get(0);
        buildContainerWithBadFieldsAndFail(null, status, false, false, IllegalStateException.class);
        buildContainerWithBadFieldsAndFail(name, null, false, false, IllegalStateException.class);
        buildContainerWithBadFieldsAndFail(name, status, true, false, IllegalStateException.class);
    }

    /**
     * Tests update an {@link Account}.
     *
     * @throws JSONException
     * 		
     */
    @Test
    public void testUpdateAccount() throws JSONException {
        // set an account with different field value
        Account.Account origin = Account.Account.fromJson(refAccountJson);
        AccountBuilder accountBuilder = new AccountBuilder(origin);
        short updatedAccountId = ((short) ((refAccountId) + 1));
        String updatedAccountName = (refAccountName) + "-updated";
        Account.Account.AccountStatus updatedAccountStatus = Account.AccountStatus.INACTIVE;
        accountBuilder.id(updatedAccountId);
        accountBuilder.name(updatedAccountName);
        accountBuilder.status(updatedAccountStatus);
        try {
            accountBuilder.build();
            Assert.fail("Should have thrown");
        } catch (IllegalStateException e) {
            // expected, as new account id does not match the parentAccountId of the two containers.
        }
        // remove all existing containers.
        for (Container.Container container : origin.getAllContainers()) {
            accountBuilder.removeContainer(container);
        }
        // build the account and assert
        Account.Account updatedAccount = accountBuilder.build();
        Assert.assertEquals(updatedAccountId, updatedAccount.getId());
        Assert.assertEquals(updatedAccountName, updatedAccount.getName());
        Assert.assertEquals(updatedAccountStatus, updatedAccount.getStatus());
        // add back the containers and assert
        for (Container.Container container : origin.getAllContainers()) {
            accountBuilder.addOrUpdateContainer(container);
        }
        accountBuilder.id(refAccountId);
        updatedAccount = accountBuilder.build();
        Assert.assertEquals(origin.getAllContainers().toString(), updatedAccount.getAllContainers().toString());
    }

    /**
     * Tests removing containers in AccountBuilder.
     */
    @Test
    public void testRemovingContainers() throws JSONException {
        Account.Account origin = Account.Account.fromJson(refAccountJson);
        AccountBuilder accountBuilder = new AccountBuilder(origin);
        // first, remove 10 containers
        ArrayList<Container.Container> containers = new ArrayList(origin.getAllContainers());
        Set<Container.Container> removed = new HashSet<>();
        while ((removed.size()) < 10) {
            Container.Container container = containers.get(AccountContainerTest.random.nextInt(containers.size()));
            removed.add(container);
            accountBuilder.removeContainer(container);
        } 
        Account.Account account = accountBuilder.build();
        Assert.assertEquals("Wrong number of containers", ((AccountContainerTest.CONTAINER_COUNT) - 10), account.getAllContainers().size());
        for (Container.Container removedContainer : removed) {
            Assert.assertNull("Container not removed ", account.getContainerById(removedContainer.getId()));
            Assert.assertNull("Container not removed ", account.getContainerByName(removedContainer.getName()));
        }
        // then, remove the rest containers
        for (Container.Container container : origin.getAllContainers()) {
            accountBuilder.removeContainer(container);
        }
        account = accountBuilder.build();
        Assert.assertEquals("Wrong container number.", 0, account.getAllContainers().size());
    }

    /**
     * Tests updating containers in an account.
     *
     * @throws JSONException
     * 		
     */
    @Test
    public void testUpdateContainerInAccount() throws JSONException {
        Account.Account account = Account.Account.fromJson(refAccountJson);
        AccountBuilder accountBuilder = new AccountBuilder(account);
        // updating with different containers
        for (int i = 0; i < (AccountContainerTest.CONTAINER_COUNT); i++) {
            Container.Container container = account.getContainerById(refContainerIds.get(i));
            accountBuilder.removeContainer(container);
            ContainerBuilder containerBuilder = new ContainerBuilder(container);
            short updatedContainerId = ((short) ((-1) * (container.getId())));
            String updatedContainerName = (container.getName()) + "-updated";
            Container.Container.ContainerStatus updatedContainerStatus = Container.ContainerStatus.INACTIVE;
            String updatedContainerDescription = (container.getDescription()) + "--updated";
            boolean updatedEncrypted = !(container.isEncrypted());
            boolean updatedPreviouslyEncrypted = updatedEncrypted || (container.wasPreviouslyEncrypted());
            boolean updatedCacheable = !(container.isCacheable());
            boolean updatedMediaScanDisabled = !(container.isMediaScanDisabled());
            String updatedReplicationPolicy = (container.getReplicationPolicy()) + "---updated";
            boolean updatedTtlRequired = !(container.isTtlRequired());
            boolean updatedSignedPathRequired = !(container.isSecurePathRequired());
            Set<String> updatedContentTypeWhitelistForFilenamesOnDownloadValues = container.getContentTypeWhitelistForFilenamesOnDownload().stream().map(( contentType) -> contentType + "--updated").collect(Collectors.toSet());
            containerBuilder.setId(updatedContainerId).setName(updatedContainerName).setStatus(updatedContainerStatus).setDescription(updatedContainerDescription).setEncrypted(updatedEncrypted).setCacheable(updatedCacheable).setMediaScanDisabled(updatedMediaScanDisabled).setReplicationPolicy(updatedReplicationPolicy).setTtlRequired(updatedTtlRequired).setSecurePathRequired(updatedSignedPathRequired).setContentTypeWhitelistForFilenamesOnDownload(updatedContentTypeWhitelistForFilenamesOnDownloadValues);
            accountBuilder.addOrUpdateContainer(containerBuilder.build());
            // build account and assert
            Account.Account updatedAccount = accountBuilder.build();
            Container.Container updatedContainer = updatedAccount.getContainerById(updatedContainerId);
            Assert.assertEquals("container id is not correctly updated", updatedContainerId, updatedContainer.getId());
            Assert.assertEquals("container name is not correctly updated", updatedContainerName, updatedContainer.getName());
            Assert.assertEquals("container status is not correctly updated", updatedContainerStatus, updatedContainer.getStatus());
            Assert.assertEquals("container description is not correctly updated", updatedContainerDescription, updatedContainer.getDescription());
            Assert.assertEquals("cacheable is not correctly updated", updatedCacheable, updatedContainer.isCacheable());
            switch (Container.Container.getCurrentJsonVersion()) {
                case JSON_VERSION_1 :
                    Assert.assertEquals("Wrong encryption setting", ENCRYPTED_DEFAULT_VALUE, updatedContainer.isEncrypted());
                    Assert.assertEquals("Wrong previous encryption setting", PREVIOUSLY_ENCRYPTED_DEFAULT_VALUE, updatedContainer.wasPreviouslyEncrypted());
                    Assert.assertEquals("Wrong media scan disabled setting", MEDIA_SCAN_DISABLED_DEFAULT_VALUE, updatedContainer.isMediaScanDisabled());
                    Assert.assertNull("Wrong replication policy", updatedContainer.getReplicationPolicy());
                    Assert.assertEquals("Wrong ttl required setting", TTL_REQUIRED_DEFAULT_VALUE, updatedContainer.isTtlRequired());
                    Assert.assertEquals("Wrong secure required setting", SECURE_PATH_REQUIRED_DEFAULT_VALUE, updatedContainer.isSecurePathRequired());
                    Assert.assertEquals("Wrong content type whitelist for filenames on download value", CONTENT_TYPE_WHITELIST_FOR_FILENAMES_ON_DOWNLOAD_DEFAULT_VALUE, updatedContainer.getContentTypeWhitelistForFilenamesOnDownload());
                    break;
                case JSON_VERSION_2 :
                    Assert.assertEquals("Wrong encryption setting", updatedEncrypted, updatedContainer.isEncrypted());
                    Assert.assertEquals("Wrong previous encryption setting", updatedPreviouslyEncrypted, updatedContainer.wasPreviouslyEncrypted());
                    Assert.assertEquals("Wrong media scan disabled setting", updatedMediaScanDisabled, updatedContainer.isMediaScanDisabled());
                    Assert.assertEquals("Wrong replication policy", updatedReplicationPolicy, updatedContainer.getReplicationPolicy());
                    Assert.assertEquals("Wrong ttl required setting", updatedTtlRequired, updatedContainer.isTtlRequired());
                    Assert.assertEquals("Wrong secure path required setting", updatedSignedPathRequired, updatedContainer.isSecurePathRequired());
                    Assert.assertEquals("Wrong content type whitelist for filenames on download value", updatedContentTypeWhitelistForFilenamesOnDownloadValues, updatedContainer.getContentTypeWhitelistForFilenamesOnDownload());
                    break;
                default :
                    throw new IllegalStateException(("Unsupported version: " + (Container.Container.getCurrentJsonVersion())));
            }
        }
    }

    /**
     * Tests updating the parent account id for a container.
     *
     * @throws JSONException
     * 		
     */
    @Test
    public void testUpdateContainerParentAccountId() throws JSONException {
        ContainerBuilder containerBuilder = new ContainerBuilder(Container.Container.fromJson(containerJsonList.get(0), refAccountId));
        short newParentAccountId = ((short) ((refAccountId) + 1));
        containerBuilder.setParentAccountId(newParentAccountId);
        Assert.assertEquals("Container's parent account id is incorrectly updated.", newParentAccountId, containerBuilder.build().getParentAccountId());
    }

    /**
     * Tests removing a non-existent container from accountBuilder.
     *
     * @throws JSONException
     * 		
     */
    @Test
    public void testRemoveNonExistContainer() throws JSONException {
        Account.Account origin = Account.Account.fromJson(refAccountJson);
        AccountBuilder accountBuilder = new AccountBuilder(origin);
        ContainerBuilder containerBuilder = setEncrypted(refContainerEncryptionValues.get(0)).setPreviouslyEncrypted(refContainerPreviousEncryptionValues.get(0)).setCacheable(refContainerCachingValues.get(0)).setBackupEnabled(refContainerBackupEnabledValues.get(0)).setMediaScanDisabled(refContainerMediaScanDisabledValues.get(0)).setReplicationPolicy(refContainerReplicationPolicyValues.get(0)).setTtlRequired(refContainerTtlRequiredValues.get(0)).setContentTypeWhitelistForFilenamesOnDownload(refContainerContentTypeWhitelistForFilenamesOnDownloadValues.get(0));
        Container.Container container = containerBuilder.build();
        accountBuilder.removeContainer(container);
        accountBuilder.removeContainer(null);
        Account.Account account = accountBuilder.build();
        assertAccountAgainstReference(account, true, true);
    }

    /**
     * Tests for {@link InMemAccountService#UNKNOWN_ACCOUNT}, {@link Container#UNKNOWN_CONTAINER},
     * {@link Container#DEFAULT_PUBLIC_CONTAINER}, and {@link Container#DEFAULT_PRIVATE_CONTAINER}.
     */
    @Test
    public void testUnknownAccountAndContainer() {
        Account.Account unknownAccount = InMemAccountService.UNKNOWN_ACCOUNT;
        Container.Container unknownContainer = UNKNOWN_CONTAINER;
        Container.Container unknownPublicContainer = DEFAULT_PUBLIC_CONTAINER;
        Container.Container unknownPrivateContainer = DEFAULT_PRIVATE_CONTAINER;
        // UNKNOWN_CONTAINER
        Assert.assertEquals("Wrong id for UNKNOWN_CONTAINER", UNKNOWN_CONTAINER_ID, unknownContainer.getId());
        Assert.assertEquals("Wrong name for UNKNOWN_CONTAINER", UNKNOWN_CONTAINER_NAME, unknownContainer.getName());
        Assert.assertEquals("Wrong status for UNKNOWN_CONTAINER", UNKNOWN_CONTAINER_STATUS, unknownContainer.getStatus());
        Assert.assertEquals("Wrong description for UNKNOWN_CONTAINER", UNKNOWN_CONTAINER_DESCRIPTION, unknownContainer.getDescription());
        Assert.assertEquals("Wrong parent account id for UNKNOWN_CONTAINER", UNKNOWN_CONTAINER_PARENT_ACCOUNT_ID, unknownContainer.getParentAccountId());
        Assert.assertEquals("Wrong cacheable setting for UNKNOWN_CONTAINER", UNKNOWN_CONTAINER_CACHEABLE_SETTING, unknownContainer.isCacheable());
        Assert.assertEquals("Wrong encrypted setting for UNKNOWN_CONTAINER", UNKNOWN_CONTAINER_ENCRYPTED_SETTING, unknownContainer.isEncrypted());
        Assert.assertEquals("Wrong previouslyEncrypted setting for UNKNOWN_CONTAINER", UNKNOWN_CONTAINER_PREVIOUSLY_ENCRYPTED_SETTING, unknownContainer.wasPreviouslyEncrypted());
        Assert.assertEquals("Wrong mediaScanDisabled setting for UNKNOWN_CONTAINER", UNKNOWN_CONTAINER_MEDIA_SCAN_DISABLED_SETTING, unknownContainer.isMediaScanDisabled());
        // DEFAULT_PUBLIC_CONTAINER
        Assert.assertEquals("Wrong id for DEFAULT_PUBLIC_CONTAINER", DEFAULT_PUBLIC_CONTAINER_ID, unknownPublicContainer.getId());
        Assert.assertEquals("Wrong name for DEFAULT_PUBLIC_CONTAINER", DEFAULT_PUBLIC_CONTAINER_NAME, unknownPublicContainer.getName());
        Assert.assertEquals("Wrong status for DEFAULT_PUBLIC_CONTAINER", DEFAULT_PUBLIC_CONTAINER_STATUS, unknownPublicContainer.getStatus());
        Assert.assertEquals("Wrong description for DEFAULT_PUBLIC_CONTAINER", DEFAULT_PUBLIC_CONTAINER_DESCRIPTION, unknownPublicContainer.getDescription());
        Assert.assertEquals("Wrong parent account id for DEFAULT_PUBLIC_CONTAINER", DEFAULT_PUBLIC_CONTAINER_PARENT_ACCOUNT_ID, unknownPublicContainer.getParentAccountId());
        Assert.assertEquals("Wrong cacheable setting for DEFAULT_PUBLIC_CONTAINER", DEFAULT_PUBLIC_CONTAINER_CACHEABLE_SETTING, unknownPublicContainer.isCacheable());
        Assert.assertEquals("Wrong encrypted setting for DEFAULT_PUBLIC_CONTAINER", DEFAULT_PUBLIC_CONTAINER_ENCRYPTED_SETTING, unknownPublicContainer.isEncrypted());
        Assert.assertEquals("Wrong previouslyEncrypted setting for DEFAULT_PUBLIC_CONTAINER", DEFAULT_PUBLIC_CONTAINER_PREVIOUSLY_ENCRYPTED_SETTING, unknownPublicContainer.wasPreviouslyEncrypted());
        Assert.assertEquals("Wrong mediaScanDisabled setting for DEFAULT_PUBLIC_CONTAINER", DEFAULT_PUBLIC_CONTAINER_MEDIA_SCAN_DISABLED_SETTING, unknownPublicContainer.isMediaScanDisabled());
        // DEFAULT_PRIVATE_CONTAINER
        Assert.assertEquals("Wrong id for DEFAULT_PRIVATE_CONTAINER", DEFAULT_PRIVATE_CONTAINER_ID, unknownPrivateContainer.getId());
        Assert.assertEquals("Wrong name for DEFAULT_PRIVATE_CONTAINER", DEFAULT_PRIVATE_CONTAINER_NAME, unknownPrivateContainer.getName());
        Assert.assertEquals("Wrong status for DEFAULT_PRIVATE_CONTAINER", DEFAULT_PRIVATE_CONTAINER_STATUS, unknownPrivateContainer.getStatus());
        Assert.assertEquals("Wrong description for DEFAULT_PRIVATE_CONTAINER", DEFAULT_PRIVATE_CONTAINER_DESCRIPTION, unknownPrivateContainer.getDescription());
        Assert.assertEquals("Wrong parent account id for DEFAULT_PRIVATE_CONTAINER", DEFAULT_PRIVATE_CONTAINER_PARENT_ACCOUNT_ID, unknownPrivateContainer.getParentAccountId());
        Assert.assertEquals("Wrong cacheable setting for DEFAULT_PRIVATE_CONTAINER", DEFAULT_PRIVATE_CONTAINER_CACHEABLE_SETTING, unknownPrivateContainer.isCacheable());
        Assert.assertEquals("Wrong encrypted setting for DEFAULT_PRIVATE_CONTAINER", DEFAULT_PRIVATE_CONTAINER_ENCRYPTED_SETTING, unknownPrivateContainer.isEncrypted());
        Assert.assertEquals("Wrong previouslyEncrypted setting for DEFAULT_PRIVATE_CONTAINER", DEFAULT_PRIVATE_CONTAINER_PREVIOUSLY_ENCRYPTED_SETTING, unknownPrivateContainer.wasPreviouslyEncrypted());
        Assert.assertEquals("Wrong mediaScanDisabled setting for DEFAULT_PRIVATE_CONTAINER", DEFAULT_PRIVATE_CONTAINER_MEDIA_SCAN_DISABLED_SETTING, unknownPrivateContainer.isMediaScanDisabled());
        // UNKNOWN_ACCOUNT
        Assert.assertEquals("Wrong id for UNKNOWN_ACCOUNT", UNKNOWN_ACCOUNT_ID, unknownAccount.getId());
        Assert.assertEquals("Wrong name for UNKNOWN_ACCOUNT", UNKNOWN_ACCOUNT_NAME, unknownAccount.getName());
        Assert.assertEquals("Wrong status for UNKNOWN_ACCOUNT", ACTIVE, unknownAccount.getStatus());
        Assert.assertEquals("Wrong number of containers for UNKNOWN_ACCOUNT", 3, unknownAccount.getAllContainers().size());
        Assert.assertEquals("Wrong unknown container get from UNKNOWN_ACCOUNT", UNKNOWN_CONTAINER, unknownAccount.getContainerById(UNKNOWN_CONTAINER_ID));
        Assert.assertEquals("Wrong unknown public container get from UNKNOWN_ACCOUNT", DEFAULT_PUBLIC_CONTAINER, unknownAccount.getContainerById(DEFAULT_PUBLIC_CONTAINER_ID));
        Assert.assertEquals("Wrong unknown private container get from UNKNOWN_ACCOUNT", DEFAULT_PRIVATE_CONTAINER, unknownAccount.getContainerById(DEFAULT_PRIVATE_CONTAINER_ID));
    }

    /**
     * Tests {@link Account#equals(Object)} that checks equality of {@link Container}s.
     */
    @Test
    public void testAccountEqual() {
        // Check two accounts with same fields but no containers.
        Account.Account accountNoContainer = build();
        Account.Account accountNoContainerDuplicate = build();
        Assert.assertTrue("Two accounts should be equal.", accountNoContainer.equals(accountNoContainerDuplicate));
        // Check two accounts with same fields and containers.
        Account.Account accountWithContainers = Account.Account.fromJson(refAccountJson);
        Account.Account accountWithContainersDuplicate = Account.Account.fromJson(refAccountJson);
        Assert.assertTrue("Two accounts should be equal.", accountWithContainers.equals(accountWithContainersDuplicate));
        // Check two accounts with same fields but one has containers, the other one does not.
        Assert.assertFalse("Two accounts should not be equal.", accountNoContainer.equals(accountWithContainers));
        // Check two accounts with the same fields and the same number of containers. One container of one account has one
        // field different from the other one.
        Container.Container updatedContainer = setEncrypted(refContainerEncryptionValues.get(0)).setPreviouslyEncrypted(refContainerPreviousEncryptionValues.get(0)).setCacheable(refContainerCachingValues.get(0)).setBackupEnabled(refContainerBackupEnabledValues.get(0)).setMediaScanDisabled(refContainerMediaScanDisabledValues.get(0)).setReplicationPolicy(refContainerReplicationPolicyValues.get(0)).setTtlRequired(refContainerTtlRequiredValues.get(0)).setContentTypeWhitelistForFilenamesOnDownload(refContainerContentTypeWhitelistForFilenamesOnDownloadValues.get(0)).build();
        refContainers.remove(0);
        refContainers.add(updatedContainer);
        Account.Account accountWithModifiedContainers = build();
        Assert.assertFalse("Two accounts should not be equal.", accountWithContainers.equals(accountWithModifiedContainers));
    }
}

