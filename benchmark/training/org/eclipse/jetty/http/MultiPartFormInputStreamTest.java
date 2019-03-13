/**
 *
 */
/**
 * ========================================================================
 */
/**
 * Copyright (c) 1995-2019 Mort Bay Consulting Pty. Ltd.
 */
/**
 * ------------------------------------------------------------------------
 */
/**
 * All rights reserved. This program and the accompanying materials
 */
/**
 * are made available under the terms of the Eclipse Public License v1.0
 */
/**
 * and Apache License v2.0 which accompanies this distribution.
 */
/**
 *
 */
/**
 * The Eclipse Public License is available at
 */
/**
 * http://www.eclipse.org/legal/epl-v10.html
 */
/**
 *
 */
/**
 * The Apache License v2.0 is available at
 */
/**
 * http://www.opensource.org/licenses/apache2.0.php
 */
/**
 *
 */
/**
 * You may elect to redistribute this code under either of these licenses.
 */
/**
 * ========================================================================
 */
/**
 *
 */
package org.eclipse.jetty.http;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import javax.servlet.MultipartConfigElement;
import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Part;
import org.eclipse.jetty.http.MultiPartFormInputStream.MultiPart;
import org.eclipse.jetty.util.B64Code;
import org.eclipse.jetty.util.IO;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


/**
 * MultiPartInputStreamTest
 */
public class MultiPartFormInputStreamTest {
    private static final String FILENAME = "stuff.txt";

    protected String _contentType = "multipart/form-data, boundary=AaB03x";

    protected String _multi = MultiPartFormInputStreamTest.createMultipartRequestString(MultiPartFormInputStreamTest.FILENAME);

    // TODO: move to testing dir concept
    protected String _dirname = (((System.getProperty("java.io.tmpdir")) + (File.separator)) + "myfiles-") + (TimeUnit.NANOSECONDS.toMillis(System.nanoTime()));

    protected File _tmpDir = new File(_dirname);

    public MultiPartFormInputStreamTest() {
        _tmpDir.deleteOnExit();
    }

    @Test
    public void testBadMultiPartRequest() throws Exception {
        String boundary = "X0Y0";
        String str = ((((((((("--" + boundary) + "\r\n") + "Content-Disposition: form-data; name=\"fileup\"; filename=\"test.upload\"\r\n") + "Content-Type: application/octet-stream\r\n\r\n") + "How now brown cow.") + "\r\n--") + boundary) + "-\r\n") + "Content-Disposition: form-data; name=\"fileup\"; filename=\"test.upload\"\r\n") + "\r\n";
        MultipartConfigElement config = new MultipartConfigElement(_dirname, 1024, 3072, 50);
        MultiPartFormInputStream mpis = new MultiPartFormInputStream(new ByteArrayInputStream(str.getBytes()), ("multipart/form-data, boundary=" + boundary), config, _tmpDir);
        mpis.setDeleteOnExit(true);
        IOException x = Assertions.assertThrows(IOException.class, () -> mpis.getParts(), "Incomplete Multipart");
        MatcherAssert.assertThat(x.getMessage(), Matchers.startsWith("Incomplete"));
    }

    @Test
    public void testFinalBoundaryOnly() throws Exception {
        String delimiter = "\r\n";
        final String boundary = "MockMultiPartTestBoundary";
        // Malformed multipart request body containing only an arbitrary string of text, followed by the final boundary marker, delimited by empty lines.
        String str = ((((((delimiter + "Hello world") + delimiter)// Two delimiter markers, which make an empty line.
         + delimiter) + "--") + boundary) + "--") + delimiter;
        MultipartConfigElement config = new MultipartConfigElement(_dirname, 1024, 3072, 50);
        MultiPartFormInputStream mpis = new MultiPartFormInputStream(new ByteArrayInputStream(str.getBytes()), ("multipart/form-data, boundary=" + boundary), config, _tmpDir);
        mpis.setDeleteOnExit(true);
        Assertions.assertTrue(mpis.getParts().isEmpty());
    }

    @Test
    public void testEmpty() throws Exception {
        String delimiter = "\r\n";
        final String boundary = "MockMultiPartTestBoundary";
        String str = (((delimiter + "--") + boundary) + "--") + delimiter;
        MultipartConfigElement config = new MultipartConfigElement(_dirname, 1024, 3072, 50);
        MultiPartFormInputStream mpis = new MultiPartFormInputStream(new ByteArrayInputStream(str.getBytes()), ("multipart/form-data, boundary=" + boundary), config, _tmpDir);
        mpis.setDeleteOnExit(true);
        Assertions.assertTrue(mpis.getParts().isEmpty());
    }

