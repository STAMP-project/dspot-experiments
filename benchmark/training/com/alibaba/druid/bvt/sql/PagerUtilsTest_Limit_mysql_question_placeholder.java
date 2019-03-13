package com.alibaba.druid.bvt.sql;


import junit.framework.TestCase;


public class PagerUtilsTest_Limit_mysql_question_placeholder extends TestCase {
    public void testQuestionLimitPlaceholder1() {
        String sql = "select * from test_table limit ?";
        testQuestionLimitPlaceholderInternal(sql);
    }

    public void testQuestionLimitPlaceholder2() {
        String sql = "select * from test_table limit ?, ?";
        testQuestionLimitPlaceholderInternal(sql);
    }
}

