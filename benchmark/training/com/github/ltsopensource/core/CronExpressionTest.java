package com.github.ltsopensource.core;


import com.github.ltsopensource.core.support.CronExpression;
import java.text.ParseException;
import java.util.Date;
import org.junit.Test;


/**
 *
 *
 * @author Robert HG (254963746@qq.com) on 3/3/15.
 */
public class CronExpressionTest {
    @Test
    public void test1() throws ParseException {
        CronExpression cronExpression = new CronExpression("59 23 * * *");
        exec(cronExpression, new Date());
    }
}

