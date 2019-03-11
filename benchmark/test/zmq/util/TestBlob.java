package zmq.util;


import ZMQ.CHARSET;
import java.util.HashMap;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestBlob {
    @Test
    public void testBlobMap() {
        HashMap<Blob, String> map = new HashMap<Blob, String>();
        Blob b = Blob.createBlob("a".getBytes(CHARSET));
        map.put(b, "aa");
        Assert.assertThat(map.remove(b), CoreMatchers.notNullValue());
        Assert.assertThat(map.size(), CoreMatchers.is(0));
    }
}

