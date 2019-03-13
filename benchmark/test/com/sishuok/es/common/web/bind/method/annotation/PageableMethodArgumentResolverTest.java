/**
 * Copyright (c) 2005-2012 https://github.com/zhangkaitao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.sishuok.es.common.web.bind.method.annotation;


import Sort.Direction;
import com.sishuok.es.common.web.bind.annotation.PageableDefaults;
import java.lang.reflect.Method;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.NativeWebRequest;


/**
 * <p>User: Zhang Kaitao
 * <p>Date: 13-1-23 ??3:42
 * <p>Version: 1.0
 */
public class PageableMethodArgumentResolverTest {
    Method pageable;

    Method pageableAndSort;

    Method methodDefaultPageable;

    Method parameterDefaultPageable;

    Method customNamePrefixPageableAndSort;

    MockHttpServletRequest request;

    @Test
    public void testPageable() throws Exception {
        int pn = 1;
        int pageSize = 10;
        request.setParameter("page.pn", String.valueOf(pn));
        request.setParameter("page.size", String.valueOf(pageSize));
        MethodParameter parameter = new MethodParameter(pageable, 0);
        NativeWebRequest webRequest = new org.springframework.web.context.request.ServletWebRequest(request);
        Pageable pageable = ((Pageable) (new PageableMethodArgumentResolver().resolveArgument(parameter, null, webRequest, null)));
        // ?????-1??0??
        Assert.assertEquals((pn - 1), pageable.getPageNumber());
        Assert.assertEquals(pageSize, pageable.getPageSize());
        Assert.assertEquals(null, pageable.getSort());
    }

    @Test
    public void testPageableAndUnOrderedSort() throws Exception {
        int pn = 1;
        int pageSize = 10;
        request.setParameter("page.pn", String.valueOf(pn));
        request.setParameter("page.size", String.valueOf(pageSize));
        request.setParameter("sort.id", "desc");
        request.setParameter("sort.baseInfo.realname", "asc");
        MethodParameter parameter = new MethodParameter(pageableAndSort, 0);
        NativeWebRequest webRequest = new org.springframework.web.context.request.ServletWebRequest(request);
        Pageable pageable = ((Pageable) (new PageableMethodArgumentResolver().resolveArgument(parameter, null, webRequest, null)));
        // ?????-1??0??
        Assert.assertEquals((pn - 1), pageable.getPageNumber());
        Assert.assertEquals(pageSize, pageable.getPageSize());
        Sort expectedSort = and(new Sort(Direction.DESC, "id"));
        Assert.assertEquals(expectedSort, pageable.getSort());
    }

    @Test
    public void testPageableAndOrderedSort() throws Exception {
        int pn = 1;
        int pageSize = 10;
        request.setParameter("page.pn", String.valueOf(pn));
        request.setParameter("page.size", String.valueOf(pageSize));
        request.setParameter("sort1.baseInfo.realname", "asc");
        request.setParameter("sort2.id", "desc");
        MethodParameter parameter = new MethodParameter(pageableAndSort, 0);
        NativeWebRequest webRequest = new org.springframework.web.context.request.ServletWebRequest(request);
        Pageable pageable = ((Pageable) (new PageableMethodArgumentResolver().resolveArgument(parameter, null, webRequest, null)));
        // ?????-1??0??
        Assert.assertEquals((pn - 1), pageable.getPageNumber());
        Assert.assertEquals(pageSize, pageable.getPageSize());
        Sort expectedSort = and(new Sort(Direction.DESC, "id"));
        Assert.assertEquals(expectedSort, pageable.getSort());
    }

    @Test
    public void testMethodDefaultPageable() throws Exception {
        MethodParameter parameter = new MethodParameter(methodDefaultPageable, 0);
        NativeWebRequest webRequest = new org.springframework.web.context.request.ServletWebRequest(request);
        Pageable pageable = ((Pageable) (new PageableMethodArgumentResolver().resolveArgument(parameter, null, webRequest, null)));
        Assert.assertEquals(PageableMethodArgumentResolverTest.Controller.DEFAULT_PAGENUMBER, pageable.getPageNumber());
        Assert.assertEquals(PageableMethodArgumentResolverTest.Controller.DEFAULT_PAGESIZE, pageable.getPageSize());
        Sort expectedSort = and(new Sort(Direction.ASC, "name"));
        Assert.assertEquals(expectedSort, pageable.getSort());
    }

