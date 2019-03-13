/**
 * Copyright 2008-2019 the original author or authors.
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
package org.springframework.data.jpa.repository.support;


import Key.CREATE_IF_NOT_FOUND;
import java.io.IOException;
import java.io.Serializable;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.metamodel.Metamodel;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.jpa.domain.sample.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.custom.CustomGenericJpaRepositoryFactory;
import org.springframework.data.jpa.repository.custom.UserCustomExtendedRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ClassUtils;


/**
 * Unit test for {@code JpaRepositoryFactory}.
 *
 * @author Oliver Gierke
 * @author Thomas Darimont
 * @author Jens Schauder
 */
@RunWith(MockitoJUnitRunner.Silent.class)
public class JpaRepositoryFactoryUnitTests {
    JpaRepositoryFactory factory;

    @Mock
    EntityManager entityManager;

    @Mock
    Metamodel metamodel;

    @Mock
    @SuppressWarnings("rawtypes")
    JpaEntityInformation entityInformation;

    @Mock
    EntityManagerFactory emf;

    /**
     * Assert that the instance created for the standard configuration is a valid {@code UserRepository}.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void setsUpBasicInstanceCorrectly() throws Exception {
        Assert.assertNotNull(factory.getRepository(JpaRepositoryFactoryUnitTests.SimpleSampleRepository.class));
    }

    @Test
    public void allowsCallingOfObjectMethods() {
        JpaRepositoryFactoryUnitTests.SimpleSampleRepository repository = factory.getRepository(JpaRepositoryFactoryUnitTests.SimpleSampleRepository.class);
        repository.hashCode();
        repository.toString();
        repository.equals(repository);
    }

    /**
     * Asserts that the factory recognized configured predicateExecutor classes that contain custom method but no custom
     * implementation could be found. Furthremore the exception has to contain the name of the predicateExecutor interface as for
     * a large predicateExecutor configuration it's hard to find out where this error occured.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void capturesMissingCustomImplementationAndProvidesInterfacename() throws Exception {
        try {
            factory.getRepository(JpaRepositoryFactoryUnitTests.SampleRepository.class);
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(e.getMessage().contains(JpaRepositoryFactoryUnitTests.SampleRepository.class.getName()));
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void handlesRuntimeExceptionsCorrectly() {
        JpaRepositoryFactoryUnitTests.SampleRepository repository = factory.getRepository(JpaRepositoryFactoryUnitTests.SampleRepository.class, new JpaRepositoryFactoryUnitTests.SampleCustomRepositoryImpl());
        repository.throwingRuntimeException();
    }

    @Test(expected = IOException.class)
    public void handlesCheckedExceptionsCorrectly() throws Exception {
        JpaRepositoryFactoryUnitTests.SampleRepository repository = factory.getRepository(JpaRepositoryFactoryUnitTests.SampleRepository.class, new JpaRepositoryFactoryUnitTests.SampleCustomRepositoryImpl());
        repository.throwingCheckedException();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void createsProxyWithCustomBaseClass() {
        JpaRepositoryFactory factory = new CustomGenericJpaRepositoryFactory(entityManager);
        factory.setQueryLookupStrategyKey(CREATE_IF_NOT_FOUND);
        UserCustomExtendedRepository repository = factory.getRepository(UserCustomExtendedRepository.class);
        repository.customMethod(1);
    }

    // DATAJPA-710, DATACMNS-542
    @Test
    public void usesConfiguredRepositoryBaseClass() {
        factory.setRepositoryBaseClass(JpaRepositoryFactoryUnitTests.CustomJpaRepository.class);
        JpaRepositoryFactoryUnitTests.SampleRepository repository = factory.getRepository(JpaRepositoryFactoryUnitTests.SampleRepository.class);
        Assert.assertEquals(JpaRepositoryFactoryUnitTests.CustomJpaRepository.class, getTargetClass());
    }

    // DATAJPA-819
    @Test
    public void crudMethodMetadataPostProcessorUsesBeanClassLoader() {
        ClassLoader classLoader = new org.springframework.core.OverridingClassLoader(ClassUtils.getDefaultClassLoader());
        factory.setBeanClassLoader(classLoader);
        Object processor = ReflectionTestUtils.getField(factory, "crudMethodMetadataPostProcessor");
        Assert.assertThat(ReflectionTestUtils.getField(processor, "classLoader"), CoreMatchers.is(((Object) (classLoader))));
    }

    private interface SimpleSampleRepository extends JpaRepository<User, Integer> {
        @Transactional
        @Override
        Optional<User> findById(Integer id);
    }

    /**
     * Sample interface to contain a custom method.
     *
     * @author Oliver Gierke
     */
    public interface SampleCustomRepository {
        void throwingRuntimeException();

        void throwingCheckedException() throws IOException;
    }

    /**
     * Implementation of the custom predicateExecutor interface.
     *
     * @author Oliver Gierke
     */
    private class SampleCustomRepositoryImpl implements JpaRepositoryFactoryUnitTests.SampleCustomRepository {
        @Override
        public void throwingRuntimeException() {
            throw new IllegalArgumentException("You lose!");
        }

        @Override
        public void throwingCheckedException() throws IOException {
            throw new IOException("You lose!");
        }
    }

    private interface SampleRepository extends JpaRepository<User, Integer> , JpaRepositoryFactoryUnitTests.SampleCustomRepository {}

    private interface QueryDslSampleRepository extends JpaRepositoryFactoryUnitTests.SimpleSampleRepository , QuerydslPredicateExecutor<User> {}

    static class CustomJpaRepository<T, ID extends Serializable> extends SimpleJpaRepository<T, ID> {
        public CustomJpaRepository(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
            super(entityInformation, entityManager);
        }
    }
}

