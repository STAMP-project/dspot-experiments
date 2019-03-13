/**
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.ap.test.bugs._1130;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.mapstruct.ap.testutil.IssueKey;
import org.mapstruct.ap.testutil.WithClasses;
import org.mapstruct.ap.testutil.runner.AnnotationProcessorTestRunner;
import org.mapstruct.factory.Mappers;


/**
 * Tests that when calling an update method for a previously null property, the factory method is called even if that
 * factory method has a {@link TargetType} annotation.
 *
 * @author Andreas Gudian
 */
@IssueKey("1130")
@RunWith(AnnotationProcessorTestRunner.class)
@WithClasses(Issue1130Mapper.class)
public class Issue1130Test {
    @Test
    public void factoryMethodWithTargetTypeInUpdateMethods() {
        Issue1130Mapper.AEntity aEntity = new Issue1130Mapper.AEntity();
        aEntity.setB(new Issue1130Mapper.BEntity());
        Issue1130Mapper.ADto aDto = new Issue1130Mapper.ADto();
        Mappers.getMapper(Issue1130Mapper.class).mergeA(aEntity, aDto);
        assertThat(aDto.getB()).isNotNull();
        assertThat(aDto.getB().getPassedViaConstructor()).isEqualTo("created by factory");
    }
}

