/**
 * Copyright (C) 2015 Square, Inc.
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
package keywhiz.cli;


import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class UtilitiesTest {
    @Test
    public void validateNameReturnsTrueOnValidNames() throws Exception {
        assertThat(Utilities.validName("hello")).isTrue();
        assertThat(Utilities.validName("hello_world")).isTrue();
        assertThat(Utilities.validName("Hello-World")).isTrue();
        assertThat(Utilities.validName("foo.yml")).isTrue();
        assertThat(Utilities.validName("I_am_secret.yml")).isTrue();
        assertThat(Utilities.validName("I-am-secret.yml")).isTrue();
    }

    @Test
    public void validateNameReturnsFalseOnInvalidNames() throws Exception {
        assertThat(Utilities.validName("hello!")).isFalse();
        assertThat(Utilities.validName("hello world")).isFalse();
        assertThat(Utilities.validName("$$ bill yall")).isFalse();
        assertThat(Utilities.validName("blah/../../../etc/passwd")).isFalse();
        assertThat(Utilities.validName("bad/I-am-secret.yml")).isFalse();
        assertThat(Utilities.validName(".almostvalid")).isFalse();
        assertThat(Utilities.validName("bad\tI-am-secret.yml")).isFalse();
        List<String> specialCharacters = Arrays.asList("&", "|", "(", ")", "[", "]", "{", "}", "^", ";", ",", "\\", "/", "<", ">", "\t", "\n", "\r", "`", "'", "\"", "?", "#", "%", "*", "+", "=", "\u0000", "\u0002", "\u0000");
        for (String name : specialCharacters) {
            Assert.assertFalse((("Failed character: \"" + name) + "\""), Utilities.validName(name));
        }
    }
}

