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
package com.liferay.portal.vulcan.multipart;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.liferay.portal.kernel.json.JSONUtil;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.InternalServerErrorException;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.Test;


/**
 *
 *
 * @author Alejandro Hern?ndez
 */
public class MultipartBodyTest {
    @Test
    public void testGetBinaryFile() {
        BinaryFile binaryFile = new BinaryFile("contentType", "fileName", null, 0);
        MultipartBody multipartBody = MultipartBody.of(Collections.singletonMap("file", binaryFile), ( __) -> _objectMapper, Collections.emptyMap());
        MatcherAssert.assertThat(multipartBody.getBinaryFile("file"), Is.is(binaryFile));
        MatcherAssert.assertThat(multipartBody.getBinaryFile("null"), Is.is(CoreMatchers.nullValue()));
    }

    @Test
    public void testGetValueAsString() {
        MultipartBody multipartBody = MultipartBody.of(Collections.emptyMap(), ( __) -> _objectMapper, Collections.singletonMap("key", "value"));
        MatcherAssert.assertThat(multipartBody.getValueAsString("key"), Is.is("value"));
        MatcherAssert.assertThat(multipartBody.getValueAsString("null"), Is.is(CoreMatchers.nullValue()));
    }

    @Test
    public void testGgetValueAsInstance() throws IOException {
        // With object mapper
        MultipartBody multipartBody = MultipartBody.of(Collections.emptyMap(), ( __) -> _objectMapper, Collections.singletonMap("key", JSONUtil.put("string", "Hello").put("number", 42).put("list", Arrays.asList(1, 2, 3)).toString()));
        MultipartBodyTest.TestClass testClass = multipartBody.getValueAsInstance("key", MultipartBodyTest.TestClass.class);
        MatcherAssert.assertThat(testClass.list, Matchers.contains(1, 2, 3));
        MatcherAssert.assertThat(testClass.number, Is.is(42L));
        MatcherAssert.assertThat(testClass.string, Is.is("Hello"));
        MatcherAssert.assertThat(testClass.testClass, Is.is(CoreMatchers.nullValue()));
        try {
            multipartBody.getValueAsInstance("null", MultipartBodyTest.TestClass.class);
            throw new AssertionError("Should thrown exception");
        } catch (Exception e) {
            MatcherAssert.assertThat(e, Is.is(CoreMatchers.instanceOf(BadRequestException.class)));
            MatcherAssert.assertThat(e.getMessage(), Is.is("Missing JSON property with the key: null"));
        }
        // Without object mapper
        multipartBody = MultipartBody.of(Collections.emptyMap(), ( __) -> null, Collections.singletonMap("key", "value"));
        try {
            multipartBody.getValueAsInstance("key", MultipartBodyTest.TestClass.class);
            throw new AssertionError();
        } catch (Exception e) {
            MatcherAssert.assertThat(e, Is.is(CoreMatchers.instanceOf(InternalServerErrorException.class)));
            String expectedMessage = "Unable to get object mapper for class " + (MultipartBodyTest.TestClass.class.getName());
            MatcherAssert.assertThat(e.getMessage(), Is.is(expectedMessage));
        }
    }

    public static class TestClass {
        public List<Integer> list;

        public Long number;

        public String string;

        public MultipartBodyTest.TestClass testClass;
    }

    private static final ObjectMapper _objectMapper = new ObjectMapper();
}

