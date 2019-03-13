package com.zheng.upms.rpc.service;


import com.zheng.upms.dao.mapper.UpmsUserMapper;
import com.zheng.upms.rpc.api.UpmsPermissionService;
import com.zheng.upms.rpc.api.UpmsSystemService;
import com.zheng.upms.rpc.api.UpmsUserService;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;


/**
 * ????
 * Created by shuzheng on 2017/2/19.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:applicationContext.xml", "classpath:META-INF/spring/applicationContext-jdbc.xml", "classpath:META-INF/spring/applicationContext-listener.xml" })
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
public class UpmsServiceTest {
    @Autowired
    private UpmsSystemService upmsSystemService;

    @Autowired
    private UpmsUserMapper upmsUserMapper;

    @Autowired
    private UpmsUserService upmsUserService;

    @Autowired
    private UpmsPermissionService upmsPermissionService;

    @Test
    public void index() {
        UpmsUser upmsUser = new UpmsUser();
        upmsUser.setAvatar("");
        upmsUser.setCtime(System.currentTimeMillis());
        upmsUser.setEmail("");
        upmsUser.setLocked(((byte) (0)));
        upmsUser.setPassword("xxx");
        upmsUser.setPhone("");
        upmsUser.setRealname("zsz");
        upmsUser.setSex(((byte) (1)));
        upmsUser.setSalt("");
        upmsUser.setUsername("zsz");
        upmsUserService.insertSelective(upmsUser);
        System.out.println(upmsUser.getUserId());
    }

    @Test
    public void selectForPage() {
        // ????????+??????
        UpmsPermissionExample upmsPermissionExample = new UpmsPermissionExample();
        upmsPermissionExample.createCriteria().andSystemIdEqualTo(1);
        List<UpmsPermission> upmsPermissions = upmsPermissionService.selectByExampleForStartPage(upmsPermissionExample, 2, 20);
        System.out.println(upmsPermissions.size());
        // ??????offset+limit??
        upmsPermissionExample = new UpmsPermissionExample();
        upmsPermissionExample.createCriteria().andSystemIdEqualTo(2);
        upmsPermissions = upmsPermissionService.selectByExampleForOffsetPage(upmsPermissionExample, 3, 5);
        System.out.println(upmsPermissions.size());
    }
}

