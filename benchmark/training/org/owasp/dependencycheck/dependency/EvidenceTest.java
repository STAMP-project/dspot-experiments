/**
 * This file is part of dependency-check-core.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Copyright (c) 2012 Jeremy Long. All Rights Reserved.
 */
package org.owasp.dependencycheck.dependency;


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.owasp.dependencycheck.BaseTest;

import static Confidence.HIGH;
import static Confidence.HIGHEST;
import static Confidence.LOW;
import static Confidence.MEDIUM;


/**
 *
 *
 * @author Jeremy Long
 */
public class EvidenceTest extends BaseTest {
    /**
     * Test of equals method, of class Evidence.
     */
    @Test
    public void testEquals() {
        Evidence that0 = new Evidence("file", "name", "guice-3.0", HIGHEST);
        Evidence that1 = new Evidence("jar", "package name", "dependency", HIGHEST);
        Evidence that2 = new Evidence("jar", "package name", "google", HIGHEST);
        Evidence that3 = new Evidence("jar", "package name", "guice", HIGHEST);
        Evidence that4 = new Evidence("jar", "package name", "inject", HIGHEST);
        Evidence that5 = new Evidence("jar", "package name", "inject", LOW);
        Evidence that6 = new Evidence("jar", "package name", "internal", LOW);
        Evidence that7 = new Evidence("manifest", "Bundle-Description", "Guice is a lightweight dependency injection framework for Java 5 and above", MEDIUM);
        Evidence that8 = new Evidence("Manifest", "Implementation-Title", "Spring Framework", HIGH);
        Evidence instance = new Evidence("Manifest", "Implementation-Title", "Spring Framework", HIGH);
        Assert.assertFalse(instance.equals(that0));
        Assert.assertFalse(instance.equals(that1));
        Assert.assertFalse(instance.equals(that2));
        Assert.assertFalse(instance.equals(that3));
        Assert.assertFalse(instance.equals(that4));
        Assert.assertFalse(instance.equals(that5));
        Assert.assertFalse(instance.equals(that6));
        Assert.assertFalse(instance.equals(that7));
        Assert.assertTrue(instance.equals(that8));
    }

    @Test
    public void testHashcodeContract() throws Exception {
        final Evidence titleCase = new Evidence("Manifest", "Implementation-Title", "Spring Framework", HIGH);
        final Evidence lowerCase = new Evidence("manifest", "implementation-title", "spring framework", HIGH);
        Assert.assertThat(titleCase, CoreMatchers.is(CoreMatchers.equalTo(lowerCase)));
        Assert.assertThat(titleCase.hashCode(), CoreMatchers.is(CoreMatchers.equalTo(lowerCase.hashCode())));
    }

    /**
     * Test of compareTo method, of class Evidence.
     */
    @Test
    public void testCompareTo() {
        Evidence that0 = new Evidence("file", "name", "guice-3.0", HIGHEST);
        Evidence that1 = new Evidence("jar", "package name", "dependency", HIGHEST);
        Evidence that2 = new Evidence("jar", "package name", "google", HIGHEST);
        Evidence that3 = new Evidence("jar", "package name", "guice", HIGHEST);
        Evidence that4 = new Evidence("jar", "package name", "inject", HIGHEST);
        Evidence that5 = new Evidence("jar", "package name", "inject", LOW);
        Evidence that6 = new Evidence("jar", "package name", "internal", LOW);
        Evidence that7 = new Evidence("manifest", "Bundle-Description", "Guice is a lightweight dependency injection framework for Java 5 and above", MEDIUM);
        Evidence that8 = new Evidence("Manifest", "Implementation-Title", "Spring Framework", HIGH);
        Evidence that9 = new Evidence("manifest", "implementation-title", "zippy", HIGH);
        Evidence instance = new Evidence("Manifest", "Implementation-Title", "Spring Framework", HIGH);
        int result = instance.compareTo(that0);
        Assert.assertTrue((result > 0));
        result = instance.compareTo(that1);
        Assert.assertTrue((result > 0));
        result = instance.compareTo(that2);
        Assert.assertTrue((result > 0));
        result = instance.compareTo(that3);
        Assert.assertTrue((result > 0));
        result = instance.compareTo(that4);
        Assert.assertTrue((result > 0));
        result = instance.compareTo(that5);
        Assert.assertTrue((result > 0));
        result = instance.compareTo(that6);
        Assert.assertTrue((result > 0));
        result = instance.compareTo(that7);
        Assert.assertTrue((result > 0));
        result = instance.compareTo(that8);
        Assert.assertTrue((result == 0));
        result = instance.compareTo(that9);
        Assert.assertTrue((result < 0));
    }
}

