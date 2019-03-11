/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.document;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class DocumentOperationsApplierForNonOverlappingRegionsWithDocumentFileTest {
    private static final String FILE_PATH = "psvm.java";

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private File temporaryFile;

    private DocumentOperationsApplierForNonOverlappingRegions applier;

    @Test
    public void insertAtStartOfTheDocumentShouldSucceed() throws IOException {
        writeContentToTemporaryFile("static void main(String[] args) {}");
        try (DocumentFile documentFile = new DocumentFile(temporaryFile, StandardCharsets.UTF_8)) {
            applier = new DocumentOperationsApplierForNonOverlappingRegions(documentFile);
            applier.addDocumentOperation(new InsertDocumentOperation(0, 0, "public "));
            applier.apply();
        }
        try (FileInputStream stream = new FileInputStream(temporaryFile)) {
            final String actualContent = new String(readAllBytes(stream));
            Assert.assertEquals("public static void main(String[] args) {}", actualContent);
        }
    }

    @Test
    public void removeTokenShouldSucceed() throws IOException {
        writeContentToTemporaryFile("public static void main(String[] args) {}");
        try (DocumentFile documentFile = new DocumentFile(temporaryFile, StandardCharsets.UTF_8)) {
            applier = new DocumentOperationsApplierForNonOverlappingRegions(documentFile);
            applier.addDocumentOperation(new DeleteDocumentOperation(0, 0, 7, 13));
            applier.apply();
        }
        try (FileInputStream stream = new FileInputStream(temporaryFile)) {
            final String actualContent = new String(readAllBytes(stream));
            Assert.assertEquals("public  void main(String[] args) {}", actualContent);
        }
    }

    @Test
    public void insertAndRemoveTokensShouldSucceed() throws IOException {
        final String code = "static void main(final String[] args) {}";
        writeContentToTemporaryFile(code);
        try (DocumentFile documentFile = new DocumentFile(temporaryFile, StandardCharsets.UTF_8)) {
            applier = new DocumentOperationsApplierForNonOverlappingRegions(documentFile);
            applier.addDocumentOperation(new InsertDocumentOperation(0, 0, "public "));
            applier.addDocumentOperation(new DeleteDocumentOperation(0, 0, 17, 23));
            applier.apply();
        }
        try (FileInputStream stream = new FileInputStream(temporaryFile)) {
            final String actualContent = new String(readAllBytes(stream));
            Assert.assertEquals("public static void main(String[] args) {}", actualContent);
        }
    }

    @Test
    public void insertAndDeleteVariousTokensShouldSucceed() throws IOException {
        final String code = "void main(String[] args) {}";
        writeContentToTemporaryFile(code);
        try (DocumentFile documentFile = new DocumentFile(temporaryFile, StandardCharsets.UTF_8)) {
            applier = new DocumentOperationsApplierForNonOverlappingRegions(documentFile);
            applier.addDocumentOperation(new InsertDocumentOperation(0, 0, "public "));
            applier.addDocumentOperation(new InsertDocumentOperation(0, 0, "static "));
            applier.addDocumentOperation(new DeleteDocumentOperation(0, 0, 0, 4));
            applier.addDocumentOperation(new InsertDocumentOperation(0, 10, "final "));
            applier.addDocumentOperation(new DeleteDocumentOperation(0, 0, 25, 27));
            applier.apply();
        }
        try (FileInputStream stream = new FileInputStream(temporaryFile)) {
            final String actualContent = new String(readAllBytes(stream));
            Assert.assertEquals("public static  main(final String[] args) ", actualContent);
        }
    }

    @Test
    public void replaceATokenShouldSucceed() throws IOException {
        final String code = "int main(String[] args) {}";
        writeContentToTemporaryFile(code);
        try (DocumentFile documentFile = new DocumentFile(temporaryFile, StandardCharsets.UTF_8)) {
            applier = new DocumentOperationsApplierForNonOverlappingRegions(documentFile);
            applier.addDocumentOperation(new ReplaceDocumentOperation(0, 0, 0, "int".length(), "void"));
            applier.apply();
        }
        try (FileInputStream stream = new FileInputStream(temporaryFile)) {
            final String actualContent = new String(readAllBytes(stream));
            Assert.assertEquals("void main(String[] args) {}", actualContent);
        }
    }

    @Test
    public void replaceVariousTokensShouldSucceed() throws IOException {
        final String code = "int main(String[] args) {}";
        writeContentToTemporaryFile(code);
        final List<DocumentOperation> documentOperations = new LinkedList<>();
        documentOperations.add(new ReplaceDocumentOperation(0, 0, 0, "int".length(), "void"));
        documentOperations.add(new ReplaceDocumentOperation(0, 0, 4, (4 + ("main".length())), "foo"));
        documentOperations.add(new ReplaceDocumentOperation(0, 0, 9, (9 + ("String".length())), "CharSequence"));
        shuffleAndApplyOperations(documentOperations);
        try (FileInputStream stream = new FileInputStream(temporaryFile)) {
            final String actualContent = new String(readAllBytes(stream));
            Assert.assertEquals("void foo(CharSequence[] args) {}", actualContent);
        }
    }

    @Test
    public void insertDeleteAndReplaceVariousTokensShouldSucceed() throws IOException {
        final String code = "static int main(CharSequence[] args) {}";
        writeContentToTemporaryFile(code);
        final List<DocumentOperation> documentOperations = new LinkedList<>();
        documentOperations.add(new InsertDocumentOperation(0, 0, "public"));
        documentOperations.add(new DeleteDocumentOperation(0, 0, 0, 6));
        documentOperations.add(new ReplaceDocumentOperation(0, 0, 7, (7 + ("int".length())), "void"));
        documentOperations.add(new InsertDocumentOperation(0, 16, "final "));
        documentOperations.add(new ReplaceDocumentOperation(0, 0, 16, (16 + ("CharSequence".length())), "String"));
        shuffleAndApplyOperations(documentOperations);
        try (FileInputStream stream = new FileInputStream(temporaryFile)) {
            final String actualContent = new String(readAllBytes(stream));
            Assert.assertEquals("public void main(final String[] args) {}", actualContent);
        }
    }
}

