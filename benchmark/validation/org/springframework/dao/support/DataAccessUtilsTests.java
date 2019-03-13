/**
 * Copyright 2002-2017 the original author or authors.
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
package org.springframework.dao.support;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.dao.TypeMismatchDataAccessException;


/**
 *
 *
 * @author Juergen Hoeller
 * @since 20.10.2004
 */
public class DataAccessUtilsTests {
    @Test
    public void withEmptyCollection() {
        Collection<String> col = new HashSet<>();
        Assert.assertNull(DataAccessUtils.uniqueResult(col));
        try {
            DataAccessUtils.requiredUniqueResult(col);
            Assert.fail("Should have thrown IncorrectResultSizeDataAccessException");
        } catch (IncorrectResultSizeDataAccessException ex) {
            // expected
            Assert.assertEquals(1, ex.getExpectedSize());
            Assert.assertEquals(0, ex.getActualSize());
        }
        try {
            DataAccessUtils.objectResult(col, String.class);
            Assert.fail("Should have thrown IncorrectResultSizeDataAccessException");
        } catch (IncorrectResultSizeDataAccessException ex) {
            // expected
            Assert.assertEquals(1, ex.getExpectedSize());
            Assert.assertEquals(0, ex.getActualSize());
        }
        try {
            DataAccessUtils.intResult(col);
            Assert.fail("Should have thrown IncorrectResultSizeDataAccessException");
        } catch (IncorrectResultSizeDataAccessException ex) {
            // expected
            Assert.assertEquals(1, ex.getExpectedSize());
            Assert.assertEquals(0, ex.getActualSize());
        }
        try {
            DataAccessUtils.longResult(col);
            Assert.fail("Should have thrown IncorrectResultSizeDataAccessException");
        } catch (IncorrectResultSizeDataAccessException ex) {
            // expected
            Assert.assertEquals(1, ex.getExpectedSize());
            Assert.assertEquals(0, ex.getActualSize());
        }
    }

    @Test
    public void withTooLargeCollection() {
        Collection<String> col = new HashSet<>(2);
        col.add("test1");
        col.add("test2");
        try {
            DataAccessUtils.uniqueResult(col);
            Assert.fail("Should have thrown IncorrectResultSizeDataAccessException");
        } catch (IncorrectResultSizeDataAccessException ex) {
            // expected
            Assert.assertEquals(1, ex.getExpectedSize());
            Assert.assertEquals(2, ex.getActualSize());
        }
        try {
            DataAccessUtils.requiredUniqueResult(col);
            Assert.fail("Should have thrown IncorrectResultSizeDataAccessException");
        } catch (IncorrectResultSizeDataAccessException ex) {
            // expected
            Assert.assertEquals(1, ex.getExpectedSize());
            Assert.assertEquals(2, ex.getActualSize());
        }
        try {
            DataAccessUtils.objectResult(col, String.class);
            Assert.fail("Should have thrown IncorrectResultSizeDataAccessException");
        } catch (IncorrectResultSizeDataAccessException ex) {
            // expected
            Assert.assertEquals(1, ex.getExpectedSize());
            Assert.assertEquals(2, ex.getActualSize());
        }
        try {
            DataAccessUtils.intResult(col);
            Assert.fail("Should have thrown IncorrectResultSizeDataAccessException");
        } catch (IncorrectResultSizeDataAccessException ex) {
            // expected
            Assert.assertEquals(1, ex.getExpectedSize());
            Assert.assertEquals(2, ex.getActualSize());
        }
        try {
            DataAccessUtils.longResult(col);
            Assert.fail("Should have thrown IncorrectResultSizeDataAccessException");
        } catch (IncorrectResultSizeDataAccessException ex) {
            // expected
            Assert.assertEquals(1, ex.getExpectedSize());
            Assert.assertEquals(2, ex.getActualSize());
        }
    }

    @Test
    public void withInteger() {
        Collection<Integer> col = new HashSet<>(1);
        col.add(5);
        Assert.assertEquals(Integer.valueOf(5), DataAccessUtils.uniqueResult(col));
        Assert.assertEquals(Integer.valueOf(5), DataAccessUtils.requiredUniqueResult(col));
        Assert.assertEquals(Integer.valueOf(5), DataAccessUtils.objectResult(col, Integer.class));
        Assert.assertEquals("5", DataAccessUtils.objectResult(col, String.class));
        Assert.assertEquals(5, DataAccessUtils.intResult(col));
        Assert.assertEquals(5, DataAccessUtils.longResult(col));
    }

