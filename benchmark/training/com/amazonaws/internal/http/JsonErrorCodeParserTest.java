/**
 * Copyright 2011-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.amazonaws.internal.http;


import JsonErrorCodeParser.X_AMZN_ERROR_TYPE;
import com.amazonaws.protocol.json.JsonContent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;


public class JsonErrorCodeParserTest {
    /**
     * Value of error type present in headers for tests below
     */
    private static final String HEADER_ERROR_TYPE = "headerErrorType";

    /**
     * Value of error type present in JSON content for tests below
     */
    private static final String JSON_ERROR_TYPE = "jsonErrorType";

    private static final String ERROR_FIELD_NAME = "testErrorCode";

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final JsonErrorCodeParser parser = new JsonErrorCodeParser(JsonErrorCodeParserTest.ERROR_FIELD_NAME);

    @Test
    public void parseErrorType_ErrorTypeInHeadersTakesPrecedence_NoSuffix() {
        String actualErrorType = parser.parseErrorCode(JsonErrorCodeParserTest.httpResponseWithHeaders(X_AMZN_ERROR_TYPE, JsonErrorCodeParserTest.HEADER_ERROR_TYPE), JsonErrorCodeParserTest.toJsonContent(JsonErrorCodeParserTest.JSON_ERROR_TYPE));
        Assert.assertEquals(JsonErrorCodeParserTest.HEADER_ERROR_TYPE, actualErrorType);
    }

    @Test
    public void parseErrorType_ErrorTypeInHeadersTakesPrecedence_SuffixIgnored() {
        String actualErrorType = parser.parseErrorCode(JsonErrorCodeParserTest.httpResponseWithHeaders(X_AMZN_ERROR_TYPE, String.format("%s:%s", JsonErrorCodeParserTest.HEADER_ERROR_TYPE, "someSuffix")), JsonErrorCodeParserTest.toJsonContent(JsonErrorCodeParserTest.JSON_ERROR_TYPE));
        Assert.assertEquals(JsonErrorCodeParserTest.HEADER_ERROR_TYPE, actualErrorType);
    }

    @Test
    public void parseErrorType_ErrorTypeInContent_NoPrefix() {
        String actualErrorType = parser.parseErrorCode(JsonErrorCodeParserTest.httpResponseWithoutHeaders(), JsonErrorCodeParserTest.toJsonContent(JsonErrorCodeParserTest.JSON_ERROR_TYPE));
        Assert.assertEquals(JsonErrorCodeParserTest.JSON_ERROR_TYPE, actualErrorType);
    }

    @Test
    public void parseErrorType_ErrorTypeInContent_PrefixIgnored() {
        String actualErrorType = parser.parseErrorCode(JsonErrorCodeParserTest.httpResponseWithoutHeaders(), JsonErrorCodeParserTest.toJsonContent(String.format("%s#%s", "somePrefix", JsonErrorCodeParserTest.JSON_ERROR_TYPE)));
        Assert.assertEquals(JsonErrorCodeParserTest.JSON_ERROR_TYPE, actualErrorType);
    }

    @Test
    public void parseErrorType_NotPresentInHeadersAndNullContent_ReturnsNull() {
        Assert.assertNull(parser.parseErrorCode(JsonErrorCodeParserTest.httpResponseWithoutHeaders(), null));
    }

    @Test
    public void parseErrorType_NotPresentInHeadersAndEmptyContent_ReturnsNull() {
        Assert.assertNull(parser.parseErrorCode(JsonErrorCodeParserTest.httpResponseWithoutHeaders(), new JsonContent(null, new ObjectMapper().createObjectNode())));
    }
}

