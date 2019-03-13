package io.mycat.parser.druid;


import org.junit.Test;


/**
 *
 *
 * @author Hash Zhang
 * @version 1.0.0
 * @unknown 2016/7/7
 */
public class DruidUpdateParserTest {
    /**
     * ??????????
     *
     * @throws NoSuchMethodException
     * 		
     */
    @Test
    public void testUpdateShardColumn() throws NoSuchMethodException {
        throwExceptionParse("update hotnews set id = 1 where name = 234;", true);
        throwExceptionParse("update hotnews set id = 1 where id = 3;", true);
        throwExceptionParse("update hotnews set id = 1, name = '123' where id = 1 and name = '234'", false);
        throwExceptionParse("update hotnews set id = 1, name = '123' where id = 1 or name = '234'", true);
        throwExceptionParse("update hotnews set id = 'A', name = '123' where id = 'A' and name = '234'", false);
        throwExceptionParse("update hotnews set id = 'A', name = '123' where id = 'A' or name = '234'", true);
        throwExceptionParse("update hotnews set id = 1.5, name = '123' where id = 1.5 and name = '234'", false);
        throwExceptionParse("update hotnews set id = 1.5, name = '123' where id = 1.5 or name = '234'", true);
        throwExceptionParse("update hotnews set id = 1, name = '123' where name = '234' and (id = 1 or age > 3)", true);
        throwExceptionParse("update hotnews set id = 1, name = '123' where id = 1 and (name = '234' or age > 3)", false);
        // ??????????between???
        throwExceptionParse("update hotnews set id = 1, name = '123' where id = 1 and name in (select name from test)", false);
        throwExceptionParse("update hotnews set id = 1, name = '123' where name = '123' and id in (select id from test)", true);
        throwExceptionParse("update hotnews set id = 1, name = '123' where id between 1 and 3", true);
        throwExceptionParse("update hotnews set id = 1, name = '123' where id between 1 and 3 and name = '234'", true);
        throwExceptionParse("update hotnews set id = 1, name = '123' where id between 1 and 3 or name = '234'", true);
        throwExceptionParse("update hotnews set id = 1, name = '123' where id = 1 and name between '124' and '234'", false);
    }

    /**
     * ????????????
     *
     * @throws NoSuchMethodException
     * 		
     */
    @Test
    public void testAliasUpdateShardColumn() throws NoSuchMethodException {
        throwExceptionParse("update hotnews h set h.id = 1 where h.name = 234;", true);
        throwExceptionParse("update hotnews h set h.id = 1 where h.id = 3;", true);
        throwExceptionParse("update hotnews h set h.id = 1, h.name = '123' where h.id = 1 and h.name = '234'", false);
        throwExceptionParse("update hotnews h set h.id = 1, h.name = '123' where h.id = 1 or h.name = '234'", true);
        throwExceptionParse("update hotnews h set h.id = 'A', h.name = '123' where h.id = 'A' and h.name = '234'", false);
        throwExceptionParse("update hotnews h set h.id = 'A', h.name = '123' where h.id = 'A' or h.name = '234'", true);
        throwExceptionParse("update hotnews h set h.id = 1.5, h.name = '123' where h.id = 1.5 and h.name = '234'", false);
        throwExceptionParse("update hotnews h set h.id = 1.5, h.name = '123' where h.id = 1.5 or h.name = '234'", true);
        throwExceptionParse("update hotnews h set id = 1, h.name = '123' where h.id = 1 and h.name = '234'", false);
        throwExceptionParse("update hotnews h set h.id = 1, h.name = '123' where id = 1 or h.name = '234'", true);
        throwExceptionParse("update hotnews h set h.id = 1, h.name = '123' where h.name = '234' and (h.id = 1 or h.age > 3)", true);
        throwExceptionParse("update hotnews h set h.id = 1, h.name = '123' where h.id = 1 and (h.name = '234' or h.age > 3)", false);
    }
}

