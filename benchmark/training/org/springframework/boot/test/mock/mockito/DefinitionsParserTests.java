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
package org.springframework.boot.test.mock.mockito;


import MockReset.NONE;
import org.junit.Test;
import org.mockito.Answers;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.mock.mockito.example.ExampleExtraInterface;
import org.springframework.boot.test.mock.mockito.example.ExampleService;
import org.springframework.boot.test.mock.mockito.example.ExampleServiceCaller;
import org.springframework.boot.test.mock.mockito.example.RealExampleService;
import org.springframework.util.ReflectionUtils;

import static MockReset.NONE;


/**
 * Tests for {@link DefinitionsParser}.
 *
 * @author Phillip Webb
 */
public class DefinitionsParserTests {
    private DefinitionsParser parser = new DefinitionsParser();

    @Test
    public void parseSingleMockBean() {
        this.parser.parse(DefinitionsParserTests.SingleMockBean.class);
        assertThat(getDefinitions()).hasSize(1);
        assertThat(getMockDefinition(0).getTypeToMock().resolve()).isEqualTo(ExampleService.class);
    }

    @Test
    public void parseRepeatMockBean() {
        this.parser.parse(DefinitionsParserTests.RepeatMockBean.class);
        assertThat(getDefinitions()).hasSize(2);
        assertThat(getMockDefinition(0).getTypeToMock().resolve()).isEqualTo(ExampleService.class);
        assertThat(getMockDefinition(1).getTypeToMock().resolve()).isEqualTo(ExampleServiceCaller.class);
    }

    @Test
    public void parseMockBeanAttributes() {
        this.parser.parse(DefinitionsParserTests.MockBeanAttributes.class);
        assertThat(getDefinitions()).hasSize(1);
        MockDefinition definition = getMockDefinition(0);
        assertThat(definition.getName()).isEqualTo("Name");
        assertThat(definition.getTypeToMock().resolve()).isEqualTo(ExampleService.class);
        assertThat(definition.getExtraInterfaces()).containsExactly(ExampleExtraInterface.class);
        assertThat(definition.getAnswer()).isEqualTo(Answers.RETURNS_SMART_NULLS);
        assertThat(definition.isSerializable()).isTrue();
        assertThat(definition.getReset()).isEqualTo(NONE);
        assertThat(definition.getQualifier()).isNull();
    }

    @Test
    public void parseMockBeanOnClassAndField() {
        this.parser.parse(DefinitionsParserTests.MockBeanOnClassAndField.class);
        assertThat(getDefinitions()).hasSize(2);
        MockDefinition classDefinition = getMockDefinition(0);
        assertThat(classDefinition.getTypeToMock().resolve()).isEqualTo(ExampleService.class);
        assertThat(classDefinition.getQualifier()).isNull();
        MockDefinition fieldDefinition = getMockDefinition(1);
        assertThat(fieldDefinition.getTypeToMock().resolve()).isEqualTo(ExampleServiceCaller.class);
        QualifierDefinition qualifier = QualifierDefinition.forElement(ReflectionUtils.findField(DefinitionsParserTests.MockBeanOnClassAndField.class, "caller"));
        assertThat(fieldDefinition.getQualifier()).isNotNull().isEqualTo(qualifier);
    }

    @Test
    public void parseMockBeanInferClassToMock() {
        this.parser.parse(DefinitionsParserTests.MockBeanInferClassToMock.class);
        assertThat(getDefinitions()).hasSize(1);
        assertThat(getMockDefinition(0).getTypeToMock().resolve()).isEqualTo(ExampleService.class);
    }

    @Test
    public void parseMockBeanMissingClassToMock() {
        assertThatIllegalStateException().isThrownBy(() -> this.parser.parse(.class)).withMessageContaining("Unable to deduce type to mock");
    }

    @Test
    public void parseMockBeanMultipleClasses() {
        this.parser.parse(DefinitionsParserTests.MockBeanMultipleClasses.class);
        assertThat(getDefinitions()).hasSize(2);
        assertThat(getMockDefinition(0).getTypeToMock().resolve()).isEqualTo(ExampleService.class);
        assertThat(getMockDefinition(1).getTypeToMock().resolve()).isEqualTo(ExampleServiceCaller.class);
    }

    @Test
    public void parseMockBeanMultipleClassesWithName() {
        assertThatIllegalStateException().isThrownBy(() -> this.parser.parse(.class)).withMessageContaining("The name attribute can only be used when mocking a single class");
    }

    @Test
    public void parseSingleSpyBean() {
        this.parser.parse(DefinitionsParserTests.SingleSpyBean.class);
        assertThat(getDefinitions()).hasSize(1);
        assertThat(getSpyDefinition(0).getTypeToSpy().resolve()).isEqualTo(RealExampleService.class);
    }

    @Test
    public void parseRepeatSpyBean() {
        this.parser.parse(DefinitionsParserTests.RepeatSpyBean.class);
        assertThat(getDefinitions()).hasSize(2);
        assertThat(getSpyDefinition(0).getTypeToSpy().resolve()).isEqualTo(RealExampleService.class);
        assertThat(getSpyDefinition(1).getTypeToSpy().resolve()).isEqualTo(ExampleServiceCaller.class);
    }

