package com.alibaba.otter.canal.server;


import com.alibaba.otter.canal.protocol.ClientIdentity;
import com.alibaba.otter.canal.protocol.Message;
import com.alibaba.otter.canal.server.embedded.CanalServerWithEmbedded;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.util.CollectionUtils;


public abstract class BaseCanalServerWithEmbededTest {
    protected static final String cluster1 = "127.0.0.1:2188";

    protected static final String DESTINATION = "example";

    protected static final String DETECTING_SQL = "insert into retl.xdual values(1,now()) on duplicate key update x=now()";

    protected static final String MYSQL_ADDRESS = "127.0.0.1";

    protected static final String USERNAME = "canal";

    protected static final String PASSWORD = "canal";

    protected static final String FILTER = ".*\\\\..*";

    private CanalServerWithEmbedded server;

    private ClientIdentity clientIdentity = new ClientIdentity(BaseCanalServerWithEmbededTest.DESTINATION, ((short) (1)));

    @Test
    public void testGetWithoutAck() {
        int maxEmptyCount = 10;
        int emptyCount = 0;
        int totalCount = 0;
        server.subscribe(clientIdentity);
        while (emptyCount < maxEmptyCount) {
            Message message = server.getWithoutAck(clientIdentity, 11);
            if (CollectionUtils.isEmpty(message.getEntries())) {
                emptyCount++;
                try {
                    Thread.sleep((emptyCount * 300L));
                } catch (InterruptedException e) {
                    Assert.fail();
                }
                System.out.println(("empty count : " + emptyCount));
            } else {
                emptyCount = 0;
                totalCount += message.getEntries().size();
                server.ack(clientIdentity, message.getId());
            }
        } 
        System.out.println(("!!!!!! testGetWithoutAck totalCount : " + totalCount));
        server.unsubscribe(clientIdentity);
    }

    @Test
    public void testGet() {
        int maxEmptyCount = 10;
        int emptyCount = 0;
        int totalCount = 0;
        server.subscribe(clientIdentity);
        while (emptyCount < maxEmptyCount) {
            Message message = server.get(clientIdentity, 11);
            if (CollectionUtils.isEmpty(message.getEntries())) {
                emptyCount++;
                try {
                    Thread.sleep((emptyCount * 300L));
                } catch (InterruptedException e) {
                    Assert.fail();
                }
                System.out.println(("empty count : " + emptyCount));
            } else {
                emptyCount = 0;
                totalCount += message.getEntries().size();
            }
        } 
        System.out.println(("!!!!!! testGet totalCount : " + totalCount));
        server.unsubscribe(clientIdentity);
    }
}

