/**
 * The Alluxio Open Foundation licenses this work under the Apache License, version 2.0
 * (the "License"). You may not use this work except in compliance with the License, which is
 * available at www.apache.org/licenses/LICENSE-2.0
 *
 * This software is distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied, as more fully set forth in the License.
 *
 * See the NOTICE file distributed with this work for information regarding copyright ownership.
 */
package alluxio.network;


import PropertyKey.LOCALITY_ORDER;
import PropertyKey.LOCALITY_SCRIPT;
import Template.LOCALITY_TIER;
import alluxio.ConfigurationRule;
import alluxio.conf.InstancedConfiguration;
import alluxio.util.network.NetworkAddressUtils;
import alluxio.wire.TieredIdentity;
import alluxio.wire.TieredIdentity.LocalityTier;
import com.google.common.collect.ImmutableMap;
import java.io.Closeable;
import java.io.File;
import java.util.Arrays;
import org.apache.commons.io.FileUtils;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.powermock.reflect.Whitebox;


/**
 * Unit tests for {@link TieredIdentityFactory}.
 */
public class TieredIdentityFactoryTest {
    @Rule
    public TemporaryFolder mFolder = new TemporaryFolder();

    @Rule
    public ExpectedException mThrown = ExpectedException.none();

    private InstancedConfiguration mConfiguration;

    @Test
    public void defaultConf() throws Exception {
        TieredIdentity identity = TieredIdentityFactory.create(mConfiguration);
        TieredIdentity expected = new TieredIdentity(Arrays.asList(new LocalityTier("node", NetworkAddressUtils.getLocalNodeName(mConfiguration)), new LocalityTier("rack", null)));
        Assert.assertEquals(expected, identity);
    }

    @Test
    public void fromScript() throws Exception {
        String scriptPath = setupScript("node=myhost,rack=myrack,custom=mycustom", mFolder.newFile());
        try (Closeable c = new ConfigurationRule(ImmutableMap.of(LOCALITY_ORDER, "node,rack,custom", LOCALITY_SCRIPT, scriptPath), mConfiguration).toResource()) {
            TieredIdentity identity = TieredIdentityFactory.create(mConfiguration);
            TieredIdentity expected = new TieredIdentity(Arrays.asList(new LocalityTier("node", "myhost"), new LocalityTier("rack", "myrack"), new LocalityTier("custom", "mycustom")));
            Assert.assertEquals(expected, identity);
        }
    }

    @Test
    public void fromScriptClasspath() throws Exception {
        String customScriptName = "my-alluxio-locality.sh";
        File dir = mFolder.newFolder("fromScriptClasspath");
        Whitebox.invokeMethod(ClassLoader.getSystemClassLoader(), "addURL", dir.toURI().toURL());
        File script = new File(dir, customScriptName);
        setupScript("node=myhost,rack=myrack,custom=mycustom", script);
        try (Closeable c = new ConfigurationRule(ImmutableMap.of(LOCALITY_ORDER, "node,rack,custom", LOCALITY_SCRIPT, customScriptName), mConfiguration).toResource()) {
            TieredIdentity identity = TieredIdentityFactory.create(mConfiguration);
            TieredIdentity expected = new TieredIdentity(Arrays.asList(new LocalityTier("node", "myhost"), new LocalityTier("rack", "myrack"), new LocalityTier("custom", "mycustom")));
            Assert.assertEquals(expected, identity);
        }
        script.delete();
    }

    @Test
    public void overrideScript() throws Exception {
        String scriptPath = setupScript("node=myhost,rack=myrack,custom=mycustom", mFolder.newFile());
        try (Closeable c = new ConfigurationRule(ImmutableMap.of(LOCALITY_TIER.format("node"), "overridden", LOCALITY_ORDER, "node,rack,custom", LOCALITY_SCRIPT, scriptPath), mConfiguration).toResource()) {
            TieredIdentity identity = TieredIdentityFactory.create(mConfiguration);
            TieredIdentity expected = new TieredIdentity(Arrays.asList(new LocalityTier("node", "overridden"), new LocalityTier("rack", "myrack"), new LocalityTier("custom", "mycustom")));
            Assert.assertEquals(expected, identity);
        }
    }

    @Test
    public void outOfOrderScript() throws Exception {
        String scriptPath = setupScript("rack=myrack,node=myhost", mFolder.newFile());
        try (Closeable c = new ConfigurationRule(ImmutableMap.of(LOCALITY_SCRIPT, scriptPath), mConfiguration).toResource()) {
            TieredIdentity identity = TieredIdentityFactory.create(mConfiguration);
            TieredIdentity expected = new TieredIdentity(Arrays.asList(new LocalityTier("node", "myhost"), new LocalityTier("rack", "myrack")));
            Assert.assertEquals(expected, identity);
        }
    }

    @Test
    public void repeatedScriptKey() throws Exception {
        String output = "rack=myrack,node=myhost,rack=myrack2";
        mThrown.expectMessage(("Encountered repeated tier definition for rack when parsing " + "tiered identity from string rack=myrack,node=myhost,rack=myrack2"));
        runScriptWithOutput(output);
    }

    @Test
    public void unknownScriptKey() throws Exception {
        String badOutput = "unknown=x";
        mThrown.expectMessage(("Unrecognized tier: unknown. The tiers defined by " + "alluxio.locality.order are [node, rack]"));
        runScriptWithOutput(badOutput);
    }

    @Test
    public void invalidScriptOutput() throws Exception {
        for (String badOutput : new String[]{ "x", "a=b c=d", "node=b,cd", "node=b,abc,x=y" }) {
            try {
                runScriptWithOutput(badOutput);
                Assert.fail("Expected an exception to be thrown");
            } catch (Exception e) {
                Assert.assertThat(e.getMessage(), CoreMatchers.containsString("Failed to parse"));
                Assert.assertThat(e.getMessage(), CoreMatchers.containsString(badOutput));
            }
        }
    }

    @Test
    public void notExecutable() throws Exception {
        File script = mFolder.newFile();
        FileUtils.writeStringToFile(script, "#!/bin/bash");
        try (Closeable c = new ConfigurationRule(ImmutableMap.of(LOCALITY_SCRIPT, script.getAbsolutePath()), mConfiguration).toResource()) {
            try {
                TieredIdentity identity = TieredIdentityFactory.create(mConfiguration);
            } catch (RuntimeException e) {
                Assert.assertThat(e.getMessage(), CoreMatchers.containsString(script.getAbsolutePath()));
                Assert.assertThat(e.getMessage(), CoreMatchers.containsString("Permission denied"));
            }
        }
    }

    @Test
    public void fromString() throws Exception {
        Assert.assertEquals(new TieredIdentity(Arrays.asList(new LocalityTier("node", "b"), new LocalityTier("rack", "d"))), TieredIdentityFactory.fromString("node=b,rack=d", mConfiguration));
    }
}

