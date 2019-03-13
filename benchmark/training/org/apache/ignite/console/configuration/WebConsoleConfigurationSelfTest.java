/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ignite.console.configuration;


import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.junit.Test;


/**
 * Check difference of Ignite configuration with Ignite Web Console "Configuration" screen.
 */
public class WebConsoleConfigurationSelfTest {
    /**
     *
     */
    protected static final Set<String> EMPTY_FIELDS = Collections.emptySet();

    /**
     *
     */
    protected static final Set<String> SPI_EXCLUDED_FIELDS = Collections.singleton("name");

    /**
     * Map of properties metadata by class.
     */
    protected final Map<Class<?>, MetadataInfo> metadata = new HashMap<>();

    /**
     * Check an accordance of possible to configure properties and configuration classes.
     */
    @Test
    public void testConfiguration() {
        prepareMetadata();
        HashMap<Class<?>, WrongFields> diff = new HashMap<>();
        for (Map.Entry<Class<?>, MetadataInfo> ent : metadata.entrySet()) {
            Class<?> cls = ent.getKey();
            MetadataInfo meta = ent.getValue();
            Set<String> props = meta.getGeneratedFields();
            Set<String> knownDeprecated = meta.getDeprecatedFields();
            Set<String> excludeFields = meta.getExcludedFields();
            boolean clsDeprecated = (cls.getAnnotation(Deprecated.class)) != null;
            Map<String, FieldProcessingInfo> clsProps = new HashMap<>();
            for (Method m : cls.getMethods()) {
                String mtdName = m.getName();
                String propName = (((mtdName.length()) > 3) && ((mtdName.startsWith("get")) || (mtdName.startsWith("set")))) ? (mtdName.toLowerCase().charAt(3)) + (mtdName.substring(4)) : ((mtdName.length()) > 2) && (mtdName.startsWith("is")) ? (mtdName.toLowerCase().charAt(2)) + (mtdName.substring(3)) : null;
                boolean deprecated = clsDeprecated || ((m.getAnnotation(Deprecated.class)) != null);
                if ((propName != null) && (!(excludeFields.contains(propName)))) {
                    clsProps.put(propName, clsProps.getOrDefault(propName, new FieldProcessingInfo(propName, 0, deprecated)).deprecated(deprecated).next());
                }
            }
            Set<String> missedFields = new HashSet<>();
            Set<String> deprecatedFields = new HashSet<>();
            for (Map.Entry<String, FieldProcessingInfo> e : clsProps.entrySet()) {
                String prop = e.getKey();
                FieldProcessingInfo info = e.getValue();
                if ((((info.getOccurrence()) > 1) && (!(info.isDeprecated()))) && (!(props.contains(prop))))
                    missedFields.add(prop);

                if (((((info.getOccurrence()) > 1) && (info.isDeprecated())) && (!(props.contains(prop)))) && (!(knownDeprecated.contains(prop))))
                    deprecatedFields.add(prop);

            }
            Set<String> rmvFields = new HashSet<>();
            for (String p : props)
                if (!(clsProps.containsKey(p)))
                    rmvFields.add(p);


            for (String p : knownDeprecated)
                if (!(clsProps.containsKey(p)))
                    rmvFields.add(p);


            WrongFields fields = new WrongFields(missedFields, deprecatedFields, rmvFields);
            if (fields.nonEmpty()) {
                diff.put(cls, fields);
                log(("Result for class: " + (cls.getName())));
                if (!(missedFields.isEmpty())) {
                    log("  Missed");
                    for (String fld : missedFields)
                        log(("    " + fld));

                }
                if (!(deprecatedFields.isEmpty())) {
                    log("  Deprecated");
                    for (String fld : deprecatedFields)
                        log(("    " + fld));

                }
                if (!(rmvFields.isEmpty())) {
                    log("  Removed");
                    for (String fld : rmvFields)
                        log(("    " + fld));

                }
                log("");
            }
        }
        // Test will pass only if no difference found between IgniteConfiguration and Web Console generated configuration.
        assert diff.isEmpty() : "Found difference between IgniteConfiguration and Web Console";
    }
}

