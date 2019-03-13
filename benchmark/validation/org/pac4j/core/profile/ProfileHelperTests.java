package org.pac4j.core.profile;


import AnonymousProfile.INSTANCE;
import Gender.UNSPECIFIED;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.pac4j.core.util.TestsConstants;

import static CommonProfile.SEPARATOR;


/**
 * Tests {@link ProfileHelper}.
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
public final class ProfileHelperTests implements TestsConstants {
    // Examples of attribute keys, borrowed from the SAML module to have something realistic.
    private static final String SAML_CONDITION_NOT_BEFORE_ATTRIBUTE = "notBefore";

    private static final String SAML_CONDITION_NOT_ON_OR_AFTER_ATTRIBUTE = "notOnOrAfter";

    private static final String SESSION_INDEX = "sessionindex";

    private static final String ISSUER_ID = "issuerId";

    private static final String AUTHN_CONTEXT = "authnContext";

    private static final String SAML_NAME_ID_FORMAT = "samlNameIdFormat";

    private static final String SAML_NAME_ID_NAME_QUALIFIER = "samlNameIdNameQualifier";

    private static final String SAML_NAME_ID_SP_NAME_QUALIFIER = "samlNameIdSpNameQualifier";

    private static final String SAML_NAME_ID_SP_PROVIDED_ID = "samlNameIdSpProvidedId";

    private LocalDateTime notBeforeTime;

    private LocalDateTime notOnOrAfterTime;

    @Test
    public void testIsTypedIdOf() {
        Assert.assertFalse(ProfileHelper.isTypedIdOf(TestsConstants.VALUE, CommonProfile.class));
        Assert.assertFalse(ProfileHelper.isTypedIdOf(null, CommonProfile.class));
        Assert.assertFalse(ProfileHelper.isTypedIdOf(TestsConstants.VALUE, null));
        Assert.assertTrue(ProfileHelper.isTypedIdOf(("org.pac4j.core.profile.CommonProfile" + (SEPARATOR)), CommonProfile.class));
    }

    @Test
    public void testBuildUserProfileByClassCompleteName() {
        final CommonProfile profile = new CommonProfile();
        profile.setId(TestsConstants.ID);
        profile.addAttribute(TestsConstants.NAME, TestsConstants.VALUE);
        final CommonProfile profile2 = ProfileHelper.buildUserProfileByClassCompleteName(CommonProfile.class.getName());
        Assert.assertNotNull(profile2);
    }

    @Test
    public void testSanitizeNullIdentifier() {
        Assert.assertNull(ProfileHelper.sanitizeIdentifier(new CommonProfile(), null));
    }

    @Test
    public void testSanitizeNullProfile() {
        Assert.assertEquals("123", ProfileHelper.sanitizeIdentifier(null, 123));
    }

    @Test
    public void testSanitize() {
        Assert.assertEquals("yes", ProfileHelper.sanitizeIdentifier(new CommonProfile(), "org.pac4j.core.profile.CommonProfile#yes"));
    }

    /**
     * Tests {@link ProfileHelper#restoreOrBuildProfile(ProfileDefinition, String, Map, Map, Object...)} when the profile is created using
     * its constructor. The Typed ID contains a separator.
     */
    @Test
    public void testProfileRestoreFromClassName() {
        final String typedId = ((CommonProfile.class.getName()) + (SEPARATOR)) + (TestsConstants.ID);
        final CommonProfile restoredProfile = profileRestoreMustBringBackAllAttributes(typedId);
        Assert.assertNotNull(restoredProfile);
        Assert.assertEquals(TestsConstants.ID, restoredProfile.getId());
        Assert.assertEquals("a@b.cc", restoredProfile.getEmail());
        Assert.assertEquals("John", restoredProfile.getFirstName());
        Assert.assertEquals("Doe", restoredProfile.getFamilyName());
        Assert.assertEquals(UNSPECIFIED, restoredProfile.getGender());// Because it was not set

        Assert.assertNull(restoredProfile.getDisplayName());// Was not set either

        Assert.assertEquals("12345-67890", restoredProfile.getAttribute(ProfileHelperTests.SESSION_INDEX));
        Assert.assertEquals(notBeforeTime, restoredProfile.getAttribute(ProfileHelperTests.SAML_CONDITION_NOT_BEFORE_ATTRIBUTE));
        Assert.assertEquals(notOnOrAfterTime, restoredProfile.getAttribute(ProfileHelperTests.SAML_CONDITION_NOT_ON_OR_AFTER_ATTRIBUTE));
        Assert.assertEquals("IssuerId", restoredProfile.getAuthenticationAttribute(ProfileHelperTests.ISSUER_ID));
        List<String> context = ((List<String>) (restoredProfile.getAuthenticationAttribute(ProfileHelperTests.AUTHN_CONTEXT)));
        Assert.assertThat(context, CoreMatchers.hasItem("ContextItem1"));
        Assert.assertThat(context, CoreMatchers.hasItem("ContextItem2"));
        Assert.assertEquals("NameIdFormat", restoredProfile.getAuthenticationAttribute(ProfileHelperTests.SAML_NAME_ID_FORMAT));
        Assert.assertEquals("NameIdNameQualifier", restoredProfile.getAuthenticationAttribute(ProfileHelperTests.SAML_NAME_ID_NAME_QUALIFIER));
        Assert.assertEquals("NameIdSpNameQualifier", restoredProfile.getAuthenticationAttribute(ProfileHelperTests.SAML_NAME_ID_SP_NAME_QUALIFIER));
        Assert.assertEquals("NameIdSpProvidedId", restoredProfile.getAuthenticationAttribute(ProfileHelperTests.SAML_NAME_ID_SP_PROVIDED_ID));
        Assert.assertEquals(notBeforeTime, restoredProfile.getAuthenticationAttribute(ProfileHelperTests.SAML_CONDITION_NOT_BEFORE_ATTRIBUTE));
        Assert.assertEquals(notOnOrAfterTime, restoredProfile.getAuthenticationAttribute(ProfileHelperTests.SAML_CONDITION_NOT_ON_OR_AFTER_ATTRIBUTE));
    }

    /**
     * Tests {@link ProfileHelper#restoreOrBuildProfile(ProfileDefinition, String, Map, Map, Object...)} when the profile is created using
     * through the profile definition's create function. The Typed ID does not contain a separator.
     */
    @Test
    public void testProfileRestoreFromProfileDefinitionCreateFunction() {
        final String typedId = TestsConstants.ID;
        final CommonProfile restoredProfile = profileRestoreMustBringBackAllAttributes(typedId);
        Assert.assertNotNull(restoredProfile);
        Assert.assertEquals(TestsConstants.ID, restoredProfile.getId());
        Assert.assertEquals("a@b.cc", restoredProfile.getEmail());
        Assert.assertEquals("John", restoredProfile.getFirstName());
        Assert.assertEquals("Doe", restoredProfile.getFamilyName());
        Assert.assertEquals(UNSPECIFIED, restoredProfile.getGender());// Because it was not set

        Assert.assertNull(restoredProfile.getDisplayName());// Was not set either

        Assert.assertEquals("12345-67890", restoredProfile.getAttribute(ProfileHelperTests.SESSION_INDEX));
        Assert.assertEquals(notBeforeTime, restoredProfile.getAttribute(ProfileHelperTests.SAML_CONDITION_NOT_BEFORE_ATTRIBUTE));
        Assert.assertEquals(notOnOrAfterTime, restoredProfile.getAttribute(ProfileHelperTests.SAML_CONDITION_NOT_ON_OR_AFTER_ATTRIBUTE));
        Assert.assertEquals("IssuerId", restoredProfile.getAuthenticationAttribute(ProfileHelperTests.ISSUER_ID));
        List<String> context = ((List<String>) (restoredProfile.getAuthenticationAttribute(ProfileHelperTests.AUTHN_CONTEXT)));
        Assert.assertThat(context, CoreMatchers.hasItem("ContextItem1"));
        Assert.assertThat(context, CoreMatchers.hasItem("ContextItem2"));
        Assert.assertEquals("NameIdFormat", restoredProfile.getAuthenticationAttribute(ProfileHelperTests.SAML_NAME_ID_FORMAT));
        Assert.assertEquals("NameIdNameQualifier", restoredProfile.getAuthenticationAttribute(ProfileHelperTests.SAML_NAME_ID_NAME_QUALIFIER));
        Assert.assertEquals("NameIdSpNameQualifier", restoredProfile.getAuthenticationAttribute(ProfileHelperTests.SAML_NAME_ID_SP_NAME_QUALIFIER));
        Assert.assertEquals("NameIdSpProvidedId", restoredProfile.getAuthenticationAttribute(ProfileHelperTests.SAML_NAME_ID_SP_PROVIDED_ID));
        Assert.assertEquals(notBeforeTime, restoredProfile.getAuthenticationAttribute(ProfileHelperTests.SAML_CONDITION_NOT_BEFORE_ATTRIBUTE));
        Assert.assertEquals(notOnOrAfterTime, restoredProfile.getAuthenticationAttribute(ProfileHelperTests.SAML_CONDITION_NOT_ON_OR_AFTER_ATTRIBUTE));
    }

    @Test
    public void testFlatIntoOneProfileOneAnonymousProfile() {
        final List<CommonProfile> profiles = Arrays.asList(INSTANCE);
        Assert.assertEquals(INSTANCE, ProfileHelper.flatIntoOneProfile(profiles).get());
    }

    @Test
    public void testFlatIntoOneProfileNAnonymousProfiles() {
        final List<CommonProfile> profiles = Arrays.asList(null, INSTANCE, null, INSTANCE);
        Assert.assertEquals(INSTANCE, ProfileHelper.flatIntoOneProfile(profiles).get());
    }

    @Test
    public void testFlatIntoOneProfileOneProfile() {
        final CommonProfile profile1 = new CommonProfile();
        profile1.setId("ONE");
        final List<CommonProfile> profiles = Arrays.asList(profile1);
        Assert.assertEquals(profile1, ProfileHelper.flatIntoOneProfile(profiles).get());
    }

    @Test
    public void testFlatIntoOneProfileNProfiles() {
        final CommonProfile profile2 = new CommonProfile();
        profile2.setId("TWO");
        final List<CommonProfile> profiles = Arrays.asList(INSTANCE, null, profile2);
        Assert.assertEquals(profile2, ProfileHelper.flatIntoOneProfile(profiles).get());
    }
}

