/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.converter.jaxb;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;


@RunWith(MockitoJUnitRunner.class)
public class NonXmlCharFiltererTest {
    private NonXmlCharFilterer nonXmlCharFilterer;

    @Mock
    private NonXmlCharFilterer nonXmlCharFiltererMock;

    @Test
    public void testIsFilteredValidChars() {
        // Per http://www.w3.org/TR/2004/REC-xml-20040204/#NT-Char
        // Char ::= #x9 | #xA | #xD | [#x20-#xD7FF] | [#xE000-#xFFFD] |
        // [#x10000-#x10FFFF]
        checkSingleValid(9);
        checkSingleValid(10);
        checkSingleValid(13);
        checkRangeValid(32, 55295);
        checkRangeValid(57344, 65533);
        // not checking [0x10000, 0x10FFFF], as it goes beyond
        // Character.MAX_VALUE
    }

    @Test
    public void testIsFilteredInvalidChars() {
        // Per http://www.w3.org/TR/2004/REC-xml-20040204/#NT-Char
        // Char ::= #x9 | #xA | #xD | [#x20-#xD7FF] | [#xE000-#xFFFD] |
        // [#x10000-#x10FFFF]
        checkRangeInvalid(0, 8);
        checkRangeInvalid(11, 12);
        checkRangeInvalid(14, 31);
        checkRangeInvalid(55296, 57343);
        checkRangeInvalid(65534, 65535);
        // no need to check beyond #x10FFFF as this is greater than
        // Character.MAX_VALUE
    }

    @Test
    public void testFilter1ArgNonFiltered() {
        Mockito.when(nonXmlCharFiltererMock.filter(ArgumentMatchers.anyString())).thenCallRealMethod();
        Mockito.when(nonXmlCharFiltererMock.filter(ArgumentMatchers.any(char[].class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt())).thenReturn(false);
        String string = "abc";
        String result = nonXmlCharFiltererMock.filter(string);
        Mockito.verify(nonXmlCharFiltererMock).filter(new char[]{ 'a', 'b', 'c' }, 0, 3);
        Assert.assertSame("Should have returned the same string if nothing was filtered", string, result);
    }

    @Test
    public void testFilter1ArgFiltered() {
        Mockito.when(nonXmlCharFiltererMock.filter(ArgumentMatchers.anyString())).thenCallRealMethod();
        Mockito.when(nonXmlCharFiltererMock.filter(ArgumentMatchers.eq(new char[]{ 'a', 'b', 'c' }), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt())).thenAnswer(new Answer<Boolean>() {
            public Boolean answer(InvocationOnMock invocation) throws Throwable {
                char[] buffer = ((char[]) (invocation.getArguments()[0]));
                buffer[0] = 'i';
                buffer[1] = 'o';
                return true;
            }
        });
        String result = nonXmlCharFiltererMock.filter("abc");
        Mockito.verify(nonXmlCharFiltererMock).filter(ArgumentMatchers.any(char[].class), ArgumentMatchers.eq(0), ArgumentMatchers.eq(3));
        Assert.assertEquals("Should have returned filtered string", "ioc", result);
    }

    @Test
    public void testFilter1ArgNullArg() {
        nonXmlCharFiltererMock.filter(null);
        Mockito.verify(nonXmlCharFiltererMock, Mockito.never()).filter(ArgumentMatchers.any(char[].class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
    }

    @Test
    public void testFilter3Args() {
        Mockito.when(nonXmlCharFiltererMock.filter(ArgumentMatchers.any(char[].class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt())).thenCallRealMethod();
        Mockito.when(nonXmlCharFiltererMock.isFiltered(ArgumentMatchers.anyChar())).thenReturn(true, false, true);
        char[] buffer = new char[]{ '1', '2', '3', '4', '5', '6' };
        nonXmlCharFiltererMock.filter(buffer, 2, 3);
        Mockito.verify(nonXmlCharFiltererMock).isFiltered('3');
        Mockito.verify(nonXmlCharFiltererMock).isFiltered('4');
        Mockito.verify(nonXmlCharFiltererMock).isFiltered('5');
        Assert.assertArrayEquals("Unexpected buffer contents", new char[]{ '1', '2', ' ', '4', ' ', '6' }, buffer);
    }

    @Test
    public void testFilter3ArgsNullArg() {
        nonXmlCharFilterer.filter(null, 2, 3);
    }
}

