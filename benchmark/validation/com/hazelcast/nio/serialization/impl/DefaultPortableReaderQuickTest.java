/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
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
package com.hazelcast.nio.serialization.impl;


import com.hazelcast.map.AbstractEntryProcessor;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.nio.serialization.HazelcastSerializationException;
import com.hazelcast.nio.serialization.Portable;
import com.hazelcast.nio.serialization.PortableFactory;
import com.hazelcast.nio.serialization.PortableReader;
import com.hazelcast.nio.serialization.PortableWriter;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.QuickTest;
import java.io.IOException;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastSerialClassRunner.class)
@Category(QuickTest.class)
public class DefaultPortableReaderQuickTest extends HazelcastTestSupport {
    static final DefaultPortableReaderQuickTest.CarPortable NON_EMPTY_PORSCHE = new DefaultPortableReaderQuickTest.CarPortable("Porsche", new DefaultPortableReaderQuickTest.EnginePortable(300), DefaultPortableReaderQuickTest.WheelPortable.w("front", true), DefaultPortableReaderQuickTest.WheelPortable.w("rear", true));

    static final DefaultPortableReaderQuickTest.CarPortable PORSCHE = new DefaultPortableReaderQuickTest.CarPortable("Porsche", new DefaultPortableReaderQuickTest.EnginePortable(300), DefaultPortableReaderQuickTest.WheelPortable.w("front", false), DefaultPortableReaderQuickTest.WheelPortable.w("rear", false));

