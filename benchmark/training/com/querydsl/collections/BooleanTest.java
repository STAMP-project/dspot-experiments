package com.querydsl.collections;


import com.querydsl.core.alias.Alias;
import java.util.Collections;
import org.junit.Assert;
import org.junit.Test;


public class BooleanTest {
    public static class Entity {
        private boolean boolean1 = true;

        private Boolean boolean2 = Boolean.TRUE;

        public boolean isBoolean1() {
            return boolean1;
        }

        public Boolean getBoolean2() {
            return boolean2;
        }
    }

    @Test
    public void primitive_boolean() {
        BooleanTest.Entity entity = Alias.alias(BooleanTest.Entity.class);
        Assert.assertEquals(1, CollQueryFactory.from(entity, Collections.singleton(new BooleanTest.Entity())).where($(entity.isBoolean1()).eq(Boolean.TRUE)).fetchCount());
    }

    @Test
    public void object_boolean() {
        BooleanTest.Entity entity = Alias.alias(BooleanTest.Entity.class);
        Assert.assertEquals(1, CollQueryFactory.from(entity, Collections.singleton(new BooleanTest.Entity())).where($(entity.getBoolean2()).eq(Boolean.TRUE)).fetchCount());
    }
}

