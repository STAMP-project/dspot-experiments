/**
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.restassured.itest.java;


import io.restassured.itest.java.support.WithJetty;
import org.hamcrest.Matchers;
import org.junit.Test;


public class GivenWhenThenRootPathITest extends WithJetty {
    @Test
    public void given_when_then_works_with_root_path() {
        get("/jsonStore").then().assertThat().root("store.%s", withArgs("book")).body("category.size()", Matchers.equalTo(4)).appendRoot("%s.%s", withArgs("author", "size()")).body(withNoArgs(), Matchers.equalTo(4));
    }
}

