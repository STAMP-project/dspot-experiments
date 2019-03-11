/**
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
package org.flowable.test.spring.boot;


import flowable.Application;
import flowable.mappers.CustomMybatisMapper;
import org.flowable.common.engine.impl.interceptor.CommandContext;
import org.flowable.engine.ManagementService;
import org.flowable.engine.impl.util.CommandContextUtil;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;


/**
 *
 *
 * @author Dominik Bartos
 */
public class CustomMybatisMapperConfigurationTest {
    @Test
    public void executeCustomMybatisMapperQuery() throws Exception {
        AnnotationConfigApplicationContext applicationContext = this.context(Application.class);
        ManagementService managementService = applicationContext.getBean(ManagementService.class);
        String processDefinitionId = managementService.executeCustomSql(new org.flowable.engine.impl.cmd.AbstractCustomSqlExecution<CustomMybatisMapper, String>(CustomMybatisMapper.class) {
            @Override
            public String execute(CustomMybatisMapper customMybatisMapper) {
                return customMybatisMapper.loadProcessDefinitionIdByKey("waiter");
            }
        });
        Assert.assertNotNull("the processDefinitionId should not be null!", processDefinitionId);
    }

    @Test
    public void executeCustomMybatisXmlQuery() throws Exception {
        AnnotationConfigApplicationContext applicationContext = this.context(Application.class);
        ManagementService managementService = applicationContext.getBean(ManagementService.class);
        String processDefinitionDeploymentId = managementService.executeCommand(new org.flowable.common.engine.impl.interceptor.Command<String>() {
            @Override
            public String execute(CommandContext commandContext) {
                return ((String) (CommandContextUtil.getDbSqlSession(commandContext).selectOne("selectProcessDefinitionDeploymentIdByKey", "waiter")));
            }
        });
        Assert.assertNotNull("the processDefinitionDeploymentId should not be null!", processDefinitionDeploymentId);
    }
}

