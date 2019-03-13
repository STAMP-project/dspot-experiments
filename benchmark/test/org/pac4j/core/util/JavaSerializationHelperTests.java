package org.pac4j.core.util;


import org.apache.shiro.subject.SimplePrincipalCollection;
import org.junit.Assert;
import org.junit.Test;
import org.pac4j.core.profile.CommonProfile;


/**
 * Tests {@link JavaSerializationHelper}.
 *
 * @author Jerome Leleu
 * @since 1.8.1
 */
public final class JavaSerializationHelperTests implements TestsConstants {
    private JavaSerializationHelper helper = new JavaSerializationHelper();

    @Test
    public void testBytesSerialization() {
        final CommonProfile profile = getUserProfile();
        final byte[] serialized = helper.serializeToBytes(profile);
        final CommonProfile profile2 = ((CommonProfile) (helper.deserializeFromBytes(serialized)));
        Assert.assertEquals(profile.getId(), profile2.getId());
        Assert.assertEquals(profile.getAttribute(TestsConstants.NAME), profile2.getAttribute(TestsConstants.NAME));
    }

    @Test
    public void testBytesSerializationUnsecure() {
        JavaSerializationHelper h = new JavaSerializationHelper();
        h.clearTrustedClasses();
        h.clearTrustedPackages();
        final CommonProfile profile = getUserProfile();
        final byte[] serialized = h.serializeToBytes(profile);
        Assert.assertNull(h.deserializeFromBytes(serialized));
    }

    @Test
    public void testBytesSerializationTrustedClass() {
        JavaSerializationHelper h = new JavaSerializationHelper();
        h.clearTrustedPackages();
        h.clearTrustedClasses();
        h.addTrustedClass(SimplePrincipalCollection.class);
        final SimplePrincipalCollection spc = new SimplePrincipalCollection();
        final byte[] serialized = h.serializeToBytes(spc);
        Assert.assertEquals(spc, h.deserializeFromBytes(serialized));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testTrustedPackagesModification() {
        JavaSerializationHelper h = new JavaSerializationHelper();
        h.getTrustedPackages().add("org.apache");
    }

    @Test
    public void testBytesSerializationTrustedPackage() {
        JavaSerializationHelper h = new JavaSerializationHelper();
        h.addTrustedPackage("org.apache");
        final SimplePrincipalCollection spc = new SimplePrincipalCollection();
        final byte[] serialized = h.serializeToBytes(spc);
        Assert.assertNotNull(h.deserializeFromBytes(serialized));
    }

    @Test
    public void testBase64StringSerialization() {
        final CommonProfile profile = getUserProfile();
        final String serialized = helper.serializeToBase64(profile);
        final CommonProfile profile2 = ((CommonProfile) (helper.deserializeFromBase64(serialized)));
        Assert.assertEquals(profile.getId(), profile2.getId());
        Assert.assertEquals(profile.getAttribute(TestsConstants.NAME), profile2.getAttribute(TestsConstants.NAME));
    }
}

