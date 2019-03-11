/**
 * Copyright 2011-2019 the original author or authors.
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
package io.lettuce.apigenerator;


import io.lettuce.core.internal.LettuceSets;
import java.io.File;
import java.util.Set;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * Create async API based on the templates.
 *
 * @author Mark Paluch
 */
@RunWith(Parameterized.class)
public class CreateAsyncApi {
    private Set<String> KEEP_METHOD_RESULT_TYPE = LettuceSets.unmodifiableSet("shutdown", "debugOom", "debugSegfault", "digest", "close", "isOpen", "BaseRedisCommands.reset", "getStatefulConnection", "setAutoFlushCommands", "flushCommands");

    private CompilationUnitFactory factory;

    /**
     *
     *
     * @param templateName
     * 		
     */
    public CreateAsyncApi(String templateName) {
        String targetName = templateName.replace("Commands", "AsyncCommands");
        File templateFile = new File(Constants.TEMPLATES, (("io/lettuce/core/api/" + templateName) + ".java"));
        String targetPackage;
        if (templateName.contains("RedisSentinel")) {
            targetPackage = "io.lettuce.core.sentinel.api.async";
        } else {
            targetPackage = "io.lettuce.core.api.async";
        }
        factory = new CompilationUnitFactory(templateFile, Constants.SOURCES, targetPackage, targetName, commentMutator(), methodTypeMutator(), ( methodDeclaration) -> true, importSupplier(), null, null);
        factory.keepMethodSignaturesFor(KEEP_METHOD_RESULT_TYPE);
    }

    @Test
    public void createInterface() throws Exception {
        factory.createInterface();
    }
}

