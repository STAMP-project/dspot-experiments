package org.hswebframework.web.commons.entity.param;


import SerializerFeature.PrettyFormat;
import Term.Type.or;
import com.alibaba.fastjson.JSON;
import java.util.List;
import org.hswebframework.ezorm.core.param.Term;
import org.junit.Assert;
import org.junit.Test;


public class TermExpressionParserTest {
    @Test
    public void testSimple() {
        String expression = "name=?? or age=10";
        List<Term> terms = TermExpressionParser.parse(expression);
        Assert.assertNotNull(terms);
        Assert.assertEquals(terms.size(), 2);
        Assert.assertEquals(terms.get(0).getColumn(), "name");
        Assert.assertEquals(terms.get(0).getValue(), "??");
        Assert.assertEquals(terms.get(1).getColumn(), "age");
        Assert.assertEquals(terms.get(1).getValue(), "10");
        Assert.assertEquals(terms.get(1).getType(), or);
    }

    @Test
    public void testNest() {
        String expression = "name = ?? and (age > 10 or age <= 20) and test like test2 and (age gt age2 or age btw age3,age4 or (age > 10 or age <= 20))";
        System.out.println(expression);
        List<Term> terms = TermExpressionParser.parse(expression);
        System.out.println(JSON.toJSONString(terms, PrettyFormat));
        Assert.assertNotNull(terms);
        Assert.assertEquals(terms.size(), 4);
        Assert.assertEquals(terms.get(1).getTerms().size(), 2);
        Assert.assertEquals(terms.get(0).getColumn(), "name");
        Assert.assertEquals(terms.get(0).getValue(), "??");
        Assert.assertEquals(terms.get(1).getTerms().get(0).getColumn(), "age");
        Assert.assertEquals(terms.get(1).getTerms().get(0).getTermType(), "gt");
        Assert.assertEquals(terms.get(1).getTerms().get(0).getValue(), "10");
        Assert.assertEquals(terms.get(1).getTerms().get(1).getColumn(), "age");
        Assert.assertEquals(terms.get(1).getTerms().get(1).getTermType(), "lte");
        Assert.assertEquals(terms.get(1).getTerms().get(1).getValue(), "20");
        Assert.assertEquals(terms.get(1).getTerms().get(1).getType(), or);
        Assert.assertEquals(terms.get(2).getColumn(), "test");
        Assert.assertEquals(terms.get(2).getValue(), "test2");
        Assert.assertEquals(terms.get(2).getTermType(), "like");
    }
}

