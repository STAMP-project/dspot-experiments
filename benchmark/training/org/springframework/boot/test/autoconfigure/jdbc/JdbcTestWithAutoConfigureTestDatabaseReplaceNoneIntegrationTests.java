/**
 * Copyright 2012-2017 the original author or authors.
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
package org.springframework.boot.test.autoconfigure.jdbc;


import AutoConfigureTestDatabase.Replace;
import javax.sql.DataSource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;


/**
 * Integration tests for {@link JdbcTest}.
 *
 * @author Phillip Webb
 * @author Stephane Nicoll
 */
@RunWith(SpringRunner.class)
@JdbcTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class JdbcTestWithAutoConfigureTestDatabaseReplaceNoneIntegrationTests {
    @Autowired
    private DataSource dataSource;

    @Test
    public void usesDefaultEmbeddedDatabase() throws Exception {
        // HSQL is explicitly defined and should not be replaced
        String product = this.dataSource.getConnection().getMetaData().getDatabaseProductName();
        assertThat(product).startsWith("HSQL");
    }
}

