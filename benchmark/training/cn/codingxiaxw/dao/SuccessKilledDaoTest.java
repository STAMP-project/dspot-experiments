package cn.codingxiaxw.dao;


import cn.codingxiaxw.entity.SuccessKilled;
import javax.annotation.Resource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


/**
 * Created by codingBoy on 16/11/27.
 */
// ??junit spring?????
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:spring/spring-dao.xml" })
public class SuccessKilledDaoTest {
    @Resource
    private SuccessKilledDao successKilledDao;

    @Test
    public void insertSuccessKilled() throws Exception {
        long seckillId = 1000L;
        long userPhone = 13476191877L;
        int insertCount = successKilledDao.insertSuccessKilled(seckillId, userPhone);
        System.out.println(("insertCount=" + insertCount));
    }

    @Test
    public void queryByIdWithSeckill() throws Exception {
        long seckillId = 1000L;
        long userPhone = 13476191877L;
        SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(seckillId, userPhone);
        System.out.println(successKilled);
        System.out.println(successKilled.getSeckill());
    }
}

