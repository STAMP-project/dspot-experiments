/**
 * Copyright 2009-2012 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.apache.ibatis.submitted.immutable_constructor;


import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.Assert;
import org.junit.Test;


public final class ImmutablePOJOTest {
    private static final Integer POJO_ID = 1;

    private static final String POJO_DESCRIPTION = "Description of immutable";

    private static SqlSessionFactory factory;

    @Test
    public void shouldLoadImmutablePOJOBySignature() {
        final SqlSession session = ImmutablePOJOTest.factory.openSession();
        try {
            final ImmutablePOJOMapper mapper = session.getMapper(ImmutablePOJOMapper.class);
            final ImmutablePOJO pojo = mapper.getImmutablePOJO(ImmutablePOJOTest.POJO_ID);
            Assert.assertEquals(ImmutablePOJOTest.POJO_ID, pojo.getId());
            Assert.assertEquals(ImmutablePOJOTest.POJO_DESCRIPTION, pojo.getDescription());
        } finally {
            session.close();
        }
    }

    @Test(expected = PersistenceException.class)
    public void shouldFailLoadingImmutablePOJO() {
        final SqlSession session = ImmutablePOJOTest.factory.openSession();
        try {
            final ImmutablePOJOMapper mapper = session.getMapper(ImmutablePOJOMapper.class);
            mapper.getImmutablePOJONoMatchingConstructor(ImmutablePOJOTest.POJO_ID);
        } finally {
            session.close();
        }
    }
}

