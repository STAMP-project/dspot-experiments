/**
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.ap.test.conversion.lossy;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.mapstruct.ap.testutil.IssueKey;
import org.mapstruct.ap.testutil.WithClasses;
import org.mapstruct.ap.testutil.runner.AnnotationProcessorTestRunner;


/**
 * Tests the conversion between Joda-Time types and String/Date/Calendar.
 *
 * @author Sjaak Derksen
 */
@RunWith(AnnotationProcessorTestRunner.class)
@WithClasses({ OversizedKitchenDrawerDto.class, RegularKitchenDrawerEntity.class, VerySpecialNumber.class, VerySpecialNumberMapper.class, CutleryInventoryMapper.class, CutleryInventoryDto.class, CutleryInventoryEntity.class })
@IssueKey("5")
public class LossyConversionTest {
    @Test
    public void testNoErrorCase() {
        CutleryInventoryDto dto = new CutleryInventoryDto();
        dto.setNumberOfForks(5);
        dto.setNumberOfKnifes(((short) (7)));
        dto.setNumberOfSpoons(((byte) (3)));
        dto.setApproximateKnifeLength(3.7F);
        CutleryInventoryEntity entity = CutleryInventoryMapper.INSTANCE.map(dto);
        assertThat(entity.getNumberOfForks()).isEqualTo(5L);
        assertThat(entity.getNumberOfKnifes()).isEqualTo(7);
        assertThat(entity.getNumberOfSpoons()).isEqualTo(((short) (3)));
        assertThat(entity.getApproximateKnifeLength()).isCloseTo(3.7, withinPercentage(1.0E-4));
    }
}

