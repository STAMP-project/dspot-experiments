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


import AccountStatus.ACTIVE;
import HelixAccountServiceConfig.BACKUP_DIRECTORY_KEY;
import HelixAccountServiceConfig.UPDATER_POLLING_INTERVAL_MS_KEY;
import HelixAccountServiceConfig.ZK_CLIENT_CONNECT_STRING_KEY;
import com.codahale.metrics.MetricRegistry;
import com.github.ambry.config.HelixPropertyStoreConfig;
import com.github.ambry.config.VerifiableProperties;
import com.github.ambry.utils.TestUtils;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.helix.ZNRecord;
import org.junit.Assert;
import org.junit.Test;

import static AccountStatus.ACTIVE;
import static AccountStatus.INACTIVE;
import static junit.framework.Assert.assertTrue;


/**
 * Unit tests for {@link HelixAccountService}.
 */
public class HelixAccountServiceTest {
    private static final int ZK_CLIENT_CONNECTION_TIMEOUT_MS = 20000;

    private static final int ZK_CLIENT_SESSION_TIMEOUT_MS = 20000;

    private static final String ZK_CONNECT_STRING = "dummyHost:dummyPort";

    private static final String STORE_ROOT_PATH = "/ambry_test/helix_account_service";

    private static final Random random = new Random();

    private static final String BAD_ACCOUNT_METADATA_STRING = "badAccountMetadataString";

    private static final int NUM_REF_ACCOUNT = 10;

    private static final int NUM_CONTAINER_PER_ACCOUNT = 4;

    private static final Map<Short, Account.Account> idToRefAccountMap = new HashMap<>();

    private static final Map<Short, Map<Short, Container.Container>> idToRefContainerMap = new HashMap<>();

    private final Properties helixConfigProps = new Properties();

    private final Path accountBackupDir;

    private final MockNotifier<String> notifier;

    private VerifiableProperties vHelixConfigProps;

    private HelixPropertyStoreConfig storeConfig;

    private Account.Account refAccount;

    private short refAccountId;

    private String refAccountName;

    private AccountStatus refAccountStatus;

    private Container.Container refContainer;

    private short refContainerId;

    private String refContainerName;

    private ContainerStatus refContainerStatus;

    private String refContainerDescription;

    private boolean refContainerCaching;

    private boolean refContainerEncryption;

    private boolean refContainerPreviousEncryption;

    private boolean refContainerMediaScanDisabled;

    private boolean refContainerTtlRequired;

    private String refReplicationPolicy;

    private short refParentAccountId;

    private AccountService accountService;

    private MockHelixAccountServiceFactory mockHelixAccountServiceFactory;

    /**
     * Resets variables and settings, and cleans up if the store already exists.
     *
     * @throws Exception
     * 		Any unexpected exception.
     */
    public HelixAccountServiceTest() throws Exception {
        helixConfigProps.setProperty(((HelixPropertyStoreConfig.HELIX_PROPERTY_STORE_PREFIX) + "zk.client.connection.timeout.ms"), String.valueOf(HelixAccountServiceTest.ZK_CLIENT_CONNECTION_TIMEOUT_MS));
        helixConfigProps.setProperty(((HelixPropertyStoreConfig.HELIX_PROPERTY_STORE_PREFIX) + "zk.client.session.timeout.ms"), String.valueOf(HelixAccountServiceTest.ZK_CLIENT_SESSION_TIMEOUT_MS));
        helixConfigProps.setProperty(ZK_CLIENT_CONNECT_STRING_KEY, HelixAccountServiceTest.ZK_CONNECT_STRING);
        helixConfigProps.setProperty(((HelixPropertyStoreConfig.HELIX_PROPERTY_STORE_PREFIX) + "root.path"), HelixAccountServiceTest.STORE_ROOT_PATH);
        accountBackupDir = Paths.get(TestUtils.getTempDir("account-backup")).toAbsolutePath();
        helixConfigProps.setProperty(BACKUP_DIRECTORY_KEY, accountBackupDir.toString());
        vHelixConfigProps = new VerifiableProperties(helixConfigProps);
        storeConfig = new HelixPropertyStoreConfig(vHelixConfigProps);
        notifier = new MockNotifier<>();
        mockHelixAccountServiceFactory = new MockHelixAccountServiceFactory(vHelixConfigProps, new MetricRegistry(), notifier, null);
        deleteStoreIfExists();
        generateReferenceAccountsAndContainers();
    }

