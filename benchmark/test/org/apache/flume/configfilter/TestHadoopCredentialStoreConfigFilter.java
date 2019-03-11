/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.flume.configfilter;


import java.io.File;
import java.util.HashMap;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.EnvironmentVariables;


public class TestHadoopCredentialStoreConfigFilter {
    private static String providerPathDefault;

    private static String providerPathEnv;

    private static String providerPathPwdFile;

    @ClassRule
    public static final EnvironmentVariables environmentVariables = new EnvironmentVariables();

    private static File fileDefault;

    private static File fileEnvPassword;

    private static File fileFilePassword;

    private HadoopCredentialStoreConfigFilter configFilter;

    @Test
    public void filterDefaultPasswordFile() {
        HashMap<String, String> configuration = new HashMap<>();
        configuration.put(HadoopCredentialStoreConfigFilter.CREDENTIAL_PROVIDER_PATH, TestHadoopCredentialStoreConfigFilter.providerPathDefault);
        configFilter.initializeWithConfiguration(configuration);
        Assert.assertEquals("filtered_default", configFilter.filter("password"));
    }

    @Test
    public void filterWithEnvPassword() {
        TestHadoopCredentialStoreConfigFilter.environmentVariables.set("HADOOP_CREDSTORE_PASSWORD", "envSecret");
        HashMap<String, String> configuration = new HashMap<>();
        configuration.put(HadoopCredentialStoreConfigFilter.CREDENTIAL_PROVIDER_PATH, TestHadoopCredentialStoreConfigFilter.providerPathEnv);
        configFilter.initializeWithConfiguration(configuration);
        Assert.assertEquals("filtered_env", configFilter.filter("password"));
    }

    @Test
    public void filterWithPasswordFile() {
        HashMap<String, String> configuration = new HashMap<>();
        configuration.put(HadoopCredentialStoreConfigFilter.CREDENTIAL_PROVIDER_PATH, TestHadoopCredentialStoreConfigFilter.providerPathPwdFile);
        configuration.put(HadoopCredentialStoreConfigFilter.PASSWORD_FILE_CONFIG_KEY, "test-password.txt");
        configFilter.initializeWithConfiguration(configuration);
        Assert.assertEquals("filtered_file", configFilter.filter("password"));
    }

    @Test
    public void filterWithEnvNoPassword() {
        HashMap<String, String> configuration = new HashMap<>();
        configuration.put(HadoopCredentialStoreConfigFilter.CREDENTIAL_PROVIDER_PATH, TestHadoopCredentialStoreConfigFilter.providerPathEnv);
        configFilter.initializeWithConfiguration(configuration);
        Assert.assertNull(configFilter.filter("password"));
    }

    @Test
    public void filterErrorWithPasswordFileWrongPassword() {
        HashMap<String, String> configuration = new HashMap<>();
        configuration.put(HadoopCredentialStoreConfigFilter.CREDENTIAL_PROVIDER_PATH, TestHadoopCredentialStoreConfigFilter.providerPathPwdFile);
        configuration.put(HadoopCredentialStoreConfigFilter.PASSWORD_FILE_CONFIG_KEY, "test-password2.txt");
        configFilter.initializeWithConfiguration(configuration);
        Assert.assertNull(configFilter.filter("password"));
    }

    @Test
    public void filterErrorWithPasswordFileNoPasswordFile() {
        HashMap<String, String> configuration = new HashMap<>();
        configuration.put(HadoopCredentialStoreConfigFilter.CREDENTIAL_PROVIDER_PATH, TestHadoopCredentialStoreConfigFilter.providerPathPwdFile);
        configFilter.initializeWithConfiguration(configuration);
        Assert.assertNull(configFilter.filter("password"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void filterErrorWithNoProvider() {
        HashMap<String, String> configuration = new HashMap<>();
        configFilter.initializeWithConfiguration(configuration);
    }
}

