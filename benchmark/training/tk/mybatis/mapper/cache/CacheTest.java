package tk.mybatis.mapper.cache;


import org.apache.ibatis.session.SqlSession;
import org.junit.Assert;
import org.junit.Test;
import tk.mybatis.mapper.base.BaseTest;
import tk.mybatis.mapper.base.Country;
import tk.mybatis.mapper.base.CountryMapper;


/**
 *
 *
 * @author liuzh
 */
public class CacheTest extends BaseTest {
    @Test
    public void testNoCache() {
        SqlSession sqlSession = getSqlSession();
        try {
            CountryMapper mapper = sqlSession.getMapper(CountryMapper.class);
            Country country = selectByPrimaryKey(35);
            Assert.assertEquals("China", country.getCountryname());
            Assert.assertEquals("CN", country.getCountrycode());
            // ?? CountryMapper ?????????????????????????? SqlSession???
            country.setCountryname("??");
            country.setCountrycode("ZH");
        } finally {
            sqlSession.close();
        }
        // ?????? sqlSession
        sqlSession = getSqlSession();
        try {
            CountryMapper mapper = sqlSession.getMapper(CountryMapper.class);
            Country country = selectByPrimaryKey(35);
            Assert.assertEquals("China", country.getCountryname());
            Assert.assertEquals("CN", country.getCountrycode());
            Assert.assertNotEquals("??", country.getCountryname());
            Assert.assertNotEquals("ZH", country.getCountrycode());
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testSingleInterfaceCache() {
        // ???????????????????
        SqlSession sqlSession = getSqlSession();
        try {
            CountryCacheMapper mapper = sqlSession.getMapper(CountryCacheMapper.class);
            Country country = selectByPrimaryKey(35);
            Assert.assertEquals("China", country.getCountryname());
            Assert.assertEquals("CN", country.getCountrycode());
            // ????????????????????????
            country.setCountryname("??");
            country.setCountrycode("ZH");
        } finally {
            sqlSession.close();
        }
        // ?? sqlSession.close() ???????????? sqlSession
        sqlSession = getSqlSession();
        try {
            CountryCacheMapper mapper = sqlSession.getMapper(CountryCacheMapper.class);
            Country country = selectByPrimaryKey(35);
            Assert.assertEquals("??", country.getCountryname());
            Assert.assertEquals("ZH", country.getCountrycode());
        } finally {
            sqlSession.close();
        }
        // ????????
        sqlSession = getSqlSession();
        try {
            CountryCacheMapper mapper = sqlSession.getMapper(CountryCacheMapper.class);
            // ?? update ????
            updateByPrimaryKey(new Country());
            Country country = selectByPrimaryKey(35);
            Assert.assertEquals("China", country.getCountryname());
            Assert.assertEquals("CN", country.getCountrycode());
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testCountryCacheRefMapper() {
        // --------------------selectByPrimaryKey---------------------
        // ???????????????????
        SqlSession sqlSession = getSqlSession();
        try {
            CountryCacheRefMapper mapper = sqlSession.getMapper(CountryCacheRefMapper.class);
            Country country = selectByPrimaryKey(35);
            Assert.assertEquals("China", country.getCountryname());
            Assert.assertEquals("CN", country.getCountrycode());
            // ????????????????????????
            country.setCountryname("??");
            country.setCountrycode("ZH");
        } finally {
            sqlSession.close();
        }
        // ?? sqlSession.close() ???????????? sqlSession
        sqlSession = getSqlSession();
        try {
            CountryCacheRefMapper mapper = sqlSession.getMapper(CountryCacheRefMapper.class);
            Country country = selectByPrimaryKey(35);
            Assert.assertEquals("??", country.getCountryname());
            Assert.assertEquals("ZH", country.getCountrycode());
        } finally {
            sqlSession.close();
        }
        // --------------------selectById---------------------
        sqlSession = getSqlSession();
        try {
            CountryCacheRefMapper mapper = sqlSession.getMapper(CountryCacheRefMapper.class);
            Country country = mapper.selectById(35);
            Assert.assertEquals("China", country.getCountryname());
            Assert.assertEquals("CN", country.getCountrycode());
            // ????????????????????????
            country.setCountryname("??");
            country.setCountrycode("ZH");
        } finally {
            sqlSession.close();
        }
        // ?? sqlSession.close() ???????????? sqlSession
        sqlSession = getSqlSession();
        try {
            CountryCacheRefMapper mapper = sqlSession.getMapper(CountryCacheRefMapper.class);
            Country country = mapper.selectById(35);
            Assert.assertEquals("??", country.getCountryname());
            Assert.assertEquals("ZH", country.getCountrycode());
        } finally {
            sqlSession.close();
        }
        // ????????
        sqlSession = getSqlSession();
        try {
            CountryCacheRefMapper mapper = sqlSession.getMapper(CountryCacheRefMapper.class);
            // ?? update ????
            updateByPrimaryKey(new Country());
            Country country = mapper.selectById(35);
            Assert.assertEquals("China", country.getCountryname());
            Assert.assertEquals("CN", country.getCountrycode());
        } finally {
            sqlSession.close();
        }
    }
}

