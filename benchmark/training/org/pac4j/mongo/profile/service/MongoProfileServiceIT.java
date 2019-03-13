package org.pac4j.mongo.profile.service;


import AbstractProfileService.LINKEDID;
import AbstractProfileService.SERIALIZED_PROFILE;
import java.util.List;
import java.util.Map;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Test;
import org.pac4j.core.credentials.UsernamePasswordCredentials;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;
import org.pac4j.mongo.profile.MongoProfile;
import org.pac4j.mongo.test.tools.MongoServer;


/**
 * Tests the {@link MongoProfileService}.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public final class MongoProfileServiceIT implements TestsConstants {
    private static final int PORT = 37017;

    private static final String MONGO_ID = "mongoId";

    private static final String MONGO_LINKEDID = "mongoLinkedId";

    private static final String MONGO_LINKEDID2 = "mongoLinkedId2";

    private static final String MONGO_USER = "mongoUser";

    private static final String MONGO_PASS = "mongoPass";

    private static final String MONGO_PASS2 = "mongoPass2";

    private final MongoServer mongoServer = new MongoServer();

    @Test
    public void testNullPasswordEncoder() {
        final MongoProfileService authenticator = new MongoProfileService(getClient(), FIRSTNAME);
        authenticator.setPasswordEncoder(null);
        TestsHelper.expectException(() -> authenticator.init(), TechnicalException.class, "passwordEncoder cannot be null");
    }

    @Test
    public void testNullMongoClient() {
        final MongoProfileService authenticator = new MongoProfileService(null, FIRSTNAME, MongoServer.PASSWORD_ENCODER);
        TestsHelper.expectException(() -> authenticator.init(), TechnicalException.class, "mongoClient cannot be null");
    }

    @Test
    public void testNullDatabase() {
        final MongoProfileService authenticator = new MongoProfileService(getClient(), FIRSTNAME, MongoServer.PASSWORD_ENCODER);
        authenticator.setUsersDatabase(null);
        TestsHelper.expectException(() -> authenticator.init(), TechnicalException.class, "usersDatabase cannot be blank");
    }

    @Test
    public void testNullCollection() {
        final MongoProfileService authenticator = new MongoProfileService(getClient(), FIRSTNAME, MongoServer.PASSWORD_ENCODER);
        authenticator.setUsersCollection(null);
        TestsHelper.expectException(() -> authenticator.init(), TechnicalException.class, "usersCollection cannot be blank");
    }

    @Test
    public void testNullUsername() {
        final MongoProfileService authenticator = new MongoProfileService(getClient(), FIRSTNAME, MongoServer.PASSWORD_ENCODER);
        authenticator.setUsernameAttribute(null);
        TestsHelper.expectException(() -> authenticator.init(), TechnicalException.class, "usernameAttribute cannot be blank");
    }

    @Test
    public void testNullPassword() {
        final MongoProfileService authenticator = new MongoProfileService(getClient(), FIRSTNAME, MongoServer.PASSWORD_ENCODER);
        authenticator.setPasswordAttribute(null);
        final UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(GOOD_USERNAME, PASSWORD);
        TestsHelper.expectException(() -> authenticator.validate(credentials, null), TechnicalException.class, "passwordAttribute cannot be blank");
    }

    @Test
    public void testGoodUsernameAttribute() {
        final UsernamePasswordCredentials credentials = login(GOOD_USERNAME, PASSWORD, FIRSTNAME);
        final CommonProfile profile = credentials.getUserProfile();
        Assert.assertNotNull(profile);
        Assert.assertTrue((profile instanceof MongoProfile));
        final MongoProfile dbProfile = ((MongoProfile) (profile));
        Assert.assertEquals(GOOD_USERNAME, dbProfile.getId());
        Assert.assertEquals(FIRSTNAME_VALUE, dbProfile.getAttribute(FIRSTNAME));
    }

    @Test
    public void testGoodUsernameNoAttribute() {
        final UsernamePasswordCredentials credentials = login(GOOD_USERNAME, PASSWORD, "");
        final CommonProfile profile = credentials.getUserProfile();
        Assert.assertNotNull(profile);
        Assert.assertTrue((profile instanceof MongoProfile));
        final MongoProfile dbProfile = ((MongoProfile) (profile));
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
        final ObjectId objectId = new ObjectId();
        final MongoProfile profile = new MongoProfile();
        profile.setId(MongoProfileServiceIT.MONGO_ID);
        profile.setLinkedId(MongoProfileServiceIT.MONGO_LINKEDID);
        profile.addAttribute(USERNAME, MongoProfileServiceIT.MONGO_USER);
        profile.addAttribute(FIRSTNAME, objectId);
        final MongoProfileService mongoProfileService = new MongoProfileService(getClient());
        mongoProfileService.setPasswordEncoder(MongoServer.PASSWORD_ENCODER);
        // create
        mongoProfileService.create(profile, MongoProfileServiceIT.MONGO_PASS);
        // check credentials
        final UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(MongoProfileServiceIT.MONGO_USER, MongoProfileServiceIT.MONGO_PASS);
        mongoProfileService.validate(credentials, null);
        final CommonProfile profile1 = credentials.getUserProfile();
        Assert.assertNotNull(profile1);
        // check data
        final List<Map<String, Object>> results = getData(mongoProfileService, MongoProfileServiceIT.MONGO_ID);
        Assert.assertEquals(1, results.size());
        final Map<String, Object> result = results.get(0);
        Assert.assertEquals(6, result.size());
        Assert.assertEquals(MongoProfileServiceIT.MONGO_ID, result.get(ID));
        Assert.assertEquals(MongoProfileServiceIT.MONGO_LINKEDID, result.get(LINKEDID));
        Assert.assertNotNull(result.get(SERIALIZED_PROFILE));
        Assert.assertTrue(MongoServer.PASSWORD_ENCODER.matches(MongoProfileServiceIT.MONGO_PASS, ((String) (result.get(PASSWORD)))));
        Assert.assertEquals(MongoProfileServiceIT.MONGO_USER, result.get(USERNAME));
        // findById
        final MongoProfile profile2 = mongoProfileService.findByLinkedId(MongoProfileServiceIT.MONGO_LINKEDID);
        Assert.assertEquals(MongoProfileServiceIT.MONGO_ID, profile2.getId());
        Assert.assertEquals(MongoProfileServiceIT.MONGO_LINKEDID, profile2.getLinkedId());
        Assert.assertEquals(MongoProfileServiceIT.MONGO_USER, profile2.getUsername());
        Assert.assertEquals(objectId, profile2.getAttribute(FIRSTNAME));
        Assert.assertEquals(2, profile2.getAttributes().size());
        // update
        profile.setLinkedId(MongoProfileServiceIT.MONGO_LINKEDID2);
        mongoProfileService.update(profile, MongoProfileServiceIT.MONGO_PASS2);
        final List<Map<String, Object>> results2 = getData(mongoProfileService, MongoProfileServiceIT.MONGO_ID);
        Assert.assertEquals(1, results2.size());
        final Map<String, Object> result2 = results2.get(0);
        Assert.assertEquals(6, result2.size());
        Assert.assertEquals(MongoProfileServiceIT.MONGO_ID, result2.get(ID));
        Assert.assertEquals(MongoProfileServiceIT.MONGO_LINKEDID2, result2.get(LINKEDID));
        Assert.assertNotNull(result2.get(SERIALIZED_PROFILE));
        Assert.assertTrue(MongoServer.PASSWORD_ENCODER.matches(MongoProfileServiceIT.MONGO_PASS2, ((String) (result2.get(PASSWORD)))));
        Assert.assertEquals(MongoProfileServiceIT.MONGO_USER, result2.get(USERNAME));
        // remove
        mongoProfileService.remove(profile);
        final List<Map<String, Object>> results3 = getData(mongoProfileService, MongoProfileServiceIT.MONGO_ID);
        Assert.assertEquals(0, results3.size());
    }
}

