package tk.mybatis.mapper.mapperhelper;


import org.junit.Assert;
import org.junit.Test;
import tk.mybatis.mapper.entity.Config;


public class SqlHelperTest {
    private Config config;

    @Test
    public void testLogicDeleteSql() {
        String wherePKColumns = SqlHelper.wherePKColumns(User.class);
        Assert.assertEquals("<where> AND id = #{id} AND is_valid = 1</where>", wherePKColumns);
        String whereAllIfColumns = SqlHelper.whereAllIfColumns(User.class, false);
        Assert.assertEquals("<where><if test=\"id != null\"> AND id = #{id}</if><if test=\"username != null\"> AND username = #{username}</if> AND is_valid = 1</where>", whereAllIfColumns);
        String isLogicDeletedColumn = SqlHelper.whereLogicDelete(User.class, true);
        Assert.assertEquals(" AND is_valid = 0", isLogicDeletedColumn);
        String notLogicDeletedColumn = SqlHelper.whereLogicDelete(User.class, false);
        Assert.assertEquals(" AND is_valid = 1", notLogicDeletedColumn);
        String updateSetColumns = SqlHelper.updateSetColumns(User.class, null, false, false);
        Assert.assertEquals("<set>username = #{username},is_valid = 1,</set>", updateSetColumns);
    }
}

