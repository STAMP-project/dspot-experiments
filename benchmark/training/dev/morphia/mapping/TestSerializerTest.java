package dev.morphia.mapping;


import dev.morphia.TestBase;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Serialized;
import java.io.IOException;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Uwe Schaefer, (us@thomas-daily.de)
 */
public class TestSerializerTest extends TestBase {
    private static final String TEST_TEXT = "In 1970, the British Empire lay in ruins, and foreign nationalists frequented the streets - many of them Hungarians (not the " + "streets - the foreign nationals). Anyway, many of these Hungarians went into tobacconist's shops to buy cigarettes.... ";

    @Test
    public final void testSerialize() throws IOException, ClassNotFoundException {
        final byte[] test = new byte[2048];
        final byte[] stringBytes = TestSerializerTest.TEST_TEXT.getBytes();
        System.arraycopy(stringBytes, 0, test, 0, stringBytes.length);
        byte[] ser = Serializer.serialize(test, false);
        byte[] after = ((byte[]) (Serializer.deserialize(ser, false)));
        Assert.assertTrue(((ser.length) > 2048));
        Assert.assertTrue(((after.length) == 2048));
        Assert.assertTrue(new String(after).startsWith(TestSerializerTest.TEST_TEXT));
        ser = Serializer.serialize(test, true);
        after = ((byte[]) (Serializer.deserialize(ser, true)));
        Assert.assertTrue(((ser.length) < 2048));
        Assert.assertTrue(((after.length) == 2048));
        Assert.assertTrue(new String(after).startsWith(TestSerializerTest.TEST_TEXT));
    }

    @Test
    public final void testSerializedAttribute() {
        final byte[] test = new byte[2048];
        final byte[] stringBytes = TestSerializerTest.TEST_TEXT.getBytes();
        System.arraycopy(stringBytes, 0, test, 0, stringBytes.length);
        TestSerializerTest.E e = new TestSerializerTest.E();
        e.payload1 = test;
        e.payload2 = test;
        getDs().save(e);
        e = getDs().get(e);
        Assert.assertTrue(((e.payload1.length) == 2048));
        Assert.assertTrue(new String(e.payload1).startsWith(TestSerializerTest.TEST_TEXT));
        Assert.assertTrue(((e.payload2.length) == 2048));
        Assert.assertTrue(new String(e.payload2).startsWith(TestSerializerTest.TEST_TEXT));
    }

    private static class E {
        @Id
        private ObjectId id;

        @Serialized
        private byte[] payload1;

        @Serialized
        private byte[] payload2;
    }
}

