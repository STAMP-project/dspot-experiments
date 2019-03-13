package org.whispersystems.textsecuregcm.tests.storage;


import java.util.HashSet;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.whispersystems.textsecuregcm.storage.Account;
import org.whispersystems.textsecuregcm.storage.Device;


public class AccountTest {
    private final Device oldMasterDevice = Mockito.mock(Device.class);

    private final Device recentMasterDevice = Mockito.mock(Device.class);

    private final Device agingSecondaryDevice = Mockito.mock(Device.class);

    private final Device recentSecondaryDevice = Mockito.mock(Device.class);

    private final Device oldSecondaryDevice = Mockito.mock(Device.class);

    @Test
    public void testAccountActive() {
        Account recentAccount = new Account("+14152222222", new HashSet<Device>() {
            {
                add(recentMasterDevice);
                add(recentSecondaryDevice);
            }
        }, "1234".getBytes());
        Assert.assertTrue(recentAccount.isActive());
        Account oldSecondaryAccount = new Account("+14152222222", new HashSet<Device>() {
            {
                add(recentMasterDevice);
                add(agingSecondaryDevice);
            }
        }, "1234".getBytes());
        Assert.assertTrue(oldSecondaryAccount.isActive());
        Account agingPrimaryAccount = new Account("+14152222222", new HashSet<Device>() {
            {
                add(oldMasterDevice);
                add(agingSecondaryDevice);
            }
        }, "1234".getBytes());
        Assert.assertTrue(agingPrimaryAccount.isActive());
    }

    @Test
    public void testAccountInactive() {
        Account oldPrimaryAccount = new Account("+14152222222", new HashSet<Device>() {
            {
                add(oldMasterDevice);
                add(oldSecondaryDevice);
            }
        }, "1234".getBytes());
        Assert.assertFalse(oldPrimaryAccount.isActive());
    }
}

