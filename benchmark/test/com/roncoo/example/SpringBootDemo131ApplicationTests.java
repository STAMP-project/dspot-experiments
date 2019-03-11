package com.roncoo.example;


import com.roncoo.example.bean.RoncooUser;
import com.roncoo.example.dao.RoncooUserDao;
import com.roncoo.example.util.base.Page;
import java.util.Date;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringBootDemo131ApplicationTests {
    @Autowired
    private RoncooUserDao roncooUserDao;

    @Test
    public void insert() {
        RoncooUser roncooUser = new RoncooUser();
        roncooUser.setName("??");
        roncooUser.setCreateTime(new Date());
        int result = roncooUserDao.insert(roncooUser);
        System.out.println(result);
    }

    @Test
    public void delete() {
        int result = roncooUserDao.deleteById(1);
        System.out.println(result);
    }

    @Test
    public void update() {
        RoncooUser roncooUser = new RoncooUser();
        roncooUser.setId(2);
        roncooUser.setName("??2");
        roncooUser.setCreateTime(new Date());
        int result = roncooUserDao.updateById(roncooUser);
        System.out.println(result);
    }

    @Test
    public void select() {
        RoncooUser result = roncooUserDao.selectById(2);
        System.out.println(result);
    }

    // ????
    @Test
    public void queryForPage() {
        Page<RoncooUser> result = roncooUserDao.queryForPage(1, 20, "??");
        System.out.println(result.getList());
    }
}

