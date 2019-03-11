/**
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.elasticsearch.hadoop.cli;


import Keytool.Command;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.elasticsearch.hadoop.security.KeystoreWrapper;
import org.elasticsearch.hadoop.util.BytesArray;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class KeytoolTest {
    @Test
    public void executeNoCommand() {
        TestPrompt console = new TestPrompt();
        Assert.assertThat(Keytool.execute(console, new String[]{  }), Matchers.equalTo(1));
        KeytoolTest.assertHelpMessage(console.getOutput());
    }

    @Test
    public void executeNoCommandWithArgs() {
        TestPrompt console = new TestPrompt();
        Assert.assertThat(Keytool.execute(console, new String[]{ "--stdin" }), Matchers.equalTo(1));
        KeytoolTest.assertHelpMessage(console.getOutput());
    }

    @Test
    public void executeHelp() {
        TestPrompt console = new TestPrompt();
        Assert.assertThat(Keytool.execute(console, new String[]{ "-h" }), Matchers.equalTo(0));
        KeytoolTest.assertHelpMessage(console.getOutput());
    }

    @Test
    public void executeHelpExt() {
        TestPrompt console = new TestPrompt();
        Assert.assertThat(Keytool.execute(console, new String[]{ "--help" }), Matchers.equalTo(0));
        KeytoolTest.assertHelpMessage(console.getOutput());
    }

    @Test
    public void executeCommandFail() {
        TestPrompt console = new TestPrompt();
        Assert.assertThat(Keytool.execute(console, new String[]{ "blah" }), Matchers.equalTo(2));
        KeytoolTest.assertHelpMessage(console.getOutput());
    }

    @Test
    public void executeCommandFailWithHelp() {
        TestPrompt console = new TestPrompt();
        Assert.assertThat(Keytool.execute(console, new String[]{ "blah", "--help" }), Matchers.equalTo(2));
        KeytoolTest.assertHelpMessage(console.getOutput());
    }

    @Test
    public void executeAddFailNoArgument() {
        TestPrompt console = new TestPrompt();
        Assert.assertThat(Keytool.execute(console, new String[]{ "add" }), Matchers.equalTo(4));
        KeytoolTest.assertHelpMessage(console.getOutput());
    }

    @Test
    public void executeAddFailNoSettingsName() {
        TestPrompt console = new TestPrompt();
        Assert.assertThat(Keytool.execute(console, new String[]{ "add", "--stdin" }), Matchers.equalTo(4));
        KeytoolTest.assertHelpMessage(console.getOutput());
    }

    @Test
    public void executeAddFailUnknownArgument() {
        TestPrompt console = new TestPrompt();
        Assert.assertThat(Keytool.execute(console, new String[]{ "add", "--stdin", "property.name", "someOtherTHing" }), Matchers.equalTo(3));
        KeytoolTest.assertHelpMessage(console.getOutput());
    }

    @Test
    public void executeAdd() {
        TestPrompt console = new TestPrompt();
        Assert.assertThat(Keytool.execute(console, new String[]{ "add", "--stdin", "property.name", "someOtherTHing" }), Matchers.equalTo(3));
        KeytoolTest.assertHelpMessage(console.getOutput());
    }

    @Test
    public void executeListFailUnknownArgument() {
        TestPrompt console = new TestPrompt();
        Assert.assertThat(Keytool.execute(console, new String[]{ "list", "property.name" }), Matchers.equalTo(3));
        KeytoolTest.assertHelpMessage(console.getOutput());
    }

    private static final String HELP = "A tool for managing settings stored in an ES-Hadoop keystore\n" + ((((((((((("\n" + "Commands\n") + "--------\n") + "create - Creates a new elasticsearch keystore\n") + "list - List entries in the keystore\n") + "add - Add a setting to the keystore\n") + "remove - Remove a setting from the keystore\n") + "\n") + "Option         Description        \n") + "------         -----------        \n") + "-h, --help     show help          \n") + "-f, --force    ignore overwriting warnings when adding to the keystore");

    private static class KeytoolHarness extends Keytool {
        private boolean exists;

        private BytesArray fileBytes;

        KeytoolHarness(Prompt prompt, Keytool.Command command, boolean exists, BytesArray fileBytes) {
            super(prompt, command);
            this.exists = exists;
            this.fileBytes = fileBytes;
        }

        @Override
        protected InputStream openRead() throws FileNotFoundException {
            return new org.elasticsearch.hadoop.util.FastByteArrayInputStream(fileBytes);
        }

        @Override
        protected OutputStream openWrite() throws IOException {
            this.exists = true;
            this.fileBytes.reset();
            return new org.elasticsearch.hadoop.util.FastByteArrayOutputStream(fileBytes);
        }

        @Override
        protected boolean ksExists() {
            return exists;
        }

        public BytesArray getFileBytes() {
            return fileBytes;
        }
    }

    @Test
    public void createKeystore() throws Exception {
        TestPrompt console = new TestPrompt();
        KeytoolTest.KeytoolHarness keytool = new KeytoolTest.KeytoolHarness(console, Command.CREATE, false, new BytesArray(128));
        Assert.assertThat(keytool.run(null, false, false), Matchers.equalTo(0));
        Assert.assertThat(console.getOutput(), Matchers.equalTo(""));
        Assert.assertThat(console.hasInputLeft(), Matchers.is(false));
        Assert.assertThat(keytool.ksExists(), Matchers.is(true));
        Assert.assertThat(keytool.fileBytes.length(), Matchers.is(Matchers.not(0)));
    }

    @Test
    public void createKeystoreExistsAlreadyOverwrite() throws Exception {
        TestPrompt console = new TestPrompt();
        console.addInput("y");
        KeytoolTest.KeytoolHarness keytool = new KeytoolTest.KeytoolHarness(console, Command.CREATE, true, new BytesArray(128));
        Assert.assertThat(keytool.run(null, false, false), Matchers.equalTo(0));
        Assert.assertThat(console.getOutput(), Matchers.equalTo(""));
        Assert.assertThat(console.hasInputLeft(), Matchers.is(false));
        Assert.assertThat(keytool.ksExists(), Matchers.is(true));
        Assert.assertThat(keytool.fileBytes.length(), Matchers.is(Matchers.not(0)));
    }

    @Test
    public void createKeystoreExistsAlreadyCancel() throws Exception {
        TestPrompt console = new TestPrompt();
        console.addInput("n");
        KeytoolTest.KeytoolHarness keytool = new KeytoolTest.KeytoolHarness(console, Command.CREATE, true, new BytesArray(128));
        Assert.assertThat(keytool.run(null, false, false), Matchers.equalTo(0));
        Assert.assertThat(console.getOutput(), Matchers.equalTo("Exiting without creating keystore\n"));
        Assert.assertThat(console.hasInputLeft(), Matchers.is(false));
        Assert.assertThat(keytool.ksExists(), Matchers.is(true));
        Assert.assertThat(keytool.fileBytes.length(), Matchers.is(0));
    }

    @Test
    public void createKeystoreExistsAlreadyCancelAfterGarbage() throws Exception {
        TestPrompt console = new TestPrompt();
        console.addInput("nope").addInput("yup").addInput("blahblahblah").addInput("n");
        KeytoolTest.KeytoolHarness keytool = new KeytoolTest.KeytoolHarness(console, Command.CREATE, true, new BytesArray(128));
        Assert.assertThat(keytool.run(null, false, false), Matchers.equalTo(0));
        Assert.assertThat(console.getOutput(), Matchers.equalTo(("Did not understand answer \'nope\'\n" + (("Did not understand answer \'yup\'\n" + "Did not understand answer \'blahblahblah\'\n") + "Exiting without creating keystore\n"))));
        Assert.assertThat(console.hasInputLeft(), Matchers.is(false));
        Assert.assertThat(keytool.ksExists(), Matchers.is(true));
        Assert.assertThat(keytool.fileBytes.length(), Matchers.is(0));
    }

    @Test
    public void listKeystoreNonExistant() throws Exception {
        TestPrompt console = new TestPrompt();
        KeytoolTest.KeytoolHarness keytool = new KeytoolTest.KeytoolHarness(console, Command.LIST, false, new BytesArray(128));
        Assert.assertThat(keytool.run(null, false, false), Matchers.equalTo(5));
        Assert.assertThat(console.getOutput(), Matchers.equalTo("ERROR: ES-Hadoop keystore not found. Use \'create\' command to create one.\n"));
        Assert.assertThat(console.hasInputLeft(), Matchers.is(false));
        Assert.assertThat(keytool.ksExists(), Matchers.is(false));
        Assert.assertThat(keytool.fileBytes.length(), Matchers.is(0));
    }

    @Test
    public void listKeystoreEmpty() throws Exception {
        BytesArray storeData = new BytesArray(128);
        KeystoreWrapper.newStore().build().saveKeystore(new org.elasticsearch.hadoop.util.FastByteArrayOutputStream(storeData));
        TestPrompt console = new TestPrompt();
        KeytoolTest.KeytoolHarness keytool = new KeytoolTest.KeytoolHarness(console, Command.LIST, true, storeData);
        Assert.assertThat(keytool.run(null, false, false), Matchers.equalTo(0));
        Assert.assertThat(console.getOutput(), Matchers.equalTo(""));
        Assert.assertThat(console.hasInputLeft(), Matchers.is(false));
        Assert.assertThat(keytool.ksExists(), Matchers.is(true));
        Assert.assertThat(keytool.fileBytes.length(), Matchers.is(Matchers.not(0)));
    }

    @Test
    public void listKeystore() throws Exception {
        BytesArray storeData = new BytesArray(128);
        KeystoreWrapper ks = KeystoreWrapper.newStore().build();
        ks.setSecureSetting("test.password.1", "blah");
        ks.setSecureSetting("test.password.2", "blah");
        ks.saveKeystore(new org.elasticsearch.hadoop.util.FastByteArrayOutputStream(storeData));
        TestPrompt console = new TestPrompt();
        KeytoolTest.KeytoolHarness keytool = new KeytoolTest.KeytoolHarness(console, Command.LIST, true, storeData);
        Assert.assertThat(keytool.run(null, false, false), Matchers.equalTo(0));
        Assert.assertThat(console.getOutput(), Matchers.equalTo(("test.password.1\n" + "test.password.2\n")));
        Assert.assertThat(console.hasInputLeft(), Matchers.is(false));
        Assert.assertThat(keytool.ksExists(), Matchers.is(true));
        Assert.assertThat(keytool.fileBytes.length(), Matchers.is(Matchers.not(0)));
    }

    @Test
    public void addNonExistant() throws Exception {
        TestPrompt console = new TestPrompt();
        console.addInput("blah");
        KeytoolTest.KeytoolHarness keytool = new KeytoolTest.KeytoolHarness(console, Command.ADD, false, new BytesArray(128));
        Assert.assertThat(run("test.password.1", false, false), Matchers.equalTo(5));
        Assert.assertThat(console.getOutput(), Matchers.equalTo("ERROR: ES-Hadoop keystore not found. Use \'create\' command to create one.\n"));
        Assert.assertThat(console.hasInputLeft(), Matchers.is(true));
        Assert.assertThat(keytool.ksExists(), Matchers.is(false));
        Assert.assertThat(keytool.fileBytes.length(), Matchers.is(0));
    }

    @Test
    public void addExistingKeyCancel() throws Exception {
        BytesArray storeData = new BytesArray(128);
        KeystoreWrapper ks = KeystoreWrapper.newStore().build();
        ks.setSecureSetting("test.password.1", "blah");
        ks.saveKeystore(new org.elasticsearch.hadoop.util.FastByteArrayOutputStream(storeData));
        TestPrompt console = new TestPrompt();
        console.addInput("n");
        KeytoolTest.KeytoolHarness keytool = new KeytoolTest.KeytoolHarness(console, Command.ADD, true, storeData);
        Assert.assertThat(run("test.password.1", false, false), Matchers.equalTo(0));
        Assert.assertThat(console.getOutput(), Matchers.equalTo("Exiting without modifying keystore\n"));
        Assert.assertThat(console.hasInputLeft(), Matchers.is(false));
        Assert.assertThat(keytool.ksExists(), Matchers.is(true));
        Assert.assertThat(keytool.fileBytes.length(), Matchers.is(Matchers.not(0)));
    }

    @Test
    public void addExistingKeyOverwrite() throws Exception {
        BytesArray storeData = new BytesArray(128);
        KeystoreWrapper ks = KeystoreWrapper.newStore().build();
        ks.setSecureSetting("test.password.1", "blah");
        ks.saveKeystore(new org.elasticsearch.hadoop.util.FastByteArrayOutputStream(storeData));
        TestPrompt console = new TestPrompt();
        console.addInput("y").addInput("blerb");
        KeytoolTest.KeytoolHarness keytool = new KeytoolTest.KeytoolHarness(console, Command.ADD, true, storeData);
        Assert.assertThat(run("test.password.1", false, false), Matchers.equalTo(0));
        Assert.assertThat(console.getOutput(), Matchers.equalTo(""));
        Assert.assertThat(console.hasInputLeft(), Matchers.is(false));
        Assert.assertThat(keytool.ksExists(), Matchers.is(true));
        Assert.assertThat(keytool.fileBytes.length(), Matchers.is(Matchers.not(0)));
        ks = KeystoreWrapper.loadStore(new org.elasticsearch.hadoop.util.FastByteArrayInputStream(keytool.fileBytes)).build();
        Assert.assertThat(ks.getSecureSetting("test.password.1"), Matchers.equalTo("blerb"));
    }

    @Test
    public void addExistingKeyForce() throws Exception {
        BytesArray storeData = new BytesArray(128);
        KeystoreWrapper ks = KeystoreWrapper.newStore().build();
        ks.setSecureSetting("test.password.1", "blah");
        ks.saveKeystore(new org.elasticsearch.hadoop.util.FastByteArrayOutputStream(storeData));
        TestPrompt console = new TestPrompt();
        console.addInput("blerb");
        KeytoolTest.KeytoolHarness keytool = new KeytoolTest.KeytoolHarness(console, Command.ADD, true, storeData);
        Assert.assertThat(run("test.password.1", false, true), Matchers.equalTo(0));
        Assert.assertThat(console.getOutput(), Matchers.equalTo(""));
        Assert.assertThat(console.hasInputLeft(), Matchers.is(false));
        Assert.assertThat(keytool.ksExists(), Matchers.is(true));
        Assert.assertThat(keytool.fileBytes.length(), Matchers.is(Matchers.not(0)));
        ks = KeystoreWrapper.loadStore(new org.elasticsearch.hadoop.util.FastByteArrayInputStream(keytool.fileBytes)).build();
        Assert.assertThat(ks.getSecureSetting("test.password.1"), Matchers.equalTo("blerb"));
    }

    @Test
    public void addKey() throws Exception {
        BytesArray storeData = new BytesArray(128);
        KeystoreWrapper ks = KeystoreWrapper.newStore().build();
        ks.saveKeystore(new org.elasticsearch.hadoop.util.FastByteArrayOutputStream(storeData));
        TestPrompt console = new TestPrompt();
        console.addInput("blahh");
        KeytoolTest.KeytoolHarness keytool = new KeytoolTest.KeytoolHarness(console, Command.ADD, true, storeData);
        Assert.assertThat(run("test.password.1", false, false), Matchers.equalTo(0));
        Assert.assertThat(console.getOutput(), Matchers.equalTo(""));
        Assert.assertThat(console.hasInputLeft(), Matchers.is(false));
        Assert.assertThat(keytool.ksExists(), Matchers.is(true));
        Assert.assertThat(keytool.fileBytes.length(), Matchers.is(Matchers.not(0)));
        ks = KeystoreWrapper.loadStore(new org.elasticsearch.hadoop.util.FastByteArrayInputStream(keytool.fileBytes)).build();
        Assert.assertThat(ks.getSecureSetting("test.password.1"), Matchers.equalTo("blahh"));
    }

    @Test
    public void addKeyStdIn() throws Exception {
        BytesArray storeData = new BytesArray(128);
        KeystoreWrapper ks = KeystoreWrapper.newStore().build();
        ks.saveKeystore(new org.elasticsearch.hadoop.util.FastByteArrayOutputStream(storeData));
        TestPrompt console = new TestPrompt();
        console.addInput("blahh");
        KeytoolTest.KeytoolHarness keytool = new KeytoolTest.KeytoolHarness(console, Command.ADD, true, storeData);
        Assert.assertThat(run("test.password.1", true, false), Matchers.equalTo(0));
        Assert.assertThat(console.getOutput(), Matchers.equalTo(""));
        Assert.assertThat(console.hasInputLeft(), Matchers.is(false));
        Assert.assertThat(keytool.ksExists(), Matchers.is(true));
        Assert.assertThat(keytool.fileBytes.length(), Matchers.is(Matchers.not(0)));
        ks = KeystoreWrapper.loadStore(new org.elasticsearch.hadoop.util.FastByteArrayInputStream(keytool.fileBytes)).build();
        Assert.assertThat(ks.getSecureSetting("test.password.1"), Matchers.equalTo("blahh"));
    }

    @Test
    public void removeNonExistant() throws Exception {
        TestPrompt console = new TestPrompt();
        KeytoolTest.KeytoolHarness keytool = new KeytoolTest.KeytoolHarness(console, Command.REMOVE, false, new BytesArray(128));
        Assert.assertThat(run("test.password.1", false, false), Matchers.equalTo(5));
        Assert.assertThat(console.getOutput(), Matchers.equalTo("ERROR: ES-Hadoop keystore not found. Use \'create\' command to create one.\n"));
        Assert.assertThat(console.hasInputLeft(), Matchers.is(false));
        Assert.assertThat(keytool.ksExists(), Matchers.is(false));
        Assert.assertThat(keytool.fileBytes.length(), Matchers.is(0));
    }

    @Test
    public void removeMissingKey() throws Exception {
        BytesArray storeData = new BytesArray(128);
        KeystoreWrapper ks = KeystoreWrapper.newStore().build();
        ks.saveKeystore(new org.elasticsearch.hadoop.util.FastByteArrayOutputStream(storeData));
        TestPrompt console = new TestPrompt();
        KeytoolTest.KeytoolHarness keytool = new KeytoolTest.KeytoolHarness(console, Command.REMOVE, true, storeData);
        Assert.assertThat(run("test.password.1", false, false), Matchers.equalTo(6));
        Assert.assertThat(console.getOutput(), Matchers.equalTo("ERROR: Setting [test.password.1] does not exist in the keystore.\n"));
        Assert.assertThat(console.hasInputLeft(), Matchers.is(false));
        Assert.assertThat(keytool.ksExists(), Matchers.is(true));
        Assert.assertThat(keytool.fileBytes.length(), Matchers.is(Matchers.not(0)));
    }

    @Test
    public void removeKey() throws Exception {
        BytesArray storeData = new BytesArray(128);
        KeystoreWrapper ks = KeystoreWrapper.newStore().build();
        ks.setSecureSetting("test.password.1", "bar");
        ks.setSecureSetting("test.password.2", "foo");
        ks.saveKeystore(new org.elasticsearch.hadoop.util.FastByteArrayOutputStream(storeData));
        TestPrompt console = new TestPrompt();
        KeytoolTest.KeytoolHarness keytool = new KeytoolTest.KeytoolHarness(console, Command.REMOVE, true, storeData);
        Assert.assertThat(run("test.password.1", false, false), Matchers.equalTo(0));
        Assert.assertThat(console.getOutput(), Matchers.equalTo(""));
        Assert.assertThat(console.hasInputLeft(), Matchers.is(false));
        Assert.assertThat(keytool.ksExists(), Matchers.is(true));
        Assert.assertThat(keytool.fileBytes.length(), Matchers.is(Matchers.not(0)));
        ks = KeystoreWrapper.loadStore(new org.elasticsearch.hadoop.util.FastByteArrayInputStream(keytool.fileBytes)).build();
        Assert.assertThat(ks.containsEntry("test.password.1"), Matchers.is(false));
        Assert.assertThat(ks.getSecureSetting("test.password.2"), Matchers.equalTo("foo"));
    }
}

