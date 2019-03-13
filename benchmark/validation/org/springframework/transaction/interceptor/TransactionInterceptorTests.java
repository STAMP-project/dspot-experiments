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
package org.springframework.transaction.interceptor;


import java.io.Serializable;
import java.util.Properties;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.lang.Nullable;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.util.SerializationTestUtils;


/**
 * Mock object based tests for TransactionInterceptor.
 *
 * @author Rod Johnson
 * @since 16.03.2003
 */
public class TransactionInterceptorTests extends AbstractTransactionAspectTests {
    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    /**
     * A TransactionInterceptor should be serializable if its
     * PlatformTransactionManager is.
     */
    @Test
    public void serializableWithAttributeProperties() throws Exception {
        TransactionInterceptor ti = new TransactionInterceptor();
        Properties props = new Properties();
        props.setProperty("methodName", "PROPAGATION_REQUIRED");
        ti.setTransactionAttributes(props);
        PlatformTransactionManager ptm = new TransactionInterceptorTests.SerializableTransactionManager();
        ti.setTransactionManager(ptm);
        ti = ((TransactionInterceptor) (SerializationTestUtils.serializeAndDeserialize(ti)));
        // Check that logger survived deserialization
        Assert.assertNotNull(ti.logger);
        Assert.assertTrue(((ti.getTransactionManager()) instanceof TransactionInterceptorTests.SerializableTransactionManager));
        Assert.assertNotNull(ti.getTransactionAttributeSource());
    }

    @Test
    public void serializableWithCompositeSource() throws Exception {
        NameMatchTransactionAttributeSource tas1 = new NameMatchTransactionAttributeSource();
        Properties props = new Properties();
        props.setProperty("methodName", "PROPAGATION_REQUIRED");
        tas1.setProperties(props);
        NameMatchTransactionAttributeSource tas2 = new NameMatchTransactionAttributeSource();
        props = new Properties();
        props.setProperty("otherMethodName", "PROPAGATION_REQUIRES_NEW");
        tas2.setProperties(props);
        TransactionInterceptor ti = new TransactionInterceptor();
        ti.setTransactionAttributeSources(tas1, tas2);
        PlatformTransactionManager ptm = new TransactionInterceptorTests.SerializableTransactionManager();
        ti.setTransactionManager(ptm);
        ti = ((TransactionInterceptor) (SerializationTestUtils.serializeAndDeserialize(ti)));
        Assert.assertTrue(((ti.getTransactionManager()) instanceof TransactionInterceptorTests.SerializableTransactionManager));
        Assert.assertTrue(((ti.getTransactionAttributeSource()) instanceof CompositeTransactionAttributeSource));
        CompositeTransactionAttributeSource ctas = ((CompositeTransactionAttributeSource) (ti.getTransactionAttributeSource()));
        Assert.assertTrue(((ctas.getTransactionAttributeSources()[0]) instanceof NameMatchTransactionAttributeSource));
        Assert.assertTrue(((ctas.getTransactionAttributeSources()[1]) instanceof NameMatchTransactionAttributeSource));
    }

    @Test
    public void determineTransactionManagerWithNoBeanFactory() {
        PlatformTransactionManager transactionManager = Mockito.mock(PlatformTransactionManager.class);
        TransactionInterceptor ti = transactionInterceptorWithTransactionManager(transactionManager, null);
        Assert.assertSame(transactionManager, ti.determineTransactionManager(new DefaultTransactionAttribute()));
    }

    @Test
    public void determineTransactionManagerWithNoBeanFactoryAndNoTransactionAttribute() {
        PlatformTransactionManager transactionManager = Mockito.mock(PlatformTransactionManager.class);
        TransactionInterceptor ti = transactionInterceptorWithTransactionManager(transactionManager, null);
        Assert.assertSame(transactionManager, ti.determineTransactionManager(null));
    }

    @Test
    public void determineTransactionManagerWithNoTransactionAttribute() {
        BeanFactory beanFactory = Mockito.mock(BeanFactory.class);
        TransactionInterceptor ti = simpleTransactionInterceptor(beanFactory);
        Assert.assertNull(ti.determineTransactionManager(null));
    }

    @Test
    public void determineTransactionManagerWithQualifierUnknown() {
        BeanFactory beanFactory = Mockito.mock(BeanFactory.class);
        TransactionInterceptor ti = simpleTransactionInterceptor(beanFactory);
        DefaultTransactionAttribute attribute = new DefaultTransactionAttribute();
        attribute.setQualifier("fooTransactionManager");
        thrown.expect(NoSuchBeanDefinitionException.class);
        thrown.expectMessage("'fooTransactionManager'");
        ti.determineTransactionManager(attribute);
    }

