/**
 * Licensed to CRATE Technology GmbH ("Crate") under one or more contributor
 * license agreements.  See the NOTICE file distributed with this work for
 * additional information regarding copyright ownership.  Crate licenses
 * this file to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * However, if you have executed another commercial license agreement
 * with Crate these terms will supersede the license and you may use the
 * software solely pursuant to the terms of the relevant commercial agreement.
 */
package io.crate.integrationtests;


import io.crate.action.sql.SQLActionException;
import io.crate.breaker.RamAccountingContext;
import org.junit.Test;


public class GroupByAggregateBreakerTest extends SQLTransportIntegrationTest {
    @Test
    public void selectGroupByWithBreaking() throws Exception {
        long origBufferSize = RamAccountingContext.FLUSH_BUFFER_SIZE;
        RamAccountingContext.FLUSH_BUFFER_SIZE = 24;
        try {
            expectedException.expect(SQLActionException.class);
            expectedException.expectMessage("CircuitBreakingException: [query] Data too large, data for ");
            // query takes 252 bytes of memory
            // 252b * 1.09 = 275b => should break with limit 256b
            execute("select region, count(*) from sys.summits group by 1");
        } finally {
            RamAccountingContext.FLUSH_BUFFER_SIZE = origBufferSize;
        }
    }
}

