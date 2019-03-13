/**
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.orm.jpa.support;


import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.PersistenceProperty;
import javax.persistence.PersistenceUnit;
import org.hibernate.ejb.HibernateEntityManager;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.lang.Nullable;
import org.springframework.orm.jpa.AbstractEntityManagerFactoryBeanTests;
import org.springframework.orm.jpa.DefaultJpaDialect;
import org.springframework.orm.jpa.EntityManagerFactoryInfo;
import org.springframework.stereotype.Repository;
import org.springframework.tests.context.SimpleMapScope;
import org.springframework.tests.mock.jndi.ExpectedLookupTemplate;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.SerializationTestUtils;


/**
 * Unit tests for persistence context and persistence unit injection.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Phillip Webb
 */
@SuppressWarnings("resource")
public class PersistenceInjectionTests extends AbstractEntityManagerFactoryBeanTests {
    @Test
    public void testPrivatePersistenceContextField() throws Exception {
        AbstractEntityManagerFactoryBeanTests.mockEmf = Mockito.mock(EntityManagerFactory.class, Mockito.withSettings().serializable());
        GenericApplicationContext gac = new GenericApplicationContext();
        gac.getDefaultListableBeanFactory().registerSingleton("entityManagerFactory", AbstractEntityManagerFactoryBeanTests.mockEmf);
        gac.registerBeanDefinition("annotationProcessor", new RootBeanDefinition(PersistenceAnnotationBeanPostProcessor.class));
        gac.registerBeanDefinition(PersistenceInjectionTests.DefaultPrivatePersistenceContextField.class.getName(), new RootBeanDefinition(PersistenceInjectionTests.DefaultPrivatePersistenceContextField.class));
        gac.registerBeanDefinition(PersistenceInjectionTests.FactoryBeanWithPersistenceContextField.class.getName(), new RootBeanDefinition(PersistenceInjectionTests.FactoryBeanWithPersistenceContextField.class));
        gac.refresh();
        PersistenceInjectionTests.DefaultPrivatePersistenceContextField bean = ((PersistenceInjectionTests.DefaultPrivatePersistenceContextField) (gac.getBean(PersistenceInjectionTests.DefaultPrivatePersistenceContextField.class.getName())));
        PersistenceInjectionTests.FactoryBeanWithPersistenceContextField bean2 = ((PersistenceInjectionTests.FactoryBeanWithPersistenceContextField) (gac.getBean(("&" + (PersistenceInjectionTests.FactoryBeanWithPersistenceContextField.class.getName())))));
        Assert.assertNotNull(bean.em);
        Assert.assertNotNull(bean2.em);
        Assert.assertNotNull(SerializationTestUtils.serializeAndDeserialize(bean.em));
        Assert.assertNotNull(SerializationTestUtils.serializeAndDeserialize(bean2.em));
    }

    @Test
    public void testPrivateVendorSpecificPersistenceContextField() {
        GenericApplicationContext gac = new GenericApplicationContext();
        gac.getDefaultListableBeanFactory().registerSingleton("entityManagerFactory", AbstractEntityManagerFactoryBeanTests.mockEmf);
        gac.registerBeanDefinition("annotationProcessor", new RootBeanDefinition(PersistenceAnnotationBeanPostProcessor.class));
        gac.registerBeanDefinition(PersistenceInjectionTests.DefaultVendorSpecificPrivatePersistenceContextField.class.getName(), new RootBeanDefinition(PersistenceInjectionTests.DefaultVendorSpecificPrivatePersistenceContextField.class));
        gac.refresh();
        PersistenceInjectionTests.DefaultVendorSpecificPrivatePersistenceContextField bean = ((PersistenceInjectionTests.DefaultVendorSpecificPrivatePersistenceContextField) (gac.getBean(PersistenceInjectionTests.DefaultVendorSpecificPrivatePersistenceContextField.class.getName())));
        Assert.assertNotNull(bean.em);
    }

    @Test
    public void testPublicExtendedPersistenceContextSetter() throws Exception {
        EntityManager mockEm = Mockito.mock(EntityManager.class);
        BDDMockito.given(AbstractEntityManagerFactoryBeanTests.mockEmf.createEntityManager()).willReturn(mockEm);
        GenericApplicationContext gac = new GenericApplicationContext();
        gac.getDefaultListableBeanFactory().registerSingleton("entityManagerFactory", AbstractEntityManagerFactoryBeanTests.mockEmf);
        gac.registerBeanDefinition("annotationProcessor", new RootBeanDefinition(PersistenceAnnotationBeanPostProcessor.class));
        gac.registerBeanDefinition(PersistenceInjectionTests.DefaultPublicPersistenceContextSetter.class.getName(), new RootBeanDefinition(PersistenceInjectionTests.DefaultPublicPersistenceContextSetter.class));
        gac.refresh();
        PersistenceInjectionTests.DefaultPublicPersistenceContextSetter bean = ((PersistenceInjectionTests.DefaultPublicPersistenceContextSetter) (gac.getBean(PersistenceInjectionTests.DefaultPublicPersistenceContextSetter.class.getName())));
        Assert.assertNotNull(bean.em);
    }

