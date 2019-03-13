package cn.myperf4j.base.test;


import cn.myperf4j.base.util.DateFormatUtils;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by LinShunkang on 2018/10/17
 */
public class DateFormatUtilsTest {
    @Test
    public void test() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long mills = System.currentTimeMillis();
        Assert.assertEquals(DateFormatUtils.format(mills), format.format(new Date(mills)));
    }
}

