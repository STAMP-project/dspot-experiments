package com.alibaba.druid.bvt.sql.mysql.param;


import com.alibaba.druid.sql.parser.SQLSelectListCache;
import com.alibaba.druid.sql.visitor.ParameterizedOutputVisitorUtils;
import com.alibaba.druid.util.FnvHash;
import com.alibaba.druid.util.JdbcConstants;
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;


public class MySqlParamTest extends TestCase {
    public static final String dbType = JdbcConstants.MYSQL;

    public void test_for_mysql_param() throws Exception {
        String sql = "/* 0b853c4a26094480140194289e3d24/0.1.1.2.1//2e3b9cf7/ */select `miller_cart`.`CART_ID`,`miller_cart`.`SKU_ID`,`miller_cart`.`ITEM_ID`,`miller_cart`.`QUANTITY`,`miller_cart`.`USER_ID`,`miller_cart`.`SELLER_ID`,`miller_cart`.`STATUS`,`miller_cart`.`EXT_STATUS`,`miller_cart`.`TYPE`,`miller_cart`.`SUB_TYPE`,`miller_cart`.`GMT_CREATE`,`miller_cart`.`GMT_MODIFIED`,`miller_cart`.`ATTRIBUTE`,`miller_cart`.`ATTRIBUTE_CC`,`miller_cart`.`EX2` from `miller_cart_0304` `miller_cart` where ((`miller_cart`.`USER_ID` = 2732851504) AND ((`miller_cart`.`STATUS` = 1) AND (`miller_cart`.`TYPE` IN (0,5,10)))) limit 0,200";
        long hash1 = ParameterizedOutputVisitorUtils.parameterizeHash(sql, MySqlParamTest.dbType, null, null);
        long hash2 = FnvHash.fnv1a_64_lower(ParameterizedOutputVisitorUtils.parameterize(sql, MySqlParamTest.dbType));
        TestCase.assertEquals(hash1, hash2);
        SQLSelectListCache cache = new SQLSelectListCache(MySqlParamTest.dbType);
        cache.add("select `miller_cart`.`CART_ID`,`miller_cart`.`SKU_ID`,`miller_cart`.`ITEM_ID`,`miller_cart`.`QUANTITY`,`miller_cart`.`USER_ID`,`miller_cart`.`SELLER_ID`,`miller_cart`.`STATUS`,`miller_cart`.`EXT_STATUS`,`miller_cart`.`TYPE`,`miller_cart`.`SUB_TYPE`,`miller_cart`.`GMT_CREATE`,`miller_cart`.`GMT_MODIFIED`,`miller_cart`.`ATTRIBUTE`,`miller_cart`.`ATTRIBUTE_CC`,`miller_cart`.`EX2` from");
        List<Object> outParameters = new ArrayList<Object>();// ???????null??????????

        long hash3 = ParameterizedOutputVisitorUtils.parameterizeHash(sql, MySqlParamTest.dbType, cache, outParameters);
        TestCase.assertEquals(hash1, hash3);
    }
}