    @Test
    public void parseSpyBeanAttributes() {
        this.parser.parse(DefinitionsParserTests.SpyBeanAttributes.class);
        assertThat(getDefinitions()).hasSize(1);
        SpyDefinition definition = getSpyDefinition(0);
        assertThat(definition.getName()).isEqualTo("Name");
        assertThat(definition.getTypeToSpy().resolve()).isEqualTo(RealExampleService.class);
        assertThat(definition.getReset()).isEqualTo(NONE);
        assertThat(definition.getQualifier()).isNull();
    }

    @Test
    public void parseSpyBeanOnClassAndField() {
        this.parser.parse(DefinitionsParserTests.SpyBeanOnClassAndField.class);
        assertThat(getDefinitions()).hasSize(2);
        SpyDefinition classDefinition = getSpyDefinition(0);
        assertThat(classDefinition.getQualifier()).isNull();
        assertThat(classDefinition.getTypeToSpy().resolve()).isEqualTo(RealExampleService.class);
        SpyDefinition fieldDefinition = getSpyDefinition(1);
        QualifierDefinition qualifier = QualifierDefinition.forElement(ReflectionUtils.findField(DefinitionsParserTests.SpyBeanOnClassAndField.class, "caller"));
        assertThat(fieldDefinition.getQualifier()).isNotNull().isEqualTo(qualifier);
        assertThat(fieldDefinition.getTypeToSpy().resolve()).isEqualTo(ExampleServiceCaller.class);
    }

    @Test
    public void parseSpyBeanInferClassToMock() {
        this.parser.parse(DefinitionsParserTests.SpyBeanInferClassToMock.class);
        assertThat(getDefinitions()).hasSize(1);
        assertThat(getSpyDefinition(0).getTypeToSpy().resolve()).isEqualTo(RealExampleService.class);
    }

    @Test
    public void parseSpyBeanMissingClassToMock() {
        assertThatIllegalStateException().isThrownBy(() -> this.parser.parse(.class)).withMessageContaining("Unable to deduce type to spy");
    }

    @Test
    public void parseSpyBeanMultipleClasses() {
        this.parser.parse(DefinitionsParserTests.SpyBeanMultipleClasses.class);
        assertThat(getDefinitions()).hasSize(2);
        assertThat(getSpyDefinition(0).getTypeToSpy().resolve()).isEqualTo(RealExampleService.class);
        assertThat(getSpyDefinition(1).getTypeToSpy().resolve()).isEqualTo(ExampleServiceCaller.class);
    }

    @Test
    public void parseSpyBeanMultipleClassesWithName() {
        assertThatIllegalStateException().isThrownBy(() -> this.parser.parse(.class)).withMessageContaining("The name attribute can only be used when spying a single class");
    }

    @MockBean(ExampleService.class)
    static class SingleMockBean {}

    @MockBeans({ @MockBean(ExampleService.class), @MockBean(ExampleServiceCaller.class) })
    static class RepeatMockBean {}

    @MockBean(name = "Name", classes = ExampleService.class, extraInterfaces = ExampleExtraInterface.class, answer = Answers.RETURNS_SMART_NULLS, serializable = true, reset = NONE)
    static class MockBeanAttributes {}

    @MockBean(ExampleService.class)
    static class MockBeanOnClassAndField {
        @MockBean(ExampleServiceCaller.class)
        @Qualifier("test")
        private Object caller;
    }

    @MockBean({ ExampleService.class, ExampleServiceCaller.class })
    static class MockBeanMultipleClasses {}

    @MockBean(name = "name", classes = { ExampleService.class, ExampleServiceCaller.class })
    static class MockBeanMultipleClassesWithName {}

    static class MockBeanInferClassToMock {
        @MockBean
        private ExampleService exampleService;
    }

    @MockBean
    static class MockBeanMissingClassToMock {}

    @SpyBean(RealExampleService.class)
    static class SingleSpyBean {}

    @SpyBeans({ @SpyBean(RealExampleService.class), @SpyBean(ExampleServiceCaller.class) })
    static class RepeatSpyBean {}

    @SpyBean(name = "Name", classes = RealExampleService.class, reset = MockReset.NONE)
    static class SpyBeanAttributes {}

    @SpyBean(RealExampleService.class)
    static class SpyBeanOnClassAndField {
        @SpyBean(ExampleServiceCaller.class)
        @Qualifier("test")
        private Object caller;
    }

    @SpyBean({ RealExampleService.class, ExampleServiceCaller.class })
    static class SpyBeanMultipleClasses {}

    @SpyBean(name = "name", classes = { RealExampleService.class, ExampleServiceCaller.class })
    static class SpyBeanMultipleClassesWithName {}

    static class SpyBeanInferClassToMock {
        @SpyBean
        private RealExampleService exampleService;
    }

    @SpyBean
    static class SpyBeanMissingClassToMock {}
}

