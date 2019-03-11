package org.hswebframework.web.authorization.starter;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.Data;
import org.hswebframework.web.authorization.annotation.Authorize;
import org.hswebframework.web.authorization.basic.aop.AopMethodAuthorizeDefinitionParser;
import org.hswebframework.web.authorization.basic.aop.DefaultAopMethodAuthorizeDefinitionParser;
import org.hswebframework.web.authorization.define.AuthorizeDefinition;
import org.hswebframework.web.commons.entity.SimpleGenericEntity;
import org.hswebframework.web.commons.entity.param.QueryParamEntity;
import org.hswebframework.web.controller.SimpleGenericEntityController;
import org.hswebframework.web.entity.authorization.PermissionEntity;
import org.hswebframework.web.service.CrudService;
import org.hswebframework.web.service.authorization.PermissionService;
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
 * @since 1.0
 */
@RunWith(MockitoJUnitRunner.class)
public class AutoSyncPermissionTest {
    @Mock
    private PermissionService permissionService;

    @InjectMocks
    private AutoSyncPermission autoSyncPermission = new AutoSyncPermission();

    private List<PermissionEntity> newPermissionEntity = new ArrayList<>();

    @Test
    public void test() throws NoSuchMethodException {
        AopMethodAuthorizeDefinitionParser parser = new DefaultAopMethodAuthorizeDefinitionParser();
        List<AuthorizeDefinition> definition = Arrays.stream(AutoSyncPermissionTest.TestController.class.getMethods()).map(( method) -> parser.parse(AutoSyncPermissionTest.TestController.class, method)).filter(Objects::nonNull).collect(Collectors.toList());
        autoSyncPermission.onApplicationEvent(new org.hswebframework.web.authorization.define.AuthorizeDefinitionInitializedEvent(definition));
        Assert.assertTrue((!(newPermissionEntity.isEmpty())));
        PermissionEntity permissionEntity = newPermissionEntity.get(0);
        Assert.assertEquals(permissionEntity.getId(), "test");
        Assert.assertEquals(permissionEntity.getName(), "????");
        Assert.assertTrue((!(permissionEntity.getActions().isEmpty())));
        Assert.assertEquals(permissionEntity.getOptionalFields().size(), 3);
    }

    @Authorize(permission = "test", description = "????")
    @Api(value = "??", tags = "??")
    public static class TestController implements SimpleGenericEntityController<AutoSyncPermissionTest.TestEntity, String, QueryParamEntity> {
        @Override
        public CrudService<AutoSyncPermissionTest.TestEntity, String> getService() {
            return null;
        }
    }

    @Data
    public static class TestEntity extends SimpleGenericEntity<String> {
        @ApiModelProperty("??")
        private String name;

        @ApiModelProperty("???")
        private String username;

        @ApiModelProperty(value = "??", hidden = true)
        private String password;
    }
}

