/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
package com.liferay.petra.nio;


import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.CodeCoverageAssertor;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.MalformedInputException;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;


/**
 *
 *
 * @author Shuyang Zhou
 */
public class CharsetDecoderUtilTest {
    @ClassRule
    public static final CodeCoverageAssertor codeCoverageAssertor = CodeCoverageAssertor.INSTANCE;

    @Test
    public void testConstructor() {
        new CharsetDecoderUtil();
    }

    @Test
    public void testDecode() throws Exception {
        Assert.assertEquals(CharBuffer.wrap("abc"), CharsetDecoderUtil.decode("US-ASCII", CodingErrorAction.REPORT, ByteBuffer.wrap("abc".getBytes("US-ASCII"))));
        try {
            CharsetDecoderUtil.decode("US-ASCII", CodingErrorAction.REPORT, ByteBuffer.wrap(new byte[]{ -1, -2, -3, -4 }));
            Assert.fail();
        } catch (MalformedInputException mie) {
            Assert.assertEquals(1, mie.getInputLength());
        }
        TestCharset testCharset = new TestCharset();
        Object cache1 = ReflectionTestUtil.getAndSetFieldValue(Charset.class, "cache1", new Object[]{ testCharset.name(), testCharset });
        try {
            CharsetDecoderUtil.decode(testCharset.name(), ByteBuffer.wrap(new byte[0]));
            Assert.fail();
        } catch (Error e) {
            Assert.assertSame(testCharset.getCharacterCodingException(), e.getCause());
        } finally {
            ReflectionTestUtil.setFieldValue(Charset.class, "cache1", cache1);
        }
    }

    @Test
    public void testGetCharsetDecoder() {
        CharsetDecoder charsetDecoder = CharsetDecoderUtil.getCharsetDecoder("UTF-8");
        Assert.assertEquals(Charset.forName("UTF-8"), charsetDecoder.charset());
        Assert.assertSame(CodingErrorAction.REPLACE, charsetDecoder.malformedInputAction());
        Assert.assertSame(CodingErrorAction.REPLACE, charsetDecoder.unmappableCharacterAction());
    }
}

