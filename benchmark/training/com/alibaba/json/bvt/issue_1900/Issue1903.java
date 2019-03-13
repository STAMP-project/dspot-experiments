package com.alibaba.json.bvt.issue_1900;


import com.alibaba.fastjson.JSON;
import java.beans.Transient;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;


public class Issue1903 extends TestCase {
    public void test_issue() throws Exception {
        Issue1903.MapHandler mh = new Issue1903.MapHandler();
        mh.add("name", "test");
        mh.add("age", 20);
        Issue1903.Issues1903 issues = ((Issue1903.Issues1903) (Proxy.newProxyInstance(mh.getClass().getClassLoader(), new Class[]{ Issue1903.Issues1903.class }, mh)));
        System.out.println(issues.getName());
        System.out.println(issues.getAge());
        System.out.println(JSON.toJSON(issues).toString());// ????: {"age":20}

        System.out.println(JSON.toJSONString(issues));// ????: {"age":20}

    }

    interface Issues1903 {
        @Transient
        public String getName();

        public void setName(String name);

        public Integer getAge();

        public void setAge(Integer age);
    }

    class MapHandler implements InvocationHandler {
        Map<String, Object> map = new HashMap<String, Object>();

        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            String name = method.getName().substring(3);
            String first = String.valueOf(name.charAt(0));
            name = name.replaceFirst(first, first.toLowerCase());
            return map.get(name);
        }

        public void add(String key, Object val) {
            map.put(key, val);
        }
    }
}

