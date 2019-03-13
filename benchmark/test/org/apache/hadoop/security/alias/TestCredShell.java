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
package org.apache.hadoop.security.alias;


import CredentialProviderFactory.CREDENTIAL_PROVIDER_PATH;
import CredentialShell.NO_VALID_PROVIDERS;
import CredentialShell.PasswordReader;
import ProviderUtils.NO_PASSWORD_CONT;
import ProviderUtils.NO_PASSWORD_ERROR;
import ProviderUtils.NO_PASSWORD_INSTRUCTIONS_DOC;
import ProviderUtils.NO_PASSWORD_WARN;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.test.GenericTestUtils;
import org.junit.Assert;
import org.junit.Test;


public class TestCredShell {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();

    private static final File tmpDir = GenericTestUtils.getTestDir("creds");

    /* The default JCEKS provider - for testing purposes */
    private String jceksProvider;

    @Test
    public void testCredentialSuccessfulLifecycle() throws Exception {
        outContent.reset();
        String[] args1 = new String[]{ "create", "credential1", "-value", "p@ssw0rd", "-provider", jceksProvider };
        int rc = 0;
        CredentialShell cs = new CredentialShell();
        cs.setConf(new Configuration());
        rc = cs.run(args1);
        Assert.assertEquals(outContent.toString(), 0, rc);
        Assert.assertTrue(outContent.toString().contains(("credential1 has been successfully " + "created.")));
        Assert.assertTrue(outContent.toString().contains(NO_PASSWORD_WARN));
        Assert.assertTrue(outContent.toString().contains(NO_PASSWORD_INSTRUCTIONS_DOC));
        Assert.assertTrue(outContent.toString().contains(NO_PASSWORD_CONT));
        outContent.reset();
        String[] args2 = new String[]{ "list", "-provider", jceksProvider };
        rc = cs.run(args2);
        Assert.assertEquals(0, rc);
        Assert.assertTrue(outContent.toString().contains("credential1"));
        outContent.reset();
        String[] args4 = new String[]{ "delete", "credential1", "-f", "-provider", jceksProvider };
        rc = cs.run(args4);
        Assert.assertEquals(0, rc);
        Assert.assertTrue(outContent.toString().contains(("credential1 has been successfully " + "deleted.")));
        outContent.reset();
        String[] args5 = new String[]{ "list", "-provider", jceksProvider };
        rc = cs.run(args5);
        Assert.assertEquals(0, rc);
        Assert.assertFalse(outContent.toString(), outContent.toString().contains("credential1"));
    }

    @Test
    public void testInvalidProvider() throws Exception {
        String[] args1 = new String[]{ "create", "credential1", "-value", "p@ssw0rd", "-provider", "sdff://file/tmp/credstore.jceks" };
        int rc = 0;
        CredentialShell cs = new CredentialShell();
        cs.setConf(new Configuration());
        rc = cs.run(args1);
        Assert.assertEquals(1, rc);
        Assert.assertTrue(outContent.toString().contains(NO_VALID_PROVIDERS));
    }

    @Test
    public void testTransientProviderWarning() throws Exception {
        String[] args1 = new String[]{ "create", "credential1", "-value", "p@ssw0rd", "-provider", "user:///" };
        int rc = 0;
        CredentialShell cs = new CredentialShell();
        cs.setConf(new Configuration());
        rc = cs.run(args1);
        Assert.assertEquals(outContent.toString(), 0, rc);
        Assert.assertTrue(outContent.toString().contains(("WARNING: you are modifying a " + "transient provider.")));
        String[] args2 = new String[]{ "delete", "credential1", "-f", "-provider", "user:///" };
        rc = cs.run(args2);
        Assert.assertEquals(outContent.toString(), 0, rc);
        Assert.assertTrue(outContent.toString().contains(("credential1 has been successfully " + "deleted.")));
    }

    @Test
    public void testTransientProviderOnlyConfig() throws Exception {
        String[] args1 = new String[]{ "create", "credential1" };
        int rc = 0;
        CredentialShell cs = new CredentialShell();
        Configuration config = new Configuration();
        config.set(CREDENTIAL_PROVIDER_PATH, "user:///");
        cs.setConf(config);
        rc = cs.run(args1);
        Assert.assertEquals(1, rc);
        Assert.assertTrue(outContent.toString().contains(NO_VALID_PROVIDERS));
    }

