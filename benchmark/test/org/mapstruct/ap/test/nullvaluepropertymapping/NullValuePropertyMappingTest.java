/**
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.ap.test.nullvaluepropertymapping;


import java.util.Arrays;
import java.util.function.BiConsumer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mapstruct.ap.testutil.IssueKey;
import org.mapstruct.ap.testutil.WithClasses;
import org.mapstruct.ap.testutil.runner.AnnotationProcessorTestRunner;


/**
 *
 *
 * @author Sjaak Derksen
 */
@IssueKey("1306")
@RunWith(AnnotationProcessorTestRunner.class)
@WithClasses({ Address.class, Customer.class, CustomerDTO.class, AddressDTO.class, HomeDTO.class, UserDTO.class })
public class NullValuePropertyMappingTest {
    @Test
    @WithClasses(CustomerMapper.class)
    public void testStrategyAppliedOnForgedMethod() {
        Customer customer = new Customer();
        customer.setAddress(null);
        UserDTO userDTO = new UserDTO();
        userDTO.setHomeDTO(new HomeDTO());
        userDTO.getHomeDTO().setAddressDTO(new AddressDTO());
        userDTO.getHomeDTO().getAddressDTO().setHouseNo(5);
        userDTO.setDetails(Arrays.asList("green hair"));
        CustomerMapper.INSTANCE.mapCustomer(customer, userDTO);
        assertThat(userDTO.getHomeDTO()).isNotNull();
        assertThat(userDTO.getHomeDTO().getAddressDTO()).isNotNull();
        assertThat(userDTO.getHomeDTO().getAddressDTO().getHouseNo()).isEqualTo(5);
        assertThat(userDTO.getDetails()).isNotNull();
        assertThat(userDTO.getDetails()).containsExactly("green hair");
    }

    @Test
    @WithClasses({ NvpmsConfig.class, CustomerNvpmsOnConfigMapper.class })
    public void testHierarchyIgnoreOnConfig() {
        testConfig((Customer s,CustomerDTO t) -> CustomerNvpmsOnConfigMapper.INSTANCE.map(s, t));
    }

    @Test
    @WithClasses(CustomerNvpmsOnMapperMapper.class)
    public void testHierarchyIgnoreOnMapping() {
        testConfig((Customer s,CustomerDTO t) -> CustomerNvpmsOnMapperMapper.INSTANCE.map(s, t));
    }

    @Test
    @WithClasses(CustomerNvpmsOnBeanMappingMethodMapper.class)
    public void testHierarchyIgnoreOnBeanMappingMethod() {
        testConfig((Customer s,CustomerDTO t) -> CustomerNvpmsOnBeanMappingMethodMapper.INSTANCE.map(s, t));
    }

    @Test
    @WithClasses(CustomerNvpmsPropertyMappingMapper.class)
    public void testHierarchyIgnoreOnPropertyMappingMehtod() {
        testConfig((Customer s,CustomerDTO t) -> CustomerNvpmsPropertyMappingMapper.INSTANCE.map(s, t));
    }

    @Test
    @WithClasses(CustomerDefaultMapper.class)
    public void testStrategyDefaultAppliedOnForgedMethod() {
        Customer customer = new Customer();
        customer.setAddress(null);
        UserDTO userDTO = new UserDTO();
        userDTO.setHomeDTO(new HomeDTO());
        userDTO.getHomeDTO().setAddressDTO(new AddressDTO());
        userDTO.getHomeDTO().getAddressDTO().setHouseNo(5);
        userDTO.setDetails(Arrays.asList("green hair"));
        CustomerDefaultMapper.INSTANCE.mapCustomer(customer, userDTO);
        assertThat(userDTO.getHomeDTO()).isNotNull();
        assertThat(userDTO.getHomeDTO().getAddressDTO()).isNotNull();
        assertThat(userDTO.getHomeDTO().getAddressDTO().getHouseNo()).isNull();
        assertThat(userDTO.getDetails()).isNotNull();
        assertThat(userDTO.getDetails()).isEmpty();
    }
}

