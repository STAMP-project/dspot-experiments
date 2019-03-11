package tests.security.cert;


import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.cert.CRL;
import java.security.cert.CRLSelector;
import java.security.cert.CertSelector;
import java.security.cert.CertStore;
import java.security.cert.CertStoreException;
import java.security.cert.CertStoreParameters;
import java.security.cert.CertStoreSpi;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Collection;
import junit.framework.TestCase;


public class CertStore2Test extends TestCase {
    private static final String CERT_STORE_PROVIDER_NAME = "TestCertStoreProvider";

    private static final String CERT_STORE_NAME = "TestCertStore";

    Provider provider;

    public void testGetInstanceStringCertStoreParameters() {
        try {
            CertStoreParameters parameters = new CertStore2Test.MyCertStoreParameters();
            CertStore certStore = CertStore.getInstance(CertStore2Test.CERT_STORE_NAME, parameters);
            TestCase.assertNotNull(certStore);
            TestCase.assertNotNull(certStore.getCertStoreParameters());
            TestCase.assertNotSame(parameters, certStore.getCertStoreParameters());
        } catch (NoSuchAlgorithmException e) {
            TestCase.fail(("unexpected exception: " + e));
        } catch (InvalidAlgorithmParameterException e) {
            TestCase.fail(("unexpected exception: " + e));
        }
        try {
            CertStore certStore = CertStore.getInstance(CertStore2Test.CERT_STORE_NAME, null);
            TestCase.assertNotNull(certStore);
            TestCase.assertNull(certStore.getCertStoreParameters());
        } catch (InvalidAlgorithmParameterException e) {
            TestCase.fail(("unexpected exception: " + e));
        } catch (NoSuchAlgorithmException e) {
            TestCase.fail(("unexpected exception: " + e));
        }
        try {
            CertStore.getInstance("UnknownCertStore", null);
            TestCase.fail("expected NoSuchAlgorithmException");
        } catch (InvalidAlgorithmParameterException e) {
            TestCase.fail(("unexpected exception: " + e));
        } catch (NoSuchAlgorithmException e) {
            // ok
        }
        try {
            CertStore.getInstance(CertStore2Test.CERT_STORE_NAME, new CertStore2Test.MyOtherCertStoreParameters());
            TestCase.fail("expected InvalidAlgorithmParameterException");
        } catch (InvalidAlgorithmParameterException e) {
            // ok
        } catch (NoSuchAlgorithmException e) {
            TestCase.fail(("unexpected exception: " + e));
        }
    }

    public void testGetInstanceStringCertStoreParametersString() {
        try {
            CertStoreParameters parameters = new CertStore2Test.MyCertStoreParameters();
            CertStore certStore = CertStore.getInstance(CertStore2Test.CERT_STORE_NAME, parameters, CertStore2Test.CERT_STORE_PROVIDER_NAME);
            TestCase.assertNotNull(certStore);
            TestCase.assertNotNull(certStore.getCertStoreParameters());
            TestCase.assertNotSame(parameters, certStore.getCertStoreParameters());
            TestCase.assertEquals(CertStore2Test.CERT_STORE_PROVIDER_NAME, certStore.getProvider().getName());
        } catch (InvalidAlgorithmParameterException e) {
            TestCase.fail(("unexpected exception: " + e));
        } catch (NoSuchAlgorithmException e) {
            TestCase.fail(("unexpected exception: " + e));
        } catch (NoSuchProviderException e) {
            TestCase.fail(("unexpected exception: " + e));
        }
        try {
            CertStore certStore = CertStore.getInstance(CertStore2Test.CERT_STORE_NAME, null, CertStore2Test.CERT_STORE_PROVIDER_NAME);
            TestCase.assertNotNull(certStore);
            TestCase.assertNull(certStore.getCertStoreParameters());
            TestCase.assertEquals(CertStore2Test.CERT_STORE_PROVIDER_NAME, certStore.getProvider().getName());
        } catch (InvalidAlgorithmParameterException e) {
            TestCase.fail(("unexpected exception: " + e));
        } catch (NoSuchAlgorithmException e) {
            TestCase.fail(("unexpected exception: " + e));
        } catch (NoSuchProviderException e) {
            TestCase.fail(("unexpected exception: " + e));
        }
        try {
            CertStore.getInstance("UnknownCertStore", new CertStore2Test.MyCertStoreParameters(), CertStore2Test.CERT_STORE_PROVIDER_NAME);
            TestCase.fail("expected NoSuchAlgorithmException");
        } catch (InvalidAlgorithmParameterException e) {
            TestCase.fail(("unexpected exception: " + e));
        } catch (NoSuchAlgorithmException e) {
            // ok
        } catch (NoSuchProviderException e) {
            TestCase.fail(("unexpected exception: " + e));
        }
        try {
            CertStore.getInstance(CertStore2Test.CERT_STORE_NAME, null, "UnknownCertStoreProvider");
            TestCase.fail("expected NoSuchProviderException");
        } catch (InvalidAlgorithmParameterException e) {
            TestCase.fail(("unexpected exception: " + e));
        } catch (NoSuchAlgorithmException e) {
            TestCase.fail(("unexpected exception: " + e));
        } catch (NoSuchProviderException e) {
            // ok
        }
        try {
            CertStore.getInstance(CertStore2Test.CERT_STORE_NAME, new CertStore2Test.MyOtherCertStoreParameters(), CertStore2Test.CERT_STORE_PROVIDER_NAME);
        } catch (InvalidAlgorithmParameterException e) {
            // ok
        } catch (NoSuchAlgorithmException e) {
            TestCase.fail(("unexpected exception: " + e));
        } catch (NoSuchProviderException e) {
            TestCase.fail(("unexpected exception: " + e));
        }
    }

