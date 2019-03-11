/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.fs.azurebfs;


import Charsets.UTF_8;
import ConfigurationKeys.FS_AZURE_ACCOUNT_KEY_PROPERTY_NAME;
import SSLSocketFactoryEx.SSLChannelMode.Default;
import SSLSocketFactoryEx.SSLChannelMode.Default_JSSE;
import SSLSocketFactoryEx.SSLChannelMode.OpenSSL;
import java.io.IOException;
import java.lang.reflect.Field;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.azurebfs.constants.ConfigurationKeys;
import org.apache.hadoop.fs.azurebfs.constants.FileSystemConfigurations;
import org.apache.hadoop.fs.azurebfs.constants.TestConfigurationKeys;
import org.apache.hadoop.fs.azurebfs.contracts.annotations.ConfigurationValidationAnnotations.Base64StringConfigurationValidatorAnnotation;
import org.apache.hadoop.fs.azurebfs.contracts.annotations.ConfigurationValidationAnnotations.BooleanConfigurationValidatorAnnotation;
import org.apache.hadoop.fs.azurebfs.contracts.annotations.ConfigurationValidationAnnotations.IntegerConfigurationValidatorAnnotation;
import org.apache.hadoop.fs.azurebfs.contracts.annotations.ConfigurationValidationAnnotations.LongConfigurationValidatorAnnotation;
import org.apache.hadoop.fs.azurebfs.contracts.annotations.ConfigurationValidationAnnotations.StringConfigurationValidatorAnnotation;
import org.apache.hadoop.fs.azurebfs.contracts.exceptions.ConfigurationPropertyNotFoundException;
import org.apache.hadoop.fs.azurebfs.contracts.exceptions.InvalidConfigurationValueException;
import org.apache.hadoop.fs.azurebfs.utils.Base64;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test ConfigurationServiceFieldsValidation.
 */
public class TestAbfsConfigurationFieldsValidation {
    private AbfsConfiguration abfsConfiguration;

    private static final String INT_KEY = "intKey";

    private static final String LONG_KEY = "longKey";

    private static final String STRING_KEY = "stringKey";

    private static final String BASE64_KEY = "base64Key";

    private static final String BOOLEAN_KEY = "booleanKey";

    private static final int DEFAULT_INT = 4194304;

    private static final int DEFAULT_LONG = 4194304;

    private static final int TEST_INT = 1234565;

    private static final int TEST_LONG = 4194304;

    private final String accountName;

    private final String encodedString;

    private final String encodedAccountKey;

    @IntegerConfigurationValidatorAnnotation(ConfigurationKey = TestAbfsConfigurationFieldsValidation.INT_KEY, MinValue = Integer.MIN_VALUE, MaxValue = Integer.MAX_VALUE, DefaultValue = TestAbfsConfigurationFieldsValidation.DEFAULT_INT)
    private int intField;

    @LongConfigurationValidatorAnnotation(ConfigurationKey = TestAbfsConfigurationFieldsValidation.LONG_KEY, MinValue = Long.MIN_VALUE, MaxValue = Long.MAX_VALUE, DefaultValue = TestAbfsConfigurationFieldsValidation.DEFAULT_LONG)
    private int longField;

    @StringConfigurationValidatorAnnotation(ConfigurationKey = TestAbfsConfigurationFieldsValidation.STRING_KEY, DefaultValue = "default")
    private String stringField;

    @Base64StringConfigurationValidatorAnnotation(ConfigurationKey = TestAbfsConfigurationFieldsValidation.BASE64_KEY, DefaultValue = "base64")
    private String base64Field;

    @BooleanConfigurationValidatorAnnotation(ConfigurationKey = TestAbfsConfigurationFieldsValidation.BOOLEAN_KEY, DefaultValue = false)
    private boolean boolField;

    public TestAbfsConfigurationFieldsValidation() throws Exception {
        super();
        this.accountName = "testaccount1.blob.core.windows.net";
        this.encodedString = Base64.encode("base64Value".getBytes(UTF_8));
        this.encodedAccountKey = Base64.encode("someAccountKey".getBytes(UTF_8));
        Configuration configuration = new Configuration();
        configuration.addResource(TestConfigurationKeys.TEST_CONFIGURATION_FILE_NAME);
        configuration.set(TestAbfsConfigurationFieldsValidation.INT_KEY, "1234565");
        configuration.set(TestAbfsConfigurationFieldsValidation.LONG_KEY, "4194304");
        configuration.set(TestAbfsConfigurationFieldsValidation.STRING_KEY, "stringValue");
        configuration.set(TestAbfsConfigurationFieldsValidation.BASE64_KEY, encodedString);
        configuration.set(TestAbfsConfigurationFieldsValidation.BOOLEAN_KEY, "true");
        configuration.set((((ConfigurationKeys.FS_AZURE_ACCOUNT_KEY_PROPERTY_NAME) + ".") + (accountName)), this.encodedAccountKey);
        abfsConfiguration = new AbfsConfiguration(configuration, accountName);
    }

