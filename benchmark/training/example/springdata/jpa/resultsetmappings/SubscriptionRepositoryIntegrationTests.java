/**
 * Copyright 2018 the original author or authors.
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
package example.springdata.jpa.resultsetmappings;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;


/**
 *
 *
 * @author Thomas Darimont
 */
@RunWith(SpringRunner.class)
@DataJpaTest
@Transactional
public class SubscriptionRepositoryIntegrationTests {
    private static final String SERVICE_1 = "Service 1";

    private static final String SERVICE_2 = "Service 2";

    @Autowired
    SubscriptionRepository repository;

    @Test
    @SuppressWarnings("unchecked")
    public void shouldReturnCorrectSubscriptionSummary() {
        // 
        // 
        assertThat(repository.findAllSubscriptionSummaries()).flatExtracting(( s) -> asList(s.getProduct(), s.getUsageCount())).contains(SubscriptionRepositoryIntegrationTests.SERVICE_1, 3L, SubscriptionRepositoryIntegrationTests.SERVICE_2, 2L);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldReturnCorrectSubscriptionProjection() {
        // 
        // 
        assertThat(repository.findAllSubscriptionProjections()).flatExtracting(( s) -> asList(s.getProduct(), s.getUsageCount())).contains(SubscriptionRepositoryIntegrationTests.SERVICE_1, 3L, SubscriptionRepositoryIntegrationTests.SERVICE_2, 2L);
    }
}

