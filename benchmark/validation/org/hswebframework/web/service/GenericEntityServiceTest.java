package org.hswebframework.web.service;


import java.util.Arrays;
import java.util.List;
import org.hswebframework.web.commons.entity.PagerResult;
import org.hswebframework.web.commons.entity.param.QueryParamEntity;
import org.hswebframework.web.dao.CrudDao;
import org.hswebframework.web.validate.ValidationException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


/**
 *
 *
 * @author zhouhao
 * @since 3.0
 */
@RunWith(MockitoJUnitRunner.class)
public class GenericEntityServiceTest {
    @InjectMocks
    private TestEntityService entityService = new TestEntityService();

    @Mock
    private CrudDao<TestEntity, String> dao;

    private QueryParamEntity queryParamEntity = new QueryParamEntity();

    @Test
    public void testSimple() {
        Assert.assertEquals(getEntityType(), TestEntity.class);
        Assert.assertEquals(getEntityInstanceType(), TestEntity.class);
        Assert.assertEquals(getPrimaryKeyType(), String.class);
    }

    @Test
    public void testQuery() {
        PagerResult<TestEntity> result = entityService.selectPager(queryParamEntity);
        Assert.assertEquals(result.getTotal(), 1);
        Assert.assertEquals(result.getData().size(), 1);
        TestEntity entity = entityService.selectByPk(getId());
        Assert.assertNotNull(entity);
        List<TestEntity> testEntities = entityService.selectByPk(Arrays.asList(getId()));
        Assert.assertTrue((!(testEntities.isEmpty())));
    }

    @Test
    public void testInsert() {
        TestEntity testEntity = // .name("??")
        builder().age(((byte) (1))).enabled(true).build();
        try {
            insert(testEntity);
            Assert.assertFalse(true);
        } catch (ValidationException e) {
            Assert.assertFalse(e.getResults().isEmpty());
            Assert.assertEquals(e.getResults().get(0).getField(), "name");
            testEntity.setId(null);
        }
        setName("??");
        String id = entityService.insert(testEntity);
        Assert.assertNotNull(id);
    }

    @Test
    public void testUpdate() {
        TestEntity testEntity = builder().age(((byte) (1))).enabled(true).name("??").build();
        setId("testEntity");
        int i = entityService.updateByPk("testEntity", testEntity);
        entityService.updateByPk(testEntity);
        updateByPk(Arrays.asList(testEntity));
        String id = saveOrUpdate(testEntity);
        Assert.assertEquals(id, getId());
        Assert.assertEquals(i, 1);
    }
}

