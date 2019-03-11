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
/**
 * Copyright 2002-2016 the original author or authors.
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
package sample;


import Directory.ROOT_DIRECTORY;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import sample.dms.DocumentDao;


/**
 * Basic integration test for DMS sample.
 *
 * @author Ben Alex
 */
@ContextConfiguration(locations = { "classpath:applicationContext-dms-shared.xml", "classpath:applicationContext-dms-insecure.xml" })
public class DmsIntegrationTests extends AbstractTransactionalJUnit4SpringContextTests {
    @Autowired
    protected JdbcTemplate jdbcTemplate;

    @Autowired
    protected DocumentDao documentDao;

    @Test
    public void testBasePopulation() {
        assertThat(this.jdbcTemplate.queryForObject("select count(id) from DIRECTORY", Integer.class)).isEqualTo(9);
        assertThat(((int) (this.jdbcTemplate.queryForObject("select count(id) from FILE", Integer.class)))).isEqualTo(90);
        assertThat(this.documentDao.findElements(ROOT_DIRECTORY).length).isEqualTo(3);
    }

    @Test
    public void testMarissaRetrieval() {
        process("rod", "koala", false);
    }

    @Test
    public void testScottRetrieval() {
        process("scott", "wombat", false);
    }

    @Test
    public void testDianneRetrieval() {
        process("dianne", "emu", false);
    }
}

