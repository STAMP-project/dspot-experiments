/**
 * Copyright 2006-2018 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.mybatis.generator.internal;


import PropertyRegistry.COMMENT_GENERATOR_SUPPRESS_DATE;
import java.io.StringReader;
import java.util.Properties;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mybatis.generator.api.CommentGenerator;
import org.mybatis.generator.api.GeneratedXmlFile;
import org.mybatis.generator.api.dom.DefaultXmlFormatter;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.codegen.XmlConstants;
import org.xml.sax.InputSource;


/**
 * This test is related to issue #87 where XML files are slightly different
 * after running through the XML merger.
 *
 * @author Jeff Butler
 */
public class XmlFileMergerTest {
    @Test
    public void testThatFilesAreTheSameAfterMerge() throws Exception {
        DefaultXmlFormatter xmlFormatter = new DefaultXmlFormatter();
        Properties p = new Properties();
        p.setProperty(COMMENT_GENERATOR_SUPPRESS_DATE, "true");
        CommentGenerator commentGenerator = new DefaultCommentGenerator();
        commentGenerator.addConfigurationProperties(p);
        Document document = new Document(XmlConstants.MYBATIS3_MAPPER_PUBLIC_ID, XmlConstants.MYBATIS3_MAPPER_SYSTEM_ID);
        document.setRootElement(getSqlMapElement(commentGenerator));
        GeneratedXmlFile generatedFile1 = new GeneratedXmlFile(document, "TestMapper.xml", "org.mybatis.test", "src", true, xmlFormatter);
        InputSource is1 = new InputSource(new StringReader(generatedFile1.getFormattedContent()));
        GeneratedXmlFile generatedFile2 = new GeneratedXmlFile(document, "TestMapper.xml", "org.mybatis.test", "src", true, xmlFormatter);
        InputSource is2 = new InputSource(new StringReader(generatedFile2.getFormattedContent()));
        String mergedSource = XmlFileMergerJaxp.getMergedSource(is1, is2, "TestMapper.xml");
        Assertions.assertEquals(generatedFile1.getFormattedContent(), mergedSource);
    }
}

