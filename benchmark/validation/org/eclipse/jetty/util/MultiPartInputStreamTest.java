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
package org.eclipse.jetty.util;


import NonCompliance.BASE64_TRANSFER_ENCODING;
import NonCompliance.CR_LINE_TERMINATION;
import NonCompliance.LF_LINE_TERMINATION;
import NonCompliance.NO_CRLF_AFTER_PREAMBLE;
import NonCompliance.QUOTED_PRINTABLE_TRANSFER_ENCODING;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.EnumSet;
import java.util.concurrent.TimeUnit;
import javax.servlet.MultipartConfigElement;
import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Part;
import org.eclipse.jetty.util.MultiPartInputStreamParser.MultiPart;
import org.eclipse.jetty.util.MultiPartInputStreamParser.NonCompliance;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * MultiPartInputStreamTest
 */
@SuppressWarnings("deprecation")
public class MultiPartInputStreamTest {
    private static final String FILENAME = "stuff.txt";

    protected String _contentType = "multipart/form-data, boundary=AaB03x";

    protected String _multi = createMultipartRequestString(MultiPartInputStreamTest.FILENAME);

    protected String _dirname = (((System.getProperty("java.io.tmpdir")) + (File.separator)) + "myfiles-") + (TimeUnit.NANOSECONDS.toMillis(System.nanoTime()));

    protected File _tmpDir = new File(_dirname);

    public MultiPartInputStreamTest() {
        _tmpDir.deleteOnExit();
    }

    @Test
    public void testBadMultiPartRequest() throws Exception {
        String boundary = "X0Y0";
        String str = ((((((("--" + boundary) + "\r\n") + "Content-Disposition: form-data; name=\"fileup\"; filename=\"test.upload\"\r\n") + "Content-Type: application/octet-stream\r\n\r\n") + "How now brown cow.") + "\r\n--") + boundary) + "-\r\n\r\n";
        MultipartConfigElement config = new MultipartConfigElement(_dirname, 1024, 3072, 50);
        MultiPartInputStreamParser mpis = new MultiPartInputStreamParser(new ByteArrayInputStream(str.getBytes()), ("multipart/form-data, boundary=" + boundary), config, _tmpDir);
        mpis.setDeleteOnExit(true);
        try {
            mpis.getParts();
            Assertions.fail("Multipart incomplete");
        } catch (IOException e) {
            Assertions.assertTrue(e.getMessage().startsWith("Incomplete"));
        }
    }

    @Test
    public void testFinalBoundaryOnly() throws Exception {
        String delimiter = "\r\n";
        final String boundary = "MockMultiPartTestBoundary";
        // Malformed multipart request body containing only an arbitrary string of text, followed by the final boundary marker, delimited by empty lines.
        String str = ((((((delimiter + "Hello world") + delimiter)// Two delimiter markers, which make an empty line.
         + delimiter) + "--") + boundary) + "--") + delimiter;
        MultipartConfigElement config = new MultipartConfigElement(_dirname, 1024, 3072, 50);
        MultiPartInputStreamParser mpis = new MultiPartInputStreamParser(new ByteArrayInputStream(str.getBytes()), ("multipart/form-data, boundary=" + boundary), config, _tmpDir);
        mpis.setDeleteOnExit(true);
        Collection<Part> parts = mpis.getParts();
        Assertions.assertTrue(mpis.getParts().isEmpty());
        Assertions.assertEquals(EnumSet.noneOf(NonCompliance.class), mpis.getNonComplianceWarnings());
    }

    @Test
    public void testEmpty() throws Exception {
        String delimiter = "\r\n";
        final String boundary = "MockMultiPartTestBoundary";
        String str = (((delimiter + "--") + boundary) + "--") + delimiter;
        MultipartConfigElement config = new MultipartConfigElement(_dirname, 1024, 3072, 50);
        MultiPartInputStreamParser mpis = new MultiPartInputStreamParser(new ByteArrayInputStream(str.getBytes()), ("multipart/form-data, boundary=" + boundary), config, _tmpDir);
        mpis.setDeleteOnExit(true);
        Assertions.assertTrue(mpis.getParts().isEmpty());
        Assertions.assertEquals(EnumSet.noneOf(NonCompliance.class), mpis.getNonComplianceWarnings());
    }

