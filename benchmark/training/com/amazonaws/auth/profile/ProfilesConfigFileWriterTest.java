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
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.auth.profile.internal.Profile;
import com.amazonaws.util.ImmutableMapParameter;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;


public class ProfilesConfigFileWriterTest {
    private static final AWSCredentials basicCredA = new BasicAWSCredentials("a", "a");

    private static final AWSCredentials basicCredB = new BasicAWSCredentials("b", "b");

    private static final AWSCredentials sessionCredC = new BasicSessionCredentials("c", "c", "c");

    private static final AWSCredentials sessionCredD = new BasicSessionCredentials("d", "d", "d");

    @Test
    public void testDumpToFile() throws IOException {
        File tmpFile = File.createTempFile("credentials.", null);
        Profile[] abcd = new Profile[]{ new Profile("a", ProfilesConfigFileWriterTest.basicCredA), new Profile("b", ProfilesConfigFileWriterTest.basicCredB), new Profile("c", ProfilesConfigFileWriterTest.sessionCredC), new Profile("d", ProfilesConfigFileWriterTest.sessionCredD) };
        ProfilesConfigFileWriter.dumpToFile(tmpFile, true, abcd);
        ProfilesConfigFileWriterTest.checkCredentialsFile(tmpFile, abcd);
        // Rewrite the file with overwrite=true
        Profile[] a = new Profile[]{ new Profile("a", ProfilesConfigFileWriterTest.basicCredA) };
        ProfilesConfigFileWriter.dumpToFile(tmpFile, true, a);
        ProfilesConfigFileWriterTest.checkCredentialsFile(tmpFile, a);
        // Rewrite the file with overwrite=false is not allowed
        try {
            ProfilesConfigFileWriter.dumpToFile(tmpFile, false, new Profile("a", ProfilesConfigFileWriterTest.basicCredA));
            Assert.fail("Should have thrown exception since the destination file already exists.");
        } catch (AmazonClientException expected) {
        }
    }

    @Test
    public void testModifyProfile() throws IOException {
        File tmpFile = File.createTempFile("credentials.", null);
        Profile[] abcd = new Profile[]{ new Profile("a", ProfilesConfigFileWriterTest.basicCredA), new Profile("b", ProfilesConfigFileWriterTest.basicCredB), new Profile("c", ProfilesConfigFileWriterTest.sessionCredC), new Profile("d", ProfilesConfigFileWriterTest.sessionCredD) };
        ProfilesConfigFileWriter.dumpToFile(tmpFile, true, abcd);
        // a <==> c, b <==> d
        Profile[] modified = new Profile[]{ new Profile("a", ProfilesConfigFileWriterTest.sessionCredC), new Profile("b", ProfilesConfigFileWriterTest.sessionCredD), new Profile("c", ProfilesConfigFileWriterTest.basicCredA), new Profile("d", ProfilesConfigFileWriterTest.basicCredB) };
        ProfilesConfigFileWriter.modifyOrInsertProfiles(tmpFile, modified);
        ProfilesConfigFileWriterTest.checkCredentialsFile(tmpFile, modified);
    }

    @Test
    public void testInsertProfile() throws IOException {
        File tmpFile = File.createTempFile("credentials.", null);
        Profile[] abcd = new Profile[]{ new Profile("a", ProfilesConfigFileWriterTest.basicCredA), new Profile("b", ProfilesConfigFileWriterTest.basicCredB), new Profile("c", ProfilesConfigFileWriterTest.sessionCredC), new Profile("d", ProfilesConfigFileWriterTest.sessionCredD) };
        ProfilesConfigFileWriter.dumpToFile(tmpFile, true, abcd);
        // Insert [e] profile
        Profile e = new Profile("e", ProfilesConfigFileWriterTest.basicCredA);
        ProfilesConfigFileWriter.modifyOrInsertProfiles(tmpFile, e);
        ProfilesConfigFileWriterTest.checkCredentialsFile(tmpFile, abcd[0], abcd[1], abcd[2], abcd[3], e);
    }

    @Test
    public void testModifyAndInsertProfile() throws IOException {
        File tmpFile = File.createTempFile("credentials.", null);
        Profile[] abcd = new Profile[]{ new Profile("a", ProfilesConfigFileWriterTest.basicCredA), new Profile("b", ProfilesConfigFileWriterTest.basicCredB), new Profile("c", ProfilesConfigFileWriterTest.sessionCredC), new Profile("d", ProfilesConfigFileWriterTest.sessionCredD) };
        ProfilesConfigFileWriter.dumpToFile(tmpFile, true, abcd);
        // a <==> c, b <==> d, +e
        Profile[] modified = new Profile[]{ new Profile("a", ProfilesConfigFileWriterTest.sessionCredC), new Profile("b", ProfilesConfigFileWriterTest.sessionCredD), new Profile("c", ProfilesConfigFileWriterTest.basicCredA), new Profile("d", ProfilesConfigFileWriterTest.basicCredB), new Profile("e", ProfilesConfigFileWriterTest.basicCredA) };
        ProfilesConfigFileWriter.modifyOrInsertProfiles(tmpFile, modified);
        ProfilesConfigFileWriterTest.checkCredentialsFile(tmpFile, modified);
    }

