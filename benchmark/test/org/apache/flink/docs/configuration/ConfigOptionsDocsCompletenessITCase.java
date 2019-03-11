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
package org.apache.flink.docs.configuration;


import ConfigOptionsDocGenerator.OptionWithMetaInfo;
import Documentation.CommonOption;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.function.Predicate;
import org.apache.flink.configuration.description.Formatter;
import org.apache.flink.configuration.description.HtmlFormatter;
import org.junit.Test;


/**
 * This test verifies that all {@link ConfigOption ConfigOptions} in the configured
 * {@link ConfigOptionsDocGenerator#LOCATIONS locations} are documented and well-defined (i.e. no 2 options exist for
 * the same key with different descriptions/default values), and that the documentation does not refer to non-existent
 * options.
 */
public class ConfigOptionsDocsCompletenessITCase {
    private static final Formatter htmlFormatter = new HtmlFormatter();

    @Test
    public void testCommonSectionCompleteness() throws IOException, ClassNotFoundException {
        Map<String, ConfigOptionsDocsCompletenessITCase.DocumentedOption> documentedOptions = ConfigOptionsDocsCompletenessITCase.parseDocumentedCommonOptions();
        Map<String, ConfigOptionsDocsCompletenessITCase.ExistingOption> existingOptions = ConfigOptionsDocsCompletenessITCase.findExistingOptions(( optionWithMetaInfo) -> (optionWithMetaInfo.field.getAnnotation(CommonOption.class)) != null);
        ConfigOptionsDocsCompletenessITCase.compareDocumentedAndExistingOptions(documentedOptions, existingOptions);
    }

    @Test
    public void testFullReferenceCompleteness() throws IOException, ClassNotFoundException {
        Map<String, ConfigOptionsDocsCompletenessITCase.DocumentedOption> documentedOptions = ConfigOptionsDocsCompletenessITCase.parseDocumentedOptions();
        Map<String, ConfigOptionsDocsCompletenessITCase.ExistingOption> existingOptions = ConfigOptionsDocsCompletenessITCase.findExistingOptions(( ignored) -> true);
        ConfigOptionsDocsCompletenessITCase.compareDocumentedAndExistingOptions(documentedOptions, existingOptions);
    }

    private static final class ExistingOption extends ConfigOptionsDocsCompletenessITCase.Option {
        private final Class<?> containingClass;

        private ExistingOption(String key, String defaultValue, String description, Class<?> containingClass) {
            super(key, defaultValue, description);
            this.containingClass = containingClass;
        }
    }

    private static final class DocumentedOption extends ConfigOptionsDocsCompletenessITCase.Option {
        private final Path containingFile;

        private DocumentedOption(String key, String defaultValue, String description, Path containingFile) {
            super(key, defaultValue, description);
            this.containingFile = containingFile;
        }
    }

    private abstract static class Option {
        protected final String key;

        protected final String defaultValue;

        protected final String description;

        private Option(String key, String defaultValue, String description) {
            this.key = key;
            this.defaultValue = defaultValue;
            this.description = description;
        }

        @Override
        public int hashCode() {
            return ((key.hashCode()) + (defaultValue.hashCode())) + (description.hashCode());
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof ConfigOptionsDocsCompletenessITCase.Option)) {
                return false;
            }
            ConfigOptionsDocsCompletenessITCase.Option other = ((ConfigOptionsDocsCompletenessITCase.Option) (obj));
            return ((this.key.equals(other.key)) && (this.defaultValue.equals(other.defaultValue))) && (this.description.equals(other.description));
        }

        @Override
        public String toString() {
            return ((((("Option(key=" + (key)) + ", default=") + (defaultValue)) + ", description=") + (description)) + ')';
        }
    }
}

