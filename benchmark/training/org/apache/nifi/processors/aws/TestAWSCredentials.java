/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.processors.aws;


import AbstractAWSCredentialsProviderProcessor.AWS_CREDENTIALS_PROVIDER_SERVICE;
import AbstractAWSProcessor.ACCESS_KEY;
import AbstractAWSProcessor.CREDENTIALS_FILE;
import AbstractAWSProcessor.SECRET_KEY;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AnonymousAWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.internal.StaticCredentialsProvider;
import org.apache.nifi.processors.aws.credentials.provider.service.AWSCredentialsProviderControllerService;
import org.apache.nifi.reporting.InitializationException;
import org.apache.nifi.util.TestRunner;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit tests for AWS Credential specification based on {@link AbstractAWSProcessor} and
 * [@link AbstractAWSCredentialsProviderProcessor},  without interaction with S3.
 */
public class TestAWSCredentials {
    private TestRunner runner = null;

    private AbstractAWSProcessor mockAwsProcessor = null;

    private AWSCredentials awsCredentials = null;

    private AWSCredentialsProvider awsCredentialsProvider = null;

    private ClientConfiguration clientConfiguration = null;

    @Test
    public void testAnonymousByDefault() {
        runner.assertValid();
        runner.run(1);
        Assert.assertEquals(AnonymousAWSCredentials.class, awsCredentials.getClass());
        Assert.assertNull(awsCredentialsProvider);
    }

    @Test
    public void testAccessKeySecretKey() {
        runner.setProperty(ACCESS_KEY, "testAccessKey");
        runner.setProperty(SECRET_KEY, "testSecretKey");
        runner.assertValid();
        runner.run(1);
        Assert.assertEquals(BasicAWSCredentials.class, awsCredentials.getClass());
        Assert.assertNull(awsCredentialsProvider);
    }

    @Test
    public void testCredentialsFile() {
        runner.setProperty(CREDENTIALS_FILE, "src/test/resources/mock-aws-credentials.properties");
        runner.assertValid();
        runner.run(1);
        Assert.assertEquals(PropertiesCredentials.class, awsCredentials.getClass());
        Assert.assertNull(awsCredentialsProvider);
    }

    @Test
    public void testCredentialsProviderControllerService() throws InitializationException {
        final AWSCredentialsProviderControllerService credsService = new AWSCredentialsProviderControllerService();
        runner.addControllerService("awsCredentialsProvider", credsService);
        runner.setProperty(credsService, ACCESS_KEY, "awsAccessKey");
        runner.setProperty(credsService, SECRET_KEY, "awsSecretKey");
        runner.enableControllerService(credsService);
        runner.setProperty(AWS_CREDENTIALS_PROVIDER_SERVICE, "awsCredentialsProvider");
        runner.assertValid();
        runner.run(1);
        Assert.assertEquals(StaticCredentialsProvider.class, awsCredentialsProvider.getClass());
        Assert.assertNull(awsCredentials);
    }
}

