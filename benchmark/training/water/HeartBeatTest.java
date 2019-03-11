package water;


import org.junit.Assert;
import org.junit.Test;


public class HeartBeatTest {
    @Test
    public void testSetMem() {
        final long mem = (((3L * 1024) * 1024) * 1024) * 1024;// 3 TB

        HeartBeat hb = new HeartBeat();
        hb.set_kv_mem(mem);
        hb.set_pojo_mem((mem + 1));
        hb.set_free_mem((mem + 2));
        hb.set_swap_mem((mem + 3));
        Assert.assertEquals(mem, hb.get_kv_mem());
        Assert.assertEquals((mem + 1), hb.get_pojo_mem());
        Assert.assertEquals((mem + 2), hb.get_free_mem());
        Assert.assertEquals((mem + 3), hb.get_swap_mem());
    }
}

