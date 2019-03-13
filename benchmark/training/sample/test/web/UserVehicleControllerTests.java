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
package sample.test.web;


import MediaType.APPLICATION_JSON;
import MediaType.TEXT_HTML;
import MediaType.TEXT_PLAIN;
import org.assertj.core.api.Assertions;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import sample.test.domain.VehicleIdentificationNumber;
import sample.test.service.VehicleDetails;


/**
 * {@code @WebMvcTest} based tests for {@link UserVehicleController}.
 *
 * @author Phillip Webb
 */
@RunWith(SpringRunner.class)
@WebMvcTest(UserVehicleController.class)
public class UserVehicleControllerTests {
    private static final VehicleIdentificationNumber VIN = new VehicleIdentificationNumber("00000000000000000");

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ApplicationContext applicationContext;

    @MockBean
    private UserVehicleService userVehicleService;

    @Test
    public void getVehicleWhenRequestingTextShouldReturnMakeAndModel() throws Exception {
        BDDMockito.given(this.userVehicleService.getVehicleDetails("sboot")).willReturn(new VehicleDetails("Honda", "Civic"));
        this.mvc.perform(get("/sboot/vehicle").accept(TEXT_PLAIN)).andExpect(status().isOk()).andExpect(content().string("Honda Civic"));
    }

    @Test
    public void getVehicleWhenRequestingJsonShouldReturnMakeAndModel() throws Exception {
        BDDMockito.given(this.userVehicleService.getVehicleDetails("sboot")).willReturn(new VehicleDetails("Honda", "Civic"));
        this.mvc.perform(get("/sboot/vehicle").accept(APPLICATION_JSON)).andExpect(status().isOk()).andExpect(content().json("{'make':'Honda','model':'Civic'}"));
    }

    @Test
    public void getVehicleWhenRequestingHtmlShouldReturnMakeAndModel() throws Exception {
        BDDMockito.given(this.userVehicleService.getVehicleDetails("sboot")).willReturn(new VehicleDetails("Honda", "Civic"));
        this.mvc.perform(get("/sboot/vehicle.html").accept(TEXT_HTML)).andExpect(status().isOk()).andExpect(content().string(Matchers.containsString("<h1>Honda Civic</h1>")));
    }

    @Test
    public void getVehicleWhenUserNotFoundShouldReturnNotFound() throws Exception {
        BDDMockito.given(this.userVehicleService.getVehicleDetails("sboot")).willThrow(new UserNameNotFoundException("sboot"));
        this.mvc.perform(get("/sboot/vehicle")).andExpect(status().isNotFound());
    }

    @Test
    public void getVehicleWhenVinNotFoundShouldReturnNotFound() throws Exception {
        BDDMockito.given(this.userVehicleService.getVehicleDetails("sboot")).willThrow(new sample.test.service.VehicleIdentificationNumberNotFoundException(UserVehicleControllerTests.VIN));
        this.mvc.perform(get("/sboot/vehicle")).andExpect(status().isNotFound());
    }

    @Test
    public void welcomeCommandLineRunnerShouldBeAvailable() {
        // Since we're a @WebMvcTest WelcomeCommandLineRunner should not be available.
        Assertions.assertThatThrownBy(() -> this.applicationContext.getBean(.class)).isInstanceOf(NoSuchBeanDefinitionException.class);
    }
}

