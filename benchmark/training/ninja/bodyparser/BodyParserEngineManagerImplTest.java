/**
 * Copyright (C) 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ninja.bodyparser;


import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class BodyParserEngineManagerImplTest {
    @Test
    public void testContentTypes() {
        List<String> types = Lists.newArrayList(createBodyParserEngineManager().getContentTypes());
        Collections.sort(types);
        Assert.assertThat(types.toString(), CoreMatchers.equalTo("[application/json, application/x-www-form-urlencoded, application/xml]"));
    }
}