    @Test
    public void testPublicSpecificExtendedPersistenceContextSetter() throws Exception {
        EntityManagerFactory mockEmf2 = Mockito.mock(EntityManagerFactory.class);
        EntityManager mockEm2 = Mockito.mock(EntityManager.class);
        BDDMockito.given(mockEmf2.createEntityManager()).willReturn(mockEm2);
        GenericApplicationContext gac = new GenericApplicationContext();
        gac.getDefaultListableBeanFactory().registerSingleton("entityManagerFactory", AbstractEntityManagerFactoryBeanTests.mockEmf);
        gac.getDefaultListableBeanFactory().registerSingleton("unit2", mockEmf2);
        gac.registerBeanDefinition("annotationProcessor", new RootBeanDefinition(PersistenceAnnotationBeanPostProcessor.class));
        gac.registerBeanDefinition(PersistenceInjectionTests.SpecificPublicPersistenceContextSetter.class.getName(), new RootBeanDefinition(PersistenceInjectionTests.SpecificPublicPersistenceContextSetter.class));
        gac.refresh();
        PersistenceInjectionTests.SpecificPublicPersistenceContextSetter bean = ((PersistenceInjectionTests.SpecificPublicPersistenceContextSetter) (gac.getBean(PersistenceInjectionTests.SpecificPublicPersistenceContextSetter.class.getName())));
        Assert.assertNotNull(bean.getEntityManager());
        bean.getEntityManager().flush();
        Mockito.verify(mockEm2).getTransaction();
        Mockito.verify(mockEm2).flush();
    }

    @Test
    public void testInjectionIntoExistingObjects() {
        EntityManager mockEm = Mockito.mock(EntityManager.class);
        BDDMockito.given(AbstractEntityManagerFactoryBeanTests.mockEmf.createEntityManager()).willReturn(mockEm);
        GenericApplicationContext gac = new GenericApplicationContext();
        gac.getDefaultListableBeanFactory().registerSingleton("entityManagerFactory", AbstractEntityManagerFactoryBeanTests.mockEmf);
        gac.registerBeanDefinition("annotationProcessor", new RootBeanDefinition(PersistenceAnnotationBeanPostProcessor.class));
        gac.refresh();
        PersistenceInjectionTests.DefaultPrivatePersistenceContextField existingBean1 = new PersistenceInjectionTests.DefaultPrivatePersistenceContextField();
        gac.getAutowireCapableBeanFactory().autowireBean(existingBean1);
        Assert.assertNotNull(existingBean1.em);
        PersistenceInjectionTests.DefaultPublicPersistenceContextSetter existingBean2 = new PersistenceInjectionTests.DefaultPublicPersistenceContextSetter();
        gac.getAutowireCapableBeanFactory().autowireBean(existingBean2);
        Assert.assertNotNull(existingBean2.em);
    }

