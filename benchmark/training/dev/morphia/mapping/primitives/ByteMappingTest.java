package dev.morphia.mapping.primitives;


import dev.morphia.TestBase;
import dev.morphia.annotations.Id;
import dev.morphia.query.FindOptions;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Test;


public class ByteMappingTest extends TestBase {
    @Test
    public void blobs() {
        getMorphia().map(ByteMappingTest.Bytes.class);
        final String data = "{ \"primitiveArray\": BinData(0, " + "\"V2hlbiBpbiB0aGUgY291cnNlIG9mIGh1bWFuIGV2ZW50cyBpdCBiZWNvbWVzIG5lY2Vzc2FyeSB0byBzdWJzY3JpYmUu\") }";
        getDb().eval((("db.Bytes.insert(" + data) + ")"));
        final ByteMappingTest.Bytes loaded = getDs().find(ByteMappingTest.Bytes.class).find(new FindOptions().limit(1)).tryNext();
    }

    @Test
    public void testMapping() {
        getMorphia().map(ByteMappingTest.Bytes.class);
        final ByteMappingTest.Bytes ent = new ByteMappingTest.Bytes();
        ent.listWrapperArray.add(new Byte[]{ 1, 2 });
        ent.listPrimitiveArray.add(new byte[]{ 2, 3, 12 });
        ent.listWrapper.addAll(Arrays.asList(((byte) (148)), ((byte) (6)), ((byte) (255))));
        ent.singlePrimitive = 100;
        ent.singleWrapper = 47;
        ent.primitiveArray = new byte[]{ 5, 93 };
        ent.wrapperArray = new Byte[]{ 55, 16, 99 };
        ent.nestedPrimitiveArray = new byte[][]{ new byte[]{ 1, 2 }, new byte[]{ 3, 4 } };
        ent.nestedWrapperArray = new Byte[][]{ new Byte[]{ 1, 2 }, new Byte[]{ 3, 4 } };
        getDs().save(ent);
        final ByteMappingTest.Bytes loaded = getDs().get(ent);
        Assert.assertNotNull(loaded.id);
        Assert.assertArrayEquals(ent.listWrapperArray.get(0), loaded.listWrapperArray.get(0));
        Assert.assertArrayEquals(ent.listPrimitiveArray.get(0), loaded.listPrimitiveArray.get(0));
        Assert.assertEquals(ent.listWrapper, loaded.listWrapper);
        Assert.assertEquals(ent.singlePrimitive, loaded.singlePrimitive, 0);
        Assert.assertEquals(ent.singleWrapper, loaded.singleWrapper, 0);
        Assert.assertArrayEquals(ent.primitiveArray, loaded.primitiveArray);
        Assert.assertArrayEquals(ent.wrapperArray, loaded.wrapperArray);
        Assert.assertArrayEquals(ent.nestedPrimitiveArray, loaded.nestedPrimitiveArray);
        Assert.assertArrayEquals(ent.nestedWrapperArray, loaded.nestedWrapperArray);
    }

    private static class Bytes {
        private final List<Byte[]> listWrapperArray = new ArrayList<Byte[]>();

        private final List<byte[]> listPrimitiveArray = new ArrayList<byte[]>();

        @Id
        private ObjectId id;

        private List<Byte> listWrapper = new ArrayList<Byte>();

        private byte singlePrimitive;

        private Byte singleWrapper;

        private byte[] primitiveArray;

        private Byte[] wrapperArray;

        private byte[][] nestedPrimitiveArray;

        private Byte[][] nestedWrapperArray;
    }

    private static class MbbDocument {
        @Id
        private ObjectId id;
    }
}

