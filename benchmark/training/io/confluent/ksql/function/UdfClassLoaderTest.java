/**
 * Copyright 2018 Confluent Inc.
 *
 * Licensed under the Confluent Community License (the "License"); you may not use
 * this file except in compliance with the License.  You may obtain a copy of the
 * License at
 *
 * http://www.confluent.io/confluent-community-license
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 */
package io.confluent.ksql.function;


import java.io.File;
import java.nio.file.Path;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.slf4j.impl.Log4jLoggerAdapter;


public class UdfClassLoaderTest {
    /* The jar contains the class org.damian.ksql.udf.ToString. It also contains the
    contents the classes from slf4j-log4j12-1.7.25.jar
     */
    private final Path udfJar = new File("src/test/resources/udf-example.jar").toPath();

    private final UdfClassLoader udfClassLoader = UdfClassLoader.newClassLoader(udfJar, getClass().getClassLoader(), ( resourceName) -> false);

    @Test
    public void shouldLoadClassesInPath() throws ClassNotFoundException {
        final UdfClassLoader udfClassLoader = UdfClassLoader.newClassLoader(udfJar, getClass().getClassLoader(), ( resourceName) -> false);
        MatcherAssert.assertThat(udfClassLoader.loadClass("org.damian.ksql.udf.ToString", true), Matchers.not(CoreMatchers.nullValue()));
    }

    @Test(expected = ClassNotFoundException.class)
    public void shouldThrowClassNotFoundIfClassIsBlacklisted() throws ClassNotFoundException {
        final UdfClassLoader udfClassLoader = UdfClassLoader.newClassLoader(udfJar, getClass().getClassLoader(), ( resourceName) -> true);
        udfClassLoader.loadClass("org.damian.ksql.udf.ToString", true);
    }

    @Test
    public void shouldLoadClassesFromParentIfNotFoundInChild() throws ClassNotFoundException {
        MatcherAssert.assertThat(udfClassLoader.loadClass("io.confluent.ksql.function.UdfClassLoaderTest", true), CoreMatchers.equalTo(UdfClassLoaderTest.class));
    }

    @Test
    public void shouldLoadNonConfluentClassesFromChildFirst() throws ClassNotFoundException {
        MatcherAssert.assertThat(udfClassLoader.loadClass("org.slf4j.impl.Log4jLoggerAdapter", true), Matchers.not(Log4jLoggerAdapter.class));
    }
}

