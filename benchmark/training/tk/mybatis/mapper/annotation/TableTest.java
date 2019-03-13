package tk.mybatis.mapper.annotation;


import javax.persistence.Table;
import org.junit.Assert;
import org.junit.Test;
import tk.mybatis.mapper.entity.Config;
import tk.mybatis.mapper.entity.EntityTable;
import tk.mybatis.mapper.mapperhelper.EntityHelper;


/**
 *
 *
 * @author liuzh
 */
public class TableTest {
    private Config config;

    @Table(name = "sys_user")
    class User {
        private String name;
    }

    @Test
    public void testColumn() {
        EntityHelper.initEntityNameMap(TableTest.User.class, config);
        EntityTable entityTable = EntityHelper.getEntityTable(TableTest.User.class);
        Assert.assertNotNull(entityTable);
        Assert.assertEquals("sys_user", entityTable.getName());
    }
}

