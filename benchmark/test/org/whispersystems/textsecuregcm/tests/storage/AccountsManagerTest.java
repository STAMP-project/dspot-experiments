package org.whispersystems.textsecuregcm.tests.storage;


import java.util.HashSet;
import java.util.Optional;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.whispersystems.textsecuregcm.redis.ReplicatedJedisPool;
import org.whispersystems.textsecuregcm.storage.Account;
import org.whispersystems.textsecuregcm.storage.Accounts;
import org.whispersystems.textsecuregcm.storage.AccountsManager;
import org.whispersystems.textsecuregcm.storage.DirectoryManager;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisException;


public class AccountsManagerTest {
    @Test
    public void testGetAccountInCache() {
        ReplicatedJedisPool cacheClient = Mockito.mock(ReplicatedJedisPool.class);
        Jedis jedis = Mockito.mock(Jedis.class);
        Accounts accounts = Mockito.mock(Accounts.class);
        DirectoryManager directoryManager = Mockito.mock(DirectoryManager.class);
        Mockito.when(cacheClient.getReadResource()).thenReturn(jedis);
        Mockito.when(jedis.get(ArgumentMatchers.eq("Account5+14152222222"))).thenReturn("{\"number\": \"+14152222222\", \"name\": \"test\"}");
        AccountsManager accountsManager = new AccountsManager(accounts, directoryManager, cacheClient);
        Optional<Account> account = accountsManager.get("+14152222222");
        TestCase.assertTrue(account.isPresent());
        Assert.assertEquals(account.get().getNumber(), "+14152222222");
        Assert.assertEquals(account.get().getProfileName(), "test");
        Mockito.verify(jedis, Mockito.times(1)).get(ArgumentMatchers.eq("Account5+14152222222"));
        Mockito.verify(jedis, Mockito.times(1)).close();
        Mockito.verifyNoMoreInteractions(jedis);
        Mockito.verifyNoMoreInteractions(accounts);
    }

    @Test
    public void testGetAccountNotInCache() {
        ReplicatedJedisPool cacheClient = Mockito.mock(ReplicatedJedisPool.class);
        Jedis jedis = Mockito.mock(Jedis.class);
        Accounts accounts = Mockito.mock(Accounts.class);
        DirectoryManager directoryManager = Mockito.mock(DirectoryManager.class);
        Account account = new Account("+14152222222", new HashSet(), new byte[16]);
        Mockito.when(cacheClient.getReadResource()).thenReturn(jedis);
        Mockito.when(cacheClient.getWriteResource()).thenReturn(jedis);
        Mockito.when(jedis.get(ArgumentMatchers.eq("Account5+14152222222"))).thenReturn(null);
        Mockito.when(accounts.get(ArgumentMatchers.eq("+14152222222"))).thenReturn(account);
        AccountsManager accountsManager = new AccountsManager(accounts, directoryManager, cacheClient);
        Optional<Account> retrieved = accountsManager.get("+14152222222");
        TestCase.assertTrue(retrieved.isPresent());
        TestCase.assertSame(retrieved.get(), account);
        Mockito.verify(jedis, Mockito.times(1)).get(ArgumentMatchers.eq("Account5+14152222222"));
        Mockito.verify(jedis, Mockito.times(1)).set(ArgumentMatchers.eq("Account5+14152222222"), ArgumentMatchers.anyString());
        Mockito.verify(jedis, Mockito.times(2)).close();
        Mockito.verifyNoMoreInteractions(jedis);
        Mockito.verify(accounts, Mockito.times(1)).get(ArgumentMatchers.eq("+14152222222"));
        Mockito.verifyNoMoreInteractions(accounts);
    }

    @Test
    public void testGetAccountBrokenCache() {
        ReplicatedJedisPool cacheClient = Mockito.mock(ReplicatedJedisPool.class);
        Jedis jedis = Mockito.mock(Jedis.class);
        Accounts accounts = Mockito.mock(Accounts.class);
        DirectoryManager directoryManager = Mockito.mock(DirectoryManager.class);
        Account account = new Account("+14152222222", new HashSet(), new byte[16]);
        Mockito.when(cacheClient.getReadResource()).thenReturn(jedis);
        Mockito.when(cacheClient.getWriteResource()).thenReturn(jedis);
        Mockito.when(jedis.get(ArgumentMatchers.eq("Account5+14152222222"))).thenThrow(new JedisException("Connection lost!"));
        Mockito.when(accounts.get(ArgumentMatchers.eq("+14152222222"))).thenReturn(account);
        AccountsManager accountsManager = new AccountsManager(accounts, directoryManager, cacheClient);
        Optional<Account> retrieved = accountsManager.get("+14152222222");
        TestCase.assertTrue(retrieved.isPresent());
        TestCase.assertSame(retrieved.get(), account);
        Mockito.verify(jedis, Mockito.times(1)).get(ArgumentMatchers.eq("Account5+14152222222"));
        Mockito.verify(jedis, Mockito.times(1)).set(ArgumentMatchers.eq("Account5+14152222222"), ArgumentMatchers.anyString());
        Mockito.verify(jedis, Mockito.times(2)).close();
        Mockito.verifyNoMoreInteractions(jedis);
        Mockito.verify(accounts, Mockito.times(1)).get(ArgumentMatchers.eq("+14152222222"));
        Mockito.verifyNoMoreInteractions(accounts);
    }
}

