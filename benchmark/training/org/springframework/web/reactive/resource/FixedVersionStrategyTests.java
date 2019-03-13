/**
 * Copyright 2002-2018 the original author or authors.
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
package org.springframework.web.reactive.resource;


import org.junit.Assert;
import org.junit.Test;


/**
 * Unit tests for {@link FixedVersionStrategy}.
 *
 * @author Rossen Stoyanchev
 * @author Brian Clozel
 */
public class FixedVersionStrategyTests {
    private static final String VERSION = "1df341f";

    private static final String PATH = "js/foo.js";

    private FixedVersionStrategy strategy;

    @Test(expected = IllegalArgumentException.class)
    public void emptyPrefixVersion() {
        new FixedVersionStrategy("  ");
    }

    @Test
    public void extractVersion() {
        Assert.assertEquals(FixedVersionStrategyTests.VERSION, this.strategy.extractVersion((((FixedVersionStrategyTests.VERSION) + "/") + (FixedVersionStrategyTests.PATH))));
        Assert.assertNull(this.strategy.extractVersion(FixedVersionStrategyTests.PATH));
    }

    @Test
    public void removeVersion() {
        Assert.assertEquals(("/" + (FixedVersionStrategyTests.PATH)), this.strategy.removeVersion((((FixedVersionStrategyTests.VERSION) + "/") + (FixedVersionStrategyTests.PATH)), FixedVersionStrategyTests.VERSION));
    }

    @Test
    public void addVersion() {
        Assert.assertEquals((((FixedVersionStrategyTests.VERSION) + "/") + (FixedVersionStrategyTests.PATH)), this.strategy.addVersion(("/" + (FixedVersionStrategyTests.PATH)), FixedVersionStrategyTests.VERSION));
    }

    // SPR-13727
    @Test
    public void addVersionRelativePath() {
        String relativePath = "../" + (FixedVersionStrategyTests.PATH);
        Assert.assertEquals(relativePath, this.strategy.addVersion(relativePath, FixedVersionStrategyTests.VERSION));
    }
}

