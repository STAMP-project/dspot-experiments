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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * Create reactive API based on the templates.
 *
 * @author Mark Paluch
 */
@RunWith(Parameterized.class)
public class CreateReactiveApi {
    private static Set<String> KEEP_METHOD_RESULT_TYPE = LettuceSets.unmodifiableSet("digest", "close", "isOpen", "BaseRedisCommands.reset", "getStatefulConnection", "setAutoFlushCommands", "flushCommands");

    private static Set<String> FORCE_FLUX_RESULT = LettuceSets.unmodifiableSet("eval", "evalsha", "dispatch");

    private static Set<String> VALUE_WRAP = LettuceSets.unmodifiableSet("geopos", "bitfield");

    private static final Map<String, String> RESULT_SPEC;

    static {
        Map<String, String> resultSpec = new HashMap<>();
        resultSpec.put("geopos", "Flux<Value<GeoCoordinates>>");
        resultSpec.put("bitfield", "Flux<Value<Long>>");
        RESULT_SPEC = resultSpec;
    }

    private CompilationUnitFactory factory;

    /**
     *
     *
     * @param templateName
     * 		
     */
    public CreateReactiveApi(String templateName) {
        String targetName = templateName.replace("Commands", "ReactiveCommands");
        File templateFile = new File(Constants.TEMPLATES, (("io/lettuce/core/api/" + templateName) + ".java"));
        String targetPackage;
        if (templateName.contains("RedisSentinel")) {
            targetPackage = "io.lettuce.core.sentinel.api.reactive";
        } else {
            targetPackage = "io.lettuce.core.api.reactive";
        }
        factory = new CompilationUnitFactory(templateFile, Constants.SOURCES, targetPackage, targetName, commentMutator(), methodTypeMutator(), ( methodDeclaration) -> true, importSupplier(), null, methodCommentMutator());
        factory.keepMethodSignaturesFor(CreateReactiveApi.KEEP_METHOD_RESULT_TYPE);
    }

    @Test
    public void createInterface() throws Exception {
        factory.createInterface();
    }
}

