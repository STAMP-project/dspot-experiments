package io.protostuff;


import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Kostiantyn Shchepanovskyi
 */
public class JsonUnsignedSerializationTest {
    public static final String JSON = "{" + (((("\"uint32\":4294967295," + "\"uint64\":18446744073709551615,") + "\"fixed32\":4294967295,") + "\"fixed64\":18446744073709551615") + "}");

    @Test
    public void testSerialize() throws Exception {
        UnsignedNumbers msg = new UnsignedNumbers();
        msg.setUint32(-1);
        msg.setFixed32(-1);
        msg.setUint64(-1L);
        msg.setFixed64(-1L);
        byte[] bytes = JsonIOUtil.toByteArray(msg, UnsignedNumbers.getSchema(), false);
        String result = new String(bytes);
        Assert.assertEquals(JsonUnsignedSerializationTest.JSON, result);
    }

    @Test
    public void testDeserialize() throws Exception {
        UnsignedNumbers msg = new UnsignedNumbers();
        JsonIOUtil.mergeFrom(JsonUnsignedSerializationTest.JSON.getBytes(), msg, UnsignedNumbers.getSchema(), false);
        Assert.assertEquals(Integer.valueOf((-1)), msg.getUint32());
        Assert.assertEquals(Integer.valueOf((-1)), msg.getFixed32());
        Assert.assertEquals(Long.valueOf((-1L)), msg.getUint64());
        Assert.assertEquals(Long.valueOf((-1L)), msg.getFixed64());
    }

    @Test
    public void testDeserializeDataSerializedAsSignedNumbers_backward_comp() throws Exception {
        UnsignedNumbers msg = new UnsignedNumbers();
        String oldJson = "{" + (((("\"uint32\":-1," + "\"uint64\":-1,") + "\"fixed32\":-1,") + "\"fixed64\":-1") + "}");
        JsonIOUtil.mergeFrom(oldJson.getBytes(), msg, UnsignedNumbers.getSchema(), false);
        Assert.assertEquals(Integer.valueOf((-1)), msg.getUint32());
        Assert.assertEquals(Integer.valueOf((-1)), msg.getFixed32());
        Assert.assertEquals(Long.valueOf((-1L)), msg.getUint64());
        Assert.assertEquals(Long.valueOf((-1L)), msg.getFixed64());
    }
}

