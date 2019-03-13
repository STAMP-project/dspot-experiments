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


import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.mapper.CountryMapper;
import com.github.pagehelper.model.Country;
import com.github.pagehelper.util.MybatisHelper;
import java.util.List;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;
import org.junit.Assert;
import org.junit.Test;


public class PageHelperTest {
    @Test
    public void shouldGetAllCountries() {
        SqlSession sqlSession = MybatisHelper.getSqlSession();
        try {
            List<Country> list = sqlSession.selectList("selectAll");
            Assert.assertEquals(183, list.size());
        } finally {
            sqlSession.close();
        }
    }

    /**
     * ??Mapper???????????RowBounds???????????xml?????????
     * <p/>
     * RowBounds?????count?????????Page????
     * <p/>
     * ???????????startPage????startPage??
     */
    @Test
    public void testMapperWithRowBounds() {
        SqlSession sqlSession = MybatisHelper.getSqlSession();
        CountryMapper countryMapper = sqlSession.getMapper(CountryMapper.class);
        try {
            // ???1??10??????????count
            List<Country> list = countryMapper.selectAll(new RowBounds(0, 10));
            Assert.assertEquals(10, list.size());
            Assert.assertEquals(183, ((Page<?>) (list)).getTotal());
            // ?????????????
            Assert.assertEquals(1, list.get(0).getId());
            Assert.assertEquals(10, list.get(((list.size()) - 1)).getId());
            // ???2??10??????????count
            list = countryMapper.selectAll(new RowBounds(10, 10));
            Assert.assertEquals(10, list.size());
            Assert.assertEquals(183, ((Page<?>) (list)).getTotal());
            // ?????????????
            Assert.assertEquals(11, list.get(0).getId());
            Assert.assertEquals(20, list.get(((list.size()) - 1)).getId());
            // ???3??20??????????count
            list = countryMapper.selectAll(new RowBounds(60, 20));
            Assert.assertEquals(20, list.size());
            Assert.assertEquals(183, ((Page<?>) (list)).getTotal());
            // ?????????????
            Assert.assertEquals(61, list.get(0).getId());
            Assert.assertEquals(80, list.get(((list.size()) - 1)).getId());
            // ????startPage?RowBounds???startPage??
            PageHelper.startPage(1, 20);
            list = countryMapper.selectAll(new RowBounds(60, 20));
            Assert.assertEquals(20, list.size());
            Assert.assertEquals(183, ((Page<?>) (list)).getTotal());
            // ?????????????
            Assert.assertEquals(1, list.get(0).getId());
            Assert.assertEquals(20, list.get(((list.size()) - 1)).getId());
        } finally {
            sqlSession.close();
        }
    }

    /**
     * ????????????PageHelper.startPage
     * <p/>
     * startPage??????????(true)?(false)??count??????????startPage?????count??
     * <p/>
     * ??startPage??????????RowBounds??startPage??
     */
    @Test
    public void testNamespaceWithStartPage() {
        SqlSession sqlSession = MybatisHelper.getSqlSession();
        try {
            // ???1??10??????????count
            PageHelper.startPage(1, 10);
            List<Country> list = sqlSession.selectList("selectAll");
            Assert.assertEquals(10, list.size());
            Assert.assertEquals(183, ((Page<?>) (list)).getTotal());
            // ???2??10??????????count
            PageHelper.startPage(2, 10, true);
            list = sqlSession.selectList("selectAll");
            Assert.assertEquals(10, list.size());
            Assert.assertEquals(183, ((Page<?>) (list)).getTotal());
            // ???2??10?????????count
            PageHelper.startPage(2, 10, false);
            list = sqlSession.selectList("selectAll");
            Assert.assertEquals(10, list.size());
            Assert.assertEquals((-1), ((Page<?>) (list)).getTotal());
            // ???3??20??????????count
            PageHelper.startPage(3, 20);
            list = sqlSession.selectList("selectAll");
            Assert.assertEquals(20, list.size());
            Assert.assertEquals(183, ((Page<?>) (list)).getTotal());
        } finally {
            sqlSession.close();
        }
    }

    /**
     * ?????????RowBounds???????RowBounds????count??
     * ??????????count???????????????
     * ?????????????????count????????startPage
     * <p/>
     * ????startPage???startPage??????startPage?????
     */
    @Test
    public void testNamespaceWithRowBounds() {
        SqlSession sqlSession = MybatisHelper.getSqlSession();
        try {
            // ???0???10???
            List<Country> list = sqlSession.selectList("selectAll", null, new RowBounds(0, 10));
            Assert.assertEquals(10, list.size());
            Assert.assertEquals(183, ((Page<?>) (list)).getTotal());
            // ?????????????
            Assert.assertEquals(1, list.get(0).getId());
            Assert.assertEquals(10, list.get(((list.size()) - 1)).getId());
            // ???10???10???
            list = sqlSession.selectList("selectAll", null, new RowBounds(10, 10));
            Assert.assertEquals(10, list.size());
            Assert.assertEquals(183, ((Page<?>) (list)).getTotal());
            // ?????????????
            Assert.assertEquals(11, list.get(0).getId());
            Assert.assertEquals(20, list.get(((list.size()) - 1)).getId());
            // ???20???20???
            list = sqlSession.selectList("selectAll", null, new RowBounds(20, 20));
            Assert.assertEquals(20, list.size());
            Assert.assertEquals(183, ((Page<?>) (list)).getTotal());
            // ?????????????
            Assert.assertEquals(21, list.get(0).getId());
            Assert.assertEquals(40, list.get(((list.size()) - 1)).getId());
            // ????startPage?RowBounds???startPage??
            PageHelper.startPage(1, 20);
            list = sqlSession.selectList("selectAll", null, new RowBounds(0, 10));
            Assert.assertEquals(20, list.size());
            Assert.assertEquals(183, ((Page<?>) (list)).getTotal());
            // ?????????????
            Assert.assertEquals(1, list.get(0).getId());
            Assert.assertEquals(20, list.get(((list.size()) - 1)).getId());
        } finally {
            sqlSession.close();
        }
    }
}

