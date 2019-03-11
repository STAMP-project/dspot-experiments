package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import junit.framework.TestCase;


public class Bug14 extends TestCase {
    public void test_0() throws Exception {
        double f = -5.5000009;
        Long i = 4294967295L;
        System.out.println(BigInteger.valueOf(i));
        System.out.println(Math.round(f));
        List<Bug14.AB> list = new ArrayList<Bug14.AB>();
        list.add(new Bug14.AB("2a", "3b"));
        list.add(new Bug14.AB("4a", "6b"));
        list.add(new Bug14.AB("6a", "7{sdf<>jgh\n}b"));
        list.add(new Bug14.AB("8a", "9b"));
        list.add(new Bug14.AB("10a", "11ba"));
        list.add(new Bug14.AB("12a", "13b"));
        String[] abc = new String[]{ "sf", "sdf", "dsffds", "sdfsdf{fds}" };
        Map<String, Bug14.AB> map = new LinkedHashMap();
        int k = 0;
        for (Bug14.AB a : list) {
            map.put(String.valueOf((k++)), a);
        }
        System.out.println(JSON.toJSON(list));
        System.out.println(JSON.toJSON(abc));
        System.out.println(JSON.toJSON(new Bug14.AB("10a", "11ba")));
        System.out.println(JSON.toJSON(map));
    }

    private static class AB {
        private String a;

        private String b;

        public AB() {
            super();
        }

        public AB(String a, String b) {
            super();
            this.a = a;
            this.b = b;
        }

        public String getA() {
            return a;
        }

        public String getB() {
            return b;
        }
    }
}

