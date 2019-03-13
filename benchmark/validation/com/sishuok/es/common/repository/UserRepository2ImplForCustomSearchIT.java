/**
 * Copyright (c) 2005-2012 https://github.com/zhangkaitao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.sishuok.es.common.repository;


import com.sishuok.es.common.entity.User;
import com.sishuok.es.common.entity.search.Searchable;
import com.sishuok.es.common.test.BaseUserIT;
import java.util.HashMap;
import java.util.Map;
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
public class UserRepository2ImplForCustomSearchIT extends BaseUserIT {
    @Autowired
    private UserRepository2 userRepository2;

    @Test
    public void testFindAllByCustomSearch1() {
        int count = 15;
        for (int i = 0; i < count; i++) {
            User user = createUser();
            user.getBaseInfo().setRealname(("zhang" + i));
            save(user);
        }
        Searchable search = Searchable.newSearchable().addSearchParam("realname_custom", "zhang");
        Assert.assertEquals(count, userRepository2.findAllByCustom(search).getNumberOfElements());
    }

    @Test
    public void testFindAllByPageAndCustomSearch2() {
        int count = 15;
        for (int i = 0; i < count; i++) {
            User user = createUser();
            user.getBaseInfo().setRealname(("zhang" + i));
            save(user);
        }
        Map<String, Object> searchParams = new HashMap<String, Object>();
        searchParams.put("realname_custom", "zhang");
        Searchable search = Searchable.newSearchable(searchParams).setPage(0, 5);
        Assert.assertEquals(5, userRepository2.findAllByCustom(search).getSize());
    }

    @Test
    public void testCountAllByCustomSearch1() {
        int count = 15;
        for (int i = 0; i < count; i++) {
            User user = createUser();
            user.getBaseInfo().setRealname(("zhang" + i));
            save(user);
        }
        Searchable search = Searchable.newSearchable().addSearchParam("realname", "zhang1");
        Assert.assertEquals(6, userRepository2.countAllByCustom(search));
    }

    @Test
    public void testCountAllByCustomSearch2() {
        int count = 15;
        for (int i = 0; i < count; i++) {
            User user = createUser();
            user.getBaseInfo().setRealname(("zhang" + i));
            save(user);
        }
        Searchable search = Searchable.newSearchable().addSearchParam("realname", "zhanga");
        Assert.assertEquals(0, userRepository2.countAllByCustom(search));
    }
}

