package org.pac4j.ldap.profile.service;


import AbstractProfileService.LINKEDID;
import AbstractProfileService.SERIALIZED_PROFILE;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.ldaptive.ConnectionFactory;
import org.ldaptive.auth.Authenticator;
import org.pac4j.core.credentials.UsernamePasswordCredentials;
import org.pac4j.core.exception.BadCredentialsException;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;
import org.pac4j.ldap.profile.LdapProfile;
import org.pac4j.ldap.test.tools.LdapServer;


/**
 * Tests the {@link LdapProfileService}.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public final class LdapProfileServiceTests implements TestsConstants {
    private static final String LDAP_ID = "ldapid";

    private static final String LDAP_LINKED_ID = "ldapLinkedId";

    private static final String LDAP_PASS = "ldapPass";

    private static final String LDAP_PASS2 = "ldapPass2";

    private static final String LDAP_USER = "ldapUser";

    private static final String LDAP_USER2 = "ldapUser2";

    private LdapServer ldapServer;

    private Authenticator authenticator;

    private ConnectionFactory connectionFactory;

    @Test
    public void testNullAuthenticator() {
        final LdapProfileService ldapProfileService = new LdapProfileService(connectionFactory, null, LdapServer.BASE_PEOPLE_DN);
        TestsHelper.expectException(() -> ldapProfileService.init(), TechnicalException.class, "ldapAuthenticator cannot be null");
    }

    @Test
    public void testNullConnectionFactory() {
        final LdapProfileService ldapProfileService = new LdapProfileService(null, authenticator, LdapServer.BASE_PEOPLE_DN);
        TestsHelper.expectException(() -> ldapProfileService.init(), TechnicalException.class, "connectionFactory cannot be null");
    }

    @Test
    public void testBlankUsersDn() {
        final LdapProfileService ldapProfileService = new LdapProfileService(connectionFactory, authenticator, "");
        TestsHelper.expectException(() -> ldapProfileService.init(), TechnicalException.class, "usersDn cannot be blank");
    }

    @Test(expected = BadCredentialsException.class)
    public void authentFailed() {
        final LdapProfileService ldapProfileService = new LdapProfileService(connectionFactory, authenticator, LdapServer.BASE_PEOPLE_DN);
        final UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(BAD_USERNAME, PASSWORD);
        ldapProfileService.validate(credentials, null);
    }

    @Test
    public void authentSuccessNoAttribute() {
        final LdapProfileService ldapProfileService = new LdapProfileService(connectionFactory, authenticator, "", LdapServer.BASE_PEOPLE_DN);
        ldapProfileService.setUsernameAttribute(LdapServer.CN);
        final UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(GOOD_USERNAME, PASSWORD);
        ldapProfileService.validate(credentials, null);
        final CommonProfile profile = credentials.getUserProfile();
        Assert.assertNotNull(profile);
        Assert.assertTrue((profile instanceof LdapProfile));
        final LdapProfile ldapProfile = ((LdapProfile) (profile));
        Assert.assertEquals(GOOD_USERNAME, ldapProfile.getId());
        Assert.assertEquals(0, ldapProfile.getAttributes().size());
    }

    @Test
    public void authentSuccessSingleAttribute() {
        final LdapProfileService ldapProfileService = new LdapProfileService(connectionFactory, authenticator, LdapServer.SN, LdapServer.BASE_PEOPLE_DN);
        ldapProfileService.setUsernameAttribute(LdapServer.CN);
        final UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(GOOD_USERNAME, PASSWORD);
        ldapProfileService.validate(credentials, null);
        final CommonProfile profile = credentials.getUserProfile();
        Assert.assertNotNull(profile);
        Assert.assertTrue((profile instanceof LdapProfile));
        final LdapProfile ldapProfile = ((LdapProfile) (profile));
        Assert.assertEquals(GOOD_USERNAME, ldapProfile.getId());
        Assert.assertEquals(1, ldapProfile.getAttributes().size());
        Assert.assertEquals(FIRSTNAME_VALUE, ldapProfile.getAttribute(LdapServer.SN));
    }

    @Test
    public void authentSuccessMultiAttribute() {
        final LdapProfileService ldapProfileService = new LdapProfileService(connectionFactory, authenticator, (((LdapServer.SN) + ",") + (LdapServer.ROLE)), LdapServer.BASE_PEOPLE_DN);
        ldapProfileService.setUsernameAttribute(LdapServer.CN);
        final UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(GOOD_USERNAME2, PASSWORD);
        ldapProfileService.validate(credentials, null);
        final CommonProfile profile = credentials.getUserProfile();
        Assert.assertNotNull(profile);
        Assert.assertTrue((profile instanceof LdapProfile));
        final LdapProfile ldapProfile = ((LdapProfile) (profile));
        Assert.assertEquals(GOOD_USERNAME2, ldapProfile.getId());
        Assert.assertEquals(1, ldapProfile.getAttributes().size());
        Assert.assertNull(ldapProfile.getAttribute(LdapServer.SN));
        final Collection<String> attributes = ((Collection<String>) (ldapProfile.getAttribute(LdapServer.ROLE)));
        Assert.assertEquals(2, attributes.size());
        Assert.assertTrue(attributes.contains(LdapServer.ROLE1));
        Assert.assertTrue(attributes.contains(LdapServer.ROLE2));
    }

    @Test
    public void testCreateUpdateFindDelete() {
        final LdapProfile profile = new LdapProfile();
        profile.setId(LdapProfileServiceTests.LDAP_ID);
        profile.setLinkedId(LdapProfileServiceTests.LDAP_LINKED_ID);
        profile.addAttribute(USERNAME, LdapProfileServiceTests.LDAP_USER);
        final LdapProfileService ldapProfileService = new LdapProfileService(connectionFactory, authenticator, LdapServer.BASE_PEOPLE_DN);
        ldapProfileService.setIdAttribute(LdapServer.CN);
        ldapProfileService.setUsernameAttribute(LdapServer.SN);
        ldapProfileService.setPasswordAttribute("userPassword");
        // create
        ldapProfileService.create(profile, LdapProfileServiceTests.LDAP_PASS);
        // check credentials
        final UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(LdapProfileServiceTests.LDAP_ID, LdapProfileServiceTests.LDAP_PASS);
        ldapProfileService.validate(credentials, null);
        final CommonProfile profile1 = credentials.getUserProfile();
        Assert.assertNotNull(profile1);
        // check data
        final List<Map<String, Object>> results = getData(ldapProfileService, LdapProfileServiceTests.LDAP_ID);
        Assert.assertEquals(1, results.size());
        final Map<String, Object> result = results.get(0);
        Assert.assertEquals(4, result.size());
        Assert.assertEquals(LdapProfileServiceTests.LDAP_ID, result.get(LdapServer.CN));
        Assert.assertEquals(LdapProfileServiceTests.LDAP_LINKED_ID, result.get(LINKEDID));
        Assert.assertNotNull(result.get(SERIALIZED_PROFILE));
        Assert.assertEquals(LdapProfileServiceTests.LDAP_USER, result.get(LdapServer.SN));
        // findById
        final LdapProfile profile2 = ldapProfileService.findById(LdapProfileServiceTests.LDAP_ID);
        Assert.assertEquals(LdapProfileServiceTests.LDAP_ID, profile2.getId());
        Assert.assertEquals(LdapProfileServiceTests.LDAP_LINKED_ID, profile2.getLinkedId());
        Assert.assertEquals(LdapProfileServiceTests.LDAP_USER, profile2.getUsername());
        Assert.assertEquals(1, profile2.getAttributes().size());
        // update
        profile.addAttribute(USERNAME, LdapProfileServiceTests.LDAP_USER2);
        ldapProfileService.update(profile, LdapProfileServiceTests.LDAP_PASS2);
        final List<Map<String, Object>> results2 = getData(ldapProfileService, LdapProfileServiceTests.LDAP_ID);
        Assert.assertEquals(1, results2.size());
        final Map<String, Object> result2 = results2.get(0);
        Assert.assertEquals(4, result2.size());
        Assert.assertEquals(LdapProfileServiceTests.LDAP_ID, result2.get(LdapServer.CN));
        Assert.assertEquals(LdapProfileServiceTests.LDAP_LINKED_ID, result2.get(LINKEDID));
        Assert.assertNotNull(result2.get(SERIALIZED_PROFILE));
        Assert.assertEquals(LdapProfileServiceTests.LDAP_USER2, result2.get(LdapServer.SN));
        // check credentials
        final UsernamePasswordCredentials credentials2 = new UsernamePasswordCredentials(LdapProfileServiceTests.LDAP_ID, LdapProfileServiceTests.LDAP_PASS2);
        ldapProfileService.validate(credentials2, null);
        final CommonProfile profile3 = credentials.getUserProfile();
        Assert.assertNotNull(profile3);
        // remove
        ldapProfileService.remove(profile);
        final List<Map<String, Object>> results3 = getData(ldapProfileService, LdapProfileServiceTests.LDAP_ID);
        Assert.assertEquals(0, results3.size());
    }
}

