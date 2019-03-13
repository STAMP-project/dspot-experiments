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
package com.liferay.dynamic.data.mapping.io.internal.exporter;


import DDMFormInstanceRecordWriterRequest.Builder;
import com.liferay.dynamic.data.mapping.io.exporter.DDMFormInstanceRecordWriterRequest;
import com.liferay.dynamic.data.mapping.io.exporter.DDMFormInstanceRecordWriterResponse;
import com.liferay.petra.string.StringBundler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Leonardo Barros
 */
public class DDMFormInstanceRecordCSVWriterTest {
    @Test
    public void testWrite() throws Exception {
        Map<String, String> ddmFormFieldsLabel = new LinkedHashMap() {
            {
                put("field1", "Field 1");
                put("field2", "Field 2");
                put("field3", "Field 3");
                put("field4", "Field 4");
            }
        };
        List<Map<String, String>> ddmFormFieldValues = new ArrayList() {
            {
                Map<String, String> map1 = new HashMap() {
                    {
                        put("field1", "2");
                        put("field2", "esta ? uma 'string'");
                        put("field3", "false");
                        put("field4", "11.7");
                    }
                };
                add(map1);
                Map<String, String> map2 = new HashMap() {
                    {
                        put("field1", "1");
                        put("field2", "esta ? uma 'string'");
                        put("field3", "");
                        put("field4", "10");
                    }
                };
                add(map2);
            }
        };
        DDMFormInstanceRecordWriterRequest.Builder builder = Builder.newBuilder(ddmFormFieldsLabel, ddmFormFieldValues);
        DDMFormInstanceRecordCSVWriter ddmFormInstanceRecordCSVWriter = new DDMFormInstanceRecordCSVWriter();
        DDMFormInstanceRecordWriterResponse ddmFormInstanceRecordWriterResponse = ddmFormInstanceRecordCSVWriter.write(builder.build());
        StringBundler sb = new StringBundler(3);
        sb.append("Field 1,Field 2,Field 3,Field 4\n");
        sb.append("2,false,esta \u00e9 uma \'string\',11.7\n");
        sb.append("1,,esta ? uma 'string',10");
        String expected = sb.toString();
        Assert.assertArrayEquals(expected.getBytes(), ddmFormInstanceRecordWriterResponse.getContent());
    }

    @Test
    public void testWriteRecords() {
        DDMFormInstanceRecordCSVWriter ddmFormInstanceRecordCSVWriter = new DDMFormInstanceRecordCSVWriter();
        List<Map<String, String>> ddmFormFieldValues = new ArrayList() {
            {
                Map<String, String> map1 = new HashMap() {
                    {
                        put("field1", "value1");
                        put("field2", "false");
                        put("field3", "134.5");
                    }
                };
                add(map1);
                Map<String, String> map2 = new HashMap() {
                    {
                        put("field1", "");
                        put("field2", "true");
                        put("field3", "45");
                    }
                };
                add(map2);
            }
        };
        StringBundler sb = new StringBundler(2);
        sb.append("value1,134.5,false\n");
        sb.append(",45,true");
        String actual = ddmFormInstanceRecordCSVWriter.writeRecords(ddmFormFieldValues);
        Assert.assertEquals(sb.toString(), actual);
    }

    @Test
    public void testWriteValues() {
        DDMFormInstanceRecordCSVWriter ddmFormInstanceRecordCSVWriter = new DDMFormInstanceRecordCSVWriter();
        List<String> values = new ArrayList() {
            {
                add("value1");
                add("2");
                add("true");
                add("this is a \"string\"");
            }
        };
        String actualValue = ddmFormInstanceRecordCSVWriter.writeValues(values);
        Assert.assertEquals("value1,2,true,\"this is a \"\"string\"\"\"", actualValue);
    }
}

