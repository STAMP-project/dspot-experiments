/**
 * Copyright 2016 Netflix, Inc.
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */
package com.netflix.genie.web.hateoas.assemblers;


import com.netflix.genie.common.dto.Application;
import com.netflix.genie.test.categories.UnitTest;
import java.util.UUID;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * Unit tests for the ApplicationResourceAssembler.
 *
 * @author tgianos
 * @since 3.0.0
 */
@Category(UnitTest.class)
public class ApplicationResourceAssemblerUnitTests {
    private static final String ID = UUID.randomUUID().toString();

    private static final String NAME = UUID.randomUUID().toString();

    private static final String USER = UUID.randomUUID().toString();

    private static final String VERSION = UUID.randomUUID().toString();

    private Application application;

    private ApplicationResourceAssembler assembler;

    /**
     * Make sure we can construct the assembler.
     */
    @Test
    public void canConstruct() {
        Assert.assertNotNull(this.assembler);
    }
}

