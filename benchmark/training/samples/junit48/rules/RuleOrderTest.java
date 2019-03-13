/**
 * Copyright 2009 the original author or authors.
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
package samples.junit48.rules;


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;


@RunWith(PowerMockRunner.class)
public class RuleOrderTest {
    private static final String EMPTY_STRING = "";

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private String temporaryFileName = RuleOrderTest.EMPTY_STRING;

    @Test
    public void rulesAreExecutedBeforeSetupMethods() throws Exception {
        Assert.assertThat(temporaryFileName, CoreMatchers.not(CoreMatchers.nullValue()));
        Assert.assertThat(temporaryFileName, CoreMatchers.not(CoreMatchers.equalTo(RuleOrderTest.EMPTY_STRING)));
    }
}

