package org.pac4j.core.profile;


import AnonymousProfile.INSTANCE;
import Pac4jConstants.USER_PROFILES;
import java.util.LinkedHashMap;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.pac4j.core.context.MockWebContext;


/**
 * Tests {@link ProfileManager}.
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
public final class ProfileManagerTests {
    private static final String CLIENT1 = "client1";

    private static final String CLIENT2 = "client2";

    private static final CommonProfile PROFILE1 = new CommonProfile();

    private static final CommonProfile PROFILE2 = new CommonProfile();

    private static final CommonProfile PROFILE3 = new CommonProfile();

    private MockWebContext context;

    private ProfileManager profileManager;

    private LinkedHashMap<String, CommonProfile> profiles;

    static {
        ProfileManagerTests.PROFILE1.setId("ID1");
        ProfileManagerTests.PROFILE1.setClientName(ProfileManagerTests.CLIENT1);
        ProfileManagerTests.PROFILE2.setId("ID2");
        ProfileManagerTests.PROFILE2.setClientName(ProfileManagerTests.CLIENT2);
        ProfileManagerTests.PROFILE3.setId("ID3");
        ProfileManagerTests.PROFILE3.setClientName(ProfileManagerTests.CLIENT1);
    }

    @Test
    public void testGetNullProfile() {
        Assert.assertFalse(profileManager.get(true).isPresent());
    }

    @Test
    public void testGetNoProfile() {
        context.setRequestAttribute(USER_PROFILES, profiles);
        Assert.assertFalse(profileManager.get(true).isPresent());
    }

    @Test
    public void testGetOneProfileFromSession() {
        profiles.put(ProfileManagerTests.CLIENT1, ProfileManagerTests.PROFILE1);
        context.getSessionStore().set(context, USER_PROFILES, profiles);
        Assert.assertEquals(ProfileManagerTests.PROFILE1, profileManager.get(true).get());
        Assert.assertTrue(profileManager.isAuthenticated());
    }

    @Test
    public void testGetOneProfilesFromSessionFirstOneAnonymous() {
        profiles.put("first", new AnonymousProfile());
        profiles.put(ProfileManagerTests.CLIENT1, ProfileManagerTests.PROFILE1);
        context.getSessionStore().set(context, USER_PROFILES, profiles);
        Assert.assertEquals(ProfileManagerTests.PROFILE1, profileManager.get(true).get());
    }

    @Test
    public void testGetOneTwoProfilesFromSession() {
        profiles.put(ProfileManagerTests.CLIENT1, ProfileManagerTests.PROFILE1);
        profiles.put(ProfileManagerTests.CLIENT2, ProfileManagerTests.PROFILE2);
        context.getSessionStore().set(context, USER_PROFILES, profiles);
        Assert.assertEquals(ProfileManagerTests.PROFILE1, profileManager.get(true).get());
        Assert.assertTrue(profileManager.isAuthenticated());
    }

    @Test
    public void testGetOneProfileFromRequest() {
        profiles.put(ProfileManagerTests.CLIENT1, ProfileManagerTests.PROFILE1);
        context.getSessionStore().set(context, USER_PROFILES, profiles);
        Assert.assertFalse(profileManager.get(false).isPresent());
    }

    @Test
    public void testGetAllNullProfile() {
        Assert.assertEquals(0, profileManager.getAll(true).size());
    }

    @Test
    public void testGetAllNoProfile() {
        context.setRequestAttribute(USER_PROFILES, profiles);
        Assert.assertEquals(0, profileManager.getAll(true).size());
        Assert.assertFalse(profileManager.isAuthenticated());
    }

    @Test
    public void testGetAllOneProfileFromSession() {
        profiles.put(ProfileManagerTests.CLIENT1, ProfileManagerTests.PROFILE1);
        context.getSessionStore().set(context, USER_PROFILES, profiles);
        Assert.assertEquals(ProfileManagerTests.PROFILE1, profileManager.getAll(true).get(0));
    }

    @Test
    public void testGetAllTwoProfilesFromSession() {
        profiles.put(ProfileManagerTests.CLIENT1, ProfileManagerTests.PROFILE1);
        profiles.put(ProfileManagerTests.CLIENT2, ProfileManagerTests.PROFILE2);
        context.getSessionStore().set(context, USER_PROFILES, profiles);
        Assert.assertEquals(ProfileManagerTests.PROFILE1, profileManager.getAll(true).get(0));
        Assert.assertEquals(ProfileManagerTests.PROFILE2, profileManager.getAll(true).get(1));
    }

    @Test
    public void testGetAllTwoProfilesFromSessionAndRequest() {
        profiles.put(ProfileManagerTests.CLIENT1, ProfileManagerTests.PROFILE1);
        context.setRequestAttribute(USER_PROFILES, profiles);
        final LinkedHashMap<String, CommonProfile> profiles2 = new LinkedHashMap<>();
        profiles2.put(ProfileManagerTests.CLIENT2, ProfileManagerTests.PROFILE2);
        context.getSessionStore().set(context, USER_PROFILES, profiles2);
        Assert.assertEquals(ProfileManagerTests.PROFILE1, profileManager.getAll(true).get(0));
        Assert.assertEquals(ProfileManagerTests.PROFILE2, profileManager.getAll(true).get(1));
    }

    @Test
    public void testGetAllOneProfileFromRequest() {
        profiles.put(ProfileManagerTests.CLIENT1, ProfileManagerTests.PROFILE1);
        context.getSessionStore().set(context, USER_PROFILES, profiles);
        Assert.assertEquals(0, profileManager.getAll(false).size());
    }

    @Test
    public void testRemoveSessionFalse() {
        profiles.put(ProfileManagerTests.CLIENT1, ProfileManagerTests.PROFILE1);
        context.getSessionStore().set(context, USER_PROFILES, profiles);
        profileManager.remove(false);
        Assert.assertTrue(profileManager.get(true).isPresent());
    }

    @Test
    public void testRemoveSessionTrue() {
        profiles.put(ProfileManagerTests.CLIENT1, ProfileManagerTests.PROFILE1);
        context.getSessionStore().set(context, USER_PROFILES, profiles);
        profileManager.remove(true);
        Assert.assertFalse(profileManager.get(true).isPresent());
    }

    @Test
    public void testLogoutSession() {
        profiles.put(ProfileManagerTests.CLIENT1, ProfileManagerTests.PROFILE1);
        context.getSessionStore().set(context, USER_PROFILES, profiles);
        profileManager.logout();
        Assert.assertFalse(profileManager.get(true).isPresent());
    }

    @Test
    public void testRemoveRequestFalse() {
        profiles.put(ProfileManagerTests.CLIENT1, ProfileManagerTests.PROFILE1);
        context.setRequestAttribute(USER_PROFILES, profiles);
        profileManager.remove(false);
        Assert.assertFalse(profileManager.get(true).isPresent());
    }

    @Test
    public void testRemoveRequestTrue() {
        profiles.put(ProfileManagerTests.CLIENT1, ProfileManagerTests.PROFILE1);
        context.setRequestAttribute(USER_PROFILES, profiles);
        profileManager.remove(true);
        Assert.assertFalse(profileManager.get(true).isPresent());
    }

    @Test
    public void saveOneProfileNoMulti() {
        profiles.put(ProfileManagerTests.CLIENT1, ProfileManagerTests.PROFILE1);
        context.setRequestAttribute(USER_PROFILES, profiles);
        profileManager.save(true, ProfileManagerTests.PROFILE2, false);
        final List<CommonProfile> profiles = profileManager.getAll(true);
        Assert.assertEquals(1, profiles.size());
        Assert.assertEquals(ProfileManagerTests.PROFILE2, profiles.get(0));
    }

    @Test
    public void saveTwoProfilesNoMulti() {
        profiles.put(ProfileManagerTests.CLIENT1, ProfileManagerTests.PROFILE1);
        profiles.put(ProfileManagerTests.CLIENT2, ProfileManagerTests.PROFILE2);
        context.setRequestAttribute(USER_PROFILES, profiles);
        profileManager.save(true, ProfileManagerTests.PROFILE3, false);
        final List<CommonProfile> profiles = profileManager.getAll(true);
        Assert.assertEquals(1, profiles.size());
        Assert.assertEquals(ProfileManagerTests.PROFILE3, profiles.get(0));
    }

    @Test
    public void saveOneProfileMulti() {
        profiles.put(ProfileManagerTests.CLIENT1, ProfileManagerTests.PROFILE1);
        context.setRequestAttribute(USER_PROFILES, profiles);
        profileManager.save(true, ProfileManagerTests.PROFILE2, true);
        final List<CommonProfile> profiles = profileManager.getAll(true);
        Assert.assertEquals(2, profiles.size());
        Assert.assertEquals(ProfileManagerTests.PROFILE1, profiles.get(0));
        Assert.assertEquals(ProfileManagerTests.PROFILE2, profiles.get(1));
    }

    @Test
    public void saveTwoProfilesMulti() {
        profiles.put(ProfileManagerTests.CLIENT1, ProfileManagerTests.PROFILE1);
        profiles.put(ProfileManagerTests.CLIENT2, ProfileManagerTests.PROFILE2);
        context.setRequestAttribute(USER_PROFILES, profiles);
        profileManager.save(true, ProfileManagerTests.PROFILE3, true);
        final List<CommonProfile> profiles = profileManager.getAll(true);
        Assert.assertEquals(2, profiles.size());
        Assert.assertEquals(ProfileManagerTests.PROFILE2, profiles.get(0));
        Assert.assertEquals(ProfileManagerTests.PROFILE3, profiles.get(1));
    }

    @Test
    public void testSingleProfileFromSessionDirectly() {
        final CommonProfile profile = new CommonProfile();
        profile.setClientName(ProfileManagerTests.CLIENT1);
        context.getSessionStore().set(context, USER_PROFILES, profile);
        Assert.assertEquals(profile, profileManager.getAll(true).get(0));
    }

    @Test
    public void testSingleProfileFromRequestDirectly() {
        final CommonProfile profile = new CommonProfile();
        profile.setClientName(ProfileManagerTests.CLIENT1);
        context.setRequestAttribute(USER_PROFILES, profile);
        Assert.assertEquals(profile, profileManager.getAll(false).get(0));
    }

    @Test
    public void testIsAuthenticatedAnonymousProfile() {
        profiles.put(ProfileManagerTests.CLIENT1, INSTANCE);
        context.getSessionStore().set(context, USER_PROFILES, profiles);
        Assert.assertEquals(INSTANCE, profileManager.getAll(true).get(0));
        Assert.assertFalse(profileManager.isAuthenticated());
    }
}

