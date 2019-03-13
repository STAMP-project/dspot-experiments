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
package com.github.pagehelper.test.reasonable;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.mapper.CountryMapper;
import com.github.pagehelper.model.Country;
import com.github.pagehelper.util.MybatisReasonableHelper;
import java.util.List;
import org.apache.ibatis.session.SqlSession;
import org.junit.Assert;
import org.junit.Test;


public class PageTest {
    /**
     * ??Mapper????????PageHelper.startPage??????????Mapper????
     */
    @Test
    public void testMapperWithStartPage() {
        SqlSession sqlSession = MybatisReasonableHelper.getSqlSession();
        CountryMapper countryMapper = sqlSession.getMapper(CountryMapper.class);
        try {
            // ???20??2???
            // ???????????????
            PageHelper.startPage(20, 50);
            List<Country> list = countryMapper.selectAll();
            PageInfo<Country> page = new PageInfo<Country>(list);
            Assert.assertEquals(33, list.size());
            Assert.assertEquals(151, page.getStartRow());
            Assert.assertEquals(4, page.getPageNum());
            Assert.assertEquals(183, page.getTotal());
            // ???-3??2???
            // ????7???????????????????
            PageHelper.startPage((-3), 50);
            list = countryMapper.selectAll();
            page = new PageInfo<Country>(list);
            Assert.assertEquals(50, list.size());
            Assert.assertEquals(1, page.getStartRow());
            Assert.assertEquals(1, page.getPageNum());
            Assert.assertEquals(183, page.getTotal());
        } finally {
            sqlSession.close();
        }
    }
}

