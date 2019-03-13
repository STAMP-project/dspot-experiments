package com.github.scribejava.core.services;


import org.junit.Assert;
import org.junit.Test;


public class RSASha1SignatureServiceTest {
    private final RSASha1SignatureService service = new RSASha1SignatureService(RSASha1SignatureServiceTest.getPrivateKey());

    @Test
    public void shouldReturnSignatureMethodString() {
        final String expected = "RSA-SHA1";
        Assert.assertEquals(expected, service.getSignatureMethod());
    }

    @Test
    public void shouldReturnSignature() {
        final String apiSecret = "api secret";
        final String tokenSecret = "token secret";
        final String baseString = "base string";
        final String signature = "LUNRzQAlpdNyM9mLXm96Va6g/qVNnEAb7p7K1KM0g8IopOFQJPoOO7cvppgt7w3QyhijWJnCmvqXaaIAGrqvd" + "yr3fIzBULh8D/iZQUNLMi08GCOA34P81XBvsc7A5uJjPDsGhJg2MzoVJ8nWJhU/lMMk4c92S1WGskeoDofRwpo=";
        Assert.assertEquals(signature, service.getSignature(baseString, apiSecret, tokenSecret));
    }
}