    @Test
    public void testPromptForCredentialWithEmptyPasswd() throws Exception {
        String[] args1 = new String[]{ "create", "credential1", "-provider", jceksProvider };
        ArrayList<String> passwords = new ArrayList<String>();
        passwords.add(null);
        passwords.add("p@ssw0rd");
        int rc = 0;
        CredentialShell shell = new CredentialShell();
        shell.setConf(new Configuration());
        shell.setPasswordReader(new TestCredShell.MockPasswordReader(passwords));
        rc = shell.run(args1);
        Assert.assertEquals(outContent.toString(), 1, rc);
        Assert.assertTrue(outContent.toString().contains("Passwords don't match"));
    }

    @Test
    public void testPromptForCredential() throws Exception {
        String[] args1 = new String[]{ "create", "credential1", "-provider", jceksProvider };
        ArrayList<String> passwords = new ArrayList<String>();
        passwords.add("p@ssw0rd");
        passwords.add("p@ssw0rd");
        int rc = 0;
        CredentialShell shell = new CredentialShell();
        shell.setConf(new Configuration());
        shell.setPasswordReader(new TestCredShell.MockPasswordReader(passwords));
        rc = shell.run(args1);
        Assert.assertEquals(0, rc);
        Assert.assertTrue(outContent.toString().contains(("credential1 has been successfully " + "created.")));
        String[] args2 = new String[]{ "delete", "credential1", "-f", "-provider", jceksProvider };
        rc = shell.run(args2);
        Assert.assertEquals(0, rc);
        Assert.assertTrue(outContent.toString().contains(("credential1 has been successfully " + "deleted.")));
    }

    public class MockPasswordReader extends CredentialShell.PasswordReader {
        List<String> passwords = null;

        public MockPasswordReader(List<String> passwds) {
            passwords = passwds;
        }

        @Override
        public char[] readPassword(String prompt) {
            if ((passwords.size()) == 0)
                return null;

            String pass = passwords.remove(0);
            return pass == null ? null : pass.toCharArray();
        }

        @Override
        public void format(String message) {
            System.out.println(message);
        }
    }

    @Test
    public void testEmptyArgList() throws Exception {
        CredentialShell shell = new CredentialShell();
        shell.setConf(new Configuration());
        Assert.assertEquals(1, shell.init(new String[0]));
    }

    @Test
    public void testCommandHelpExitsNormally() throws Exception {
        for (String cmd : Arrays.asList("create", "list", "delete")) {
            CredentialShell shell = new CredentialShell();
            shell.setConf(new Configuration());
            Assert.assertEquals((("Expected help argument on " + cmd) + " to return 0"), 0, shell.init(new String[]{ cmd, "-help" }));
        }
    }

    @Test
    public void testEmptyArgForCommands() throws Exception {
        CredentialShell shell = new CredentialShell();
        String[] command = new String[]{ "list", "-provider" };
        Assert.assertEquals((("Expected empty argument on " + command) + " to return 1"), 1, shell.init(command));
        for (String cmd : Arrays.asList("create", "delete")) {
            shell.setConf(new Configuration());
            Assert.assertEquals((("Expected empty argument on " + cmd) + " to return 1"), 1, shell.init(new String[]{ cmd }));
        }
    }

    @Test
    public void testStrict() throws Exception {
        outContent.reset();
        String[] args1 = new String[]{ "create", "credential1", "-value", "p@ssw0rd", "-provider", jceksProvider, "-strict" };
        int rc = 1;
        CredentialShell cs = new CredentialShell();
        cs.setConf(new Configuration());
        rc = cs.run(args1);
        Assert.assertEquals(outContent.toString(), 1, rc);
        Assert.assertFalse(outContent.toString().contains(("credential1 has been " + "successfully created.")));
        Assert.assertTrue(outContent.toString().contains(NO_PASSWORD_ERROR));
        Assert.assertTrue(outContent.toString().contains(NO_PASSWORD_INSTRUCTIONS_DOC));
    }

    @Test
    public void testHelp() throws Exception {
        outContent.reset();
        String[] args1 = new String[]{ "-help" };
        int rc = 0;
        CredentialShell cs = new CredentialShell();
        cs.setConf(new Configuration());
        rc = cs.run(args1);
        Assert.assertEquals(outContent.toString(), 0, rc);
        Assert.assertTrue(outContent.toString().contains("Usage"));
    }

    @Test
    public void testHelpCreate() throws Exception {
        outContent.reset();
        String[] args1 = new String[]{ "create", "-help" };
        int rc = 0;
        CredentialShell cs = new CredentialShell();
        cs.setConf(new Configuration());
        rc = cs.run(args1);
        Assert.assertEquals(outContent.toString(), 0, rc);
        Assert.assertTrue(outContent.toString().contains("Usage"));
    }
}

