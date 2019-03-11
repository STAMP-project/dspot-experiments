package org.hswebframework.web.dao.crud;


import java.util.Arrays;
import java.util.List;
import org.apache.ibatis.session.SqlSessionFactory;
import org.hswebframework.ezorm.rdb.executor.SqlExecutor;
import org.hswebframework.web.commons.entity.param.DeleteParamEntity;
import org.hswebframework.web.commons.entity.param.QueryParamEntity;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestApplication.class)
public class TestCrud extends AbstractTransactionalJUnit4SpringContextTests {
    @Autowired
    private TestDao testDao;

    @Autowired
    private SqlExecutor sqlExecutor;

    @Autowired
    @Qualifier("sqlSessionFactory2")
    SqlSessionFactory sqlSessionFactory2;

    @Autowired
    @Qualifier("sqlSessionFactory")
    SqlSessionFactory sqlSessionFactory;

    @Test
    public void testInsert() {
        TestEntity entity = new TestEntity();
        setName("??");
        setDataType(DataType.TYPE1);
        setDataTypes(new DataType[]{ DataType.TYPE1, DataType.TYPE3 });
        insert(entity);
        Assert.assertNotNull(getId());
        QueryParamEntity query = new QueryParamEntity();
        // any in
        query.where("dataTypes$in$any", Arrays.asList(DataType.TYPE1, DataType.TYPE2));
        // #102
        query.where("createTime", "2017-11-10");
        query.includes("nest.name", "*");
        // DataSourceHolder.tableSwitcher().use("h_test", "h_test2");
        List<TestEntity> entities = testDao.queryNest(query);
        // testDao.query(entity);
        DeleteParamEntity.newDelete().where("id", "1234").exec(testDao::delete);
        System.out.println(entities);
    }
}