    /**
     * Tests a clean startup of {@link HelixAccountService}, when the corresponding {@code ZooKeeper} does not
     * have any {@link ZNRecord} on it.
     *
     * @throws Exception
     * 		Any unexpected exception.
     */
    @Test
    public void testStartUpWithoutMetadataExists() {
        accountService = mockHelixAccountServiceFactory.getAccountService();
        // At time zero, no account metadata exists.
        Assert.assertEquals("The number of account in HelixAccountService is incorrect after clean startup", 0, accountService.getAllAccounts().size());
    }

    /**
     * Tests starting up a {@link HelixAccountService}, when the corresponding {@code ZooKeeper} has account metadata
     * already stored on it.
     *
     * @throws Exception
     * 		Any unexpected exception.
     */
    @Test
    public void testStartUpWithMetadataExists() throws Exception {
        // pre-populate account metadata in ZK.
        writeAccountsToHelixPropertyStore(HelixAccountServiceTest.idToRefAccountMap.values(), false);
        // When start, the helixAccountService should get the account metadata.
        accountService = mockHelixAccountServiceFactory.getAccountService();
        AccountTestUtils.assertAccountsInAccountService(HelixAccountServiceTest.idToRefAccountMap.values(), HelixAccountServiceTest.NUM_REF_ACCOUNT, accountService);
    }

    /**
     * Tests creating a number of new {@link Account} through {@link HelixAccountService}, where there is no {@link ZNRecord}
     * exists on the {@code ZooKeeper}.
     */
    @Test
    public void testCreateAccount() {
        accountService = mockHelixAccountServiceFactory.getAccountService();
        Assert.assertEquals("The number of account in HelixAccountService is incorrect", 0, accountService.getAllAccounts().size());
        boolean res = accountService.updateAccounts(HelixAccountServiceTest.idToRefAccountMap.values());
        assertTrue("Failed to update accounts", res);
        AccountTestUtils.assertAccountsInAccountService(HelixAccountServiceTest.idToRefAccountMap.values(), HelixAccountServiceTest.NUM_REF_ACCOUNT, accountService);
    }

    /**
     * Tests creating and updating accounts through {@link HelixAccountService} in various situations:
     * 0. {@link Account}s already exists on ZooKeeper.
     * 1. add a new {@link Account};
     * 2. update existing {@link Account};
     * 3. add a new {@link Container} to an existing {@link Account};
     * 4. update existing {@link Container}s of existing {@link Account}s.
     *
     * @throws Exception
     * 		Any unexpected exception.
     */
    @Test
    public void testUpdateAccount() throws Exception {
        // pre-populate account metadata in ZK.
        writeAccountsToHelixPropertyStore(HelixAccountServiceTest.idToRefAccountMap.values(), false);
        accountService = mockHelixAccountServiceFactory.getAccountService();
        AccountTestUtils.assertAccountsInAccountService(HelixAccountServiceTest.idToRefAccountMap.values(), HelixAccountServiceTest.NUM_REF_ACCOUNT, accountService);
        // add a new account
        Account.Account newAccountWithoutContainer = build();
        List<Account.Account> accountsToUpdate = Collections.singletonList(newAccountWithoutContainer);
        updateAccountsAndAssertAccountExistence(accountsToUpdate, (1 + (HelixAccountServiceTest.NUM_REF_ACCOUNT)), true);
        // update all existing reference accounts (not the new account)
        accountsToUpdate = new ArrayList();
        for (Account.Account account : accountService.getAllAccounts()) {
            AccountBuilder accountBuilder = new AccountBuilder(account);
            accountBuilder.name(((account.getName()) + "-extra"));
            accountBuilder.status((account.getStatus().equals(ACTIVE) ? INACTIVE : ACTIVE));
            accountsToUpdate.add(accountBuilder.build());
        }
        updateAccountsAndAssertAccountExistence(accountsToUpdate, (1 + (HelixAccountServiceTest.NUM_REF_ACCOUNT)), true);
        // add one container to the new account
        AccountBuilder accountBuilder = new AccountBuilder(accountService.getAccountById(refAccountId));
        accountsToUpdate = Collections.singletonList(accountBuilder.addOrUpdateContainer(refContainer).build());
        updateAccountsAndAssertAccountExistence(accountsToUpdate, (1 + (HelixAccountServiceTest.NUM_REF_ACCOUNT)), true);
        // update existing containers for all the reference accounts (not for the new account)
        accountsToUpdate = new ArrayList();
        for (Account.Account account : accountService.getAllAccounts()) {
            accountBuilder = new AccountBuilder(account);
            for (Container.Container container : account.getAllContainers()) {
                ContainerBuilder containerBuilder = new ContainerBuilder(container);
                containerBuilder.setId(((short) ((-1) * (container.getId()))));
                containerBuilder.setName(((container.getName()) + "-extra"));
                containerBuilder.setStatus((container.getStatus().equals(ContainerStatus.ACTIVE) ? ContainerStatus.INACTIVE : ContainerStatus.ACTIVE));
                containerBuilder.setDescription(((container.getDescription()) + "--extra"));
                containerBuilder.setReplicationPolicy(((container.getReplicationPolicy()) + "---extra"));
                containerBuilder.setTtlRequired((!(container.isTtlRequired())));
                accountBuilder.addOrUpdateContainer(containerBuilder.build());
            }
            accountsToUpdate.add(accountBuilder.build());
        }
        updateAccountsAndAssertAccountExistence(accountsToUpdate, (1 + (HelixAccountServiceTest.NUM_REF_ACCOUNT)), true);
    }

