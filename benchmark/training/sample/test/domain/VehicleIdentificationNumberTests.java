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
package sample.test.domain;


import org.junit.Test;


/**
 * Tests for {@link VehicleIdentificationNumber}.
 *
 * @author Phillip Webb
 * @see <a href="http://osherove.com/blog/2005/4/3/naming-standards-for-unit-tests.html">
Naming standards for unit tests</a>
 * @see <a href="http://joel-costigliola.github.io/assertj/">AssertJ</a>
 */
public class VehicleIdentificationNumberTests {
    private static final String SAMPLE_VIN = "41549485710496749";

    @Test
    public void createWhenVinIsNullShouldThrowException() {
        assertThatIllegalArgumentException().isThrownBy(() -> new VehicleIdentificationNumber(null)).withMessage("VIN must not be null");
    }

    @Test
    public void createWhenVinIsMoreThan17CharsShouldThrowException() {
        assertThatIllegalArgumentException().isThrownBy(() -> new VehicleIdentificationNumber("012345678901234567")).withMessage("VIN must be exactly 17 characters");
    }

    @Test
    public void createWhenVinIsLessThan17CharsShouldThrowException() {
        assertThatIllegalArgumentException().isThrownBy(() -> new VehicleIdentificationNumber("0123456789012345")).withMessage("VIN must be exactly 17 characters");
    }

    @Test
    public void toStringShouldReturnVin() {
        VehicleIdentificationNumber vin = new VehicleIdentificationNumber(VehicleIdentificationNumberTests.SAMPLE_VIN);
        assertThat(vin.toString()).isEqualTo(VehicleIdentificationNumberTests.SAMPLE_VIN);
    }

    @Test
    public void equalsAndHashCodeShouldBeBasedOnVin() {
        VehicleIdentificationNumber vin1 = new VehicleIdentificationNumber(VehicleIdentificationNumberTests.SAMPLE_VIN);
        VehicleIdentificationNumber vin2 = new VehicleIdentificationNumber(VehicleIdentificationNumberTests.SAMPLE_VIN);
        VehicleIdentificationNumber vin3 = new VehicleIdentificationNumber("00000000000000000");
        assertThat(vin1.hashCode()).isEqualTo(vin2.hashCode());
        assertThat(vin1).isEqualTo(vin1).isEqualTo(vin2).isNotEqualTo(vin3);
    }
}

