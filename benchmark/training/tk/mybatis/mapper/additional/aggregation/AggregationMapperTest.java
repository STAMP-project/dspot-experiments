package tk.mybatis.mapper.additional.aggregation;


import AggregateType.AVG;
import AggregateType.COUNT;
import AggregateType.MAX;
import AggregateType.MIN;
import AggregateType.SUM;
import java.util.List;
import org.apache.ibatis.session.SqlSession;
import org.junit.Assert;
import org.junit.Test;
import tk.mybatis.mapper.additional.BaseTest;
import tk.mybatis.mapper.entity.Example;


public class AggregationMapperTest extends BaseTest {
    @Test
    public void testCount() {
        SqlSession sqlSession = getSqlSession();
        try {
            UserMapper mapper = sqlSession.getMapper(UserMapper.class);
            AggregateCondition aggregateCondition = AggregateCondition.builder().aggregateBy("id").aliasName("total").aggregateType(COUNT).groupBy("role");
            Example example = new Example(User.class);
            List<User> m = mapper.selectAggregationByExample(example, aggregateCondition);
            Assert.assertEquals(2, m.size());
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testAvg() {
        SqlSession sqlSession = getSqlSession();
        try {
            UserMapper mapper = sqlSession.getMapper(UserMapper.class);
            AggregateCondition aggregateCondition = AggregateCondition.builder().aggregateBy("id").aggregateType(AVG);
            Example example = new Example(User.class);
            List<User> m = mapper.selectAggregationByExample(example, aggregateCondition);
            Assert.assertEquals(1, m.size());
            Assert.assertEquals(new Long(3), m.get(0).getId());
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testSum() {
        SqlSession sqlSession = getSqlSession();
        try {
            UserMapper mapper = sqlSession.getMapper(UserMapper.class);
            AggregateCondition aggregateCondition = AggregateCondition.builder().aggregateBy("id").aliasName("aggregation").aggregateType(SUM);
            Example example = new Example(User.class);
            List<User> m = mapper.selectAggregationByExample(example, aggregateCondition);
            Assert.assertEquals(1, m.size());
            Assert.assertEquals(new Long(21), m.get(0).getAggregation());
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testMax() {
        SqlSession sqlSession = getSqlSession();
        try {
            UserMapper mapper = sqlSession.getMapper(UserMapper.class);
            AggregateCondition aggregateCondition = AggregateCondition.builder().aggregateBy("id").aliasName("aggregation").aggregateType(MAX).groupBy("role");
            Example example = new Example(User.class);
            example.setOrderByClause("role desc");
            List<User> m = mapper.selectAggregationByExample(example, aggregateCondition);
            Assert.assertEquals(2, m.size());
            Assert.assertEquals(new Long(6), m.get(0).getAggregation());
            Assert.assertEquals(new Long(3), m.get(1).getAggregation());
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testMin() {
        SqlSession sqlSession = getSqlSession();
        try {
            UserMapper mapper = sqlSession.getMapper(UserMapper.class);
            AggregateCondition aggregateCondition = AggregateCondition.builder().aggregateBy("id").aliasName("aggregation").aggregateType(MIN);
            Example example = new Example(User.class);
            List<User> m = mapper.selectAggregationByExample(example, aggregateCondition);
            Assert.assertEquals(1, m.size());
            Assert.assertEquals(new Long(1), m.get(0).getAggregation());
        } finally {
            sqlSession.close();
        }
    }
}

