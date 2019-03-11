/**
 * Copyright (c) 2010-2019 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.persistence.dynamodb.internal;


import Regions.EU_WEST_1;
import com.google.common.collect.ImmutableMap;
import java.io.File;
import java.util.HashMap;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


/**
 *
 *
 * @author Sami Salonen
 */
public class DynamoDBConfigTest {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void testEmpty() throws Exception {
        Assert.assertNull(DynamoDBConfig.fromConfig(new HashMap<String, Object>()));
    }

    @Test
    public void testInvalidRegion() throws Exception {
        Assert.assertNull(DynamoDBConfig.fromConfig(ImmutableMap.<String, Object>of("region", "foobie")));
    }

    @Test
    public void testRegionOnly() throws Exception {
        Assert.assertNull(DynamoDBConfig.fromConfig(ImmutableMap.<String, Object>of("region", "eu-west-1")));
    }

    @Test
    public void testRegionWithAccessKeys() throws Exception {
        DynamoDBConfig fromConfig = DynamoDBConfig.fromConfig(ImmutableMap.<String, Object>of("region", "eu-west-1", "accessKey", "access1", "secretKey", "secret1"));
        Assert.assertEquals(EU_WEST_1, fromConfig.getRegion());
        Assert.assertEquals("access1", fromConfig.getCredentials().getAWSAccessKeyId());
        Assert.assertEquals("secret1", fromConfig.getCredentials().getAWSSecretKey());
        Assert.assertEquals("openhab-", fromConfig.getTablePrefix());
        Assert.assertEquals(true, fromConfig.isCreateTable());
        Assert.assertEquals(1, fromConfig.getReadCapacityUnits());
        Assert.assertEquals(1, fromConfig.getWriteCapacityUnits());
    }

    @Test
    public void testRegionWithProfilesConfigFile() throws Exception {
        File credsFile = folder.newFile("creds");
        FileUtils.write(credsFile, ("[fooprofile]\n" + (("aws_access_key_id=testAccessKey\n" + "aws_secret_access_key=testSecretKey\n") + "aws_session_token=testSessionToken\n")));
        DynamoDBConfig fromConfig = DynamoDBConfig.fromConfig(ImmutableMap.<String, Object>of("region", "eu-west-1", "profilesConfigFile", credsFile.getAbsolutePath(), "profile", "fooprofile"));
        Assert.assertEquals(EU_WEST_1, fromConfig.getRegion());
        Assert.assertEquals("openhab-", fromConfig.getTablePrefix());
        Assert.assertEquals(true, fromConfig.isCreateTable());
        Assert.assertEquals(1, fromConfig.getReadCapacityUnits());
        Assert.assertEquals(1, fromConfig.getWriteCapacityUnits());
    }

    @Test
    public void testNullConfiguration() throws Exception {
        Assert.assertNull(DynamoDBConfig.fromConfig(null));
    }

    @Test
    public void testEmptyConfiguration() throws Exception {
        Assert.assertNull(DynamoDBConfig.fromConfig(ImmutableMap.<String, Object>of()));
    }

    @Test
    public void testRegionWithInvalidProfilesConfigFile() throws Exception {
        File credsFile = folder.newFile("creds");
        FileUtils.write(credsFile, ("[fooprofile]\n" + (("aws_access_key_idINVALIDKEY=testAccessKey\n" + "aws_secret_access_key=testSecretKey\n") + "aws_session_token=testSessionToken\n")));
        Assert.assertNull(DynamoDBConfig.fromConfig(ImmutableMap.<String, Object>of("region", "eu-west-1", "profilesConfigFile", credsFile.getAbsolutePath(), "profile", "fooprofile")));
    }

    @Test
    public void testRegionWithProfilesConfigFileMissingProfile() throws Exception {
        File credsFile = folder.newFile("creds");
        FileUtils.write(credsFile, ("[fooprofile]\n" + (("aws_access_key_id=testAccessKey\n" + "aws_secret_access_key=testSecretKey\n") + "aws_session_token=testSessionToken\n")));
        Assert.assertNull(DynamoDBConfig.fromConfig(ImmutableMap.<String, Object>of("region", "eu-west-1", "profilesConfigFile", credsFile.getAbsolutePath())));
    }

