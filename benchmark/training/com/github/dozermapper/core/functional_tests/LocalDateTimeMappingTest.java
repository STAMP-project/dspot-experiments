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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class LocalDateTimeMappingTest extends AbstractFunctionalTest {
    private final LocalDate sampleLocalDate = LocalDate.of(2017, 11, 2);

    private final LocalDateTime sample = LocalDateTime.of(2017, 11, 2, 10, 20, 30);

    private final ZoneOffset sampleOffset = ZoneId.systemDefault().getRules().getOffset(sample);

    private Mapper mapper;

    @Test
    public void canConvertStringViaDateTime() {
        String source = DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(sample);
        Map<String, Object> sourceMap = new HashMap<>();
        sourceMap.put("key", source);
        LocalDateTimeMappingTest.LocalDateTimeVO dest = mapper.map(sourceMap, LocalDateTimeMappingTest.LocalDateTimeVO.class);
        Assert.assertNotNull(dest);
        LocalDateTime result = dest.getResult();
        Assert.assertTrue(String.format("Values are not equals. Expected: %s. Actual: %s", sample, result), sample.isEqual(result));
    }

    @Test
    public void canConvertInstantViaDateTime() {
        Instant source = sample.toInstant(sampleOffset);
        Map<String, Object> sourceMap = new HashMap<>();
        sourceMap.put("key", source);
        LocalDateTimeMappingTest.LocalDateTimeVO dest = mapper.map(sourceMap, LocalDateTimeMappingTest.LocalDateTimeVO.class);
        Assert.assertNotNull(dest);
        LocalDateTime result = dest.getResult();
        Assert.assertTrue(String.format("Values are not equals. Expected: %s. Actual: %s", sample, result), sample.isEqual(result));
    }

    @Test
    public void canConvertLongViaDateTime() {
        Long source = sample.toInstant(sampleOffset).toEpochMilli();
        Map<String, Object> sourceMap = new HashMap<>();
        sourceMap.put("key", source);
        LocalDateTimeMappingTest.LocalDateTimeVO dest = mapper.map(sourceMap, LocalDateTimeMappingTest.LocalDateTimeVO.class);
        Assert.assertNotNull(dest);
        LocalDateTime result = dest.getResult();
        Assert.assertTrue(String.format("Values are not equals. Expected: %s. Actual: %s", sample, result), sample.isEqual(result));
    }

    @Test
    public void canConvertDateViaDateTime() {
        Date source = new Date(sample.toInstant(sampleOffset).toEpochMilli());
        Map<String, Object> sourceMap = new HashMap<>();
        sourceMap.put("key", source);
        LocalDateTimeMappingTest.LocalDateTimeVO dest = mapper.map(sourceMap, LocalDateTimeMappingTest.LocalDateTimeVO.class);
        Assert.assertNotNull(dest);
        LocalDateTime result = dest.getResult();
        Assert.assertTrue(String.format("Values are not equals. Expected: %s. Actual: %s", sample, result), sample.isEqual(result));
    }

    @Test
    public void canConvertStringViaDate() {
        String source = DateTimeFormatter.ISO_LOCAL_DATE.format(sampleLocalDate);
        Map<String, Object> sourceMap = new HashMap<>();
        sourceMap.put("key", source);
        LocalDateTimeMappingTest.LocalDateVO dest = mapper.map(sourceMap, LocalDateTimeMappingTest.LocalDateVO.class);
        Assert.assertNotNull(dest);
        LocalDate result = dest.getResult();
        Assert.assertNotNull(result);
        Assert.assertTrue(String.format("Values are not equals. Expected: %s. Actual: %s", sampleLocalDate, result), sampleLocalDate.isEqual(result));
    }

    public static final class LocalDateTimeVO {
        private LocalDateTime result;

        public LocalDateTime getResult() {
            return result;
        }

        public LocalDateTimeMappingTest.LocalDateTimeVO setResult(LocalDateTime result) {
            this.result = result;
            return this;
        }
    }

    public static final class LocalDateVO {
        private LocalDate result;

        public LocalDate getResult() {
            return result;
        }

        public LocalDateTimeMappingTest.LocalDateVO setResult(LocalDate result) {
            this.result = result;
            return this;
        }
    }
}

