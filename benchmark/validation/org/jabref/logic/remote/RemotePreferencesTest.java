package org.jabref.logic.remote;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class RemotePreferencesTest {
    private RemotePreferences preferences;

    @Test
    public void testGetPort() {
        Assertions.assertEquals(1000, preferences.getPort());
    }

    @Test
    public void testSetPort() {
        preferences.setPort(2000);
        Assertions.assertEquals(2000, preferences.getPort());
    }

    @Test
    public void testUseRemoteServer() {
        Assertions.assertTrue(preferences.useRemoteServer());
    }

    @Test
    public void testSetUseRemoteServer() {
        preferences.setUseRemoteServer(false);
        Assertions.assertFalse(preferences.useRemoteServer());
    }

    @Test
    public void testIsDifferentPortTrue() {
        Assertions.assertTrue(preferences.isDifferentPort(2000));
    }

    @Test
    public void testIsDifferentPortFalse() {
        Assertions.assertFalse(preferences.isDifferentPort(1000));
    }
}

