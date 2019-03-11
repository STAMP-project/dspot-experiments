/**
 * Copyright (c) 2006-2019, Speedment, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); You may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.speedment.runtime.core.internal.component.sql.optimizer;


import com.speedment.runtime.core.component.sql.SqlStreamOptimizerInfo;
import com.speedment.runtime.core.db.AsynchronousQueryResult;
import com.speedment.runtime.core.db.DbmsType;
import com.speedment.runtime.core.internal.stream.builder.action.reference.FilterAction;
import com.speedment.runtime.core.internal.stream.builder.action.reference.LimitAction;
import com.speedment.runtime.core.internal.stream.builder.action.reference.PeekAction;
import com.speedment.runtime.core.internal.stream.builder.action.reference.SkipAction;
import com.speedment.runtime.core.internal.stream.builder.action.reference.SortedComparatorAction;
import com.speedment.runtime.core.stream.Pipeline;
import com.speedment.runtime.test_support.MockDbmsType;
import com.speedment.runtime.test_support.MockEntity;
import com.speedment.runtime.test_support.MockEntityUtil;
import java.util.function.Supplier;
import java.util.stream.BaseStream;
import org.junit.jupiter.api.Test;


/**
 *
 *
 * @author Per Minborg
 */
public class FilterSortedSkipOptimizer_OptimizeTest {
    private static final DbmsType DBMS_TYPE = new MockDbmsType();

    private static final Supplier<BaseStream<?, ?>> STREAM_SUPPLIER = () -> MockEntityUtil.stream(2);

    private static final FilterAction<MockEntity> FILTER_ACTION = new FilterAction(MockEntity.ID.equal(1));

    private static final SortedComparatorAction<MockEntity> SORTED_ACTION = new SortedComparatorAction(MockEntity.NAME.comparator());

    private static final SkipAction<MockEntity> SKIP_ACTION = new SkipAction(1);

    private static final LimitAction<MockEntity> LIMIT_ACTION = new LimitAction(1);

    private static final PeekAction<MockEntity> PEEK_ACTION = new PeekAction(System.out::println);

    private FilterSortedSkipOptimizer<MockEntity> instance;

    private AsynchronousQueryResult<MockEntity> asynchronousQueryResult;

    private SqlStreamOptimizerInfo<MockEntity> sqlStreamOptimizerInfo;

    @Test
    public void testFilter1Order1Skip1() {
        final Pipeline pipeline = pipelineOf(FilterSortedSkipOptimizer_OptimizeTest.FILTER_ACTION, FilterSortedSkipOptimizer_OptimizeTest.SORTED_ACTION, FilterSortedSkipOptimizer_OptimizeTest.SKIP_ACTION);
        printInfo("Before", pipeline, asynchronousQueryResult);
        Pipeline newPipeline = instance.optimize(pipeline, sqlStreamOptimizerInfo, asynchronousQueryResult);
        printInfo("After", newPipeline, asynchronousQueryResult);
    }
}