    @Test
    public void testNoBoundaryRequest() throws Exception {
        String str = "--\r\n" + ((((((((((((((((((((((("Content-Disposition: form-data; name=\"fileName\"\r\n" + "Content-Type: text/plain; charset=US-ASCII\r\n") + "Content-Transfer-Encoding: 8bit\r\n") + "\r\n") + "abc\r\n") + "--\r\n") + "Content-Disposition: form-data; name=\"desc\"\r\n") + "Content-Type: text/plain; charset=US-ASCII\r\n") + "Content-Transfer-Encoding: 8bit\r\n") + "\r\n") + "123\r\n") + "--\r\n") + "Content-Disposition: form-data; name=\"title\"\r\n") + "Content-Type: text/plain; charset=US-ASCII\r\n") + "Content-Transfer-Encoding: 8bit\r\n") + "\r\n") + "ttt\r\n") + "--\r\n") + "Content-Disposition: form-data; name=\"datafile5239138112980980385.txt\"; filename=\"datafile5239138112980980385.txt\"\r\n") + "Content-Type: application/octet-stream; charset=ISO-8859-1\r\n") + "Content-Transfer-Encoding: binary\r\n") + "\r\n") + "000\r\n") + "----\r\n");
        MultipartConfigElement config = new MultipartConfigElement(_dirname, 1024, 3072, 50);
        MultiPartFormInputStream mpis = new MultiPartFormInputStream(new ByteArrayInputStream(str.getBytes()), "multipart/form-data", config, _tmpDir);
        mpis.setDeleteOnExit(true);
        Collection<Part> parts = mpis.getParts();
        MatcherAssert.assertThat(parts.size(), Matchers.is(4));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Part fileName = mpis.getPart("fileName");
        MatcherAssert.assertThat(fileName, Matchers.notNullValue());
        MatcherAssert.assertThat(fileName.getSize(), Matchers.is(3L));
        IO.copy(fileName.getInputStream(), baos);
        MatcherAssert.assertThat(baos.toString("US-ASCII"), Matchers.is("abc"));
        baos = new ByteArrayOutputStream();
        Part desc = mpis.getPart("desc");
        MatcherAssert.assertThat(desc, Matchers.notNullValue());
        MatcherAssert.assertThat(desc.getSize(), Matchers.is(3L));
        IO.copy(desc.getInputStream(), baos);
        MatcherAssert.assertThat(baos.toString("US-ASCII"), Matchers.is("123"));
        baos = new ByteArrayOutputStream();
        Part title = mpis.getPart("title");
        MatcherAssert.assertThat(title, Matchers.notNullValue());
        MatcherAssert.assertThat(title.getSize(), Matchers.is(3L));
        IO.copy(title.getInputStream(), baos);
        MatcherAssert.assertThat(baos.toString("US-ASCII"), Matchers.is("ttt"));
    }

    @Test
    public void testNonMultiPartRequest() throws Exception {
        MultipartConfigElement config = new MultipartConfigElement(_dirname, 1024, 3072, 50);
        MultiPartFormInputStream mpis = new MultiPartFormInputStream(new ByteArrayInputStream(_multi.getBytes()), "Content-type: text/plain", config, _tmpDir);
        mpis.setDeleteOnExit(true);
        Assertions.assertTrue(mpis.getParts().isEmpty());
    }

    @Test
    public void testNoBody() {
        String body = "";
        MultipartConfigElement config = new MultipartConfigElement(_dirname, 1024, 3072, 50);
        MultiPartFormInputStream mpis = new MultiPartFormInputStream(new ByteArrayInputStream(body.getBytes()), _contentType, config, _tmpDir);
        mpis.setDeleteOnExit(true);
        IOException x = Assertions.assertThrows(IOException.class, () -> mpis.getParts());
        MatcherAssert.assertThat(x.getMessage(), Matchers.containsString("Missing initial multi part boundary"));
    }

    @Test
    public void testBodyAlreadyConsumed() throws Exception {
        ServletInputStream is = new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return true;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener readListener) {
            }