    @Test
    public void testNoBoundaryRequest() throws Exception {
        String str = "--\r\n" + ((((((((((((((((((((((("Content-Disposition: form-data; name=\"fileName\"\r\n" + "Content-Type: text/plain; charset=US-ASCII\r\n") + "Content-Transfer-Encoding: 8bit\r\n") + "\r\n") + "abc\r\n") + "--\r\n") + "Content-Disposition: form-data; name=\"desc\"\r\n") + "Content-Type: text/plain; charset=US-ASCII\r\n") + "Content-Transfer-Encoding: 8bit\r\n") + "\r\n") + "123\r\n") + "--\r\n") + "Content-Disposition: form-data; name=\"title\"\r\n") + "Content-Type: text/plain; charset=US-ASCII\r\n") + "Content-Transfer-Encoding: 8bit\r\n") + "\r\n") + "ttt\r\n") + "--\r\n") + "Content-Disposition: form-data; name=\"datafile5239138112980980385.txt\"; filename=\"datafile5239138112980980385.txt\"\r\n") + "Content-Type: application/octet-stream; charset=ISO-8859-1\r\n") + "Content-Transfer-Encoding: binary\r\n") + "\r\n") + "000\r\n") + "----\r\n");
        MultipartConfigElement config = new MultipartConfigElement(_dirname, 1024, 3072, 50);
        MultiPartInputStreamParser mpis = new MultiPartInputStreamParser(new ByteArrayInputStream(str.getBytes()), "multipart/form-data", config, _tmpDir);
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
        Assertions.assertEquals(EnumSet.noneOf(NonCompliance.class), mpis.getNonComplianceWarnings());
    }

    @Test
    public void testNonMultiPartRequest() throws Exception {
        MultipartConfigElement config = new MultipartConfigElement(_dirname, 1024, 3072, 50);
        MultiPartInputStreamParser mpis = new MultiPartInputStreamParser(new ByteArrayInputStream(_multi.getBytes()), "Content-type: text/plain", config, _tmpDir);
        mpis.setDeleteOnExit(true);
        Assertions.assertTrue(mpis.getParts().isEmpty());
        Assertions.assertEquals(EnumSet.noneOf(NonCompliance.class), mpis.getNonComplianceWarnings());
    }

