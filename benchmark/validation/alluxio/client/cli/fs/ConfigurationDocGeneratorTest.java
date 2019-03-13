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
package alluxio.client.cli.fs;


import alluxio.cli.ConfigurationDocGenerator;
import alluxio.collections.Pair;
import alluxio.conf.PropertyKey;
import alluxio.conf.ServerConfiguration;
import alluxio.util.io.PathUtils;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * Tests for {@link alluxio.cli.ConfigurationDocGenerator}.
 */
@RunWith(Parameterized.class)
public class ConfigurationDocGeneratorTest {
    enum TYPE {

        CSV,
        YML;}

    /**
     * Rule to create a new temporary folder during each test.
     */
    @Rule
    public TemporaryFolder mFolder = new TemporaryFolder();

    @Parameterized.Parameter
    public ConfigurationDocGeneratorTest.TYPE mFileType;

    @Parameterized.Parameter(1)
    public Pair<PropertyKey, String> mTestConf;

    private String mLocation;

    @Test
    public void checkCSVFile() throws Exception {
        if ((mFileType) != (ConfigurationDocGeneratorTest.TYPE.CSV)) {
            return;
        }
        Collection<PropertyKey> defaultKeys = new ArrayList<>();
        PropertyKey pKey = mTestConf.getFirst();
        defaultKeys.add(pKey);
        ConfigurationDocGenerator.writeCSVFile(defaultKeys, mLocation);
        String filePath = PathUtils.concatPath(mLocation, mTestConf.getSecond());
        Path p = Paths.get(filePath);
        Assert.assertTrue(Files.exists(p));
        // assert file contents
        List<String> userFile = Files.readAllLines(p, StandardCharsets.UTF_8);
        String defaultValue = ServerConfiguration.get(pKey);
        checkFileContents(String.format("%s,\"%s\"", pKey, defaultValue), userFile, mFileType);
    }

    @Test
    public void checkYMLFile() throws Exception {
        if ((mFileType) != (ConfigurationDocGeneratorTest.TYPE.YML)) {
            return;
        }
        Collection<PropertyKey> defaultKeys = new ArrayList<>();
        PropertyKey pKey = mTestConf.getFirst();
        String description = pKey.getDescription();
        defaultKeys.add(pKey);
        ConfigurationDocGenerator.writeYMLFile(defaultKeys, mLocation);
        String filePath = PathUtils.concatPath(mLocation, mTestConf.getSecond());
        Path p = Paths.get(filePath);
        Assert.assertTrue(Files.exists(p));
        // assert file contents
        List<String> keyDescription = Files.readAllLines(p, StandardCharsets.UTF_8);
        String expected = ((pKey + ":\n  \'") + (description.replace("'", "''"))) + "'";
        checkFileContents(expected, keyDescription, mFileType);
    }
}

