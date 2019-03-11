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
package tk.mybatis.mapper.test.identity;


import java.util.List;
import org.apache.ibatis.session.SqlSession;
import org.junit.Assert;
import org.junit.Test;
import tk.mybatis.mapper.mapper.CountryIMapper;
import tk.mybatis.mapper.mapper.MybatisHelper;
import tk.mybatis.mapper.model.CountryI;


/**
 * Created by liuzh on 2014/11/21.
 */
public class TestIndentity {
    /**
     * ??????
     */
    @Test
    public void testINDENTITYInsert() {
        SqlSession sqlSession = MybatisHelper.getSqlSession();
        try {
            CountryIMapper mapper = sqlSession.getMapper(CountryIMapper.class);
            CountryI country = new CountryI();
            country.setCountrycode("CN");
            Assert.assertEquals(1, insert(country));
            // ID???
            Assert.assertNotNull(country.getId());
            // ???????,???????????
            Assert.assertEquals(1, mapper.deleteByPrimaryKey(country.getId()));
        } finally {
            sqlSession.close();
        }
    }

    /**
     * ??????
     */
    @Test
    public void testINDENTITYInsert2() {
        SqlSession sqlSession = MybatisHelper.getSqlSession();
        try {
            CountryIMapper mapper = sqlSession.getMapper(CountryIMapper.class);
            CountryI country = new CountryI();
            country.setId(10086);
            country.setCountrycode("CN");
            country.setCountryname("??");
            Assert.assertEquals(1, insert(country));
            // ??CN??
            country = new CountryI();
            country.setCountrycode("CN");
            List<CountryI> list = select(country);
            Assert.assertEquals(1, list.size());
            Assert.assertNotNull(list.get(0).getCountryname());
            Assert.assertEquals("??", list.get(0).getCountryname());
            // ???????,???????????
            Assert.assertEquals(1, deleteByPrimaryKey(10086));
        } finally {
            sqlSession.close();
        }
    }

    /**
     * ??????
     */
    @Test
    public void testINDENTITYInsertSelective() {
        SqlSession sqlSession = MybatisHelper.getSqlSession();
        try {
            CountryIMapper mapper = sqlSession.getMapper(CountryIMapper.class);
            CountryI country = new CountryI();
            Assert.assertEquals(1, insertSelective(country));
            // ID???
            Assert.assertNotNull(country.getId());
            // ?????????????????,??????
            country = selectByPrimaryKey(country);
            // ???,?????null
            Assert.assertNotNull(country.getCountrycode());
            Assert.assertEquals("HH", country.getCountrycode());
            // ???????,???????????
            Assert.assertEquals(1, mapper.deleteByPrimaryKey(country.getId()));
        } finally {
            sqlSession.close();
        }
    }

    /**
     * ??????
     */
    @Test
    public void testINDENTITYInsertSelective2() {
        SqlSession sqlSession = MybatisHelper.getSqlSession();
        try {
            CountryIMapper mapper = sqlSession.getMapper(CountryIMapper.class);
            CountryI country = new CountryI();
            country.setId(10086);
            country.setCountrycode("CN");
            country.setCountryname("??");
            Assert.assertEquals(1, insertSelective(country));
            // ??CN??
            country = new CountryI();
            country.setCountrycode("CN");
            List<CountryI> list = select(country);
            Assert.assertEquals(1, list.size());
            Assert.assertNotNull(list.get(0).getCountryname());
            Assert.assertEquals("??", list.get(0).getCountryname());
            // ???????,???????????
            Assert.assertEquals(1, deleteByPrimaryKey(10086));
        } finally {
            sqlSession.close();
        }
    }
}

