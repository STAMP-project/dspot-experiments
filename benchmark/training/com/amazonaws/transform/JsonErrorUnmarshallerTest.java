/**
 * Copyright 2015-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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
package com.amazonaws.transform;


import com.amazonaws.AmazonServiceException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;

import static JsonErrorUnmarshaller.DEFAULT_UNMARSHALLER;


public class JsonErrorUnmarshallerTest {
    private static final String ERROR_TYPE = "CustomException";

    private static final JsonNode JSON = new ObjectMapper().createObjectNode().put("message", "Some error message").put("__type", ("apiVersion#" + (JsonErrorUnmarshallerTest.ERROR_TYPE))).put("CustomField", "This is a customField").put("CustomInt", 42);

    private static final JsonNode INVALID_CASE_JSON = new ObjectMapper().createObjectNode().put("message", "Some error message").put("__type", ("apiVersion#" + (JsonErrorUnmarshallerTest.ERROR_TYPE))).put("customField", "This is a customField").put("customInt", 42);

    private JsonErrorUnmarshaller unmarshaller;

    @Test
    public void unmarshall_ValidJsonContent_UnmarshallsCorrectly() throws Exception {
        JsonErrorUnmarshallerTest.CustomException ase = ((JsonErrorUnmarshallerTest.CustomException) (unmarshaller.unmarshall(JsonErrorUnmarshallerTest.JSON)));
        Assert.assertEquals("Some error message", getErrorMessage());
        Assert.assertEquals("This is a customField", ase.getCustomField());
        Assert.assertEquals(Integer.valueOf(42), ase.getCustomInt());
    }

    @Test
    public void unmarshall_InvalidCaseJsonContent_DoesNotUnmarshallCustomFields() throws Exception {
        JsonErrorUnmarshallerTest.CustomException ase = ((JsonErrorUnmarshallerTest.CustomException) (unmarshaller.unmarshall(JsonErrorUnmarshallerTest.INVALID_CASE_JSON)));
        Assert.assertEquals("Some error message", getErrorMessage());
        Assert.assertNull(ase.getCustomField());
        Assert.assertNull(ase.getCustomInt());
    }

    @Test
    public void match_DefaultUnmarshaller_MatchesEverything() {
        unmarshaller = DEFAULT_UNMARSHALLER;
        Assert.assertTrue(unmarshaller.matchErrorCode(null));
        Assert.assertTrue(unmarshaller.matchErrorCode(""));
        Assert.assertTrue(unmarshaller.matchErrorCode("someErrorCode"));
    }

    @Test
    public void match_MatchingErrorCode_ReturnsTrue() throws Exception {
        Assert.assertTrue(unmarshaller.matchErrorCode(JsonErrorUnmarshallerTest.ERROR_TYPE));
    }

    @Test
    public void match_NonMatchingErrorCode_ReturnsFalse() throws Exception {
        Assert.assertFalse(unmarshaller.matchErrorCode("NonMatchingErrorCode"));
    }

    @Test
    public void match_NullErrorCode_ReturnsFalse() throws Exception {
        Assert.assertFalse(unmarshaller.matchErrorCode(null));
    }

    private static class CustomException extends AmazonServiceException {
        private static final long serialVersionUID = 4140670458615826397L;

        private String customField;

        private Integer customInt;

        public CustomException(String errorMessage) {
            super(errorMessage);
        }

        public String getCustomField() {
            return customField;
        }

        public void setCustomField(String customField) {
            this.customField = customField;
        }

        public Integer getCustomInt() {
            return customInt;
        }

        public void setCustomInt(Integer customInt) {
            this.customInt = customInt;
        }
    }
}

