/**
 * Copyright 2011-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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
import com.amazonaws.auth.profile.internal.BasicProfile;
import java.io.File;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class BasicProfileConfigLoaderTest {
    @Test
    public void basicProfileWithAccessKeySecretKey() {
        File file = ProfileResourceLoader.basicProfile().asFile();
        BasicProfile profile = loadProfiles(file).getProfile("default");
        Assert.assertEquals("defaultAccessKey", profile.getAwsAccessIdKey());
        Assert.assertEquals("defaultSecretAccessKey", profile.getAwsSecretAccessKey());
    }

    @Test(expected = IllegalArgumentException.class)
    public void profileNameWithNoBrackets() {
        File file = ProfileResourceLoader.profileNameWithNoBraces().asFile();
        loadProfiles(file);
    }

    @Test(expected = IllegalArgumentException.class)
    public void profileNameWithNoOpeningBraces() {
        File file = ProfileResourceLoader.profileNameWithNoOpeningBraces().asFile();
        loadProfiles(file);
    }

    @Test(expected = IllegalArgumentException.class)
    public void profileNameWithNoClosingBraces() {
        File file = ProfileResourceLoader.profileNameWithNoClosingBraces().asFile();
        loadProfiles(file);
    }

    @Test(expected = AmazonClientException.class)
    public void blankProfileName() {
        File file = ProfileResourceLoader.profileNameWithSpaces().asFile();
        loadProfiles(file);
    }

    @Test(expected = AmazonClientException.class)
    public void emptyProfileName() {
        File file = ProfileResourceLoader.profilesWithNoProfileName().asFile();
        loadProfiles(file);
    }

    @Test
    public void duplicateProfile() {
        File file = ProfileResourceLoader.profilesWithSameProfileName().asFile();
        BasicProfile profile = loadProfiles(file).getProfile("test");
        Assert.assertEquals("testProfile2", profile.getAwsAccessIdKey());
        Assert.assertEquals("testProfile2", profile.getAwsSecretAccessKey());
        Assert.assertEquals("testProfile2", profile.getAwsSessionToken());
    }

    @Test(expected = IllegalArgumentException.class)
    public void duplicateProperty() {
        File file = ProfileResourceLoader.profilesWithTwoAccessKeyUnderSameProfile().asFile();
        loadProfiles(file);
    }

    @Test
    public void profileWithNoPropertyValue() {
        File file = ProfileResourceLoader.profileWithEmptyAccessKey().asFile();
        BasicProfile profile = loadProfiles(file).getProfile("test");
        Assert.assertThat(profile.getAwsAccessIdKey(), Matchers.isEmptyString());
    }
}

