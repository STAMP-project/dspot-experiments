package com.roncoo.education;


import com.roncoo.education.bean.RoncooUser;
import com.roncoo.education.mapper.RoncooUserMapper;
import java.util.Date;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringBootDemo301ApplicationTests {
    @Autowired
    private RoncooUserMapper mapper;

    @Test
    public void insert() {
        RoncooUser roncooUser = new RoncooUser();
        roncooUser.setName("??");
        roncooUser.setCreateTime(new Date());
        int result = mapper.insert(roncooUser);
        System.out.println(result);
    }

    @Test
    public void select() {
        RoncooUser result = mapper.selectByPrimaryKey(2);
        System.out.println(result);
    }
}

