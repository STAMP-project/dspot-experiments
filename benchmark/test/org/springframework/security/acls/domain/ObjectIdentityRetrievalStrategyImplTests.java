/**
 * Copyright 2002-2016 the original author or authors.
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
package org.springframework.security.acls.domain;


import org.junit.Test;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.acls.model.ObjectIdentityRetrievalStrategy;


/**
 * Tests for {@link ObjectIdentityRetrievalStrategyImpl}
 *
 * @author Andrei Stefan
 */
public class ObjectIdentityRetrievalStrategyImplTests {
    // ~ Methods
    // ========================================================================================================
    @Test
    public void testObjectIdentityCreation() throws Exception {
        ObjectIdentityRetrievalStrategyImplTests.MockIdDomainObject domain = new ObjectIdentityRetrievalStrategyImplTests.MockIdDomainObject();
        domain.setId(Integer.valueOf(1));
        ObjectIdentityRetrievalStrategy retStrategy = new ObjectIdentityRetrievalStrategyImpl();
        ObjectIdentity identity = retStrategy.getObjectIdentity(domain);
        assertThat(identity).isNotNull();
        assertThat(new ObjectIdentityImpl(domain)).isEqualTo(identity);
    }

    // ~ Inner Classes
    // ==================================================================================================
    @SuppressWarnings("unused")
    private class MockIdDomainObject {
        private Object id;

        public Object getId() {
            return id;
        }

        public void setId(Object id) {
            this.id = id;
        }
    }
}

