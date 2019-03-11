package com.querydsl.apt.domain;


import QProperties4Test_Naming.naming._8FRecord;
import javax.persistence.MappedSuperclass;
import org.junit.Assert;
import org.junit.Test;


public class Properties4Test extends AbstractTest {
    @MappedSuperclass
    public abstract static class Naming {
        public abstract boolean is8FRecord();
    }

    @Test
    public void test() {
        Assert.assertEquals("8FRecord", _8FRecord.getMetadata().getName());
    }
}

