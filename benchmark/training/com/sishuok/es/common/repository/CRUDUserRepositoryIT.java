/**
 * Copyright (c) 2005-2012 https://github.com/zhangkaitao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.sishuok.es.common.repository;


import com.sishuok.es.common.entity.SchoolType;
import com.sishuok.es.common.entity.User;
import com.sishuok.es.common.test.BaseUserIT;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * <p>User Repository????</p>
 * <p>????????HSQL???????</p>
 * <p>User: Zhang Kaitao
 * <p>Date: 13-1-14 ??2:36
 * <p>Version: 1.0
 */
public class CRUDUserRepositoryIT extends BaseUserIT {
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private User user;

    @Test
    public void testSave() {
        User dbUser = save(user);
        Assert.assertNotNull(getId());
    }

    @Test
    public void testUpdate() {
        User dbUser = save(user);
        clear();
        String newUsername = "zhang$$$$" + (System.currentTimeMillis());
        dbUser.setUsername(newUsername);
        userRepository.save(dbUser);
        clear();
        Assert.assertEquals(newUsername, userRepository.findOne(getId()).getUsername());
    }

    @Test
    public void testUpdateRealname() {
        String realname = "lisi";
        User dbUser = save(user);
        userRepository.updateRealname(realname, getId());
        userRepository.flush();
        entityManager.clear();
        dbUser = userRepository.findOne(getId());
        Assert.assertEquals(realname, dbUser.getBaseInfo().getRealname());
    }

    @Test
    public void testUpdateRealnameWithNamedParam() {
        String realname = "lisi";
        User dbUser = save(user);
        userRepository.updateRealnameWithNamedParam(realname, getId());
        userRepository.flush();
        entityManager.clear();
        dbUser = userRepository.findOne(getId());
        Assert.assertEquals(realname, dbUser.getBaseInfo().getRealname());
    }

    @Test
    public void testDeleteByUsername() {
        User dbUser = save(user);
        userRepository.deleteBaseInfoByUser(getId());
        userRepository.flush();
        entityManager.clear();
        dbUser = userRepository.findOne(getId());
        Assert.assertNull(dbUser.getBaseInfo());
    }

    @Test
    public void testFindByUsername() {
        userRepository.save(user);
        User dbUser = userRepository.findByUsername(user.getUsername());
        Assert.assertNotNull(dbUser);
    }

    @Test
    public void testFindByBaseInfoSex() {
        userRepository.save(user);
        User dbUser = userRepository.findByBaseInfoSex(user.getBaseInfo().getSex());
        Assert.assertNotNull(dbUser);
    }

    @Test
    public void testFindByBaseInfoSexAndShcoolInfoType() {
        userRepository.save(user);
        User dbUser = userRepository.findByBaseInfoSexAndShcoolInfoSetType(user.getBaseInfo().getSex(), SchoolType.secondary_school);
        Assert.assertNotNull(dbUser);
    }
}