    @Test
    public void testRegionWithAccessKeysWithPrefix() throws Exception {
        DynamoDBConfig fromConfig = DynamoDBConfig.fromConfig(ImmutableMap.<String, Object>of("region", "eu-west-1", "accessKey", "access1", "secretKey", "secret1", "tablePrefix", "foobie-"));
        Assert.assertEquals(EU_WEST_1, fromConfig.getRegion());
        Assert.assertEquals("access1", fromConfig.getCredentials().getAWSAccessKeyId());
        Assert.assertEquals("secret1", fromConfig.getCredentials().getAWSSecretKey());
        Assert.assertEquals("foobie-", fromConfig.getTablePrefix());
        Assert.assertEquals(true, fromConfig.isCreateTable());
        Assert.assertEquals(1, fromConfig.getReadCapacityUnits());
        Assert.assertEquals(1, fromConfig.getWriteCapacityUnits());
    }

    @Test
    public void testRegionWithAccessKeysWithPrefixWithCreateTable() throws Exception {
        DynamoDBConfig fromConfig = DynamoDBConfig.fromConfig(ImmutableMap.<String, Object>of("region", "eu-west-1", "accessKey", "access1", "secretKey", "secret1", "createTable", "false"));
        Assert.assertEquals(EU_WEST_1, fromConfig.getRegion());
        Assert.assertEquals("access1", fromConfig.getCredentials().getAWSAccessKeyId());
        Assert.assertEquals("secret1", fromConfig.getCredentials().getAWSSecretKey());
        Assert.assertEquals("openhab-", fromConfig.getTablePrefix());
        Assert.assertEquals(false, fromConfig.isCreateTable());
        Assert.assertEquals(1, fromConfig.getReadCapacityUnits());
        Assert.assertEquals(1, fromConfig.getWriteCapacityUnits());
    }

    @Test
    public void testRegionWithAccessKeysWithPrefixWithReadCapacityUnits() throws Exception {
        DynamoDBConfig fromConfig = DynamoDBConfig.fromConfig(ImmutableMap.<String, Object>of("region", "eu-west-1", "accessKey", "access1", "secretKey", "secret1", "readCapacityUnits", "5"));
        Assert.assertEquals(EU_WEST_1, fromConfig.getRegion());
        Assert.assertEquals("access1", fromConfig.getCredentials().getAWSAccessKeyId());
        Assert.assertEquals("secret1", fromConfig.getCredentials().getAWSSecretKey());
        Assert.assertEquals("openhab-", fromConfig.getTablePrefix());
        Assert.assertEquals(true, fromConfig.isCreateTable());
        Assert.assertEquals(5, fromConfig.getReadCapacityUnits());
        Assert.assertEquals(1, fromConfig.getWriteCapacityUnits());
    }

    @Test
    public void testRegionWithAccessKeysWithPrefixWithWriteCapacityUnits() throws Exception {
        DynamoDBConfig fromConfig = DynamoDBConfig.fromConfig(ImmutableMap.<String, Object>of("region", "eu-west-1", "accessKey", "access1", "secretKey", "secret1", "writeCapacityUnits", "5"));
        Assert.assertEquals(EU_WEST_1, fromConfig.getRegion());
        Assert.assertEquals("access1", fromConfig.getCredentials().getAWSAccessKeyId());
        Assert.assertEquals("secret1", fromConfig.getCredentials().getAWSSecretKey());
        Assert.assertEquals("openhab-", fromConfig.getTablePrefix());
        Assert.assertEquals(true, fromConfig.isCreateTable());
        Assert.assertEquals(1, fromConfig.getReadCapacityUnits());
        Assert.assertEquals(5, fromConfig.getWriteCapacityUnits());
    }

    @Test
    public void testRegionWithAccessKeysWithPrefixWithReadWriteCapacityUnits() throws Exception {
        DynamoDBConfig fromConfig = DynamoDBConfig.fromConfig(ImmutableMap.<String, Object>of("region", "eu-west-1", "accessKey", "access1", "secretKey", "secret1", "readCapacityUnits", "3", "writeCapacityUnits", "5"));
        Assert.assertEquals(EU_WEST_1, fromConfig.getRegion());
        Assert.assertEquals("access1", fromConfig.getCredentials().getAWSAccessKeyId());
        Assert.assertEquals("secret1", fromConfig.getCredentials().getAWSSecretKey());
        Assert.assertEquals("openhab-", fromConfig.getTablePrefix());
        Assert.assertEquals(true, fromConfig.isCreateTable());
        Assert.assertEquals(3, fromConfig.getReadCapacityUnits());
        Assert.assertEquals(5, fromConfig.getWriteCapacityUnits());
    }
}

