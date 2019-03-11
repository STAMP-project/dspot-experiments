package org.zeromq;


import java.io.File;
import java.io.IOException;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;


// FIXME tests are passing on local machine but not on Travis
@Ignore
public class ZCertStoreTest {
    private ZCertStore certStore;

    private static final String CERTSTORE_LOCATION = "target/testCurveCerts";

    @Test
    public void testAddCertificates() throws IOException {
        final int beforeAmount = certStore.getCertificatesCount();
        Assert.assertThat(beforeAmount, CoreMatchers.is(0));
        ZCert c1 = new ZCert();
        File f = c1.savePublic(((ZCertStoreTest.CERTSTORE_LOCATION) + "/c1.cert"));
        Assert.assertThat(f.exists(), CoreMatchers.is(true));
        // check the store if something changed, and if yes reload all
        boolean rc = certStore.reloadIfNecessary();
        Assert.assertThat(rc, CoreMatchers.is(true));
        // is now one certificate more in the store?
        Assert.assertThat(certStore.getCertificatesCount(), CoreMatchers.is(1));
        // check if we find our publickey using the Z85-Version to lookup
        Assert.assertThat(certStore.containsPublicKey(c1.getPublicKeyAsZ85()), CoreMatchers.is(true));
        // check if we find our publickey using the binary-Version to lookup (this will internally be encoded to z85 for the lookup)
        Assert.assertThat(certStore.containsPublicKey(c1.getPublicKey()), CoreMatchers.is(true));
        // check if we do not find some random lookup-key. Z85-Keys need to have a length of 40 bytes.
        Assert.assertThat(certStore.containsPublicKey("1234567890123456789012345678901234567890"), CoreMatchers.is(false));
        zmq.ZMQ.msleep(1000);
        // check certs in sub-directories
        ZCert c2 = new ZCert();
        f = c2.savePublic(((ZCertStoreTest.CERTSTORE_LOCATION) + "/sub/c2.cert"));
        Assert.assertThat(f.exists(), CoreMatchers.is(true));
        Assert.assertThat(certStore.getCertificatesCount(), CoreMatchers.is(2));
    }

    @Test
    public void testRemoveCertificates() throws IOException {
        int beforeAmount = certStore.getCertificatesCount();
        Assert.assertThat(beforeAmount, CoreMatchers.is(0));
        ZCert c1 = new ZCert();
        File f = c1.savePublic(((ZCertStoreTest.CERTSTORE_LOCATION) + "/c1.cert"));
        Assert.assertThat(f.exists(), CoreMatchers.is(true));
        // check the store if something changed, and if yes reload all
        boolean rc = certStore.reloadIfNecessary();
        Assert.assertThat(rc, CoreMatchers.is(true));
        // is now one certificate more in the store?
        Assert.assertThat(certStore.getCertificatesCount(), CoreMatchers.is(1));
        File certificate = new File(((ZCertStoreTest.CERTSTORE_LOCATION) + "/c1.cert"));
        rc = certificate.exists();
        Assert.assertThat(rc, CoreMatchers.is(true));
        rc = certificate.delete();
        Assert.assertThat(rc, CoreMatchers.is(true));
        rc = certStore.reloadIfNecessary();
        Assert.assertThat(rc, CoreMatchers.is(true));
        Assert.assertThat(certStore.getCertificatesCount(), CoreMatchers.is(0));
        // check if we find our publickey using the Z85-Version to lookup
        Assert.assertThat(certStore.containsPublicKey(c1.getPublicKeyAsZ85()), CoreMatchers.is(false));
        // check if we find our publickey using the binary-Version to lookup (this will internally be encoded to z85 for the lookup)
        Assert.assertThat(certStore.containsPublicKey(c1.getPublicKey()), CoreMatchers.is(false));
    }

    @Test
    public void testcheckForCertificateChanges() throws IOException {
        Assert.assertThat(certStore.getCertificatesCount(), CoreMatchers.is(0));
        ZCert cert1 = new ZCert();
        File f = cert1.savePublic(((ZCertStoreTest.CERTSTORE_LOCATION) + "/c1.cert"));
        Assert.assertThat(f.exists(), CoreMatchers.is(true));
        ZCert cert2 = new ZCert();
        f = cert2.saveSecret(((ZCertStoreTest.CERTSTORE_LOCATION) + "/sub/c2.cert"));
        Assert.assertThat(f.exists(), CoreMatchers.is(true));
        Assert.assertThat(certStore.getCertificatesCount(), CoreMatchers.is(2));
        Assert.assertThat(certStore.checkForChanges(), CoreMatchers.is(false));
        zmq.ZMQ.msleep(1000);
        // rewrite certificates and see if this change gets recognized
        ZCert othercert1 = new ZCert();
        f = othercert1.savePublic(((ZCertStoreTest.CERTSTORE_LOCATION) + "/c1.cert"));
        Assert.assertThat(f.exists(), CoreMatchers.is(true));
        // change is recognized if a file is changed only in the main-folder
        Assert.assertThat(certStore.checkForChanges(), CoreMatchers.is(true));
        // second call shall indicate change
        Assert.assertThat(certStore.checkForChanges(), CoreMatchers.is(true));
        // reload the certificates
        Assert.assertThat(certStore.getCertificatesCount(), CoreMatchers.is(2));
        Assert.assertThat(certStore.checkForChanges(), CoreMatchers.is(false));
        Assert.assertThat(certStore.containsPublicKey(cert1.getPublicKeyAsZ85()), CoreMatchers.is(false));
        Assert.assertThat(certStore.containsPublicKey(cert1.getPublicKey()), CoreMatchers.is(false));
        Assert.assertThat(certStore.containsPublicKey(othercert1.getPublicKeyAsZ85()), CoreMatchers.is(true));
        Assert.assertThat(certStore.containsPublicKey(othercert1.getPublicKey()), CoreMatchers.is(true));
        zmq.ZMQ.msleep(1000);
        // check if changes in subfolders get recognized
        f = cert2.savePublic(((ZCertStoreTest.CERTSTORE_LOCATION) + "/sub/c2.cert"));
        Assert.assertThat(f.exists(), CoreMatchers.is(true));
        Assert.assertThat(certStore.checkForChanges(), CoreMatchers.is(true));
        // second call shall indicate change
        Assert.assertThat(certStore.checkForChanges(), CoreMatchers.is(true));
        // reload the certificates
        Assert.assertThat(certStore.getCertificatesCount(), CoreMatchers.is(2));
        Assert.assertThat(certStore.checkForChanges(), CoreMatchers.is(false));
    }
}

