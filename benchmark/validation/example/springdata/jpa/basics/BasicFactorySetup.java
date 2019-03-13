/**
 * Copyright 2013-2018 the original author or authors.
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
package example.springdata.jpa.basics;


import example.springdata.jpa.simple.SimpleUserRepository;
import example.springdata.jpa.simple.User;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.junit.Test;


/**
 * Test case showing how to use the basic {@link GenericDaoFactory}
 *
 * @author Oliver Gierke
 */
public class BasicFactorySetup {
    private static final EntityManagerFactory factory = Persistence.createEntityManagerFactory("jpa.sample.plain");

    private SimpleUserRepository userRepository;

    private EntityManager em;

    private User user;

    /**
     * Showing invocation of finder method.
     */
    @Test
    public void executingFinders() {
        assertThat(userRepository.findByTheUsersName("username")).isEqualTo(user);
        assertThat(userRepository.findByLastname("lastname")).first().isEqualTo(user);
        assertThat(userRepository.findByFirstname("firstname")).first().isEqualTo(user);
    }
}

