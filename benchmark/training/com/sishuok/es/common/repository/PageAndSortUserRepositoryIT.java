/**
 * Copyright (c) 2005-2012 https://github.com/zhangkaitao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.sishuok.es.common.repository;


import Sort.Direction;
import Sort.Order;
import com.sishuok.es.common.entity.Sex;
import com.sishuok.es.common.entity.User;
import com.sishuok.es.common.test.BaseUserIT;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;


/**
 * <p>User Repository????</p>
 * <p>????????HSQL???????</p>
 * <p>User: Zhang Kaitao
 * <p>Date: 13-1-14 ??2:36
 * <p>Version: 1.0
 */
public class PageAndSortUserRepositoryIT extends BaseUserIT {
    @Autowired
    private UserRepository userRepository;

    @Test
    public void testFindAllForPage() {
        for (int i = 0; i < 15; i++) {
            save(createUser());
        }
        PageRequest pageRequest = new PageRequest(1, 5);
        Page<User> page = userRepository.findAll(pageRequest);
        Assert.assertEquals(pageRequest.getPageSize(), page.getNumberOfElements());
        Assert.assertEquals(3, page.getTotalPages());
    }

    @Test
    public void testFindByBaseInfoSexForPage() {
        for (int i = 0; i < 15; i++) {
            save(createUser());
        }
        PageRequest pageRequest = new PageRequest(1, 5);
        Page<User> page = userRepository.findByBaseInfoSex(Sex.male, pageRequest);
        Assert.assertEquals(pageRequest.getPageSize(), page.getNumberOfElements());
        Assert.assertEquals(3, page.getTotalPages());
        page = userRepository.findByBaseInfoSex(Sex.female, pageRequest);
        Assert.assertEquals(0, page.getNumberOfElements());
        Assert.assertEquals(0, page.getTotalPages());
    }

    @Test
    public void testFindAllForSort() {
        for (int i = 0; i < 15; i++) {
            save(createUser());
        }
        Sort.Order idAsc = new Sort.Order(Direction.ASC, "id");
        Sort.Order usernameDesc = new Sort.Order(Direction.DESC, "username");
        Sort sort = new Sort(idAsc, usernameDesc);
        List<User> userList = userRepository.findAll(sort);
        Assert.assertTrue(((getId()) < (getId())));
    }

    @Test
    public void testFindByBaseInfoSexForSort() {
        for (int i = 0; i < 15; i++) {
            save(createUser());
        }
        Sort.Order idAsc = new Sort.Order(Direction.ASC, "id");
        Sort.Order usernameDesc = new Sort.Order(Direction.DESC, "username");
        Sort sort = new Sort(idAsc, usernameDesc);
        List<User> userList = userRepository.findByBaseInfoSex(Sex.male, sort);
        Assert.assertTrue(((getId()) < (getId())));
    }

    @Test
    public void testFindAllForPageAndSort() {
        for (int i = 0; i < 15; i++) {
            save(createUser());
        }
        Sort.Order idAsc = new Sort.Order(Direction.ASC, "id");
        Sort.Order usernameDesc = new Sort.Order(Direction.DESC, "username");
        Sort sort = new Sort(idAsc, usernameDesc);
        PageRequest pageRequest = new PageRequest(1, 5, sort);
        Page<User> page = userRepository.findAll(pageRequest);
        Assert.assertEquals(pageRequest.getPageSize(), page.getNumberOfElements());
        Assert.assertEquals(3, page.getTotalPages());
        Assert.assertTrue(((getId()) < (getId())));
    }
}

