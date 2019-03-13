package com.alibaba.json.bvt.bug;


import SerializerFeature.WriteClassName;
import com.alibaba.fastjson.JSON;
import com.alibaba.json.test.dubbo.HelloServiceImpl;
import com.alibaba.json.test.dubbo.Tiger;
import com.alibaba.json.test.dubbo.Tigers;
import junit.framework.TestCase;
import org.junit.Assert;


public class Bug_for_dubbo extends TestCase {
    public void test_0() throws Exception {
        HelloServiceImpl helloService = new HelloServiceImpl();
        Tiger tiger = new Tiger();
        tiger.setTigerName("???");
        tiger.setTigerSex(true);
        // Tiger tigers = helloService.eatTiger(tiger).getTiger();
        Tigers tigers = helloService.eatTiger(tiger);
        Assert.assertNotNull(tigers.getTiger());
        String text = JSON.toJSONString(tigers, WriteClassName);
        System.out.println(text);
        Tigers tigers1 = ((Tigers) (JSON.parse(text)));
        Assert.assertNotNull(tigers1.getTiger());
    }
}