    /**
     * Tests reading {@link ZNRecord} from {@link HelixPropertyStore}, where the {@link ZNRecord} is empty. This is a
     * good {@link ZNRecord} format that should NOT fail fetch or update.
     *
     * @throws Exception
     * 		Any unexpected exception.
     */
    @Test
    public void testReadBadZNRecordCase1() throws Exception {
        ZNRecord zNRecord = makeZNRecordWithSimpleField(null, null, null);
        updateAndWriteZNRecord(zNRecord, true);
    }

    /**
     * Tests reading {@link ZNRecord} from {@link HelixPropertyStore}, where the {@link ZNRecord} has an irrelevant
     * simple field ("key": "value"), but ("accountMetadata": someValidMap) is missing. This is a good {@link ZNRecord}
     * format that should NOT fail fetch or update.
     *
     * @throws Exception
     * 		Any unexpected exception.
     */
    @Test
    public void testReadBadZNRecordCase2() throws Exception {
        ZNRecord zNRecord = makeZNRecordWithSimpleField(null, "key", "value");
        updateAndWriteZNRecord(zNRecord, true);
    }

    /**
     * Tests reading {@link ZNRecord} from {@link HelixPropertyStore}, where the {@link ZNRecord} has a map field
     * ("key": someValidAccountMap), but ({@link HelixAccountService#ACCOUNT_METADATA_MAP_KEY}: someValidMap)
     * is missing. This is a good {@link ZNRecord} format that should NOT fail fetch or update.
     *
     * @throws Exception
     * 		Any unexpected exception.
     */
    @Test
    public void testReadBadZNRecordCase3() throws Exception {
        Map<String, String> mapValue = new HashMap<>();
        mapValue.put(String.valueOf(refAccount.getId()), refAccount.toJson(true).toString());
        ZNRecord zNRecord = makeZNRecordWithMapField(null, "key", mapValue);
        updateAndWriteZNRecord(zNRecord, true);
    }

    /**
     * Tests reading {@link ZNRecord} from {@link HelixPropertyStore}, where the {@link ZNRecord} has a map field
     * ({@link HelixAccountService#ACCOUNT_METADATA_MAP_KEY}: accountMap), and accountMap contains
     * ("accountId": accountJsonStr) that does not match. This is a NOT good {@link ZNRecord} format that should
     * fail fetch or update.
     *
     * @throws Exception
     * 		Any unexpected exception.
     */
    @Test
    public void testReadBadZNRecordCase4() throws Exception {
        Map<String, String> mapValue = new HashMap<>();
        mapValue.put("-1", refAccount.toJson(true).toString());
        ZNRecord zNRecord = makeZNRecordWithMapField(null, ACCOUNT_METADATA_MAP_KEY, mapValue);
        updateAndWriteZNRecord(zNRecord, false);
    }

