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
package org.apache.hadoop.crypto.key;


import KeyProviderFactory.KEY_PROVIDER_PATH;
import KeyShell.NO_VALID_PROVIDERS;
import ProviderUtils.NO_PASSWORD_CONT;
import ProviderUtils.NO_PASSWORD_ERROR;
import ProviderUtils.NO_PASSWORD_INSTRUCTIONS_DOC;
import ProviderUtils.NO_PASSWORD_WARN;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.apache.hadoop.conf.Configuration;
import org.junit.Assert;
import org.junit.Test;


public class TestKeyShell {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();

    private PrintStream initialStdOut;

    private PrintStream initialStdErr;

    /* The default JCEKS provider - for testing purposes */
    private String jceksProvider;

    @Test
    public void testKeySuccessfulKeyLifecycle() throws Exception {
        int rc = 0;
        String keyName = "key1";
        KeyShell ks = new KeyShell();
        ks.setConf(new Configuration());
        outContent.reset();
        final String[] args1 = new String[]{ "create", keyName, "-provider", jceksProvider };
        rc = ks.run(args1);
        Assert.assertEquals(0, rc);
        Assert.assertTrue(outContent.toString().contains(((keyName + " has been ") + "successfully created")));
        Assert.assertTrue(outContent.toString().contains(NO_PASSWORD_WARN));
        Assert.assertTrue(outContent.toString().contains(NO_PASSWORD_INSTRUCTIONS_DOC));
        Assert.assertTrue(outContent.toString().contains(NO_PASSWORD_CONT));
        String listOut = listKeys(ks, false);
        Assert.assertTrue(listOut.contains(keyName));
        listOut = listKeys(ks, true);
        Assert.assertTrue(listOut.contains(keyName));
        Assert.assertTrue(listOut.contains("description"));
        Assert.assertTrue(listOut.contains("created"));
        outContent.reset();
        final String[] args2 = new String[]{ "roll", keyName, "-provider", jceksProvider };
        rc = ks.run(args2);
        Assert.assertEquals(0, rc);
        Assert.assertTrue(outContent.toString().contains(("key1 has been successfully " + "rolled.")));
        // jceks provider's invalidate is a no-op.
        outContent.reset();
        final String[] args3 = new String[]{ "invalidateCache", keyName, "-provider", jceksProvider };
        rc = ks.run(args3);
        Assert.assertEquals(0, rc);
        Assert.assertTrue(outContent.toString().contains(("key1 has been successfully " + "invalidated.")));
        deleteKey(ks, keyName);
        listOut = listKeys(ks, false);
        Assert.assertFalse(listOut, listOut.contains(keyName));
    }

    /* HADOOP-10586 KeyShell didn't allow -description. */
    @Test
    public void testKeySuccessfulCreationWithDescription() throws Exception {
        outContent.reset();
        final String[] args1 = new String[]{ "create", "key1", "-provider", jceksProvider, "-description", "someDescription" };
        int rc = 0;
        KeyShell ks = new KeyShell();
        ks.setConf(new Configuration());
        rc = ks.run(args1);
        Assert.assertEquals(0, rc);
        Assert.assertTrue(outContent.toString().contains(("key1 has been successfully " + "created")));
        String listOut = listKeys(ks, true);
        Assert.assertTrue(listOut.contains("description"));
        Assert.assertTrue(listOut.contains("someDescription"));
    }

    @Test
    public void testInvalidKeySize() throws Exception {
        final String[] args1 = new String[]{ "create", "key1", "-size", "56", "-provider", jceksProvider };
        int rc = 0;
        KeyShell ks = new KeyShell();
        ks.setConf(new Configuration());
        rc = ks.run(args1);
        Assert.assertEquals(1, rc);
        Assert.assertTrue(outContent.toString().contains("key1 has not been created."));
    }

    @Test
    public void testInvalidCipher() throws Exception {
        final String[] args1 = new String[]{ "create", "key1", "-cipher", "LJM", "-provider", jceksProvider };
        int rc = 0;
        KeyShell ks = new KeyShell();
        ks.setConf(new Configuration());
        rc = ks.run(args1);
        Assert.assertEquals(1, rc);
        Assert.assertTrue(outContent.toString().contains("key1 has not been created."));
    }

    @Test
    public void testInvalidProvider() throws Exception {
        final String[] args1 = new String[]{ "create", "key1", "-cipher", "AES", "-provider", "sdff://file/tmp/keystore.jceks" };
        int rc = 0;
        KeyShell ks = new KeyShell();
        ks.setConf(new Configuration());
        rc = ks.run(args1);
        Assert.assertEquals(1, rc);
        Assert.assertTrue(outContent.toString().contains(NO_VALID_PROVIDERS));
    }

