/**
 * Copyright 2012-2018 the original author or authors.
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
package org.springframework.boot.jdbc;


import DatabaseDriver.DB2;
import DatabaseDriver.DB2_AS400;
import DatabaseDriver.HANA;
import DatabaseDriver.INFORMIX;
import DatabaseDriver.ORACLE;
import DatabaseDriver.TERADATA;
import DatabaseDriver.UNKNOWN;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * Tests for the class names in the {@link DatabaseDriver} enumeration.
 *
 * @author Andy Wilkinson
 */
@RunWith(Parameterized.class)
public class DatabaseDriverClassNameTests {
    private static final Set<DatabaseDriver> EXCLUDED_DRIVERS = Collections.unmodifiableSet(EnumSet.of(UNKNOWN, ORACLE, DB2, DB2_AS400, INFORMIX, HANA, TERADATA));

    private final String className;

    private final Class<?> requiredType;

    public DatabaseDriverClassNameTests(DatabaseDriver driver, String className, Class<?> requiredType) {
        this.className = className;
        this.requiredType = requiredType;
    }

    @Test
    public void databaseClassIsOfRequiredType() throws Exception {
        assertThat(getInterfaceNames(this.className.replace('.', '/'))).contains(this.requiredType.getName().replace('.', '/'));
    }
}

