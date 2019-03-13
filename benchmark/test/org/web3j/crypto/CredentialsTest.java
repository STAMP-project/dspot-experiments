package org.web3j.crypto;


import org.junit.Test;


public class CredentialsTest {
    @Test
    public void testCredentialsFromString() {
        Credentials credentials = Credentials.create(SampleKeys.KEY_PAIR);
        verify(credentials);
    }

    @Test
    public void testCredentialsFromECKeyPair() {
        Credentials credentials = Credentials.create(SampleKeys.PRIVATE_KEY_STRING, SampleKeys.PUBLIC_KEY_STRING);
        verify(credentials);
    }
}

