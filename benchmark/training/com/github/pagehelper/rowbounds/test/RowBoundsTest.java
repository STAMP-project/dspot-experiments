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
package com.github.pagehelper.rowbounds.test;


import com.github.pagehelper.mapper.CountryMapper;
import com.github.pagehelper.model.Country;
import com.github.pagehelper.rowbounds.RowBoundsHelper;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;
import org.junit.Assert;
import org.junit.Test;


public class RowBoundsTest {
    /**
     * ??Mapper???????????RowBounds???????????xml?????????
     * <p/>
     * RowBounds?????count?????????Page????
     * <p/>
     * ???????????startPage????startPage??
     */
    @Test
    public void testMapperWithRowBounds() {
        SqlSession sqlSession = RowBoundsHelper.getSqlSession();
        CountryMapper countryMapper = sqlSession.getMapper(CountryMapper.class);
        try {
            // ???1??10??????????count
            List<Country> list = countryMapper.selectAll(new RowBounds(0, 10));
            // ??PageInfo????????????
            Assert.assertEquals(10, list.size());
            // ?????????????
            Assert.assertEquals(1, list.get(0).getId());
            Assert.assertEquals(10, list.get(((list.size()) - 1)).getId());
            // ???10??10??????????count
            list = countryMapper.selectAll(new RowBounds(90, 10));
            Assert.assertEquals(10, list.size());
            // ?????????????
            Assert.assertEquals(91, list.get(0).getId());
            Assert.assertEquals(100, list.get(((list.size()) - 1)).getId());
            // ???3??20??????????count
            list = countryMapper.selectAll(new RowBounds(100, 20));
            Assert.assertEquals(20, list.size());
            // ?????????????
            Assert.assertEquals(101, list.get(0).getId());
            Assert.assertEquals(120, list.get(((list.size()) - 1)).getId());
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
        SqlSession sqlSession = RowBoundsHelper.getSqlSession();
        try {
            // ???0???10???
            List<Country> list = sqlSession.selectList("selectAll", null, new RowBounds(0, 10));
            Assert.assertEquals(10, list.size());
            // ?????????????
            Assert.assertEquals(1, list.get(0).getId());
            Assert.assertEquals(10, list.get(((list.size()) - 1)).getId());
            // ???10???10???
            list = sqlSession.selectList("selectAll", null, new RowBounds(90, 10));
            Assert.assertEquals(10, list.size());
            // ?????????????
            Assert.assertEquals(91, list.get(0).getId());
            Assert.assertEquals(100, list.get(((list.size()) - 1)).getId());
            // ???20???20???
            list = sqlSession.selectList("selectAll", null, new RowBounds(100, 20));
            Assert.assertEquals(20, list.size());
            // ?????????????
            Assert.assertEquals(101, list.get(0).getId());
            Assert.assertEquals(120, list.get(((list.size()) - 1)).getId());
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testNamespaceWithRowBounds2() {
        SqlSession sqlSession = RowBoundsHelper.getSqlSession();
        try {
            // ???0???10???
            List<Country> list = sqlSession.selectList("selectIf", null, new RowBounds(0, 10));
            Assert.assertEquals(10, list.size());
            // ?????????????
            Assert.assertEquals(1, list.get(0).getId());
            Assert.assertEquals(10, list.get(((list.size()) - 1)).getId());
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("id", 10);
            // ???10???10???
            list = sqlSession.selectList("selectIf", map, new RowBounds(90, 10));
            Assert.assertEquals(10, list.size());
            // ?????????????
            Assert.assertEquals(101, list.get(0).getId());
            Assert.assertEquals(110, list.get(((list.size()) - 1)).getId());
        } finally {
            sqlSession.close();
        }
    }

    /**
     * ??Mapper????????PageHelper.startPage??????????Mapper????
     */
    @Test
    public void testWithRowboundsAndCountTrue() {
        SqlSession sqlSession = RowBoundsHelper.getSqlSession();
        CountryMapper countryMapper = sqlSession.getMapper(CountryMapper.class);
        try {
            // limit=0,????????????count,?????????rounbounds???count?????-1
            // ?????????????-1
            List<Country> list = countryMapper.selectAll(new RowBounds(0, (-1)));
            Assert.assertEquals(183, list.size());
            // pageSize<0?????
            list = countryMapper.selectAll(new RowBounds(0, (-100)));
            Assert.assertEquals(183, list.size());
        } finally {
            sqlSession.close();
        }
    }

    class IdBean {
        private Integer id;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }
    }
}