    @Test
    public void testPublicExtendedPersistenceContextSetterWithSerialization() throws Exception {
        PersistenceInjectionTests.DummyInvocationHandler ih = new PersistenceInjectionTests.DummyInvocationHandler();
        Object mockEm = Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[]{ EntityManager.class }, ih);
        BDDMockito.given(AbstractEntityManagerFactoryBeanTests.mockEmf.createEntityManager()).willReturn(((EntityManager) (mockEm)));
        GenericApplicationContext gac = new GenericApplicationContext();
        SimpleMapScope myScope = new SimpleMapScope();
        gac.getDefaultListableBeanFactory().registerScope("myScope", myScope);
        gac.getDefaultListableBeanFactory().registerSingleton("entityManagerFactory", AbstractEntityManagerFactoryBeanTests.mockEmf);
        gac.registerBeanDefinition("annotationProcessor", new RootBeanDefinition(PersistenceAnnotationBeanPostProcessor.class));
        RootBeanDefinition bd = new RootBeanDefinition(PersistenceInjectionTests.DefaultPublicPersistenceContextSetter.class);
        bd.setScope("myScope");
        gac.registerBeanDefinition(PersistenceInjectionTests.DefaultPublicPersistenceContextSetter.class.getName(), bd);
        gac.refresh();
        PersistenceInjectionTests.DefaultPublicPersistenceContextSetter bean = ((PersistenceInjectionTests.DefaultPublicPersistenceContextSetter) (gac.getBean(PersistenceInjectionTests.DefaultPublicPersistenceContextSetter.class.getName())));
        Assert.assertNotNull(bean.em);
        Assert.assertNotNull(SerializationTestUtils.serializeAndDeserialize(bean.em));
        SimpleMapScope serialized = ((SimpleMapScope) (SerializationTestUtils.serializeAndDeserialize(myScope)));
        serialized.close();
        Assert.assertTrue(PersistenceInjectionTests.DummyInvocationHandler.closed);
        PersistenceInjectionTests.DummyInvocationHandler.closed = false;
    }

    @Test
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void testPublicExtendedPersistenceContextSetterWithEntityManagerInfoAndSerialization() throws Exception {
        EntityManager mockEm = Mockito.mock(EntityManager.class, Mockito.withSettings().serializable());
        BDDMockito.given(mockEm.isOpen()).willReturn(true);
        PersistenceInjectionTests.EntityManagerFactoryWithInfo mockEmf = Mockito.mock(PersistenceInjectionTests.EntityManagerFactoryWithInfo.class);
        BDDMockito.given(getNativeEntityManagerFactory()).willReturn(mockEmf);
        BDDMockito.given(getJpaDialect()).willReturn(new DefaultJpaDialect());
        BDDMockito.given(getEntityManagerInterface()).willReturn(((Class) (EntityManager.class)));
        BDDMockito.given(getBeanClassLoader()).willReturn(getClass().getClassLoader());
        BDDMockito.given(createEntityManager()).willReturn(mockEm);
        GenericApplicationContext gac = new GenericApplicationContext();
        gac.getDefaultListableBeanFactory().registerSingleton("entityManagerFactory", mockEmf);
        gac.registerBeanDefinition("annotationProcessor", new RootBeanDefinition(PersistenceAnnotationBeanPostProcessor.class));
        gac.registerBeanDefinition(PersistenceInjectionTests.DefaultPublicPersistenceContextSetter.class.getName(), new RootBeanDefinition(PersistenceInjectionTests.DefaultPublicPersistenceContextSetter.class));
        gac.refresh();
        PersistenceInjectionTests.DefaultPublicPersistenceContextSetter bean = ((PersistenceInjectionTests.DefaultPublicPersistenceContextSetter) (gac.getBean(PersistenceInjectionTests.DefaultPublicPersistenceContextSetter.class.getName())));
        Assert.assertNotNull(bean.em);
        Assert.assertNotNull(SerializationTestUtils.serializeAndDeserialize(bean.em));
    }

    @Test
    public void testPublicExtendedPersistenceContextSetterWithOverriding() {
        EntityManager mockEm2 = Mockito.mock(EntityManager.class);
        GenericApplicationContext gac = new GenericApplicationContext();
        gac.getDefaultListableBeanFactory().registerSingleton("entityManagerFactory", AbstractEntityManagerFactoryBeanTests.mockEmf);
        gac.registerBeanDefinition("annotationProcessor", new RootBeanDefinition(PersistenceAnnotationBeanPostProcessor.class));
        RootBeanDefinition bd = new RootBeanDefinition(PersistenceInjectionTests.DefaultPublicPersistenceContextSetter.class);
        bd.getPropertyValues().add("entityManager", mockEm2);
        gac.registerBeanDefinition(PersistenceInjectionTests.DefaultPublicPersistenceContextSetter.class.getName(), bd);
        gac.refresh();
        PersistenceInjectionTests.DefaultPublicPersistenceContextSetter bean = ((PersistenceInjectionTests.DefaultPublicPersistenceContextSetter) (gac.getBean(PersistenceInjectionTests.DefaultPublicPersistenceContextSetter.class.getName())));
        Assert.assertSame(mockEm2, bean.em);
    }

    @Test
    public void testPrivatePersistenceUnitField() {
        GenericApplicationContext gac = new GenericApplicationContext();
        gac.getDefaultListableBeanFactory().registerSingleton("entityManagerFactory", AbstractEntityManagerFactoryBeanTests.mockEmf);
        gac.registerBeanDefinition("annotationProcessor", new RootBeanDefinition(PersistenceAnnotationBeanPostProcessor.class));
        gac.registerBeanDefinition(PersistenceInjectionTests.DefaultPrivatePersistenceUnitField.class.getName(), new RootBeanDefinition(PersistenceInjectionTests.DefaultPrivatePersistenceUnitField.class));
        gac.refresh();
        PersistenceInjectionTests.DefaultPrivatePersistenceUnitField bean = ((PersistenceInjectionTests.DefaultPrivatePersistenceUnitField) (gac.getBean(PersistenceInjectionTests.DefaultPrivatePersistenceUnitField.class.getName())));
        Assert.assertSame(AbstractEntityManagerFactoryBeanTests.mockEmf, bean.emf);
    }

    @Test
    public void testPublicPersistenceUnitSetter() {
        GenericApplicationContext gac = new GenericApplicationContext();
        gac.getDefaultListableBeanFactory().registerSingleton("entityManagerFactory", AbstractEntityManagerFactoryBeanTests.mockEmf);
        gac.registerBeanDefinition("annotationProcessor", new RootBeanDefinition(PersistenceAnnotationBeanPostProcessor.class));
        gac.registerBeanDefinition(PersistenceInjectionTests.DefaultPublicPersistenceUnitSetter.class.getName(), new RootBeanDefinition(PersistenceInjectionTests.DefaultPublicPersistenceUnitSetter.class));
        gac.refresh();
        PersistenceInjectionTests.DefaultPublicPersistenceUnitSetter bean = ((PersistenceInjectionTests.DefaultPublicPersistenceUnitSetter) (gac.getBean(PersistenceInjectionTests.DefaultPublicPersistenceUnitSetter.class.getName())));
        Assert.assertSame(AbstractEntityManagerFactoryBeanTests.mockEmf, bean.emf);
    }

    @Test
    public void testPublicPersistenceUnitSetterWithOverriding() {
        EntityManagerFactory mockEmf2 = Mockito.mock(EntityManagerFactory.class);
        GenericApplicationContext gac = new GenericApplicationContext();
        gac.getDefaultListableBeanFactory().registerSingleton("entityManagerFactory", AbstractEntityManagerFactoryBeanTests.mockEmf);
        gac.registerBeanDefinition("annotationProcessor", new RootBeanDefinition(PersistenceAnnotationBeanPostProcessor.class));
        RootBeanDefinition bd = new RootBeanDefinition(PersistenceInjectionTests.DefaultPublicPersistenceUnitSetter.class);
        bd.getPropertyValues().add("emf", mockEmf2);
        gac.registerBeanDefinition(PersistenceInjectionTests.DefaultPublicPersistenceUnitSetter.class.getName(), bd);
        gac.refresh();
        PersistenceInjectionTests.DefaultPublicPersistenceUnitSetter bean = ((PersistenceInjectionTests.DefaultPublicPersistenceUnitSetter) (gac.getBean(PersistenceInjectionTests.DefaultPublicPersistenceUnitSetter.class.getName())));
        Assert.assertSame(mockEmf2, bean.emf);
    }

    @Test
    public void testPublicPersistenceUnitSetterWithUnitIdentifiedThroughBeanName() {
        EntityManagerFactory mockEmf2 = Mockito.mock(EntityManagerFactory.class);
        GenericApplicationContext gac = new GenericApplicationContext();
        gac.getDefaultListableBeanFactory().registerSingleton("entityManagerFactory", AbstractEntityManagerFactoryBeanTests.mockEmf);
        gac.getDefaultListableBeanFactory().registerSingleton("entityManagerFactory2", mockEmf2);
        gac.registerAlias("entityManagerFactory2", "Person");
        RootBeanDefinition processorDef = new RootBeanDefinition(PersistenceAnnotationBeanPostProcessor.class);
        processorDef.getPropertyValues().add("defaultPersistenceUnitName", "entityManagerFactory");
        gac.registerBeanDefinition("annotationProcessor", processorDef);
        gac.registerBeanDefinition(PersistenceInjectionTests.DefaultPublicPersistenceUnitSetter.class.getName(), new RootBeanDefinition(PersistenceInjectionTests.DefaultPublicPersistenceUnitSetter.class));
        gac.registerBeanDefinition(PersistenceInjectionTests.DefaultPublicPersistenceUnitSetterNamedPerson.class.getName(), new RootBeanDefinition(PersistenceInjectionTests.DefaultPublicPersistenceUnitSetterNamedPerson.class));
        gac.refresh();
        PersistenceInjectionTests.DefaultPublicPersistenceUnitSetter bean = ((PersistenceInjectionTests.DefaultPublicPersistenceUnitSetter) (gac.getBean(PersistenceInjectionTests.DefaultPublicPersistenceUnitSetter.class.getName())));
        PersistenceInjectionTests.DefaultPublicPersistenceUnitSetterNamedPerson bean2 = ((PersistenceInjectionTests.DefaultPublicPersistenceUnitSetterNamedPerson) (gac.getBean(PersistenceInjectionTests.DefaultPublicPersistenceUnitSetterNamedPerson.class.getName())));
        Assert.assertSame(AbstractEntityManagerFactoryBeanTests.mockEmf, bean.emf);
        Assert.assertSame(mockEmf2, bean2.emf);
    }

    @Test
    public void testPublicPersistenceUnitSetterWithMultipleUnitsIdentifiedThroughUnitName() {
        PersistenceInjectionTests.EntityManagerFactoryWithInfo mockEmf2 = Mockito.mock(PersistenceInjectionTests.EntityManagerFactoryWithInfo.class);
        BDDMockito.given(getPersistenceUnitName()).willReturn("Person");
        GenericApplicationContext gac = new GenericApplicationContext();
        gac.getDefaultListableBeanFactory().registerSingleton("entityManagerFactory", AbstractEntityManagerFactoryBeanTests.mockEmf);
        gac.getDefaultListableBeanFactory().registerSingleton("entityManagerFactory2", mockEmf2);
        RootBeanDefinition processorDef = new RootBeanDefinition(PersistenceAnnotationBeanPostProcessor.class);
        processorDef.getPropertyValues().add("defaultPersistenceUnitName", "entityManagerFactory");
        gac.registerBeanDefinition("annotationProcessor", processorDef);
        gac.registerBeanDefinition(PersistenceInjectionTests.DefaultPublicPersistenceUnitSetter.class.getName(), new RootBeanDefinition(PersistenceInjectionTests.DefaultPublicPersistenceUnitSetter.class));
        gac.registerBeanDefinition(PersistenceInjectionTests.DefaultPublicPersistenceUnitSetterNamedPerson.class.getName(), new RootBeanDefinition(PersistenceInjectionTests.DefaultPublicPersistenceUnitSetterNamedPerson.class));
        gac.refresh();
        PersistenceInjectionTests.DefaultPublicPersistenceUnitSetter bean = ((PersistenceInjectionTests.DefaultPublicPersistenceUnitSetter) (gac.getBean(PersistenceInjectionTests.DefaultPublicPersistenceUnitSetter.class.getName())));
        PersistenceInjectionTests.DefaultPublicPersistenceUnitSetterNamedPerson bean2 = ((PersistenceInjectionTests.DefaultPublicPersistenceUnitSetterNamedPerson) (gac.getBean(PersistenceInjectionTests.DefaultPublicPersistenceUnitSetterNamedPerson.class.getName())));
        Assert.assertSame(AbstractEntityManagerFactoryBeanTests.mockEmf, bean.emf);
        Assert.assertSame(mockEmf2, bean2.emf);
    }

    @Test
    public void testPersistenceUnitsFromJndiWithDefaultUnit() {
        PersistenceInjectionTests.EntityManagerFactoryWithInfo mockEmf2 = Mockito.mock(PersistenceInjectionTests.EntityManagerFactoryWithInfo.class);
        Map<String, String> persistenceUnits = new HashMap<>();
        persistenceUnits.put("System", "pu1");
        persistenceUnits.put("Person", "pu2");
        ExpectedLookupTemplate jt = new ExpectedLookupTemplate();
        jt.addObject("java:comp/env/pu1", AbstractEntityManagerFactoryBeanTests.mockEmf);
        jt.addObject("java:comp/env/pu2", mockEmf2);
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        PersistenceAnnotationBeanPostProcessor bpp = new PersistenceAnnotationBeanPostProcessor();
        bpp.setPersistenceUnits(persistenceUnits);
        bpp.setDefaultPersistenceUnitName("System");
        bpp.setJndiTemplate(jt);
        bf.addBeanPostProcessor(bpp);
        bf.registerBeanDefinition(PersistenceInjectionTests.DefaultPublicPersistenceUnitSetter.class.getName(), new RootBeanDefinition(PersistenceInjectionTests.DefaultPublicPersistenceUnitSetter.class));
        bf.registerBeanDefinition(PersistenceInjectionTests.DefaultPublicPersistenceUnitSetterNamedPerson.class.getName(), new RootBeanDefinition(PersistenceInjectionTests.DefaultPublicPersistenceUnitSetterNamedPerson.class));
        PersistenceInjectionTests.DefaultPublicPersistenceUnitSetter bean = ((PersistenceInjectionTests.DefaultPublicPersistenceUnitSetter) (bf.getBean(PersistenceInjectionTests.DefaultPublicPersistenceUnitSetter.class.getName())));
        PersistenceInjectionTests.DefaultPublicPersistenceUnitSetterNamedPerson bean2 = ((PersistenceInjectionTests.DefaultPublicPersistenceUnitSetterNamedPerson) (bf.getBean(PersistenceInjectionTests.DefaultPublicPersistenceUnitSetterNamedPerson.class.getName())));
        Assert.assertSame(AbstractEntityManagerFactoryBeanTests.mockEmf, bean.emf);
        Assert.assertSame(mockEmf2, bean2.emf);
    }

    @Test
    public void testSinglePersistenceUnitFromJndi() {
        Map<String, String> persistenceUnits = new HashMap<>();
        persistenceUnits.put("Person", "pu1");
        ExpectedLookupTemplate jt = new ExpectedLookupTemplate();
        jt.addObject("java:comp/env/pu1", AbstractEntityManagerFactoryBeanTests.mockEmf);
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        PersistenceAnnotationBeanPostProcessor bpp = new PersistenceAnnotationBeanPostProcessor();
        bpp.setPersistenceUnits(persistenceUnits);
        bpp.setJndiTemplate(jt);
        bf.addBeanPostProcessor(bpp);
        bf.registerBeanDefinition(PersistenceInjectionTests.DefaultPublicPersistenceUnitSetter.class.getName(), new RootBeanDefinition(PersistenceInjectionTests.DefaultPublicPersistenceUnitSetter.class));
        bf.registerBeanDefinition(PersistenceInjectionTests.DefaultPublicPersistenceUnitSetterNamedPerson.class.getName(), new RootBeanDefinition(PersistenceInjectionTests.DefaultPublicPersistenceUnitSetterNamedPerson.class));
        PersistenceInjectionTests.DefaultPublicPersistenceUnitSetter bean = ((PersistenceInjectionTests.DefaultPublicPersistenceUnitSetter) (bf.getBean(PersistenceInjectionTests.DefaultPublicPersistenceUnitSetter.class.getName())));
        PersistenceInjectionTests.DefaultPublicPersistenceUnitSetterNamedPerson bean2 = ((PersistenceInjectionTests.DefaultPublicPersistenceUnitSetterNamedPerson) (bf.getBean(PersistenceInjectionTests.DefaultPublicPersistenceUnitSetterNamedPerson.class.getName())));
        Assert.assertSame(AbstractEntityManagerFactoryBeanTests.mockEmf, bean.emf);
        Assert.assertSame(AbstractEntityManagerFactoryBeanTests.mockEmf, bean2.emf);
    }

    @Test
    public void testPersistenceContextsFromJndi() {
        EntityManager mockEm = Mockito.mock(EntityManager.class);
        EntityManager mockEm2 = Mockito.mock(EntityManager.class);
        EntityManager mockEm3 = Mockito.mock(EntityManager.class);
        Map<String, String> persistenceContexts = new HashMap<>();
        persistenceContexts.put("", "pc1");
        persistenceContexts.put("Person", "pc2");
        Map<String, String> extendedPersistenceContexts = new HashMap<>();
        extendedPersistenceContexts.put("", "pc3");
        ExpectedLookupTemplate jt = new ExpectedLookupTemplate();
        jt.addObject("java:comp/env/pc1", mockEm);
        jt.addObject("java:comp/env/pc2", mockEm2);
        jt.addObject("java:comp/env/pc3", mockEm3);
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        PersistenceAnnotationBeanPostProcessor bpp = new PersistenceAnnotationBeanPostProcessor();
        bpp.setPersistenceContexts(persistenceContexts);
        bpp.setExtendedPersistenceContexts(extendedPersistenceContexts);
        bpp.setJndiTemplate(jt);
        bf.addBeanPostProcessor(bpp);
        bf.registerBeanDefinition(PersistenceInjectionTests.DefaultPrivatePersistenceContextField.class.getName(), new RootBeanDefinition(PersistenceInjectionTests.DefaultPrivatePersistenceContextField.class));
        bf.registerBeanDefinition(PersistenceInjectionTests.DefaultPrivatePersistenceContextFieldNamedPerson.class.getName(), new RootBeanDefinition(PersistenceInjectionTests.DefaultPrivatePersistenceContextFieldNamedPerson.class));
        bf.registerBeanDefinition(PersistenceInjectionTests.DefaultPublicPersistenceContextSetter.class.getName(), new RootBeanDefinition(PersistenceInjectionTests.DefaultPublicPersistenceContextSetter.class));
        PersistenceInjectionTests.DefaultPrivatePersistenceContextField bean1 = ((PersistenceInjectionTests.DefaultPrivatePersistenceContextField) (bf.getBean(PersistenceInjectionTests.DefaultPrivatePersistenceContextField.class.getName())));
        PersistenceInjectionTests.DefaultPrivatePersistenceContextFieldNamedPerson bean2 = ((PersistenceInjectionTests.DefaultPrivatePersistenceContextFieldNamedPerson) (bf.getBean(PersistenceInjectionTests.DefaultPrivatePersistenceContextFieldNamedPerson.class.getName())));
        PersistenceInjectionTests.DefaultPublicPersistenceContextSetter bean3 = ((PersistenceInjectionTests.DefaultPublicPersistenceContextSetter) (bf.getBean(PersistenceInjectionTests.DefaultPublicPersistenceContextSetter.class.getName())));
        Assert.assertSame(mockEm, bean1.em);
        Assert.assertSame(mockEm2, bean2.em);
        Assert.assertSame(mockEm3, bean3.em);
    }

    @Test
    public void testPersistenceContextsFromJndiWithDefaultUnit() {
        EntityManager mockEm = Mockito.mock(EntityManager.class);
        EntityManager mockEm2 = Mockito.mock(EntityManager.class);
        EntityManager mockEm3 = Mockito.mock(EntityManager.class);
        Map<String, String> persistenceContexts = new HashMap<>();
        persistenceContexts.put("System", "pc1");
        persistenceContexts.put("Person", "pc2");
        Map<String, String> extendedPersistenceContexts = new HashMap<>();
        extendedPersistenceContexts.put("System", "pc3");
        ExpectedLookupTemplate jt = new ExpectedLookupTemplate();
        jt.addObject("java:comp/env/pc1", mockEm);
        jt.addObject("java:comp/env/pc2", mockEm2);
        jt.addObject("java:comp/env/pc3", mockEm3);
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        PersistenceAnnotationBeanPostProcessor bpp = new PersistenceAnnotationBeanPostProcessor();
        bpp.setPersistenceContexts(persistenceContexts);
        bpp.setExtendedPersistenceContexts(extendedPersistenceContexts);
        bpp.setDefaultPersistenceUnitName("System");
        bpp.setJndiTemplate(jt);
        bf.addBeanPostProcessor(bpp);
        bf.registerBeanDefinition(PersistenceInjectionTests.DefaultPrivatePersistenceContextField.class.getName(), new RootBeanDefinition(PersistenceInjectionTests.DefaultPrivatePersistenceContextField.class));
        bf.registerBeanDefinition(PersistenceInjectionTests.DefaultPrivatePersistenceContextFieldNamedPerson.class.getName(), new RootBeanDefinition(PersistenceInjectionTests.DefaultPrivatePersistenceContextFieldNamedPerson.class));
        bf.registerBeanDefinition(PersistenceInjectionTests.DefaultPublicPersistenceContextSetter.class.getName(), new RootBeanDefinition(PersistenceInjectionTests.DefaultPublicPersistenceContextSetter.class));
        PersistenceInjectionTests.DefaultPrivatePersistenceContextField bean1 = ((PersistenceInjectionTests.DefaultPrivatePersistenceContextField) (bf.getBean(PersistenceInjectionTests.DefaultPrivatePersistenceContextField.class.getName())));
        PersistenceInjectionTests.DefaultPrivatePersistenceContextFieldNamedPerson bean2 = ((PersistenceInjectionTests.DefaultPrivatePersistenceContextFieldNamedPerson) (bf.getBean(PersistenceInjectionTests.DefaultPrivatePersistenceContextFieldNamedPerson.class.getName())));
        PersistenceInjectionTests.DefaultPublicPersistenceContextSetter bean3 = ((PersistenceInjectionTests.DefaultPublicPersistenceContextSetter) (bf.getBean(PersistenceInjectionTests.DefaultPublicPersistenceContextSetter.class.getName())));
        Assert.assertSame(mockEm, bean1.em);
        Assert.assertSame(mockEm2, bean2.em);
        Assert.assertSame(mockEm3, bean3.em);
    }

    @Test
    public void testSinglePersistenceContextFromJndi() {
        EntityManager mockEm = Mockito.mock(EntityManager.class);
        EntityManager mockEm2 = Mockito.mock(EntityManager.class);
        Map<String, String> persistenceContexts = new HashMap<>();
        persistenceContexts.put("System", "pc1");
        Map<String, String> extendedPersistenceContexts = new HashMap<>();
        extendedPersistenceContexts.put("System", "pc2");
        ExpectedLookupTemplate jt = new ExpectedLookupTemplate();
        jt.addObject("java:comp/env/pc1", mockEm);
        jt.addObject("java:comp/env/pc2", mockEm2);
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        PersistenceAnnotationBeanPostProcessor bpp = new PersistenceAnnotationBeanPostProcessor();
        bpp.setPersistenceContexts(persistenceContexts);
        bpp.setExtendedPersistenceContexts(extendedPersistenceContexts);
        bpp.setJndiTemplate(jt);
        bf.addBeanPostProcessor(bpp);
        bf.registerBeanDefinition(PersistenceInjectionTests.DefaultPrivatePersistenceContextField.class.getName(), new RootBeanDefinition(PersistenceInjectionTests.DefaultPrivatePersistenceContextField.class));
        bf.registerBeanDefinition(PersistenceInjectionTests.DefaultPublicPersistenceContextSetter.class.getName(), new RootBeanDefinition(PersistenceInjectionTests.DefaultPublicPersistenceContextSetter.class));
        PersistenceInjectionTests.DefaultPrivatePersistenceContextField bean1 = ((PersistenceInjectionTests.DefaultPrivatePersistenceContextField) (bf.getBean(PersistenceInjectionTests.DefaultPrivatePersistenceContextField.class.getName())));
        PersistenceInjectionTests.DefaultPublicPersistenceContextSetter bean2 = ((PersistenceInjectionTests.DefaultPublicPersistenceContextSetter) (bf.getBean(PersistenceInjectionTests.DefaultPublicPersistenceContextSetter.class.getName())));
        Assert.assertSame(mockEm, bean1.em);
        Assert.assertSame(mockEm2, bean2.em);
    }

    @Test
    public void testFieldOfWrongTypeAnnotatedWithPersistenceUnit() {
        PersistenceAnnotationBeanPostProcessor pabpp = new PersistenceAnnotationBeanPostProcessor();
        try {
            pabpp.postProcessProperties(null, new PersistenceInjectionTests.FieldOfWrongTypeAnnotatedWithPersistenceUnit(), "bean");
            Assert.fail("Can't inject this field");
        } catch (IllegalStateException ex) {
            // Ok
        }
    }

    @Test
    public void testSetterOfWrongTypeAnnotatedWithPersistenceUnit() {
        PersistenceAnnotationBeanPostProcessor pabpp = new PersistenceAnnotationBeanPostProcessor();
        try {
            pabpp.postProcessProperties(null, new PersistenceInjectionTests.SetterOfWrongTypeAnnotatedWithPersistenceUnit(), "bean");
            Assert.fail("Can't inject this setter");
        } catch (IllegalStateException ex) {
            // Ok
        }
    }

    @Test
    public void testSetterWithNoArgs() {
        PersistenceAnnotationBeanPostProcessor pabpp = new PersistenceAnnotationBeanPostProcessor();
        try {
            pabpp.postProcessProperties(null, new PersistenceInjectionTests.SetterWithNoArgs(), "bean");
            Assert.fail("Can't inject this setter");
        } catch (IllegalStateException ex) {
            // Ok
        }
    }

    @Test
    public void testNoPropertiesPassedIn() {
        EntityManager mockEm = Mockito.mock(EntityManager.class);
        BDDMockito.given(AbstractEntityManagerFactoryBeanTests.mockEmf.createEntityManager()).willReturn(mockEm);
        PersistenceAnnotationBeanPostProcessor pabpp = new PersistenceInjectionTests.MockPersistenceAnnotationBeanPostProcessor();
        PersistenceInjectionTests.DefaultPrivatePersistenceContextFieldExtended dppcf = new PersistenceInjectionTests.DefaultPrivatePersistenceContextFieldExtended();
        pabpp.postProcessProperties(null, dppcf, "bean");
        Assert.assertNotNull(dppcf.em);
    }

    @Test
    public void testPropertiesPassedIn() {
        Properties props = new Properties();
        props.put("foo", "bar");
        EntityManager mockEm = Mockito.mock(EntityManager.class);
        BDDMockito.given(AbstractEntityManagerFactoryBeanTests.mockEmf.createEntityManager(props)).willReturn(mockEm);
        PersistenceAnnotationBeanPostProcessor pabpp = new PersistenceInjectionTests.MockPersistenceAnnotationBeanPostProcessor();
        PersistenceInjectionTests.DefaultPrivatePersistenceContextFieldExtendedWithProps dppcf = new PersistenceInjectionTests.DefaultPrivatePersistenceContextFieldExtendedWithProps();
        pabpp.postProcessProperties(null, dppcf, "bean");
        Assert.assertNotNull(dppcf.em);
    }

    @Test
    public void testPropertiesForTransactionalEntityManager() {
        Properties props = new Properties();
        props.put("foo", "bar");
        EntityManager em = Mockito.mock(EntityManager.class);
        BDDMockito.given(AbstractEntityManagerFactoryBeanTests.mockEmf.createEntityManager(props)).willReturn(em);
        BDDMockito.given(em.getDelegate()).willReturn(new Object());
        BDDMockito.given(em.isOpen()).willReturn(true);
        PersistenceAnnotationBeanPostProcessor pabpp = new PersistenceInjectionTests.MockPersistenceAnnotationBeanPostProcessor();
        PersistenceInjectionTests.DefaultPrivatePersistenceContextFieldWithProperties transactionalField = new PersistenceInjectionTests.DefaultPrivatePersistenceContextFieldWithProperties();
        pabpp.postProcessProperties(null, transactionalField, "bean");
        Assert.assertNotNull(transactionalField.em);
        Assert.assertNotNull(transactionalField.em.getDelegate());
        Mockito.verify(em).close();
    }

    /**
     * Binds an EMF to the thread and tests if EM with different properties
     * generate new EMs or not.
     */
    @Test
    public void testPropertiesForSharedEntityManager1() {
        Properties props = new Properties();
        props.put("foo", "bar");
        EntityManager em = Mockito.mock(EntityManager.class);
        // only one call made  - the first EM definition wins (in this case the one w/ the properties)
        BDDMockito.given(AbstractEntityManagerFactoryBeanTests.mockEmf.createEntityManager(props)).willReturn(em);
        BDDMockito.given(em.getDelegate()).willReturn(new Object());
        BDDMockito.given(em.isOpen()).willReturn(true);
        PersistenceAnnotationBeanPostProcessor pabpp = new PersistenceInjectionTests.MockPersistenceAnnotationBeanPostProcessor();
        PersistenceInjectionTests.DefaultPrivatePersistenceContextFieldWithProperties transactionalFieldWithProperties = new PersistenceInjectionTests.DefaultPrivatePersistenceContextFieldWithProperties();
        PersistenceInjectionTests.DefaultPrivatePersistenceContextField transactionalField = new PersistenceInjectionTests.DefaultPrivatePersistenceContextField();
        pabpp.postProcessProperties(null, transactionalFieldWithProperties, "bean1");
        pabpp.postProcessProperties(null, transactionalField, "bean2");
        Assert.assertNotNull(transactionalFieldWithProperties.em);
        Assert.assertNotNull(transactionalField.em);
        // the EM w/ properties will be created
        Assert.assertNotNull(transactionalFieldWithProperties.em.getDelegate());
        // bind em to the thread now since it's created
        try {
            TransactionSynchronizationManager.bindResource(AbstractEntityManagerFactoryBeanTests.mockEmf, new org.springframework.orm.jpa.EntityManagerHolder(em));
            Assert.assertNotNull(transactionalField.em.getDelegate());
            Mockito.verify(em).close();
        } finally {
            TransactionSynchronizationManager.unbindResource(AbstractEntityManagerFactoryBeanTests.mockEmf);
        }
    }

    @Test
    public void testPropertiesForSharedEntityManager2() {
        Properties props = new Properties();
        props.put("foo", "bar");
        EntityManager em = Mockito.mock(EntityManager.class);
        // only one call made  - the first EM definition wins (in this case the one w/o the properties)
        BDDMockito.given(AbstractEntityManagerFactoryBeanTests.mockEmf.createEntityManager()).willReturn(em);
        BDDMockito.given(em.getDelegate()).willReturn(new Object(), 2);
        BDDMockito.given(em.isOpen()).willReturn(true);
        PersistenceAnnotationBeanPostProcessor pabpp = new PersistenceInjectionTests.MockPersistenceAnnotationBeanPostProcessor();
        PersistenceInjectionTests.DefaultPrivatePersistenceContextFieldWithProperties transactionalFieldWithProperties = new PersistenceInjectionTests.DefaultPrivatePersistenceContextFieldWithProperties();
        PersistenceInjectionTests.DefaultPrivatePersistenceContextField transactionalField = new PersistenceInjectionTests.DefaultPrivatePersistenceContextField();
        pabpp.postProcessProperties(null, transactionalFieldWithProperties, "bean1");
        pabpp.postProcessProperties(null, transactionalField, "bean2");
        Assert.assertNotNull(transactionalFieldWithProperties.em);
        Assert.assertNotNull(transactionalField.em);
        // the EM w/o properties will be created
        Assert.assertNotNull(transactionalField.em.getDelegate());
        // bind em to the thread now since it's created
        try {
            TransactionSynchronizationManager.bindResource(AbstractEntityManagerFactoryBeanTests.mockEmf, new org.springframework.orm.jpa.EntityManagerHolder(em));
            Assert.assertNotNull(transactionalFieldWithProperties.em.getDelegate());
            Mockito.verify(em).close();
        } finally {
            TransactionSynchronizationManager.unbindResource(AbstractEntityManagerFactoryBeanTests.mockEmf);
        }
    }

    @SuppressWarnings("serial")
    private static class MockPersistenceAnnotationBeanPostProcessor extends PersistenceAnnotationBeanPostProcessor {
        @Override
        protected EntityManagerFactory findEntityManagerFactory(@Nullable
        String emfName, String requestingBeanName) {
            return AbstractEntityManagerFactoryBeanTests.mockEmf;
        }
    }

    public static class DefaultPrivatePersistenceContextField {
        @PersistenceContext
        private EntityManager em;
    }

    public static class DefaultVendorSpecificPrivatePersistenceContextField {
        @PersistenceContext
        @SuppressWarnings("deprecation")
        private HibernateEntityManager em;
    }

    @SuppressWarnings("rawtypes")
    public static class FactoryBeanWithPersistenceContextField implements FactoryBean {
        @PersistenceContext
        private EntityManager em;

        @Override
        public Object getObject() throws Exception {
            return null;
        }

        @Override
        public Class getObjectType() {
            return null;
        }

        @Override
        public boolean isSingleton() {
            return true;
        }
    }

    public static class DefaultPrivatePersistenceContextFieldNamedPerson {
        @PersistenceContext(unitName = "Person")
        private EntityManager em;
    }

    public static class DefaultPrivatePersistenceContextFieldWithProperties {
        @PersistenceContext(properties = { @PersistenceProperty(name = "foo", value = "bar") })
        private EntityManager em;
    }

    @Repository
    @SuppressWarnings("serial")
    public static class DefaultPublicPersistenceContextSetter implements Serializable {
        private EntityManager em;

        @PersistenceContext(type = PersistenceContextType.EXTENDED)
        public void setEntityManager(EntityManager em) {
            if ((this.em) != null) {
                throw new IllegalStateException("Already called");
            }
            this.em = em;
        }

        public EntityManager getEntityManager() {
            return em;
        }
    }

    @SuppressWarnings("serial")
    static class PublicPersistenceContextSetterOnNonPublicClass extends PersistenceInjectionTests.DefaultPublicPersistenceContextSetter {
        @Override
        @PersistenceContext(unitName = "unit2", type = PersistenceContextType.EXTENDED)
        public void setEntityManager(EntityManager em) {
            super.setEntityManager(em);
        }
    }

    @SuppressWarnings("serial")
    public static class SpecificPublicPersistenceContextSetter extends PersistenceInjectionTests.PublicPersistenceContextSetterOnNonPublicClass {}

    public static class DefaultPrivatePersistenceUnitField {
        @PersistenceUnit
        private EntityManagerFactory emf;
    }

    public static class DefaultPublicPersistenceUnitSetter {
        private EntityManagerFactory emf;

        @PersistenceUnit
        public void setEmf(EntityManagerFactory emf) {
            if ((this.emf) != null) {
                throw new IllegalStateException("Already called");
            }
            this.emf = emf;
        }

        public EntityManagerFactory getEmf() {
            return emf;
        }
    }

    @Repository
    public static class DefaultPublicPersistenceUnitSetterNamedPerson {
        private EntityManagerFactory emf;

        @PersistenceUnit(unitName = "Person")
        public void setEmf(EntityManagerFactory emf) {
            this.emf = emf;
        }

        public EntityManagerFactory getEntityManagerFactory() {
            return emf;
        }
    }

    public static class FieldOfWrongTypeAnnotatedWithPersistenceUnit {
        @PersistenceUnit
        public String thisFieldIsOfTheWrongType;
    }

    public static class SetterOfWrongTypeAnnotatedWithPersistenceUnit {
        @PersistenceUnit
        @SuppressWarnings("rawtypes")
        public void setSomething(Comparable c) {
        }
    }

    public static class SetterWithNoArgs {
        @PersistenceUnit
        public void setSomething() {
        }
    }

    public static class DefaultPrivatePersistenceContextFieldExtended {
        @PersistenceContext(type = PersistenceContextType.EXTENDED)
        private EntityManager em;
    }

    public static class DefaultPrivatePersistenceContextFieldExtendedWithProps {
        @PersistenceContext(type = PersistenceContextType.EXTENDED, properties = { @PersistenceProperty(name = "foo", value = "bar") })
        private EntityManager em;
    }

    private interface EntityManagerFactoryWithInfo extends EntityManagerFactory , EntityManagerFactoryInfo {}

    @SuppressWarnings("serial")
    private static class DummyInvocationHandler implements Serializable , InvocationHandler {
        public static boolean closed;

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if ("isOpen".equals(method.getName())) {
                return true;
            }
            if ("close".equals(method.getName())) {
                PersistenceInjectionTests.DummyInvocationHandler.closed = true;
                return null;
            }
            if ("toString".equals(method.getName())) {
                return "";
            }
            throw new IllegalStateException();
        }
    }
}

