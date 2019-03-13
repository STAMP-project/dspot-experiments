/**
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.ap.test.bugs._1124;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.mapstruct.ap.testutil.IssueKey;
import org.mapstruct.ap.testutil.WithClasses;
import org.mapstruct.ap.testutil.runner.AnnotationProcessorTestRunner;
import org.mapstruct.factory.Mappers;


/**
 *
 *
 * @author Andreas Gudian
 */
@IssueKey("1124")
@RunWith(AnnotationProcessorTestRunner.class)
@WithClasses(Issue1124Mapper.class)
public class Issue1124Test {
    @Test
    public void nestedPropertyWithContextCompiles() {
        Issue1124Mapper.Entity entity = new Issue1124Mapper.Entity();
        Issue1124Mapper.Entity subEntity = new Issue1124Mapper.Entity();
        subEntity.setId(42L);
        entity.setEntity(subEntity);
        Issue1124Mapper.DTO dto = Mappers.getMapper(Issue1124Mapper.class).map(entity, new Issue1124Mapper.MappingContext());
        assertThat(dto.getId()).isEqualTo(42L);
    }
}