    @Test
    public void testTransientProviderWarning() throws Exception {
        final String[] args1 = new String[]{ "create", "key1", "-cipher", "AES", "-provider", "user:///" };
        int rc = 0;
        KeyShell ks = new KeyShell();
        ks.setConf(new Configuration());
        rc = ks.run(args1);
        Assert.assertEquals(0, rc);
        Assert.assertTrue(outContent.toString().contains(("WARNING: you are modifying a " + "transient provider.")));
    }

    @Test
    public void testTransientProviderOnlyConfig() throws Exception {
        final String[] args1 = new String[]{ "create", "key1" };
        int rc = 0;
        KeyShell ks = new KeyShell();
        Configuration config = new Configuration();
        config.set(KEY_PROVIDER_PATH, "user:///");
        ks.setConf(config);
        rc = ks.run(args1);
        Assert.assertEquals(1, rc);
        Assert.assertTrue(outContent.toString().contains(NO_VALID_PROVIDERS));
    }

    @Test
    public void testStrict() throws Exception {
        outContent.reset();
        int rc = 0;
        KeyShell ks = new KeyShell();
        ks.setConf(new Configuration());
        final String[] args1 = new String[]{ "create", "hello", "-provider", jceksProvider, "-strict" };
        rc = ks.run(args1);
        Assert.assertEquals(1, rc);
        Assert.assertTrue(outContent.toString().contains(NO_PASSWORD_ERROR));
        Assert.assertTrue(outContent.toString().contains(NO_PASSWORD_INSTRUCTIONS_DOC));
    }

    @Test
    public void testFullCipher() throws Exception {
        final String keyName = "key1";
        final String[] args1 = new String[]{ "create", keyName, "-cipher", "AES/CBC/pkcs5Padding", "-provider", jceksProvider };
        int rc = 0;
        KeyShell ks = new KeyShell();
        ks.setConf(new Configuration());
        rc = ks.run(args1);
        Assert.assertEquals(0, rc);
        Assert.assertTrue(outContent.toString().contains(((keyName + " has been ") + "successfully created")));
        deleteKey(ks, keyName);
    }

    @Test
    public void testAttributes() throws Exception {
        int rc;
        KeyShell ks = new KeyShell();
        ks.setConf(new Configuration());
        /* Simple creation test */
        final String[] args1 = new String[]{ "create", "keyattr1", "-provider", jceksProvider, "-attr", "foo=bar" };
        rc = ks.run(args1);
        Assert.assertEquals(0, rc);
        Assert.assertTrue(outContent.toString().contains(("keyattr1 has been " + "successfully created")));
        /* ...and list to see that we have the attr */
        String listOut = listKeys(ks, true);
        Assert.assertTrue(listOut.contains("keyattr1"));
        Assert.assertTrue(listOut.contains("attributes: [foo=bar]"));
        /* Negative tests: no attribute */
        outContent.reset();
        final String[] args2 = new String[]{ "create", "keyattr2", "-provider", jceksProvider, "-attr", "=bar" };
        rc = ks.run(args2);
        Assert.assertEquals(1, rc);
        /* Not in attribute = value form */
        outContent.reset();
        args2[5] = "foo";
        rc = ks.run(args2);
        Assert.assertEquals(1, rc);
        /* No attribute or value */
        outContent.reset();
        args2[5] = "=";
        rc = ks.run(args2);
        Assert.assertEquals(1, rc);
        /* Legal: attribute is a, value is b=c */
        outContent.reset();
        args2[5] = "a=b=c";
        rc = ks.run(args2);
        Assert.assertEquals(0, rc);
        listOut = listKeys(ks, true);
        Assert.assertTrue(listOut.contains("keyattr2"));
        Assert.assertTrue(listOut.contains("attributes: [a=b=c]"));
        /* Test several attrs together... */
        outContent.reset();
        final String[] args3 = new String[]{ "create", "keyattr3", "-provider", jceksProvider, "-attr", "foo = bar", "-attr", " glarch =baz  ", "-attr", "abc=def" };
        rc = ks.run(args3);
        Assert.assertEquals(0, rc);
        /* ...and list to ensure they're there. */
        listOut = listKeys(ks, true);
        Assert.assertTrue(listOut.contains("keyattr3"));
        Assert.assertTrue(listOut.contains("[foo=bar]"));
        Assert.assertTrue(listOut.contains("[glarch=baz]"));
        Assert.assertTrue(listOut.contains("[abc=def]"));
        /* Negative test - repeated attributes should fail */
        outContent.reset();
        final String[] args4 = new String[]{ "create", "keyattr4", "-provider", jceksProvider, "-attr", "foo=bar", "-attr", "foo=glarch" };
        rc = ks.run(args4);
        Assert.assertEquals(1, rc);
        /* Clean up to be a good citizen */
        deleteKey(ks, "keyattr1");
        deleteKey(ks, "keyattr2");
        deleteKey(ks, "keyattr3");
    }
}

