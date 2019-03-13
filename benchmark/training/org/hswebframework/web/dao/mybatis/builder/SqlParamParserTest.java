package org.hswebframework.web.dao.mybatis.builder;


import SerializerFeature.PrettyFormat;
import Term.Type.and;
import Term.Type.or;
import com.alibaba.fastjson.JSON;
import java.util.LinkedHashMap;
import java.util.Map;
import org.hswebframework.web.commons.entity.param.QueryParamEntity;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author zhouhao
 * @since 1.0
 */
public class SqlParamParserTest {
    @Test
    public void testParseQueryParam() {
        Map<String, Object> queryParam = new LinkedHashMap<>();
        queryParam.put("name", "??");
        queryParam.put("name$like$or", "??");
        queryParam.put("and", builder().name$like("??%").age$gt(1).or(builder().name$like("??").age$gt(10).build()).build());
        QueryParamEntity entity = SqlParamParser.parseQueryParam(queryParam);
        Assert.assertTrue((!(entity.getTerms().isEmpty())));
        Assert.assertEquals(entity.getTerms().get(0).getColumn(), "name");
        Assert.assertEquals(entity.getTerms().get(0).getType(), and);
        Assert.assertEquals(entity.getTerms().get(1).getColumn(), "name");
        Assert.assertEquals(entity.getTerms().get(1).getTermType(), "like");
        Assert.assertEquals(entity.getTerms().get(1).getType(), or);
        Assert.assertEquals(entity.getTerms().get(2).getType(), and);
        Assert.assertTrue((!(entity.getTerms().get(2).getTerms().isEmpty())));
        Assert.assertEquals(entity.getTerms().get(2).getTerms().get(0).getTermType(), "like");
        Assert.assertEquals(entity.getTerms().get(2).getTerms().get(1).getTermType(), "gt");
        Assert.assertTrue((!(entity.getTerms().get(2).getTerms().get(2).getTerms().isEmpty())));
        Assert.assertEquals(entity.getTerms().get(2).getTerms().get(2).getTerms().get(0).getTermType(), "like");
        Assert.assertEquals(entity.getTerms().get(2).getTerms().get(2).getTerms().get(1).getTermType(), "gt");
        System.out.println(JSON.toJSONString(entity, PrettyFormat));
    }
}

