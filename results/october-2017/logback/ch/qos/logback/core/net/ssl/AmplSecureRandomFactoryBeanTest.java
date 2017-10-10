/**
 * Logback: the reliable, generic, fast and flexible logging framework.
 * Copyright (C) 1999-2015, QOS.ch. All rights reserved.
 *
 * This program and the accompanying materials are dual-licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation
 *
 *   or (per the licensee's choosing)
 *
 * under the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation.
 */
package ch.qos.logback.core.net.ssl;


/**
 * Unit tests for {@link SecureRandomFactoryBean}.
 *
 * @author Carl Harris
 */
public class AmplSecureRandomFactoryBeanTest {
    private ch.qos.logback.core.net.ssl.SecureRandomFactoryBean factoryBean = new ch.qos.logback.core.net.ssl.SecureRandomFactoryBean();

    @org.junit.Test
    public void testDefaults() throws java.lang.Exception {
        org.junit.Assert.assertNotNull(factoryBean.createSecureRandom());
    }

    @org.junit.Test
    public void testExplicitProvider() throws java.lang.Exception {
        java.security.SecureRandom secureRandom = java.security.SecureRandom.getInstance(ch.qos.logback.core.net.ssl.SSL.DEFAULT_SECURE_RANDOM_ALGORITHM);
        factoryBean.setProvider(secureRandom.getProvider().getName());
        org.junit.Assert.assertNotNull(factoryBean.createSecureRandom());
    }

    @org.junit.Test
    public void testUnknownProvider() throws java.lang.Exception {
        factoryBean.setProvider(ch.qos.logback.core.net.ssl.SSLTestConstants.FAKE_PROVIDER_NAME);
        try {
            factoryBean.createSecureRandom();
            org.junit.Assert.fail("expected NoSuchProviderException");
        } catch (java.security.NoSuchProviderException ex) {
            org.junit.Assert.assertTrue(ex.getMessage().contains(ch.qos.logback.core.net.ssl.SSLTestConstants.FAKE_PROVIDER_NAME));
        }
    }

    @org.junit.Test
    public void testUnknownAlgorithm() throws java.lang.Exception {
        factoryBean.setAlgorithm(ch.qos.logback.core.net.ssl.SSLTestConstants.FAKE_ALGORITHM_NAME);
        try {
            factoryBean.createSecureRandom();
            org.junit.Assert.fail("expected NoSuchAlgorithmException");
        } catch (java.security.NoSuchAlgorithmException ex) {
            org.junit.Assert.assertTrue(ex.getMessage().contains(ch.qos.logback.core.net.ssl.SSLTestConstants.FAKE_ALGORITHM_NAME));
        }
    }
}

