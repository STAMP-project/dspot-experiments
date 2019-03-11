/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2017 abel533@gmail.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.github.pagehelper.test.basic;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.PageSerializable;
import com.github.pagehelper.mapper.CountryMapper;
import com.github.pagehelper.model.Country;
import com.github.pagehelper.util.MybatisHelper;
import java.util.List;
import org.apache.ibatis.session.SqlSession;
import org.junit.Assert;
import org.junit.Test;


public class PageInfoTest {
    /**
     * ??Mapper????????PageHelper.startPage??????????Mapper????
     */
    @Test
    public void testPageSize10() {
        SqlSession sqlSession = MybatisHelper.getSqlSession();
        CountryMapper countryMapper = sqlSession.getMapper(CountryMapper.class);
        try {
            // ???1??10??????????count
            PageHelper.startPage(1, 10);
            List<Country> list = countryMapper.selectAll();
            System.out.println(list);
            PageInfo<Country> page = new PageInfo<Country>(list);
            Assert.assertEquals(1, page.getPageNum());
            Assert.assertEquals(10, page.getPageSize());
            Assert.assertEquals(1, page.getStartRow());
            Assert.assertEquals(10, page.getEndRow());
            Assert.assertEquals(183, page.getTotal());
            Assert.assertEquals(19, page.getPages());
            Assert.assertEquals(true, page.isIsFirstPage());
            Assert.assertEquals(false, page.isIsLastPage());
            Assert.assertEquals(false, page.isHasPreviousPage());
            Assert.assertEquals(true, page.isHasNextPage());
            PageSerializable<Country> serializable = PageSerializable.of(list);
            Assert.assertEquals(183, serializable.getTotal());
            // ???2??10??????????count
            PageHelper.startPage(2, 10);
            list = countryMapper.selectAll();
            page = new PageInfo<Country>(list);
            Assert.assertEquals(2, page.getPageNum());
            Assert.assertEquals(10, page.getPageSize());
            Assert.assertEquals(11, page.getStartRow());
            Assert.assertEquals(20, page.getEndRow());
            Assert.assertEquals(183, page.getTotal());
            Assert.assertEquals(19, page.getPages());
            Assert.assertEquals(false, page.isIsFirstPage());
            Assert.assertEquals(false, page.isIsLastPage());
            Assert.assertEquals(true, page.isHasPreviousPage());
            Assert.assertEquals(true, page.isHasNextPage());
            // ???19??10??????????count
            PageHelper.startPage(19, 10);
            list = countryMapper.selectAll();
            page = new PageInfo<Country>(list);
            Assert.assertEquals(19, page.getPageNum());
            Assert.assertEquals(10, page.getPageSize());
            Assert.assertEquals(181, page.getStartRow());
            Assert.assertEquals(183, page.getEndRow());
            Assert.assertEquals(183, page.getTotal());
            Assert.assertEquals(19, page.getPages());
            Assert.assertEquals(false, page.isIsFirstPage());
            Assert.assertEquals(true, page.isIsLastPage());
            Assert.assertEquals(true, page.isHasPreviousPage());
            Assert.assertEquals(false, page.isHasNextPage());
        } finally {
            sqlSession.close();
        }
    }

