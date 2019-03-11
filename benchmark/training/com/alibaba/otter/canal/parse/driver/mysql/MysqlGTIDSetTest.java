package com.alibaba.otter.canal.parse.driver.mysql;


import com.alibaba.otter.canal.parse.driver.mysql.packets.GTIDSet;
import com.alibaba.otter.canal.parse.driver.mysql.packets.MysqlGTIDSet;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by hiwjd on 2018/4/25. hiwjd0@gmail.com
 */
public class MysqlGTIDSetTest {
    @Test
    public void testEncode() throws IOException {
        GTIDSet gtidSet = MysqlGTIDSet.parse("726757ad-4455-11e8-ae04-0242ac110002:1");
        byte[] bytes = gtidSet.encode();
        byte[] expected = new byte[]{ 1, 0, 0, 0, 0, 0, 0, 0, 114, 103, 87, ((byte) (173)), 68, 85, 17, ((byte) (232)), ((byte) (174)), 4, 2, 66, ((byte) (172)), 17, 0, 2, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0 };
        for (int i = 0; i < (bytes.length); i++) {
            Assert.assertEquals(expected[i], bytes[i]);
        }
    }

    @Test
    public void testParse() {
        Map<String, MysqlGTIDSet> cases = new HashMap<String, MysqlGTIDSet>(5);
        cases.put("726757ad-4455-11e8-ae04-0242ac110002:1", buildForTest(new MysqlGTIDSetTest.Material("726757ad-4455-11e8-ae04-0242ac110002", 1, 2)));
        cases.put("726757ad-4455-11e8-ae04-0242ac110002:1-3", buildForTest(new MysqlGTIDSetTest.Material("726757ad-4455-11e8-ae04-0242ac110002", 1, 4)));
        cases.put("726757ad-4455-11e8-ae04-0242ac110002:1-3:4", buildForTest(new MysqlGTIDSetTest.Material("726757ad-4455-11e8-ae04-0242ac110002", 1, 5)));
        cases.put("726757ad-4455-11e8-ae04-0242ac110002:1-3:7-9", buildForTest(new MysqlGTIDSetTest.Material("726757ad-4455-11e8-ae04-0242ac110002", 1, 4, 7, 10)));
        cases.put("726757ad-4455-11e8-ae04-0242ac110002:1-3,726757ad-4455-11e8-ae04-0242ac110003:4", buildForTest(Arrays.asList(new MysqlGTIDSetTest.Material("726757ad-4455-11e8-ae04-0242ac110002", 1, 4), new MysqlGTIDSetTest.Material("726757ad-4455-11e8-ae04-0242ac110003", 4, 5))));
        for (Map.Entry<String, MysqlGTIDSet> entry : cases.entrySet()) {
            MysqlGTIDSet expected = entry.getValue();
            MysqlGTIDSet actual = MysqlGTIDSet.parse(entry.getKey());
            Assert.assertEquals(expected, actual);
        }
    }

    private static class Material {
        public Material(String uuid, long start, long stop) {
            this.uuid = uuid;
            this.start = start;
            this.stop = stop;
            this.start1 = 0;
            this.stop1 = 0;
        }

        public Material(String uuid, long start, long stop, long start1, long stop1) {
            this.uuid = uuid;
            this.start = start;
            this.stop = stop;
            this.start1 = start1;
            this.stop1 = stop1;
        }

        public String uuid;

        public long start;

        public long stop;

        public long start1;

        public long stop1;
    }
}

