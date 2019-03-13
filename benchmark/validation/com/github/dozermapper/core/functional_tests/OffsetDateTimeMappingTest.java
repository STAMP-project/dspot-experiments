/**
 * Copyright 2005-2019 Dozer Project
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
package com.github.dozermapper.core.functional_tests;


import com.github.dozermapper.core.Mapper;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class OffsetDateTimeMappingTest extends AbstractFunctionalTest {
    private final OffsetDateTime offsetDateTimeSample = OffsetDateTime.of(2017, 11, 2, 10, 20, 30, 0, ZoneOffset.ofHours(2));

    private Mapper mapper;

    @Test
    public void canConvertString() {
        String source = DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(offsetDateTimeSample);
        Map<String, Object> sourceMap = new HashMap<>();
        sourceMap.put("key", source);
        OffsetDateTimeMappingTest.OffsetDateTimeVO dest = mapper.map(sourceMap, OffsetDateTimeMappingTest.OffsetDateTimeVO.class);
        Assert.assertNotNull(dest);
        OffsetDateTime result = dest.getResult();
        Assert.assertNotNull(result);
        Assert.assertTrue(String.format("Values are not equals. Expected: %s. Actual: %s", offsetDateTimeSample, result), offsetDateTimeSample.isEqual(result));
    }

    @Test
    public void canConvertInstant() {
        Instant source = offsetDateTimeSample.toInstant();
        Map<String, Object> sourceMap = new HashMap<>();
        sourceMap.put("key", source);
        OffsetDateTimeMappingTest.OffsetDateTimeVO dest = mapper.map(sourceMap, OffsetDateTimeMappingTest.OffsetDateTimeVO.class);
        Assert.assertNotNull(dest);
        OffsetDateTime result = dest.getResult();
        Assert.assertNotNull(result);
        Assert.assertTrue(String.format("Values are not equals. Expected: %s. Actual: %s", offsetDateTimeSample, result), offsetDateTimeSample.isEqual(result));
    }

    @Test
    public void canConvertLong() {
        Long source = offsetDateTimeSample.toInstant().toEpochMilli();
        Map<String, Object> sourceMap = new HashMap<>();
        sourceMap.put("key", source);
        OffsetDateTimeMappingTest.OffsetDateTimeVO dest = mapper.map(sourceMap, OffsetDateTimeMappingTest.OffsetDateTimeVO.class);
        Assert.assertNotNull(dest);
        OffsetDateTime result = dest.getResult();
        Assert.assertNotNull(result);
        Assert.assertTrue(String.format("Values are not equals. Expected: %s. Actual: %s", offsetDateTimeSample, result), offsetDateTimeSample.isEqual(result));
    }

    @Test
    public void canConvertDate() {
        Date source = new Date(offsetDateTimeSample.toInstant().toEpochMilli());
        Map<String, Object> sourceMap = new HashMap<>();
        sourceMap.put("key", source);
        OffsetDateTimeMappingTest.OffsetDateTimeVO dest = mapper.map(sourceMap, OffsetDateTimeMappingTest.OffsetDateTimeVO.class);
        Assert.assertNotNull(dest);
        OffsetDateTime result = dest.getResult();
        Assert.assertNotNull(result);
        Assert.assertTrue(String.format("Values are not equals. Expected: %s. Actual: %s", offsetDateTimeSample, result), offsetDateTimeSample.isEqual(result));
    }

    public static class OffsetDateTimeVO {
        private OffsetDateTime result;

        public OffsetDateTime getResult() {
            return result;
        }

        public void setResult(OffsetDateTime result) {
            this.result = result;
        }
    }
}

