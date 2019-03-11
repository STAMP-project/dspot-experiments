/**
 * Copyright 2014-2018 the original author or authors.
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
package example.springdata.jpa.security;


import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;


/**
 * Integration test to show the usage of Spring Security constructs within Repository query methods.
 *
 * @author Oliver Gierke
 * @author Thomas Darimont
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class SecurityIntegrationTests {
    @Autowired
    UserRepository userRepository;

    @Autowired
    BusinessObjectRepository businessObjectRepository;

    @Autowired
    SecureBusinessObjectRepository secureBusinessObjectRepository;

    User tom;

    User ollie;

    User admin;

    UsernamePasswordAuthenticationToken olliAuth;

    UsernamePasswordAuthenticationToken tomAuth;

    UsernamePasswordAuthenticationToken adminAuth;

    BusinessObject object1;

    BusinessObject object2;

    BusinessObject object3;

    @Test
    public void findBusinessObjectsForCurrentUserShouldReturnOnlyBusinessObjectsWhereCurrentUserIsOwner() {
        SecurityContextHolder.getContext().setAuthentication(tomAuth);
        List<BusinessObject> businessObjects = secureBusinessObjectRepository.findBusinessObjectsForCurrentUser();
        Assert.assertThat(businessObjects, hasSize(1));
        Assert.assertThat(businessObjects, contains(object3));
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(ollie, "x"));
        businessObjects = secureBusinessObjectRepository.findBusinessObjectsForCurrentUser();
        Assert.assertThat(businessObjects, hasSize(2));
        Assert.assertThat(businessObjects, contains(object1, object2));
    }

    @Test
    public void findBusinessObjectsForCurrentUserShouldReturnAllObjectsForAdmin() {
        SecurityContextHolder.getContext().setAuthentication(adminAuth);
        List<BusinessObject> businessObjects = secureBusinessObjectRepository.findBusinessObjectsForCurrentUser();
        Assert.assertThat(businessObjects, hasSize(3));
        Assert.assertThat(businessObjects, contains(object1, object2, object3));
    }

    @Test
    public void findBusinessObjectsForCurrentUserByIdShouldReturnOnlyBusinessObjectsWhereCurrentUserIsOwner() {
        SecurityContextHolder.getContext().setAuthentication(tomAuth);
        List<BusinessObject> businessObjects = secureBusinessObjectRepository.findBusinessObjectsForCurrentUserById();
        Assert.assertThat(businessObjects, hasSize(1));
        Assert.assertThat(businessObjects, contains(object3));
        SecurityContextHolder.getContext().setAuthentication(olliAuth);
        businessObjects = secureBusinessObjectRepository.findBusinessObjectsForCurrentUserById();
        Assert.assertThat(businessObjects, hasSize(2));
        Assert.assertThat(businessObjects, contains(object1, object2));
    }

    @Test
    public void findBusinessObjectsForCurrentUserByIdShouldReturnAllObjectsForAdmin() {
        SecurityContextHolder.getContext().setAuthentication(adminAuth);
        List<BusinessObject> businessObjects = secureBusinessObjectRepository.findBusinessObjectsForCurrentUserById();
        Assert.assertThat(businessObjects, hasSize(3));
        Assert.assertThat(businessObjects, contains(object1, object2, object3));
    }

    @Test
    public void customUpdateStatementShouldAllowToUseSecurityContextInformationViaSpelParameters() {
        SecurityContextHolder.getContext().setAuthentication(adminAuth);
        secureBusinessObjectRepository.modifiyDataWithRecordingSecurityContext();
        for (BusinessObject bo : businessObjectRepository.findAll()) {
            Assert.assertThat(bo.getLastModifiedDate(), is(notNullValue()));
            Assert.assertThat(bo.getLastModifiedBy().getFirstname(), is("admin"));
        }
    }
}

