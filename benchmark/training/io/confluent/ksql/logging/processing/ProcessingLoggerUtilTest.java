/**
 * Copyright 2018 Confluent Inc.
 *
 * Licensed under the Confluent Community License (the "License"); you may not use
 * this file except in compliance with the License.  You may obtain a copy of the
 * License at
 *
 * http://www.confluent.io/confluent-community-license
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 */
package io.confluent.ksql.logging.processing;


import java.util.Arrays;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class ProcessingLoggerUtilTest {
    @Test
    public void shouldJoinCorrectly() {
        Assert.assertThat(ProcessingLoggerUtil.join("foo", "bar"), Matchers.equalTo("foo.bar"));
    }

    @Test
    public void shouldJoinIterableCorrectly() {
        Assert.assertThat(ProcessingLoggerUtil.join(Arrays.asList("foo", "bar")), Matchers.equalTo("foo.bar"));
    }
}

