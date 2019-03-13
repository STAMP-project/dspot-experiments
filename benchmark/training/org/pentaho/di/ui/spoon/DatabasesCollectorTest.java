/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2017 by Hitachi Vantara : http://www.pentaho.com
 *
 * ******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ****************************************************************************
 */
package org.pentaho.di.ui.spoon;


import org.junit.Assert;
import org.junit.Test;
import org.pentaho.di.base.AbstractMeta;
import org.pentaho.di.repository.Repository;


/**
 *
 *
 * @author Andrey Khayrutdinov
 */
public class DatabasesCollectorTest {
    @Test
    public void repositoryIsNull() throws Exception {
        DatabasesCollector collector = new DatabasesCollector(DatabasesCollectorTest.prepareMeta(DatabasesCollectorTest.mockDb("mysql")), null);
        collector.collectDatabases();
        Assert.assertEquals(collector.getDatabaseNames().size(), 1);
        Assert.assertNotNull(collector.getMetaFor("mysql"));
    }

    @Test
    public void repositoryDuplicates() throws Exception {
        AbstractMeta meta = DatabasesCollectorTest.prepareMeta(DatabasesCollectorTest.mockDb("mysql"));
        Repository repository = DatabasesCollectorTest.mockRepository(DatabasesCollectorTest.mockDb("mysql"));
        DatabasesCollector collector = new DatabasesCollector(meta, repository);
        collector.collectDatabases();
        Assert.assertEquals(collector.getDatabaseNames().size(), 1);
        Assert.assertNotNull(collector.getMetaFor("mysql"));
    }

    @Test
    public void repositoryContainsUnique() throws Exception {
        AbstractMeta meta = DatabasesCollectorTest.prepareMeta(DatabasesCollectorTest.mockDb("mysql"), DatabasesCollectorTest.mockDb("oracle"));
        Repository repository = DatabasesCollectorTest.mockRepository(DatabasesCollectorTest.mockDb("h2"));
        DatabasesCollector collector = new DatabasesCollector(meta, repository);
        collector.collectDatabases();
        Assert.assertEquals(collector.getDatabaseNames().size(), 3);
    }
}

