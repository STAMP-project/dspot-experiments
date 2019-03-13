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
package com.speedment.runtime.core.internal.component.sql.override;


import com.speedment.runtime.core.internal.component.sql.override.optimized.reference.OptimizedCountTerminator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 *
 *
 * @author Per Minborg
 */
public class SqlStreamTerminatorComponentImplTest {
    private SqlStreamTerminatorComponentImpl instance;

    @Test
    public void testGetters() {
        // Count is optimized by default. Test separately
        referenceTerminators().filter(( c) -> !(.class.equals(c))).forEach(this::testGetter);
    }

    @Test
    public void testGetCountTerminator() {
        Assertions.assertEquals(OptimizedCountTerminator.create().getClass().getName(), instance.getCountTerminator().getClass().getName());
    }

    @Test
    public void testSetters() {
        referenceTerminators().forEach(this::testSetter);
    }
}

