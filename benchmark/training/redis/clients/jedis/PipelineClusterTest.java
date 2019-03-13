package redis.clients.jedis;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.Test;
import redis.clients.util.SafeEncoder;


public class PipelineClusterTest {
    private PipelineCluster pipelineCluster;

    private List<String> keys = new ArrayList<String>();

    @Test
    public void testMset() throws Exception {
        Map<String, String> keyValues = new HashMap<String, String>();
        for (int i = 0; i < (keys.size()); i++) {
            keyValues.put(keys.get(i), ("value-" + (i + 1)));
        }
        String response = pipelineCluster.mset(keyValues);
        System.out.println(response);
    }

    @Test
    public void testMget() throws Exception {
        Map<String, String> keyValues = pipelineCluster.mget(keys);
        System.out.println(keyValues);
    }

    @Test
    public void testMdel() throws Exception {
        long deleteCount = pipelineCluster.mdel(keys);
        System.out.println(deleteCount);
    }

    @Test
    public void testHset() throws Exception {
        String key = "we:media:24:key";
        Set<String> keys = pipelineCluster.hkeys(key);
        System.out.println(keys.size());
        for (String vid : keys) {
            System.out.println(vid);
        }
    }

    @Test
    public void testSScan() throws Exception {
        String key = "sscan:test:1";
        for (int i = 1; i <= 500; i++) {
            pipelineCluster.sadd(key, ("v=" + i));
        }
        String cursor = "0";
        int count = 200;
        ScanParams params = new ScanParams();
        params.count(count);
        while (true) {
            ScanResult<byte[]> sscan = pipelineCluster.sscan(SafeEncoder.encode(key), SafeEncoder.encode(cursor), params);
            List<byte[]> list = sscan.getResult();
            cursor = sscan.getCursor();
            System.out.println(((("cursor=" + cursor) + " size") + (list.size())));
            if (cursor.equals("0")) {
                break;
            }
        } 
        pipelineCluster.del(key);
    }

    @Test
    public void testMHgetAll() {
        List<String> keys = new ArrayList<String>();
        keys.add("ugc:video:feature:5946211");
        keys.add("ugc:video:feature:30491583");
        keys.add("ugc:video:feature:63108807");
        keys.add("ugc:video:feature:77257903");
        keys.add("ugc:video:feature:10113377");
        keys.add("ugc:video:feature:30542906");
        keys.add("ugc:video:feature:54608980");
        keys.add("ugc:video:feature:72082818");
        Map<String, Map<String, String>> mmap = pipelineCluster.mHgetAll(keys);
        for (Map.Entry<String, Map<String, String>> entry : mmap.entrySet()) {
            System.out.println(entry.getKey());
            for (Map.Entry<String, String> entry2 : entry.getValue().entrySet()) {
                System.out.println(((("\t" + (entry2.getKey())) + ":") + (entry2.getValue())));
            }
        }
    }
}

