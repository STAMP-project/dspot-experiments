/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.hibernateFilters;


import org.hibernate.Hibernate;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.FailureExpected;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
public class ProxyPreservingFiltersOutsideInitialSessionTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    @FailureExpected(jiraKey = "HHH-11076", message = "Fix rejected, we need another approach to fix this issue!")
    public void testPreserveFilters() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            AccountGroup accountGroup = new AccountGroup();
            accountGroup.setId(1L);
            entityManager.persist(accountGroup);
            Account account = new Account();
            account.setName("A1");
            account.setRegionCode("Europe");
            entityManager.persist(account);
            accountGroup.getAccounts().add(account);
            account = new Account();
            account.setName("A2");
            account.setRegionCode("Europe");
            entityManager.persist(account);
            accountGroup.getAccounts().add(account);
            account = new Account();
            account.setName("A3");
            account.setRegionCode("US");
            entityManager.persist(account);
            accountGroup.getAccounts().add(account);
        });
        AccountGroup group = TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            entityManager.unwrap(.class).enableFilter("byRegion").setParameter("region", "US");
            return entityManager.find(.class, 1L);
        });
        Assert.assertEquals(1, group.getAccounts().size());
    }

    @Test
    public void testChangeFilterBeforeInitializeInSameSession() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            AccountGroup accountGroup = new AccountGroup();
            accountGroup.setId(1L);
            entityManager.persist(accountGroup);
            Account account = new Account();
            account.setName("A1");
            account.setRegionCode("Europe");
            entityManager.persist(account);
            accountGroup.getAccounts().add(account);
            account = new Account();
            account.setName("A2");
            account.setRegionCode("Europe");
            entityManager.persist(account);
            accountGroup.getAccounts().add(account);
            account = new Account();
            account.setName("A3");
            account.setRegionCode("US");
            entityManager.persist(account);
            accountGroup.getAccounts().add(account);
        });
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            entityManager.unwrap(.class).enableFilter("byRegion").setParameter("region", "US");
            AccountGroup accountGroup = entityManager.find(.class, 1L);
            // Change the filter
            entityManager.unwrap(.class).enableFilter("byRegion").setParameter("region", "Europe");
            Hibernate.initialize(accountGroup.getAccounts());
            // will contain accounts with regionCode "Europe"
            assertEquals(2, accountGroup.getAccounts().size());
            return accountGroup;
        });
    }

    @Test
    @FailureExpected(jiraKey = "HHH-11076", message = "Fix rejected, we need another approach to fix this issue!")
    public void testChangeFilterBeforeInitializeInTempSession() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            AccountGroup accountGroup = new AccountGroup();
            accountGroup.setId(1L);
            entityManager.persist(accountGroup);
            Account account = new Account();
            account.setName("A1");
            account.setRegionCode("Europe");
            entityManager.persist(account);
            accountGroup.getAccounts().add(account);
            account = new Account();
            account.setName("A2");
            account.setRegionCode("Europe");
            entityManager.persist(account);
            accountGroup.getAccounts().add(account);
            account = new Account();
            account.setName("A3");
            account.setRegionCode("US");
            entityManager.persist(account);
            accountGroup.getAccounts().add(account);
        });
        AccountGroup group = TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            entityManager.unwrap(.class).enableFilter("byRegion").setParameter("region", "US");
            AccountGroup accountGroup = entityManager.find(.class, 1L);
            // Change the filter.
            entityManager.unwrap(.class).enableFilter("byRegion").setParameter("region", "Europe");
            return accountGroup;
        });
        log.info("Initialize accounts collection");
        // What should group.getAccounts() contain? Should it be accounts with regionCode "Europe"
        // because that was the most recent filter used in the session?
        Hibernate.initialize(group.getAccounts());
        // The following will fail because the collection will only contain accounts with regionCode "US"
        Assert.assertEquals(2, group.getAccounts().size());
    }

    @Test
    public void testMergeNoFilterThenInitializeTempSession() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            AccountGroup accountGroup = new AccountGroup();
            accountGroup.setId(1L);
            entityManager.persist(accountGroup);
            Account account = new Account();
            account.setName("A1");
            account.setRegionCode("Europe");
            entityManager.persist(account);
            accountGroup.getAccounts().add(account);
            account = new Account();
            account.setName("A2");
            account.setRegionCode("Europe");
            entityManager.persist(account);
            accountGroup.getAccounts().add(account);
            account = new Account();
            account.setName("A3");
            account.setRegionCode("US");
            entityManager.persist(account);
            accountGroup.getAccounts().add(account);
        });
        final AccountGroup group = TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            entityManager.unwrap(.class).enableFilter("byRegion").setParameter("region", "US");
            return entityManager.find(.class, 1L);
        });
        final AccountGroup mergedGroup = TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            return entityManager.merge(group);
        });
        // group.getAccounts() will be unfiltered because merge cleared AbstractCollectionPersister#enabledFilters
        Hibernate.initialize(mergedGroup.getAccounts());
        Assert.assertEquals(3, mergedGroup.getAccounts().size());
    }

    @Test
    public void testSaveOrUpdateNoFilterThenInitializeTempSession() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            AccountGroup accountGroup = new AccountGroup();
            accountGroup.setId(1L);
            entityManager.persist(accountGroup);
            Account account = new Account();
            account.setName("A1");
            account.setRegionCode("Europe");
            entityManager.persist(account);
            accountGroup.getAccounts().add(account);
            account = new Account();
            account.setName("A2");
            account.setRegionCode("Europe");
            entityManager.persist(account);
            accountGroup.getAccounts().add(account);
            account = new Account();
            account.setName("A3");
            account.setRegionCode("US");
            entityManager.persist(account);
            accountGroup.getAccounts().add(account);
        });
        final AccountGroup group = TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            entityManager.unwrap(.class).enableFilter("byRegion").setParameter("region", "US");
            return entityManager.find(.class, 1L);
        });
        final AccountGroup savedGroup = TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            // saveOrUpdate adds the PersistenceCollection to the session "as is"
            return ((AccountGroup) (entityManager.unwrap(.class).merge(group)));
        });
        Hibernate.initialize(savedGroup.getAccounts());
        // group.getAccounts() should not be filtered.
        // the following fails because AbstractCollectionPersister#enabledFilters is still intact.
        Assert.assertEquals(3, savedGroup.getAccounts().size());
    }
}

