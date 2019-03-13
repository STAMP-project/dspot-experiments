package org.pac4j.couch.profile.service;


import AbstractProfileService.LINKEDID;
import AbstractProfileService.SERIALIZED_PROFILE;
import java.util.List;
import java.util.Map;
import org.apache.shiro.authc.credential.DefaultPasswordService;
import org.ektorp.CouchDbConnector;
import org.junit.Assert;
import org.junit.Test;
import org.pac4j.core.credentials.UsernamePasswordCredentials;
import org.pac4j.core.credentials.password.PasswordEncoder;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;
import org.pac4j.couch.profile.CouchProfile;
import org.pac4j.couch.test.tools.CouchServer;

import static CouchProfileService.COUCH_ID;


/**
 * Tests the {@link CouchProfileService}.
 *
 * @author Elie Roux
 * @since 2.0.0
 */
public final class CouchProfileServiceTests implements TestsConstants {
    private static final String COUCH_ID_FIELD = COUCH_ID;

    private static final String COUCH_ID = "couchId";

    private static final String COUCH_LINKED_ID = "couchLinkedId";

    private static final String COUCH_USER = "couchUser";

    private static final String COUCH_USER2 = "couchUser2";

    private static final String COUCH_PASS = "couchPass";

    private static final String COUCH_PASS2 = "couchPass2";

    private static final String IDPERSON1 = "idperson1";

    private static final String IDPERSON2 = "idperson2";

    private static final String IDPERSON3 = "idperson3";

    public static final PasswordEncoder PASSWORD_ENCODER = new org.pac4j.core.credentials.password.ShiroPasswordEncoder(new DefaultPasswordService());

    private static final CouchServer couchServer = new CouchServer();

    private static final CouchDbConnector couchDbConnector = CouchProfileServiceTests.couchServer.start();

    @Test
    public void testNullConnector() {
        final CouchProfileService couchProfileService = new CouchProfileService(null);
        couchProfileService.setPasswordEncoder(CouchProfileServiceTests.PASSWORD_ENCODER);
        TestsHelper.expectException(() -> couchProfileService.init(), TechnicalException.class, "couchDbConnector cannot be null");
    }

    @Test(expected = AccountNotFoundException.class)
    public void authentFailed() {
        final CouchProfileService couchProfileService = new CouchProfileService(CouchProfileServiceTests.couchDbConnector);
        couchProfileService.setPasswordEncoder(CouchProfileServiceTests.PASSWORD_ENCODER);
        final UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(BAD_USERNAME, PASSWORD);
        couchProfileService.validate(credentials, null);
    }

    @Test
    public void authentSuccessSingleAttribute() {
        final CouchProfileService couchProfileService = new CouchProfileService(CouchProfileServiceTests.couchDbConnector);
        couchProfileService.setPasswordEncoder(CouchProfileServiceTests.PASSWORD_ENCODER);
        final UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(GOOD_USERNAME, PASSWORD);
        couchProfileService.validate(credentials, null);
        final CommonProfile profile = credentials.getUserProfile();
        Assert.assertNotNull(profile);
        Assert.assertTrue((profile instanceof CouchProfile));
        final CouchProfile couchProfile = ((CouchProfile) (profile));
        Assert.assertEquals(GOOD_USERNAME, couchProfile.getUsername());
        Assert.assertEquals(2, couchProfile.getAttributes().size());
        Assert.assertEquals(FIRSTNAME_VALUE, couchProfile.getAttribute(FIRSTNAME));
    }

