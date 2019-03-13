package com.alibaba.druid.benckmark;


import com.alibaba.druid.util.FnvHash;
import junit.framework.TestCase;


public class FnvHashTest extends TestCase {
    static String sql = "SELECT id, item_id, rule_id, tag_id, ext , gmt_create, gmt_modified FROM wukong_preview_item_tag WHERE item_id = ? AND rule_id = ?";

    static char[] chars = FnvHashTest.sql.toCharArray();

    public void test_perf_fnv() throws Exception {
        for (int i = 0; i < 5; ++i) {
            // perf_hashCode64(sql); // 168
            perf_hashCode64(FnvHashTest.chars);// 169

        }
    }

    public void test_fnv_hash_1a() throws Exception {
        TestCase.assertEquals(FnvHash.fnv1a_64("bcd"), FnvHash.fnv1a_64("abcde", 1, 4));
    }
}

