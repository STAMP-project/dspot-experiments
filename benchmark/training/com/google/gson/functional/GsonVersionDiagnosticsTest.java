/**
 * Copyright (C) 2018 Gson Authors
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
package com.google.gson.functional;


import com.google.gson.Gson;
import java.util.regex.Pattern;
import junit.framework.TestCase;
import org.junit.Test;


/**
 * Functional tests to validate printing of Gson version on AssertionErrors
 *
 * @author Inderjeet Singh
 */
public class GsonVersionDiagnosticsTest extends TestCase {
    private static final Pattern GSON_VERSION_PATTERN = Pattern.compile("(\\(GSON \\d\\.\\d\\.\\d)(?:[-.][A-Z]+)?\\)$");

    private Gson gson;

    @Test
    public void testVersionPattern() {
        TestCase.assertTrue(GsonVersionDiagnosticsTest.GSON_VERSION_PATTERN.matcher("(GSON 2.8.5)").matches());
        TestCase.assertTrue(GsonVersionDiagnosticsTest.GSON_VERSION_PATTERN.matcher("(GSON 2.8.5-SNAPSHOT)").matches());
    }

    @Test
    public void testAssertionErrorInSerializationPrintsVersion() {
        try {
            gson.toJson(new GsonVersionDiagnosticsTest.TestType());
            TestCase.fail();
        } catch (AssertionError expected) {
            ensureAssertionErrorPrintsGsonVersion(expected);
        }
    }

    @Test
    public void testAssertionErrorInDeserializationPrintsVersion() {
        try {
            gson.fromJson("{'a':'abc'}", GsonVersionDiagnosticsTest.TestType.class);
            TestCase.fail();
        } catch (AssertionError expected) {
            ensureAssertionErrorPrintsGsonVersion(expected);
        }
    }

    private static final class TestType {
        @SuppressWarnings("unused")
        String a;
    }
}

