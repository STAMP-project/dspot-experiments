package cn.codingxiaxw.dao.cache;


import cn.codingxiaxw.dao.SeckillDao;
import cn.codingxiaxw.entity.Seckill;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


/**
 * Created by codingBoy on 17/2/17.
 */
// ??junit spring?????
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:spring/spring-dao.xml" })
public class RedisDaoTest {
    private long id = 1001;

    @Autowired
    private RedisDao redisDao;

    @Autowired
    private SeckillDao seckillDao;

    @Test
    public void testSeckill() {
        // get and put
        Seckill seckill = redisDao.getSeckill(id);
        if (seckill == null) {
            seckill = seckillDao.queryById(id);
            if (seckill != null) {
                String result = redisDao.putSeckill(seckill);
                System.out.println(result);
                seckill = redisDao.getSeckill(id);
                System.out.println(seckill);
            }
        }
    }

    @Test
    public void getFromRedisOrDb() {
        Seckill seckill = redisDao.getOrPutSeckill(id, ( i) -> seckillDao.queryById(i));
        Assert.assertEquals(1001, seckill.getSeckillId());
        Assert.assertNotNull(redisDao.getSeckill(id));
    }
}

