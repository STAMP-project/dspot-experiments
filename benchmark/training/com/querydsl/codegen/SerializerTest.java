/**
 * Copyright 2015, The Querydsl Team (http://www.querydsl.com/team)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.querydsl.codegen;


import SimpleSerializerConfig.DEFAULT;
import com.mysema.codegen.JavaWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Collections;
import org.junit.Test;


public class SerializerTest {
    private EntityType type;

    private Writer writer = new StringWriter();

    private TypeMappings typeMappings = new JavaTypeMappings();

    @Test
    public void entitySerializer() throws Exception {
        new EntitySerializer(typeMappings, Collections.<String>emptyList()).serialize(type, DEFAULT, new JavaWriter(writer));
    }

    @Test
    public void entitySerializer2() throws Exception {
        new EntitySerializer(typeMappings, Collections.<String>emptyList()).serialize(type, new SimpleSerializerConfig(true, true, true, true, ""), new JavaWriter(writer));
    }

    @Test
    public void embeddableSerializer() throws Exception {
        new EmbeddableSerializer(typeMappings, Collections.<String>emptyList()).serialize(type, DEFAULT, new JavaWriter(writer));
    }

    @Test
    public void supertypeSerializer() throws IOException {
        new SupertypeSerializer(typeMappings, Collections.<String>emptyList()).serialize(type, DEFAULT, new JavaWriter(writer));
    }

    @Test
    public void projectionSerializer() throws IOException {
        new ProjectionSerializer(typeMappings).serialize(type, DEFAULT, new JavaWriter(writer));
    }
}