    @Test
    public void determineTransactionManagerWithQualifierAndDefault() {
        BeanFactory beanFactory = Mockito.mock(BeanFactory.class);
        PlatformTransactionManager transactionManager = Mockito.mock(PlatformTransactionManager.class);
        TransactionInterceptor ti = transactionInterceptorWithTransactionManager(transactionManager, beanFactory);
        PlatformTransactionManager fooTransactionManager = associateTransactionManager(beanFactory, "fooTransactionManager");
        DefaultTransactionAttribute attribute = new DefaultTransactionAttribute();
        attribute.setQualifier("fooTransactionManager");
        Assert.assertSame(fooTransactionManager, ti.determineTransactionManager(attribute));
    }

    @Test
    public void determineTransactionManagerWithQualifierAndDefaultName() {
        BeanFactory beanFactory = Mockito.mock(BeanFactory.class);
        associateTransactionManager(beanFactory, "defaultTransactionManager");
        TransactionInterceptor ti = transactionInterceptorWithTransactionManagerName("defaultTransactionManager", beanFactory);
        PlatformTransactionManager fooTransactionManager = associateTransactionManager(beanFactory, "fooTransactionManager");
        DefaultTransactionAttribute attribute = new DefaultTransactionAttribute();
        attribute.setQualifier("fooTransactionManager");
        Assert.assertSame(fooTransactionManager, ti.determineTransactionManager(attribute));
    }

    @Test
    public void determineTransactionManagerWithEmptyQualifierAndDefaultName() {
        BeanFactory beanFactory = Mockito.mock(BeanFactory.class);
        PlatformTransactionManager defaultTransactionManager = associateTransactionManager(beanFactory, "defaultTransactionManager");
        TransactionInterceptor ti = transactionInterceptorWithTransactionManagerName("defaultTransactionManager", beanFactory);
        DefaultTransactionAttribute attribute = new DefaultTransactionAttribute();
        attribute.setQualifier("");
        Assert.assertSame(defaultTransactionManager, ti.determineTransactionManager(attribute));
    }

    @Test
    public void determineTransactionManagerWithQualifierSeveralTimes() {
        BeanFactory beanFactory = Mockito.mock(BeanFactory.class);
        TransactionInterceptor ti = simpleTransactionInterceptor(beanFactory);
        PlatformTransactionManager txManager = associateTransactionManager(beanFactory, "fooTransactionManager");
        DefaultTransactionAttribute attribute = new DefaultTransactionAttribute();
        attribute.setQualifier("fooTransactionManager");
        PlatformTransactionManager actual = ti.determineTransactionManager(attribute);
        Assert.assertSame(txManager, actual);
        // Call again, should be cached
        PlatformTransactionManager actual2 = ti.determineTransactionManager(attribute);
        Assert.assertSame(txManager, actual2);
        Mockito.verify(beanFactory, Mockito.times(1)).containsBean("fooTransactionManager");
        Mockito.verify(beanFactory, Mockito.times(1)).getBean("fooTransactionManager", PlatformTransactionManager.class);
    }

    @Test
    public void determineTransactionManagerWithBeanNameSeveralTimes() {
        BeanFactory beanFactory = Mockito.mock(BeanFactory.class);
        TransactionInterceptor ti = transactionInterceptorWithTransactionManagerName("fooTransactionManager", beanFactory);
        PlatformTransactionManager txManager = associateTransactionManager(beanFactory, "fooTransactionManager");
        DefaultTransactionAttribute attribute = new DefaultTransactionAttribute();
        PlatformTransactionManager actual = ti.determineTransactionManager(attribute);
        Assert.assertSame(txManager, actual);
        // Call again, should be cached
        PlatformTransactionManager actual2 = ti.determineTransactionManager(attribute);
        Assert.assertSame(txManager, actual2);
        Mockito.verify(beanFactory, Mockito.times(1)).getBean("fooTransactionManager", PlatformTransactionManager.class);
    }

    @Test
    public void determineTransactionManagerDefaultSeveralTimes() {
        BeanFactory beanFactory = Mockito.mock(BeanFactory.class);
        TransactionInterceptor ti = simpleTransactionInterceptor(beanFactory);
        PlatformTransactionManager txManager = Mockito.mock(PlatformTransactionManager.class);
        BDDMockito.given(beanFactory.getBean(PlatformTransactionManager.class)).willReturn(txManager);
        DefaultTransactionAttribute attribute = new DefaultTransactionAttribute();
        PlatformTransactionManager actual = ti.determineTransactionManager(attribute);
        Assert.assertSame(txManager, actual);
        // Call again, should be cached
        PlatformTransactionManager actual2 = ti.determineTransactionManager(attribute);
        Assert.assertSame(txManager, actual2);
        Mockito.verify(beanFactory, Mockito.times(1)).getBean(PlatformTransactionManager.class);
    }

    /**
     * We won't use this: we just want to know it's serializable.
     */
    @SuppressWarnings("serial")
    public static class SerializableTransactionManager implements Serializable , PlatformTransactionManager {
        @Override
        public TransactionStatus getTransaction(@Nullable
        TransactionDefinition definition) throws TransactionException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void commit(TransactionStatus status) throws TransactionException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void rollback(TransactionStatus status) throws TransactionException {
            throw new UnsupportedOperationException();
        }
    }
}

