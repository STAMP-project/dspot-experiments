/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.shardingsphere.transaction.xa.jta.datasource.properties.dialect;


import java.util.Properties;
import org.apache.shardingsphere.core.config.DatabaseAccessConfiguration;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public final class H2XAPropertiesTest {
    @Test
    public void assertBuild() {
        Properties actual = new H2XAProperties().build(new DatabaseAccessConfiguration("jdbc:h2:mem:db0;DB_CLOSE_DELAY=-1;DATABASE_TO_UPPER=false;MODE=MYSQL", "root", "root"));
        Assert.assertThat(actual.getProperty("user"), CoreMatchers.is("root"));
        Assert.assertThat(actual.getProperty("password"), CoreMatchers.is("root"));
        Assert.assertThat(actual.getProperty("URL"), CoreMatchers.is("jdbc:h2:mem:db0;DB_CLOSE_DELAY=-1;DATABASE_TO_UPPER=false;MODE=MYSQL"));
    }
}

