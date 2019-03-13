package org.pac4j.core.profile.service;


import AbstractProfileService.LINKEDID;
import AbstractProfileService.SERIALIZED_PROFILE;
import java.util.List;
import java.util.Map;
import org.apache.shiro.authc.credential.DefaultPasswordService;
import org.junit.Assert;
import org.junit.Test;
import org.pac4j.core.credentials.UsernamePasswordCredentials;
import org.pac4j.core.credentials.password.PasswordEncoder;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.util.TestsConstants;


/**
 * Tests the {@link InMemoryProfileService}.
 *
 * @author Elie Roux
 * @since 2.1.0
 */
public final class InMemoryProfileServiceTests implements TestsConstants {
    private static final String TEST_ID = "testId";

    private static final String TEST_LINKED_ID = "testLinkedId";

    private static final String TEST_USER = "testUser";

    private static final String TEST_USER2 = "testUser2";

    private static final String TEST_PASS = "testPass";

    private static final String TEST_PASS2 = "testPass2";

    private static final String IDPERSON1 = "idperson1";

    private static final String IDPERSON2 = "idperson2";

    private static final String IDPERSON3 = "idperson3";

    public static final PasswordEncoder PASSWORD_ENCODER = new org.pac4j.core.credentials.password.ShiroPasswordEncoder(new DefaultPasswordService());

    public InMemoryProfileService<CommonProfile> inMemoryProfileService;

    @Test(expected = AccountNotFoundException.class)
    public void authentFailed() {
        final UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(TestsConstants.BAD_USERNAME, TestsConstants.PASSWORD);
        inMemoryProfileService.validate(credentials, null);
    }

    @Test
    public void authentSuccessSingleAttribute() {
        final UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(TestsConstants.GOOD_USERNAME, TestsConstants.PASSWORD);
        inMemoryProfileService.validate(credentials, null);
        final CommonProfile profile = credentials.getUserProfile();
        Assert.assertNotNull(profile);
        Assert.assertEquals(TestsConstants.GOOD_USERNAME, profile.getUsername());
        Assert.assertEquals(2, profile.getAttributes().size());
        Assert.assertEquals(TestsConstants.FIRSTNAME_VALUE, profile.getAttribute(TestsConstants.FIRSTNAME));
    }

    @Test
    public void testCreateUpdateFindDelete() {
        final CommonProfile profile = new CommonProfile();
        profile.setId(InMemoryProfileServiceTests.TEST_ID);
        profile.setLinkedId(InMemoryProfileServiceTests.TEST_LINKED_ID);
        profile.addAttribute(TestsConstants.USERNAME, InMemoryProfileServiceTests.TEST_USER);
        // create
        inMemoryProfileService.create(profile, InMemoryProfileServiceTests.TEST_PASS);
        // check credentials
        final UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(InMemoryProfileServiceTests.TEST_USER, InMemoryProfileServiceTests.TEST_PASS);
        inMemoryProfileService.validate(credentials, null);
        final CommonProfile profile1 = credentials.getUserProfile();
        Assert.assertNotNull(profile1);
        // check data
        final List<Map<String, Object>> results = getData(InMemoryProfileServiceTests.TEST_ID);
        Assert.assertEquals(1, results.size());
        final Map<String, Object> result = results.get(0);
        Assert.assertEquals(5, result.size());
        Assert.assertEquals(InMemoryProfileServiceTests.TEST_ID, result.get("id"));
        Assert.assertEquals(InMemoryProfileServiceTests.TEST_LINKED_ID, result.get(LINKEDID));
        Assert.assertNotNull(result.get(SERIALIZED_PROFILE));
        Assert.assertEquals(InMemoryProfileServiceTests.TEST_USER, result.get(TestsConstants.USERNAME));
        // findById
        final CommonProfile profile2 = inMemoryProfileService.findById(InMemoryProfileServiceTests.TEST_ID);
        Assert.assertEquals(InMemoryProfileServiceTests.TEST_ID, profile2.getId());
        Assert.assertEquals(InMemoryProfileServiceTests.TEST_LINKED_ID, profile2.getLinkedId());
        Assert.assertEquals(InMemoryProfileServiceTests.TEST_USER, profile2.getUsername());
        Assert.assertEquals(1, profile2.getAttributes().size());
        // update with password
        profile.addAttribute(TestsConstants.USERNAME, InMemoryProfileServiceTests.TEST_USER2);
        inMemoryProfileService.update(profile, InMemoryProfileServiceTests.TEST_PASS2);
        List<Map<String, Object>> results2 = getData(InMemoryProfileServiceTests.TEST_ID);
        Assert.assertEquals(1, results2.size());
        Map<String, Object> result2 = results2.get(0);
        Assert.assertEquals(5, result2.size());
        Assert.assertEquals(InMemoryProfileServiceTests.TEST_ID, result2.get("id"));
        Assert.assertEquals(InMemoryProfileServiceTests.TEST_LINKED_ID, result2.get(LINKEDID));
        Assert.assertNotNull(result2.get(SERIALIZED_PROFILE));
        Assert.assertEquals(InMemoryProfileServiceTests.TEST_USER2, result2.get(TestsConstants.USERNAME));
        // check credentials
        final UsernamePasswordCredentials credentials2 = new UsernamePasswordCredentials(InMemoryProfileServiceTests.TEST_USER2, InMemoryProfileServiceTests.TEST_PASS2);
        inMemoryProfileService.validate(credentials2, null);
        final CommonProfile profile3 = credentials.getUserProfile();
        Assert.assertNotNull(profile3);
        // update with no password update
        inMemoryProfileService.update(profile, null);
        results2 = getData(InMemoryProfileServiceTests.TEST_ID);
        Assert.assertEquals(1, results2.size());
        result2 = results2.get(0);
        Assert.assertEquals(5, result2.size());
        Assert.assertEquals(InMemoryProfileServiceTests.TEST_USER2, result2.get(TestsConstants.USERNAME));
        // check credentials
        inMemoryProfileService.validate(credentials2, null);
        final CommonProfile profile4 = credentials.getUserProfile();
        Assert.assertNotNull(profile4);
        // remove
        inMemoryProfileService.remove(profile);
        final List<Map<String, Object>> results3 = getData(InMemoryProfileServiceTests.TEST_ID);
        Assert.assertEquals(0, results3.size());
    }
}