    @Test(expected = IllegalArgumentException.class)
    public void nullAttributeName() throws IOException {
        reader(DefaultPortableReaderQuickTest.PORSCHE).readPortableArray(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void emptyAttributeName() throws IOException {
        reader(DefaultPortableReaderQuickTest.PORSCHE).readPortableArray("");
    }

    @Test(expected = HazelcastSerializationException.class)
    public void wrongAttributeName_specialCharsNotTreatedSpecially() throws IOException {
        reader(DefaultPortableReaderQuickTest.PORSCHE).readPortableArray("-;',;");
    }

    @Test(expected = HazelcastSerializationException.class)
    public void wrongAttributeName() throws IOException {
        reader(DefaultPortableReaderQuickTest.PORSCHE).readPortableArray("wheelsss");
    }

    @Test(expected = HazelcastSerializationException.class)
    public void wrongNestedAttributeName() throws IOException {
        reader(DefaultPortableReaderQuickTest.PORSCHE).readPortableArray("wheels[0].seriall");
    }

    @Test(expected = IllegalArgumentException.class)
    public void wrongDotsExpression_middle() throws IOException {
        reader(DefaultPortableReaderQuickTest.PORSCHE).readIntArray("wheels[0]..serial");
    }

    @Test(expected = IllegalArgumentException.class)
    public void wrongDotsExpression_end() throws IOException {
        Portable a = reader(DefaultPortableReaderQuickTest.PORSCHE).readPortable("wheels[0].");
    }

    @Test(expected = IllegalArgumentException.class)
    public void wrongDotsExpression_end_tooMany() throws IOException {
        reader(DefaultPortableReaderQuickTest.PORSCHE).readPortable("wheels[0]...");
    }

    @Test(expected = IllegalArgumentException.class)
    public void wrongDotsExpression_beg() throws IOException {
        reader(DefaultPortableReaderQuickTest.PORSCHE).readPortable(".wheels[0]");
    }

    @Test(expected = IllegalArgumentException.class)
    public void wrongDotsExpression_beg_tooMany() throws IOException {
        reader(DefaultPortableReaderQuickTest.PORSCHE).readPortable("...wheels[0]");
    }

    @Test(expected = IllegalArgumentException.class)
    public void malformedQuantifier_leading() throws IOException {
        reader(DefaultPortableReaderQuickTest.PORSCHE).readPortable("wheels[");
    }

    @Test(expected = IllegalArgumentException.class)
    public void malformedQuantifier_middle() throws IOException {
        reader(DefaultPortableReaderQuickTest.PORSCHE).readPortable("wheels[0");
    }

    @Test(expected = IllegalArgumentException.class)
    public void malformedQuantifier_trailing() throws IOException {
        reader(DefaultPortableReaderQuickTest.PORSCHE).readPortable("wheels0]");
    }

    @Test(expected = IllegalArgumentException.class)
    public void malformedQuantifier_trailingNoNumber() throws IOException {
        reader(DefaultPortableReaderQuickTest.PORSCHE).readPortable("wheels]");
    }

    @Test(expected = IllegalArgumentException.class)
    public void malformedQuantifierNested_leading() throws IOException {
        reader(DefaultPortableReaderQuickTest.PORSCHE).readPortable("wheels[0].chips[");
    }

    @Test(expected = IllegalArgumentException.class)
    public void malformedQuantifierNested_middle() throws IOException {
        reader(DefaultPortableReaderQuickTest.PORSCHE).readPortable("wheels[0].chips[0");
    }

    @Test(expected = IllegalArgumentException.class)
    public void malformedQuantifierNested_trailing() throws IOException {
        reader(DefaultPortableReaderQuickTest.PORSCHE).readPortable("wheels[0].chips0]");
    }

    @Test(expected = IllegalArgumentException.class)
    public void malformedQuantifierNested_trailingNoNumber() throws IOException {
        reader(DefaultPortableReaderQuickTest.PORSCHE).readPortable("wheels[0].chips]");
    }

    @Test(expected = IllegalArgumentException.class)
    public void wrongMethodType() throws IOException {
        reader(DefaultPortableReaderQuickTest.PORSCHE).readPortable("wheels");
    }

    @Test
    public void primitive() throws IOException {
        String expected = "Porsche";
        Assert.assertEquals(expected, reader(DefaultPortableReaderQuickTest.PORSCHE).readUTF("name"));
    }

    @Test
    public void nestedPrimitive() throws IOException {
        int expected = 300;
        Assert.assertEquals(expected, reader(DefaultPortableReaderQuickTest.PORSCHE).readInt("engine.power"));
    }

    @Test
    public void portableAttribute() throws IOException {
        DefaultPortableReaderQuickTest.EnginePortable expected = DefaultPortableReaderQuickTest.PORSCHE.engine;
        Assert.assertEquals(expected, reader(DefaultPortableReaderQuickTest.PORSCHE).readPortable("engine"));
    }

    @Test
    public void nestedPortableAttribute() throws IOException {
        DefaultPortableReaderQuickTest.ChipPortable expected = DefaultPortableReaderQuickTest.PORSCHE.engine.chip;
        Assert.assertEquals(expected, reader(DefaultPortableReaderQuickTest.PORSCHE).readPortable("engine.chip"));
    }

    @Test
    public void primitiveArrayAtTheEnd_wholeArrayFetched() throws IOException {
        String[] expected = new String[]{ "911", "GT" };
        Assert.assertArrayEquals(expected, reader(DefaultPortableReaderQuickTest.PORSCHE).readUTFArray("model"));
    }

    @Test
    public void primitiveArrayAtTheEnd_wholeArrayFetched_withAny() throws IOException {
        String[] expected = new String[]{ "911", "GT" };
        Assert.assertArrayEquals(expected, reader(DefaultPortableReaderQuickTest.PORSCHE).readUTFArray("model[any]"));
    }

    @Test
    public void primitiveArrayAtTheEnd_oneElementFetched() throws IOException {
        String expected = "911";
        Assert.assertEquals(expected, reader(DefaultPortableReaderQuickTest.PORSCHE).readUTF("model[0]"));
    }

    @Test
    public void primitiveArrayAtTheEnd_lastElementFetched() throws IOException {
        String expected = "GT";
        Assert.assertEquals(expected, reader(DefaultPortableReaderQuickTest.PORSCHE).readUTF("model[1]"));
    }

    @Test
    public void portableArray_wholeArrayFetched() throws IOException {
        Portable[] expected = DefaultPortableReaderQuickTest.PORSCHE.wheels;
        Assert.assertArrayEquals(expected, reader(DefaultPortableReaderQuickTest.PORSCHE).readPortableArray("wheels"));
    }

    @Test
    public void portableArray_wholeArrayFetched_withAny() throws IOException {
        Portable[] expected = DefaultPortableReaderQuickTest.PORSCHE.wheels;
        Assert.assertArrayEquals(expected, reader(DefaultPortableReaderQuickTest.PORSCHE).readPortableArray("wheels[any]"));
    }

    @Test
    public void portableArrayAtTheEnd_oneElementFetched() throws IOException {
        Portable expected = DefaultPortableReaderQuickTest.PORSCHE.wheels[0];
        Assert.assertEquals(expected, reader(DefaultPortableReaderQuickTest.PORSCHE).readPortable("wheels[0]"));
    }

    @Test
    public void portableArrayAtTheEnd_lastElementFetched() throws IOException {
        Portable expected = DefaultPortableReaderQuickTest.PORSCHE.wheels[1];
        Assert.assertEquals(expected, reader(DefaultPortableReaderQuickTest.PORSCHE).readPortable("wheels[1]"));
    }

    @Test
    public void portableArrayFirst_primitiveAtTheEnd() throws IOException {
        String expected = "rear";
        Assert.assertEquals(expected, reader(DefaultPortableReaderQuickTest.PORSCHE).readUTF("wheels[1].name"));
    }

    @Test
    public void portableArrayFirst_portableAtTheEnd() throws IOException {
        DefaultPortableReaderQuickTest.ChipPortable expected = ((DefaultPortableReaderQuickTest.WheelPortable) (DefaultPortableReaderQuickTest.PORSCHE.wheels[1])).chip;
        Assert.assertEquals(expected, reader(DefaultPortableReaderQuickTest.PORSCHE).readPortable("wheels[1].chip"));
    }

    @Test
    public void portableArrayFirst_portableArrayAtTheEnd_oneElementFetched() throws IOException {
        Portable expected = ((DefaultPortableReaderQuickTest.WheelPortable) (DefaultPortableReaderQuickTest.PORSCHE.wheels[0])).chips[1];
        Assert.assertEquals(expected, reader(DefaultPortableReaderQuickTest.PORSCHE).readPortable("wheels[0].chips[1]"));
    }

    @Test
    public void portableArrayFirst_portableArrayAtTheEnd_wholeArrayFetched() throws IOException {
        Portable[] expected = ((DefaultPortableReaderQuickTest.WheelPortable) (DefaultPortableReaderQuickTest.PORSCHE.wheels[0])).chips;
        Assert.assertArrayEquals(expected, reader(DefaultPortableReaderQuickTest.PORSCHE).readPortableArray("wheels[0].chips"));
    }

    @Test
    public void portableArrayFirst_portableArrayAtTheEnd_wholeArrayFetched_withAny() throws IOException {
        Portable[] expected = ((DefaultPortableReaderQuickTest.WheelPortable) (DefaultPortableReaderQuickTest.PORSCHE.wheels[0])).chips;
        Assert.assertArrayEquals(expected, reader(DefaultPortableReaderQuickTest.PORSCHE).readPortableArray("wheels[0].chips[any]"));
    }

    @Test
    public void portableArrayFirst_portableArrayInTheMiddle_primitiveAtTheEnd() throws IOException {
        int expected = 20;
        Assert.assertEquals(expected, reader(DefaultPortableReaderQuickTest.PORSCHE).readInt("wheels[0].chips[0].power"));
    }

    @Test
    public void portableArrayFirst_primitiveArrayAtTheEnd() throws IOException {
        int expected = 12 + 5;
        Assert.assertEquals(expected, reader(DefaultPortableReaderQuickTest.PORSCHE).readInt("wheels[0].serial[1]"));
    }

    @Test(expected = HazelcastSerializationException.class)
    public void portableArrayFirst_primitiveArrayAtTheEnd2() throws IOException {
        reader(DefaultPortableReaderQuickTest.PORSCHE).readInt("wheels[0].serial[1].x");
    }

    @Test
    public void portableArrayFirst_primitiveArrayAtTheEnd_wholeArrayFetched() throws IOException {
        int[] expected = ((DefaultPortableReaderQuickTest.WheelPortable) (DefaultPortableReaderQuickTest.PORSCHE.wheels[0])).serial;
        Assert.assertArrayEquals(expected, reader(DefaultPortableReaderQuickTest.PORSCHE).readIntArray("wheels[0].serial"));
    }

    @Test
    public void portableArrayFirst_primitiveArrayAtTheEnd_wholeArrayFetched_withAny() throws IOException {
        int[] expected = ((DefaultPortableReaderQuickTest.WheelPortable) (DefaultPortableReaderQuickTest.PORSCHE.wheels[0])).serial;
        Assert.assertArrayEquals(expected, reader(DefaultPortableReaderQuickTest.PORSCHE).readIntArray("wheels[0].serial[any]"));
    }

    @Test
    public void portableArrayFirst_withAny_primitiveArrayAtTheEnd() throws IOException {
        int[] expected = new int[]{ 17, 16 };
        Assert.assertArrayEquals(expected, reader(DefaultPortableReaderQuickTest.PORSCHE).readIntArray("wheels[any].serial[1]"));
    }

    @Test
    public void portableArrayFirst_withAny_primitiveArrayAtTheEnd2() throws IOException {
        Portable[] expected = new Portable[]{ ((DefaultPortableReaderQuickTest.WheelPortable) (DefaultPortableReaderQuickTest.PORSCHE.wheels[0])).chip, ((DefaultPortableReaderQuickTest.WheelPortable) (DefaultPortableReaderQuickTest.PORSCHE.wheels[1])).chip };
        Assert.assertArrayEquals(expected, reader(DefaultPortableReaderQuickTest.PORSCHE).readPortableArray("wheels[any].chip"));
    }

    @Test
    public void portableArrayFirst_withAny_primitiveArrayAtTheEnd3() throws IOException {
        Portable[] expected = new Portable[]{ ((DefaultPortableReaderQuickTest.WheelPortable) (DefaultPortableReaderQuickTest.PORSCHE.wheels[0])).chips[1], ((DefaultPortableReaderQuickTest.WheelPortable) (DefaultPortableReaderQuickTest.PORSCHE.wheels[1])).chips[1] };
        Assert.assertArrayEquals(expected, reader(DefaultPortableReaderQuickTest.PORSCHE).readPortableArray("wheels[any].chips[1]"));
    }

    @Test
    public void portableArrayFirst_withAny_primitiveArrayAtTheEnd5() throws IOException {
        String[] expected = new String[]{ "front", "rear" };
        Assert.assertArrayEquals(expected, reader(DefaultPortableReaderQuickTest.PORSCHE).readUTFArray("wheels[any].name"));
    }

    @Test
    public void portableArrayFirst_withAny_primitiveArrayAtTheEnd6() throws IOException {
        Assert.assertNull(reader(DefaultPortableReaderQuickTest.PORSCHE).readIntArray("wheels[1].emptyChips[any].power"));
    }

    @Test
    public void portableArrayFirst_withAny_primitiveArrayAtTheEnd7() throws IOException {
        Assert.assertArrayEquals(null, reader(DefaultPortableReaderQuickTest.PORSCHE).readIntArray("wheels[1].nullChips[any].power"));
    }

    @Test
    public void portableArrayFirst_withAny_primitiveArrayAtTheEnd8() throws IOException {
        Assert.assertNull(reader(DefaultPortableReaderQuickTest.PORSCHE).readPortableArray("wheels[1].emptyChips[any]"));
    }

    @Test
    public void portableArrayFirst_withAny_primitiveArrayAtTheEnd8a() throws IOException {
        Portable[] expected = new Portable[]{ null, null };
        Assert.assertArrayEquals(expected, reader(DefaultPortableReaderQuickTest.PORSCHE).readPortableArray("wheels[any].emptyChips[any]"));
    }

    @Test
    public void portableArrayFirst_withAny_primitiveArrayAtTheEnd9() throws IOException {
        Portable[] expected = new Portable[]{  };
        Assert.assertArrayEquals(expected, reader(DefaultPortableReaderQuickTest.PORSCHE).readPortableArray("wheels[1].emptyChips"));
    }

    @Test
    public void portableArrayFirst_withAny_primitiveArrayAtTheEnd10() throws IOException {
        Assert.assertArrayEquals(null, reader(DefaultPortableReaderQuickTest.PORSCHE).readPortableArray("wheels[1].nullChips[any]"));
    }

    @Test
    public void portableArrayFirst_withAny_primitiveArrayAtTheEnd11() throws IOException {
        Assert.assertArrayEquals(null, reader(DefaultPortableReaderQuickTest.PORSCHE).readPortableArray("wheels[1].nullChips"));
    }

    @Test
    public void reusingTheReader_multipleCalls_stateResetCorrectly() throws IOException {
        PortableReader reader = reader(DefaultPortableReaderQuickTest.PORSCHE);
        Assert.assertEquals("rear", reader.readUTF("wheels[1].name"));
        Assert.assertEquals(300, reader.readInt("engine.power"));
        Assert.assertEquals(46, reader.readInt("wheels[0].serial[0]"));
        try {
            reader.readFloat("wheels[0].serial[0]");
            Assert.fail();
        } catch (Exception ignored) {
        }
        Assert.assertEquals("front", reader.readUTF("wheels[0].name"));
        Assert.assertEquals(45, reader.readInt("wheels[1].serial[0]"));
        try {
            reader.readIntArray("name");
            Assert.fail();
        } catch (Exception ignored) {
        }
        Assert.assertEquals(15, reader.readInt("engine.chip.power"));
        Assert.assertEquals("Porsche", reader.readUTF("name"));
    }

    public static class EntryStealingProcessor extends AbstractEntryProcessor {
        private final Object key;

        private Data stolenEntryData;

        EntryStealingProcessor(String key) {
            super(false);
            this.key = key;
        }

        @Override
        public Object process(Map.Entry entry) {
            // hack to get rid of de-serialization cost (assuming in-memory-format is BINARY, if it is OBJECT you can replace
            // the null check below with entry.getValue() != null), but works only for versions >= 3.6
            if (key.equals(entry.getKey())) {
                stolenEntryData = ((Data) (getValueData()));
            }
            return null;
        }
    }

    static class CarPortable implements Portable {
        static final int FACTORY_ID = 1;

        static final int ID = 5;

        int power;

        String name;

        DefaultPortableReaderQuickTest.EnginePortable engine;

        Portable[] wheels;

        public String[] model;

        public CarPortable() {
        }

        CarPortable(String name, DefaultPortableReaderQuickTest.EnginePortable engine, DefaultPortableReaderQuickTest.WheelPortable... wheels) {
            this.power = 100;
            this.name = name;
            this.engine = engine;
            this.wheels = wheels;
            this.model = new String[]{ "911", "GT" };
        }

        @Override
        public int getFactoryId() {
            return DefaultPortableReaderQuickTest.CarPortable.FACTORY_ID;
        }

        @Override
        public int getClassId() {
            return DefaultPortableReaderQuickTest.CarPortable.ID;
        }

        @Override
        public void writePortable(PortableWriter writer) throws IOException {
            writer.writeInt("power", power);
            writer.writeUTF("name", name);
            writer.writePortable("engine", engine);
            writer.writePortableArray("wheels", wheels);
            writer.writeUTFArray("model", model);
        }

        @Override
        public void readPortable(PortableReader reader) throws IOException {
            power = reader.readInt("power");
            name = reader.readUTF("name");
            engine = reader.readPortable("engine");
            wheels = reader.readPortableArray("wheels");
            model = reader.readUTFArray("model");
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            DefaultPortableReaderQuickTest.CarPortable that = ((DefaultPortableReaderQuickTest.CarPortable) (o));
            if ((name) != null ? !(name.equals(that.name)) : (that.name) != null) {
                return false;
            }
            return (engine) != null ? engine.equals(that.engine) : (that.engine) == null;
        }

        @Override
        public int hashCode() {
            int result = ((name) != null) ? name.hashCode() : 0;
            result = (31 * result) + ((engine) != null ? engine.hashCode() : 0);
            return result;
        }
    }

    static class EnginePortable implements Portable , Comparable<DefaultPortableReaderQuickTest.EnginePortable> {
        static final int FACTORY_ID = 1;

        static final int ID = 8;

        Integer power;

        DefaultPortableReaderQuickTest.ChipPortable chip;

        public EnginePortable() {
            this.chip = new DefaultPortableReaderQuickTest.ChipPortable();
        }

        EnginePortable(int power) {
            this.power = power;
            this.chip = new DefaultPortableReaderQuickTest.ChipPortable();
        }

        @Override
        public int getFactoryId() {
            return DefaultPortableReaderQuickTest.EnginePortable.FACTORY_ID;
        }

        @Override
        public int getClassId() {
            return DefaultPortableReaderQuickTest.EnginePortable.ID;
        }

        @Override
        public void writePortable(PortableWriter writer) throws IOException {
            writer.writeInt("power", power);
            writer.writePortable("chip", chip);
        }

        @Override
        public void readPortable(PortableReader reader) throws IOException {
            power = reader.readInt("power");
            chip = reader.readPortable("chip");
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            DefaultPortableReaderQuickTest.EnginePortable that = ((DefaultPortableReaderQuickTest.EnginePortable) (o));
            return power.equals(that.power);
        }

        @Override
        public int hashCode() {
            return power;
        }

        @Override
        public int compareTo(DefaultPortableReaderQuickTest.EnginePortable o) {
            return this.power.compareTo(o.power);
        }
    }

    static class ChipPortable implements Portable , Comparable<DefaultPortableReaderQuickTest.ChipPortable> {
        static final int FACTORY_ID = 1;

        static final int ID = 6;

        Integer power;

        public ChipPortable() {
            this.power = 15;
        }

        ChipPortable(int power) {
            this.power = power;
        }

        @Override
        public int getFactoryId() {
            return DefaultPortableReaderQuickTest.ChipPortable.FACTORY_ID;
        }

        @Override
        public int getClassId() {
            return DefaultPortableReaderQuickTest.ChipPortable.ID;
        }

        @Override
        public void writePortable(PortableWriter writer) throws IOException {
            writer.writeInt("power", power);
        }

        @Override
        public void readPortable(PortableReader reader) throws IOException {
            power = reader.readInt("power");
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            DefaultPortableReaderQuickTest.ChipPortable that = ((DefaultPortableReaderQuickTest.ChipPortable) (o));
            return power.equals(that.power);
        }

        @Override
        public int hashCode() {
            return power;
        }

        @Override
        public int compareTo(DefaultPortableReaderQuickTest.ChipPortable o) {
            return this.power.compareTo(o.power);
        }
    }

    static class WheelPortable implements Portable , Comparable<DefaultPortableReaderQuickTest.WheelPortable> {
        static final int FACTORY_ID = 1;

        static final int ID = 7;

        String name;

        DefaultPortableReaderQuickTest.ChipPortable chip;

        Portable[] chips;

        Portable[] emptyChips;

        Portable[] nullChips;

        int[] serial;

        public WheelPortable() {
        }

        WheelPortable(String name, boolean nonNull) {
            this.name = name;
            this.chip = new DefaultPortableReaderQuickTest.ChipPortable(100);
            this.chips = new Portable[]{ new DefaultPortableReaderQuickTest.ChipPortable(20), new DefaultPortableReaderQuickTest.ChipPortable(40) };
            if (nonNull) {
                this.emptyChips = new Portable[]{ new DefaultPortableReaderQuickTest.ChipPortable(20) };
                this.nullChips = new Portable[]{ new DefaultPortableReaderQuickTest.ChipPortable(20) };
            } else {
                this.emptyChips = new Portable[]{  };
                this.nullChips = null;
            }
            int nameLength = name.length();
            this.serial = new int[]{ 41 + nameLength, 12 + nameLength, 79 + nameLength, 18 + nameLength, 102 + nameLength };
        }

        @Override
        public int getFactoryId() {
            return DefaultPortableReaderQuickTest.WheelPortable.FACTORY_ID;
        }

        @Override
        public int getClassId() {
            return DefaultPortableReaderQuickTest.WheelPortable.ID;
        }

        @Override
        public void writePortable(PortableWriter writer) throws IOException {
            writer.writeUTF("name", name);
            writer.writePortable("chip", chip);
            writer.writePortableArray("chips", chips);
            writer.writePortableArray("emptyChips", emptyChips);
            writer.writePortableArray("nullChips", nullChips);
            writer.writeIntArray("serial", serial);
        }

        @Override
        public void readPortable(PortableReader reader) throws IOException {
            name = reader.readUTF("name");
            chip = reader.readPortable("chip");
            chips = reader.readPortableArray("chips");
            emptyChips = reader.readPortableArray("emptyChips");
            nullChips = reader.readPortableArray("nullChips");
            serial = reader.readIntArray("serial");
        }

        static DefaultPortableReaderQuickTest.WheelPortable w(String name, boolean nonNull) {
            return new DefaultPortableReaderQuickTest.WheelPortable(name, nonNull);
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            DefaultPortableReaderQuickTest.WheelPortable that = ((DefaultPortableReaderQuickTest.WheelPortable) (o));
            return (name) != null ? name.equals(that.name) : (that.name) == null;
        }

        @Override
        public int hashCode() {
            return (name) != null ? name.hashCode() : 0;
        }

        @Override
        public int compareTo(DefaultPortableReaderQuickTest.WheelPortable o) {
            return this.name.compareTo(o.name);
        }
    }

    public static class TestPortableFactory implements PortableFactory {
        public static final int ID = 1;

        @Override
        public Portable create(int classId) {
            if ((DefaultPortableReaderQuickTest.CarPortable.ID) == classId) {
                return new DefaultPortableReaderQuickTest.CarPortable();
            } else
                if ((DefaultPortableReaderQuickTest.EnginePortable.ID) == classId) {
                    return new DefaultPortableReaderQuickTest.EnginePortable();
                } else
                    if ((DefaultPortableReaderQuickTest.WheelPortable.ID) == classId) {
                        return new DefaultPortableReaderQuickTest.WheelPortable();
                    } else
                        if ((DefaultPortableReaderQuickTest.ChipPortable.ID) == classId) {
                            return new DefaultPortableReaderQuickTest.ChipPortable();
                        } else {
                            return null;
                        }



        }
    }
}

