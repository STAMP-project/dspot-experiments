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


import com.github.dozermapper.core.vo.A;
import com.github.dozermapper.core.vo.B;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class MappingProcessorTest extends AbstractDozerTest {
    private ArrayList<Object> sourceList;

    private ArrayList<Object> destinationList;

    @Test
    public void testTwiceObjectToObjectConvert() {
        // todo mapping processor should be redesigned, see #377
        DozerBeanMapper mapper = ((DozerBeanMapper) (DozerBeanMapperBuilder.buildDefault()));
        Mapper mappingProcessor = mapper.getMappingProcessor();
        A src = new A();
        src.setB(new B());
        A dest1 = new A();
        mappingProcessor.map(src, dest1);
        A dest2 = new A();
        mappingProcessor.map(src, dest2);
        Assert.assertSame(dest1.getB(), dest2.getB());
    }

    @Test
    public void testPrepareDetinationList_OK() {
        List<?> result = MappingProcessor.prepareDestinationList(sourceList, destinationList);
        Assert.assertEquals(destinationList, result);
        destinationList.add("");
        result = MappingProcessor.prepareDestinationList(sourceList, destinationList);
        Assert.assertEquals(destinationList, result);
    }

    @Test
    public void testPrepareDetinationList_Null() {
        List<?> result = MappingProcessor.prepareDestinationList(sourceList, null);
        Assert.assertNotNull(result);
        Assert.assertEquals(new ArrayList<>(), result);
    }

    @Test
    public void testPrepareDetinationList_Array() {
        List<?> result = MappingProcessor.prepareDestinationList(sourceList, new Object[]{ "A" });
        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.size());
        Assert.assertEquals("A", result.iterator().next());
    }

    @Test
    public void testPrepareDetinationList_StrangeCase() {
        List<?> result = MappingProcessor.prepareDestinationList(sourceList, "Hullo");
        Assert.assertNotNull(result);
        Assert.assertEquals(new ArrayList<>(), result);
    }

    @Test
    public void testRemoveOrphans_OK() {
        destinationList.add("A");
        MappingProcessor.removeOrphans(sourceList, destinationList);
        Assert.assertTrue(destinationList.isEmpty());
    }

    @Test
    public void testRemoveOrphans_Many() {
        destinationList.add("A");
        destinationList.add("B");
        destinationList.add("C");
        sourceList.add("B");
        sourceList.add("D");
        MappingProcessor.removeOrphans(sourceList, destinationList);
        Assert.assertEquals(2, destinationList.size());
        Assert.assertEquals("B", destinationList.get(0));
        Assert.assertEquals("D", destinationList.get(1));
    }

    @Test
    public void testRemoveOrphans_Ordering() {
        destinationList.add(new MappingProcessorTest.Ordered(1));
        destinationList.add(new MappingProcessorTest.Ordered(2));
        destinationList.add(new MappingProcessorTest.Ordered(3));
        sourceList.add(new MappingProcessorTest.Ordered(0));
        sourceList.add(new MappingProcessorTest.Ordered(3));
        sourceList.add(new MappingProcessorTest.Ordered(2));
        sourceList.add(new MappingProcessorTest.Ordered(1));
        MappingProcessor.removeOrphans(sourceList, destinationList);
        Assert.assertEquals(4, destinationList.size());
        Assert.assertEquals(new MappingProcessorTest.Ordered(1), destinationList.get(0));
        Assert.assertEquals(new MappingProcessorTest.Ordered(2), destinationList.get(1));
        Assert.assertEquals(new MappingProcessorTest.Ordered(3), destinationList.get(2));
        Assert.assertEquals(new MappingProcessorTest.Ordered(0), destinationList.get(3));
    }

    private static final class Ordered {
        private int id;

        private Ordered(int id) {
            this.id = id;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            MappingProcessorTest.Ordered ordered = ((MappingProcessorTest.Ordered) (o));
            return (id) == (ordered.id);
        }

        @Override
        public int hashCode() {
            return id;
        }
    }
}

