/**
 * Syncany, www.syncany.org
 * Copyright (C) 2011-2016 Philipp C. Heckel <philipp.heckel@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.syncany.config;


import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.syncany.config.to.ConfigTO;
import org.syncany.config.to.RepoTO;
import org.syncany.config.to.RepoTO.TransformerTO;
import org.syncany.tests.util.TestAssertUtil;
import org.syncany.tests.util.TestConfigUtil;


public class ConfigTest {
    @Test
    public void testConfigValid() throws Exception {
        // Setup
        File localDir = new File("/some/folder");
        ConfigTO configTO = new ConfigTO();
        RepoTO repoTO = new RepoTO();
        configTO.setMachineName("somevalidmachinename");// <<< valid

        repoTO.setChunkerTO(TestConfigUtil.createFixedChunkerTO());// <<< valid

        repoTO.setMultiChunker(TestConfigUtil.createZipMultiChunkerTO());// <<< valid

        repoTO.setRepoId(new byte[]{ 1, 2 });// <<< valid

        repoTO.setTransformers(null);// <<< valid

        // Run!
        Config config = new Config(localDir, configTO, repoTO);
        // Test
        Assert.assertEquals("/some/folder/.syncany", config.getAppDir().getAbsolutePath());
        Assert.assertEquals("/some/folder/.syncany/cache", config.getCacheDir().getAbsolutePath());
        Assert.assertEquals("/some/folder/.syncany/db", config.getDatabaseDir().getAbsolutePath());
        Assert.assertEquals("/some/folder/.syncany/db/local.db", config.getDatabaseFile().getAbsolutePath());
        Assert.assertNotNull(config.getChunker());
        Assert.assertEquals("FixedChunker", config.getChunker().getClass().getSimpleName());
        Assert.assertEquals("SHA1", config.getChunker().getChecksumAlgorithm());
        Assert.assertNotNull(config.getMultiChunker());
        Assert.assertEquals("ZipMultiChunker", config.getMultiChunker().getClass().getSimpleName());
        Assert.assertNotNull(config.getTransformer());
        Assert.assertEquals("NoTransformer", config.getTransformer().getClass().getSimpleName());
        Assert.assertNotNull(config.getCache());
    }

    @Test(expected = ConfigException.class)
    public void testConfigInitLocalDirNull() throws Exception {
        File localDir = null;
        ConfigTO configTO = new ConfigTO();
        RepoTO repoTO = new RepoTO();
        new Config(localDir, configTO, repoTO);
    }

    @Test(expected = ConfigException.class)
    public void testConfigInitConfigTONull() throws Exception {
        File localDir = new File("/some/folder");
        ConfigTO configTO = null;
        RepoTO repoTO = new RepoTO();
        new Config(localDir, configTO, repoTO);
    }

    @Test(expected = ConfigException.class)
    public void testConfigInitRepoTONull() throws Exception {
        File localDir = new File("/some/folder");
        ConfigTO configTO = new ConfigTO();
        RepoTO repoTO = null;
        new Config(localDir, configTO, repoTO);
    }

    @Test
    public void testConfigMachineNameInvalidChars() throws Exception {
        File localDir = new File("/some/folder");
        ConfigTO configTO = new ConfigTO();
        RepoTO repoTO = new RepoTO();
        configTO.setMachineName("invalid machine name");
        // Run!
        try {
            new Config(localDir, configTO, repoTO);
            Assert.fail("Machine name should not have been accepted.");
        } catch (ConfigException e) {
            TestAssertUtil.assertErrorStackTraceContains("Machine name", e);
        }
    }

    @Test
    public void testConfigMachineNameInvalidNull() throws Exception {
        File localDir = new File("/some/folder");
        ConfigTO configTO = new ConfigTO();
        RepoTO repoTO = new RepoTO();
        configTO.setMachineName(null);// <<< Invalid

        // Run!
        try {
            new Config(localDir, configTO, repoTO);
            Assert.fail("Machine name should not have been accepted.");
        } catch (ConfigException e) {
            TestAssertUtil.assertErrorStackTraceContains("Machine name", e);
        }
    }

    @Test
    public void testConfigMultiChunkerNull() throws Exception {
        // Setup
        File localDir = new File("/some/folder");
        ConfigTO configTO = new ConfigTO();
        RepoTO repoTO = new RepoTO();
        configTO.setMachineName("somevalidmachinename");// <<< valid

        repoTO.setChunkerTO(TestConfigUtil.createFixedChunkerTO());// <<< valid

        repoTO.setRepoId(new byte[]{ 1, 2 });// <<< valid

        repoTO.setTransformers(null);// <<< valid

        repoTO.setMultiChunker(null);// <<< INVALID !!

        // Run!
        try {
            new Config(localDir, configTO, repoTO);
            Assert.fail("Config should not been have initialized.");
        } catch (ConfigException e) {
            TestAssertUtil.assertErrorStackTraceContains("No multichunker", e);
        }
    }

    @Test
    public void testConfigCipherTransformersInvalidType() throws Exception {
        // Setup
        File localDir = new File("/some/folder");
        ConfigTO configTO = new ConfigTO();
        RepoTO repoTO = new RepoTO();
        configTO.setMachineName("somevalidmachinename");// <<< valid

        repoTO.setChunkerTO(TestConfigUtil.createFixedChunkerTO());// <<< valid

        repoTO.setMultiChunker(TestConfigUtil.createZipMultiChunkerTO());// <<< valid

        repoTO.setRepoId(new byte[]{ 1, 2 });// <<< valid

        // Set invalid transformer
        TransformerTO invalidTransformerTO = new TransformerTO();
        invalidTransformerTO.setType("invalid-typeXXX");
        invalidTransformerTO.setSettings(new HashMap<String, String>());
        List<TransformerTO> transformers = new ArrayList<TransformerTO>();
        transformers.add(invalidTransformerTO);
        repoTO.setTransformers(transformers);// <<< INVALID !

        // Run!
        try {
            new Config(localDir, configTO, repoTO);
            Assert.fail("Transformer should NOT have been found.");
        } catch (ConfigException e) {
            TestAssertUtil.assertErrorStackTraceContains("invalid-typeXXX", e);
        }
    }

    @Test
    @SuppressWarnings("serial")
    public void testConfigCipherTransformersCipherFound() throws Exception {
        // Setup
        File localDir = new File("/some/folder");
        ConfigTO configTO = new ConfigTO();
        RepoTO repoTO = new RepoTO();
        configTO.setMachineName("somevalidmachinename");// <<< valid

        repoTO.setChunkerTO(TestConfigUtil.createFixedChunkerTO());// <<< valid

        repoTO.setMultiChunker(TestConfigUtil.createZipMultiChunkerTO());// <<< valid

        repoTO.setRepoId(new byte[]{ 1, 2 });// <<< valid

        configTO.setMasterKey(createDummyMasterKey());// <<< valid

        // Set invalid transformer
        TransformerTO invalidTransformerTO = new TransformerTO();
        invalidTransformerTO.setType("cipher");
        invalidTransformerTO.setSettings(new HashMap<String, String>() {
            {
                put("cipherspecs", "1,2");
            }
        });
        List<TransformerTO> transformers = new ArrayList<TransformerTO>();
        transformers.add(invalidTransformerTO);
        repoTO.setTransformers(transformers);// <<< valid

        // Run!
        Config config = new Config(localDir, configTO, repoTO);
        // Test
        Assert.assertNotNull(config.getChunker());
        Assert.assertNotNull(config.getMultiChunker());
        Assert.assertNotNull(config.getTransformer());
        Assert.assertEquals("CipherTransformer", config.getTransformer().getClass().getSimpleName());
    }

    @Test
    @SuppressWarnings("serial")
    public void testConfigCipherTransformersCipherNotFound() throws Exception {
        // Setup
        File localDir = new File("/some/folder");
        ConfigTO configTO = new ConfigTO();
        RepoTO repoTO = new RepoTO();
        configTO.setMachineName("somevalidmachinename");// <<< valid

        repoTO.setChunkerTO(TestConfigUtil.createFixedChunkerTO());// <<< valid

        repoTO.setMultiChunker(TestConfigUtil.createZipMultiChunkerTO());// <<< valid

        repoTO.setRepoId(new byte[]{ 1, 2 });// <<< valid

        configTO.setMasterKey(createDummyMasterKey());// <<< valid

        // Set invalid transformer
        TransformerTO invalidTransformerTO = new TransformerTO();
        invalidTransformerTO.setType("cipher");
        invalidTransformerTO.setSettings(new HashMap<String, String>() {
            {
                put("cipherspecs", "1,INVALIDXXXX");// <<<< INVALID !

            }
        });
        List<TransformerTO> transformers = new ArrayList<TransformerTO>();
        transformers.add(invalidTransformerTO);
        repoTO.setTransformers(transformers);
        // Run!
        try {
            new Config(localDir, configTO, repoTO);
            Assert.fail("Transformer should NOT have been able to initialize.");
        } catch (ConfigException e) {
            TestAssertUtil.assertErrorStackTraceContains("INVALIDXXXX", e);
        }
    }
}

