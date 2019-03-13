/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2018 by Hitachi Vantara : http://www.pentaho.com
 *
 * ******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ****************************************************************************
 */
package org.pentaho.di.repository;


import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.Mockito;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.LogChannel;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;
import org.pentaho.test.util.XXEUtils;


public class RepositoriesMetaTest {
    private RepositoriesMeta repoMeta;

    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    @Test
    public void testToString() throws Exception {
        RepositoriesMeta repositoriesMeta = new RepositoriesMeta();
        Assert.assertEquals("RepositoriesMeta", repositoriesMeta.toString());
    }

    @Test
    public void testReadData_closeInput() throws Exception {
        String repositoriesFile = getClass().getResource("repositories.xml").getPath();
        LogChannel log = Mockito.mock(LogChannel.class);
        Mockito.when(repoMeta.getKettleUserRepositoriesFile()).thenReturn(repositoriesFile);
        Mockito.when(repoMeta.newLogChannel()).thenReturn(log);
        repoMeta.readData();
        RandomAccessFile fos = null;
        try {
            File file = new File(repositoriesFile);
            if (file.exists()) {
                fos = new RandomAccessFile(file, "rw");
            }
        } catch (FileNotFoundException | SecurityException e) {
            Assert.fail("the file with properties should be unallocated");
        } finally {
            if (fos != null) {
                fos.close();
            }
        }
    }

