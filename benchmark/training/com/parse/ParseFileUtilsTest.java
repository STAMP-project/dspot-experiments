/**
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse;


import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = TestHelper.ROBOLECTRIC_SDK_VERSION)
public class ParseFileUtilsTest {
    private static final String TEST_STRING = "this is a test string";

    private static final String TEST_JSON = "{ \"foo\": \"bar\" }";

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void testReadFileToString() throws Exception {
        File file = temporaryFolder.newFile("file.txt");
        BufferedOutputStream out = null;
        try {
            out = new BufferedOutputStream(new FileOutputStream(file));
            out.write(ParseFileUtilsTest.TEST_STRING.getBytes("UTF-8"));
        } finally {
            ParseIOUtils.closeQuietly(out);
        }
        Assert.assertEquals(ParseFileUtilsTest.TEST_STRING, ParseFileUtils.readFileToString(file, "UTF-8"));
    }

    @Test
    public void testWriteStringToFile() throws Exception {
        File file = temporaryFolder.newFile("file.txt");
        ParseFileUtils.writeStringToFile(file, ParseFileUtilsTest.TEST_STRING, "UTF-8");
        InputStream in = null;
        String content;
        try {
            in = new FileInputStream(file);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ParseIOUtils.copy(in, out);
            content = new String(out.toByteArray(), "UTF-8");
        } finally {
            ParseIOUtils.closeQuietly(in);
        }
        Assert.assertEquals(ParseFileUtilsTest.TEST_STRING, content);
    }

    @Test
    public void testReadFileToJSONObject() throws Exception {
        File file = temporaryFolder.newFile("file.txt");
        BufferedOutputStream out = null;
        try {
            out = new BufferedOutputStream(new FileOutputStream(file));
            out.write(ParseFileUtilsTest.TEST_JSON.getBytes("UTF-8"));
        } finally {
            ParseIOUtils.closeQuietly(out);
        }
        JSONObject json = ParseFileUtils.readFileToJSONObject(file);
        Assert.assertNotNull(json);
        Assert.assertEquals("bar", json.getString("foo"));
    }

    @Test
    public void testWriteJSONObjectToFile() throws Exception {
        File file = temporaryFolder.newFile("file.txt");
        ParseFileUtils.writeJSONObjectToFile(file, new JSONObject(ParseFileUtilsTest.TEST_JSON));
        InputStream in = null;
        String content;
        try {
            in = new FileInputStream(file);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ParseIOUtils.copy(in, out);
            content = new String(out.toByteArray());
        } finally {
            ParseIOUtils.closeQuietly(in);
        }
        JSONObject json = new JSONObject(content);
        Assert.assertNotNull(json);
        Assert.assertEquals("bar", json.getString("foo"));
    }
}

