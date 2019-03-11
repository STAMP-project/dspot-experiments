/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jctools.channels.mapping;


import org.junit.Assert;
import org.junit.Test;


public class MapperTest {
    private static final int EXAMPLE_SIZE_IN_BYTES = 16;

    private long startAddress;

    private Mapper<MapperTest.Example> mapper;

    @Test
    public void shouldUnderstandInterfaceFields() {
        Assert.assertEquals(MapperTest.EXAMPLE_SIZE_IN_BYTES, mapper.getSizeInBytes());
        StubFlyweight example = newFlyweight();
        Assert.assertNotNull(example);
        Assert.assertTrue((example instanceof MapperTest.Example));
    }

    @Test
    public void shouldBeAbleToReadAndWriteData() {
        MapperTest.Example writer = ((MapperTest.Example) (newFlyweight()));
        MapperTest.Example reader = ((MapperTest.Example) (newFlyweight()));
        writer.setFoo(5);
        Assert.assertEquals(5, reader.getFoo());
        writer.setBar(6L);
        Assert.assertEquals(6L, reader.getBar());
    }

    @Test
    public void shouldBeAbleToMoveFlyweights() {
        MapperTest.Example writer = ((MapperTest.Example) (newFlyweight()));
        MapperTest.Example reader = ((MapperTest.Example) (newFlyweight()));
        StubFlyweight writeCursor = ((StubFlyweight) (writer));
        StubFlyweight readCursor = ((StubFlyweight) (reader));
        writeCursor.moveTo(((startAddress) + (MapperTest.EXAMPLE_SIZE_IN_BYTES)));
        readCursor.moveTo(((startAddress) + (MapperTest.EXAMPLE_SIZE_IN_BYTES)));
        writer.setFoo(5);
        Assert.assertEquals(5, reader.getFoo());
        writer.setBar(6L);
        Assert.assertEquals(6L, reader.getBar());
    }

    // ---------------------------------------------------
    public interface Example {
        int getFoo();

        void setFoo(int value);

        long getBar();

        void setBar(long value);
    }
}

