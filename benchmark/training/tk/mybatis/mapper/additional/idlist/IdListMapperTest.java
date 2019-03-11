package tk.mybatis.mapper.additional.idlist;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.ibatis.session.SqlSession;
import org.junit.Assert;
import org.junit.Test;
import tk.mybatis.mapper.additional.BaseTest;
import tk.mybatis.mapper.additional.Country;


public class IdListMapperTest extends BaseTest {
    @Test
    public void testByIdList() {
        SqlSession sqlSession = getSqlSession();
        try {
            CountryMapper mapper = sqlSession.getMapper(CountryMapper.class);
            List<Long> idList = Arrays.asList(1L, 2L, 3L);
            List<Country> countryList = mapper.selectByIdList(idList);
            Assert.assertEquals(3, countryList.size());
            Assert.assertEquals(1L, ((long) (countryList.get(0).getId())));
            Assert.assertEquals(2L, ((long) (countryList.get(1).getId())));
            Assert.assertEquals(3L, ((long) (countryList.get(2).getId())));
            // ??
            Assert.assertEquals(3, mapper.deleteByIdList(idList));
            // ????0
            Assert.assertEquals(0, mapper.selectByIdList(idList).size());
        } finally {
            sqlSession.close();
        }
    }

    @Test(expected = Exception.class)
    public void testDeleteByEmptyIdList() {
        SqlSession sqlSession = getSqlSession();
        try {
            CountryMapper mapper = sqlSession.getMapper(CountryMapper.class);
            deleteByIdList(new ArrayList<Long>());
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testSelectByEmptyIdList() {
        SqlSession sqlSession = getSqlSession();
        try {
            CountryMapper mapper = sqlSession.getMapper(CountryMapper.class);
            Assert.assertEquals(183, selectByIdList(new ArrayList<Long>()).size());
        } finally {
            sqlSession.close();
        }
    }
}

