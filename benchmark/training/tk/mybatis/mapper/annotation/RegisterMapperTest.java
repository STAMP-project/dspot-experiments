package tk.mybatis.mapper.annotation;


import org.apache.ibatis.session.Configuration;
import org.junit.Assert;
import org.junit.Test;
import tk.mybatis.mapper.entity.Config;
import tk.mybatis.mapper.mapperhelper.MapperHelper;


/**
 *
 *
 * @author liuzh
 */
public class RegisterMapperTest {
    private Config config;

    private Configuration configuration;

    @RegisterMapper
    interface MapperHashRegisterMapper {}

    interface UserMapper extends RegisterMapperTest.MapperHashRegisterMapper {}

    @Test
    public void testHashRegisterMapper() {
        MapperHelper mapperHelper = new MapperHelper();
        Assert.assertTrue(mapperHelper.isExtendCommonMapper(RegisterMapperTest.UserMapper.class));
    }

    interface RoleMapper {}

    @Test
    public void testRoleMapper() {
        MapperHelper mapperHelper = new MapperHelper();
        Assert.assertFalse(mapperHelper.isExtendCommonMapper(RegisterMapperTest.RoleMapper.class));
    }

    @RegisterMapper
    interface RoleMapper2 {}

    @Test
    public void testRoleMapper2() {
        MapperHelper mapperHelper = new MapperHelper();
        Assert.assertFalse(mapperHelper.isExtendCommonMapper(RegisterMapperTest.RoleMapper2.class));
    }
}

