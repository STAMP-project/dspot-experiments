package com.ql.util.express.test;


import com.ql.util.express.ExpressRunner;
import com.ql.util.express.IExpressContext;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;


public class OpCallTest {
    @Test
    public void testList() throws Exception {
        ExpressRunner runner = new ExpressRunner(false, true);
        runner.addOperator("@love", new LoveOperator("@love"));
        runner.loadMutilExpress(null, "function abc(String s){println(s)}");
        runner.addOperatorWithAlias("??", "println", null);
        runner.addFunctionOfClassMethod("isVIP", BeanExample.class.getName(), "isVIP", new Class[]{ String.class }, "");
        runner.addOperatorWithAlias("??VIP", "isVIP", "???$1,????VIP??");
        String[][] expressTest = new String[][]{ new String[]{ "println \"ssssss\"", "null" }, new String[]{ "println (\"ssssss\")", "null" }, new String[]{ "abc (\"bbbbbbbb\")", "null" }, new String[]{ "\u6253\u5370 (\"\u51fd\u6570\u522b\u540d\u6d4b\u8bd5\")", "null" }, new String[]{ "isVIP (\"\u7384\u96be\")", "false" }, new String[]{ "\u662f\u5426VIP (\"\u7384\u96be\")", "false" } };
        IExpressContext<String, Object> expressContext = new ExpressContextExample(null);
        for (int point = 0; point < (expressTest.length); point++) {
            String expressStr = expressTest[point][0];
            List<String> errorList = new ArrayList<String>();
            Object result = runner.execute(expressStr, expressContext, errorList, false, true);
            if ((((result == null) && ((expressTest[point][1].equalsIgnoreCase("null")) == false)) || ((expressTest[point][1].equalsIgnoreCase("null")) && (result != null))) || ((result != null) && ((expressTest[point][1].equalsIgnoreCase(result.toString())) == false))) {
                throw new Exception(((((("????,???????????:" + expressStr) + " = ") + result) + "???????") + (expressTest[point][1])));
            }
            System.out.println(((((("Example " + point) + " : ") + expressStr) + " =  ") + result));
            if ((errorList.size()) > 0) {
                System.out.println(("\t\t\u7cfb\u7edf\u8f93\u51fa\u7684\u9519\u8bef\u63d0\u793a\u4fe1\u606f:" + errorList));
            }
        }
    }
}

