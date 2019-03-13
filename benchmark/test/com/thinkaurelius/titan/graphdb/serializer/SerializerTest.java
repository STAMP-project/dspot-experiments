package com.thinkaurelius.titan.graphdb.serializer;


import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.thinkaurelius.titan.diskstorage.ReadBuffer;
import com.thinkaurelius.titan.diskstorage.StaticBuffer;
import com.thinkaurelius.titan.graphdb.database.serialize.DataOutput;
import com.thinkaurelius.titan.graphdb.serializer.attributes.TClass1;
import com.thinkaurelius.titan.graphdb.serializer.attributes.TClass1Serializer;
import com.thinkaurelius.titan.graphdb.serializer.attributes.TClass2;
import com.thinkaurelius.titan.graphdb.serializer.attributes.TClass2Serializer;
import com.thinkaurelius.titan.graphdb.serializer.attributes.TEnum;
import com.thinkaurelius.titan.graphdb.serializer.attributes.TEnumSerializer;
import com.thinkaurelius.titan.testutil.RandomGenerator;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static StringSerializer.LONG_COMPRESSION_THRESHOLD;
import static StringSerializer.TEXT_COMRPESSION_THRESHOLD;


// Arrays (support null serialization)
public class SerializerTest extends SerializerTestCommon {
    private static final Logger log = LoggerFactory.getLogger(SerializerTest.class);

    @Test
    public void objectWriteReadTest() {
        serialize.registerClass(2, TClass1.class, new TClass1Serializer());
        serialize.registerClass(80342, TClass2.class, new TClass2Serializer());
        serialize.registerClass(999, TEnum.class, new TEnumSerializer());
        objectWriteRead();
    }

    @Test
    public void comparableStringSerialization() {
        // Characters
        DataOutput out = serialize.getDataOutput(((((int) (Character.MAX_VALUE)) * 2) + 8));
        for (char c = Character.MIN_VALUE; c < (Character.MAX_VALUE); c++) {
            out.writeObjectNotNull(Character.valueOf(c));
        }
        ReadBuffer b = out.getStaticBuffer().asReadBuffer();
        for (char c = Character.MIN_VALUE; c < (Character.MAX_VALUE); c++) {
            Assert.assertEquals(c, serialize.readObjectNotNull(b, Character.class).charValue());
        }
        // String
        for (int t = 0; t < 10000; t++) {
            DataOutput out1 = serialize.getDataOutput((32 + 5));
            DataOutput out2 = serialize.getDataOutput((32 + 5));
            String s1 = RandomGenerator.randomString(1, 32);
            String s2 = RandomGenerator.randomString(1, 32);
            out1.writeObjectByteOrder(s1, String.class);
            out2.writeObjectByteOrder(s2, String.class);
            StaticBuffer b1 = out1.getStaticBuffer();
            StaticBuffer b2 = out2.getStaticBuffer();
            Assert.assertEquals(s1, serialize.readObjectByteOrder(b1.asReadBuffer(), String.class));
            Assert.assertEquals(s2, serialize.readObjectByteOrder(b2.asReadBuffer(), String.class));
            Assert.assertEquals(((s1 + " vs ") + s2), Integer.signum(s1.compareTo(s2)), Integer.signum(b1.compareTo(b2)));
        }
    }

    @Test
    public void classSerialization() {
        DataOutput out = serialize.getDataOutput(128);
        out.writeObjectNotNull(Boolean.class);
        out.writeObjectNotNull(Byte.class);
        out.writeObjectNotNull(Double.class);
        ReadBuffer b = out.getStaticBuffer().asReadBuffer();
        Assert.assertEquals(Boolean.class, serialize.readObjectNotNull(b, Class.class));
        Assert.assertEquals(Byte.class, serialize.readObjectNotNull(b, Class.class));
        Assert.assertEquals(Double.class, serialize.readObjectNotNull(b, Class.class));
    }

