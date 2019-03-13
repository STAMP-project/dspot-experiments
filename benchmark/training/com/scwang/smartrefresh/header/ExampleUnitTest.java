package com.scwang.smartrefresh.header;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import org.junit.Assert;
import org.junit.Test;


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        System.out.println(new SimpleDateFormat("'Last update' M-d HH:mm", Locale.CHINA).format(new Date()));
        Assert.assertEquals(4, (2 + 2));
    }
}

