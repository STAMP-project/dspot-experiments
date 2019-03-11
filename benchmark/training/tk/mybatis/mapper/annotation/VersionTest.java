package tk.mybatis.mapper.annotation;


import java.util.Set;
import org.apache.ibatis.session.Configuration;
import org.junit.Assert;
import org.junit.Test;
import tk.mybatis.mapper.entity.Config;
import tk.mybatis.mapper.entity.EntityColumn;
import tk.mybatis.mapper.entity.EntityTable;
import tk.mybatis.mapper.mapperhelper.EntityHelper;
import tk.mybatis.mapper.mapperhelper.SqlHelper;
import tk.mybatis.mapper.version.VersionException;


/**
 *
 *
 * @author liuzh
 */
public class VersionTest {
    private Config config;

    private Configuration configuration;

    class UserVersion {
        @Version
        private String name;
    }

    @Test
    public void testVersion() {
        EntityHelper.initEntityNameMap(VersionTest.UserVersion.class, config);
        EntityTable entityTable = EntityHelper.getEntityTable(VersionTest.UserVersion.class);
        Assert.assertNotNull(entityTable);
        Set<EntityColumn> columns = entityTable.getEntityClassColumns();
        Assert.assertEquals(1, columns.size());
        for (EntityColumn column : columns) {
            Assert.assertTrue(column.getEntityField().isAnnotationPresent(Version.class));
        }
    }

    /**
     * ??????????? @Version ??
     */
    class UserVersionError {
        @Version
        private Long id;

        @Version
        private String name;
    }

    @Test(expected = VersionException.class)
    public void testVersionError() {
        EntityHelper.initEntityNameMap(VersionTest.UserVersionError.class, config);
        EntityTable entityTable = EntityHelper.getEntityTable(VersionTest.UserVersionError.class);
        Assert.assertNotNull(entityTable);
        SqlHelper.wherePKColumns(VersionTest.UserVersionError.class, true);
    }
}

