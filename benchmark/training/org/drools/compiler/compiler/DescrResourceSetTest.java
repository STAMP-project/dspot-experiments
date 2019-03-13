package org.drools.compiler.compiler;


import java.io.File;
import java.io.FileInputStream;
import java.util.Set;
import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.compiler.lang.descr.PackageDescr;
import org.drools.core.io.impl.InputStreamResource;
import org.junit.Test;
import org.kie.internal.builder.KnowledgeBuilderConfiguration;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DescrResourceSetTest {
    protected static final transient Logger logger = LoggerFactory.getLogger(KnowledgeBuilderImpl.class);

    private static final PackageDescrResourceVisitor visitor = new PackageDescrResourceVisitor();

    private static final KnowledgeBuilderConfiguration conf = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration();

    @Test
    public void drlFilesTest() throws Exception {
        Set<File> drlFiles = getDrlFiles();
        for (File drl : drlFiles) {
            final DrlParser parser = new DrlParser(getLanguageLevel());
            InputStreamResource resource = new InputStreamResource(new FileInputStream(drl));
            PackageDescr pkgDescr = parser.parse(resource);
            if (parser.hasErrors()) {
                continue;
            }
            DescrResourceSetTest.visitor.visit(pkgDescr);
        }
        DescrResourceSetTest.logger.debug(((drlFiles.size()) + " drl tested."));
    }
}

