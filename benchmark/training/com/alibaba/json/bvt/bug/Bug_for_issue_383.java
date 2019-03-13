package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import java.util.Collection;
import java.util.HashSet;
import junit.framework.TestCase;
import org.junit.Assert;


public class Bug_for_issue_383 extends TestCase {
    public void test_for_issue() throws Exception {
        Bug_for_issue_383.TestClass ts = new Bug_for_issue_383.TestClass();
        ts.getHashSet().add(1);
        ts.getHashSet().add(4);
        ts.getMember().getHashSet().add(10);
        ts.getMember().getHashSet().add(15);
        String jsonStr = JSON.toJSONString(ts, new SerializerFeature[]{ SerializerFeature.WriteClassName, SerializerFeature.DisableCircularReferenceDetect });
        System.out.println(jsonStr);
        ts = JSON.parseObject(jsonStr, Bug_for_issue_383.TestClass.class);
        Assert.assertEquals(HashSet.class, ts.getHashSet().getClass());
        for (Integer val : ts.getHashSet()) {
            System.out.println(val);
        }
    }

    public static class TestClass {
        private Collection<Integer> hashSet = new HashSet<Integer>();

        private Bug_for_issue_383.Member member = new Bug_for_issue_383.Member();

        public TestClass() {
        }

        public Collection<Integer> getHashSet() {
            return hashSet;
        }

        public void setHashSet(Collection<Integer> hashSet) {
            this.hashSet = hashSet;
        }

        public Bug_for_issue_383.Member getMember() {
            return member;
        }

        public void setMember(Bug_for_issue_383.Member member) {
            this.member = member;
        }
    }

    public static class Member {
        private Collection<Integer> hashSet = new HashSet<Integer>();

        public Member() {
        }

        public Collection<Integer> getHashSet() {
            return hashSet;
        }

        public void setHashSet(Collection<Integer> hashSet) {
            this.hashSet = hashSet;
        }
    }
}

