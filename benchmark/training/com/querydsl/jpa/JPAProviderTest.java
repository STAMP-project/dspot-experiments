package com.querydsl.jpa;


import HQLTemplates.DEFAULT;
import com.querydsl.jpa.impl.JPAProvider;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.junit.Assert;
import org.junit.Test;


// 5.664
public class JPAProviderTest {
    private EntityManagerFactory factory;

    private EntityManager em;

    @Test
    public void hibernate() {
        factory = Persistence.createEntityManagerFactory("h2");
        em = factory.createEntityManager();
        System.out.println(em.getDelegate().getClass());
        Assert.assertEquals(DEFAULT, JPAProvider.getTemplates(em));
    }

    @Test
    public void hibernate_for_proxy() {
        factory = Persistence.createEntityManagerFactory("h2");
        em = factory.createEntityManager();
        InvocationHandler handler = new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                return method.invoke(em, args);
            }
        };
        EntityManager proxy = ((EntityManager) (Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class<?>[]{ EntityManager.class }, handler)));
        Assert.assertEquals(DEFAULT, JPAProvider.getTemplates(proxy));
    }

    @Test
    public void eclipseLink() {
        factory = Persistence.createEntityManagerFactory("h2-eclipselink");
        em = factory.createEntityManager();
        System.out.println(em.getDelegate().getClass());
        System.out.println(em.getProperties());
        Assert.assertEquals(EclipseLinkTemplates.DEFAULT, JPAProvider.getTemplates(em));
    }

    @Test
    public void eclipseLink_for_proxy() {
        factory = Persistence.createEntityManagerFactory("h2-eclipselink");
        em = factory.createEntityManager();
        InvocationHandler handler = new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                return method.invoke(em, args);
            }
        };
        EntityManager proxy = ((EntityManager) (Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class<?>[]{ EntityManager.class }, handler)));
        Assert.assertEquals(EclipseLinkTemplates.DEFAULT, JPAProvider.getTemplates(proxy));
    }
}

