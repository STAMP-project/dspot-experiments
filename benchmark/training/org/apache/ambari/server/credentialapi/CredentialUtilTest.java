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
package org.apache.ambari.server.credentialapi;


import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.util.ToolRunner;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


/**
 * Tests CredentialUtilTest.
 */
public class CredentialUtilTest {
    /**
     * Cleans up itself after a test method is run.
     */
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    /**
     * Redirect System.out to a stream. CredentialShell() writes to System.out.
     * We want to capture that.
     */
    private final ByteArrayOutputStream out = new ByteArrayOutputStream();

    /**
     * Redirect System.err to a stream.
     */
    private final ByteArrayOutputStream err = new ByteArrayOutputStream();

    /**
     * CRUD command verbs
     */
    private static final String CREATE_VERB = "create";

    private static final String DELETE_VERB = "delete";

    private static final String LIST_VERB = "list";

    private static final String GET_VERB = "get";

    /**
     * Creates a non-existing credential
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testCreateCommand() throws Exception {
        String alias = "javax.jdo.option.ConnectionPassword";
        String credential = "MyTopSecretPassword";
        String providerPath = getProviderPath("CreateCommandTest.jceks");
        String[] args = getCreateArgs(alias, credential, providerPath);
        int exitCode = executeCommand(args);
        Assert.assertEquals(exitCode, 0);
    }

    /**
     * Overwrites an existing credential.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testCreateCommandOverwriteExisting() throws Exception {
        String alias = "javax.jdo.option.ConnectionPassword";
        String credential = "MyTopSecretPassword";
        String providerPath = getProviderPath("CreateCommandTest.jceks");
        String[] args;
        int exitCode;
        /* Create a new credential */
        args = getCreateArgs(alias, credential, providerPath);
        exitCode = ToolRunner.run(new Configuration(), new CredentialUtil(), args);
        Assert.assertEquals(exitCode, 0);
        /* Update the created credential */
        credential = "MyUpdatedTopSecretPassword";
        args = getUpdateArgs(alias, credential, providerPath);
        exitCode = executeCommand(args);
        Assert.assertEquals(exitCode, 0);
    }

    /**
     * Updates a non-existing credential. Should create it.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testCreateCommandOverwriteNonExisting() throws Exception {
        String alias = "javax.jdo.option.ConnectionPassword";
        String credential = "MyTopSecretPassword";
        String providerPath = getProviderPath("CreateCommandTest.jceks");
        String[] args;
        int exitCode;
        /* Update a non-existing credential. Should create it. */
        args = getUpdateArgs(alias, credential, providerPath);
        exitCode = executeCommand(args);
        Assert.assertEquals(exitCode, 0);
    }

    /**
     * Safely creates a credential. If credential already exists, nothing is done.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testSafeCreateCommandExisting() throws Exception {
        String alias = "javax.jdo.option.ConnectionPassword";
        String credential = "MyTopSecretPassword";
        String providerPath = getProviderPath("CreateCommandTest.jceks");
        String[] args;
        int exitCode;
        /* Create a new credential */
        args = getCreateArgs(alias, credential, providerPath);
        exitCode = ToolRunner.run(new Configuration(), new CredentialUtil(), args);
        Assert.assertEquals(exitCode, 0);
        /* Safely update the previously created credential. Nothing is done. */
        credential = "AnotherTopSecretPassword";
        args = getSafeCreateArgs(alias, credential, providerPath);
        exitCode = executeCommand(args);
        Assert.assertEquals(exitCode, 0);
    }

    /**
     * Safely creates a credential. If it does not exist, it will be created.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testSafeCreateCommandNotExisting() throws Exception {
        String alias = "javax.jdo.option.ConnectionPassword";
        String credential = "MyTopSecretPassword";
        String providerPath = getProviderPath("CreateCommandTest.jceks");
        String[] args;
        int exitCode;
        /* Safely update a non-existing credential. Should create the credential. */
        credential = "AnotherTopSecretPassword";
        args = getSafeCreateArgs(alias, credential, providerPath);
        exitCode = executeCommand(args);
        Assert.assertEquals(exitCode, 0);
    }

    /**
     * Delete an existing credential
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testDeleteCommandExisting() throws Exception {
        String alias = "javax.jdo.option.ConnectionPassword";
        String credential = "MyTopSecretPassword";
        String providerPath = getProviderPath("CreateCommandTest.jceks");
        String[] args;
        int exitCode;
        /* Create a new credential */
        args = getCreateArgs(alias, credential, providerPath);
        exitCode = executeCommand(args);
        Assert.assertEquals(exitCode, 0);
        /* Delete the above credential */
        args = getDeleteArgs(alias, providerPath);
        exitCode = executeCommand(args);
        Assert.assertEquals(exitCode, 0);
    }

    /**
     * Delete a non-existing credential
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testDeleteCommandNonExisting() throws Exception {
        String alias = "javax.jdo.option.ConnectionPassword";
        String providerPath = getProviderPath("CreateCommandTest.jceks");
        String[] args;
        int exitCode;
        /* Delete a non-existing credential. Should fail with exit code 1. */
        args = getDeleteArgs(alias, providerPath);
        exitCode = executeCommand(args);
        Assert.assertEquals(exitCode, 1);
    }

    /**
     * Retrieve an existing credential.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testGetCommandExisting() throws Exception {
        String alias = "javax.jdo.option.ConnectionPassword";
        String credential = "MyTopSecretPassword";
        String providerPath = getProviderPath("CreateCommandTest.jceks");
        String[] args;
        int exitCode;
        /* Create a new credential */
        args = getCreateArgs(alias, credential, providerPath);
        exitCode = executeCommand(args);
        Assert.assertEquals(exitCode, 0);
        out.reset();
        /* Get the existing credential. */
        args = getGetArgs(alias, providerPath);
        exitCode = executeCommand(args);
        Assert.assertEquals(exitCode, 0);
        String retrievedCredential = out.toString().trim();
        Assert.assertEquals(credential, retrievedCredential);
    }

    /**
     * Retrieve a non-existing credential.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testGetCommandNonExisting() throws Exception {
        String alias = "javax.jdo.option.ConnectionPassword";
        String providerPath = getProviderPath("CreateCommandTest.jceks");
        String[] args;
        int exitCode;
        /* Get a non-existing credential. Should fail with exit code 1. */
        args = getGetArgs(alias, providerPath);
        exitCode = executeCommand(args);
        Assert.assertEquals(exitCode, 1);
    }

    /**
     * Create, delete and attempt to get the alias.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testGetCommandAfterDeletion() throws Exception {
        String alias = "javax.jdo.option.ConnectionPassword";
        String credential = "MyTopSecretPassword";
        String providerPath = getProviderPath("CreateCommandTest.jceks");
        String[] args;
        int exitCode;
        /* Create a new credential */
        args = getCreateArgs(alias, credential, providerPath);
        exitCode = executeCommand(args);
        Assert.assertEquals(exitCode, 0);
        /* Delete the above credential */
        args = getDeleteArgs(alias, providerPath);
        exitCode = executeCommand(args);
        Assert.assertEquals(exitCode, 0);
        /* Get the existing credential. Should not be there. */
        args = getGetArgs(alias, providerPath);
        exitCode = executeCommand(args);
        Assert.assertEquals(exitCode, 1);
    }

    /**
     * Execute get on an invalid provider path.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testGetCommandWithNonExistingProvider() throws Exception {
        String alias = "javax.jdo.option.ConnectionPassword";
        String credential = "MyTopSecretPassword";
        String providerPath = getProviderPath("CreateCommandTest.jceks");
        String[] args;
        int exitCode;
        /* Create a new credential */
        args = getCreateArgs(alias, credential, providerPath);
        exitCode = executeCommand(args);
        Assert.assertEquals(exitCode, 0);
        /* Get a credential. Should not be there. */
        args = getGetArgs(alias, "BadProvider.jceks");
        exitCode = executeCommand(args);
        Assert.assertEquals(exitCode, 1);
    }

    /**
     * List all aliases.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testListCommand() throws Exception {
        String providerPath = getProviderPath("CreateCommandTest.jceks");
        String[] args;
        int exitCode;
        final int numEntries = 5;
        Properties properties = new Properties();
        /* Create some alias password entries. */
        for (int i = 0; i < numEntries; ++i) {
            String alias = String.format("alias_%d", (i + 1));
            String credential = String.format("credential_%d", (i + 1));
            properties.setProperty(alias, credential);
            args = getCreateArgs(alias, credential, providerPath);
            exitCode = executeCommand(args);
            Assert.assertEquals(exitCode, 0);
        }
        out.reset();
        /* List all aliases. */
        args = getListArgs(providerPath);
        exitCode = executeCommand(args);
        Assert.assertEquals(exitCode, 0);
        List<String> aliases = Arrays.asList(out.toString().split(System.getProperty("line.separator")));
        Enumeration enumeration = properties.propertyNames();
        while (enumeration.hasMoreElements()) {
            String alias = ((String) (enumeration.nextElement()));
            Assert.assertTrue(aliases.contains(alias));
        } 
    }

    /**
     * Prints the tool usage.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testToolUsage() throws Exception {
        String[] args = new String[1];
        int exitCode;
        /* Invoke tool help */
        args[0] = "-help";
        exitCode = executeCommand(args);
        Assert.assertEquals(exitCode, 0);
        Assert.assertTrue((!(out.toString().isEmpty())));
    }

    /**
     * Invoke Create command help
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testCreateCommandUsage() throws Exception {
        String[] args = new String[2];
        int exitCode;
        args[0] = CredentialUtilTest.CREATE_VERB;
        args[1] = "-help";
        exitCode = executeCommand(args);
        Assert.assertEquals(exitCode, 0);
        Assert.assertTrue((!(out.toString().isEmpty())));
    }

    /* Invoke Delete command help

    @throws Exception
     */
    @Test
    public void testDeleteCommandUsage() throws Exception {
        String[] args = new String[2];
        int exitCode;
        args[0] = CredentialUtilTest.DELETE_VERB;
        args[1] = "-help";
        exitCode = executeCommand(args);
        Assert.assertEquals(exitCode, 0);
        Assert.assertTrue((!(out.toString().isEmpty())));
    }

    /* Invoke List command help

    @throws Exception
     */
    @Test
    public void testListCommandUsage() throws Exception {
        String[] args = new String[2];
        int exitCode;
        args[0] = CredentialUtilTest.LIST_VERB;
        args[1] = "-help";
        exitCode = executeCommand(args);
        Assert.assertEquals(exitCode, 0);
        Assert.assertTrue((!(out.toString().isEmpty())));
    }

    /* Invoke Get command help

    @throws Exception
     */
    @Test
    public void testGetCommandUsage() throws Exception {
        String[] args = new String[2];
        int exitCode;
        args[0] = CredentialUtilTest.GET_VERB;
        args[1] = "-help";
        exitCode = executeCommand(args);
        Assert.assertEquals(exitCode, 0);
        Assert.assertTrue((!(out.toString().isEmpty())));
    }
}

