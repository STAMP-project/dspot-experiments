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
package org.springframework.data.jpa.repository;


import javax.persistence.Query;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;


/**
 * Testcase to run {@link UserRepository} integration tests on top of EclipseLink.
 *
 * @author Oliver Gierke
 * @author Thomas Darimont
 * @author Jens Schauder
 */
@ContextConfiguration("classpath:eclipselink.xml")
public class EclipseLinkNamespaceUserRepositoryTests extends NamespaceUserRepositoryTests {
    /**
     * This test will fail once https://bugs.eclipse.org/bugs/show_bug.cgi?id=521915 is fixed.
     */
    // DATAJPA-1172
    @Override
    @Test
    public void queryProvidesCorrectNumberOfParametersForNativeQuery() {
        Query query = em.createNativeQuery("select 1 from User where firstname=? and lastname=?");
        assertThat(query.getParameters()).describedAs("Due to a bug eclipse has size 0. If this is no longer the case the special code path triggered in NamedOrIndexedQueryParameterSetter.registerExcessParameters can be removed").hasSize(0);
    }

    /**
     * Ignores the test for EclipseLink 2.7.2. Reconsider once https://bugs.eclipse.org/bugs/show_bug.cgi?id=533240 is
     * fixed.
     */
    // DATAJPA-1314
    @Override
    @Test
    public void findByEmptyArrayOfIntegers() throws Exception {
        assumeNotEclipseLink2_7_2plus();
        super.findByEmptyArrayOfIntegers();
    }

    /**
     * Ignores the test for EclipseLink 2.7.2. Reconsider once https://bugs.eclipse.org/bugs/show_bug.cgi?id=533240 is
     * fixed.
     */
    // DATAJPA-1314
    @Override
    @Test
    public void findByAgeWithEmptyArrayOfIntegersOrFirstName() {
        assumeNotEclipseLink2_7_2plus();
        super.findByAgeWithEmptyArrayOfIntegersOrFirstName();
    }

    /**
     * Ignores the test for EclipseLink 2.7.2. Reconsider once https://bugs.eclipse.org/bugs/show_bug.cgi?id=533240 is
     * fixed.
     */
    // DATAJPA-1314
    @Override
    @Test
    public void findByEmptyCollectionOfIntegers() throws Exception {
        assumeNotEclipseLink2_7_2plus();
        super.findByEmptyCollectionOfIntegers();
    }

    /**
     * Ignores the test for EclipseLink 2.7.2. Reconsider once https://bugs.eclipse.org/bugs/show_bug.cgi?id=533240 is
     * fixed.
     */
    // DATAJPA-1314
    @Override
    @Test
    public void findByEmptyCollectionOfStrings() throws Exception {
        assumeNotEclipseLink2_7_2plus();
        super.findByEmptyCollectionOfStrings();
    }
}