    @Test
    public void testParameterDefaultPageable() throws Exception {
        MethodParameter parameter = new MethodParameter(parameterDefaultPageable, 0);
        NativeWebRequest webRequest = new org.springframework.web.context.request.ServletWebRequest(request);
        Pageable pageable = ((Pageable) (new PageableMethodArgumentResolver().resolveArgument(parameter, null, webRequest, null)));
        Assert.assertEquals(PageableMethodArgumentResolverTest.Controller.DEFAULT_PAGENUMBER, pageable.getPageNumber());
        Assert.assertEquals(PageableMethodArgumentResolverTest.Controller.DEFAULT_PAGESIZE, pageable.getPageSize());
        Sort expectedSort = and(new Sort(Direction.ASC, "name"));
        Assert.assertEquals(expectedSort, pageable.getSort());
    }

    @Test
    public void testParameterDefaultPageableAndOverrideSort() throws Exception {
        request.setParameter("sort2.id", "desc");
        request.setParameter("sort1.baseInfo.realname", "asc");
        MethodParameter parameter = new MethodParameter(parameterDefaultPageable, 0);
        NativeWebRequest webRequest = new org.springframework.web.context.request.ServletWebRequest(request);
        Pageable pageable = ((Pageable) (new PageableMethodArgumentResolver().resolveArgument(parameter, null, webRequest, null)));
        Assert.assertEquals(PageableMethodArgumentResolverTest.Controller.DEFAULT_PAGENUMBER, pageable.getPageNumber());
        Assert.assertEquals(PageableMethodArgumentResolverTest.Controller.DEFAULT_PAGESIZE, pageable.getPageSize());
        Sort expectedSort = and(new Sort(Direction.DESC, "id"));
        Assert.assertEquals(expectedSort, pageable.getSort());
    }

    @Test
    public void testCustomNamePrefixPageableAndSort() throws Exception {
        int pn = 1;
        int pageSize = 10;
        request.setParameter("foo_page.pn", String.valueOf(pn));
        request.setParameter("foo_page.size", String.valueOf(pageSize));
        request.setParameter("foo_sort2.id", "desc");
        request.setParameter("foo_sort1.baseInfo.realname", "asc");
        MethodParameter parameter = new MethodParameter(customNamePrefixPageableAndSort, 0);
        NativeWebRequest webRequest = new org.springframework.web.context.request.ServletWebRequest(request);
        Pageable pageable = ((Pageable) (new PageableMethodArgumentResolver().resolveArgument(parameter, null, webRequest, null)));
        // ?????-1??0??
        Assert.assertEquals((pn - 1), pageable.getPageNumber());
        Assert.assertEquals(pageSize, pageable.getPageSize());
        Sort expectedSort = and(new Sort(Direction.DESC, "id"));
        Assert.assertEquals(expectedSort, pageable.getSort());
    }

    @Test(expected = IllegalStateException.class)
    public void testErrorSortProperty() throws Exception {
        int pn = 1;
        int pageSize = 10;
        request.setParameter("foo_page.pn", String.valueOf(pn));
        request.setParameter("foo_page.size", String.valueOf(pageSize));
        request.setParameter("foo_sort2.id$", "desc");
        request.setParameter("foo_sort1.baseInfo.realname", "asc");
        MethodParameter parameter = new MethodParameter(customNamePrefixPageableAndSort, 0);
        NativeWebRequest webRequest = new org.springframework.web.context.request.ServletWebRequest(request);
        Pageable pageable = ((Pageable) (new PageableMethodArgumentResolver().resolveArgument(parameter, null, webRequest, null)));
    }

    static class Controller {
        static final int DEFAULT_PAGESIZE = 198;

        static final int DEFAULT_PAGENUMBER = 42;

        public void pageable(Pageable pageable) {
        }

        public void pageableAndSort(Pageable pageable) {
        }

        @PageableDefaults(value = PageableMethodArgumentResolverTest.Controller.DEFAULT_PAGESIZE, pageNumber = PageableMethodArgumentResolverTest.Controller.DEFAULT_PAGENUMBER, sort = { "id=desc", "name=asc" })
        public void methodDefaultPageable(Pageable pageable) {
        }

        public void parameterDefaultPageable(@PageableDefaults(value = PageableMethodArgumentResolverTest.Controller.DEFAULT_PAGESIZE, pageNumber = PageableMethodArgumentResolverTest.Controller.DEFAULT_PAGENUMBER, sort = { "id=desc", "name=asc" })
        Pageable pageable) {
        }

        public void customNamePrefixPageableAndSort(@Qualifier("foo")
        Pageable foo, @Qualifier("test")
        Pageable test) {
        }
    }
}