    /**
     * Tests reading {@link ZNRecord} from {@link HelixPropertyStore}, where the {@link ZNRecord} has a map field
     * ({@link HelixAccountService#ACCOUNT_METADATA_MAP_KEY}: accountMap), and accountMap contains
     * ("accountId": badAccountJsonString). This is a NOT good {@link ZNRecord} format that should fail fetch or update.
     *
     * @throws Exception
     * 		Any unexpected exception.
     */
    @Test
    public void testReadBadZNRecordCase5() throws Exception {
        Map<String, String> mapValue = new HashMap<>();
        mapValue.put(String.valueOf(refAccount.getId()), HelixAccountServiceTest.BAD_ACCOUNT_METADATA_STRING);
        ZNRecord zNRecord = makeZNRecordWithMapField(null, ACCOUNT_METADATA_MAP_KEY, mapValue);
        updateAndWriteZNRecord(zNRecord, false);
    }

    /**
     * Tests reading {@link ZNRecord} from {@link HelixPropertyStore}, where the {@link ZNRecord} has an invalid account
     * record and a valid account record. This is a NOT good {@link ZNRecord} format and it should fail fetch or update
     * operations, with none of the record should be read.
     *
     * @throws Exception
     * 		Any unexpected exception.
     */
    @Test
    public void testReadBadZNRecordCase6() throws Exception {
        ZNRecord zNRecord = new ZNRecord(String.valueOf(System.currentTimeMillis()));
        Map<String, String> accountMap = new HashMap<>();
        accountMap.put(String.valueOf(refAccount.getId()), refAccount.toJson(true).toString());
        accountMap.put(String.valueOf(((refAccount.getId()) + 1)), HelixAccountServiceTest.BAD_ACCOUNT_METADATA_STRING);
        zNRecord.setMapField(ACCOUNT_METADATA_MAP_KEY, accountMap);
        updateAndWriteZNRecord(zNRecord, false);
    }

    /**
     * Tests receiving a bad message, it will not be recognized by {@link HelixAccountService}, but will also not
     * crash the service.
     *
     * @throws Exception
     * 		Any unexpected exception.
     */
    @Test
    public void receiveBadMessage() throws Exception {
        accountService = mockHelixAccountServiceFactory.getAccountService();
        updateAccountsAndAssertAccountExistence(HelixAccountServiceTest.idToRefAccountMap.values(), HelixAccountServiceTest.NUM_REF_ACCOUNT, true);
        notifier.publish(ACCOUNT_METADATA_CHANGE_TOPIC, "badMessage");
        Assert.assertEquals("The number of account in HelixAccountService is different from expected", HelixAccountServiceTest.NUM_REF_ACCOUNT, accountService.getAllAccounts().size());
    }

    /**
     * Tests receiving a bad topic, it will not be recognized by {@link HelixAccountService}, but will also not
     * crash the service.
     *
     * @throws Exception
     * 		Any unexpected exception.
     */
    @Test
    public void receiveBadTopic() throws Exception {
        accountService = mockHelixAccountServiceFactory.getAccountService();
        updateAccountsAndAssertAccountExistence(HelixAccountServiceTest.idToRefAccountMap.values(), HelixAccountServiceTest.NUM_REF_ACCOUNT, true);
        notifier.publish("badTopic", FULL_ACCOUNT_METADATA_CHANGE_MESSAGE);
        Assert.assertEquals("The number of account in HelixAccountService is different from expected", HelixAccountServiceTest.NUM_REF_ACCOUNT, accountService.getAllAccounts().size());
    }

