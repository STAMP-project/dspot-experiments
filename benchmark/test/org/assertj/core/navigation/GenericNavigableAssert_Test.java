/**
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * Copyright 2012-2019 the original author or authors.
 */
package org.assertj.core.navigation;


import org.assertj.core.api.AbstractIterableAssert;
import org.assertj.core.test.Vehicle;
import org.assertj.core.test.VehicleAssert;
import org.assertj.core.test.VehicleFactory;
import org.junit.jupiter.api.Test;


public abstract class GenericNavigableAssert_Test<T extends Iterable<Vehicle>, ASSERT extends AbstractIterableAssert<?, T, Vehicle, VehicleAssert>> {
    protected VehicleFactory vehicleFactory;

    protected T expectedVehicles;

    protected ASSERT vehiclesAssert;

    @Test
    public void should_allow_to_assert_navigated_elements() {
        first().hasName(getVehicle(0).getName()).isEqualTo(getVehicle(0));
        last().isEqualTo(getVehicle(2));
        element(2).isEqualTo(getVehicle(2));
    }

    @Test
    public void should_honor_iterable_assertions() {
        contains(getVehicle(1));
        containsOnly(getVehicle(1), getVehicle(2), getVehicle(0));
        containsExactly(getVehicle(0), getVehicle(1), getVehicle(2));
        doesNotContain(new VehicleFactory.Car("doesNotExist"), new VehicleFactory.Car("doesNotExist2"));
    }

    @Test
    public void element_navigating_failing_test_index_greater_size() {
        GenericNavigableAssert_Test.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> vehiclesAssert.element(10).isEqualTo(getVehicle(0))).withMessageContaining("VehicleFactory.vehicles check index");
    }

    @Test
    public void element_navigating_failing_test_actual_not_equal_to_given() {
        GenericNavigableAssert_Test.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> vehiclesAssert.element(1).isEqualTo(getVehicle(2))).withMessageContaining("VehicleFactory.vehicles element at index 1");
    }

    @Test
    public void first_element_navigating_failing_test() {
        GenericNavigableAssert_Test.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> vehiclesAssert.first().isEqualTo(getVehicle(1))).withMessageContaining("VehicleFactory.vehicles check first element");
    }

    @Test
    public void last_element_navigating_failing_test() {
        GenericNavigableAssert_Test.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> vehiclesAssert.last().isEqualTo(getVehicle(1))).withMessageContaining("VehicleFactory.vehicles check last element");
    }
}

