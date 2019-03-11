package com.baidu.disconf.core.test.utils;


import com.baidu.disconf.core.common.utils.MyStringUtils;
import org.junit.Test;


/**
 * MyStringUtilsTestCase
 *
 * @author knightliao
 */
public class MyStringUtilsTestCase {
    @Test
    public void getRandomName() {
        System.out.println(MyStringUtils.getRandomName("abc.properties"));
    }
}

