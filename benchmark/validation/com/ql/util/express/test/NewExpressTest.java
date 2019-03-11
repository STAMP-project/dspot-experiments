package com.ql.util.express.test;


import com.ql.util.express.ExpressRunner;
import com.ql.util.express.IExpressContext;
import org.junit.Assert;
import org.junit.Test;


public class NewExpressTest {
    @Test
    public void testParse() throws Exception {
        String[][] expresses = new String[][]{ new String[]{ "0 - 3295837566L", "-3295837566" }, new String[]{ "1==1? 50+50:100+100", "100" }, new String[]{ "1==2? 50+50:100+100", "200" }, new String[]{ "int[][] abc = new int[10][10]; abc[0][0] = 100; abc[0][0]-10", "90" }, new String[]{ "Integer.parseInt(\"1\")-1", "0" }, new String[]{ "Double.parseDouble(\"-0.22\")", "-0.22" }, new String[]{ "(int)((Double.parseDouble(\"0.22\")-0.21)*100)", "1" }, new String[]{ "1+2+/** ???? **/ 12+1 ", "16" }, new String[]{ "3240732988055L", "3240732988055" }, new String[]{ "3240732988054", "3240732988054" }, new String[]{ "0.5d", "0.5" }, new String[]{ "0.3f", "0.3" }, new String[]{ "0.55", "0.55" }, new String[]{ "1+1", "2" }, new String[]{ "(1+1)*(9-7);", "4" }, new String[]{ "1+1;2+5", "7" }, new String[]{ "false && true", "false" }, new String[]{ "true || fale", "true" }, new String[]{ "return 100/2;", "50" }, new String[]{ "return 10;1 + 1;", "10" }, new String[]{ "if(1==1) then{ return 100; }else{return 10;}", "100" }, new String[]{ "if(1==2) then{ return 100; }else{return 10;}", "10" }, new String[]{ "if(1==1) then{ return 100;}", "100" }, new String[]{ "if(1==2) then{ return 100;}", "null" }, new String[]{ "if(1==1) { return 100; }else{return 10;}", "100" }, new String[]{ "if(1==2) { return 100; }else{return 10;}", "10" }, new String[]{ "if(1==1) { return 100;}", "100" }, new String[]{ "if(1==2) { return 100;}", "null" }, new String[]{ "int i = 2", "2" }, new String[]{ "i=2;i<10;", "true" }, new String[]{ "a  =0 ; for(int i=0;i<10;i=i+1){a = a + 1;} return a;", "10" }, new String[]{ "new String(\"ss\")", "ss" }, new String[]{ "(new String[1][1])[0][0]", "null" }, new String[]{ "a = new String[1][9];  a[0][1+1] = \"qianghui\"; b = a[0][2]; ", "qianghui" }, new String[]{ "(new String[3][5])[1].length", "5" }, new String[]{ "\"abc\".length()", "3" }, new String[]{ "\"abc\".substring(1,3).substring(1,2)", "c" }, new String[]{ "Integer.SIZE", "32" }, new String[]{ "new com.ql.util.express.test.BeanExample(\"qianghui\").name", "qianghui" }, new String[]{ "System.out.println(1)", "null" }, new String[]{ "int a = 0;for(int i= 0;i<10;i++){ System.out.println(i); a= a+ i} return a;", "45" }, new String[]{ "int a = 0;for(int i= 0;i<10;i++){ if(i > 5) then{break;}  a= a+ i;} return a;", "15" }, new String[]{ "int a = 0;for(int i= 0;i<10;i++){ if(i <=5) then{continue;}  a= a+ i;} return a;", "30" }, new String[]{ "int a =0; alias pa a; pa++ ;return a", "1" }, new String[]{ "int[][] a = new int[10][10]; alias pa a[0]; pa[0] =100 ;return a[0][0]", "100" }, new String[]{ "int[][] a = new int[10][10]; {exportAlias pa a[0]; pa[0] =100} ;pa[1] =200; return a[0][0] + a[0][1];", "300" }, new String[]{ "int[][] a = new int[10][10]; {exportDef int i = 1; exportDef int j=1; a[i][j] =999} ; return a[i][j];", "999" }, new String[]{ "return ((float)9)/2", "4.5" }, new String[]{ "int a =9; return ((float)a)/2", "4.5" }, new String[]{ "float a =9; return a/2", "4.5" }, new String[]{ "macro  ??    {100 + 100} ??;", "200" }, new String[]{ "function union(String a,String b){return a +\'-\'+ b;}; union(\"qiang\",\"hui\")", "qiang-hui" }, new String[]{ " 3+4 in (8+3,7,9)", "true" }, new String[]{ "\"ab\" like \"a%\"", "true" }, new String[]{ "int ?? = 100; int ?? = 200 ;return ?? + ??", "300" }, new String[]{ "1 ? 1 ", "2" }, new String[]{ " 'a' love 'b' love 'c'", "c{b{a}b}c" }, new String[]{ "if 1==2 then {return 10}else{return 100}", "100" } };
        for (int i = 0; i < (expresses.length); i++) {
            IExpressContext<String, Object> expressContext = new com.ql.util.express.DefaultContext<String, Object>();
            ExpressRunner runner = new ExpressRunner(false, true);
            runner.addOperatorWithAlias("?", "+", null);
            runner.addOperator("love", "+", new LoveOperator("love"));
            Object result = runner.execute(expresses[i][0], expressContext, null, false, true);
            System.out.println(("?????" + result));
            System.out.println(("?????" + expressContext));
            Assert.assertTrue(((((("???????:" + (expresses[i][0])) + " ????") + (expresses[i][1])) + " ?????") + result), expresses[i][1].equals((result == null ? "null" : result.toString())));
        }
    }
}

