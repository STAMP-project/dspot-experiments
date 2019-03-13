/**
 * Copyright (c) 2005-2012 https://github.com/zhangkaitao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.sishuok.es.common.repository;


import com.sishuok.es.common.entity.User;
import com.sishuok.es.common.test.BaseUserIT;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;


/**
 * <p>??DDD Specification?Repository????JpaSpecificationExecutor</p>
 * <p>User: Zhang Kaitao
 * <p>Date: 13-1-14 ??5:02
 * <p>Version: 1.0
 */
public class SpecificationUserRepositoryIT extends BaseUserIT {
    @Autowired
    private UserRepository userRepository;

    private User user;

    @Test
    public void test() {
        save(user);
        Specification<User> spec = new Specification<User>() {
            @Override
            public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                return cb.equal(root.get("id"), getId());
            }
        };
        clear();
        User dbUser = userRepository.findOne(spec);
        Assert.assertNotNull(dbUser);
    }
}

