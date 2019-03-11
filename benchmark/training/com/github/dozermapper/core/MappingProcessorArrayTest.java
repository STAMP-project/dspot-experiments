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
package com.github.dozermapper.core;


import java.util.Arrays;
import java.util.Date;
import org.junit.Assert;
import org.junit.Test;


public class MappingProcessorArrayTest extends AbstractDozerTest {
    public static class PrimitiveArray {
        private int[] data;

        public int[] getData() {
            return data;
        }

        public void setData(int[] data) {
            this.data = data;
        }
    }

    public static final class MyDate extends Date {
        public MyDate(long ms) {
            super(ms);
        }
    }

    public static class FinalCopyByReferenceArray {
        private MappingProcessorArrayTest.MyDate[] data;

        public MappingProcessorArrayTest.MyDate[] getData() {
            return data;
        }

        public void setData(MappingProcessorArrayTest.MyDate[] data) {
            this.data = data;
        }
    }

    public static class FinalCopyByReferenceDest {
        private Date[] data;

        public Date[] getData() {
            return data;
        }

        public void setData(Date[] data) {
            this.data = data;
        }
    }

    @Test
    public void testPrimitiveArrayCopy() {
        MappingProcessorArrayTest.PrimitiveArray test = new MappingProcessorArrayTest.PrimitiveArray();
        int[] data = new int[1024 * 1024];
        for (int i = 0; i < (data.length); i++) {
            data[i] = i;
        }
        test.setData(data);
        Mapper dozer = DozerBeanMapperBuilder.buildDefault();
        MappingProcessorArrayTest.PrimitiveArray result = dozer.map(test, MappingProcessorArrayTest.PrimitiveArray.class);
        long start = System.currentTimeMillis();
        result = dozer.map(test, MappingProcessorArrayTest.PrimitiveArray.class);
        System.out.println(((System.currentTimeMillis()) - start));
        Assert.assertNotSame(test.getData(), result.getData());
        Assert.assertTrue(Arrays.equals(test.getData(), result.getData()));
    }

    @Test
    public void testReferenceCopy() {
        MappingProcessorArrayTest.FinalCopyByReferenceArray test = new MappingProcessorArrayTest.FinalCopyByReferenceArray();
        MappingProcessorArrayTest.MyDate[] data = new MappingProcessorArrayTest.MyDate[1024 * 1024];
        for (int i = 0; i < (data.length); i++) {
            data[i] = new MappingProcessorArrayTest.MyDate(i);
        }
        test.setData(data);
        Mapper dozer = DozerBeanMapperBuilder.create().withMappingFiles("mappings/mappingProcessorArrayTest.xml").build();
        MappingProcessorArrayTest.FinalCopyByReferenceDest result = dozer.map(test, MappingProcessorArrayTest.FinalCopyByReferenceDest.class);
        long start = System.currentTimeMillis();
        result = dozer.map(test, MappingProcessorArrayTest.FinalCopyByReferenceDest.class);
        System.out.println(((System.currentTimeMillis()) - start));
        Assert.assertNotSame(test.getData(), result.getData());
        Assert.assertTrue(Arrays.equals(test.getData(), result.getData()));
    }
}

