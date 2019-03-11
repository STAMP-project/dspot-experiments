package com.roncoo.example;


import com.roncoo.example.bean.RoncooUserLog;
import com.roncoo.example.dao.RoncooUserLogDao;
import java.util.Date;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringBootDemo141ApplicationTests {
    @Autowired
    private RoncooUserLogDao roncooUserLogDao;

    @Test
    public void insert() {
        RoncooUserLog entity = new RoncooUserLog();
        entity.setUserName("??");
        entity.setUserIp("192.168.0.1");
        entity.setCreateTime(new Date());
        roncooUserLogDao.save(entity);
    }

    @Test
    public void delete() {
        roncooUserLogDao.delete(1);
    }

    @Test
    public void update() {
        RoncooUserLog entity = new RoncooUserLog();
        entity.setId(2);
        entity.setUserName("??2");
        entity.setUserIp("192.168.0.1");
        entity.setCreateTime(new Date());
        roncooUserLogDao.save(entity);
    }

    @Test
    public void select() {
        RoncooUserLog result = roncooUserLogDao.findOne(2);
        System.out.println(result);
    }

    @Test
    public void select2() {
        RoncooUserLog result = roncooUserLogDao.findByUserName("??2");
        System.out.println(result);
    }

    // ??
    @Test
    public void queryForPage() {
        Pageable pageable = new org.springframework.data.domain.PageRequest(0, 20, new Sort(new org.springframework.data.domain.Sort.Order(Direction.DESC, "id")));
        // Page<RoncooUserLog> result = roncooUserLogDao.findByUserName("??2", pageable);
        Page<RoncooUserLog> result = roncooUserLogDao.findAll(pageable);
        System.out.println(result.getContent());
    }
}

