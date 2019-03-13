/**
 * Copyright (C) 2015 Square Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dagger.internal.codegen;


import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.List;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


@RunWith(JUnit4.class)
public class GraphAnalysisLoaderTest {
    @Test
    public void resolveType() {
        final List<String> resolveAttempts = new ArrayList<String>();
        Elements elements = Mockito.mock(Elements.class);
        Mockito.when(elements.getTypeElement(ArgumentMatchers.any(CharSequence.class))).then(new Answer<TypeElement>() {
            @Override
            public TypeElement answer(InvocationOnMock invocationOnMock) throws Throwable {
                resolveAttempts.add(invocationOnMock.getArguments()[0].toString());
                return null;
            }
        });
        Assert.assertNull(GraphAnalysisLoader.resolveType(elements, "blah.blah.Foo$Bar$Baz"));
        List<String> expectedAttempts = ImmutableList.<String>builder().add("blah.blah.Foo.Bar.Baz").add("blah.blah.Foo.Bar$Baz").add("blah.blah.Foo$Bar.Baz").add("blah.blah.Foo$Bar$Baz").build();
        Assert.assertEquals(expectedAttempts, resolveAttempts);
        resolveAttempts.clear();
        Assert.assertNull(GraphAnalysisLoader.resolveType(elements, "$$Foo$$Bar$$Baz$$"));
        expectedAttempts = ImmutableList.<String>builder().add("$.Foo.$Bar.$Baz.$").add("$.Foo.$Bar.$Baz$$").add("$.Foo.$Bar$.Baz.$").add("$.Foo.$Bar$.Baz$$").add("$.Foo.$Bar$$Baz.$").add("$.Foo.$Bar$$Baz$$").add("$.Foo$.Bar.$Baz.$").add("$.Foo$.Bar.$Baz$$").add("$.Foo$.Bar$.Baz.$").add("$.Foo$.Bar$.Baz$$").add("$.Foo$.Bar$$Baz.$").add("$.Foo$.Bar$$Baz$$").add("$.Foo$$Bar.$Baz.$").add("$.Foo$$Bar.$Baz$$").add("$.Foo$$Bar$.Baz.$").add("$.Foo$$Bar$.Baz$$").add("$.Foo$$Bar$$Baz.$").add("$.Foo$$Bar$$Baz$$").add("$$Foo.$Bar.$Baz.$").add("$$Foo.$Bar.$Baz$$").add("$$Foo.$Bar$.Baz.$").add("$$Foo.$Bar$.Baz$$").add("$$Foo.$Bar$$Baz.$").add("$$Foo.$Bar$$Baz$$").add("$$Foo$.Bar.$Baz.$").add("$$Foo$.Bar.$Baz$$").add("$$Foo$.Bar$.Baz.$").add("$$Foo$.Bar$.Baz$$").add("$$Foo$.Bar$$Baz.$").add("$$Foo$.Bar$$Baz$$").add("$$Foo$$Bar.$Baz.$").add("$$Foo$$Bar.$Baz$$").add("$$Foo$$Bar$.Baz.$").add("$$Foo$$Bar$.Baz$$").add("$$Foo$$Bar$$Baz.$").add("$$Foo$$Bar$$Baz$$").build();
        Assert.assertEquals(expectedAttempts, resolveAttempts);
        Mockito.validateMockitoUsage();
    }
}

