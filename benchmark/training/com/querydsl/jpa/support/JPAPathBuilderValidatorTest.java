package com.querydsl.jpa.support;


import com.querydsl.jpa.domain.Cat;
import java.util.Collection;
import javax.persistence.EntityManagerFactory;
import org.junit.Assert;
import org.junit.Test;


public class JPAPathBuilderValidatorTest {
    private EntityManagerFactory entityManagerFactory;

    @Test
    public void validate() {
        JPAPathBuilderValidator validator = new JPAPathBuilderValidator(entityManagerFactory.getMetamodel());
        Assert.assertEquals(String.class, validator.validate(Cat.class, "name", String.class));
        Assert.assertEquals(Cat.class, validator.validate(Cat.class, "kittens", Collection.class));
        Assert.assertEquals(Cat.class, validator.validate(Cat.class, "mate", Cat.class));
        Assert.assertNull(validator.validate(Cat.class, "xxx", String.class));
        Assert.assertNull(validator.validate(Object.class, "name", String.class));
    }
}

