/**
 * Copyright (c) 2010-2018. Axon Framework
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.axonframework.eventsourcing.eventstore.jpa;


import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Jochen Munz
 */
public class SQLStateResolverTest {
    @Test
    public void testDefaultResolver_duplicateKeyException() {
        SQLStateResolver resolver = new SQLStateResolver();
        boolean isDuplicateKey = resolver.isDuplicateKeyViolation(duplicateKeyException());
        Assert.assertTrue(isDuplicateKey);
    }

    @Test
    public void testDefaultResolver_integrityConstraintViolated() {
        SQLStateResolver resolver = new SQLStateResolver();
        boolean isDuplicateKey = resolver.isDuplicateKeyViolation(integrityContraintViolation());
        Assert.assertTrue(isDuplicateKey);
    }

    @Test
    public void testExplicitResolver_duplicateKeyException() {
        SQLStateResolver resolver = new SQLStateResolver("23505");
        boolean isDuplicateKey = resolver.isDuplicateKeyViolation(duplicateKeyException());
        Assert.assertTrue(isDuplicateKey);
    }

    @Test
    public void testExplicitResolver_integrityConstraintViolated() {
        SQLStateResolver resolver = new SQLStateResolver("23505");
        boolean isDuplicateKey = resolver.isDuplicateKeyViolation(integrityContraintViolation());
        Assert.assertFalse("A general state code should not be matched by the explicitly configured resolver", isDuplicateKey);
    }
}

