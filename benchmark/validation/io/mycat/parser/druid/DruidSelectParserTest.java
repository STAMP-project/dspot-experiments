package io.mycat.parser.druid;


import io.mycat.route.parser.druid.impl.DruidSelectParser;
import java.lang.reflect.InvocationTargetException;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by Hash Zhang on 2016/4/29.
 * Modified by Hash Zhang on 2016/5/25 add testGroupByWithViewAlias.
 */
public class DruidSelectParserTest {
    DruidSelectParser druidSelectParser = new DruidSelectParser();

    /**
     * ?????DruidSelectParser?buildGroupByCols??????????
     * ??select???????alias???
     * ???groupby?????????
     *
     * @throws NoSuchMethodException
     * 		
     * @throws InvocationTargetException
     * 		
     * @throws IllegalAccessException
     * 		
     */
    @Test
    public void testGroupByWithAlias() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        String functionColumn = "DATE_FORMAT(h.times,'%b %d %Y %h:%i %p')";
        Object result = invokeGroupBy(functionColumn);
        Assert.assertEquals(functionColumn, ((String[]) (result))[0]);
    }

    /**
     * ?????DruidSelectParser????????????
     *
     * @throws NoSuchMethodException
     * 		
     * @throws InvocationTargetException
     * 		
     * @throws IllegalAccessException
     * 		
     */
    @Test
    public void testGroupByWithViewAlias() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        String functionColumn = "select id from (select h.id from hotnews h  union select h.title from hotnews h ) as t1 group by t1.id;";
        Object result = invokeGroupBy(functionColumn);
        Assert.assertEquals(functionColumn, ((String[]) (result))[0]);
    }
}

