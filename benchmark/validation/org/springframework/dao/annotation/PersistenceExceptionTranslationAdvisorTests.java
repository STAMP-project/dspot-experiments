/**
 * Copyright 2002-2015 the original author or authors.
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
package org.springframework.dao.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.persistence.PersistenceException;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.stereotype.Repository;


/**
 * Tests for PersistenceExceptionTranslationAdvisor's exception translation, as applied by
 * PersistenceExceptionTranslationPostProcessor.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 */
public class PersistenceExceptionTranslationAdvisorTests {
    private RuntimeException doNotTranslate = new RuntimeException();

    private PersistenceException persistenceException1 = new PersistenceException();

    @Test
    public void noTranslationNeeded() {
        PersistenceExceptionTranslationAdvisorTests.RepositoryInterfaceImpl target = new PersistenceExceptionTranslationAdvisorTests.RepositoryInterfaceImpl();
        PersistenceExceptionTranslationAdvisorTests.RepositoryInterface ri = createProxy(target);
        ri.noThrowsClause();
        ri.throwsPersistenceException();
        target.setBehavior(persistenceException1);
        try {
            ri.noThrowsClause();
            Assert.fail();
        } catch (RuntimeException ex) {
            Assert.assertSame(persistenceException1, ex);
        }
        try {
            ri.throwsPersistenceException();
            Assert.fail();
        } catch (RuntimeException ex) {
            Assert.assertSame(persistenceException1, ex);
        }
    }

    @Test
    public void translationNotNeededForTheseExceptions() {
        PersistenceExceptionTranslationAdvisorTests.RepositoryInterfaceImpl target = new PersistenceExceptionTranslationAdvisorTests.StereotypedRepositoryInterfaceImpl();
        PersistenceExceptionTranslationAdvisorTests.RepositoryInterface ri = createProxy(target);
        ri.noThrowsClause();
        ri.throwsPersistenceException();
        target.setBehavior(doNotTranslate);
        try {
            ri.noThrowsClause();
            Assert.fail();
        } catch (RuntimeException ex) {
            Assert.assertSame(doNotTranslate, ex);
        }
        try {
            ri.throwsPersistenceException();
            Assert.fail();
        } catch (RuntimeException ex) {
            Assert.assertSame(doNotTranslate, ex);
        }
    }

    @Test
    public void translationNeededForTheseExceptions() {
        doTestTranslationNeededForTheseExceptions(new PersistenceExceptionTranslationAdvisorTests.StereotypedRepositoryInterfaceImpl());
    }

    @Test
    public void translationNeededForTheseExceptionsOnSuperclass() {
        doTestTranslationNeededForTheseExceptions(new PersistenceExceptionTranslationAdvisorTests.MyStereotypedRepositoryInterfaceImpl());
    }

    @Test
    public void translationNeededForTheseExceptionsWithCustomStereotype() {
        doTestTranslationNeededForTheseExceptions(new PersistenceExceptionTranslationAdvisorTests.CustomStereotypedRepositoryInterfaceImpl());
    }

    @Test
    public void translationNeededForTheseExceptionsOnInterface() {
        doTestTranslationNeededForTheseExceptions(new PersistenceExceptionTranslationAdvisorTests.MyInterfaceStereotypedRepositoryInterfaceImpl());
    }

    @Test
    public void translationNeededForTheseExceptionsOnInheritedInterface() {
        doTestTranslationNeededForTheseExceptions(new PersistenceExceptionTranslationAdvisorTests.MyInterfaceInheritedStereotypedRepositoryInterfaceImpl());
    }

    public interface RepositoryInterface {
        void noThrowsClause();

        void throwsPersistenceException() throws PersistenceException;
    }

    public static class RepositoryInterfaceImpl implements PersistenceExceptionTranslationAdvisorTests.RepositoryInterface {
        private RuntimeException runtimeException;

        public void setBehavior(RuntimeException rex) {
            this.runtimeException = rex;
        }

        @Override
        public void noThrowsClause() {
            if ((runtimeException) != null) {
                throw runtimeException;
            }
        }

        @Override
        public void throwsPersistenceException() throws PersistenceException {
            if ((runtimeException) != null) {
                throw runtimeException;
            }
        }
    }

    // Extends above class just to add repository annotation
    @Repository
    public static class StereotypedRepositoryInterfaceImpl extends PersistenceExceptionTranslationAdvisorTests.RepositoryInterfaceImpl {}

    public static class MyStereotypedRepositoryInterfaceImpl extends PersistenceExceptionTranslationAdvisorTests.StereotypedRepositoryInterfaceImpl {}

    @PersistenceExceptionTranslationAdvisorTests.MyRepository
    public static class CustomStereotypedRepositoryInterfaceImpl extends PersistenceExceptionTranslationAdvisorTests.RepositoryInterfaceImpl {}

    @Target({ ElementType.TYPE })
    @Retention(RetentionPolicy.RUNTIME)
    @Repository
    public @interface MyRepository {}

    @Repository
    public interface StereotypedInterface {}

    public static class MyInterfaceStereotypedRepositoryInterfaceImpl extends PersistenceExceptionTranslationAdvisorTests.RepositoryInterfaceImpl implements PersistenceExceptionTranslationAdvisorTests.StereotypedInterface {}

    public interface StereotypedInheritingInterface extends PersistenceExceptionTranslationAdvisorTests.StereotypedInterface {}

    public static class MyInterfaceInheritedStereotypedRepositoryInterfaceImpl extends PersistenceExceptionTranslationAdvisorTests.RepositoryInterfaceImpl implements PersistenceExceptionTranslationAdvisorTests.StereotypedInheritingInterface {}
}