    public void testGetInstanceStringCertStoreParametersProvider() {
        try {
            CertStoreParameters parameters = new CertStore2Test.MyCertStoreParameters();
            CertStore certStore = CertStore.getInstance(CertStore2Test.CERT_STORE_NAME, parameters, provider);
            TestCase.assertNotNull(certStore);
            TestCase.assertNotNull(certStore.getCertStoreParameters());
            TestCase.assertNotSame(parameters, certStore.getCertStoreParameters());
            TestCase.assertSame(provider, certStore.getProvider());
        } catch (NoSuchAlgorithmException e) {
            TestCase.fail(("unexpected exception: " + e));
        } catch (InvalidAlgorithmParameterException e) {
            TestCase.fail(("unexpected exception: " + e));
        }
        try {
            CertStore certStore = CertStore.getInstance(CertStore2Test.CERT_STORE_NAME, null, provider);
            TestCase.assertNotNull(certStore);
            TestCase.assertNull(certStore.getCertStoreParameters());
            TestCase.assertSame(provider, certStore.getProvider());
        } catch (NoSuchAlgorithmException e) {
            TestCase.fail(("unexpected exception: " + e));
        } catch (InvalidAlgorithmParameterException e) {
            TestCase.fail(("unexpected exception: " + e));
        }
        try {
            CertStore.getInstance("UnknownCertStore", null, provider);
            TestCase.fail("expected NoSuchAlgorithmException");
        } catch (NoSuchAlgorithmException e) {
            // ok
        } catch (InvalidAlgorithmParameterException e) {
            TestCase.fail(("unexpected exception: " + e));
        }
        try {
            CertStore.getInstance(CertStore2Test.CERT_STORE_NAME, new CertStore2Test.MyOtherCertStoreParameters(), provider);
            TestCase.fail("expected InvalidAlgorithmParameterException");
        } catch (NoSuchAlgorithmException e) {
            TestCase.fail(("unexpected exception: " + e));
        } catch (InvalidAlgorithmParameterException e) {
            // ok
        }
    }

    public void testGetCertificates() {
        CertStore certStore = null;
        try {
            certStore = CertStore.getInstance(CertStore2Test.CERT_STORE_NAME, null);
        } catch (InvalidAlgorithmParameterException e) {
            TestCase.fail(("unexpected exception: " + e));
        } catch (NoSuchAlgorithmException e) {
            TestCase.fail(("unexpected exception: " + e));
        }
        TestCase.assertNotNull(certStore);
        try {
            Collection<? extends Certificate> certificates = certStore.getCertificates(null);
            TestCase.assertNull(certificates);
        } catch (CertStoreException e) {
            TestCase.fail(("unexpected exception: " + e));
        }
        try {
            Collection<? extends Certificate> certificates = certStore.getCertificates(new CertStore2Test.MyCertSelector());
            TestCase.assertNotNull(certificates);
            TestCase.assertTrue(certificates.isEmpty());
        } catch (CertStoreException e) {
            TestCase.fail(("unexpected exception: " + e));
        }
        try {
            certStore.getCertificates(new CertStore2Test.MyOtherCertSelector());
            TestCase.fail("expected CertStoreException");
        } catch (CertStoreException e) {
            // ok
        }
    }

