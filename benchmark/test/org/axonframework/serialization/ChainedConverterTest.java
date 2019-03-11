/**
 * Copyright (c) 2010-2018. Axon Framework
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.axonframework.serialization;


import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.lang.annotation.Documented;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 *
 *
 * @author Allard Buijze
 */
public class ChainedConverterTest {
    private ContentTypeConverter testSubject;

    private Object source;

    private Class<?> target;

    private List<ContentTypeConverter<?, ?>> candidates;

    private ContentTypeConverter<?, ?> stringToReaderConverter;

    private ContentTypeConverter<?, ?> stringToByteConverter;

    private ContentTypeConverter<?, ?> bytesToInputStreamConverter;

    private ContentTypeConverter<?, ?> numberToStringConverter;

    @Test
    public void testComplexRoute() throws Exception {
        target = InputStream.class;
        source = 1L;
        testSubject = ChainedConverter.calculateChain(Number.class, target, candidates);
        Assert.assertNotNull(testSubject);
        Mockito.verify(numberToStringConverter, Mockito.never()).convert(ArgumentMatchers.any());
        Mockito.verify(stringToReaderConverter, Mockito.never()).convert(ArgumentMatchers.any());
        Mockito.verify(bytesToInputStreamConverter, Mockito.never()).convert(ArgumentMatchers.any());
        Mockito.verify(stringToByteConverter, Mockito.never()).convert(ArgumentMatchers.any());
        InputStream actual = convertSource();
        Assert.assertNotNull(actual);
        Assert.assertArrayEquals("hello".getBytes(), IOUtils.toByteArray(actual));
        Mockito.verify(numberToStringConverter).convert(ArgumentMatchers.any());
        Mockito.verify(stringToByteConverter).convert(ArgumentMatchers.any());
        Mockito.verify(bytesToInputStreamConverter).convert(ArgumentMatchers.any());
        Mockito.verify(stringToReaderConverter, Mockito.never()).convert(ArgumentMatchers.any());
    }

    @Test
    public void testSimpleRoute() {
        target = String.class;
        source = 1L;
        testSubject = ChainedConverter.calculateChain(Number.class, target, candidates);
        Assert.assertNotNull(testSubject);
        Mockito.verify(numberToStringConverter, Mockito.never()).convert(ArgumentMatchers.any());
        Mockito.verify(stringToReaderConverter, Mockito.never()).convert(ArgumentMatchers.any());
        Mockito.verify(bytesToInputStreamConverter, Mockito.never()).convert(ArgumentMatchers.any());
        Mockito.verify(stringToByteConverter, Mockito.never()).convert(ArgumentMatchers.any());
        String actual = convertSource();
        Assert.assertEquals("hello", actual);
        Mockito.verify(numberToStringConverter).convert(ArgumentMatchers.any());
        Mockito.verify(stringToReaderConverter, Mockito.never()).convert(ArgumentMatchers.any());
        Mockito.verify(bytesToInputStreamConverter, Mockito.never()).convert(ArgumentMatchers.any());
        Mockito.verify(stringToByteConverter, Mockito.never()).convert(ArgumentMatchers.any());
    }

    @Test(expected = CannotConvertBetweenTypesException.class)
    public void testInexistentRoute() {
        target = InputStream.class;
        source = new StringReader("hello");
        testSubject = ChainedConverter.calculateChain(Reader.class, target, candidates);
    }

    // Detects an issue where the ChainedConverter hangs as it evaluates a recursive route
    @Test(expected = CannotConvertBetweenTypesException.class)
    public void testAnotherInexistentRoute() {
        target = Number.class;
        source = "hello";
        Assert.assertFalse(ChainedConverter.canConvert(String.class, target, candidates));
        testSubject = ChainedConverter.calculateChain(String.class, target, candidates);
    }

    @Test(expected = CannotConvertBetweenTypesException.class)
    public void testAThirdInexistentRoute() {
        target = Documented.class;
        source = "hello".getBytes();
        testSubject = ChainedConverter.calculateChain(byte[].class, target, candidates);
    }

    @Test
    public void testDiscontinuousChainIsRejected() {
        try {
            testSubject = new ChainedConverter(Arrays.<ContentTypeConverter>asList(numberToStringConverter, bytesToInputStreamConverter));
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(("Wrong message: " + (e.getMessage())), e.getMessage().contains("continuous chain"));
        }
    }
}

