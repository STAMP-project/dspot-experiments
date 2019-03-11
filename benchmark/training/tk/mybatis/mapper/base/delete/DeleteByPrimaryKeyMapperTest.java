package tk.mybatis.mapper.base.delete;


import org.apache.ibatis.session.SqlSession;
import org.junit.Assert;
import org.junit.Test;
import tk.mybatis.mapper.base.BaseTest;
import tk.mybatis.mapper.base.CountryMapper;


public class DeleteByPrimaryKeyMapperTest extends BaseTest {
    @Test
    public void testDeleteByPrimaryKey() {
        SqlSession sqlSession = getSqlSession();
        try {
            CountryMapper mapper = sqlSession.getMapper(CountryMapper.class);
            Assert.assertEquals(183, selectAll().size());
            Assert.assertEquals(1, mapper.deleteByPrimaryKey(1L));
            Assert.assertEquals(182, selectAll().size());
            Assert.assertEquals(1, deleteByPrimaryKey(2));
            Assert.assertEquals(181, selectAll().size());
            Assert.assertEquals(1, mapper.deleteByPrimaryKey("3"));
            Assert.assertEquals(180, selectAll().size());
            Assert.assertEquals(0, deleteByPrimaryKey(1));
            Assert.assertEquals(180, selectAll().size());
        } finally {
            sqlSession.close();
        }
    }
}

