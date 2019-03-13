/**
 * Copyright 2006-2007 the original author or authors.
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
package org.springframework.batch.core;


import junit.framework.TestCase;


/**
 *
 *
 * @author Dave Syer
 */
public class EntityTests extends TestCase {
    Entity entity = new Entity(new Long(11));

    /**
     * Test method for {@link org.springframework.batch.core.Entity#hashCode()}.
     */
    public void testHashCode() {
        TestCase.assertEquals(entity.hashCode(), new Entity(entity.getId()).hashCode());
    }

    /**
     * Test method for {@link org.springframework.batch.core.Entity#hashCode()}.
     */
    public void testHashCodeNullId() {
        int withoutNull = entity.hashCode();
        entity.setId(null);
        int withNull = entity.hashCode();
        TestCase.assertTrue((withoutNull != withNull));
    }

    /**
     * Test method for {@link org.springframework.batch.core.Entity#getVersion()}.
     */
    public void testGetVersion() {
        TestCase.assertEquals(null, entity.getVersion());
    }

    /**
     * Test method for {@link org.springframework.batch.core.Entity#getVersion()}.
     */
    public void testIncrementVersion() {
        entity.incrementVersion();
        TestCase.assertEquals(new Integer(0), entity.getVersion());
    }

    /**
     * Test method for {@link org.springframework.batch.core.Entity#getVersion()}.
     */
    public void testIncrementVersionTwice() {
        entity.incrementVersion();
        entity.incrementVersion();
        TestCase.assertEquals(new Integer(1), entity.getVersion());
    }

    /**
     *
     *
     * @throws Exception
     * 		
     */
    public void testToString() throws Exception {
        Entity job = new Entity();
        TestCase.assertTrue(((job.toString().indexOf("id=null")) >= 0));
    }

    /**
     * Test method for {@link org.springframework.batch.core.Entity#equals(java.lang.Object)}.
     */
    public void testEqualsSelf() {
        TestCase.assertEquals(entity, entity);
    }

    /**
     * Test method for {@link org.springframework.batch.core.Entity#equals(java.lang.Object)}.
     */
    public void testEqualsSelfWithNullId() {
        entity = new Entity(null);
        TestCase.assertEquals(entity, entity);
    }

    /**
     * Test method for {@link org.springframework.batch.core.Entity#equals(java.lang.Object)}.
     */
    public void testEqualsEntityWithNullId() {
        entity = new Entity(null);
        TestCase.assertNotSame(entity, new Entity(null));
    }

    /**
     * Test method for {@link org.springframework.batch.core.Entity#equals(java.lang.Object)}.
     */
    public void testEqualsEntity() {
        TestCase.assertEquals(entity, new Entity(entity.getId()));
    }

    /**
     * Test method for {@link org.springframework.batch.core.Entity#equals(java.lang.Object)}.
     */
    public void testEqualsEntityWrongId() {
        TestCase.assertFalse(entity.equals(new Entity()));
    }

    /**
     * Test method for {@link org.springframework.batch.core.Entity#equals(java.lang.Object)}.
     */
    public void testEqualsObject() {
        TestCase.assertFalse(entity.equals(new Object()));
    }

    /**
     * Test method for {@link org.springframework.batch.core.Entity#equals(java.lang.Object)}.
     */
    public void testEqualsNull() {
        TestCase.assertFalse(entity.equals(null));
    }
}

