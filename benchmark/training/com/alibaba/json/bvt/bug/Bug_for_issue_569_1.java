package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import java.util.List;
import junit.framework.TestCase;
import org.junit.Assert;


/**
 * Created by wenshao on 16/8/11.
 */
public class Bug_for_issue_569_1 extends TestCase {
    public void test_for_issue() throws Exception {
        String str = "{\"bList\":[{\"data\":[0,1]},{\"data\":[1,2]},{\"data\":[2,3]},{\"data\":[3,4]},{\"data\":[4,5]},{\"data\":[5,6]},{\"data\":[6,7]},{\"data\":[7,8]},{\"data\":[8,9]},{\"data\":[9,10]}]}";
        Bug_for_issue_569_1.A<Integer> aInteger;
        Bug_for_issue_569_1.A<Long> aLong;
        // aInteger = JSON.parseObject(str, new TypeReference<A<Integer>>() {
        // });
        // Assert.assertEquals(aInteger.getbList().get(0).getData().get(0).getClass().getName(), Integer.class.getName());
        // 
        aLong = JSON.parseObject(str, new com.alibaba.fastjson.TypeReference<Bug_for_issue_569_1.A<Long>>() {});
        Assert.assertEquals(aLong.getbList().get(0).getData().get(0).getClass().getName(), Long.class.getName());
    }

    public static class A<T> {
        private List<Bug_for_issue_569_1.B<T>> bList;

        public List<Bug_for_issue_569_1.B<T>> getbList() {
            return bList;
        }

        public void setbList(List<Bug_for_issue_569_1.B<T>> bList) {
            this.bList = bList;
        }
    }

    public static class B<T> {
        private List<T> data;

        public List<T> getData() {
            return data;
        }

        public void setData(List<T> data) {
            this.data = data;
        }
    }
}

