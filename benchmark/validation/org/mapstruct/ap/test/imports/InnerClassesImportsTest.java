/**
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.ap.test.imports;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.mapstruct.ap.test.imports.innerclasses.BeanFacade;
import org.mapstruct.ap.test.imports.innerclasses.BeanWithInnerEnum;
import org.mapstruct.ap.test.imports.innerclasses.BeanWithInnerEnumMapper;
import org.mapstruct.ap.test.imports.innerclasses.InnerClassMapper;
import org.mapstruct.ap.test.imports.innerclasses.SourceWithInnerClass;
import org.mapstruct.ap.test.imports.innerclasses.TargetWithInnerClass;
import org.mapstruct.ap.testutil.IssueKey;
import org.mapstruct.ap.testutil.WithClasses;
import org.mapstruct.ap.testutil.runner.AnnotationProcessorTestRunner;
import org.mapstruct.ap.testutil.runner.GeneratedSource;

import static org.mapstruct.ap.test.imports.innerclasses.BeanWithInnerEnum.InnerEnum.A;


/**
 * Test for generating a mapper which references nested types (static inner classes).
 *
 * @author Ewald Volkert
 */
@WithClasses({ SourceWithInnerClass.class, TargetWithInnerClass.class, InnerClassMapper.class// 
, BeanFacade.class, BeanWithInnerEnum.class, BeanWithInnerEnumMapper.class })
@RunWith(AnnotationProcessorTestRunner.class)
public class InnerClassesImportsTest {
    private final GeneratedSource generatedSource = new GeneratedSource();

    @Test
    @IssueKey("412")
    public void mapperRequiresInnerClassImports() {
        SourceWithInnerClass source = new SourceWithInnerClass();
        source.setInnerClassMember(new SourceWithInnerClass.SourceInnerClass(412));
        TargetWithInnerClass target = InnerClassMapper.INSTANCE.sourceToTarget(source);
        assertThat(target).isNotNull();
        assertThat(target.getInnerClassMember().getValue()).isEqualTo(412);
        generatedSource.forMapper(InnerClassMapper.class).containsImportFor(SourceWithInnerClass.SourceInnerClass.class);
        generatedSource.forMapper(InnerClassMapper.class).containsImportFor(TargetWithInnerClass.TargetInnerClass.class);
    }

    @Test
    @IssueKey("412")
    public void mapperRequiresInnerInnerClassImports() {
        SourceWithInnerClass.SourceInnerClass source = new SourceWithInnerClass.SourceInnerClass();
        source.setValue(412);
        TargetWithInnerClass.TargetInnerClass.TargetInnerInnerClass target = InnerClassMapper.INSTANCE.innerSourceToInnerInnerTarget(source);
        assertThat(target).isNotNull();
        assertThat(target.getValue()).isEqualTo(412);
        generatedSource.forMapper(InnerClassMapper.class).containsImportFor(SourceWithInnerClass.SourceInnerClass.class);
        generatedSource.forMapper(InnerClassMapper.class).containsImportFor(TargetWithInnerClass.TargetInnerClass.TargetInnerInnerClass.class);
    }

    @Test
    @IssueKey("209")
    public void mapperRequiresInnerEnumImports() {
        BeanWithInnerEnum source = new BeanWithInnerEnum();
        source.setTest("whatever");
        source.setInnerEnum(A);
        BeanFacade target = BeanWithInnerEnumMapper.INSTANCE.toFacade(source);
        assertThat(target).isNotNull();
        assertThat(target.getInnerEnum()).isEqualTo("A");
        BeanWithInnerEnum sourceAgain = BeanWithInnerEnumMapper.INSTANCE.fromFacade(target);
        assertThat(sourceAgain).isNotNull();
        assertThat(sourceAgain.getInnerEnum()).isEqualTo(A);
        generatedSource.forMapper(BeanWithInnerEnumMapper.class).containsImportFor(BeanWithInnerEnum.InnerEnum.class);
    }
}

