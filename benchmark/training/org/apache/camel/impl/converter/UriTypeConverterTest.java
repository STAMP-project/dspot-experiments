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
package org.apache.camel.impl.converter;


import java.net.URI;
import org.apache.camel.TypeConversionException;
import org.junit.Assert;
import org.junit.Test;


public class UriTypeConverterTest {
    static final String EXAMPLE = "http://www.example.com";

    static final URI EXAMPLE_URI = URI.create(UriTypeConverterTest.EXAMPLE);

    static final String INVALID = ":";

    @Test(expected = TypeConversionException.class)
    public void shouldComplainOnInvalidStringUrisConvertingToUri() {
        UriTypeConverter.toUri(UriTypeConverterTest.INVALID);
    }

    @Test
    public void shouldConvertFromStringsToUris() {
        Assert.assertEquals(UriTypeConverterTest.EXAMPLE_URI, UriTypeConverter.toUri(UriTypeConverterTest.EXAMPLE));
    }

    @Test
    public void shouldConvertFromUrisToStrings() {
        Assert.assertEquals(UriTypeConverterTest.EXAMPLE, UriTypeConverter.toCharSequence(UriTypeConverterTest.EXAMPLE_URI));
    }
}

