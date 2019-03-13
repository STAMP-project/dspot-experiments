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
package org.apache.nifi.processors.aws.credentials.provider.service;


import AbstractAWSProcessor.PROXY_HOST;
import AbstractAWSProcessor.PROXY_HOST_PORT;
import AbstractAWSProcessor.PROXY_PASSWORD;
import AbstractAWSProcessor.PROXY_USERNAME;
import org.apache.nifi.util.TestRunner;
import org.junit.Test;


public class AWSProcessorProxyTest {
    private TestRunner runner;

    @SuppressWarnings("deprecation")
    @Test
    public void testProxyHostOnlyInvalid() throws Throwable {
        runner.setProperty(PROXY_HOST, "proxyHost");
        runner.assertNotValid();
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testProxyHostPortOnlyInvalid() throws Throwable {
        runner.setProperty(PROXY_HOST_PORT, "1");
        runner.assertNotValid();
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testProxyHostPortNonNumberInvalid() throws Throwable {
        runner.setProperty(PROXY_HOST_PORT, "a");
        runner.assertNotValid();
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testProxyHostAndPortValid() throws Throwable {
        runner.setProperty(PROXY_HOST_PORT, "1");
        runner.setProperty(PROXY_HOST, "proxyHost");
        runner.assertValid();
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testProxyUserNoPasswordInValid() throws Throwable {
        runner.setProperty(PROXY_USERNAME, "foo");
        runner.assertNotValid();
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testProxyNoUserPasswordInValid() throws Throwable {
        runner.setProperty(PROXY_PASSWORD, "foo");
        runner.assertNotValid();
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testProxyUserPasswordNoHostInValid() throws Throwable {
        runner.setProperty(PROXY_USERNAME, "foo");
        runner.setProperty(PROXY_PASSWORD, "foo");
        runner.assertNotValid();
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testProxyUserPasswordHostValid() throws Throwable {
        runner.setProperty(PROXY_HOST_PORT, "1");
        runner.setProperty(PROXY_HOST, "proxyHost");
        runner.setProperty(PROXY_USERNAME, "foo");
        runner.setProperty(PROXY_PASSWORD, "foo");
        runner.assertValid();
    }
}

