package org.pac4j.sql.profile.service;


import AbstractProfileService.LINKEDID;
import AbstractProfileService.SERIALIZED_PROFILE;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.junit.Assert;
import org.junit.Test;
import org.pac4j.core.credentials.UsernamePasswordCredentials;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;
import org.pac4j.sql.profile.DbProfile;
import org.pac4j.sql.test.tools.DbServer;


/**
 * Tests the {@link DbProfileService}.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public final class DbProfileServiceTests implements TestsConstants {
    private static final int DB_ID = 100000000;

    private static final String DB_LINKED_ID = "dbLinkedId";

    private static final String DB_PASS = "dbPass";

    private static final String DB_USER = "dbUser";

    private static final String DB_USER2 = "dbUser2";

    private DataSource ds = DbServer.getInstance();

    @Test
    public void testNullPasswordEncoder() {
        final DbProfileService dbProfileService = new DbProfileService(ds, FIRSTNAME);
        TestsHelper.expectException(() -> dbProfileService.validate(null, null), TechnicalException.class, "passwordEncoder cannot be null");
    }

    @Test
    public void testNullDataSource() {
        final DbProfileService dbProfileService = new DbProfileService(null, FIRSTNAME);
        dbProfileService.setPasswordEncoder(DbServer.PASSWORD_ENCODER);
        TestsHelper.expectException(() -> dbProfileService.validate(null, null), TechnicalException.class, "dataSource cannot be null");
    }

    @Test
    public void testGoodUsernameAttribute() {
        final UsernamePasswordCredentials credentials = login(GOOD_USERNAME, PASSWORD, FIRSTNAME);
        final CommonProfile profile = credentials.getUserProfile();
        Assert.assertNotNull(profile);
        Assert.assertTrue((profile instanceof DbProfile));
        final DbProfile dbProfile = ((DbProfile) (profile));
        Assert.assertEquals(GOOD_USERNAME, dbProfile.getId());
        Assert.assertEquals(FIRSTNAME_VALUE, dbProfile.getAttribute(FIRSTNAME));
    }

    @Test
    public void testGoodUsernameNoAttribute() {
        final UsernamePasswordCredentials credentials = login(GOOD_USERNAME, PASSWORD, "");
        final CommonProfile profile = credentials.getUserProfile();
        Assert.assertNotNull(profile);
        Assert.assertTrue((profile instanceof DbProfile));
        final DbProfile dbProfile = ((DbProfile) (profile));
        Assert.assertEquals(GOOD_USERNAME, dbProfile.getId());
        Assert.assertNull(dbProfile.getAttribute(FIRSTNAME));
    }

    @Test
    public void testMultipleUsername() {
        TestsHelper.expectException(() -> login(MULTIPLE_USERNAME, PASSWORD, ""), MultipleAccountsFoundException.class, "Too many accounts found for: misagh");
    }

    @Test
    public void testBadUsername() {
        TestsHelper.expectException(() -> login(BAD_USERNAME, PASSWORD, ""), AccountNotFoundException.class, "No account found for: michael");
    }

    @Test
    public void testBadPassword() {
        TestsHelper.expectException(() -> login(GOOD_USERNAME, (PASSWORD + "bad"), ""), BadCredentialsException.class, "Bad credentials for: jle");
    }

    @Test
    public void testCreateUpdateFindDelete() {
        final DbProfile profile = new DbProfile();
        profile.setId(("" + (DbProfileServiceTests.DB_ID)));
        profile.setLinkedId(DbProfileServiceTests.DB_LINKED_ID);
        profile.addAttribute(USERNAME, DbProfileServiceTests.DB_USER);
        final DbProfileService dbProfileService = new DbProfileService(ds);
        dbProfileService.setPasswordEncoder(DbServer.PASSWORD_ENCODER);
        // create
        dbProfileService.create(profile, DbProfileServiceTests.DB_PASS);
        // check credentials
        final UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(DbProfileServiceTests.DB_USER, DbProfileServiceTests.DB_PASS);
        dbProfileService.validate(credentials, null);
        final CommonProfile profile1 = credentials.getUserProfile();
        Assert.assertNotNull(profile1);
        // check data
        final List<Map<String, Object>> results = getData(DbProfileServiceTests.DB_ID);
        Assert.assertEquals(1, results.size());
        final Map<String, Object> result = results.get(0);
        Assert.assertEquals(5, result.size());
        Assert.assertEquals(DbProfileServiceTests.DB_ID, result.get(ID));
        Assert.assertEquals(DbProfileServiceTests.DB_LINKED_ID, result.get(LINKEDID));
        Assert.assertNotNull(result.get(SERIALIZED_PROFILE));
        Assert.assertTrue(DbServer.PASSWORD_ENCODER.matches(DbProfileServiceTests.DB_PASS, ((String) (result.get(PASSWORD)))));
        Assert.assertEquals(DbProfileServiceTests.DB_USER, result.get(USERNAME));
        // findById
        final DbProfile profile2 = dbProfileService.findById(("" + (DbProfileServiceTests.DB_ID)));
        Assert.assertEquals(("" + (DbProfileServiceTests.DB_ID)), profile2.getId());
        Assert.assertEquals(DbProfileServiceTests.DB_LINKED_ID, profile2.getLinkedId());
        Assert.assertEquals(DbProfileServiceTests.DB_USER, profile2.getUsername());
        Assert.assertEquals(1, profile2.getAttributes().size());
        // update
        profile.addAttribute(USERNAME, DbProfileServiceTests.DB_USER2);
        dbProfileService.update(profile, null);
        final List<Map<String, Object>> results2 = getData(DbProfileServiceTests.DB_ID);
        Assert.assertEquals(1, results2.size());
        final Map<String, Object> result2 = results2.get(0);
        Assert.assertEquals(5, result2.size());
        Assert.assertEquals(DbProfileServiceTests.DB_ID, result2.get(ID));
        Assert.assertEquals(DbProfileServiceTests.DB_LINKED_ID, result2.get(LINKEDID));
        Assert.assertNotNull(result2.get(SERIALIZED_PROFILE));
        Assert.assertTrue(DbServer.PASSWORD_ENCODER.matches(DbProfileServiceTests.DB_PASS, ((String) (result2.get(PASSWORD)))));
        Assert.assertEquals(DbProfileServiceTests.DB_USER2, result2.get(USERNAME));
        // remove
        dbProfileService.remove(profile);
        final List<Map<String, Object>> results3 = getData(DbProfileServiceTests.DB_ID);
        Assert.assertEquals(0, results3.size());
    }
}