    /**
     * ??Mapper????????PageHelper.startPage??????????Mapper????
     */
    @Test
    public void testPageSize50() {
        SqlSession sqlSession = MybatisHelper.getSqlSession();
        CountryMapper countryMapper = sqlSession.getMapper(CountryMapper.class);
        try {
            // ???1??50??????????count
            PageHelper.startPage(1, 50);
            List<Country> list = countryMapper.selectAll();
            PageInfo<Country> page = new PageInfo<Country>(list);
            Assert.assertEquals(1, page.getPageNum());
            Assert.assertEquals(50, page.getPageSize());
            Assert.assertEquals(1, page.getStartRow());
            Assert.assertEquals(50, page.getEndRow());
            Assert.assertEquals(183, page.getTotal());
            Assert.assertEquals(4, page.getPages());
            Assert.assertEquals(true, page.isIsFirstPage());
            Assert.assertEquals(false, page.isIsLastPage());
            Assert.assertEquals(false, page.isHasPreviousPage());
            Assert.assertEquals(true, page.isHasNextPage());
            // ???2??50??????????count
            PageHelper.startPage(2, 50);
            list = countryMapper.selectAll();
            page = new PageInfo<Country>(list);
            Assert.assertEquals(2, page.getPageNum());
            Assert.assertEquals(50, page.getPageSize());
            Assert.assertEquals(51, page.getStartRow());
            Assert.assertEquals(100, page.getEndRow());
            Assert.assertEquals(183, page.getTotal());
            Assert.assertEquals(4, page.getPages());
            Assert.assertEquals(false, page.isIsFirstPage());
            Assert.assertEquals(false, page.isIsLastPage());
            Assert.assertEquals(true, page.isHasPreviousPage());
            Assert.assertEquals(true, page.isHasNextPage());
            // ???3??50??????????count
            PageHelper.startPage(3, 50);
            list = countryMapper.selectAll();
            page = new PageInfo<Country>(list);
            Assert.assertEquals(3, page.getPageNum());
            Assert.assertEquals(50, page.getPageSize());
            Assert.assertEquals(101, page.getStartRow());
            Assert.assertEquals(150, page.getEndRow());
            Assert.assertEquals(183, page.getTotal());
            Assert.assertEquals(4, page.getPages());
            Assert.assertEquals(false, page.isIsFirstPage());
            Assert.assertEquals(false, page.isIsLastPage());
            Assert.assertEquals(true, page.isHasPreviousPage());
            Assert.assertEquals(true, page.isHasNextPage());
            // ???4??50??????????count
            PageHelper.startPage(4, 50);
            list = countryMapper.selectAll();
            page = new PageInfo<Country>(list);
            Assert.assertEquals(4, page.getPageNum());
            Assert.assertEquals(50, page.getPageSize());
            Assert.assertEquals(151, page.getStartRow());
            Assert.assertEquals(183, page.getEndRow());
            Assert.assertEquals(183, page.getTotal());
            Assert.assertEquals(4, page.getPages());
            Assert.assertEquals(false, page.isIsFirstPage());
            Assert.assertEquals(true, page.isIsLastPage());
            Assert.assertEquals(true, page.isHasPreviousPage());
            Assert.assertEquals(false, page.isHasNextPage());
        } finally {
            sqlSession.close();
        }
    }

    /**
     * ??Mapper????????PageHelper.startPage??????????Mapper????
     */
    @Test
    public void testNavigatePages() {
        SqlSession sqlSession = MybatisHelper.getSqlSession();
        CountryMapper countryMapper = sqlSession.getMapper(CountryMapper.class);
        try {
            // ???1??10??????????count
            PageHelper.startPage(1, 10);
            List<Country> list = countryMapper.selectAll();
            PageInfo<Country> page = new PageInfo<Country>(list, 20);
            Assert.assertEquals(1, page.getPageNum());
            Assert.assertEquals(10, page.getPageSize());
            Assert.assertEquals(1, page.getStartRow());
            Assert.assertEquals(10, page.getEndRow());
            Assert.assertEquals(183, page.getTotal());
            Assert.assertEquals(19, page.getPages());
            Assert.assertEquals(true, page.isIsFirstPage());
            Assert.assertEquals(false, page.isIsLastPage());
            Assert.assertEquals(false, page.isHasPreviousPage());
            Assert.assertEquals(true, page.isHasNextPage());
            // ???2??50??????????count
            PageHelper.startPage(2, 50);
            list = countryMapper.selectAll();
            page = new PageInfo<Country>(list, 2);
            Assert.assertEquals(2, page.getPageNum());
            Assert.assertEquals(50, page.getPageSize());
            Assert.assertEquals(51, page.getStartRow());
            Assert.assertEquals(100, page.getEndRow());
            Assert.assertEquals(183, page.getTotal());
            Assert.assertEquals(4, page.getPages());
            Assert.assertEquals(false, page.isIsFirstPage());
            Assert.assertEquals(false, page.isIsLastPage());
            Assert.assertEquals(true, page.isHasPreviousPage());
            Assert.assertEquals(true, page.isHasNextPage());
        } finally {
            sqlSession.close();
        }
    }
}