    @Test
    public void withSameIntegerInstanceTwice() {
        Integer i = 5;
        Collection<Integer> col = new ArrayList<>(1);
        col.add(i);
        col.add(i);
        Assert.assertEquals(Integer.valueOf(5), DataAccessUtils.uniqueResult(col));
        Assert.assertEquals(Integer.valueOf(5), DataAccessUtils.requiredUniqueResult(col));
        Assert.assertEquals(Integer.valueOf(5), DataAccessUtils.objectResult(col, Integer.class));
        Assert.assertEquals("5", DataAccessUtils.objectResult(col, String.class));
        Assert.assertEquals(5, DataAccessUtils.intResult(col));
        Assert.assertEquals(5, DataAccessUtils.longResult(col));
    }

    // on JDK 9
    @Test
    @SuppressWarnings("deprecation")
    public void withEquivalentIntegerInstanceTwice() {
        Collection<Integer> col = new ArrayList<>(2);
        col.add(new Integer(5));
        col.add(new Integer(5));
        try {
            DataAccessUtils.uniqueResult(col);
            Assert.fail("Should have thrown IncorrectResultSizeDataAccessException");
        } catch (IncorrectResultSizeDataAccessException ex) {
            // expected
            Assert.assertEquals(1, ex.getExpectedSize());
            Assert.assertEquals(2, ex.getActualSize());
        }
    }

    @Test
    public void withLong() {
        Collection<Long> col = new HashSet<>(1);
        col.add(5L);
        Assert.assertEquals(Long.valueOf(5L), DataAccessUtils.uniqueResult(col));
        Assert.assertEquals(Long.valueOf(5L), DataAccessUtils.requiredUniqueResult(col));
        Assert.assertEquals(Long.valueOf(5L), DataAccessUtils.objectResult(col, Long.class));
        Assert.assertEquals("5", DataAccessUtils.objectResult(col, String.class));
        Assert.assertEquals(5, DataAccessUtils.intResult(col));
        Assert.assertEquals(5, DataAccessUtils.longResult(col));
    }

    @Test
    public void withString() {
        Collection<String> col = new HashSet<>(1);
        col.add("test1");
        Assert.assertEquals("test1", DataAccessUtils.uniqueResult(col));
        Assert.assertEquals("test1", DataAccessUtils.requiredUniqueResult(col));
        Assert.assertEquals("test1", DataAccessUtils.objectResult(col, String.class));
        try {
            DataAccessUtils.intResult(col);
            Assert.fail("Should have thrown TypeMismatchDataAccessException");
        } catch (TypeMismatchDataAccessException ex) {
            // expected
        }
        try {
            DataAccessUtils.longResult(col);
            Assert.fail("Should have thrown TypeMismatchDataAccessException");
        } catch (TypeMismatchDataAccessException ex) {
            // expected
        }
    }

    @Test
    public void withDate() {
        Date date = new Date();
        Collection<Date> col = new HashSet<>(1);
        col.add(date);
        Assert.assertEquals(date, DataAccessUtils.uniqueResult(col));
        Assert.assertEquals(date, DataAccessUtils.requiredUniqueResult(col));
        Assert.assertEquals(date, DataAccessUtils.objectResult(col, Date.class));
        Assert.assertEquals(date.toString(), DataAccessUtils.objectResult(col, String.class));
        try {
            DataAccessUtils.intResult(col);
            Assert.fail("Should have thrown TypeMismatchDataAccessException");
        } catch (TypeMismatchDataAccessException ex) {
            // expected
        }
        try {
            DataAccessUtils.longResult(col);
            Assert.fail("Should have thrown TypeMismatchDataAccessException");
        } catch (TypeMismatchDataAccessException ex) {
            // expected
        }
    }

    @Test
    public void exceptionTranslationWithNoTranslation() {
        DataAccessUtilsTests.MapPersistenceExceptionTranslator mpet = new DataAccessUtilsTests.MapPersistenceExceptionTranslator();
        RuntimeException in = new RuntimeException();
        Assert.assertSame(in, DataAccessUtils.translateIfNecessary(in, mpet));
    }

    @Test
    public void exceptionTranslationWithTranslation() {
        DataAccessUtilsTests.MapPersistenceExceptionTranslator mpet = new DataAccessUtilsTests.MapPersistenceExceptionTranslator();
        RuntimeException in = new RuntimeException("in");
        InvalidDataAccessApiUsageException out = new InvalidDataAccessApiUsageException("out");
        mpet.addTranslation(in, out);
        Assert.assertSame(out, DataAccessUtils.translateIfNecessary(in, mpet));
    }

    public static class MapPersistenceExceptionTranslator implements PersistenceExceptionTranslator {
        // in to out
        private final Map<RuntimeException, RuntimeException> translations = new HashMap<>();

        public void addTranslation(RuntimeException in, RuntimeException out) {
            this.translations.put(in, out);
        }

        @Override
        public DataAccessException translateExceptionIfPossible(RuntimeException ex) {
            return ((DataAccessException) (translations.get(ex)));
        }
    }
}

