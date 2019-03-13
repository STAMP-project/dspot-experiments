/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ambari.swagger;


import com.google.common.collect.ImmutableSet;
import io.swagger.models.Swagger;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import org.apache.maven.plugin.logging.Log;
import org.junit.Assert;
import org.junit.Test;


public class AmbariSwaggerReaderTest {
    /**
     * Test the {@link AmbariSwaggerReader#joinPaths(String, String...)} method
     */
    @Test
    public void testJoinPaths() {
        Assert.assertEquals("/toplevel/nested/{param}/list", AmbariSwaggerReader.joinPaths("", "/", "/", "", "toplevel", "/nested/", "/{param}", "list"));
        Assert.assertEquals("/toplevel/nested/{param}/list", AmbariSwaggerReader.joinPaths("/", "toplevel", "", "/nested/", "/", "/{param}", "list", ""));
    }

    /**
     * Test the basic case: one top level API and one nested API, each with one operation
     */
    @Test
    public void swaggerBasicCase() {
        AmbariSwaggerReader asr = new AmbariSwaggerReader(null, createMock(Log.class));
        Swagger swagger = asr.read(ImmutableSet.of(TopLevelAPI.class, NestedAPI.class));
        Assert.assertEquals(ImmutableSet.of("/toplevel/top", "/toplevel/{param}/nested/list"), swagger.getPaths().keySet());
        AmbariSwaggerReaderTest.assertPathParamsExist(swagger, "/toplevel/{param}/nested/list", "param");
    }

    /**
     * Test conflicting nested API's (the same API's are returned from different top level API's).
     * In this case the nested API should be associated to the first processed top level API.
     */
    @Test
    public void swaggerConflictingNestedApis() {
        AmbariSwaggerReader asr = new AmbariSwaggerReader(null, createMock(Log.class));
        Set<Class<?>> classes = new LinkedHashSet<>(Arrays.asList(TopLevelAPI.class, AnotherTopLevelAPI.class, NestedAPI.class));
        Swagger swagger = asr.read(classes);
        Assert.assertEquals(ImmutableSet.of("/toplevel/top", "/toplevel/{param}/nested/list", "/toplevel2/anotherTop"), swagger.getPaths().keySet());
        AmbariSwaggerReaderTest.assertPathParamsExist(swagger, "/toplevel/{param}/nested/list", "param");
    }

    /**
     * Test conflicting nested API's (the same API's are returned from different top level API's) with
     * {@link SwaggerPreferredParent} annotation.
     * In this case the nested API should be associated to the preferred top level API.
     */
    @Test
    public void swaggerConflictingNestedApisWithPreferredParent() {
        AmbariSwaggerReader asr = new AmbariSwaggerReader(null, createMock(Log.class));
        Set<Class<?>> classes = new LinkedHashSet<>(Arrays.asList(TopLevelAPI.class, AnotherTopLevelAPI.class, NestedWithPreferredParentAPI.class));
        Swagger swagger = asr.read(classes);
        Assert.assertEquals(ImmutableSet.of("/toplevel/top", "/toplevel2/{param}/nestedWithPreferredParent/list", "/toplevel2/anotherTop"), swagger.getPaths().keySet());
        AmbariSwaggerReaderTest.assertPathParamsExist(swagger, "/toplevel2/{param}/nestedWithPreferredParent/list", "param");
    }

    /**
     * Test conflicting nested API's (the same API's are returned from different top level API's) with
     * {@link SwaggerPreferredParent} annotation.
     * In this case the preferred parent API is the same as the one otherwise would have been set.
     */
    @Test
    public void swaggerConflictingNestedApisWithSamePreferredParent() {
        AmbariSwaggerReader asr = new AmbariSwaggerReader(null, createMock(Log.class));
        Set<Class<?>> classes = new LinkedHashSet<>(Arrays.asList(TopLevelAPI.class, AnotherTopLevelAPI.class, NestedWithSamePreferredParentAPI.class));
        Swagger swagger = asr.read(classes);
        Assert.assertEquals(ImmutableSet.of("/toplevel/top", "/toplevel/{param}/nestedWithSamePreferredParent/list", "/toplevel2/anotherTop"), swagger.getPaths().keySet());
        AmbariSwaggerReaderTest.assertPathParamsExist(swagger, "/toplevel/{param}/nestedWithSamePreferredParent/list", "param");
    }

    /**
     * Test conflicting nested API's (the same API's are returned from different top level API's) with
     * {@link SwaggerPreferredParent} annotation.
     * In this case we expect an ignore since NestedWithBadPreferredParentAPI set a preferred parent which
     * does not have it as a child.
     */
    @Test
    public void swaggerConflictingNestedApisWithBadPreferredParent() {
        AmbariSwaggerReader asr = new AmbariSwaggerReader(null, createMock(Log.class));
        Set<Class<?>> classes = new LinkedHashSet<>(Arrays.asList(TopLevelAPI.class, AnotherTopLevelAPI.class, NestedWithBadPreferredParentAPI.class));
        Swagger swagger = asr.read(classes);
        Assert.assertEquals(ImmutableSet.of("/toplevel/top", "/toplevel2/{param}/nestedWithBadPreferredParent/list", "/toplevel2/anotherTop"), swagger.getPaths().keySet());
        AmbariSwaggerReaderTest.assertPathParamsExist(swagger, "/toplevel2/{param}/nestedWithBadPreferredParent/list", "param");
    }

    /**
     * Test nested API which uses {@link org.apache.ambari.annotations.SwaggerOverwriteNestedAPI} annotation.
     * In this case we expect default values to be overwritten by the usage of the annotation.
     */
    @Test
    public void swaggerNestedApisWithOverwrite() {
        AmbariSwaggerReader asr = new AmbariSwaggerReader(null, createMock(Log.class));
        Set<Class<?>> classes = new LinkedHashSet<>(Arrays.asList(NestedWithOverwrite.class, TopLevel4API.class));
        Swagger swagger = asr.read(classes);
        Assert.assertEquals(ImmutableSet.of("/toplevel3/{foo}/bar/list", "/toplevel4/top"), swagger.getPaths().keySet());
        AmbariSwaggerReaderTest.assertPathParamsExist(swagger, "/toplevel3/{foo}/bar/list", "foo");
    }

    /**
     * If an API is both top level (the class has a @Path annotation) and nested (class is a return type of an
     * API operation) then it should be treated as top level.
     */
    @Test
    public void swaggerApiThatIsBothTopLevelAndNestedIsCountedAsTopLevel() {
        AmbariSwaggerReader asr = new AmbariSwaggerReader(null, createMock(Log.class));
        Swagger swagger = asr.read(ImmutableSet.of(YetAnotherTopLevelAPI.class, NestedAndTopLevelAPI.class));
        Assert.assertEquals(ImmutableSet.of("/toplevel3/yetAnotherTop", "/canBeReachedFromTopToo/list"), swagger.getPaths().keySet());
    }
}

