package com.querydsl.apt.domain;


import QRooEntities_MyEntity.myEntity;
import QRooEntities_MyEntity2.myEntity2;
import org.junit.Assert;
import org.junit.Test;


public class RooEntitiesTest {
    @Test
    public void rooJpaEntity() {
        Assert.assertNotNull(myEntity);
    }

    @Test
    public void rooJpaActiveRecord() {
        Assert.assertNotNull(myEntity2);
    }
}

