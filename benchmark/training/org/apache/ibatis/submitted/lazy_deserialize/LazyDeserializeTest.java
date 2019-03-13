/**
 * Copyright 2011 MyBatis.org.
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
package org.apache.ibatis.submitted.lazy_deserialize;


import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Franta Mejta
 * @unknown 2011-04-06T10:58:55+0200
 */
public final class LazyDeserializeTest {
    private static final int FOO_ID = 1;

    private static final int BAR_ID = 10;

    private static SqlSessionFactory factory;

    @Test
    public void testLoadLazyDeserialize() throws Exception {
        final SqlSession session = LazyDeserializeTest.factory.openSession();
        try {
            final Mapper mapper = session.getMapper(Mapper.class);
            final LazyObjectFoo foo = mapper.loadFoo(LazyDeserializeTest.FOO_ID);
            final byte[] serializedFoo = this.serializeFoo(foo);
            final LazyObjectFoo deserializedFoo = this.deserializeFoo(serializedFoo);
            Assert.assertNotNull(deserializedFoo);
            Assert.assertEquals(Integer.valueOf(LazyDeserializeTest.FOO_ID), deserializedFoo.getId());
            Assert.assertNotNull(deserializedFoo.getLazyObjectBar());
            Assert.assertEquals(Integer.valueOf(LazyDeserializeTest.BAR_ID), deserializedFoo.getLazyObjectBar().getId());
        } finally {
            session.close();
        }
    }
}

