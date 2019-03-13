/**
 * Copyright 2008-2014 the original author or authors.
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
package org.springframework.batch.sample.support;


import java.sql.ResultSet;
import java.sql.SQLException;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * Encapsulates logic for testing custom {@link RowMapper} implementations.
 *
 * @author Robert Kasanicky
 * @param <T>
 * 		the item type
 */
public abstract class AbstractRowMapperTests<T> {
    // row number should be irrelevant
    private static final int IGNORED_ROW_NUMBER = 0;

    // mock result set
    private ResultSet rs = Mockito.mock(ResultSet.class);

    /* Regular usage scenario. */
    @Test
    public void testRegularUse() throws SQLException {
        setUpResultSetMock(rs);
        Assert.assertEquals(expectedDomainObject(), rowMapper().mapRow(rs, AbstractRowMapperTests.IGNORED_ROW_NUMBER));
    }
}

