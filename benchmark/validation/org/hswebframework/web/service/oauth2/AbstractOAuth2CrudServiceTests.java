package org.hswebframework.web.service.oauth2;


import com.alibaba.fastjson.JSON;
import java.util.Arrays;
import java.util.List;
import org.hswebframework.web.BusinessException;
import org.hswebframework.web.authorization.oauth2.client.OAuth2RequestService;
import org.hswebframework.web.authorization.oauth2.client.OAuth2SessionBuilder;
import org.hswebframework.web.authorization.oauth2.client.request.OAuth2Session;
import org.hswebframework.web.commons.entity.PagerResult;
import org.hswebframework.web.commons.entity.param.QueryParamEntity;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class AbstractOAuth2CrudServiceTests {
    @Mock
    private OAuth2RequestService requestService;

    @Mock
    private OAuth2SessionBuilder sessionBuilder;

    @Mock
    private OAuth2Session oAuth2Session;

    @InjectMocks
    private TestEntityService testEntityService;

    @Test
    public void testCUD() {
        String id = testEntityService.insert(builder().build());
        Assert.assertNotNull(id);
        TestEntity entity = selectByPk("test");
        Assert.assertNotNull(entity);
        int i = testEntityService.updateByPk("test", entity);
        Assert.assertEquals(i, 1);
        deleteByPk("test");
        Assert.assertEquals(i, 1);
        String saveId = saveOrUpdate(entity);
        Assert.assertNotNull(saveId, id);
        try {
            updateByPk(Arrays.asList(entity));
            Assert.assertTrue(false);
        } catch (BusinessException e) {
            Assert.assertEquals(e.getMessage(), "??????");
        }
    }

    @Test
    public void testQuery() {
        PagerResult<TestEntity> result = testEntityService.selectPager(new QueryParamEntity().where("name", "test"));
        System.out.println(JSON.toJSONString(result));
        TestEntity entity = selectByPk("test");
        Assert.assertNotNull(entity);
        Assert.assertTrue(isBoy());
        Assert.assertEquals(getName(), "test");
        System.out.println(JSON.toJSONString(entity));
        List<TestEntity> all = select();
        Assert.assertNotNull(all);
        List<TestEntity> noPaging = testEntityService.select(QueryParamEntity.empty());
        Assert.assertNotNull(noPaging);
        int total = testEntityService.count(QueryParamEntity.empty());
        Assert.assertEquals(total, 1);
        total = count();
        Assert.assertEquals(total, 1);
    }

    interface OAuth2MethodRequest {
        String getMethod();

        String getResponse();
    }
}

