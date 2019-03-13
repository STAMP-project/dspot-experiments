package cn.hutool.core.convert;


import org.junit.Assert;
import org.junit.Test;


/**
 * ??????????
 * ??????
 *
 * @author Looly
 */
public class ConvertToSBCAndDBCTest {
    @Test
    public void toSBCTest() {
        String a = "123456789";
        String sbc = Convert.toSBC(a);
        Assert.assertEquals("?????????", sbc);
    }

    @Test
    public void toDBCTest() {
        String a = "?????????";
        String dbc = Convert.toDBC(a);
        Assert.assertEquals("123456789", dbc);
    }
}

