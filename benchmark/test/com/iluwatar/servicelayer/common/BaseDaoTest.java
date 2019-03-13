/**
 * The MIT License
 * Copyright (c) 2014-2016 Ilkka Sepp?l?
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.iluwatar.servicelayer.common;


import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * Date: 12/28/15 - 10:53 PM
 * Test for Base Data Access Objects
 *
 * @param <E>
 * 		Type of Base Entity
 * @param <D>
 * 		Type of Dao Base Implementation
 * @author Jeroen Meulemeester
 */
public abstract class BaseDaoTest<E extends BaseEntity, D extends DaoBaseImpl<E>> {
    /**
     * The number of entities stored before each test
     */
    private static final int INITIAL_COUNT = 5;

    /**
     * The unique id generator, shared between all entities
     */
    private static final AtomicInteger ID_GENERATOR = new AtomicInteger();

    /**
     * Factory, used to create new entity instances with the given name
     */
    private final Function<String, E> factory;

    /**
     * The tested data access object
     */
    private final D dao;

    /**
     * Create a new test using the given factory and dao
     *
     * @param factory
     * 		The factory, used to create new entity instances with the given name
     * @param dao
     * 		The tested data access object
     */
    public BaseDaoTest(final Function<String, E> factory, final D dao) {
        this.factory = factory;
        this.dao = dao;
    }

    @Test
    public void testFind() {
        final List<E> all = findAll();
        for (final E entity : all) {
            final E byId = this.dao.find(getId());
            Assertions.assertNotNull(byId);
            Assertions.assertEquals(getId(), getId());
        }
    }

    @Test
    public void testDelete() {
        final List<E> originalEntities = findAll();
        delete(originalEntities.get(1));
        delete(originalEntities.get(2));
        final List<E> entitiesLeft = findAll();
        Assertions.assertNotNull(entitiesLeft);
        Assertions.assertEquals(((BaseDaoTest.INITIAL_COUNT) - 2), entitiesLeft.size());
    }

    @Test
    public void testFindAll() {
        final List<E> all = findAll();
        Assertions.assertNotNull(all);
        Assertions.assertEquals(BaseDaoTest.INITIAL_COUNT, all.size());
    }

    @Test
    public void testSetId() {
        final E entity = this.factory.apply("name");
        Assertions.assertNull(getId());
        final Long expectedId = 1L;
        setId(expectedId);
        Assertions.assertEquals(expectedId, getId());
    }

    @Test
    public void testSetName() {
        final E entity = this.factory.apply("name");
        Assertions.assertEquals("name", getName());
        Assertions.assertEquals("name", entity.toString());
        final String expectedName = "new name";
        setName(expectedName);
        Assertions.assertEquals(expectedName, getName());
        Assertions.assertEquals(expectedName, entity.toString());
    }
}

