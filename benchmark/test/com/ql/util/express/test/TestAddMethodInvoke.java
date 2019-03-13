package com.ql.util.express.test;


import com.ql.util.express.ExpressRunner;
import com.ql.util.express.IExpressContext;
import com.ql.util.express.Operator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;


/**
 * Created by tianqiao on 16/10/17.
 */
public class TestAddMethodInvoke {
    @Test
    public void testStringMethod() throws Exception {
        ExpressRunner runner = new ExpressRunner();
        IExpressContext<String, Object> context = new com.ql.util.express.DefaultContext<String, Object>();
        Object result = runner.execute("'helloWorld'.length()", context, null, false, false);
        System.out.println(result);
        runner.addFunctionAndClassMethod("isBlank", Object.class, new Operator() {
            @Override
            public Object executeInner(Object[] list) throws Exception {
                String str = ((String) (list[0]));
                return (str.trim().length()) == 0;
            }
        });
        runner.addFunctionAndClassMethod("isNotBlank", String.class, new Operator() {
            @Override
            public Object executeInner(Object[] list) throws Exception {
                String str = ((String) (list[0]));
                return (str.trim().length()) > 0;
            }
        });
        result = runner.execute("isBlank(\'\t\n\')", context, null, false, false);
        assert ((Boolean) (result));
        result = runner.execute("\'\t\n\'.isBlank()", context, null, false, false);
        assert ((Boolean) (result));
        result = runner.execute("isNotBlank('helloworld')", context, null, false, false);
        assert ((Boolean) (result));
        result = runner.execute("'helloworld'.isNotBlank()", context, null, false, false);
        assert ((Boolean) (result));
    }

    @Test
    public void testArrayOrMapJoinMethod() throws Exception {
        ExpressRunner runner = new ExpressRunner();
        IExpressContext<String, Object> context = new com.ql.util.express.DefaultContext<String, Object>();
        runner.addClassMethod("join", List.class, new Operator() {
            @Override
            public Object executeInner(Object[] list) throws Exception {
                ArrayList arrayList = ((ArrayList) (list[0]));
                return StringUtils.join(arrayList, ((String) (list[1])));
            }
        });
        runner.addClassMethod("join", Map.class, new Operator() {
            @Override
            public Object executeInner(Object[] list) throws Exception {
                HashMap map = ((HashMap) (list[0]));
                StringBuilder sb = new StringBuilder();
                for (Object key : map.keySet()) {
                    sb.append(key).append("=").append(map.get(key)).append(((String) (list[1])));
                }
                return sb.substring(0, ((sb.length()) - 1));
            }
        });
        Object result = runner.execute("list=new ArrayList();list.add(1);list.add(2);list.add(3);return list.join(' , ');", context, null, false, false);
        System.out.println(result);
        result = runner.execute("list=new HashMap();list.put('a',1);list.put('b',2);list.put('c',3);return list.join(' , ');", context, null, false, false);
        System.out.println(result);
    }

    @Test
    public void testAop() throws Exception {
        ExpressRunner runner = new ExpressRunner();
        IExpressContext<String, Object> context = new com.ql.util.express.DefaultContext<String, Object>();
        runner.addClassMethod("size", List.class, new Operator() {
            @Override
            public Object executeInner(Object[] list) throws Exception {
                ArrayList arrayList = ((ArrayList) (list[0]));
                System.out.println("???List.size()??");
                return arrayList.size();
            }
        });
        runner.addClassField("??", List.class, new Operator() {
            @Override
            public Object executeInner(Object[] list) throws Exception {
                ArrayList arrayList = ((ArrayList) (list[0]));
                System.out.println("???List.?? ?????");
                return arrayList.size();
            }
        });
        Object result = runner.execute("list=new ArrayList();list.add(1);list.add(2);list.add(3);return list.size();", context, null, false, false);
        System.out.println(result);
        result = runner.execute("list=new ArrayList();list.add(1);list.add(2);list.add(3);return list.??;", context, null, false, false);
        System.out.println(result);
        // bugfix ??return ??????????getType???????
        Object result2 = runner.execute("list=new ArrayList();list.add(1);list.add(2);list.add(3);list.??;", context, null, false, false);
        System.out.println(result2);
    }
}

