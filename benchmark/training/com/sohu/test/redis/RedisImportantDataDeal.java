package com.sohu.test.redis;


import InstanceStatusEnum.GOOD_STATUS;
import com.sohu.cache.dao.InstanceDao;
import com.sohu.cache.entity.InstanceInfo;
import com.sohu.cache.redis.RedisCenter;
import com.sohu.cache.util.TypeUtil;
import com.sohu.test.BaseTest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import javax.annotation.Resource;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;


/**
 * redis??????????BaseTest?local???
 *
 * @author leifu
 * @unknown 2015?3?4?
 * @unknown ??11:04:04
 */
public class RedisImportantDataDeal extends BaseTest {
    private static final Logger logger = LoggerFactory.getLogger(RedisImportantDataDeal.class);

    @Resource(name = "instanceDao")
    private InstanceDao instanceDao;

    @Resource(name = "redisCenter")
    private RedisCenter redisCenter;

    @Test
    public void clearAllAppData() {
        // /////////////???????/////////////////
        // /////////////???????/////////////////
        // /////////////???????/////////////////
        long appId = 0L;
        // /////////////???????///////////////
        // /////////////???????/////////////////
        // /////////////???????/////////////////
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        RedisImportantDataDeal.logger.warn((("?????appId:" + appId) + "???????(??y????):"));
        String confirm = null;
        try {
            confirm = br.readLine();
        } catch (IOException e1) {
            e1.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if (!("y".equals(confirm))) {
            return;
        }
        List<InstanceInfo> instanceList = instanceDao.getInstListByAppId(appId);
        if (CollectionUtils.isEmpty(instanceList)) {
            RedisImportantDataDeal.logger.error("appId: {}, ??????????????");
        }
        for (InstanceInfo instance : instanceList) {
            if ((instance.getStatus()) != (GOOD_STATUS.getStatus())) {
                continue;
            }
            String host = instance.getIp();
            int port = instance.getPort();
            // master + ?sentinel??
            Boolean isMater = redisCenter.isMaster(appId, host, port);
            if (((isMater != null) && (isMater.equals(true))) && (!(TypeUtil.isRedisSentinel(instance.getType())))) {
                Jedis jedis = new Jedis(host, port, 30000);
                try {
                    RedisImportantDataDeal.logger.info("{}:{} ??????", host, port);
                    long start = System.currentTimeMillis();
                    jedis.flushAll();
                    RedisImportantDataDeal.logger.info("{}:{} ????, ??:{} ms", host, port, ((System.currentTimeMillis()) - start));
                } catch (Exception e) {
                    RedisImportantDataDeal.logger.error(e.getMessage());
                } finally {
                    jedis.close();
                }
            }
        }
    }
}