            @Override
            public int read() {
                return 0;
            }
        };
        MultipartConfigElement config = new MultipartConfigElement(_dirname, 1024, 3072, 50);
        MultiPartFormInputStream mpis = new MultiPartFormInputStream(is, _contentType, config, _tmpDir);
        mpis.setDeleteOnExit(true);
        Collection<Part> parts = mpis.getParts();
        Assertions.assertEquals(0, parts.size());
    }

    @Test
    public void testWhitespaceBodyWithCRLF() {
        String whitespace = "              \n\n\n\r\n\r\n\r\n\r\n";
        MultipartConfigElement config = new MultipartConfigElement(_dirname, 1024, 3072, 50);
        MultiPartFormInputStream mpis = new MultiPartFormInputStream(new ByteArrayInputStream(whitespace.getBytes()), _contentType, config, _tmpDir);
        mpis.setDeleteOnExit(true);
        IOException x = Assertions.assertThrows(IOException.class, () -> mpis.getParts());
        MatcherAssert.assertThat(x.getMessage(), Matchers.containsString("Missing initial multi part boundary"));
    }

    @Test
    public void testWhitespaceBody() {
        String whitespace = " ";
        MultipartConfigElement config = new MultipartConfigElement(_dirname, 1024, 3072, 50);
        MultiPartFormInputStream mpis = new MultiPartFormInputStream(new ByteArrayInputStream(whitespace.getBytes()), _contentType, config, _tmpDir);
        mpis.setDeleteOnExit(true);
        IOException x = Assertions.assertThrows(IOException.class, () -> mpis.getParts());
        MatcherAssert.assertThat(x.getMessage(), Matchers.containsString("Missing initial"));
    }

    @Test
    public void testLeadingWhitespaceBodyWithCRLF() throws Exception {
        String body = "              \n\n\n\r\n\r\n\r\n\r\n" + ((((((((((((("--AaB03x\r\n" + "content-disposition: form-data; name=\"field1\"\r\n") + "\r\n") + "Joe Blow\r\n") + "--AaB03x\r\n") + "content-disposition: form-data; name=\"stuff\"; filename=\"") + "foo.txt") + "\"\r\n") + "Content-Type: text/plain\r\n") + "\r\n") + "aaaa") + "bbbbb") + "\r\n") + "--AaB03x--\r\n");
        MultipartConfigElement config = new MultipartConfigElement(_dirname, 1024, 3072, 50);
        MultiPartFormInputStream mpis = new MultiPartFormInputStream(new ByteArrayInputStream(body.getBytes()), _contentType, config, _tmpDir);
        mpis.setDeleteOnExit(true);
        Collection<Part> parts = mpis.getParts();
        MatcherAssert.assertThat(parts, Matchers.notNullValue());
        MatcherAssert.assertThat(parts.size(), Matchers.is(2));
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Part field1 = mpis.getPart("field1");
            MatcherAssert.assertThat(field1, Matchers.notNullValue());
            IO.copy(field1.getInputStream(), baos);
            MatcherAssert.assertThat(baos.toString("US-ASCII"), Matchers.is("Joe Blow"));
        }
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Part stuff = mpis.getPart("stuff");
            MatcherAssert.assertThat(stuff, Matchers.notNullValue());
            IO.copy(stuff.getInputStream(), baos);
            MatcherAssert.assertThat(baos.toString("US-ASCII"), Matchers.containsString("aaaa"));
        }
    }

    @Test
    public void testLeadingWhitespaceBodyWithoutCRLF() throws Exception {
        String body = "            " + ((((((((((((("--AaB03x\r\n" + "content-disposition: form-data; name=\"field1\"\r\n") + "\r\n") + "Joe Blow\r\n") + "--AaB03x\r\n") + "content-disposition: form-data; name=\"stuff\"; filename=\"") + "foo.txt") + "\"\r\n") + "Content-Type: text/plain\r\n") + "\r\n") + "aaaa") + "bbbbb") + "\r\n") + "--AaB03x--\r\n");
        MultipartConfigElement config = new MultipartConfigElement(_dirname, 1024, 3072, 50);
        MultiPartFormInputStream mpis = new MultiPartFormInputStream(new ByteArrayInputStream(body.getBytes()), _contentType, config, _tmpDir);
        mpis.setDeleteOnExit(true);
        Collection<Part> parts = mpis.getParts();
        MatcherAssert.assertThat(parts, Matchers.notNullValue());
        MatcherAssert.assertThat(parts.size(), Matchers.is(1));
        Part stuff = mpis.getPart("stuff");
        MatcherAssert.assertThat(stuff, Matchers.notNullValue());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        IO.copy(stuff.getInputStream(), baos);
        MatcherAssert.assertThat(baos.toString("US-ASCII"), Matchers.containsString("bbbbb"));
    }

    @Test
    public void testNoLimits() throws Exception {
        MultipartConfigElement config = new MultipartConfigElement(_dirname);
        MultiPartFormInputStream mpis = new MultiPartFormInputStream(new ByteArrayInputStream(_multi.getBytes()), _contentType, config, _tmpDir);
        mpis.setDeleteOnExit(true);
        Collection<Part> parts = mpis.getParts();
        Assertions.assertFalse(parts.isEmpty());
    }

    @Test
    public void testRequestTooBig() {
        MultipartConfigElement config = new MultipartConfigElement(_dirname, 60, 100, 50);
        MultiPartFormInputStream mpis = new MultiPartFormInputStream(new ByteArrayInputStream(_multi.getBytes()), _contentType, config, _tmpDir);
        mpis.setDeleteOnExit(true);
        IllegalStateException x = Assertions.assertThrows(IllegalStateException.class, () -> mpis.getParts());
        MatcherAssert.assertThat(x.getMessage(), Matchers.containsString("Request exceeds maxRequestSize"));
    }

    @Test
    public void testRequestTooBigThrowsErrorOnGetParts() {
        MultipartConfigElement config = new MultipartConfigElement(_dirname, 60, 100, 50);
        MultiPartFormInputStream mpis = new MultiPartFormInputStream(new ByteArrayInputStream(_multi.getBytes()), _contentType, config, _tmpDir);
        mpis.setDeleteOnExit(true);
        // cause parsing
        IllegalStateException x = Assertions.assertThrows(IllegalStateException.class, () -> mpis.getParts());
        MatcherAssert.assertThat(x.getMessage(), Matchers.containsString("Request exceeds maxRequestSize"));
        // try again
        x = Assertions.assertThrows(IllegalStateException.class, () -> mpis.getParts());
        MatcherAssert.assertThat(x.getMessage(), Matchers.containsString("Request exceeds maxRequestSize"));
    }

    @Test
    public void testFileTooBig() {
        MultipartConfigElement config = new MultipartConfigElement(_dirname, 40, 1024, 30);
        MultiPartFormInputStream mpis = new MultiPartFormInputStream(new ByteArrayInputStream(_multi.getBytes()), _contentType, config, _tmpDir);
        mpis.setDeleteOnExit(true);
        IllegalStateException x = Assertions.assertThrows(IllegalStateException.class, () -> mpis.getParts(), "stuff.txt should have been larger than maxFileSize");
        MatcherAssert.assertThat(x.getMessage(), Matchers.startsWith("Multipart Mime part"));
    }

    @Test
    public void testFileTooBigThrowsErrorOnGetParts() {
        MultipartConfigElement config = new MultipartConfigElement(_dirname, 40, 1024, 30);
        MultiPartFormInputStream mpis = new MultiPartFormInputStream(new ByteArrayInputStream(_multi.getBytes()), _contentType, config, _tmpDir);
        mpis.setDeleteOnExit(true);
        // Caused parsing
        IllegalStateException x = Assertions.assertThrows(IllegalStateException.class, () -> mpis.getParts(), "stuff.txt should have been larger than maxFileSize");
        MatcherAssert.assertThat(x.getMessage(), Matchers.startsWith("Multipart Mime part"));
        // test again after the parsing
        x = Assertions.assertThrows(IllegalStateException.class, () -> mpis.getParts(), "stuff.txt should have been larger than maxFileSize");
        MatcherAssert.assertThat(x.getMessage(), Matchers.startsWith("Multipart Mime part"));
    }

    @Test
    public void testPartFileNotDeleted() throws Exception {
        MultipartConfigElement config = new MultipartConfigElement(_dirname, 1024, 3072, 50);
        MultiPartFormInputStream mpis = new MultiPartFormInputStream(new ByteArrayInputStream(MultiPartFormInputStreamTest.createMultipartRequestString("tptfd").getBytes()), _contentType, config, _tmpDir);
        mpis.setDeleteOnExit(true);
        mpis.getParts();
        MultiPart part = ((MultiPart) (mpis.getPart("stuff")));
        File stuff = part.getFile();
        MatcherAssert.assertThat(stuff, Matchers.notNullValue());// longer than 100 bytes, should already be a tmp file

        part.write("tptfd.txt");
        File tptfd = new File((((_dirname) + (File.separator)) + "tptfd.txt"));
        MatcherAssert.assertThat(tptfd.exists(), Matchers.is(true));
        MatcherAssert.assertThat(stuff.exists(), Matchers.is(false));// got renamed

        part.cleanUp();
        MatcherAssert.assertThat(tptfd.exists(), Matchers.is(true));// explicitly written file did not get removed after cleanup

        tptfd.deleteOnExit();// clean up test

    }

    @Test
    public void testPartTmpFileDeletion() throws Exception {
        MultipartConfigElement config = new MultipartConfigElement(_dirname, 1024, 3072, 50);
        MultiPartFormInputStream mpis = new MultiPartFormInputStream(new ByteArrayInputStream(MultiPartFormInputStreamTest.createMultipartRequestString("tptfd").getBytes()), _contentType, config, _tmpDir);
        mpis.setDeleteOnExit(true);
        mpis.getParts();
        MultiPart part = ((MultiPart) (mpis.getPart("stuff")));
        File stuff = part.getFile();
        MatcherAssert.assertThat(stuff, Matchers.notNullValue());// longer than 100 bytes, should already be a tmp file

        MatcherAssert.assertThat(stuff.exists(), Matchers.is(true));
        part.cleanUp();
        MatcherAssert.assertThat(stuff.exists(), Matchers.is(false));// tmp file was removed after cleanup

    }

    @Test
    public void testLFOnlyRequest() throws Exception {
        String str = "--AaB03x\n" + ((((((("content-disposition: form-data; name=\"field1\"\n" + "\n") + "Joe Blow") + "\r\n--AaB03x\n") + "content-disposition: form-data; name=\"field2\"\n") + "\n") + "Other") + "\r\n--AaB03x--\n");
        MultipartConfigElement config = new MultipartConfigElement(_dirname, 1024, 3072, 50);
        MultiPartFormInputStream mpis = new MultiPartFormInputStream(new ByteArrayInputStream(str.getBytes()), _contentType, config, _tmpDir);
        mpis.setDeleteOnExit(true);
        Collection<Part> parts = mpis.getParts();
        MatcherAssert.assertThat(parts.size(), Matchers.is(2));
        Part p1 = mpis.getPart("field1");
        MatcherAssert.assertThat(p1, Matchers.notNullValue());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        IO.copy(p1.getInputStream(), baos);
        MatcherAssert.assertThat(baos.toString("UTF-8"), Matchers.is("Joe Blow"));
        Part p2 = mpis.getPart("field2");
        MatcherAssert.assertThat(p2, Matchers.notNullValue());
        baos = new ByteArrayOutputStream();
        IO.copy(p2.getInputStream(), baos);
        MatcherAssert.assertThat(baos.toString("UTF-8"), Matchers.is("Other"));
    }

    @Test
    public void testCROnlyRequest() {
        String str = "--AaB03x\r" + ((((((("content-disposition: form-data; name=\"field1\"\r" + "\r") + "Joe Blow\r") + "--AaB03x\r") + "content-disposition: form-data; name=\"field2\"\r") + "\r") + "Other\r") + "--AaB03x--\r");
        MultipartConfigElement config = new MultipartConfigElement(_dirname, 1024, 3072, 50);
        MultiPartFormInputStream mpis = new MultiPartFormInputStream(new ByteArrayInputStream(str.getBytes()), _contentType, config, _tmpDir);
        mpis.setDeleteOnExit(true);
        IllegalStateException x = Assertions.assertThrows(IllegalStateException.class, () -> mpis.getParts(), "Improper EOL");
        MatcherAssert.assertThat(x.getMessage(), Matchers.containsString("Bad EOL"));
    }

    @Test
    public void testCRandLFMixRequest() {
        String str = "--AaB03x\r" + (((((((("content-disposition: form-data; name=\"field1\"\r" + "\r") + "\nJoe Blow\n") + "\r") + "--AaB03x\r") + "content-disposition: form-data; name=\"field2\"\r") + "\r") + "Other\r") + "--AaB03x--\r");
        MultipartConfigElement config = new MultipartConfigElement(_dirname, 1024, 3072, 50);
        MultiPartFormInputStream mpis = new MultiPartFormInputStream(new ByteArrayInputStream(str.getBytes()), _contentType, config, _tmpDir);
        mpis.setDeleteOnExit(true);
        IllegalStateException x = Assertions.assertThrows(IllegalStateException.class, () -> mpis.getParts(), "Improper EOL");
        MatcherAssert.assertThat(x.getMessage(), Matchers.containsString("Bad EOL"));
    }

    @Test
    public void testBufferOverflowNoCRLF() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write("--AaB03x\r\n".getBytes());
        // create content that will overrun default buffer size of BufferedInputStream
        for (int i = 0; i < 3000; i++) {
            baos.write('a');
        }
        MultipartConfigElement config = new MultipartConfigElement(_dirname, 1024, 3072, 50);
        MultiPartFormInputStream mpis = new MultiPartFormInputStream(new ByteArrayInputStream(baos.toByteArray()), _contentType, config, _tmpDir);
        mpis.setDeleteOnExit(true);
        IllegalStateException x = Assertions.assertThrows(IllegalStateException.class, () -> mpis.getParts(), "Header Line Exceeded Max Length");
        MatcherAssert.assertThat(x.getMessage(), Matchers.containsString("Header Line Exceeded Max Length"));
    }

    @Test
    public void testCharsetEncoding() throws Exception {
        String contentType = "multipart/form-data; boundary=TheBoundary; charset=ISO-8859-1";
        String str = "--TheBoundary\r\n" + (((("content-disposition: form-data; name=\"field1\"\r\n" + "\r\n") + "\nJoe Blow\n") + "\r\n") + "--TheBoundary--\r\n");
        MultipartConfigElement config = new MultipartConfigElement(_dirname, 1024, 3072, 50);
        MultiPartFormInputStream mpis = new MultiPartFormInputStream(new ByteArrayInputStream(str.getBytes()), contentType, config, _tmpDir);
        mpis.setDeleteOnExit(true);
        Collection<Part> parts = mpis.getParts();
        MatcherAssert.assertThat(parts.size(), Matchers.is(1));
    }

    @Test
    public void testBadlyEncodedFilename() throws Exception {
        String contents = "--AaB03x\r\n" + (((((((("content-disposition: form-data; name=\"stuff\"; filename=\"" + "Taken on Aug 22 \\ 2012.jpg") + "\"\r\n") + "Content-Type: text/plain\r\n") + "\r\n") + "stuff") + "aaa") + "\r\n") + "--AaB03x--\r\n");
        MultipartConfigElement config = new MultipartConfigElement(_dirname, 1024, 3072, 50);
        MultiPartFormInputStream mpis = new MultiPartFormInputStream(new ByteArrayInputStream(contents.getBytes()), _contentType, config, _tmpDir);
        mpis.setDeleteOnExit(true);
        Collection<Part> parts = mpis.getParts();
        MatcherAssert.assertThat(parts.size(), Matchers.is(1));
        MatcherAssert.assertThat(parts.iterator().next().getSubmittedFileName(), Matchers.is("Taken on Aug 22 \\ 2012.jpg"));
    }

    @Test
    public void testBadlyEncodedMSFilename() throws Exception {
        String contents = "--AaB03x\r\n" + (((((((("content-disposition: form-data; name=\"stuff\"; filename=\"" + "c:\\this\\really\\is\\some\\path\\to\\a\\file.txt") + "\"\r\n") + "Content-Type: text/plain\r\n") + "\r\n") + "stuff") + "aaa") + "\r\n") + "--AaB03x--\r\n");
        MultipartConfigElement config = new MultipartConfigElement(_dirname, 1024, 3072, 50);
        MultiPartFormInputStream mpis = new MultiPartFormInputStream(new ByteArrayInputStream(contents.getBytes()), _contentType, config, _tmpDir);
        mpis.setDeleteOnExit(true);
        Collection<Part> parts = mpis.getParts();
        MatcherAssert.assertThat(parts.size(), Matchers.is(1));
        MatcherAssert.assertThat(parts.iterator().next().getSubmittedFileName(), Matchers.is("c:\\this\\really\\is\\some\\path\\to\\a\\file.txt"));
    }

    @Test
    public void testCorrectlyEncodedMSFilename() throws Exception {
        String contents = "--AaB03x\r\n" + (((((((("content-disposition: form-data; name=\"stuff\"; filename=\"" + "c:\\\\this\\\\really\\\\is\\\\some\\\\path\\\\to\\\\a\\\\file.txt") + "\"\r\n") + "Content-Type: text/plain\r\n") + "\r\n") + "stuff") + "aaa") + "\r\n") + "--AaB03x--\r\n");
        MultipartConfigElement config = new MultipartConfigElement(_dirname, 1024, 3072, 50);
        MultiPartFormInputStream mpis = new MultiPartFormInputStream(new ByteArrayInputStream(contents.getBytes()), _contentType, config, _tmpDir);
        mpis.setDeleteOnExit(true);
        Collection<Part> parts = mpis.getParts();
        MatcherAssert.assertThat(parts.size(), Matchers.is(1));
        MatcherAssert.assertThat(parts.iterator().next().getSubmittedFileName(), Matchers.is("c:\\this\\really\\is\\some\\path\\to\\a\\file.txt"));
    }

    @Test
    public void testMultiWithSpaceInFilename() throws Exception {
        testMulti("stuff with spaces.txt");
    }

    @Test
    public void testWriteFilesIfContentDispositionFilename() throws Exception {
        String s = "--AaB03x\r\n" + (((((((((("content-disposition: form-data; name=\"field1\"; filename=\"frooble.txt\"\r\n" + "\r\n") + "Joe Blow\r\n") + "--AaB03x\r\n") + "content-disposition: form-data; name=\"stuff\"\r\n") + "Content-Type: text/plain\r\n") + "\r\n") + "sss") + "aaa") + "\r\n") + "--AaB03x--\r\n");
        // all default values for multipartconfig, ie file size threshold 0
        MultipartConfigElement config = new MultipartConfigElement(_dirname);
        MultiPartFormInputStream mpis = new MultiPartFormInputStream(new ByteArrayInputStream(s.getBytes()), _contentType, config, _tmpDir);
        mpis.setDeleteOnExit(true);
        mpis.setWriteFilesWithFilenames(true);
        Collection<Part> parts = mpis.getParts();
        MatcherAssert.assertThat(parts.size(), Matchers.is(2));
        Part field1 = mpis.getPart("field1");// has a filename, should be written to a file

        File f = getFile();
        MatcherAssert.assertThat(f, Matchers.notNullValue());// longer than 100 bytes, should already be a tmp file

        Part stuff = mpis.getPart("stuff");
        f = getFile();// should only be in memory, no filename

        MatcherAssert.assertThat(f, Matchers.nullValue());
    }

    @Test
    public void testMultiSameNames() throws Exception {
        String sameNames = "--AaB03x\r\n" + ((((((((("content-disposition: form-data; name=\"stuff\"; filename=\"stuff1.txt\"\r\n" + "Content-Type: text/plain\r\n") + "\r\n") + "00000\r\n") + "--AaB03x\r\n") + "content-disposition: form-data; name=\"stuff\"; filename=\"stuff2.txt\"\r\n") + "Content-Type: text/plain\r\n") + "\r\n") + "110000000000000000000000000000000000000000000000000\r\n") + "--AaB03x--\r\n");
        MultipartConfigElement config = new MultipartConfigElement(_dirname, 1024, 3072, 50);
        MultiPartFormInputStream mpis = new MultiPartFormInputStream(new ByteArrayInputStream(sameNames.getBytes()), _contentType, config, _tmpDir);
        mpis.setDeleteOnExit(true);
        Collection<Part> parts = mpis.getParts();
        Assertions.assertEquals(2, parts.size());
        for (Part p : parts)
            Assertions.assertEquals("stuff", p.getName());

        // if they all have the name name, then only retrieve the first one
        Part p = mpis.getPart("stuff");
        Assertions.assertNotNull(p);
        Assertions.assertEquals(5, p.getSize());
    }

    @Test
    public void testBase64EncodedContent() throws Exception {
        String contentWithEncodedPart = ((((((((("--AaB03x\r\n" + ((((((((("Content-disposition: form-data; name=\"other\"\r\n" + "Content-Type: text/plain\r\n") + "\r\n") + "other") + "\r\n") + "--AaB03x\r\n") + "Content-disposition: form-data; name=\"stuff\"; filename=\"stuff.txt\"\r\n") + "Content-Transfer-Encoding: base64\r\n") + "Content-Type: application/octet-stream\r\n") + "\r\n")) + (B64Code.encode("hello jetty"))) + "\r\n") + "--AaB03x\r\n") + "Content-disposition: form-data; name=\"final\"\r\n") + "Content-Type: text/plain\r\n") + "\r\n") + "the end") + "\r\n") + "--AaB03x--\r\n";
        MultipartConfigElement config = new MultipartConfigElement(_dirname, 1024, 3072, 50);
        MultiPartFormInputStream mpis = new MultiPartFormInputStream(new ByteArrayInputStream(contentWithEncodedPart.getBytes()), _contentType, config, _tmpDir);
        mpis.setDeleteOnExit(true);
        Collection<Part> parts = mpis.getParts();
        Assertions.assertEquals(3, parts.size());
        Part p1 = mpis.getPart("other");
        Assertions.assertNotNull(p1);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        IO.copy(p1.getInputStream(), baos);
        Assertions.assertEquals("other", baos.toString("US-ASCII"));
        Part p2 = mpis.getPart("stuff");
        Assertions.assertNotNull(p2);
        baos = new ByteArrayOutputStream();
        IO.copy(p2.getInputStream(), baos);
        Assertions.assertEquals(B64Code.encode("hello jetty"), baos.toString("US-ASCII"));
        Part p3 = mpis.getPart("final");
        Assertions.assertNotNull(p3);
        baos = new ByteArrayOutputStream();
        IO.copy(p3.getInputStream(), baos);
        Assertions.assertEquals("the end", baos.toString("US-ASCII"));
    }

    @Test
    public void testQuotedPrintableEncoding() throws Exception {
        String contentWithEncodedPart = "--AaB03x\r\n" + (((((((((((("Content-disposition: form-data; name=\"other\"\r\n" + "Content-Type: text/plain\r\n") + "\r\n") + "other") + "\r\n") + "--AaB03x\r\n") + "Content-disposition: form-data; name=\"stuff\"; filename=\"stuff.txt\"\r\n") + "Content-Transfer-Encoding: quoted-printable\r\n") + "Content-Type: text/plain\r\n") + "\r\n") + "truth=3Dbeauty") + "\r\n") + "--AaB03x--\r\n");
        MultipartConfigElement config = new MultipartConfigElement(_dirname, 1024, 3072, 50);
        MultiPartFormInputStream mpis = new MultiPartFormInputStream(new ByteArrayInputStream(contentWithEncodedPart.getBytes()), _contentType, config, _tmpDir);
        mpis.setDeleteOnExit(true);
        Collection<Part> parts = mpis.getParts();
        Assertions.assertEquals(2, parts.size());
        Part p1 = mpis.getPart("other");
        Assertions.assertNotNull(p1);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        IO.copy(p1.getInputStream(), baos);
        Assertions.assertEquals("other", baos.toString("US-ASCII"));
        Part p2 = mpis.getPart("stuff");
        Assertions.assertNotNull(p2);
        baos = new ByteArrayOutputStream();
        IO.copy(p2.getInputStream(), baos);
        Assertions.assertEquals("truth=3Dbeauty", baos.toString("US-ASCII"));
    }

    @Test
    public void testGeneratedForm() throws Exception {
        String contentType = "multipart/form-data, boundary=WebKitFormBoundary7MA4YWf7OaKlSxkTrZu0gW";
        String body = "Content-Type: multipart/form-data; boundary=WebKitFormBoundary7MA4YWf7OaKlSxkTrZu0gW\r\n" + ((((((((("\r\n" + "--WebKitFormBoundary7MA4YWf7OaKlSxkTrZu0gW\r\n") + "Content-Disposition: form-data; name=\"part1\"\r\n") + "\n") + "wNf\uff90xVam\uffbft\r\n") + "--WebKitFormBoundary7MA4YWf7OaKlSxkTrZu0gW\n") + "Content-Disposition: form-data; name=\"part2\"\r\n") + "\r\n") + "&\uffb3\u001b\u0014\uffba\uffd9\ufff9\uffd6\uffc3O\r\n") + "--WebKitFormBoundary7MA4YWf7OaKlSxkTrZu0gW--");
        MultipartConfigElement config = new MultipartConfigElement(_dirname, 1024, 3072, 50);
        MultiPartFormInputStream mpis = new MultiPartFormInputStream(new ByteArrayInputStream(body.getBytes()), contentType, config, _tmpDir);
        mpis.setDeleteOnExit(true);
        Collection<Part> parts = mpis.getParts();
        MatcherAssert.assertThat(parts, Matchers.notNullValue());
        MatcherAssert.assertThat(parts.size(), Matchers.is(2));
        Part part1 = mpis.getPart("part1");
        MatcherAssert.assertThat(part1, Matchers.notNullValue());
        Part part2 = mpis.getPart("part2");
        MatcherAssert.assertThat(part2, Matchers.notNullValue());
    }
}

