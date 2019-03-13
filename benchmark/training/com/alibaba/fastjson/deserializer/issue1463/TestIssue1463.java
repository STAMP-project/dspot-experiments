package com.alibaba.fastjson.deserializer.issue1463;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.deserializer.issue1463.beans.Person;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author LNAmp
 * @since 2017?09?11?

https://github.com/alibaba/fastjson/issues/569
 */
public class TestIssue1463 {
    private Person wenshao;

    @Test
    public void testIssue1463() {
        String str = doubleDeserialization(wenshao);
        try {
            wenshao = JSON.parseObject(str, Person.class);
        } catch (Throwable ex) {
            Assert.assertEquals(((ex.getCause()) instanceof NullPointerException), false);
        }
    }
}

