/**
 * Copyright ? 2010-2017 Nokia
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
package org.jsonschema2pojo.cli;


import com.beust.jcommander.ParameterException;
import org.jsonschema2pojo.Annotator;
import org.junit.Assert;
import org.junit.Test;


public class ClassConverterTest {
    private ClassConverter converter = new ClassConverter("--custom-annotator");

    @Test
    @SuppressWarnings("unchecked")
    public void classIsCreatedFromFullyQualifiedClassName() {
        Class<Annotator> clazz = converter.convert(Annotator.class.getName());
        Assert.assertThat(clazz, is(equalTo(Annotator.class)));
    }

    @Test(expected = ParameterException.class)
    public void invalidClassNameThrowsParameterException() {
        converter.convert("some garbage.name");
    }

    @Test(expected = ParameterException.class)
    public void nullValueThrowsParameterException() {
        converter.convert(null);
    }
}