    @Test
    public void testReadData() throws Exception {
        LogChannel log = Mockito.mock(LogChannel.class);
        Mockito.doReturn(getClass().getResource("repositories.xml").getPath()).when(repoMeta).getKettleUserRepositoriesFile();
        Mockito.doReturn(log).when(repoMeta).newLogChannel();
        repoMeta.readData();
        String repositoriesXml = (((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + (Const.CR)) + "<repositories>") + (Const.CR)) + "  <connection>") + (Const.CR)) + "    <name>local postgres</name>") + (Const.CR)) + "    <server>localhost</server>") + (Const.CR)) + "    <type>POSTGRESQL</type>") + (Const.CR)) + "    <access>Native</access>") + (Const.CR)) + "    <database>hibernate</database>") + (Const.CR)) + "    <port>5432</port>") + (Const.CR)) + "    <username>auser</username>") + (Const.CR)) + "    <password>Encrypted 2be98afc86aa7f285bb18bd63c99dbdde</password>") + (Const.CR)) + "    <servername/>") + (Const.CR)) + "    <data_tablespace/>") + (Const.CR)) + "    <index_tablespace/>") + (Const.CR)) + "    <attributes>") + (Const.CR)) + "      <attribute><code>FORCE_IDENTIFIERS_TO_LOWERCASE</code><attribute>N</attribute></attribute>") + (Const.CR)) + "      <attribute><code>FORCE_IDENTIFIERS_TO_UPPERCASE</code><attribute>N</attribute></attribute>") + (Const.CR)) + "      <attribute><code>IS_CLUSTERED</code><attribute>N</attribute></attribute>") + (Const.CR)) + "      <attribute><code>PORT_NUMBER</code><attribute>5432</attribute></attribute>") + (Const.CR)) + "      <attribute><code>PRESERVE_RESERVED_WORD_CASE</code><attribute>N</attribute></attribute>") + (Const.CR)) + "      <attribute><code>QUOTE_ALL_FIELDS</code><attribute>N</attribute></attribute>") + (Const.CR)) + "      <attribute><code>SUPPORTS_BOOLEAN_DATA_TYPE</code><attribute>Y</attribute></attribute>") + (Const.CR)) + "      <attribute><code>SUPPORTS_TIMESTAMP_DATA_TYPE</code><attribute>Y</attribute></attribute>") + (Const.CR)) + "      <attribute><code>USE_POOLING</code><attribute>N</attribute></attribute>") + (Const.CR)) + "    </attributes>") + (Const.CR)) + "  </connection>") + (Const.CR)) + "  <repository>    <id>KettleFileRepository</id>") + (Const.CR)) + "    <name>Test Repository</name>") + (Const.CR)) + "    <description>Test Repository Description</description>") + (Const.CR)) + "    <is_default>false</is_default>") + (Const.CR)) + "    <base_directory>test-repository</base_directory>") + (Const.CR)) + "    <read_only>N</read_only>") + (Const.CR)) + "    <hides_hidden_files>N</hides_hidden_files>") + (Const.CR)) + "  </repository>  </repositories>") + (Const.CR);
        Assert.assertEquals(repositoriesXml, repoMeta.getXML());
        RepositoriesMeta clone = repoMeta.clone();
        Assert.assertEquals(repositoriesXml, repoMeta.getXML());
        Assert.assertNotSame(clone, repoMeta);
        Assert.assertEquals(1, repoMeta.nrRepositories());
        RepositoryMeta repository = repoMeta.getRepository(0);
        Assert.assertEquals("Test Repository", repository.getName());
        Assert.assertEquals("Test Repository Description", repository.getDescription());
        Assert.assertEquals((((((((((((((("  <repository>    <id>KettleFileRepository</id>" + (Const.CR)) + "    <name>Test Repository</name>") + (Const.CR)) + "    <description>Test Repository Description</description>") + (Const.CR)) + "    <is_default>false</is_default>") + (Const.CR)) + "    <base_directory>test-repository</base_directory>") + (Const.CR)) + "    <read_only>N</read_only>") + (Const.CR)) + "    <hides_hidden_files>N</hides_hidden_files>") + (Const.CR)) + "  </repository>"), repository.getXML());
        Assert.assertSame(repository, repoMeta.searchRepository("Test Repository"));
        Assert.assertSame(repository, repoMeta.findRepositoryById("KettleFileRepository"));
        Assert.assertSame(repository, repoMeta.findRepository("Test Repository"));
        Assert.assertNull(repoMeta.findRepository("not found"));
        Assert.assertNull(repoMeta.findRepositoryById("not found"));
        Assert.assertEquals(0, repoMeta.indexOfRepository(repository));
        repoMeta.removeRepository(0);
        Assert.assertEquals(0, repoMeta.nrRepositories());
        Assert.assertNull(repoMeta.searchRepository("Test Repository"));
        repoMeta.addRepository(0, repository);
        Assert.assertEquals(1, repoMeta.nrRepositories());
        repoMeta.removeRepository(1);
        Assert.assertEquals(1, repoMeta.nrRepositories());
        Assert.assertEquals(1, repoMeta.nrDatabases());
        Assert.assertEquals("local postgres", repoMeta.getDatabase(0).getName());
        DatabaseMeta searchDatabase = repoMeta.searchDatabase("local postgres");
        Assert.assertSame(searchDatabase, repoMeta.getDatabase(0));
        Assert.assertEquals(0, repoMeta.indexOfDatabase(searchDatabase));
        repoMeta.removeDatabase(0);
        Assert.assertEquals(0, repoMeta.nrDatabases());
        Assert.assertNull(repoMeta.searchDatabase("local postgres"));
        repoMeta.addDatabase(0, searchDatabase);
        Assert.assertEquals(1, repoMeta.nrDatabases());
        repoMeta.removeDatabase(1);
        Assert.assertEquals(1, repoMeta.nrDatabases());
        Assert.assertEquals("Unable to read repository with id [junk]. RepositoryMeta is not available.", repoMeta.getErrorMessage());
    }

    @Test
    public void testNothingToRead() throws Exception {
        Mockito.doReturn("filedoesnotexist.xml").when(repoMeta).getKettleUserRepositoriesFile();
        Assert.assertTrue(repoMeta.readData());
        Assert.assertEquals(0, repoMeta.nrDatabases());
        Assert.assertEquals(0, repoMeta.nrRepositories());
    }

    @Test
    public void testReadDataFromInputStream() throws Exception {
        InputStream inputStream = getClass().getResourceAsStream("repositories.xml");
        repoMeta.readDataFromInputStream(inputStream);
        Assert.assertEquals(1, repoMeta.nrDatabases());
        Assert.assertEquals(1, repoMeta.nrRepositories());
    }

    @Test
    public void testErrorReadingInputStream() throws Exception {
        try {
            repoMeta.readDataFromInputStream(getClass().getResourceAsStream("filedoesnotexist.xml"));
        } catch (KettleException e) {
            Assert.assertEquals((((((Const.CR) + "Error reading information from file:") + (Const.CR)) + "InputStream cannot be null") + (Const.CR)), e.getMessage());
        }
    }

    @Test
    public void testErrorReadingFile() throws Exception {
        Mockito.when(repoMeta.getKettleUserRepositoriesFile()).thenReturn(getClass().getResource("bad-repositories.xml").getPath());
        try {
            repoMeta.readData();
        } catch (KettleException e) {
            Assert.assertEquals((((((Const.CR) + "Error reading information from file:") + (Const.CR)) + "The element type \"repositories\" must be terminated by the matching end-tag \"</repositories>\".") + (Const.CR)), e.getMessage());
        }
    }

    @Test
    public void testWriteFile() throws Exception {
        String path = getClass().getResource("repositories.xml").getPath().replace("repositories.xml", "new-repositories.xml");
        Mockito.doReturn(path).when(repoMeta).getKettleUserRepositoriesFile();
        repoMeta.writeData();
        InputStream resourceAsStream = getClass().getResourceAsStream("new-repositories.xml");
        Assert.assertEquals(((((("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + (Const.CR)) + "<repositories>") + (Const.CR)) + "  </repositories>") + (Const.CR)), IOUtils.toString(resourceAsStream));
        new File(path).delete();
    }

    @Test
    public void testErrorWritingFile() throws Exception {
        Mockito.when(repoMeta.getKettleUserRepositoriesFile()).thenReturn(null);
        try {
            repoMeta.writeData();
        } catch (KettleException e) {
            Assert.assertTrue(e.getMessage().startsWith(((Const.CR) + "Error writing repositories metadata")));
        }
    }

    @Test(expected = KettleException.class)
    public void exceptionThrownWhenParsingXmlWithBigAmountOfExternalEntitiesFromInputStream() throws Exception {
        repoMeta.readDataFromInputStream(new ByteArrayInputStream(XXEUtils.MALICIOUS_XML.getBytes()));
    }
}