    /**
     * Tests a number of bad inputs.
     */
    @Test
    public void testNullInputs() throws IOException {
        try {
            new MockHelixAccountServiceFactory(null, new MetricRegistry(), notifier, null).getAccountService();
            Assert.fail("should have thrown");
        } catch (NullPointerException e) {
            // expected
        }
        try {
            new MockHelixAccountServiceFactory(vHelixConfigProps, null, notifier, null).getAccountService();
            Assert.fail("should have thrown");
        } catch (NullPointerException e) {
            // expected
        }
        accountService = new MockHelixAccountServiceFactory(vHelixConfigProps, new MetricRegistry(), null, null).getAccountService();
        accountService.close();
        accountService = mockHelixAccountServiceFactory.getAccountService();
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
     * Tests updating a collection of {@link Account}s, where the {@link Account}s are conflicting among themselves
     * in name.
     *
     * @throws Exception
     * 		Any unexpected exception.
     */
    @Test
    public void testUpdateNameConflictingAccounts() throws Exception {
        accountService = mockHelixAccountServiceFactory.getAccountService();
        List<Account.Account> conflictAccounts = new ArrayList<>();
        conflictAccounts.add(new AccountBuilder(((short) (1)), "a", INACTIVE).build());
        conflictAccounts.add(new AccountBuilder(((short) (2)), "a", INACTIVE).build());
        Assert.assertFalse("Wrong return value from update operation.", accountService.updateAccounts(conflictAccounts));
    }

    /**
     * Tests updating a collection of {@link Account}s, where the {@link Account}s are conflicting among themselves
     * in id.
     *
     * @throws Exception
     * 		Any unexpected exception.
     */
    @Test
    public void testUpdateIdConflictingAccounts() throws Exception {
        accountService = mockHelixAccountServiceFactory.getAccountService();
        List<Account.Account> conflictAccounts = new ArrayList<>();
        conflictAccounts.add(new AccountBuilder(((short) (1)), "a", INACTIVE).build());
        conflictAccounts.add(new AccountBuilder(((short) (1)), "b", INACTIVE).build());
        Assert.assertFalse("Wrong return value from update operation.", accountService.updateAccounts(conflictAccounts));
    }

    /**
     * Tests updating a collection of {@link Account}s, where there are duplicate {@link Account}s in id and name.
     *
     * @throws Exception
     * 		Any unexpected exception.
     */
    @Test
    public void testUpdateDuplicateAccounts() throws Exception {
        accountService = mockHelixAccountServiceFactory.getAccountService();
        List<Account.Account> conflictAccounts = new ArrayList<>();
        conflictAccounts.add(new AccountBuilder(((short) (1)), "a", INACTIVE).build());
        conflictAccounts.add(new AccountBuilder(((short) (1)), "a", INACTIVE).build());
        Assert.assertFalse("Wrong return value from update operation.", accountService.updateAccounts(conflictAccounts));
    }

    /**
     * Tests updating a {@link Account}, which has the same id and name as an existing record, and will replace the
     * existing record. This test corresponds to case A specified in the JavaDoc of {@link AccountService}.
     *
     * @throws Exception
     * 		Any unexpected exception.
     */
    @Test
    public void testNonConflictingUpdateCaseA() throws Exception {
        accountService = mockHelixAccountServiceFactory.getAccountService();
        // write two accounts (1, "a") and (2, "b")
        writeAccountsForConflictTest();
        Account.Account accountToUpdate = accountService.getAccountById(((short) (1)));
        Collection<Account.Account> nonConflictAccounts = Collections.singleton(new AccountBuilder(accountToUpdate).status(ACTIVE).build());
        updateAccountsAndAssertAccountExistence(nonConflictAccounts, 2, true);
    }

    /**
     * Tests updating a {@link Account}, which has the same id as an existing record and a non-conflicting name with any
     * of the existing record. The new record will replace the existing record. This test corresponds to case B specified
     * in the JavaDoc of {@link AccountService}.
     *
     * @throws Exception
     * 		Any unexpected exception.
     */
    @Test
    public void testNonConflictingUpdateCaseB() throws Exception {
        accountService = mockHelixAccountServiceFactory.getAccountService();
        // write two accounts (1, "a") and (2, "b")
        writeAccountsForConflictTest();
        Account.Account accountToUpdate = accountService.getAccountById(((short) (1)));
        Collection<Account.Account> nonConflictAccounts = Collections.singleton(new AccountBuilder(accountToUpdate).status(ACTIVE).build());
        updateAccountsAndAssertAccountExistence(nonConflictAccounts, 2, true);
    }

    /**
     * Tests updating a {@link Account}, which has a new id and name different from any of the existing record. The
     * new record will replace the existing record. This test corresponds to case C specified in the JavaDoc of
     * {@link AccountService}.
     *
     * @throws Exception
     * 		Any unexpected exception.
     */
    @Test
    public void testNonConflictingUpdateCaseC() throws Exception {
        accountService = mockHelixAccountServiceFactory.getAccountService();
        // write two accounts (1, "a") and (2, "b")
        writeAccountsForConflictTest();
        Collection<Account.Account> nonConflictAccounts = Collections.singleton(new AccountBuilder(((short) (3)), "c", ACTIVE).build());
        updateAccountsAndAssertAccountExistence(nonConflictAccounts, 3, true);
    }

    /**
     * Tests updating a {@link Account}, which has a new id but a name conflicting with an existing record. The update
     * operation will fail. This test corresponds to case D specified in the JavaDoc of {@link AccountService}.
     *
     * @throws Exception
     * 		Any unexpected exception.
     */
    @Test
    public void testConflictingUpdateCaseD() throws Exception {
        accountService = mockHelixAccountServiceFactory.getAccountService();
        // write two accounts (1, "a") and (2, "b")
        writeAccountsForConflictTest();
        Collection<Account.Account> conflictAccounts = Collections.singleton(new AccountBuilder(((short) (3)), "a", INACTIVE).build());
        Assert.assertFalse("Wrong return value from update operation.", accountService.updateAccounts(conflictAccounts));
        Assert.assertEquals("Wrong account number in HelixAccountService", 2, accountService.getAllAccounts().size());
        Assert.assertNull("Wrong account got from HelixAccountService", accountService.getAccountById(((short) (3))));
    }

    /**
     * Tests updating a {@link Account}, which has the same id as an existing record, but the name conflicting with
     * another existing record. The update operation will fail. This test corresponds to case E specified in the JavaDoc
     * of {@link AccountService}.
     *
     * @throws Exception
     * 		Any unexpected exception.
     */
    @Test
    public void testConflictingUpdateCaseE() throws Exception {
        accountService = mockHelixAccountServiceFactory.getAccountService();
        // write two accounts (1, "a") and (2, "b")
        writeAccountsForConflictTest();
        Collection<Account.Account> conflictAccounts = Collections.singleton(new AccountBuilder(((short) (1)), "b", INACTIVE).build());
        Assert.assertFalse("Wrong return value from update operation.", accountService.updateAccounts(conflictAccounts));
        Assert.assertEquals("Wrong account number in HelixAccountService", 2, accountService.getAllAccounts().size());
        Assert.assertEquals("Wrong account name got from HelixAccountService", "a", accountService.getAccountById(((short) (1))).getName());
    }

    /**
     * Test updating an account with a conflicting expected snapshot version.
     */
    @Test
    public void testConflictingSnapshotVersionUpdate() throws Exception {
        accountService = mockHelixAccountServiceFactory.getAccountService();
        // write two accounts (1, "a") and (2, "b")
        writeAccountsForConflictTest();
        Account.Account expectedAccount = accountService.getAccountById(((short) (1)));
        int currentSnapshotVersion = expectedAccount.getSnapshotVersion();
        for (int snapshotVersionOffset : new int[]{ -2, -1, 1 }) {
            int snapshotVersionToUse = currentSnapshotVersion + snapshotVersionOffset;
            Collection<Account.Account> conflictAccounts = Collections.singleton(new AccountBuilder(((short) (1)), "c", INACTIVE).snapshotVersion(snapshotVersionToUse).build());
            Assert.assertFalse("Wrong return value from update operation.", accountService.updateAccounts(conflictAccounts));
            Assert.assertEquals("Wrong account number in HelixAccountService", 2, accountService.getAllAccounts().size());
            Account.Account account = accountService.getAccountById(((short) (1)));
            Assert.assertEquals("Account should not have been updated", expectedAccount, account);
            Assert.assertEquals("Snapshot version should not have been updated", currentSnapshotVersion, account.getSnapshotVersion());
        }
        Collection<Account.Account> validAccounts = Collections.singleton(new AccountBuilder(((short) (1)), "c", INACTIVE).snapshotVersion(currentSnapshotVersion).build());
        updateAccountsAndAssertAccountExistence(validAccounts, 2, true);
    }

    /**
     * Tests reading conflicting {@link Account} metadata from {@link HelixPropertyStore}. Two {@link Account}s have
     * different accountIds but the same accountNames. This is a BAD record that should impact fetching or updating
     * {@link Account}s.
     *
     * @throws Exception
     * 		Any unexpected exception.
     */
    @Test
    public void testReadConflictAccountDataFromHelixPropertyStoreCase1() throws Exception {
        List<Account.Account> conflictAccounts = new ArrayList<>();
        Account.Account account1 = build();
        Account.Account account2 = build();
        conflictAccounts.add(account1);
        conflictAccounts.add(account2);
        readAndUpdateBadRecord(conflictAccounts);
    }

    /**
     * Tests reading conflicting {@link Account} metadata from {@link org.apache.helix.store.HelixPropertyStore}.
     *
     * @throws Exception
     * 		Any unexpected exception.
     */
    @Test
    public void testReadConflictAccountDataFromHelixPropertyStoreCase2() throws Exception {
        List<Account.Account> conflictAccounts = new ArrayList<>();
        Account.Account account1 = build();
        Account.Account account2 = build();
        Account.Account account3 = build();
        Account.Account account4 = build();
        conflictAccounts.add(account1);
        conflictAccounts.add(account2);
        conflictAccounts.add(account3);
        conflictAccounts.add(account4);
        readAndUpdateBadRecord(conflictAccounts);
    }

    /**
     * Tests a series of operations.
     * 1. PrePopulates account (1, "a");
     * 2. Starts up a {@link HelixAccountService};
     * 3. Remote copy adds a new account (2, "b"), and the update has not been propagated to the {@link HelixAccountService};
     * 4. The {@link HelixAccountService} attempts to update an account (3, "b"), which should fail because it will eventually
     *    conflict with the remote copy;
     *
     * @throws Exception
     * 		Any unexpected exception.
     */
    @Test
    public void testReadConflictAccountDataFromHelixPropertyStoreCase3() throws Exception {
        Account.Account account1 = build();
        List<Account.Account> accounts = Collections.singletonList(account1);
        writeAccountsToHelixPropertyStore(accounts, false);
        accountService = mockHelixAccountServiceFactory.getAccountService();
        AccountTestUtils.assertAccountInAccountService(account1, accountService);
        Account.Account account2 = build();
        accounts = Collections.singletonList(account2);
        writeAccountsToHelixPropertyStore(accounts, false);
        Account.Account conflictingAccount = build();
        accounts = Collections.singletonList(conflictingAccount);
        Assert.assertFalse(accountService.updateAccounts(accounts));
        Assert.assertEquals("Number of account is wrong.", 1, accountService.getAllAccounts().size());
        AccountTestUtils.assertAccountInAccountService(account1, accountService);
    }

    /**
     * Tests adding/removing {@link Consumer}.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testAccountUpdateConsumer() throws Exception {
        // pre-populate account metadata in ZK.
        writeAccountsToHelixPropertyStore(HelixAccountServiceTest.idToRefAccountMap.values(), false);
        accountService = mockHelixAccountServiceFactory.getAccountService();
        AccountTestUtils.assertAccountsInAccountService(HelixAccountServiceTest.idToRefAccountMap.values(), HelixAccountServiceTest.NUM_REF_ACCOUNT, accountService);
        // add consumer
        int numOfConsumers = 10;
        List<Collection<Account.Account>> updatedAccountsReceivedByConsumers = new ArrayList<>();
        List<Consumer<Collection<Account.Account>>> accountUpdateConsumers = IntStream.range(0, numOfConsumers).mapToObj(( i) -> ((Consumer<Collection<Account.Account>>) (updatedAccountsReceivedByConsumers::add))).peek(accountService::addAccountUpdateConsumer).collect(Collectors.toList());
        // listen to adding a new account
        Account.Account newAccount = build();
        Set<Account.Account> accountsToUpdate = Collections.singleton(newAccount);
        updateAccountsAndAssertAccountExistence(accountsToUpdate, (1 + (HelixAccountServiceTest.NUM_REF_ACCOUNT)), true);
        assertAccountUpdateConsumers(accountsToUpdate, numOfConsumers, updatedAccountsReceivedByConsumers);
        // listen to modification of existing accounts. Only updated accounts will be received by consumers.
        updatedAccountsReceivedByConsumers.clear();
        accountsToUpdate = new HashSet();
        for (Account.Account account : accountService.getAllAccounts()) {
            AccountBuilder accountBuilder = new AccountBuilder(account);
            accountBuilder.name(((account.getName()) + "-extra"));
            accountsToUpdate.add(accountBuilder.build());
        }
        updateAccountsAndAssertAccountExistence(accountsToUpdate, (1 + (HelixAccountServiceTest.NUM_REF_ACCOUNT)), true);
        assertAccountUpdateConsumers(accountsToUpdate, numOfConsumers, updatedAccountsReceivedByConsumers);
        // removes the consumers so the consumers will not be informed.
        updatedAccountsReceivedByConsumers.clear();
        for (Consumer<Collection<Account.Account>> accountUpdateConsumer : accountUpdateConsumers) {
            accountService.removeAccountUpdateConsumer(accountUpdateConsumer);
        }
        Account.Account account = accountService.getAccountById(refAccountId);
        Account.Account updatedAccount = build();
        accountsToUpdate = new HashSet(Collections.singleton(updatedAccount));
        updateAccountsAndAssertAccountExistence(accountsToUpdate, (1 + (HelixAccountServiceTest.NUM_REF_ACCOUNT)), true);
        assertAccountUpdateConsumers(Collections.emptySet(), 0, updatedAccountsReceivedByConsumers);
    }

    /**
     * Tests the background updater for updating accounts from remote. During the initialization of
     * {@link HelixAccountService}, its internal {@link HelixPropertyStore} will be read to first time get account data.
     * Because of the background account updater, it should continuously make get calls to the {@link HelixPropertyStore},
     * even no notification for account updates is received. Therefore, there will be more than 1 get calls to the
     * {@link HelixPropertyStore}.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testBackgroundUpdater() throws Exception {
        helixConfigProps.setProperty(UPDATER_POLLING_INTERVAL_MS_KEY, "1");
        vHelixConfigProps = new VerifiableProperties(helixConfigProps);
        storeConfig = new HelixPropertyStoreConfig(vHelixConfigProps);
        String updaterThreadPrefix = UUID.randomUUID().toString();
        MockHelixAccountServiceFactory mockHelixAccountServiceFactory = new MockHelixAccountServiceFactory(vHelixConfigProps, new MetricRegistry(), notifier, updaterThreadPrefix);
        accountService = mockHelixAccountServiceFactory.getAccountService();
        CountDownLatch latch = new CountDownLatch(1);
        mockHelixAccountServiceFactory.getHelixStore(HelixAccountServiceTest.ZK_CONNECT_STRING, storeConfig).setReadLatch(latch);
        Assert.assertEquals("Wrong number of thread for account updater.", 1, numThreadsByThisName(updaterThreadPrefix));
        awaitLatchOrTimeout(latch, 100);
    }

    /**
     * Tests disabling the background thread. By setting the polling interval to 0ms, the accounts should not be fetched.
     * Therefore, after the {@link HelixAccountService} starts, there should be a single get call to the
     * {@link HelixPropertyStore}.
     */
    @Test
    public void testDisableBackgroundUpdater() {
        helixConfigProps.setProperty(UPDATER_POLLING_INTERVAL_MS_KEY, "0");
        vHelixConfigProps = new VerifiableProperties(helixConfigProps);
        storeConfig = new HelixPropertyStoreConfig(vHelixConfigProps);
        String updaterThreadPrefix = UUID.randomUUID().toString();
        MockHelixAccountServiceFactory mockHelixAccountServiceFactory = new MockHelixAccountServiceFactory(vHelixConfigProps, new MetricRegistry(), notifier, updaterThreadPrefix);
        accountService = mockHelixAccountServiceFactory.getAccountService();
        Assert.assertEquals("Wrong number of thread for account updater.", 0, numThreadsByThisName(updaterThreadPrefix));
    }

    /**
     * Tests disabling the background thread. By setting the polling interval to 0ms, the accounts should not be fetched.
     * Therefore, after the {@link HelixAccountService} starts, there should be a single get call to the
     * {@link HelixPropertyStore}.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testDisabledBackups() throws Exception {
        helixConfigProps.remove(BACKUP_DIRECTORY_KEY);
        vHelixConfigProps = new VerifiableProperties(helixConfigProps);
        storeConfig = new HelixPropertyStoreConfig(vHelixConfigProps);
        String updaterThreadPrefix = UUID.randomUUID().toString();
        MockHelixAccountServiceFactory mockHelixAccountServiceFactory = new MockHelixAccountServiceFactory(vHelixConfigProps, new MetricRegistry(), notifier, updaterThreadPrefix);
        accountService = mockHelixAccountServiceFactory.getAccountService();
        updateAccountsAndAssertAccountExistence(Collections.singleton(refAccount), 1, true);
    }
}

