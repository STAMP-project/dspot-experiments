package io.socket.hasbinary;


import JSONObject.NULL;
import java.nio.charset.Charset;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class HasBinaryTest {
    @Test
    public void byteArray() {
        Assert.assertTrue(HasBinary.hasBinary(new byte[0]));
    }

    @Test
    public void anArrayThatDoesNotContainByteArray() throws JSONException {
        JSONArray arr = new JSONArray("[1, \"cool\", 2]");
        Assert.assertTrue((!(HasBinary.hasBinary(arr))));
    }

    @Test
    public void anArrayContainsByteArray() throws JSONException {
        JSONArray arr = new JSONArray("[1, null, 2]");
        arr.put(1, "asdfasdf".getBytes(Charset.forName("UTF-8")));
        Assert.assertTrue(HasBinary.hasBinary(arr));
    }

    @Test
    public void anObjectThatDoesNotContainByteArray() throws JSONException {
        JSONObject ob = new JSONObject("{\"a\": \"a\", \"b\": [], \"c\": 1234}");
        Assert.assertTrue((!(HasBinary.hasBinary(ob))));
    }

    @Test
    public void anObjectThatContainsByteArray() throws JSONException {
        JSONObject ob = new JSONObject("{\"a\": \"a\", \"b\": null, \"c\": 1234}");
        ob.put("b", "abc".getBytes(Charset.forName("UTF-8")));
        Assert.assertTrue(HasBinary.hasBinary(ob));
    }

    @Test
    public void testNull() {
        Assert.assertTrue((!(HasBinary.hasBinary(null))));
    }

    @Test
    public void aComplexObjectThatContainsNoBinary() throws JSONException {
        JSONObject ob = new JSONObject();
        ob.put("x", new JSONArray("[\"a\", \"b\", 123]"));
        ob.put("y", NULL);
        ob.put("z", new JSONObject("{\"a\": \"x\", \"b\": \"y\", \"c\": 3, \"d\": null}"));
        ob.put("w", new JSONArray());
        Assert.assertTrue((!(HasBinary.hasBinary(ob))));
    }

    @Test
    public void aComplexObjectThatContainsBinary() throws JSONException {
        JSONObject ob = new JSONObject();
        ob.put("x", new JSONArray("[\"a\", \"b\", 123]"));
        ob.put("y", NULL);
        ob.put("z", new JSONObject("{\"a\": \"x\", \"b\": \"y\", \"c\": 3, \"d\": null}"));
        ob.put("w", new JSONArray());
        ob.put("bin", "xxx".getBytes(Charset.forName("UTF-8")));
        Assert.assertTrue(HasBinary.hasBinary(ob));
    }
}

