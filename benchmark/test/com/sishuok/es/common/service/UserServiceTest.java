/**
 * Copyright (c) 2005-2012 https://github.com/zhangkaitao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.sishuok.es.common.service;


import Sort.Direction;
import com.sishuok.es.common.entity.User;
import com.sishuok.es.common.entity.search.Searchable;
import com.sishuok.es.common.test.BaseUserIT;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;


/**
 * <p>User: Zhang Kaitao
 * <p>Date: 13-1-17 ??7:55
 * <p>Version: 1.0
 */
public class UserServiceTest extends BaseUserIT {
    @Autowired
    protected UserService userService;

    @Test
    public void testSave() {
        User dbUser = save(createUser());
        Assert.assertNotNull(getId());
    }

    @Test
    public void testUpdate() {
        User dbUser = save(createUser());
        clear();
        String newUsername = "zhang$$$$" + (System.currentTimeMillis());
        dbUser.setUsername(newUsername);
        update(dbUser);
        clear();
        Assert.assertEquals(newUsername, userService.findOne(getId()).getUsername());
    }

    @Test
    public void testDeleteById() {
        User dbUser = save(createUser());
        clear();
        userService.delete(getId());
        clear();
        Assert.assertNull(userService.findOne(getId()));
    }

    @Test
    public void testDeleteByEntity() {
        User dbUser = save(createUser());
        clear();
        delete(dbUser);
        clear();
        Assert.assertNull(userService.findOne(getId()));
    }

    @Test
    public void testFindOne() {
        User dbUser = save(createUser());
        clear();
        Assert.assertNotNull(userService.findOne(getId()));
    }

    @Test
    public void testExists() {
        User dbUser = save(createUser());
        clear();
        Assert.assertTrue(userService.exists(getId()));
    }

    @Test
    public void testCount() {
        int count = 15;
        for (int i = 0; i < count; i++) {
            userService.save(createUser());
        }
        Assert.assertEquals(count, count());
    }

    @Test
    public void testFindAll() {
        int count = 15;
        User user = null;
        for (int i = 0; i < count; i++) {
            user = save(createUser());
        }
        List<User> userList = findAll();
        Assert.assertEquals(count, userList.size());
        Assert.assertTrue(userList.contains(user));
    }

    @Test
    public void testFindAllBySort() {
        int count = 15;
        User user = null;
        for (int i = 0; i < count; i++) {
            user = save(createUser());
        }
        Sort sortDesc = new Sort(Direction.DESC, "id");
        Sort sortAsc = new Sort(Direction.ASC, "id");
        List<User> userDescList = userService.findAll(sortDesc);
        List<User> userAscList = userService.findAll(sortAsc);
        Assert.assertEquals(count, userDescList.size());
        Assert.assertEquals(count, userAscList.size());
        Assert.assertTrue(userDescList.contains(user));
        Assert.assertTrue(userAscList.contains(user));
        Assert.assertTrue(((getId()) < (getId())));
        Assert.assertTrue(((getId()) > (getId())));
    }

    @Test
    public void testFindAllByPageableAndSortDesc() {
        int count = 15;
        User lastUser = null;
        for (int i = 0; i < count; i++) {
            lastUser = save(createUser());
        }
        Sort sortDesc = new Sort(Direction.DESC, "id");
        Pageable pageable = new org.springframework.data.domain.PageRequest(0, 5, sortDesc);
        Page<User> userPage = userService.findAll(pageable);
        Assert.assertEquals(5, userPage.getNumberOfElements());
        Assert.assertTrue(userPage.getContent().contains(lastUser));
        Assert.assertTrue(((getId()) > (getId())));
    }

    @Test
    public void testFindAllByPageableAndSortAsc() {
        int count = 15;
        User lastUser = null;
        for (int i = 0; i < count; i++) {
            lastUser = save(createUser());
        }
        Sort sortAsc = new Sort(Direction.ASC, "id");
        Pageable pageable = new org.springframework.data.domain.PageRequest(0, 5, sortAsc);
        Page<User> userPage = userService.findAll(pageable);
        Assert.assertEquals(5, userPage.getNumberOfElements());
        Assert.assertFalse(userPage.getContent().contains(lastUser));
        Assert.assertTrue(((getId()) < (getId())));
    }

    @Test
    public void testFindAllBySearchAndNoPage() {
        int count = 15;
        User lastUser = null;
        for (int i = 0; i < count; i++) {
            lastUser = createUser();
            lastUser.setUsername(("zhang" + i));
            userService.save(lastUser);
        }
        Map<String, Object> searchParams = new HashMap<String, Object>();
        searchParams.put("username_like", "zhang");
        Searchable search = Searchable.newSearchable(searchParams);
        List<User> userList = userService.findAllWithNoPageNoSort(search);
        Assert.assertEquals(count, userList.size());
        Assert.assertTrue(userList.contains(lastUser));
    }

    @Test
    public void testFindAllBySearchAndSort() {
        int count = 15;
        User lastUser = null;
        for (int i = 0; i < count; i++) {
            lastUser = createUser();
            lastUser.setUsername(("zhang" + i));
            userService.save(lastUser);
        }
        Map<String, Object> searchParams = new HashMap<String, Object>();
        searchParams.put("username_like", "zhang");
        Sort sortDesc = new Sort(Direction.DESC, "id");
        Searchable search = Searchable.newSearchable(searchParams).addSort(sortDesc);
        List<User> userList = userService.findAllWithSort(search);
        Assert.assertEquals(count, userList.size());
        Assert.assertTrue(userList.contains(lastUser));
        Assert.assertTrue(((getId()) > (getId())));
    }

    @Test
    public void testFindAllBySearchAndPageableAndSortAsc() {
        int count = 15;
        User lastUser = null;
        for (int i = 0; i < count; i++) {
            lastUser = save(createUser());
        }
        Sort sortAsc = new Sort(Direction.ASC, "id");
        Pageable pageable = new org.springframework.data.domain.PageRequest(0, 5, sortAsc);
        Map<String, Object> searchParams = new HashMap<String, Object>();
        searchParams.put("username_like", "zhang");
        Searchable search = Searchable.newSearchable(searchParams).setPage(pageable);
        Page<User> userPage = userService.findAll(search);
        Assert.assertEquals(5, userPage.getNumberOfElements());
        Assert.assertFalse(userPage.getContent().contains(lastUser));
        Assert.assertTrue(((getId()) < (getId())));
    }
}