    @Test
    public void testCreateUpdateFindDelete() {
        final CouchProfile profile = new CouchProfile();
        profile.setId(CouchProfileServiceTests.COUCH_ID);
        profile.setLinkedId(CouchProfileServiceTests.COUCH_LINKED_ID);
        profile.addAttribute(USERNAME, CouchProfileServiceTests.COUCH_USER);
        final CouchProfileService couchProfileService = new CouchProfileService(CouchProfileServiceTests.couchDbConnector);
        couchProfileService.setPasswordEncoder(CouchProfileServiceTests.PASSWORD_ENCODER);
        // create
        couchProfileService.create(profile, CouchProfileServiceTests.COUCH_PASS);
        // check credentials
        final UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(CouchProfileServiceTests.COUCH_USER, CouchProfileServiceTests.COUCH_PASS);
        couchProfileService.validate(credentials, null);
        final CommonProfile profile1 = credentials.getUserProfile();
        Assert.assertNotNull(profile1);
        // check data
        final List<Map<String, Object>> results = getData(couchProfileService, CouchProfileServiceTests.COUCH_ID);
        Assert.assertEquals(1, results.size());
        final Map<String, Object> result = results.get(0);
        Assert.assertEquals(5, result.size());
        Assert.assertEquals(CouchProfileServiceTests.COUCH_ID, result.get(CouchProfileServiceTests.COUCH_ID_FIELD));
        Assert.assertEquals(CouchProfileServiceTests.COUCH_LINKED_ID, result.get(LINKEDID));
        Assert.assertNotNull(result.get(SERIALIZED_PROFILE));
        Assert.assertEquals(CouchProfileServiceTests.COUCH_USER, result.get(USERNAME));
        // findById
        final CouchProfile profile2 = couchProfileService.findById(CouchProfileServiceTests.COUCH_ID);
        Assert.assertEquals(CouchProfileServiceTests.COUCH_ID, profile2.getId());
        Assert.assertEquals(CouchProfileServiceTests.COUCH_LINKED_ID, profile2.getLinkedId());
        Assert.assertEquals(CouchProfileServiceTests.COUCH_USER, profile2.getUsername());
        Assert.assertEquals(1, profile2.getAttributes().size());
        // update with password
        profile.addAttribute(USERNAME, CouchProfileServiceTests.COUCH_USER2);
        couchProfileService.update(profile, CouchProfileServiceTests.COUCH_PASS2);
        List<Map<String, Object>> results2 = getData(couchProfileService, CouchProfileServiceTests.COUCH_ID);
        Assert.assertEquals(1, results2.size());
        Map<String, Object> result2 = results2.get(0);
        Assert.assertEquals(5, result2.size());
        Assert.assertEquals(CouchProfileServiceTests.COUCH_ID, result2.get(CouchProfileServiceTests.COUCH_ID_FIELD));
        Assert.assertEquals(CouchProfileServiceTests.COUCH_LINKED_ID, result2.get(LINKEDID));
        Assert.assertNotNull(result2.get(SERIALIZED_PROFILE));
        Assert.assertEquals(CouchProfileServiceTests.COUCH_USER2, result2.get(USERNAME));
        // check credentials
        final UsernamePasswordCredentials credentials2 = new UsernamePasswordCredentials(CouchProfileServiceTests.COUCH_USER2, CouchProfileServiceTests.COUCH_PASS2);
        couchProfileService.validate(credentials2, null);
        CommonProfile profile3 = credentials.getUserProfile();
        Assert.assertNotNull(profile3);
        // update with no password update
        couchProfileService.update(profile, null);
        results2 = getData(couchProfileService, CouchProfileServiceTests.COUCH_ID);
        Assert.assertEquals(1, results2.size());
        result2 = results2.get(0);
        Assert.assertEquals(5, result2.size());
        Assert.assertEquals(CouchProfileServiceTests.COUCH_USER2, result2.get(USERNAME));
        // check credentials
        couchProfileService.validate(credentials2, null);
        profile3 = credentials.getUserProfile();
        Assert.assertNotNull(profile3);
        // remove
        couchProfileService.remove(profile);
        final List<Map<String, Object>> results3 = getData(couchProfileService, CouchProfileServiceTests.COUCH_ID);
        Assert.assertEquals(0, results3.size());
    }
}

