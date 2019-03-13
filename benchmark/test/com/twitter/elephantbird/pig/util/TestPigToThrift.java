package com.twitter.elephantbird.pig.util;


import KeyEnum.A;
import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableMap;
import com.twitter.elephantbird.pig.test.thrift.MapKeyTest;
import com.twitter.elephantbird.thrift.test.Person;
import java.nio.ByteBuffer;
import org.apache.pig.data.TupleFactory;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit tests for {@link PigToThrift}.
 *
 * @author Andy Schlaikjer
 */
public class TestPigToThrift {
    private static final TupleFactory TF = TupleFactory.getInstance();

    @Test
    public void testPerson() {
        Person expected = TestPigToThrift.personMessage("Joe", 1, null, "123-456-7890", "HOME");
        Person actual = PigToThrift.newInstance(Person.class).getThriftObject(TestPigToThrift.personTuple("Joe", 1, null, "123-456-7890", "HOME"));
        Assert.assertNotNull(actual);
        Assert.assertEquals(expected, actual);
    }

    // (expected = RuntimeException.class)
    @Test
    public void testPersonBadEnumValue() {
        PigToThrift.newInstance(Person.class).getThriftObject(TestPigToThrift.personTuple("Joe", 1, null, "123-456-7890", "ASDF"));
    }

    @Test
    public void testSupportedMapKeyTypes() {
        MapKeyTest expected = new MapKeyTest().setBooleans(ImmutableMap.of(true, 1)).setBytes(ImmutableMap.of(((byte) (1)), 1)).setShorts(ImmutableMap.of(((short) (1)), 1)).setInts(ImmutableMap.of(1, 1)).setLongs(ImmutableMap.of(1L, 1)).setDoubles(ImmutableMap.of(1.0, 1)).setEnums(ImmutableMap.of(A, 1)).setStrings(ImmutableMap.of("a", 1)).setBinaries(ImmutableMap.of(ByteBuffer.wrap("1".getBytes(Charsets.UTF_8)), 1));
        MapKeyTest actual = PigToThrift.newInstance(MapKeyTest.class).getThriftObject(TestPigToThrift.tuple(ImmutableMap.of("true", 1), ImmutableMap.of("1", 1), ImmutableMap.of("1", 1), ImmutableMap.of("1", 1), ImmutableMap.of("1", 1), ImmutableMap.of("1.0", 1), ImmutableMap.of(A.name(), 1), ImmutableMap.of("a", 1), ImmutableMap.of("1", 1)));
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testMapKeyConversionFailure() {
        MapKeyTest fail = PigToThrift.newInstance(MapKeyTest.class).getThriftObject(TestPigToThrift.tuple(null, ImmutableMap.of("notabyte", 1)));
        Assert.assertNotNull(fail);
        Assert.assertNull(fail.getBytes());
    }
}

