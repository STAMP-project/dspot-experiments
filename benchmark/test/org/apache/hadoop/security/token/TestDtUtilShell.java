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
package org.apache.hadoop.security.token;


import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.security.Credentials;
import org.apache.hadoop.test.GenericTestUtils;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class TestDtUtilShell {
    private static byte[] IDENTIFIER = new byte[]{ 105, 100, 101, 110, 116, 105, 102, 105, 101, 114 };

    private static byte[] PASSWORD = new byte[]{ 112, 97, 115, 115, 119, 111, 114, 100 };

    private static Text KIND = new Text("testTokenKind");

    private static Text SERVICE = new Text("testTokenService");

    private static Text SERVICE2 = new Text("ecivreSnekoTtset");

    private static Configuration defaultConf = new Configuration();

    private static FileSystem localFs = null;

    private final String alias = "proxy_ip:1234";

    private final String getUrl = (TestDtUtilShell.SERVICE_GET.toString()) + "://localhost:9000/";

    private final String getUrl2 = "http://localhost:9000/";

    public static Text SERVICE_GET = new Text("testTokenServiceGet");

    public static Text KIND_GET = new Text("testTokenKindGet");

    public static Token<?> MOCK_TOKEN = new Token(TestDtUtilShell.IDENTIFIER, TestDtUtilShell.PASSWORD, TestDtUtilShell.KIND_GET, TestDtUtilShell.SERVICE_GET);

    private static final Text SERVICE_IMPORT = new Text("testTokenServiceImport");

    private static final Text KIND_IMPORT = new Text("testTokenKindImport");

    private static final Token<?> IMPORT_TOKEN = new Token(TestDtUtilShell.IDENTIFIER, TestDtUtilShell.PASSWORD, TestDtUtilShell.KIND_IMPORT, TestDtUtilShell.SERVICE_IMPORT);

    static {
        try {
            TestDtUtilShell.defaultConf.set("fs.defaultFS", "file:///");
            TestDtUtilShell.localFs = FileSystem.getLocal(TestDtUtilShell.defaultConf);
        } catch (IOException e) {
            throw new RuntimeException("init failure", e);
        }
    }

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    private final Path workDir = new Path(GenericTestUtils.getTestDir("TestDtUtilShell").getAbsolutePath());

    private final Path tokenFile = new Path(workDir, "testPrintTokenFile");

    private final Path tokenFile2 = new Path(workDir, "testPrintTokenFile2");

    private final Path tokenLegacyFile = new Path(workDir, "testPrintTokenFile3");

    private final Path tokenFileGet = new Path(workDir, "testGetTokenFile");

    private final Path tokenFileImport = new Path(workDir, "testImportTokenFile");

    private final String tokenFilename = tokenFile.toString();

    private final String tokenFilename2 = tokenFile2.toString();

    private final String tokenFilenameGet = tokenFileGet.toString();

    private final String tokenFilenameImport = tokenFileImport.toString();

    private String[] args = null;

    private DtUtilShell dt = null;

    private int rc = 0;

    @Test
    public void testPrint() throws Exception {
        args = new String[]{ "print", tokenFilename };
        rc = dt.run(args);
        Assert.assertEquals("test simple print exit code", 0, rc);
        Assert.assertTrue(("test simple print output kind:\n" + (outContent.toString())), outContent.toString().contains(TestDtUtilShell.KIND.toString()));
        Assert.assertTrue(("test simple print output service:\n" + (outContent.toString())), outContent.toString().contains(TestDtUtilShell.SERVICE.toString()));
        outContent.reset();
        args = new String[]{ "print", tokenLegacyFile.toString() };
        rc = dt.run(args);
        Assert.assertEquals("test legacy print exit code", 0, rc);
        Assert.assertTrue(("test simple print output kind:\n" + (outContent.toString())), outContent.toString().contains(TestDtUtilShell.KIND.toString()));
        Assert.assertTrue(("test simple print output service:\n" + (outContent.toString())), outContent.toString().contains(TestDtUtilShell.SERVICE.toString()));
        outContent.reset();
        args = new String[]{ "print", "-alias", TestDtUtilShell.SERVICE.toString(), tokenFilename };
        rc = dt.run(args);
        Assert.assertEquals("test alias print exit code", 0, rc);
        Assert.assertTrue(("test simple print output kind:\n" + (outContent.toString())), outContent.toString().contains(TestDtUtilShell.KIND.toString()));
        Assert.assertTrue(("test simple print output service:\n" + (outContent.toString())), outContent.toString().contains(TestDtUtilShell.SERVICE.toString()));
        outContent.reset();
        args = new String[]{ "print", "-alias", "not-a-serivce", tokenFilename };
        rc = dt.run(args);
        Assert.assertEquals("test no alias print exit code", 0, rc);
        Assert.assertFalse(("test no alias print output kind:\n" + (outContent.toString())), outContent.toString().contains(TestDtUtilShell.KIND.toString()));
        Assert.assertFalse(("test no alias print output service:\n" + (outContent.toString())), outContent.toString().contains(TestDtUtilShell.SERVICE.toString()));
    }

    @Test
    public void testEdit() throws Exception {
        String oldService = TestDtUtilShell.SERVICE2.toString();
        String newAlias = "newName:12345";
        args = new String[]{ "edit", "-service", oldService, "-alias", newAlias, tokenFilename2 };
        rc = dt.run(args);
        Assert.assertEquals("test simple edit exit code", 0, rc);
        args = new String[]{ "print", "-alias", oldService, tokenFilename2 };
        rc = dt.run(args);
        Assert.assertEquals("test simple edit print old exit code", 0, rc);
        Assert.assertTrue(("test simple edit output kind old:\n" + (outContent.toString())), outContent.toString().contains(TestDtUtilShell.KIND.toString()));
        Assert.assertTrue(("test simple edit output service old:\n" + (outContent.toString())), outContent.toString().contains(oldService));
        args = new String[]{ "print", "-alias", newAlias, tokenFilename2 };
        rc = dt.run(args);
        Assert.assertEquals("test simple edit print new exit code", 0, rc);
        Assert.assertTrue(("test simple edit output kind new:\n" + (outContent.toString())), outContent.toString().contains(TestDtUtilShell.KIND.toString()));
        Assert.assertTrue(("test simple edit output service new:\n" + (outContent.toString())), outContent.toString().contains(newAlias));
    }

    @Test
    public void testAppend() throws Exception {
        args = new String[]{ "append", tokenFilename, tokenFilename2 };
        rc = dt.run(args);
        Assert.assertEquals("test simple append exit code", 0, rc);
        args = new String[]{ "print", tokenFilename2 };
        rc = dt.run(args);
        Assert.assertEquals("test simple append print exit code", 0, rc);
        Assert.assertTrue(("test simple append output kind:\n" + (outContent.toString())), outContent.toString().contains(TestDtUtilShell.KIND.toString()));
        Assert.assertTrue(("test simple append output service:\n" + (outContent.toString())), outContent.toString().contains(TestDtUtilShell.SERVICE.toString()));
        Assert.assertTrue(("test simple append output service:\n" + (outContent.toString())), outContent.toString().contains(TestDtUtilShell.SERVICE2.toString()));
    }

    @Test
    public void testRemove() throws Exception {
        args = new String[]{ "remove", "-alias", TestDtUtilShell.SERVICE.toString(), tokenFilename };
        rc = dt.run(args);
        Assert.assertEquals("test simple remove exit code", 0, rc);
        args = new String[]{ "print", tokenFilename };
        rc = dt.run(args);
        Assert.assertEquals("test simple remove print exit code", 0, rc);
        Assert.assertFalse(("test simple remove output kind:\n" + (outContent.toString())), outContent.toString().contains(TestDtUtilShell.KIND.toString()));
        Assert.assertFalse(("test simple remove output service:\n" + (outContent.toString())), outContent.toString().contains(TestDtUtilShell.SERVICE.toString()));
    }

    @Test
    public void testGet() throws Exception {
        args = new String[]{ "get", getUrl, tokenFilenameGet };
        rc = dt.run(args);
        Assert.assertEquals("test mocked get exit code", 0, rc);
        args = new String[]{ "print", tokenFilenameGet };
        rc = dt.run(args);
        String oc = outContent.toString();
        Assert.assertEquals("test print after get exit code", 0, rc);
        Assert.assertTrue(("test print after get output kind:\n" + oc), oc.contains(TestDtUtilShell.KIND_GET.toString()));
        Assert.assertTrue(("test print after get output service:\n" + oc), oc.contains(TestDtUtilShell.SERVICE_GET.toString()));
    }

    @Test
    public void testGetWithServiceFlag() throws Exception {
        args = new String[]{ "get", getUrl2, "-service", TestDtUtilShell.SERVICE_GET.toString(), tokenFilenameGet };
        rc = dt.run(args);
        Assert.assertEquals("test mocked get with service flag exit code", 0, rc);
        args = new String[]{ "print", tokenFilenameGet };
        rc = dt.run(args);
        String oc = outContent.toString();
        Assert.assertEquals("test print after get with service flag exit code", 0, rc);
        Assert.assertTrue(("test print after get with service flag output kind:\n" + oc), oc.contains(TestDtUtilShell.KIND_GET.toString()));
        Assert.assertTrue(("test print after get with service flag output service:\n" + oc), oc.contains(TestDtUtilShell.SERVICE_GET.toString()));
    }

    @Test
    public void testGetWithAliasFlag() throws Exception {
        args = new String[]{ "get", getUrl, "-alias", alias, tokenFilenameGet };
        rc = dt.run(args);
        Assert.assertEquals("test mocked get with alias flag exit code", 0, rc);
        args = new String[]{ "print", tokenFilenameGet };
        rc = dt.run(args);
        String oc = outContent.toString();
        Assert.assertEquals("test print after get with alias flag exit code", 0, rc);
        Assert.assertTrue(("test print after get with alias flag output kind:\n" + oc), oc.contains(TestDtUtilShell.KIND_GET.toString()));
        Assert.assertTrue(("test print after get with alias flag output alias:\n" + oc), oc.contains(alias));
        Assert.assertFalse(("test print after get with alias flag output old service:\n" + oc), oc.contains(TestDtUtilShell.SERVICE_GET.toString()));
    }

    @Test
    public void testFormatJavaFlag() throws Exception {
        args = new String[]{ "get", getUrl, "-format", "java", tokenFilenameGet };
        rc = dt.run(args);
        Assert.assertEquals("test mocked get with java format flag exit code", 0, rc);
        Credentials creds = new Credentials();
        Credentials spyCreds = Mockito.spy(creds);
        DataInputStream in = new DataInputStream(new FileInputStream(tokenFilenameGet));
        spyCreds.readTokenStorageStream(in);
        Mockito.verify(spyCreds).readFields(in);
    }

    @Test
    public void testFormatProtoFlag() throws Exception {
        args = new String[]{ "get", getUrl, "-format", "protobuf", tokenFilenameGet };
        rc = dt.run(args);
        Assert.assertEquals("test mocked get with protobuf format flag exit code", 0, rc);
        Credentials creds = new Credentials();
        Credentials spyCreds = Mockito.spy(creds);
        DataInputStream in = new DataInputStream(new FileInputStream(tokenFilenameGet));
        spyCreds.readTokenStorageStream(in);
        Mockito.verify(spyCreds, Mockito.never()).readFields(in);
    }

    @Test
    public void testImport() throws Exception {
        String base64 = TestDtUtilShell.IMPORT_TOKEN.encodeToUrlString();
        args = new String[]{ "import", base64, tokenFilenameImport };
        rc = dt.run(args);
        Assert.assertEquals("test simple import print old exit code", 0, rc);
        args = new String[]{ "print", tokenFilenameImport };
        rc = dt.run(args);
        Assert.assertEquals("test simple import print old exit code", 0, rc);
        Assert.assertTrue(("test print after import output:\n" + (outContent)), outContent.toString().contains(TestDtUtilShell.KIND_IMPORT.toString()));
        Assert.assertTrue(("test print after import output:\n" + (outContent)), outContent.toString().contains(TestDtUtilShell.SERVICE_IMPORT.toString()));
        Assert.assertTrue(("test print after simple import output:\n" + (outContent)), outContent.toString().contains(base64));
    }

    @Test
    public void testImportWithAliasFlag() throws Exception {
        String base64 = TestDtUtilShell.IMPORT_TOKEN.encodeToUrlString();
        args = new String[]{ "import", base64, "-alias", alias, tokenFilenameImport };
        rc = dt.run(args);
        Assert.assertEquals("test import with alias print old exit code", 0, rc);
        args = new String[]{ "print", tokenFilenameImport };
        rc = dt.run(args);
        Assert.assertEquals("test simple import print old exit code", 0, rc);
        Assert.assertTrue(("test print after import output:\n" + (outContent)), outContent.toString().contains(TestDtUtilShell.KIND_IMPORT.toString()));
        Assert.assertTrue(("test print after import with alias output:\n" + (outContent)), outContent.toString().contains(alias));
    }
}

