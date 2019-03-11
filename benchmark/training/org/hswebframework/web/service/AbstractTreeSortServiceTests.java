package org.hswebframework.web.service;


import com.alibaba.fastjson.JSON;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.hswebframework.web.dao.CrudDao;
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
public class AbstractTreeSortServiceTests {
    @InjectMocks
    private TestTreeEntityService entityService = new TestTreeEntityService();

    @Mock
    private CrudDao<TestEntity, String> dao;

    private List<TestEntity> entities = new ArrayList<>();

    @Test
    public void testBatchInsert() {
        String treeJson = "{'id':'1','parentId':'-1','name':'???','children':[" + (("{'id':'101','parentId':'1','name':'???1'}," + "{'id':'102','parentId':'1','name':'???2'}") + "]}");
        TestEntity entity = JSON.parseObject(treeJson, TestEntity.class);
        insert(entity);
        Assert.assertEquals(entities.size(), 3);
        entities.clear();
        updateByPk(entity);
        Assert.assertEquals(entities.size(), 3);
        entities.clear();
        updateBatch(Arrays.asList(entity));
        Assert.assertEquals(entities.size(), 3);
    }
}

