/**
 * Copyright 2012-2018 the original author or authors.
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
package sample.groovytemplates;


import java.util.regex.Pattern;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;


/**
 * A Basic Spring MVC Test for the Sample Controller"
 *
 * @author Biju Kunjummen
 * @author Doo-Hwan, Kwak
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class MessageControllerWebTests {
    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Test
    public void testHome() throws Exception {
        this.mockMvc.perform(get("/")).andExpect(status().isOk()).andExpect(content().string(Matchers.containsString("<title>Messages")));
    }

    @Test
    public void testCreate() throws Exception {
        this.mockMvc.perform(post("/").param("text", "FOO text").param("summary", "FOO")).andExpect(status().isFound()).andExpect(header().string("location", MessageControllerWebTests.RegexMatcher.matches("/[0-9]+")));
    }

    @Test
    public void testCreateValidation() throws Exception {
        this.mockMvc.perform(post("/").param("text", "").param("summary", "")).andExpect(status().isOk()).andExpect(content().string(Matchers.containsString("is required")));
    }

    private static class RegexMatcher extends TypeSafeMatcher<String> {
        private final String regex;

        RegexMatcher(String regex) {
            this.regex = regex;
        }

        @Override
        public boolean matchesSafely(String item) {
            return Pattern.compile(this.regex).matcher(item).find();
        }

        @Override
        public void describeMismatchSafely(String item, Description mismatchDescription) {
            mismatchDescription.appendText("was \"").appendText(item).appendText("\"");
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("a string that matches regex: ").appendText(this.regex);
        }

        public static Matcher<String> matches(String regex) {
            return new MessageControllerWebTests.RegexMatcher(regex);
        }
    }
}

