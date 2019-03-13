package org.whispersystems.textsecuregcm.tests.auth;


import java.util.Optional;
import javax.ws.rs.WebApplicationException;
import junit.framework.TestCase;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.whispersystems.textsecuregcm.auth.Anonymous;
import org.whispersystems.textsecuregcm.auth.OptionalAccess;
import org.whispersystems.textsecuregcm.storage.Account;
import org.whispersystems.textsecuregcm.util.Base64;


public class OptionalAccessTest {
    @Test
    public void testUnidentifiedMissingTarget() {
        try {
            OptionalAccess.verify(Optional.empty(), Optional.empty(), Optional.empty());
            throw new AssertionError("should fail");
        } catch (WebApplicationException e) {
            TestCase.assertEquals(e.getResponse().getStatus(), 401);
        }
    }

    @Test
    public void testUnidentifiedMissingTargetDevice() {
        Account account = Mockito.mock(Account.class);
        Mockito.when(account.isActive()).thenReturn(true);
        Mockito.when(account.getDevice(ArgumentMatchers.eq(10))).thenReturn(Optional.empty());
        Mockito.when(account.getUnidentifiedAccessKey()).thenReturn(Optional.of("1234".getBytes()));
        try {
            OptionalAccess.verify(Optional.empty(), Optional.of(new Anonymous(Base64.encodeBytes("1234".getBytes()))), Optional.of(account), "10");
        } catch (WebApplicationException e) {
            TestCase.assertEquals(e.getResponse().getStatus(), 401);
        }
    }

    @Test
    public void testUnidentifiedBadTargetDevice() {
        Account account = Mockito.mock(Account.class);
        Mockito.when(account.isActive()).thenReturn(true);
        Mockito.when(account.getDevice(ArgumentMatchers.eq(10))).thenReturn(Optional.empty());
        Mockito.when(account.getUnidentifiedAccessKey()).thenReturn(Optional.of("1234".getBytes()));
        try {
            OptionalAccess.verify(Optional.empty(), Optional.of(new Anonymous(Base64.encodeBytes("1234".getBytes()))), Optional.of(account), "$$");
        } catch (WebApplicationException e) {
            TestCase.assertEquals(e.getResponse().getStatus(), 422);
        }
    }

    @Test
    public void testUnidentifiedBadCode() {
        Account account = Mockito.mock(Account.class);
        Mockito.when(account.isActive()).thenReturn(true);
        Mockito.when(account.getUnidentifiedAccessKey()).thenReturn(Optional.of("1234".getBytes()));
        try {
            OptionalAccess.verify(Optional.empty(), Optional.of(new Anonymous(Base64.encodeBytes("5678".getBytes()))), Optional.of(account));
            throw new AssertionError("should fail");
        } catch (WebApplicationException e) {
            TestCase.assertEquals(e.getResponse().getStatus(), 401);
        }
    }

    @Test
    public void testIdentifiedMissingTarget() {
        Account account = Mockito.mock(Account.class);
        Mockito.when(account.isActive()).thenReturn(true);
        try {
            OptionalAccess.verify(Optional.of(account), Optional.empty(), Optional.empty());
            throw new AssertionError("should fail");
        } catch (WebApplicationException e) {
            TestCase.assertEquals(e.getResponse().getStatus(), 404);
        }
    }

    @Test
    public void testUnsolicitedBadTarget() {
        Account account = Mockito.mock(Account.class);
        Mockito.when(account.isUnrestrictedUnidentifiedAccess()).thenReturn(false);
        Mockito.when(account.isActive()).thenReturn(true);
        try {
            OptionalAccess.verify(Optional.empty(), Optional.empty(), Optional.of(account));
            throw new AssertionError("shold fai");
        } catch (WebApplicationException e) {
            TestCase.assertEquals(e.getResponse().getStatus(), 401);
        }
    }

    @Test
    public void testUnsolicitedGoodTarget() {
        Account account = Mockito.mock(Account.class);
        Anonymous random = Mockito.mock(Anonymous.class);
        Mockito.when(account.isUnrestrictedUnidentifiedAccess()).thenReturn(true);
        Mockito.when(account.isActive()).thenReturn(true);
        OptionalAccess.verify(Optional.empty(), Optional.of(random), Optional.of(account));
    }

    @Test
    public void testUnidentifiedGoodTarget() {
        Account account = Mockito.mock(Account.class);
        Mockito.when(account.getUnidentifiedAccessKey()).thenReturn(Optional.of("1234".getBytes()));
        Mockito.when(account.isActive()).thenReturn(true);
        OptionalAccess.verify(Optional.empty(), Optional.of(new Anonymous(Base64.encodeBytes("1234".getBytes()))), Optional.of(account));
    }

    @Test
    public void testUnidentifiedInactive() {
        Account account = Mockito.mock(Account.class);
        Mockito.when(account.getUnidentifiedAccessKey()).thenReturn(Optional.of("1234".getBytes()));
        Mockito.when(account.isActive()).thenReturn(false);
        try {
            OptionalAccess.verify(Optional.empty(), Optional.of(new Anonymous(Base64.encodeBytes("1234".getBytes()))), Optional.of(account));
            throw new AssertionError();
        } catch (WebApplicationException e) {
            TestCase.assertEquals(e.getResponse().getStatus(), 401);
        }
    }

    @Test
    public void testIdentifiedGoodTarget() {
        Account source = Mockito.mock(Account.class);
        Account target = Mockito.mock(Account.class);
        Mockito.when(target.isActive()).thenReturn(true);
        OptionalAccess.verify(Optional.of(source), Optional.empty(), Optional.of(target));
    }
}

