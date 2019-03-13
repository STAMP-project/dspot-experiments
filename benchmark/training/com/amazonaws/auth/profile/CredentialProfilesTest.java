/**
 * Copyright 2012-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.amazonaws.auth.profile;


import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSSessionCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.profile.internal.Profile;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import org.junit.Assert;
import org.junit.Test;


public class CredentialProfilesTest {
    /**
     * Name of the default profile used in the configuration file.
     */
    private static final String DEFAULT_PROFILE_NAME = "default";

    /**
     * Name of the sample test profile used during testing.
     */
    private static final String PROFILE_NAME_TEST = "test";

    @Test(expected = IllegalArgumentException.class)
    public void loadProfileFromNonExistentFile() {
        new ProfilesConfigFile(new File("/some/invalid/file/location.txt"));
    }

    /**
     * Tests two profiles having same name. The second profile overrides the first profile. Also
     * checks if the AWS Access Key ID and AWS Secret Access Key are mapped properly under the
     * profile.
     */
    @Test
    public void testTwoProfileWithSameName() throws URISyntaxException {
        ProfilesConfigFile profile = new ProfilesConfigFile(ProfileResourceLoader.profilesWithSameProfileName().asFile());
        AWSCredentials defaultCred = profile.getCredentials(CredentialProfilesTest.DEFAULT_PROFILE_NAME);
        Assert.assertNotNull(defaultCred);
        Assert.assertTrue((defaultCred instanceof BasicAWSCredentials));
        AWSCredentials testCred = profile.getCredentials(CredentialProfilesTest.PROFILE_NAME_TEST);
        Assert.assertNotNull(testCred);
        Assert.assertTrue((testCred instanceof AWSSessionCredentials));
        AWSSessionCredentials testSessionCred = ((AWSSessionCredentials) (testCred));
        Assert.assertEquals(testSessionCred.getAWSAccessKeyId(), "testProfile2");
        Assert.assertEquals(testSessionCred.getAWSSecretKey(), "testProfile2");
        Assert.assertEquals(testSessionCred.getSessionToken(), "testProfile2");
    }

    /**
     * Tests loading profile with a profile name having only spaces. An exception should be thrown
     * in this case.
     */
    @Test
    public void testProfileNameWithJustSpaces() {
        checkExpectedException(ProfileResourceLoader.profileNameWithSpaces(), AmazonClientException.class, "Should throw an exception as there is a profile mentioned with no profile name.");
    }

    /**
     * Tests loading profile with a profile name not mentioned. An exception should be thrown in
     * this case.
     */
    @Test
    public void testProfileWithNoProfileNameGiven() {
        checkExpectedException(ProfileResourceLoader.profilesWithNoProfileName(), AmazonClientException.class, "Should throw an exception as there is a profile mentioned with only spaces.");
    }

    /**
     * Tests loading profile with a profile name not having opening or closing braces. An exception
     * should be thrown in this case.
     */
    @Test
    public void testProfileWithProfileNameNotHavingOpeningOrClosingBraces() {
        checkExpectedException(ProfileResourceLoader.profileNameWithNoClosingBraces(), IllegalArgumentException.class, "Should throw an exception as there is a profile name mentioned with no closing braces.");
        checkExpectedException(ProfileResourceLoader.profileNameWithNoOpeningBraces(), IllegalArgumentException.class, "Should throw an exception as there is a profile name mentioned with no opening braces.");
        checkExpectedException(ProfileResourceLoader.profileNameWithNoBraces(), IllegalArgumentException.class, "Should throw an exception as there is a profile name mentioned with no braces.");
    }

    /**
     * Tests loading profile with AWS Access Key not specified for a profile. An exception should be
     * thrown in this case.
     */
    @Test
    public void testProfileWithAccessKeyNotSpecified() throws URISyntaxException {
        checkDeferredException(ProfileResourceLoader.accessKeyNotSpecified(), AmazonClientException.class, "test2", "Should throw an exception as there is a profile with AWS Access Key ID not specified.");
    }

    @Test
    public void testProfileWithEmptyAccessKey() throws URISyntaxException {
        checkDeferredException(ProfileResourceLoader.profileWithEmptyAccessKey(), AmazonClientException.class, "test", "Should throw an exception as there is a profile with an empty AWS Access Key ID");
    }

    /**
     * Tests loading profile with AWS Secret Access Key not specified for a profile. An exception
     * should be thrown in this case.
     */
    @Test
    public void testProfileWithSecretAccessKeyNotSpecified() throws Exception {
        checkDeferredException(ProfileResourceLoader.profilesWithSecretAccessKeyNotSpecified(), AmazonClientException.class, "profile test2", "Should throw an exception as there is a profile with AWS Secret Access Key not specified.");
    }

    @Test
    public void testProfileWithEmptySecretAccessKey() throws URISyntaxException {
        checkDeferredException(ProfileResourceLoader.profileWithEmptySecretKey(), AmazonClientException.class, "test", "Should throw an exception as there is a profile with an empty AWS Secret Access Key.");
    }

    /**
     * Tests loading profile with a profile having multiple AWS Access Key ID's. An exception should
     * be thrown in this case.
     */
    @Test
    public void testProfileWithMultipleAccessOrSecretKeysUnderSameProfile() {
        checkExpectedException(ProfileResourceLoader.profilesWithTwoAccessKeyUnderSameProfile(), IllegalArgumentException.class, "Should throw an exception as there is a profile with two AWS Access Key ID's.");
    }

    /**
     * Tests loading profile with a file that contains other configuration informations like region,
     * output format etc., The file should be parsed correctly and the profiles must be loaded.
     */
    @Test
    public void testProfileWithOtherConfigurations() throws URISyntaxException {
        ProfilesConfigFile profile = new ProfilesConfigFile(ProfileResourceLoader.profilesContainingOtherConfiguration().asFile());
        Assert.assertNotNull(profile.getCredentials(CredentialProfilesTest.DEFAULT_PROFILE_NAME));
        Assert.assertNotNull(profile.getCredentials(CredentialProfilesTest.PROFILE_NAME_TEST));
        Assert.assertEquals(profile.getCredentials(CredentialProfilesTest.PROFILE_NAME_TEST).getAWSAccessKeyId(), "test");
        Assert.assertEquals(profile.getCredentials(CredentialProfilesTest.PROFILE_NAME_TEST).getAWSSecretKey(), "test key");
    }

    /**
     * Test verifying we pick up a change to a file.
     */
    @Test
    public void testReadUpdatedProfile() throws IOException, URISyntaxException {
        ProfilesConfigFile fixture = new ProfilesConfigFile(ProfileResourceLoader.basicProfile().asFile());
        File modifiable = File.createTempFile("UpdatableProfile", ".tst");
        ProfilesConfigFileWriter.dumpToFile(modifiable, true, fixture.getAllProfiles().values().toArray(new Profile[1]));
        ProfilesConfigFile test = new ProfilesConfigFile(modifiable);
        AWSCredentials orig = test.getCredentials(CredentialProfilesTest.DEFAULT_PROFILE_NAME);
        Assert.assertEquals("defaultAccessKey", orig.getAWSAccessKeyId());
        Assert.assertEquals("defaultSecretAccessKey", orig.getAWSSecretKey());
        // Sleep to ensure that the timestamp on the file (when we modify it) is
        // distinguishably later from the original write.
        try {
            Thread.sleep(2000);
        } catch (Exception e) {
        }
        Profile newProfile = new Profile(CredentialProfilesTest.DEFAULT_PROFILE_NAME, new BasicAWSCredentials("newAccessKey", "newSecretKey"));
        ProfilesConfigFileWriter.modifyOneProfile(modifiable, CredentialProfilesTest.DEFAULT_PROFILE_NAME, newProfile);
        test.refresh();
        AWSCredentials updated = test.getCredentials(CredentialProfilesTest.DEFAULT_PROFILE_NAME);
        Assert.assertEquals("newAccessKey", updated.getAWSAccessKeyId());
        Assert.assertEquals("newSecretKey", updated.getAWSSecretKey());
    }

    /**
     * Tests loading a profile that assumes a role, but the source profile does not exist.
     */
    @Test
    public void testRoleProfileWithNoSourceName() throws URISyntaxException {
        checkDeferredException(ProfileResourceLoader.roleProfileWithNoSourceName(), AmazonClientException.class, "test", "Should throw an exception as there is a role profile with a missing source role");
    }

    /**
     * Tests loading a profile that assumes a role, but the source profile does not exist.
     */
    @Test
    public void testRoleProfileWithEmptySourceName() throws URISyntaxException {
        checkDeferredException(ProfileResourceLoader.roleProfileWithEmptySourceName(), AmazonClientException.class, "test", "Should throw an exception as there is a role profile with an empty source role");
    }

    /**
     * Tests loading a profile that assumes a role, but the source profile does not exist.
     */
    @Test
    public void testRoleProfileMissingSource() throws URISyntaxException {
        checkDeferredException(ProfileResourceLoader.roleProfileMissingSource(), AmazonClientException.class, "test", "Should throw an exception as there is a role profile without a source specified");
    }

    /**
     * Tests loading a profile that assumes a role, but the source profile does not exist.
     */
    @Test
    public void testRoleProfileWithRoleSource() throws URISyntaxException {
        checkDeferredException(ProfileResourceLoader.roleProfileWithRoleSource(), AmazonClientException.class, "test", "Should throw an exception as a role profile can not use a role profile as its source");
    }
}

