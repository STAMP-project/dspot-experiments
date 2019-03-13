/**
 * Copyright (C) 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.gson.metrics;


import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;


/**
 * Tests to measure performance for Gson. All tests in this file will be disabled in code. To run
 * them remove disabled_ prefix from the tests and run them.
 *
 * @author Inderjeet Singh
 * @author Joel Leitch
 */
public class PerformanceTest extends TestCase {
    private static final int COLLECTION_SIZE = 5000;

    private static final int NUM_ITERATIONS = 100;

    private Gson gson;

    public void testDummy() {
        // This is here to prevent Junit for complaining when we disable all tests.
    }

    private static class ExceptionHolder {
        public final String message;

        public final String stackTrace;

        // For use by Gson
        @SuppressWarnings("unused")
        private ExceptionHolder() {
            this("", "");
        }

        public ExceptionHolder(String message, String stackTrace) {
            this.message = message;
            this.stackTrace = stackTrace;
        }
    }

    @SuppressWarnings("unused")
    private static class CollectionEntry {
        final String name;

        final String value;

        // For use by Gson
        private CollectionEntry() {
            this(null, null);
        }

        CollectionEntry(String name, String value) {
            this.name = name;
            this.value = value;
        }
    }

    @SuppressWarnings("unused")
    private static final class ClassWithList {
        final String field;

        final List<PerformanceTest.ClassWithField> list = new ArrayList<PerformanceTest.ClassWithField>(PerformanceTest.COLLECTION_SIZE);

        ClassWithList() {
            this(null);
        }

        ClassWithList(String field) {
            this.field = field;
        }
    }

    @SuppressWarnings("unused")
    private static final class ClassWithField {
        final String field;

        ClassWithField() {
            this("");
        }

        public ClassWithField(String field) {
            this.field = field;
        }
    }

    @SuppressWarnings("unused")
    private static final class ClassWithListOfObjects {
        @Expose
        final String field;

        @Expose
        final List<PerformanceTest.ClassWithExposedField> list = new ArrayList<PerformanceTest.ClassWithExposedField>(PerformanceTest.COLLECTION_SIZE);

        ClassWithListOfObjects() {
            this(null);
        }

        ClassWithListOfObjects(String field) {
            this.field = field;
        }
    }

    @SuppressWarnings("unused")
    private static final class ClassWithExposedField {
        @Expose
        final String field;

        ClassWithExposedField() {
            this("");
        }

        ClassWithExposedField(String field) {
            this.field = field;
        }
    }
}

