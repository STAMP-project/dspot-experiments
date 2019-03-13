/**
 * Copyright 2019 http://www.hswebframework.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.hswebframework.web.authorization.starter;


import DataStatus.STATUS_DISABLED;
import DataStatus.STATUS_ENABLED;
import org.hswebframework.web.authorization.Authentication;
import org.hswebframework.web.authorization.AuthenticationInitializeService;
import org.hswebframework.web.entity.authorization.UserEntity;
import org.hswebframework.web.service.authorization.PasswordStrengthValidator;
import org.hswebframework.web.service.authorization.UserService;
import org.hswebframework.web.service.authorization.UsernameValidator;
import org.hswebframework.web.tests.SimpleWebApplicationTests;
import org.hswebframework.web.validate.ValidationException;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;


/**
 *
 *
 * @author zhouhao
 */
@Configuration
@Import(UserTests.Config.class)
public class UserTests extends SimpleWebApplicationTests {
    @Configuration
    public static class Config {
        @Bean
        public PasswordStrengthValidator passwordStrengthValidator() {
            return new PasswordStrengthValidator() {
                @Override
                public boolean validate(String data) {
                    return (data.length()) >= 4;
                }

                @Override
                public String getErrorMessage() {
                    return "??????";
                }
            };
        }

        @Bean
        public UsernameValidator usernameValidator() {
            return ( username) -> (username.length()) >= 4;
        }
    }

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationInitializeService authenticationInitializeService;

    @Test
    public void testInitAuth() {
        UserEntity entity = createTestUser();
        Authentication authentication = authenticationInitializeService.initUserAuthorization(entity.getId());
        Assert.assertNotNull(authentication);
        Assert.assertEquals(authentication.getUser().getUsername(), entity.getUsername());
    }

    @Test
    public void testCRUD() {
        UserEntity userEntity = userService.createEntity();
        userEntity.setName("??");
        userEntity.setUsername("test");
        userEntity.setPassword("123");
        userEntity.setCreatorId("admin");
        userEntity.setCreateTimeNow();
        try {
            userService.insert(userEntity);
            Assert.assertTrue(false);
        } catch (ValidationException e) {
            Assert.assertEquals(e.getResults().get(0).getMessage(), "??????");
        }
        userEntity.setPassword("password_1234");
        String id = userService.insert(userEntity);
        UserEntity newUserEntity = userEntity.clone();
        newUserEntity.setUsername("test2");
        String anotherId = userService.insert(newUserEntity);
        Assert.assertNotNull(id);
        Assert.assertEquals(userEntity.getPassword().length(), 32);
        UserEntity entityInDb = userService.selectByUsername(userEntity.getUsername());
        Assert.assertEquals(entityInDb.getStatus(), STATUS_ENABLED);
        Assert.assertNotNull(entityInDb.getCreateTime());
        Assert.assertEquals(entityInDb.getPassword(), userService.encodePassword("password_1234", entityInDb.getSalt()));
        entityInDb = userService.selectByUsername(userEntity.getUsername());
        Assert.assertEquals(entityInDb.getStatus(), STATUS_ENABLED);
        Assert.assertNotNull(entityInDb.getCreateTime());
        try {
            userService.updatePassword(id, "test", "test");
            Assert.assertTrue(false);
        } catch (ValidationException e) {
            // ????
        }
        userService.updatePassword(id, "password_1234", "password_2345");
        entityInDb = userService.selectByUsername(userEntity.getUsername());
        Assert.assertEquals(entityInDb.getPassword(), userService.encodePassword("password_2345", entityInDb.getSalt()));
        entityInDb.setId(anotherId);
        entityInDb.setName("???");
        userService.update(anotherId, entityInDb);
        entityInDb.setId(id);
        userService.update(id, entityInDb);
        entityInDb = userService.selectByUsername(userEntity.getUsername());
        Assert.assertEquals("???", entityInDb.getName());
        userService.disable(id);
        entityInDb = userService.selectByUsername(userEntity.getUsername());
        Assert.assertEquals(STATUS_DISABLED, entityInDb.getStatus());
        userService.enable(id);
        entityInDb = userService.selectByUsername(userEntity.getUsername());
        Assert.assertEquals(STATUS_ENABLED, entityInDb.getStatus());
    }
}

