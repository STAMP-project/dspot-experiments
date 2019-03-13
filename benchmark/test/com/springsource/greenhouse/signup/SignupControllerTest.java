/**
 * Copyright 2010 the original author or authors.
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
package com.springsource.greenhouse.signup;


import com.springsource.greenhouse.account.AccountRepository;
import com.springsource.greenhouse.account.EmailAlreadyOnFileException;
import com.springsource.greenhouse.account.Person;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.springframework.test.web.server.MockMvc;
import org.springframework.test.web.server.result.MockMvcResultMatchers;


public class SignupControllerTest {
    @Test
    public void signupFromApi_happyTest() throws Exception {
        AccountRepository accountRepository = Mockito.mock(AccountRepository.class);
        SignedUpGateway gateway = Mockito.mock(SignedUpGateway.class);
        SignupController signupController = new SignupController(accountRepository, gateway);
        String signupJson = "{\"first-name\":\"Roy\",\"last-name\":\"Clarkson\",\"email\":\"roy@clarkson.com\",\"confirm-email\":\"roy@clarkson.com\",\"gender\":\"M\",\"birthdate\":{\"month\":7,\"day\":8,\"year\":1976},\"password\":\"letmein\"}";
        MockMvc mockMvc = standaloneSetup(signupController).build();
        mockMvc.perform(post("/signup").contentType(APPLICATION_JSON).body(signupJson.getBytes())).andExpect(MockMvcResultMatchers.status().isCreated()).andExpect(MockMvcResultMatchers.jsonPath("message", Matchers.Matchers.equalTo("Account created")));
    }

    @Test
    public void signupFromApi_duplicateEmail() throws Exception {
        AccountRepository accountRepository = Mockito.mock(AccountRepository.class);
        Mockito.when(accountRepository.createAccount(ArgumentMatchers.any(Person.class))).thenThrow(new EmailAlreadyOnFileException("roy@clarkson.com"));
        SignedUpGateway gateway = Mockito.mock(SignedUpGateway.class);
        SignupController signupController = new SignupController(accountRepository, gateway);
        String signupJson = "{\"first-name\":\"Roy\",\"last-name\":\"Clarkson\",\"email\":\"roy@clarkson.com\",\"confirm-email\":\"roy@clarkson.com\",\"gender\":\"M\",\"birthdate\":{\"month\":7,\"day\":8,\"year\":1976},\"password\":\"letmein\"}";
        MockMvc mockMvc = standaloneSetup(signupController).build();
        mockMvc.perform(post("/signup").contentType(APPLICATION_JSON).body(signupJson.getBytes())).andExpect(MockMvcResultMatchers.status().isBadRequest()).andExpect(MockMvcResultMatchers.jsonPath("message", Matchers.Matchers.equalTo("Account creation error"))).andExpect(MockMvcResultMatchers.jsonPath("errors[0].field", Matchers.Matchers.equalTo("email"))).andExpect(MockMvcResultMatchers.jsonPath("errors[0].code", Matchers.Matchers.equalTo("account.duplicateEmail"))).andExpect(MockMvcResultMatchers.jsonPath("errors[0].message", Matchers.Matchers.equalTo("already on file")));
    }

    @Test
    public void signupFromApi_validationErrors() throws Exception {
        AccountRepository accountRepository = Mockito.mock(AccountRepository.class);
        Mockito.when(accountRepository.createAccount(ArgumentMatchers.any(Person.class))).thenThrow(new EmailAlreadyOnFileException("roy@clarkson.com"));
        SignedUpGateway gateway = Mockito.mock(SignedUpGateway.class);
        SignupController signupController = new SignupController(accountRepository, gateway);
        String signupJson = "{\"first-name\":null,\"last-name\":\"Clarkson\",\"email\":\"roy@clarkson.com\",\"confirm-email\":\"roy@clarkson.com\",\"gender\":\"M\",\"birthdate\":{\"month\":7,\"day\":8,\"year\":1976},\"password\":\"letmein\"}";
        MockMvc mockMvc = standaloneSetup(signupController).build();
        mockMvc.perform(post("/signup").contentType(APPLICATION_JSON).body(signupJson.getBytes())).andExpect(MockMvcResultMatchers.status().isBadRequest()).andExpect(MockMvcResultMatchers.jsonPath("message", Matchers.Matchers.equalTo("Validation error"))).andExpect(MockMvcResultMatchers.jsonPath("errors[0].field", Matchers.Matchers.equalTo("firstName"))).andExpect(MockMvcResultMatchers.jsonPath("errors[0].code", Matchers.Matchers.equalTo("NotEmpty"))).andExpect(MockMvcResultMatchers.jsonPath("errors[0].message", Matchers.Matchers.equalTo("may not be empty")));
    }

    @Test
    public void signupFromApi_mismatchedEmails() throws Exception {
        AccountRepository accountRepository = Mockito.mock(AccountRepository.class);
        Mockito.when(accountRepository.createAccount(ArgumentMatchers.any(Person.class))).thenThrow(new EmailAlreadyOnFileException("roy@clarkson.com"));
        SignedUpGateway gateway = Mockito.mock(SignedUpGateway.class);
        SignupController signupController = new SignupController(accountRepository, gateway);
        String signupJson = "{\"first-name\":\"Roy\",\"last-name\":\"Clarkson\",\"email\":\"roy@clarkson.com\",\"confirm-email\":\"rclarkson@vmware.com\",\"gender\":\"M\",\"birthdate\":{\"month\":7,\"day\":8,\"year\":1976},\"password\":\"letmein\"}";
        MockMvc mockMvc = standaloneSetup(signupController).build();
        mockMvc.perform(post("/signup").contentType(APPLICATION_JSON).body(signupJson.getBytes())).andExpect(MockMvcResultMatchers.status().isBadRequest()).andExpect(MockMvcResultMatchers.jsonPath("message", Matchers.Matchers.equalTo("Validation error"))).andExpect(MockMvcResultMatchers.jsonPath("errors[0].field", Matchers.Matchers.equalTo("email"))).andExpect(MockMvcResultMatchers.jsonPath("errors[0].code", Matchers.Matchers.equalTo("Confirm"))).andExpect(MockMvcResultMatchers.jsonPath("errors[0].message", Matchers.Matchers.equalTo("does not match confirmation email")));
    }
}

