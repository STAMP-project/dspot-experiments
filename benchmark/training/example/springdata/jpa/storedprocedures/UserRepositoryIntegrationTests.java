/**
 * Copyright 2014-2018 the original author or authors.
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
package example.springdata.jpa.storedprocedures;


import ParameterMode.IN;
import ParameterMode.OUT;
import javax.persistence.EntityManager;
import javax.persistence.StoredProcedureQuery;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;


/**
 * Intergration test showing the usage of JPA 2.1 stored procedures support through Spring Data repositories.
 *
 * @author Thomas Darimont
 * @author Oliver Gierke
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class UserRepositoryIntegrationTests {
    @Autowired
    UserRepository repository;

    /**
     *
     *
     * @see DATAJPA-455
     */
    @Test
    public void entityAnnotatedCustomNamedProcedurePlus1IO() {
        Assert.assertThat(repository.plus1BackedByOtherNamedStoredProcedure(1), CoreMatchers.is(2));
    }

    /**
     *
     *
     * @see DATAJPA-455
     */
    @Test
    public void invokeDerivedStoredProcedure() {
        Assert.assertThat(repository.plus1inout(1), CoreMatchers.is(2));
    }

    // This is what it would look like implemented manually.
    @Autowired
    EntityManager em;

    @Test
    public void plainJpa21() {
        StoredProcedureQuery proc = em.createStoredProcedureQuery("plus1inout");
        proc.registerStoredProcedureParameter(1, Integer.class, IN);
        proc.registerStoredProcedureParameter(2, Integer.class, OUT);
        proc.setParameter(1, 1);
        proc.execute();
        Assert.assertThat(proc.getOutputParameterValue(2), CoreMatchers.is(((Object) (2))));
    }

    @Test
    public void plainJpa21_entityAnnotatedCustomNamedProcedurePlus1IO() {
        StoredProcedureQuery proc = em.createNamedStoredProcedureQuery("User.plus1");
        proc.setParameter("arg", 1);
        proc.execute();
        Assert.assertThat(proc.getOutputParameterValue("res"), CoreMatchers.is(((Object) (2))));
    }
}

