/**
 * Copyright 2015, The Querydsl Team (http://www.querydsl.com/team)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.querydsl.apt.domain;


import com.querydsl.core.annotations.QueryEntity;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;

import static QArrayExtTest_BinaryFile.binaryFile;


public class ArrayExtTest {
    private static final QArrayExtTest_BinaryFile binaryFile = binaryFile;

    @QueryEntity
    public static class BinaryFile {
        byte[] contentPart;

        List<byte[]> list;

        Map<String, byte[]> map1;

        Map<byte[], String> map2;
    }

    @Test
    public void binaryFile_contentPart() {
        Assert.assertEquals(ArrayPath.class, ArrayExtTest.binaryFile.contentPart.getClass());
        Assert.assertEquals(byte[].class, ArrayExtTest.binaryFile.contentPart.getType());
    }

    @Test
    public void binaryFile_list() {
        Assert.assertEquals(ListPath.class, ArrayExtTest.binaryFile.list.getClass());
        Assert.assertEquals(List.class, ArrayExtTest.binaryFile.list.getType());
        Assert.assertEquals(byte[].class, ArrayExtTest.binaryFile.list.getParameter(0));
        Assert.assertEquals(SimplePath.class, ArrayExtTest.binaryFile.list.get(0).getClass());
        Assert.assertEquals(byte[].class, ArrayExtTest.binaryFile.list.get(0).getType());
    }

    @Test
    public void binaryFile_map1() {
        Assert.assertEquals(MapPath.class, ArrayExtTest.binaryFile.map1.getClass());
        Assert.assertEquals(Map.class, ArrayExtTest.binaryFile.map1.getType());
        Assert.assertEquals(String.class, ArrayExtTest.binaryFile.map1.getParameter(0));
        Assert.assertEquals(byte[].class, ArrayExtTest.binaryFile.map1.getParameter(1));
        Assert.assertEquals(SimplePath.class, ArrayExtTest.binaryFile.map1.get("").getClass());
        Assert.assertEquals(byte[].class, ArrayExtTest.binaryFile.map1.get("").getType());
    }

    @Test
    public void binaryFile_map2() {
        Assert.assertEquals(MapPath.class, ArrayExtTest.binaryFile.map2.getClass());
        Assert.assertEquals(Map.class, ArrayExtTest.binaryFile.map2.getType());
        Assert.assertEquals(byte[].class, ArrayExtTest.binaryFile.map2.getParameter(0));
        Assert.assertEquals(String.class, ArrayExtTest.binaryFile.map2.getParameter(1));
        Assert.assertEquals(StringPath.class, ArrayExtTest.binaryFile.map2.get(new byte[0]).getClass());
        Assert.assertEquals(String.class, ArrayExtTest.binaryFile.map2.get(new byte[0]).getType());
    }
}

