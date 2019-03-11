/**
 * Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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
package com.amazonaws.services.dynamodbv2.datamodeling;


import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class ConversionToAttributeValuesTest {
    private DynamoDBMapperModelFactory models;

    private DynamoDBMapperConfig finalConfig;

    @Test
    public void converterFailsForSubProperty() throws Exception {
        DynamoDBMapperTableModel<ConversionToAttributeValuesTest.ConverterData> tableModel = getTable(ConversionToAttributeValuesTest.ConverterData.class);
        Map<String, AttributeValue> withSubData = tableModel.convert(new ConversionToAttributeValuesTest.ConverterData());
        Assert.assertEquals("bar", tableModel.unconvert(withSubData).getSubDocument().getaData().getValue());
    }

    @DynamoDBTable(tableName = "test")
    public static class ConverterData {
        @DynamoDBHashKey
        private String key;

        @DynamoDBTypeConverted(converter = ConversionToAttributeValuesTest.CustomDataConverter.class)
        ConversionToAttributeValuesTest.CustomData customConverted;

        private ConversionToAttributeValuesTest.ConverterSubDocument subDocument;

        public ConverterData() {
            customConverted = new ConversionToAttributeValuesTest.CustomData("foo");
            subDocument = new ConversionToAttributeValuesTest.ConverterSubDocument();
            subDocument.setaData(new ConversionToAttributeValuesTest.CustomData("bar"));
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public ConversionToAttributeValuesTest.ConverterSubDocument getSubDocument() {
            return subDocument;
        }

        public void setSubDocument(ConversionToAttributeValuesTest.ConverterSubDocument subProperty) {
            this.subDocument = subProperty;
        }

        public ConversionToAttributeValuesTest.CustomData getCustomConverted() {
            return customConverted;
        }

        public void setCustomConverted(ConversionToAttributeValuesTest.CustomData customConverted) {
            this.customConverted = customConverted;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o)
                return true;

            if ((o == null) || ((getClass()) != (o.getClass())))
                return false;

            ConversionToAttributeValuesTest.ConverterData that = ((ConversionToAttributeValuesTest.ConverterData) (o));
            return ConversionToAttributeValuesTest.equals(subDocument, that.subDocument);
        }

        @Override
        public int hashCode() {
            return ConversionToAttributeValuesTest.hash(subDocument);
        }
    }

    @DynamoDBDocument
    public static class ConverterSubDocument {
        @DynamoDBTypeConverted(converter = ConversionToAttributeValuesTest.CustomDataConverter.class)
        private ConversionToAttributeValuesTest.CustomData aData;

        public ConversionToAttributeValuesTest.CustomData getaData() {
            return aData;
        }

        public void setaData(ConversionToAttributeValuesTest.CustomData aData) {
            this.aData = aData;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o)
                return true;

            if ((o == null) || ((getClass()) != (o.getClass())))
                return false;

            ConversionToAttributeValuesTest.ConverterSubDocument that = ((ConversionToAttributeValuesTest.ConverterSubDocument) (o));
            return ConversionToAttributeValuesTest.equals(aData, that.aData);
        }

        @Override
        public int hashCode() {
            return ConversionToAttributeValuesTest.hash(aData);
        }
    }

    public static class CustomData {
        private final String value;

        public CustomData(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o)
                return true;

            if ((o == null) || ((getClass()) != (o.getClass())))
                return false;

            ConversionToAttributeValuesTest.CustomData that = ((ConversionToAttributeValuesTest.CustomData) (o));
            return ConversionToAttributeValuesTest.equals(value, that.value);
        }

        @Override
        public int hashCode() {
            return ConversionToAttributeValuesTest.hash(value);
        }
    }

    public static class CustomDataConverter implements DynamoDBTypeConverter<String, ConversionToAttributeValuesTest.CustomData> {
        public String convert(ConversionToAttributeValuesTest.CustomData object) {
            return object.getValue();
        }

        public ConversionToAttributeValuesTest.CustomData unconvert(String object) {
            return new ConversionToAttributeValuesTest.CustomData(object);
        }
    }
}

