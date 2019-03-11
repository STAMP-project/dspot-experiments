/**
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
package org.flowable.engine.test.db;


import org.flowable.engine.impl.test.PluggableFlowableTestCase;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 *
 * @author Tom Baeyens
 */
public class MetaDataTest extends PluggableFlowableTestCase {
    private static final Logger LOGGER = LoggerFactory.getLogger(MetaDataTest.class);

    @Test
    public void testMetaData() {
        getProcessEngineConfiguration().getCommandExecutor().execute(new org.flowable.common.engine.impl.interceptor.Command<Object>() {
            @Override
            public Object execute(org.flowable.common.engine.impl.interceptor.CommandContext commandContext) {
                // PRINT THE TABLE NAMES TO CHECK IF WE CAN USE METADATA INSTEAD
                // THIS IS INTENDED FOR TEST THAT SHOULD RUN ON OUR QA
                // INFRASTRUCTURE TO SEE IF METADATA
                // CAN BE USED INSTEAD OF PERFORMING A QUERY THAT MIGHT FAIL
                try {
                    org.apache.ibatis.session.SqlSession sqlSession = org.flowable.engine.impl.util.CommandContextUtil.getDbSqlSession(commandContext).getSqlSession();
                    java.sql.ResultSet tables = sqlSession.getConnection().getMetaData().getTables(null, null, null, null);
                    while (tables.next()) {
                        java.sql.ResultSetMetaData resultSetMetaData = tables.getMetaData();
                        int columnCount = resultSetMetaData.getColumnCount();
                        for (int i = 1; i <= columnCount; i++) {
                            MetaDataTest.LOGGER.info("result set column {}|{}|{}|{}", i, resultSetMetaData.getColumnName(i), resultSetMetaData.getColumnLabel(i), tables.getString(i));
                        }
                        MetaDataTest.LOGGER.info("-------------------------------------------------------");
                    } 
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        });
    }
}