    public void testGetCRLs() {
        CertStore certStore = null;
        try {
            certStore = CertStore.getInstance(CertStore2Test.CERT_STORE_NAME, new CertStore2Test.MyCertStoreParameters());
        } catch (InvalidAlgorithmParameterException e) {
            TestCase.fail(("unexpected exception: " + e));
        } catch (NoSuchAlgorithmException e) {
            TestCase.fail(("unexpected exception: " + e));
        }
        TestCase.assertNotNull(certStore);
        try {
            Collection<? extends CRL> ls = certStore.getCRLs(null);
            TestCase.assertNull(ls);
        } catch (CertStoreException e) {
            TestCase.fail(("unexpected exception: " + e));
        }
        try {
            Collection<? extends CRL> ls = certStore.getCRLs(new CertStore2Test.MyCRLSelector());
            TestCase.assertNotNull(ls);
            TestCase.assertTrue(ls.isEmpty());
        } catch (CertStoreException e) {
            TestCase.fail(("unexpected exception: " + e));
        }
        try {
            certStore.getCRLs(new CertStore2Test.MyOtherCRLSelector());
            TestCase.fail("expected CertStoreException");
        } catch (CertStoreException e) {
            // ok
        }
    }

    static class MyCertStoreProvider extends Provider {
        protected MyCertStoreProvider() {
            super(CertStore2Test.CERT_STORE_PROVIDER_NAME, 1.0, "Test CertStore Provider 1.0");
            put(("CertStore." + (CertStore2Test.CERT_STORE_NAME)), CertStore2Test.MyCertStoreSpi.class.getName());
        }
    }

    static class MyCertStoreParameters implements CertStoreParameters {
        public Object clone() {
            return new CertStore2Test.MyCertStoreParameters();
        }
    }

    static class MyOtherCertStoreParameters implements CertStoreParameters {
        public Object clone() {
            return new CertStore2Test.MyCertStoreParameters();
        }
    }

    static class MyCRLSelector implements CRLSelector {
        public boolean match(CRL crl) {
            return false;
        }

        public Object clone() {
            return new CertStore2Test.MyCRLSelector();
        }
    }

    static class MyOtherCRLSelector implements CRLSelector {
        public boolean match(CRL crl) {
            return false;
        }

        public Object clone() {
            return new CertStore2Test.MyOtherCRLSelector();
        }
    }

    static class MyCertSelector implements CertSelector {
        public boolean match(Certificate cert) {
            return false;
        }

        public Object clone() {
            return new CertStore2Test.MyCertSelector();
        }
    }

    static class MyOtherCertSelector implements CertSelector {
        public boolean match(Certificate crl) {
            return false;
        }

        public Object clone() {
            return new CertStore2Test.MyOtherCRLSelector();
        }
    }

    public static class MyCertStoreSpi extends CertStoreSpi {
        public MyCertStoreSpi() throws InvalidAlgorithmParameterException {
            super(null);
        }

        public MyCertStoreSpi(CertStoreParameters params) throws InvalidAlgorithmParameterException {
            super(params);
            if ((params != null) && (!(params instanceof CertStore2Test.MyCertStoreParameters))) {
                throw new InvalidAlgorithmParameterException("invalid parameters");
            }
        }

        @Override
        public Collection<? extends CRL> engineGetCRLs(CRLSelector selector) throws CertStoreException {
            if (selector != null) {
                if (!(selector instanceof CertStore2Test.MyCRLSelector)) {
                    throw new CertStoreException();
                }
                return new ArrayList<CRL>();
            }
            return null;
        }

        @Override
        public Collection<? extends Certificate> engineGetCertificates(CertSelector selector) throws CertStoreException {
            if (selector != null) {
                if (!(selector instanceof CertStore2Test.MyCertSelector)) {
                    throw new CertStoreException();
                }
                return new ArrayList<Certificate>();
            }
            return null;
        }
    }
}

