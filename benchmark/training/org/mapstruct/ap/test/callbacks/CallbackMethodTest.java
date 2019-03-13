/**
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.ap.test.callbacks;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mapstruct.ap.testutil.IssueKey;
import org.mapstruct.ap.testutil.WithClasses;
import org.mapstruct.ap.testutil.runner.AnnotationProcessorTestRunner;


/**
 * Test for callback methods that are defined using {@link BeforeMapping} / {@link AfterMapping}
 *
 * @author Andreas Gudian
 */
@RunWith(AnnotationProcessorTestRunner.class)
@WithClasses({ ClassContainingCallbacks.class, Invocation.class, Source.class, Target.class, SourceTargetMapper.class, SourceTargetCollectionMapper.class, BaseMapper.class, Qualified.class, SourceEnum.class, TargetEnum.class })
@IssueKey("14")
public class CallbackMethodTest {
    @Test
    public void callbackMethodsForBeanMappingCalled() {
        SourceTargetMapper.INSTANCE.sourceToTarget(createSource());
        assertBeanMappingInvocations(ClassContainingCallbacks.getInvocations());
        assertBeanMappingInvocations(BaseMapper.getInvocations());
    }

    @Test
    public void callbackMethodsForBeanMappingWithResultParamCalled() {
        SourceTargetMapper.INSTANCE.sourceToTarget(createSource(), createEmptyTarget());
        assertBeanMappingInvocations(ClassContainingCallbacks.getInvocations());
        assertBeanMappingInvocations(BaseMapper.getInvocations());
    }

    @Test
    public void callbackMethodsForIterableMappingCalled() {
        SourceTargetCollectionMapper.INSTANCE.sourceToTarget(Arrays.asList(createSource()));
        assertIterableMappingInvocations(ClassContainingCallbacks.getInvocations());
        assertIterableMappingInvocations(BaseMapper.getInvocations());
    }

    @Test
    public void callbackMethodsForIterableMappingWithResultParamCalled() {
        SourceTargetCollectionMapper.INSTANCE.sourceToTarget(Arrays.asList(createSource()), new ArrayList<Target>());
        assertIterableMappingInvocations(ClassContainingCallbacks.getInvocations());
        assertIterableMappingInvocations(BaseMapper.getInvocations());
    }

    @Test
    public void callbackMethodsForMapMappingCalled() {
        SourceTargetCollectionMapper.INSTANCE.sourceToTarget(toMap("foo", createSource()));
        assertMapMappingInvocations(ClassContainingCallbacks.getInvocations());
        assertMapMappingInvocations(BaseMapper.getInvocations());
    }

    @Test
    public void callbackMethodsForMapMappingWithResultParamCalled() {
        SourceTargetCollectionMapper.INSTANCE.sourceToTarget(toMap("foo", createSource()), new HashMap<String, Target>());
        assertMapMappingInvocations(ClassContainingCallbacks.getInvocations());
        assertMapMappingInvocations(BaseMapper.getInvocations());
    }

    @Test
    public void qualifiersAreEvaluatedCorrectly() {
        Source source = createSource();
        Target target = SourceTargetMapper.INSTANCE.qualifiedSourceToTarget(source);
        assertQualifiedInvocations(ClassContainingCallbacks.getInvocations(), source, target);
        assertQualifiedInvocations(BaseMapper.getInvocations(), source, target);
        reset();
        List<Source> sourceList = Arrays.asList(createSource());
        List<Target> targetList = SourceTargetCollectionMapper.INSTANCE.qualifiedSourceToTarget(sourceList);
        assertQualifiedInvocations(ClassContainingCallbacks.getInvocations(), sourceList, targetList);
        assertQualifiedInvocations(BaseMapper.getInvocations(), sourceList, targetList);
    }

    @Test
    public void callbackMethodsForEnumMappingCalled() {
        SourceEnum source = SourceEnum.B;
        TargetEnum target = SourceTargetMapper.INSTANCE.toTargetEnum(source);
        List<Invocation> invocations = new ArrayList<Invocation>();
        invocations.addAll(allBeforeMappingMethods(source, target, TargetEnum.class));
        invocations.addAll(allAfterMappingMethods(source, target, TargetEnum.class));
        assertThat(invocations).isEqualTo(ClassContainingCallbacks.getInvocations());
        assertThat(invocations).isEqualTo(BaseMapper.getInvocations());
    }
}

