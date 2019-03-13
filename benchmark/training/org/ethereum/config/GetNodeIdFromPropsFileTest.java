/**
 * Copyright (c) [2017] [ <ether.camp> ]
 * This file is part of the ethereumJ library.
 *
 * The ethereumJ library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The ethereumJ library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the ethereumJ library. If not, see <http://www.gnu.org/licenses/>.
 */
package org.ethereum.config;


import java.io.File;
import java.io.FileReader;
import javax.annotation.concurrent.NotThreadSafe;
import org.ethereum.crypto.ECKey;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import org.spongycastle.util.encoders.Hex;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;


/**
 * Not thread safe - testGeneratedNodePrivateKey temporarily removes the nodeId.properties
 * file which may influence other tests.
 */
@SuppressWarnings("ConstantConditions")
@NotThreadSafe
public class GetNodeIdFromPropsFileTest {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    private File nodeIdPropertiesFile;

    @Test
    public void testGenerateNodeIdFromPropsFileReadsExistingFile() throws Exception {
        // Create temporary nodeId.properties file
        ECKey key = new ECKey();
        String expectedNodePrivateKey = Hex.toHexString(key.getPrivKeyBytes());
        String expectedNodeId = Hex.toHexString(key.getNodeId());
        createNodeIdPropertiesFile("database-test", key);
        new GetNodeIdFromPropsFile("database-test").getNodePrivateKey();
        Assert.assertTrue(nodeIdPropertiesFile.exists());
        String contents = FileCopyUtils.copyToString(new FileReader(nodeIdPropertiesFile));
        String[] lines = StringUtils.tokenizeToStringArray(contents, "\n");
        Assert.assertEquals(4, lines.length);
        Assert.assertTrue(lines[0].startsWith("#Generated NodeID."));
        Assert.assertTrue(lines[1].startsWith("#"));
        Assert.assertTrue(lines[2].startsWith(("nodeIdPrivateKey=" + expectedNodePrivateKey)));
        Assert.assertTrue(lines[3].startsWith(("nodeId=" + expectedNodeId)));
    }

    @Test
    public void testGenerateNodeIdFromPropsDoesntGenerateRandomWhenFileIsPresent() throws Exception {
        // Create temporary nodeId.properties file
        createNodeIdPropertiesFile("database-test", new ECKey());
        GenerateNodeIdRandomly randomNodeIdGeneratorStrategySpy = Mockito.spy(new GenerateNodeIdRandomly("database-test"));
        GenerateNodeIdStrategy fileNodeIdGeneratorStrategy = new GetNodeIdFromPropsFile("database-test").withFallback(randomNodeIdGeneratorStrategySpy);
        fileNodeIdGeneratorStrategy.getNodePrivateKey();
        Mockito.verifyZeroInteractions(randomNodeIdGeneratorStrategySpy);
    }

    @Test
    public void testGenerateNodeIdFromPropsGeneratesRandomWhenFileIsNotPresent() {
        GenerateNodeIdRandomly randomNodeIdGeneratorStrategySpy = Mockito.spy(new GenerateNodeIdRandomly("database-test"));
        GenerateNodeIdStrategy fileNodeIdGeneratorStrategy = new GetNodeIdFromPropsFile("database-test").withFallback(randomNodeIdGeneratorStrategySpy);
        fileNodeIdGeneratorStrategy.getNodePrivateKey();
        Mockito.verify(randomNodeIdGeneratorStrategySpy).getNodePrivateKey();
    }

    @Test
    public void testGenerateNodeIdFromPropsGeneratesRandomWhenFileIsNotPresentAndNoFallback() {
        exception.expect(RuntimeException.class);
        exception.expectMessage("Can't read 'nodeId.properties' and no fallback method has been set");
        GenerateNodeIdStrategy fileNodeIdGeneratorStrategy = new GetNodeIdFromPropsFile("database-test");
        fileNodeIdGeneratorStrategy.getNodePrivateKey();
    }
}

