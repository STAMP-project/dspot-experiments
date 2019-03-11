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
package tk.mybatis.mapper.test.transientc;


import java.util.List;
import org.apache.ibatis.session.SqlSession;
import org.junit.Assert;
import org.junit.Test;
import tk.mybatis.mapper.mapper.CountryTMapper;
import tk.mybatis.mapper.mapper.MybatisHelper;
import tk.mybatis.mapper.model.CountryT;


/**
 * Created by liuzh on 2014/11/21.
 */
public class TestTransient {
    /**
     * ??????
     */
    @Test
    public void testDynamicInsert() {
        SqlSession sqlSession = MybatisHelper.getSqlSession();
        try {
            CountryTMapper mapper = sqlSession.getMapper(CountryTMapper.class);
            CountryT country = new CountryT();
            country.setId(10086);
            country.setCountrycode("CN");
            country.setCountryname("??");
            Assert.assertEquals(1, insert(country));
            // ??CN??
            country = new CountryT();
            country.setCountrycode("CN");
            List<CountryT> list = select(country);
            Assert.assertEquals(2, list.size());
            // ??????null
            Assert.assertNull(list.get(0).getCountrycode());
            // ???????,???????????
            Assert.assertEquals(1, deleteByPrimaryKey(10086));
        } finally {
            sqlSession.close();
        }
    }

    /**
     * ??????????
     */
    @Test
    public void testDynamicUpdateByPrimaryKey() {
        SqlSession sqlSession = MybatisHelper.getSqlSession();
        try {
            CountryTMapper mapper = sqlSession.getMapper(CountryTMapper.class);
            CountryT country = new CountryT();
            country.setId(174);
            country.setCountryname("??");
            country.setCountrycode("US");
            Assert.assertEquals(1, updateByPrimaryKey(country));
            country = selectByPrimaryKey(174);
            Assert.assertNotNull(country);
            Assert.assertEquals(174, ((int) (country.getId())));
            Assert.assertEquals("??", country.getCountryname());
            Assert.assertNull(country.getCountrycode());
        } finally {
            sqlSession.close();
        }
    }

    /**
     * ??????????
     */
    @Test
    public void testDynamicSelect() {
        SqlSession sqlSession = MybatisHelper.getSqlSession();
        try {
            CountryTMapper mapper = sqlSession.getMapper(CountryTMapper.class);
            CountryT country = new CountryT();
            country.setId(174);
            country.setCountrycode("US");
            List<CountryT> countryList = select(country);
            Assert.assertEquals(1, countryList.size());
            Assert.assertEquals(true, ((countryList.get(0).getId()) == 174));
            Assert.assertNotNull(countryList.get(0).getCountryname());
            Assert.assertNull(countryList.get(0).getCountrycode());
        } finally {
            sqlSession.close();
        }
    }
}

