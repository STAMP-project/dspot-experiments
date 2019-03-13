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
package tk.mybatis.mapper.test.country;


import java.util.List;
import org.apache.ibatis.session.SqlSession;
import org.junit.Assert;
import org.junit.Test;
import tk.mybatis.mapper.mapper.CountryMapper;
import tk.mybatis.mapper.mapper.MybatisHelper;
import tk.mybatis.mapper.model.Country;


/**
 * ???????????
 *
 * @author liuzh
 */
public class TestSelect {
    /**
     * ????
     */
    @Test
    public void testDynamicSelectAll() {
        SqlSession sqlSession = MybatisHelper.getSqlSession();
        try {
            CountryMapper mapper = sqlSession.getMapper(CountryMapper.class);
            Country country = new Country();
            List<Country> countryList;
            // country.setDynamicTableName123("country_123");
            // countryList = mapper.select(country);
            // ????
            // Assert.assertEquals(2, countryList.size());
            country.setDynamicTableName123(null);
            countryList = mapper.select(country);
            // ????
            Assert.assertEquals(183, countryList.size());
        } finally {
            sqlSession.close();
        }
    }

    /**
     * ????
     */
    @Test
    public void testDynamicSelectPage() {
        SqlSession sqlSession = MybatisHelper.getSqlSession();
        try {
            CountryMapper mapper = sqlSession.getMapper(CountryMapper.class);
            Country country = new Country();
            country.setCountrycode("US");
            List<Country> countryList = mapper.selectPage(country, 0, 10);
            // ????
            Assert.assertEquals(1, countryList.size());
            countryList = mapper.selectPage(null, 100, 10);
            // ????
            Assert.assertEquals(10, countryList.size());
        } finally {
            sqlSession.close();
        }
    }

    /**
     * ????
     */
    @Test
    public void testAllColumns() {
        SqlSession sqlSession = MybatisHelper.getSqlSession();
        try {
            CountryMapper mapper = sqlSession.getMapper(CountryMapper.class);
            Country country = new Country();
            // 35,'China','CN'
            country.setCountrycode("CN");
            country.setId(35);
            country.setCountryname("China");
            List<Country> countryList = mapper.select(country);
            Assert.assertEquals(1, countryList.size());
        } finally {
            sqlSession.close();
        }
    }

    /**
     * ???null?????
     */
    @Test
    public void testDynamicSelectAllByNull() {
        SqlSession sqlSession = MybatisHelper.getSqlSession();
        try {
            CountryMapper mapper = sqlSession.getMapper(CountryMapper.class);
            mapper.select(null);
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
            CountryMapper mapper = sqlSession.getMapper(CountryMapper.class);
            Country country = new Country();
            country.setCountrycode("CN");
            List<Country> countryList = mapper.select(country);
            Assert.assertEquals(1, countryList.size());
            Assert.assertEquals(true, ((countryList.get(0).getId()) == 35));
            Assert.assertEquals("China", countryList.get(0).getCountryname());
        } finally {
            sqlSession.close();
        }
    }

    /**
     * ????????
     */
    @Test
    public void testDynamicSelectZero() {
        SqlSession sqlSession = MybatisHelper.getSqlSession();
        try {
            CountryMapper mapper = sqlSession.getMapper(CountryMapper.class);
            Country country = new Country();
            country.setCountrycode("CN");
            country.setCountryname("??");// ???? China

            List<Country> countryList = mapper.select(country);
            Assert.assertEquals(0, countryList.size());
        } finally {
            sqlSession.close();
        }
    }

    /**
     * ???????,?????????
     */
    @Test
    public void testDynamicSelectNotFoundKeyProperties() {
        SqlSession sqlSession = MybatisHelper.getSqlSession();
        try {
            CountryMapper mapper = sqlSession.getMapper(CountryMapper.class);
            // ??????
            Assert.assertEquals(183, select(new TestSelect.Key()).size());
            TestSelect.Key key = new TestSelect.Key();
            key.setCountrycode("CN");
            key.setCountrytel("+86");
            Assert.assertEquals(1, select(key).size());
        } finally {
            sqlSession.close();
        }
    }

    class Key extends Country {
        private String countrytel;

        public String getCountrytel() {
            return countrytel;
        }

        public void setCountrytel(String countrytel) {
            this.countrytel = countrytel;
        }
    }
}