    @Test
    public void testNoBody() throws Exception {
        String body = "";
        MultipartConfigElement config = new MultipartConfigElement(_dirname, 1024, 3072, 50);
        MultiPartInputStreamParser mpis = new MultiPartInputStreamParser(new ByteArrayInputStream(body.getBytes()), _contentType, config, _tmpDir);
        mpis.setDeleteOnExit(true);
        try {
            mpis.getParts();
            Assertions.fail("Multipart missing body");
        } catch (IOException e) {
            Assertions.assertTrue(e.getMessage().startsWith("Missing content"));
        }
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
            public int read() throws IOException {
                return 0;
            }
        };
        MultipartConfigElement config = new MultipartConfigElement(_dirname, 1024, 3072, 50);
        MultiPartInputStreamParser mpis = new MultiPartInputStreamParser(is, _contentType, config, _tmpDir);
        mpis.setDeleteOnExit(true);
        Collection<Part> parts = mpis.getParts();
        Assertions.assertEquals(0, parts.size());
    }

    @Test
    public void testWhitespaceBodyWithCRLF() throws Exception {
        String whitespace = "              \n\n\n\r\n\r\n\r\n\r\n";
        MultipartConfigElement config = new MultipartConfigElement(_dirname, 1024, 3072, 50);
        MultiPartInputStreamParser mpis = new MultiPartInputStreamParser(new ByteArrayInputStream(whitespace.getBytes()), _contentType, config, _tmpDir);
        mpis.setDeleteOnExit(true);
        try {
            mpis.getParts();
            Assertions.fail("Multipart missing body");
        } catch (IOException e) {
            Assertions.assertTrue(e.getMessage().startsWith("Missing initial"));
        }
    }

    @Test
    public void testWhitespaceBody() throws Exception {
        String whitespace = " ";
        MultipartConfigElement config = new MultipartConfigElement(_dirname, 1024, 3072, 50);
        MultiPartInputStreamParser mpis = new MultiPartInputStreamParser(new ByteArrayInputStream(whitespace.getBytes()), _contentType, config, _tmpDir);
        mpis.setDeleteOnExit(true);
        try {
            mpis.getParts();
            Assertions.fail("Multipart missing body");
        } catch (IOException e) {
            Assertions.assertTrue(e.getMessage().startsWith("Missing initial"));
        }
    }

    @SuppressWarnings("Duplicates")
    @Test
    public void testLeadingWhitespaceBodyWithCRLF() throws Exception {
        String body = "              \n\n\n\r\n\r\n\r\n\r\n" + ((((((((((((("--AaB03x\r\n" + "content-disposition: form-data; name=\"field1\"\r\n") + "\r\n") + "Joe Blow\r\n") + "--AaB03x\r\n") + "content-disposition: form-data; name=\"stuff\"; filename=\"") + "foo.txt") + "\"\r\n") + "Content-Type: text/plain\r\n") + "\r\n") + "aaaa") + "bbbbb") + "\r\n") + "--AaB03x--\r\n");
        MultipartConfigElement config = new MultipartConfigElement(_dirname, 1024, 3072, 50);
        MultiPartInputStreamParser mpis = new MultiPartInputStreamParser(new ByteArrayInputStream(body.getBytes()), _contentType, config, _tmpDir);
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
        Assertions.assertEquals(EnumSet.of(LF_LINE_TERMINATION), mpis.getNonComplianceWarnings());
    }

    @Test
    public void testLeadingWhitespaceBodyWithoutCRLF() throws Exception {
        String body = "            " + ((((((((((((("--AaB03x\r\n" + "content-disposition: form-data; name=\"field1\"\r\n") + "\r\n") + "Joe Blow\r\n") + "--AaB03x\r\n") + "content-disposition: form-data; name=\"stuff\"; filename=\"") + "foo.txt") + "\"\r\n") + "Content-Type: text/plain\r\n") + "\r\n") + "aaaa") + "bbbbb") + "\r\n") + "--AaB03x--\r\n");
        MultipartConfigElement config = new MultipartConfigElement(_dirname, 1024, 3072, 50);
        MultiPartInputStreamParser mpis = new MultiPartInputStreamParser(new ByteArrayInputStream(body.getBytes()), _contentType, config, _tmpDir);
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
            MatcherAssert.assertThat(baos.toString("US-ASCII"), Matchers.containsString("bbbbb"));
        }
        Assertions.assertEquals(EnumSet.of(NO_CRLF_AFTER_PREAMBLE), mpis.getNonComplianceWarnings());
    }

    @Test
    public void testNoLimits() throws Exception {
        MultipartConfigElement config = new MultipartConfigElement(_dirname);
        MultiPartInputStreamParser mpis = new MultiPartInputStreamParser(new ByteArrayInputStream(_multi.getBytes()), _contentType, config, _tmpDir);
        mpis.setDeleteOnExit(true);
        Collection<Part> parts = mpis.getParts();
        Assertions.assertFalse(parts.isEmpty());
    }

    @Test
    public void testRequestTooBig() throws Exception {
        MultipartConfigElement config = new MultipartConfigElement(_dirname, 60, 100, 50);
        MultiPartInputStreamParser mpis = new MultiPartInputStreamParser(new ByteArrayInputStream(_multi.getBytes()), _contentType, config, _tmpDir);
        mpis.setDeleteOnExit(true);
        Collection<Part> parts = null;
        try {
            parts = mpis.getParts();
            Assertions.fail("Request should have exceeded maxRequestSize");
        } catch (IllegalStateException e) {
            Assertions.assertTrue(e.getMessage().startsWith("Request exceeds maxRequestSize"));
        }
    }

    @Test
    public void testRequestTooBigThrowsErrorOnGetParts() throws Exception {
        MultipartConfigElement config = new MultipartConfigElement(_dirname, 60, 100, 50);
        MultiPartInputStreamParser mpis = new MultiPartInputStreamParser(new ByteArrayInputStream(_multi.getBytes()), _contentType, config, _tmpDir);
        mpis.setDeleteOnExit(true);
        Collection<Part> parts = null;
        // cause parsing
        try {
            parts = mpis.getParts();
            Assertions.fail("Request should have exceeded maxRequestSize");
        } catch (IllegalStateException e) {
            Assertions.assertTrue(e.getMessage().startsWith("Request exceeds maxRequestSize"));
        }
        // try again
        try {
            parts = mpis.getParts();
            Assertions.fail("Request should have exceeded maxRequestSize");
        } catch (IllegalStateException e) {
            Assertions.assertTrue(e.getMessage().startsWith("Request exceeds maxRequestSize"));
        }
    }

    @Test
    public void testFileTooBig() throws Exception {
        MultipartConfigElement config = new MultipartConfigElement(_dirname, 40, 1024, 30);
        MultiPartInputStreamParser mpis = new MultiPartInputStreamParser(new ByteArrayInputStream(_multi.getBytes()), _contentType, config, _tmpDir);
        mpis.setDeleteOnExit(true);
        Collection<Part> parts = null;
        try {
            parts = mpis.getParts();
            Assertions.fail("stuff.txt should have been larger than maxFileSize");
        } catch (IllegalStateException e) {
            Assertions.assertTrue(e.getMessage().startsWith("Multipart Mime part"));
        }
    }

    @Test
    public void testFileTooBigThrowsErrorOnGetParts() throws Exception {
        MultipartConfigElement config = new MultipartConfigElement(_dirname, 40, 1024, 30);
        MultiPartInputStreamParser mpis = new MultiPartInputStreamParser(new ByteArrayInputStream(_multi.getBytes()), _contentType, config, _tmpDir);
        mpis.setDeleteOnExit(true);
        Collection<Part> parts = null;
        try {
            parts = mpis.getParts();// caused parsing

            Assertions.fail("stuff.txt should have been larger than maxFileSize");
        } catch (IllegalStateException e) {
            Assertions.assertTrue(e.getMessage().startsWith("Multipart Mime part"));
        }
        // test again after the parsing
        try {
            parts = mpis.getParts();// caused parsing

            Assertions.fail("stuff.txt should have been larger than maxFileSize");
        } catch (IllegalStateException e) {
            Assertions.assertTrue(e.getMessage().startsWith("Multipart Mime part"));
        }
    }

    @Test
    public void testPartFileNotDeleted() throws Exception {
        MultipartConfigElement config = new MultipartConfigElement(_dirname, 1024, 3072, 50);
        MultiPartInputStreamParser mpis = new MultiPartInputStreamParser(new ByteArrayInputStream(createMultipartRequestString("tptfd").getBytes()), _contentType, config, _tmpDir);
        mpis.setDeleteOnExit(true);
        Collection<Part> parts = mpis.getParts();
        MultiPart part = ((MultiPart) (mpis.getPart("stuff")));
        File stuff = getFile();
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
        MultiPartInputStreamParser mpis = new MultiPartInputStreamParser(new ByteArrayInputStream(createMultipartRequestString("tptfd").getBytes()), _contentType, config, _tmpDir);
        mpis.setDeleteOnExit(true);
        Collection<Part> parts = mpis.getParts();
        MultiPart part = ((MultiPart) (mpis.getPart("stuff")));
        File stuff = getFile();
        MatcherAssert.assertThat(stuff, Matchers.notNullValue());// longer than 100 bytes, should already be a tmp file

        MatcherAssert.assertThat(stuff.exists(), Matchers.is(true));
        part.cleanUp();
        MatcherAssert.assertThat(stuff.exists(), Matchers.is(false));// tmp file was removed after cleanup

    }

    @Test
    public void testLFOnlyRequest() throws Exception {
        String str = "--AaB03x\n" + ((((((("content-disposition: form-data; name=\"field1\"\n" + "\n") + "Joe Blow\n") + "--AaB03x\n") + "content-disposition: form-data; name=\"field2\"\n") + "\n") + "Other\n") + "--AaB03x--\n");
        MultipartConfigElement config = new MultipartConfigElement(_dirname, 1024, 3072, 50);
        MultiPartInputStreamParser mpis = new MultiPartInputStreamParser(new ByteArrayInputStream(str.getBytes()), _contentType, config, _tmpDir);
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
        Assertions.assertEquals(EnumSet.of(LF_LINE_TERMINATION), mpis.getNonComplianceWarnings());
    }

    @Test
    public void testCROnlyRequest() throws Exception {
        String str = "--AaB03x\r" + ((((((("content-disposition: form-data; name=\"field1\"\r" + "\r") + "Joe Blow\r") + "--AaB03x\r") + "content-disposition: form-data; name=\"field2\"\r") + "\r") + "Other\r") + "--AaB03x--\r");
        MultipartConfigElement config = new MultipartConfigElement(_dirname, 1024, 3072, 50);
        MultiPartInputStreamParser mpis = new MultiPartInputStreamParser(new ByteArrayInputStream(str.getBytes()), _contentType, config, _tmpDir);
        mpis.setDeleteOnExit(true);
        Collection<Part> parts = mpis.getParts();
        MatcherAssert.assertThat(parts.size(), Matchers.is(2));
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
        Assertions.assertEquals(EnumSet.of(CR_LINE_TERMINATION), mpis.getNonComplianceWarnings());
    }

    @Test
    public void testCRandLFMixRequest() throws Exception {
        String str = "--AaB03x\r" + (((((((("content-disposition: form-data; name=\"field1\"\r" + "\r") + "\nJoe Blow\n") + "\r") + "--AaB03x\r") + "content-disposition: form-data; name=\"field2\"\r") + "\r") + "Other\r") + "--AaB03x--\r");
        MultipartConfigElement config = new MultipartConfigElement(_dirname, 1024, 3072, 50);
        MultiPartInputStreamParser mpis = new MultiPartInputStreamParser(new ByteArrayInputStream(str.getBytes()), _contentType, config, _tmpDir);
        mpis.setDeleteOnExit(true);
        Collection<Part> parts = mpis.getParts();
        MatcherAssert.assertThat(parts.size(), Matchers.is(2));
        Part p1 = mpis.getPart("field1");
        MatcherAssert.assertThat(p1, Matchers.notNullValue());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        IO.copy(p1.getInputStream(), baos);
        MatcherAssert.assertThat(baos.toString("UTF-8"), Matchers.is("\nJoe Blow\n"));
        Part p2 = mpis.getPart("field2");
        MatcherAssert.assertThat(p2, Matchers.notNullValue());
        baos = new ByteArrayOutputStream();
        IO.copy(p2.getInputStream(), baos);
        MatcherAssert.assertThat(baos.toString("UTF-8"), Matchers.is("Other"));
        Assertions.assertEquals(EnumSet.of(CR_LINE_TERMINATION), mpis.getNonComplianceWarnings());
    }

    @Test
    public void testBufferOverflowNoCRLF() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write("--AaB03x".getBytes());
        // create content that will overrun default buffer size of BufferedInputStream
        for (int i = 0; i < 8500; i++) {
            baos.write('a');
        }
        MultipartConfigElement config = new MultipartConfigElement(_dirname, 1024, 3072, 50);
        MultiPartInputStreamParser mpis = new MultiPartInputStreamParser(new ByteArrayInputStream(baos.toByteArray()), _contentType, config, _tmpDir);
        mpis.setDeleteOnExit(true);
        try {
            mpis.getParts();
            Assertions.fail("Multipart buffer overrun");
        } catch (IOException e) {
            Assertions.assertTrue(e.getMessage().startsWith("Buffer size exceeded"));
        }
    }

    @Test
    public void testCharsetEncoding() throws Exception {
        String contentType = "multipart/form-data; boundary=TheBoundary; charset=ISO-8859-1";
        String str = "--TheBoundary\r" + (((("content-disposition: form-data; name=\"field1\"\r" + "\r") + "\nJoe Blow\n") + "\r") + "--TheBoundary--\r");
        MultipartConfigElement config = new MultipartConfigElement(_dirname, 1024, 3072, 50);
        MultiPartInputStreamParser mpis = new MultiPartInputStreamParser(new ByteArrayInputStream(str.getBytes()), contentType, config, _tmpDir);
        mpis.setDeleteOnExit(true);
        Collection<Part> parts = mpis.getParts();
        MatcherAssert.assertThat(parts.size(), Matchers.is(1));
    }

    @Test
    public void testBadlyEncodedFilename() throws Exception {
        String contents = "--AaB03x\r\n" + (((((((("content-disposition: form-data; name=\"stuff\"; filename=\"" + "Taken on Aug 22 \\ 2012.jpg") + "\"\r\n") + "Content-Type: text/plain\r\n") + "\r\n") + "stuff") + "aaa") + "\r\n") + "--AaB03x--\r\n");
        MultipartConfigElement config = new MultipartConfigElement(_dirname, 1024, 3072, 50);
        MultiPartInputStreamParser mpis = new MultiPartInputStreamParser(new ByteArrayInputStream(contents.getBytes()), _contentType, config, _tmpDir);
        mpis.setDeleteOnExit(true);
        Collection<Part> parts = mpis.getParts();
        MatcherAssert.assertThat(parts.size(), Matchers.is(1));
        MatcherAssert.assertThat(getSubmittedFileName(), Matchers.is("Taken on Aug 22 \\ 2012.jpg"));
    }

    @Test
    public void testBadlyEncodedMSFilename() throws Exception {
        String contents = "--AaB03x\r\n" + (((((((("content-disposition: form-data; name=\"stuff\"; filename=\"" + "c:\\this\\really\\is\\some\\path\\to\\a\\file.txt") + "\"\r\n") + "Content-Type: text/plain\r\n") + "\r\n") + "stuff") + "aaa") + "\r\n") + "--AaB03x--\r\n");
        MultipartConfigElement config = new MultipartConfigElement(_dirname, 1024, 3072, 50);
        MultiPartInputStreamParser mpis = new MultiPartInputStreamParser(new ByteArrayInputStream(contents.getBytes()), _contentType, config, _tmpDir);
        mpis.setDeleteOnExit(true);
        Collection<Part> parts = mpis.getParts();
        MatcherAssert.assertThat(parts.size(), Matchers.is(1));
        MatcherAssert.assertThat(getSubmittedFileName(), Matchers.is("c:\\this\\really\\is\\some\\path\\to\\a\\file.txt"));
    }

    @Test
    public void testCorrectlyEncodedMSFilename() throws Exception {
        String contents = "--AaB03x\r\n" + (((((((("content-disposition: form-data; name=\"stuff\"; filename=\"" + "c:\\\\this\\\\really\\\\is\\\\some\\\\path\\\\to\\\\a\\\\file.txt") + "\"\r\n") + "Content-Type: text/plain\r\n") + "\r\n") + "stuff") + "aaa") + "\r\n") + "--AaB03x--\r\n");
        MultipartConfigElement config = new MultipartConfigElement(_dirname, 1024, 3072, 50);
        MultiPartInputStreamParser mpis = new MultiPartInputStreamParser(new ByteArrayInputStream(contents.getBytes()), _contentType, config, _tmpDir);
        mpis.setDeleteOnExit(true);
        Collection<Part> parts = mpis.getParts();
        MatcherAssert.assertThat(parts.size(), Matchers.is(1));
        MatcherAssert.assertThat(getSubmittedFileName(), Matchers.is("c:\\this\\really\\is\\some\\path\\to\\a\\file.txt"));
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
        MultiPartInputStreamParser mpis = new MultiPartInputStreamParser(new ByteArrayInputStream(s.getBytes()), _contentType, config, _tmpDir);
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
        MultiPartInputStreamParser mpis = new MultiPartInputStreamParser(new ByteArrayInputStream(sameNames.getBytes()), _contentType, config, _tmpDir);
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
        MultiPartInputStreamParser mpis = new MultiPartInputStreamParser(new ByteArrayInputStream(contentWithEncodedPart.getBytes()), _contentType, config, _tmpDir);
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
        Assertions.assertEquals("hello jetty", baos.toString("US-ASCII"));
        Part p3 = mpis.getPart("final");
        Assertions.assertNotNull(p3);
        baos = new ByteArrayOutputStream();
        IO.copy(p3.getInputStream(), baos);
        Assertions.assertEquals("the end", baos.toString("US-ASCII"));
        Assertions.assertEquals(EnumSet.of(BASE64_TRANSFER_ENCODING), mpis.getNonComplianceWarnings());
    }

    @Test
    public void testQuotedPrintableEncoding() throws Exception {
        String contentWithEncodedPart = "--AaB03x\r\n" + (((((((((((("Content-disposition: form-data; name=\"other\"\r\n" + "Content-Type: text/plain\r\n") + "\r\n") + "other") + "\r\n") + "--AaB03x\r\n") + "Content-disposition: form-data; name=\"stuff\"; filename=\"stuff.txt\"\r\n") + "Content-Transfer-Encoding: quoted-printable\r\n") + "Content-Type: text/plain\r\n") + "\r\n") + "truth=3Dbeauty") + "\r\n") + "--AaB03x--\r\n");
        MultipartConfigElement config = new MultipartConfigElement(_dirname, 1024, 3072, 50);
        MultiPartInputStreamParser mpis = new MultiPartInputStreamParser(new ByteArrayInputStream(contentWithEncodedPart.getBytes()), _contentType, config, _tmpDir);
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
        Assertions.assertEquals("truth=beauty", baos.toString("US-ASCII"));
        Assertions.assertEquals(EnumSet.of(QUOTED_PRINTABLE_TRANSFER_ENCODING), mpis.getNonComplianceWarnings());
    }
}