    /**
     * Tests that comments and unsupported properties are preserved after
     * profile modification.
     *
     * @throws URISyntaxException
     * 		
     */
    @Test
    public void testModifyAndInsertProfile_WithComments() throws IOException, URISyntaxException {
        File credWithComments = ProfileResourceLoader.profilesWithComments().asFile();
        File tmpFile = ProfilesConfigFileWriterTest.copyToTempFile(credWithComments);
        String originalContent = FileUtils.readFileToString(tmpFile);
        Profile[] expected = new Profile[]{ new Profile("a", ProfilesConfigFileWriterTest.basicCredA), new Profile("b", ProfilesConfigFileWriterTest.basicCredB), new Profile("c", ProfilesConfigFileWriterTest.sessionCredC), new Profile("d", ProfilesConfigFileWriterTest.sessionCredD) };
        // a <==> b, c <==> d, also renaming them to uppercase letters
        Profile[] modified = new Profile[]{ new Profile("A", ProfilesConfigFileWriterTest.basicCredB), new Profile("B", ProfilesConfigFileWriterTest.basicCredA), new Profile("C", ProfilesConfigFileWriterTest.sessionCredD), new Profile("D", ProfilesConfigFileWriterTest.sessionCredC) };
        ProfilesConfigFileWriter.modifyProfiles(tmpFile, ImmutableMapParameter.of("a", modified[0], "b", modified[1], "c", modified[2], "d", modified[3]));
        ProfilesConfigFileWriterTest.checkCredentialsFile(tmpFile, modified);
        // Sanity check that the content is altered
        String modifiedContent = FileUtils.readFileToString(tmpFile);
        Assert.assertFalse(originalContent.equals(modifiedContent));
        // Restore the properties
        ProfilesConfigFileWriter.modifyProfiles(tmpFile, ImmutableMapParameter.of("A", expected[0], "B", expected[1], "C", expected[2], "D", expected[3]));
        ProfilesConfigFileWriterTest.checkCredentialsFile(tmpFile, expected);
        // Check that the content is now the same as the original
        String restoredContent = FileUtils.readFileToString(tmpFile);
        Assert.assertEquals(originalContent, restoredContent);
    }

    @Test
    public void testRenameProfile() throws IOException {
        File tmpFile = File.createTempFile("credentials.", null);
        Profile[] abcd = new Profile[]{ new Profile("a", ProfilesConfigFileWriterTest.basicCredA), new Profile("b", ProfilesConfigFileWriterTest.basicCredB), new Profile("c", ProfilesConfigFileWriterTest.sessionCredC), new Profile("d", ProfilesConfigFileWriterTest.sessionCredD) };
        ProfilesConfigFileWriter.dumpToFile(tmpFile, true, abcd);
        // Rename a to A
        Profile[] modified = new Profile[]{ new Profile("A", ProfilesConfigFileWriterTest.basicCredA), new Profile("b", ProfilesConfigFileWriterTest.basicCredB), new Profile("c", ProfilesConfigFileWriterTest.sessionCredC), new Profile("d", ProfilesConfigFileWriterTest.sessionCredD) };
        ProfilesConfigFileWriter.modifyOneProfile(tmpFile, "a", new Profile("A", ProfilesConfigFileWriterTest.basicCredA));
        ProfilesConfigFileWriterTest.checkCredentialsFile(tmpFile, modified);
    }

    @Test
    public void testDeleteProfile() throws IOException {
        File tmpFile = File.createTempFile("credentials.", null);
        Profile[] abcd = new Profile[]{ new Profile("a", ProfilesConfigFileWriterTest.basicCredA), new Profile("b", ProfilesConfigFileWriterTest.basicCredB), new Profile("c", ProfilesConfigFileWriterTest.sessionCredC), new Profile("d", ProfilesConfigFileWriterTest.sessionCredD) };
        ProfilesConfigFileWriter.dumpToFile(tmpFile, true, abcd);
        // Delete a and c
        Profile[] modified = new Profile[]{ new Profile("b", ProfilesConfigFileWriterTest.basicCredB), new Profile("d", ProfilesConfigFileWriterTest.sessionCredD) };
        ProfilesConfigFileWriter.deleteProfiles(tmpFile, "a", "c");
        ProfilesConfigFileWriterTest.checkCredentialsFile(tmpFile, modified);
    }

    /**
     * Tests that the original credentials file is properly restored if the
     * in-place modification fails with error.
     */
    @Test
    public void testInPlaceModificationErrorHandling() throws IOException {
        File tmpFile = File.createTempFile("credentials.", null);
        Profile[] abcd = new Profile[]{ new Profile("a", ProfilesConfigFileWriterTest.basicCredA), new Profile("b", ProfilesConfigFileWriterTest.basicCredB), new Profile("c", ProfilesConfigFileWriterTest.sessionCredC), new Profile("d", ProfilesConfigFileWriterTest.sessionCredD) };
        ProfilesConfigFileWriter.dumpToFile(tmpFile, true, abcd);
        String originalContent = FileUtils.readFileToString(tmpFile);
        // Insert [e] profile, which throws RuntimeException when the getProperties method is called.
        Profile e = new ProfilesConfigFileWriterTest.ProfileWithException("e", ProfilesConfigFileWriterTest.basicCredA);
        try {
            ProfilesConfigFileWriter.modifyOrInsertProfiles(tmpFile, e);
            Assert.fail("An exception is expected.");
        } catch (AmazonClientException expected) {
        }
        // Check that the original file is restored
        Assert.assertTrue(tmpFile.exists());
        String restoredContent = FileUtils.readFileToString(tmpFile);
        Assert.assertEquals(originalContent, restoredContent);
    }

    private static class ProfileWithException extends Profile {
        public ProfileWithException(String profileName, AWSCredentials awsCredentials) {
            super(profileName, awsCredentials);
        }

        @Override
        public Map<String, String> getProperties() {
            throw new RuntimeException("Some exception...");
        }
    }
}

