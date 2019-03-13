/**
 * Copyright 2014-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.session.web.http;


import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class OnCommittedResponseWrapperTests {
    private static final String NL = "\r\n";

    @Mock
    HttpServletResponse delegate;

    @Mock
    PrintWriter writer;

    @Mock
    ServletOutputStream out;

    OnCommittedResponseWrapper response;

    boolean committed;

    // --- printwriter
    @Test
    public void printWriterHashCode() throws Exception {
        int expected = this.writer.hashCode();
        assertThat(this.response.getWriter().hashCode()).isEqualTo(expected);
    }

    @Test
    public void printWriterCheckError() throws Exception {
        boolean expected = true;
        BDDMockito.given(this.writer.checkError()).willReturn(expected);
        assertThat(this.response.getWriter().checkError()).isEqualTo(expected);
    }

    @Test
    public void printWriterWriteInt() throws Exception {
        int expected = 1;
        this.response.getWriter().write(expected);
        Mockito.verify(this.writer).write(expected);
    }

    @Test
    public void printWriterWriteCharIntInt() throws Exception {
        char[] buff = new char[0];
        int off = 2;
        int len = 3;
        this.response.getWriter().write(buff, off, len);
        Mockito.verify(this.writer).write(buff, off, len);
    }

    @Test
    public void printWriterWriteChar() throws Exception {
        char[] buff = new char[0];
        this.response.getWriter().write(buff);
        Mockito.verify(this.writer).write(buff);
    }

    @Test
    public void printWriterWriteStringIntInt() throws Exception {
        String s = "";
        int off = 2;
        int len = 3;
        this.response.getWriter().write(s, off, len);
        Mockito.verify(this.writer).write(s, off, len);
    }

    @Test
    public void printWriterWriteString() throws Exception {
        String s = "";
        this.response.getWriter().write(s);
        Mockito.verify(this.writer).write(s);
    }

    @Test
    public void printWriterPrintBoolean() throws Exception {
        boolean b = true;
        this.response.getWriter().print(b);
        Mockito.verify(this.writer).print(b);
    }

    @Test
    public void printWriterPrintChar() throws Exception {
        char c = 1;
        this.response.getWriter().print(c);
        Mockito.verify(this.writer).print(c);
    }

    @Test
    public void printWriterPrintInt() throws Exception {
        int i = 1;
        this.response.getWriter().print(i);
        Mockito.verify(this.writer).print(i);
    }

    @Test
    public void printWriterPrintLong() throws Exception {
        long l = 1;
        this.response.getWriter().print(l);
        Mockito.verify(this.writer).print(l);
    }

    @Test
    public void printWriterPrintFloat() throws Exception {
        float f = 1;
        this.response.getWriter().print(f);
        Mockito.verify(this.writer).print(f);
    }

    @Test
    public void printWriterPrintDouble() throws Exception {
        double x = 1;
        this.response.getWriter().print(x);
        Mockito.verify(this.writer).print(x);
    }

    @Test
    public void printWriterPrintCharArray() throws Exception {
        char[] x = new char[0];
        this.response.getWriter().print(x);
        Mockito.verify(this.writer).print(x);
    }

    @Test
    public void printWriterPrintString() throws Exception {
        String x = "1";
        this.response.getWriter().print(x);
        Mockito.verify(this.writer).print(x);
    }

    @Test
    public void printWriterPrintObject() throws Exception {
        Object x = "1";
        this.response.getWriter().print(x);
        Mockito.verify(this.writer).print(x);
    }

    @Test
    public void printWriterPrintln() throws Exception {
        this.response.getWriter().println();
        Mockito.verify(this.writer).println();
    }

    @Test
    public void printWriterPrintlnBoolean() throws Exception {
        boolean b = true;
        this.response.getWriter().println(b);
        Mockito.verify(this.writer).println(b);
    }

    @Test
    public void printWriterPrintlnChar() throws Exception {
        char c = 1;
        this.response.getWriter().println(c);
        Mockito.verify(this.writer).println(c);
    }

    @Test
    public void printWriterPrintlnInt() throws Exception {
        int i = 1;
        this.response.getWriter().println(i);
        Mockito.verify(this.writer).println(i);
    }

    @Test
    public void printWriterPrintlnLong() throws Exception {
        long l = 1;
        this.response.getWriter().println(l);
        Mockito.verify(this.writer).println(l);
    }

    @Test
    public void printWriterPrintlnFloat() throws Exception {
        float f = 1;
        this.response.getWriter().println(f);
        Mockito.verify(this.writer).println(f);
    }

    @Test
    public void printWriterPrintlnDouble() throws Exception {
        double x = 1;
        this.response.getWriter().println(x);
        Mockito.verify(this.writer).println(x);
    }

    @Test
    public void printWriterPrintlnCharArray() throws Exception {
        char[] x = new char[0];
        this.response.getWriter().println(x);
        Mockito.verify(this.writer).println(x);
    }

    @Test
    public void printWriterPrintlnString() throws Exception {
        String x = "1";
        this.response.getWriter().println(x);
        Mockito.verify(this.writer).println(x);
    }

    @Test
    public void printWriterPrintlnObject() throws Exception {
        Object x = "1";
        this.response.getWriter().println(x);
        Mockito.verify(this.writer).println(x);
    }

    @Test
    public void printWriterPrintfStringObjectVargs() throws Exception {
        String format = "format";
        Object[] args = new Object[]{ "1" };
        this.response.getWriter().printf(format, args);
        Mockito.verify(this.writer).printf(format, args);
    }

    @Test
    public void printWriterPrintfLocaleStringObjectVargs() throws Exception {
        Locale l = Locale.US;
        String format = "format";
        Object[] args = new Object[]{ "1" };
        this.response.getWriter().printf(l, format, args);
        Mockito.verify(this.writer).printf(l, format, args);
    }

    @Test
    public void printWriterFormatStringObjectVargs() throws Exception {
        String format = "format";
        Object[] args = new Object[]{ "1" };
        this.response.getWriter().format(format, args);
        Mockito.verify(this.writer).format(format, args);
    }

    @Test
    public void printWriterFormatLocaleStringObjectVargs() throws Exception {
        Locale l = Locale.US;
        String format = "format";
        Object[] args = new Object[]{ "1" };
        this.response.getWriter().format(l, format, args);
        Mockito.verify(this.writer).format(l, format, args);
    }

    @Test
    public void printWriterAppendCharSequence() throws Exception {
        String x = "a";
        this.response.getWriter().append(x);
        Mockito.verify(this.writer).append(x);
    }

    @Test
    public void printWriterAppendCharSequenceIntInt() throws Exception {
        String x = "abcdef";
        int start = 1;
        int end = 3;
        this.response.getWriter().append(x, start, end);
        Mockito.verify(this.writer).append(x, start, end);
    }

    @Test
    public void printWriterAppendChar() throws Exception {
        char x = 1;
        this.response.getWriter().append(x);
        Mockito.verify(this.writer).append(x);
    }

    // servletoutputstream
    @Test
    public void outputStreamHashCode() throws Exception {
        int expected = this.out.hashCode();
        assertThat(this.response.getOutputStream().hashCode()).isEqualTo(expected);
    }

    @Test
    public void outputStreamWriteInt() throws Exception {
        int expected = 1;
        this.response.getOutputStream().write(expected);
        Mockito.verify(this.out).write(expected);
    }

    @Test
    public void outputStreamWriteByte() throws Exception {
        byte[] expected = new byte[0];
        this.response.getOutputStream().write(expected);
        Mockito.verify(this.out).write(expected);
    }

    @Test
    public void outputStreamWriteByteIntInt() throws Exception {
        int start = 1;
        int end = 2;
        byte[] expected = new byte[0];
        this.response.getOutputStream().write(expected, start, end);
        Mockito.verify(this.out).write(expected, start, end);
    }

    @Test
    public void outputStreamPrintBoolean() throws Exception {
        boolean b = true;
        this.response.getOutputStream().print(b);
        Mockito.verify(this.out).print(b);
    }

    @Test
    public void outputStreamPrintChar() throws Exception {
        char c = 1;
        this.response.getOutputStream().print(c);
        Mockito.verify(this.out).print(c);
    }

    @Test
    public void outputStreamPrintInt() throws Exception {
        int i = 1;
        this.response.getOutputStream().print(i);
        Mockito.verify(this.out).print(i);
    }

    @Test
    public void outputStreamPrintLong() throws Exception {
        long l = 1;
        this.response.getOutputStream().print(l);
        Mockito.verify(this.out).print(l);
    }

    @Test
    public void outputStreamPrintFloat() throws Exception {
        float f = 1;
        this.response.getOutputStream().print(f);
        Mockito.verify(this.out).print(f);
    }

    @Test
    public void outputStreamPrintDouble() throws Exception {
        double x = 1;
        this.response.getOutputStream().print(x);
        Mockito.verify(this.out).print(x);
    }

    @Test
    public void outputStreamPrintString() throws Exception {
        String x = "1";
        this.response.getOutputStream().print(x);
        Mockito.verify(this.out).print(x);
    }

    @Test
    public void outputStreamPrintln() throws Exception {
        this.response.getOutputStream().println();
        Mockito.verify(this.out).println();
    }

    @Test
    public void outputStreamPrintlnBoolean() throws Exception {
        boolean b = true;
        this.response.getOutputStream().println(b);
        Mockito.verify(this.out).println(b);
    }

    @Test
    public void outputStreamPrintlnChar() throws Exception {
        char c = 1;
        this.response.getOutputStream().println(c);
        Mockito.verify(this.out).println(c);
    }

    @Test
    public void outputStreamPrintlnInt() throws Exception {
        int i = 1;
        this.response.getOutputStream().println(i);
        Mockito.verify(this.out).println(i);
    }

    @Test
    public void outputStreamPrintlnLong() throws Exception {
        long l = 1;
        this.response.getOutputStream().println(l);
        Mockito.verify(this.out).println(l);
    }

    @Test
    public void outputStreamPrintlnFloat() throws Exception {
        float f = 1;
        this.response.getOutputStream().println(f);
        Mockito.verify(this.out).println(f);
    }

    @Test
    public void outputStreamPrintlnDouble() throws Exception {
        double x = 1;
        this.response.getOutputStream().println(x);
        Mockito.verify(this.out).println(x);
    }

    @Test
    public void outputStreamPrintlnString() throws Exception {
        String x = "1";
        this.response.getOutputStream().println(x);
        Mockito.verify(this.out).println(x);
    }

    // The amount of content specified in the setContentLength method of the response
    // has been greater than zero and has been written to the response.
    @Test
    public void contentLengthPrintWriterWriteIntCommits() throws Exception {
        int expected = 1;
        this.response.setContentLength(String.valueOf(expected).length());
        this.response.getWriter().write(expected);
        assertThat(this.committed).isTrue();
    }

    @Test
    public void contentLengthPrintWriterWriteIntMultiDigitCommits() throws Exception {
        int expected = 10000;
        this.response.setContentLength(String.valueOf(expected).length());
        this.response.getWriter().write(expected);
        assertThat(this.committed).isTrue();
    }

    @Test
    public void contentLengthPlus1PrintWriterWriteIntMultiDigitCommits() throws Exception {
        int expected = 10000;
        this.response.setContentLength(((String.valueOf(expected).length()) + 1));
        this.response.getWriter().write(expected);
        assertThat(this.committed).isFalse();
        this.response.getWriter().write(1);
        assertThat(this.committed).isTrue();
    }

    @Test
    public void contentLengthPrintWriterWriteCharIntIntCommits() throws Exception {
        char[] buff = new char[0];
        int off = 2;
        int len = 3;
        this.response.setContentLength(3);
        this.response.getWriter().write(buff, off, len);
        assertThat(this.committed).isTrue();
    }

    @Test
    public void contentLengthPrintWriterWriteCharCommits() throws Exception {
        char[] buff = new char[4];
        this.response.setContentLength(buff.length);
        this.response.getWriter().write(buff);
        assertThat(this.committed).isTrue();
    }

    @Test
    public void contentLengthPrintWriterWriteStringIntIntCommits() throws Exception {
        String s = "";
        int off = 2;
        int len = 3;
        this.response.setContentLength(3);
        this.response.getWriter().write(s, off, len);
        assertThat(this.committed).isTrue();
    }

    @Test
    public void contentLengthPrintWriterWriteStringCommits() throws IOException {
        String body = "something";
        this.response.setContentLength(body.length());
        this.response.getWriter().write(body);
        assertThat(this.committed).isTrue();
    }

    @Test
    public void printWriterWriteStringContentLengthCommits() throws IOException {
        String body = "something";
        this.response.getWriter().write(body);
        this.response.setContentLength(body.length());
        assertThat(this.committed).isTrue();
    }

    @Test
    public void printWriterWriteStringDoesNotCommit() throws IOException {
        String body = "something";
        this.response.getWriter().write(body);
        assertThat(this.committed).isFalse();
    }

    @Test
    public void contentLengthPrintWriterPrintBooleanCommits() throws Exception {
        boolean b = true;
        this.response.setContentLength(1);
        this.response.getWriter().print(b);
        assertThat(this.committed).isTrue();
    }

    @Test
    public void contentLengthPrintWriterPrintCharCommits() throws Exception {
        char c = 1;
        this.response.setContentLength(1);
        this.response.getWriter().print(c);
        assertThat(this.committed).isTrue();
    }

    @Test
    public void contentLengthPrintWriterPrintIntCommits() throws Exception {
        int i = 1234;
        this.response.setContentLength(String.valueOf(i).length());
        this.response.getWriter().print(i);
        assertThat(this.committed).isTrue();
    }

    @Test
    public void contentLengthPrintWriterPrintLongCommits() throws Exception {
        long l = 12345;
        this.response.setContentLength(String.valueOf(l).length());
        this.response.getWriter().print(l);
        assertThat(this.committed).isTrue();
    }

    @Test
    public void contentLengthPrintWriterPrintFloatCommits() throws Exception {
        float f = 12345;
        this.response.setContentLength(String.valueOf(f).length());
        this.response.getWriter().print(f);
        assertThat(this.committed).isTrue();
    }

    @Test
    public void contentLengthPrintWriterPrintDoubleCommits() throws Exception {
        double x = 1.2345;
        this.response.setContentLength(String.valueOf(x).length());
        this.response.getWriter().print(x);
        assertThat(this.committed).isTrue();
    }

    @Test
    public void contentLengthPrintWriterPrintCharArrayCommits() throws Exception {
        char[] x = new char[10];
        this.response.setContentLength(x.length);
        this.response.getWriter().print(x);
        assertThat(this.committed).isTrue();
    }

    @Test
    public void contentLengthPrintWriterPrintStringCommits() throws Exception {
        String x = "12345";
        this.response.setContentLength(x.length());
        this.response.getWriter().print(x);
        assertThat(this.committed).isTrue();
    }

    @Test
    public void contentLengthPrintWriterPrintObjectCommits() throws Exception {
        Object x = "12345";
        this.response.setContentLength(String.valueOf(x).length());
        this.response.getWriter().print(x);
        assertThat(this.committed).isTrue();
    }

    @Test
    public void contentLengthPrintWriterPrintlnCommits() throws Exception {
        this.response.setContentLength(OnCommittedResponseWrapperTests.NL.length());
        this.response.getWriter().println();
        assertThat(this.committed).isTrue();
    }

    @Test
    public void contentLengthPrintWriterPrintlnBooleanCommits() throws Exception {
        boolean b = true;
        this.response.setContentLength(1);
        this.response.getWriter().println(b);
        assertThat(this.committed).isTrue();
    }

    @Test
    public void contentLengthPrintWriterPrintlnCharCommits() throws Exception {
        char c = 1;
        this.response.setContentLength(1);
        this.response.getWriter().println(c);
        assertThat(this.committed).isTrue();
    }

    @Test
    public void contentLengthPrintWriterPrintlnIntCommits() throws Exception {
        int i = 12345;
        this.response.setContentLength(String.valueOf(i).length());
        this.response.getWriter().println(i);
        assertThat(this.committed).isTrue();
    }

    @Test
    public void contentLengthPrintWriterPrintlnLongCommits() throws Exception {
        long l = 12345678;
        this.response.setContentLength(String.valueOf(l).length());
        this.response.getWriter().println(l);
        assertThat(this.committed).isTrue();
    }

    @Test
    public void contentLengthPrintWriterPrintlnFloatCommits() throws Exception {
        float f = 1234;
        this.response.setContentLength(String.valueOf(f).length());
        this.response.getWriter().println(f);
        assertThat(this.committed).isTrue();
    }

    @Test
    public void contentLengthPrintWriterPrintlnDoubleCommits() throws Exception {
        double x = 1;
        this.response.setContentLength(String.valueOf(x).length());
        this.response.getWriter().println(x);
        assertThat(this.committed).isTrue();
    }

    @Test
    public void contentLengthPrintWriterPrintlnCharArrayCommits() throws Exception {
        char[] x = new char[20];
        this.response.setContentLength(x.length);
        this.response.getWriter().println(x);
        assertThat(this.committed).isTrue();
    }

    @Test
    public void contentLengthPrintWriterPrintlnStringCommits() throws Exception {
        String x = "1";
        this.response.setContentLength(String.valueOf(x).length());
        this.response.getWriter().println(x);
        assertThat(this.committed).isTrue();
    }

    @Test
    public void contentLengthPrintWriterPrintlnObjectCommits() throws Exception {
        Object x = "1";
        this.response.setContentLength(String.valueOf(x).length());
        this.response.getWriter().println(x);
        assertThat(this.committed).isTrue();
    }

    @Test
    public void contentLengthPrintWriterAppendCharSequenceCommits() throws Exception {
        String x = "a";
        this.response.setContentLength(String.valueOf(x).length());
        this.response.getWriter().append(x);
        assertThat(this.committed).isTrue();
    }

    @Test
    public void contentLengthPrintWriterAppendCharSequenceIntIntCommits() throws Exception {
        String x = "abcdef";
        int start = 1;
        int end = 3;
        this.response.setContentLength((end - start));
        this.response.getWriter().append(x, start, end);
        assertThat(this.committed).isTrue();
    }

    @Test
    public void contentLengthPrintWriterAppendCharCommits() throws Exception {
        char x = 1;
        this.response.setContentLength(1);
        this.response.getWriter().append(x);
        assertThat(this.committed).isTrue();
    }

    @Test
    public void contentLengthOutputStreamWriteIntCommits() throws Exception {
        int expected = 1;
        this.response.setContentLength(String.valueOf(expected).length());
        this.response.getOutputStream().write(expected);
        assertThat(this.committed).isTrue();
    }

    @Test
    public void contentLengthOutputStreamWriteIntMultiDigitCommits() throws Exception {
        int expected = 10000;
        this.response.setContentLength(String.valueOf(expected).length());
        this.response.getOutputStream().write(expected);
        assertThat(this.committed).isTrue();
    }

    @Test
    public void contentLengthPlus1OutputStreamWriteIntMultiDigitCommits() throws Exception {
        int expected = 10000;
        this.response.setContentLength(((String.valueOf(expected).length()) + 1));
        this.response.getOutputStream().write(expected);
        assertThat(this.committed).isFalse();
        this.response.getOutputStream().write(1);
        assertThat(this.committed).isTrue();
    }

    // gh-171
    @Test
    public void contentLengthPlus1OutputStreamWriteByteArrayMultiDigitCommits() throws Exception {
        String expected = "{\n" + ((("  \"parameterName\" : \"_csrf\",\n" + "  \"token\" : \"06300b65-c4aa-4c8f-8cda-39ee17f545a0\",\n") + "  \"headerName\" : \"X-CSRF-TOKEN\"\n") + "}");
        this.response.setContentLength(((expected.length()) + 1));
        this.response.getOutputStream().write(expected.getBytes());
        assertThat(this.committed).isFalse();
        this.response.getOutputStream().write("1".getBytes("UTF-8"));
        assertThat(this.committed).isTrue();
    }

    @Test
    public void contentLengthOutputStreamPrintBooleanCommits() throws Exception {
        boolean b = true;
        this.response.setContentLength(1);
        this.response.getOutputStream().print(b);
        assertThat(this.committed).isTrue();
    }

    @Test
    public void contentLengthOutputStreamPrintCharCommits() throws Exception {
        char c = 1;
        this.response.setContentLength(1);
        this.response.getOutputStream().print(c);
        assertThat(this.committed).isTrue();
    }

    @Test
    public void contentLengthOutputStreamPrintIntCommits() throws Exception {
        int i = 1234;
        this.response.setContentLength(String.valueOf(i).length());
        this.response.getOutputStream().print(i);
        assertThat(this.committed).isTrue();
    }

    @Test
    public void contentLengthOutputStreamPrintLongCommits() throws Exception {
        long l = 12345;
        this.response.setContentLength(String.valueOf(l).length());
        this.response.getOutputStream().print(l);
        assertThat(this.committed).isTrue();
    }

    @Test
    public void contentLengthOutputStreamPrintFloatCommits() throws Exception {
        float f = 12345;
        this.response.setContentLength(String.valueOf(f).length());
        this.response.getOutputStream().print(f);
        assertThat(this.committed).isTrue();
    }

    @Test
    public void contentLengthOutputStreamPrintDoubleCommits() throws Exception {
        double x = 1.2345;
        this.response.setContentLength(String.valueOf(x).length());
        this.response.getOutputStream().print(x);
        assertThat(this.committed).isTrue();
    }

    @Test
    public void contentLengthOutputStreamPrintStringCommits() throws Exception {
        String x = "12345";
        this.response.setContentLength(x.length());
        this.response.getOutputStream().print(x);
        assertThat(this.committed).isTrue();
    }

    @Test
    public void contentLengthOutputStreamPrintlnCommits() throws Exception {
        this.response.setContentLength(OnCommittedResponseWrapperTests.NL.length());
        this.response.getOutputStream().println();
        assertThat(this.committed).isTrue();
    }

    @Test
    public void contentLengthOutputStreamPrintlnBooleanCommits() throws Exception {
        boolean b = true;
        this.response.setContentLength(1);
        this.response.getOutputStream().println(b);
        assertThat(this.committed).isTrue();
    }

    @Test
    public void contentLengthOutputStreamPrintlnCharCommits() throws Exception {
        char c = 1;
        this.response.setContentLength(1);
        this.response.getOutputStream().println(c);
        assertThat(this.committed).isTrue();
    }

    @Test
    public void contentLengthOutputStreamPrintlnIntCommits() throws Exception {
        int i = 12345;
        this.response.setContentLength(String.valueOf(i).length());
        this.response.getOutputStream().println(i);
        assertThat(this.committed).isTrue();
    }

    @Test
    public void contentLengthOutputStreamPrintlnLongCommits() throws Exception {
        long l = 12345678;
        this.response.setContentLength(String.valueOf(l).length());
        this.response.getOutputStream().println(l);
        assertThat(this.committed).isTrue();
    }

    @Test
    public void contentLengthOutputStreamPrintlnFloatCommits() throws Exception {
        float f = 1234;
        this.response.setContentLength(String.valueOf(f).length());
        this.response.getOutputStream().println(f);
        assertThat(this.committed).isTrue();
    }

    @Test
    public void contentLengthOutputStreamPrintlnDoubleCommits() throws Exception {
        double x = 1;
        this.response.setContentLength(String.valueOf(x).length());
        this.response.getOutputStream().println(x);
        assertThat(this.committed).isTrue();
    }

    @Test
    public void contentLengthOutputStreamPrintlnStringCommits() throws Exception {
        String x = "1";
        this.response.setContentLength(String.valueOf(x).length());
        this.response.getOutputStream().println(x);
        assertThat(this.committed).isTrue();
    }

    @Test
    public void contentLengthDoesNotCommit() throws IOException {
        String body = "something";
        this.response.setContentLength(body.length());
        assertThat(this.committed).isFalse();
    }

    @Test
    public void contentLengthOutputStreamWriteStringCommits() throws IOException {
        String body = "something";
        this.response.setContentLength(body.length());
        this.response.getOutputStream().print(body);
        assertThat(this.committed).isTrue();
    }

    @Test
    public void addHeaderContentLengthPrintWriterWriteStringCommits() throws Exception {
        int expected = 1234;
        this.response.addHeader("Content-Length", String.valueOf(String.valueOf(expected).length()));
        this.response.getWriter().write(expected);
        assertThat(this.committed).isTrue();
    }

    @Test
    public void bufferSizePrintWriterWriteCommits() throws Exception {
        String expected = "1234567890";
        BDDMockito.given(this.response.getBufferSize()).willReturn(expected.length());
        this.response.getWriter().write(expected);
        assertThat(this.committed).isTrue();
    }

    @Test
    public void bufferSizeCommitsOnce() throws Exception {
        String expected = "1234567890";
        BDDMockito.given(this.response.getBufferSize()).willReturn(expected.length());
        this.response.getWriter().write(expected);
        assertThat(this.committed).isTrue();
        this.committed = false;
        this.response.getWriter().write(expected);
        assertThat(this.committed).isFalse();
    }
}