    @Test
    public void parallelDeserialization() throws InterruptedException {
        serialize.registerClass(1, TClass2.class, new TClass2Serializer());
        final long value = 8;
        final String str = "123456";
        final TClass2 c = new TClass2("abcdefg", 333);
        DataOutput out = serialize.getDataOutput(128);
        out.putLong(value);
        out.writeClassAndObject(Long.valueOf(value));
        out.writeObject(c, TClass2.class);
        out.writeObjectNotNull(str);
        final StaticBuffer b = out.getStaticBuffer();
        int numThreads = 4;
        Thread[] threads = new Thread[numThreads];
        for (int i = 0; i < numThreads; i++) {
            threads[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int j = 0; j < 100000; j++) {
                        ReadBuffer buffer = b.asReadBuffer();
                        Assert.assertEquals(8, buffer.getLong());
                        Assert.assertEquals(value, ((long) (serialize.readClassAndObject(buffer))));
                        Assert.assertEquals(c, serialize.readObject(buffer, TClass2.class));
                        Assert.assertEquals(str, serialize.readObjectNotNull(buffer, String.class));
                    }
                }
            });
            threads[i].start();
        }
        for (int i = 0; i < numThreads; i++) {
            threads[i].join();
        }
    }

    @Test
    public void primitiveSerialization() {
        DataOutput out = serialize.getDataOutput(128);
        out.writeObjectNotNull(Boolean.FALSE);
        out.writeObjectNotNull(Boolean.TRUE);
        out.writeObjectNotNull(Byte.MIN_VALUE);
        out.writeObjectNotNull(Byte.MAX_VALUE);
        out.writeObjectNotNull(new Byte(((byte) (0))));
        out.writeObjectNotNull(Short.MIN_VALUE);
        out.writeObjectNotNull(Short.MAX_VALUE);
        out.writeObjectNotNull(new Short(((short) (0))));
        out.writeObjectNotNull(Character.MIN_VALUE);
        out.writeObjectNotNull(Character.MAX_VALUE);
        out.writeObjectNotNull(new Character('a'));
        out.writeObjectNotNull(Integer.MIN_VALUE);
        out.writeObjectNotNull(Integer.MAX_VALUE);
        out.writeObjectNotNull(new Integer(0));
        out.writeObjectNotNull(Long.MIN_VALUE);
        out.writeObjectNotNull(Long.MAX_VALUE);
        out.writeObjectNotNull(new Long(0));
        out.writeObjectNotNull(new Float(((float) (0.0))));
        out.writeObjectNotNull(new Double(0.0));
        ReadBuffer b = out.getStaticBuffer().asReadBuffer();
        Assert.assertEquals(Boolean.FALSE, serialize.readObjectNotNull(b, Boolean.class));
        Assert.assertEquals(Boolean.TRUE, serialize.readObjectNotNull(b, Boolean.class));
        Assert.assertEquals(Byte.MIN_VALUE, serialize.readObjectNotNull(b, Byte.class).longValue());
        Assert.assertEquals(Byte.MAX_VALUE, serialize.readObjectNotNull(b, Byte.class).longValue());
        Assert.assertEquals(0, serialize.readObjectNotNull(b, Byte.class).longValue());
        Assert.assertEquals(Short.MIN_VALUE, serialize.readObjectNotNull(b, Short.class).longValue());
        Assert.assertEquals(Short.MAX_VALUE, serialize.readObjectNotNull(b, Short.class).longValue());
        Assert.assertEquals(0, serialize.readObjectNotNull(b, Short.class).longValue());
        Assert.assertEquals(Character.MIN_VALUE, serialize.readObjectNotNull(b, Character.class).charValue());
        Assert.assertEquals(Character.MAX_VALUE, serialize.readObjectNotNull(b, Character.class).charValue());
        Assert.assertEquals(new Character('a'), serialize.readObjectNotNull(b, Character.class));
        Assert.assertEquals(Integer.MIN_VALUE, serialize.readObjectNotNull(b, Integer.class).longValue());
        Assert.assertEquals(Integer.MAX_VALUE, serialize.readObjectNotNull(b, Integer.class).longValue());
        Assert.assertEquals(0, serialize.readObjectNotNull(b, Integer.class).longValue());
        Assert.assertEquals(Long.MIN_VALUE, serialize.readObjectNotNull(b, Long.class).longValue());
        Assert.assertEquals(Long.MAX_VALUE, serialize.readObjectNotNull(b, Long.class).longValue());
        Assert.assertEquals(0, serialize.readObjectNotNull(b, Long.class).longValue());
        Assert.assertEquals(0.0, serialize.readObjectNotNull(b, Float.class).floatValue(), 1.0E-20);
        Assert.assertEquals(0.0, serialize.readObjectNotNull(b, Double.class).doubleValue(), 1.0E-20);
    }

    @Test
    public void testObjectVerification() {
        serialize.registerClass(2, TClass1.class, new TClass1Serializer());
        TClass1 t1 = new TClass1(24223, 0.25F);
        DataOutput out = serialize.getDataOutput(128);
        out.writeClassAndObject(t1);
        out.writeClassAndObject(null);
        out.writeObject(t1, TClass1.class);
        out.writeObject(null, TClass1.class);
        // Test failure
        for (Object o : new Object[]{ new TClass2("abc", 2), Calendar.getInstance(), Lists.newArrayList() }) {
            try {
                out.writeObjectNotNull(o);
                Assert.fail();
            } catch (Exception e) {
            }
        }
        ReadBuffer b = out.getStaticBuffer().asReadBuffer();
        Assert.assertEquals(t1, serialize.readClassAndObject(b));
        Assert.assertNull(serialize.readClassAndObject(b));
        Assert.assertEquals(t1, serialize.readObject(b, TClass1.class));
        Assert.assertNull(serialize.readObject(b, TClass1.class));
        Assert.assertFalse(b.hasRemaining());
    }

    @Test
    public void longWriteTest() {
        multipleStringWrite();
    }

    @Test
    public void largeWriteTest() {
        String base = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";// 26 chars

        String str = "";
        for (int i = 0; i < 100; i++)
            str += base;

        DataOutput out = serialize.getDataOutput(128);
        out.writeObjectNotNull(str);
        ReadBuffer b = out.getStaticBuffer().asReadBuffer();
        if (printStats)
            SerializerTest.log.debug(bufferStats(b));

        Assert.assertEquals(str, serialize.readObjectNotNull(b, String.class));
        Assert.assertFalse(b.hasRemaining());
    }

    @Test
    public void enumSerializeTest() {
        serialize.registerClass(1, TEnum.class, new TEnumSerializer());
        DataOutput out = serialize.getDataOutput(128);
        out.writeObjectNotNull(TEnum.TWO);
        out.writeObjectNotNull(TEnum.THREE);
        ReadBuffer b = out.getStaticBuffer().asReadBuffer();
        if (printStats)
            SerializerTest.log.debug(bufferStats(b));

        Assert.assertEquals(TEnum.TWO, serialize.readObjectNotNull(b, TEnum.class));
        Assert.assertEquals(TEnum.THREE, serialize.readObjectNotNull(b, TEnum.class));
        Assert.assertFalse(b.hasRemaining());
    }

    @Test
    public void testStringCompression() {
        // ASCII encoding
        for (int t = 0; t < 100; t++) {
            String x = SerializerTest.getRandomString(((TEXT_COMRPESSION_THRESHOLD) - 1), SerializerTest.ASCII_VALUE);
            Assert.assertEquals(((x.length()) + 1), getStringBuffer(x).length());
        }
        // SMAZ Encoding
        // String[] texts = {
        // "To Sherlock Holmes she is always the woman. I have seldom heard him mention her under any other name. In his eyes she eclipses and predominates the whole of her sex.",
        // "His manner was not effusive. It seldom was; but he was glad, I think, to see me. With hardly a word spoken, but with a kindly eye, he waved me to an armchair",
        // "I could not help laughing at the ease with which he explained his process of deduction.",
        // "A man entered who could hardly have been less than six feet six inches in height, with the chest and limbs of a Hercules. His dress was rich with a richness which would, in England"
        // };
        // for (String text : texts) {
        // assertTrue(text.length()> StringSerializer.TEXT_COMRPESSION_THRESHOLD);
        // StaticBuffer s = getStringBuffer(text);
        // //            System.out.println(String.format("String length [%s] -> byte size [%s]",text.length(),s.length()));
        // assertTrue(text.length()>s.length()); //Test that actual compression is happening
        // }
        // Gzip Encoding
        String[] patterns = new String[]{ "aQd>@!as/df5h", "sdfodoiwk", "sdf", "ab", "asdfwewefefwdfkajhqwkdhj" };
        int targetLength = (LONG_COMPRESSION_THRESHOLD) * 5;
        for (String pattern : patterns) {
            StringBuilder sb = new StringBuilder(targetLength);
            for (int i = 0; i < (targetLength / (pattern.length())); i++)
                sb.append(pattern);

            String text = sb.toString();
            Assert.assertTrue(((text.length()) > (LONG_COMPRESSION_THRESHOLD)));
            StaticBuffer s = getStringBuffer(text);
            // System.out.println(String.format("String length [%s] -> byte size [%s]",text.length(),s.length()));
            Assert.assertTrue(((text.length()) > ((s.length()) * 10)));// Test that radical compression is happening

        }
        for (int t = 0; t < 10000; t++) {
            String x = SerializerTest.STRING_FACTORY.newInstance();
            DataOutput o = serialize.getDataOutput(64);
            o.writeObject(x, String.class);
            ReadBuffer r = o.getStaticBuffer().asReadBuffer();
            String y = serialize.readObject(r, String.class);
            Assert.assertEquals(x, y);
        }
    }

    @Test
    public void testSerializationMixture() {
        serialize.registerClass(1, TClass1.class, new TClass1Serializer());
        for (int t = 0; t < 1000; t++) {
            DataOutput out = serialize.getDataOutput(128);
            int num = (SerializerTest.random.nextInt(100)) + 1;
            List<SerializerTest.SerialEntry> entries = new ArrayList<SerializerTest.SerialEntry>(num);
            for (int i = 0; i < num; i++) {
                Map.Entry<Class, SerializerTest.Factory> type = Iterables.get(SerializerTest.TYPES.entrySet(), SerializerTest.random.nextInt(SerializerTest.TYPES.size()));
                Object element = type.getValue().newInstance();
                boolean notNull = true;
                if ((SerializerTest.random.nextDouble()) < 0.5) {
                    notNull = false;
                    if ((SerializerTest.random.nextDouble()) < 0.2)
                        element = null;

                }
                entries.add(new SerializerTest.SerialEntry(element, type.getKey(), notNull));
                if (notNull)
                    out.writeObjectNotNull(element);
                else
                    out.writeObject(element, type.getKey());

            }
            StaticBuffer sb = out.getStaticBuffer();
            ReadBuffer in = sb.asReadBuffer();
            for (SerializerTest.SerialEntry entry : entries) {
                Object read;
                if (entry.notNull)
                    read = serialize.readObjectNotNull(in, entry.clazz);
                else
                    read = serialize.readObject(in, entry.clazz);

                if ((entry.object) == null)
                    Assert.assertNull(read);
                else
                    if (entry.clazz.isArray()) {
                        Assert.assertEquals(Array.getLength(entry.object), Array.getLength(read));
                        for (int i = 0; i < (Array.getLength(read)); i++) {
                            Assert.assertEquals(Array.get(entry.object, i), Array.get(read, i));
                        }
                    } else
                        Assert.assertEquals(entry.object, read);


            }
        }
    }

    @Test
    public void testSerializedOrder() {
        serialize.registerClass(1, TClass1.class, new TClass1Serializer());
        Map<Class, SerializerTest.Factory> sortTypes = new HashMap<Class, SerializerTest.Factory>();
        for (Map.Entry<Class, SerializerTest.Factory> entry : SerializerTest.TYPES.entrySet()) {
            if (serialize.isOrderPreservingDatatype(entry.getKey()))
                sortTypes.put(entry.getKey(), entry.getValue());

        }
        Assert.assertEquals(10, sortTypes.size());
        for (int t = 0; t < 3000000; t++) {
            DataOutput o1 = serialize.getDataOutput(64);
            DataOutput o2 = serialize.getDataOutput(64);
            Map.Entry<Class, SerializerTest.Factory> type = Iterables.get(sortTypes.entrySet(), SerializerTest.random.nextInt(sortTypes.size()));
            Comparable c1 = ((Comparable) (type.getValue().newInstance()));
            Comparable c2 = ((Comparable) (type.getValue().newInstance()));
            o1.writeObjectByteOrder(c1, type.getKey());
            o2.writeObjectByteOrder(c2, type.getKey());
            StaticBuffer s1 = o1.getStaticBuffer();
            StaticBuffer s2 = o2.getStaticBuffer();
            Assert.assertEquals(Math.signum(c1.compareTo(c2)), Math.signum(s1.compareTo(s2)), 0.0);
            Object c1o = serialize.readObjectByteOrder(s1.asReadBuffer(), type.getKey());
            Object c2o = serialize.readObjectByteOrder(s2.asReadBuffer(), type.getKey());
            Assert.assertEquals(c1, c1o);
            Assert.assertEquals(c2, c2o);
        }
    }

    private static class SerialEntry {
        final Object object;

        final Class clazz;

        final boolean notNull;

        private SerialEntry(Object object, Class clazz, boolean notNull) {
            this.object = object;
            this.clazz = clazz;
            this.notNull = notNull;
        }
    }

    public interface Factory<T> {
        public T newInstance();
    }

    public static final Random random = new Random();

    public static final int MAX_CHAR_VALUE = 20000;

    public static final int ASCII_VALUE = 128;

    public static final SerializerTest.Factory<String> STRING_FACTORY = new SerializerTest.Factory<String>() {
        @Override
        public String newInstance() {
            if ((SerializerTest.random.nextDouble()) > 0.1) {
                return SerializerTest.getRandomString(((StringSerializer.TEXT_COMRPESSION_THRESHOLD) * 2), ((SerializerTest.random.nextDouble()) > 0.5 ? SerializerTest.ASCII_VALUE : SerializerTest.MAX_CHAR_VALUE));
            } else {
                return SerializerTest.getRandomString(((StringSerializer.LONG_COMPRESSION_THRESHOLD) * 4), ((SerializerTest.random.nextDouble()) > 0.5 ? SerializerTest.ASCII_VALUE : SerializerTest.MAX_CHAR_VALUE));
            }
        }
    };

    public static Map<Class, SerializerTest.Factory> TYPES = new HashMap<Class, SerializerTest.Factory>() {
        {
            put(Byte.class, new SerializerTest.Factory<Byte>() {
                @Override
                public Byte newInstance() {
                    return ((byte) (SerializerTest.random.nextInt()));
                }
            });
            put(Short.class, new SerializerTest.Factory<Short>() {
                @Override
                public Short newInstance() {
                    return ((short) (SerializerTest.random.nextInt()));
                }
            });
            put(Integer.class, new SerializerTest.Factory<Integer>() {
                @Override
                public Integer newInstance() {
                    return SerializerTest.random.nextInt();
                }
            });
            put(Long.class, new SerializerTest.Factory<Long>() {
                @Override
                public Long newInstance() {
                    return SerializerTest.random.nextLong();
                }
            });
            put(Boolean.class, new SerializerTest.Factory<Boolean>() {
                @Override
                public Boolean newInstance() {
                    return (SerializerTest.random.nextInt(2)) == 0;
                }
            });
            put(Character.class, new SerializerTest.Factory<Character>() {
                @Override
                public Character newInstance() {
                    return ((char) (SerializerTest.random.nextInt()));
                }
            });
            put(Date.class, new SerializerTest.Factory<Date>() {
                @Override
                public Date newInstance() {
                    return new Date(SerializerTest.random.nextLong());
                }
            });
            put(Float.class, new SerializerTest.Factory<Float>() {
                @Override
                public Float newInstance() {
                    return ((SerializerTest.random.nextFloat()) * 10000) - (10000 / 2.0F);
                }
            });
            put(Double.class, new SerializerTest.Factory<Double>() {
                @Override
                public Double newInstance() {
                    return ((SerializerTest.random.nextDouble()) * 10000000) - (10000000 / 2.0);
                }
            });
            put(Geoshape.class, new SerializerTest.Factory<Geoshape>() {
                @Override
                public Geoshape newInstance() {
                    if ((SerializerTest.random.nextDouble()) > 0.5)
                        return Geoshape.box(SerializerTest.randomGeoPoint(), SerializerTest.randomGeoPoint(), SerializerTest.randomGeoPoint(), SerializerTest.randomGeoPoint());
                    else
                        return Geoshape.circle(SerializerTest.randomGeoPoint(), SerializerTest.randomGeoPoint(), ((SerializerTest.random.nextInt(100)) + 1));

                }
            });
            put(String.class, SerializerTest.STRING_FACTORY);
            put(boolean[].class, SerializerTest.getArrayFactory(boolean.class, get(Boolean.class)));
            put(byte[].class, SerializerTest.getArrayFactory(byte.class, get(Byte.class)));
            put(short[].class, SerializerTest.getArrayFactory(short.class, get(Short.class)));
            put(int[].class, SerializerTest.getArrayFactory(int.class, get(Integer.class)));
            put(long[].class, SerializerTest.getArrayFactory(long.class, get(Long.class)));
            put(float[].class, SerializerTest.getArrayFactory(float.class, get(Float.class)));
            put(double[].class, SerializerTest.getArrayFactory(double.class, get(Double.class)));
            put(char[].class, SerializerTest.getArrayFactory(char.class, get(Character.class)));
            put(String[].class, SerializerTest.getArrayFactory(String.class, get(String.class)));
            put(TClass1.class, new SerializerTest.Factory<TClass1>() {
                @Override
                public TClass1 newInstance() {
                    return new TClass1(SerializerTest.random.nextLong(), SerializerTest.random.nextFloat());
                }
            });
        }
    };
}