    @Test
    public void testValidateFunctionsInConfigServiceImpl() throws Exception {
        Field[] fields = this.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(IntegerConfigurationValidatorAnnotation.class)) {
                Assert.assertEquals(TestAbfsConfigurationFieldsValidation.TEST_INT, abfsConfiguration.validateInt(field));
            } else
                if (field.isAnnotationPresent(LongConfigurationValidatorAnnotation.class)) {
                    Assert.assertEquals(TestAbfsConfigurationFieldsValidation.DEFAULT_LONG, abfsConfiguration.validateLong(field));
                } else
                    if (field.isAnnotationPresent(StringConfigurationValidatorAnnotation.class)) {
                        Assert.assertEquals("stringValue", abfsConfiguration.validateString(field));
                    } else
                        if (field.isAnnotationPresent(Base64StringConfigurationValidatorAnnotation.class)) {
                            Assert.assertEquals(this.encodedString, abfsConfiguration.validateBase64String(field));
                        } else
                            if (field.isAnnotationPresent(BooleanConfigurationValidatorAnnotation.class)) {
                                Assert.assertEquals(true, abfsConfiguration.validateBoolean(field));
                            }




        }
    }

    @Test
    public void testConfigServiceImplAnnotatedFieldsInitialized() throws Exception {
        // test that all the ConfigurationServiceImpl annotated fields have been initialized in the constructor
        Assert.assertEquals(FileSystemConfigurations.DEFAULT_WRITE_BUFFER_SIZE, abfsConfiguration.getWriteBufferSize());
        Assert.assertEquals(FileSystemConfigurations.DEFAULT_READ_BUFFER_SIZE, abfsConfiguration.getReadBufferSize());
        Assert.assertEquals(FileSystemConfigurations.DEFAULT_MIN_BACKOFF_INTERVAL, abfsConfiguration.getMinBackoffIntervalMilliseconds());
        Assert.assertEquals(FileSystemConfigurations.DEFAULT_MAX_BACKOFF_INTERVAL, abfsConfiguration.getMaxBackoffIntervalMilliseconds());
        Assert.assertEquals(FileSystemConfigurations.DEFAULT_BACKOFF_INTERVAL, abfsConfiguration.getBackoffIntervalMilliseconds());
        Assert.assertEquals(FileSystemConfigurations.DEFAULT_MAX_RETRY_ATTEMPTS, abfsConfiguration.getMaxIoRetries());
        Assert.assertEquals(FileSystemConfigurations.MAX_AZURE_BLOCK_SIZE, abfsConfiguration.getAzureBlockSize());
        Assert.assertEquals(FileSystemConfigurations.AZURE_BLOCK_LOCATION_HOST_DEFAULT, abfsConfiguration.getAzureBlockLocationHost());
    }

    @Test
    public void testGetAccountKey() throws Exception {
        String accountKey = abfsConfiguration.getStorageAccountKey();
        Assert.assertEquals(this.encodedAccountKey, accountKey);
    }

    @Test(expected = ConfigurationPropertyNotFoundException.class)
    public void testGetAccountKeyWithNonExistingAccountName() throws Exception {
        Configuration configuration = new Configuration();
        configuration.addResource(TestConfigurationKeys.TEST_CONFIGURATION_FILE_NAME);
        configuration.unset(FS_AZURE_ACCOUNT_KEY_PROPERTY_NAME);
        AbfsConfiguration abfsConfig = new AbfsConfiguration(configuration, "bogusAccountName");
        abfsConfig.getStorageAccountKey();
    }

    @Test
    public void testSSLSocketFactoryConfiguration() throws IOException, IllegalAccessException, InvalidConfigurationValueException {
        Assert.assertEquals(Default, abfsConfiguration.getPreferredSSLFactoryOption());
        Assert.assertNotEquals(Default_JSSE, abfsConfiguration.getPreferredSSLFactoryOption());
        Assert.assertNotEquals(OpenSSL, abfsConfiguration.getPreferredSSLFactoryOption());
        Configuration configuration = new Configuration();
        configuration.setEnum(ConfigurationKeys.FS_AZURE_SSL_CHANNEL_MODE_KEY, Default_JSSE);
        AbfsConfiguration localAbfsConfiguration = new AbfsConfiguration(configuration, accountName);
        Assert.assertEquals(Default_JSSE, localAbfsConfiguration.getPreferredSSLFactoryOption());
        configuration = new Configuration();
        configuration.setEnum(ConfigurationKeys.FS_AZURE_SSL_CHANNEL_MODE_KEY, OpenSSL);
        localAbfsConfiguration = new AbfsConfiguration(configuration, accountName);
        Assert.assertEquals(OpenSSL, localAbfsConfiguration.getPreferredSSLFactoryOption());
    }
}

