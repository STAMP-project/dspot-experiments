package org.hibernate.test.tool.schema.internal;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.relational.Namespace;
import org.hibernate.boot.model.relational.Namespace.Name;
import org.hibernate.boot.model.relational.QualifiedTableName;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.jdbc.env.spi.IdentifierHelper;
import org.hibernate.engine.jdbc.internal.Formatter;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.ForeignKey;
import org.hibernate.mapping.Table;
import org.hibernate.tool.schema.extract.internal.TableInformationImpl;
import org.hibernate.tool.schema.extract.spi.ColumnInformation;
import org.hibernate.tool.schema.extract.spi.DatabaseInformation;
import org.hibernate.tool.schema.extract.spi.ForeignKeyInformation;
import org.hibernate.tool.schema.extract.spi.ForeignKeyInformation.ColumnReferenceMapping;
import org.hibernate.tool.schema.extract.spi.InformationExtractor;
import org.hibernate.tool.schema.extract.spi.NameSpaceTablesInformation;
import org.hibernate.tool.schema.extract.spi.TableInformation;
import org.hibernate.tool.schema.internal.AbstractSchemaMigrator;
import org.hibernate.tool.schema.internal.exec.GenerationTarget;
import org.hibernate.tool.schema.spi.ExecutionOptions;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 *
 *
 * @author Milo van der Zee
 */
public class CheckForExistingForeignKeyTest {
    private class SchemaMigrator extends AbstractSchemaMigrator {
        /**
         * Needed constructor.
         */
        public SchemaMigrator() {
            super(null, null);
        }

        /**
         * Needed implementation. Not used in test.
         */
        @Override
        protected NameSpaceTablesInformation performTablesMigration(Metadata metadata, DatabaseInformation existingDatabase, ExecutionOptions options, Dialect dialect, Formatter formatter, Set<String> exportIdentifiers, boolean tryToCreateCatalogs, boolean tryToCreateSchemas, Set<Identifier> exportedCatalogs, Namespace namespace, GenerationTarget[] targets) {
            return null;
        }
    }

    private class ColumnReferenceMappingImpl implements ColumnReferenceMapping {
        private ColumnInformation referencingColumnMetadata;

        private ColumnInformation referencedColumnMetadata;

        public ColumnReferenceMappingImpl(ColumnInformation referencingColumnMetadata, ColumnInformation referencedColumnMetadata) {
            this.referencingColumnMetadata = referencingColumnMetadata;
            this.referencedColumnMetadata = referencedColumnMetadata;
        }

        @Override
        public ColumnInformation getReferencingColumnMetadata() {
            return referencingColumnMetadata;
        }

        @Override
        public ColumnInformation getReferencedColumnMetadata() {
            return referencedColumnMetadata;
        }
    }

    private class IdentifierHelperImpl implements IdentifierHelper {
        @Override
        public Identifier normalizeQuoting(Identifier identifier) {
            return null;
        }

        @Override
        public Identifier toIdentifier(String text) {
            return null;
        }

        @Override
        public Identifier toIdentifier(String text, boolean quoted) {
            return null;
        }

        @Override
        public Identifier applyGlobalQuoting(String text) {
            return null;
        }

        @Override
        public boolean isReservedWord(String word) {
            return false;
        }

        @Override
        public String toMetaDataCatalogName(Identifier catalogIdentifier) {
            return null;
        }

        @Override
        public String toMetaDataSchemaName(Identifier schemaIdentifier) {
            return null;
        }

        @Override
        public String toMetaDataObjectName(Identifier identifier) {
            return identifier.getText();
        }
    }

    /**
     * If the key has no name it should never be found. Result is that those keys are always recreated. But keys always
     * have a name so this is no problem.
     *
     * @throws NoSuchMethodException
     * 		- error
     * @throws SecurityException
     * 		- error
     * @throws IllegalAccessException
     * 		- error
     * @throws IllegalArgumentException
     * 		- error
     * @throws InvocationTargetException
     * 		- error
     */
    @Test
    public void testForeignKeyWithoutName() throws IllegalAccessException, IllegalArgumentException, NoSuchMethodException, SecurityException, InvocationTargetException {
        // Get the private method
        Method method = AbstractSchemaMigrator.class.getDeclaredMethod("checkForExistingForeignKey", ForeignKey.class, TableInformation.class);
        method.setAccessible(true);
        // foreignKey name with same name should match
        ForeignKey foreignKey = new ForeignKey();
        TableInformation tableInformation = new TableInformationImpl(null, null, null, false, null);
        boolean found = ((boolean) (method.invoke(new CheckForExistingForeignKeyTest.SchemaMigrator(), foreignKey, tableInformation)));
        Assert.assertFalse("Key should not be found", found);
    }

    /**
     * Test key not found if tableinformation is missing.
     *
     * @throws NoSuchMethodException
     * 		- error
     * @throws SecurityException
     * 		- error
     * @throws IllegalAccessException
     * 		- error
     * @throws IllegalArgumentException
     * 		- error
     * @throws InvocationTargetException
     * 		- error
     */
    @Test
    public void testMissingTableInformation() throws IllegalAccessException, IllegalArgumentException, NoSuchMethodException, SecurityException, InvocationTargetException {
        // Get the private method
        Method method = AbstractSchemaMigrator.class.getDeclaredMethod("checkForExistingForeignKey", ForeignKey.class, TableInformation.class);
        method.setAccessible(true);
        // foreignKey name with same name should match
        ForeignKey foreignKey = new ForeignKey();
        foreignKey.setName("objectId2id");
        boolean found = ((boolean) (method.invoke(new CheckForExistingForeignKeyTest.SchemaMigrator(), foreignKey, null)));
        Assert.assertFalse("Key should not be found", found);
    }

    /**
     * Check detection of existing foreign keys with the same name exists.
     *
     * @throws SecurityException
     * 		- error
     * @throws NoSuchMethodException
     * 		- error
     * @throws InvocationTargetException
     * 		- error
     * @throws IllegalArgumentException
     * 		- error
     * @throws IllegalAccessException
     * 		- error
     * @throws NoSuchFieldException
     * 		- error
     */
    @Test
    public void testKeyWithSameNameExists() throws IllegalAccessException, IllegalArgumentException, NoSuchFieldException, NoSuchMethodException, SecurityException, InvocationTargetException {
        // Get the private method
        Method method = AbstractSchemaMigrator.class.getDeclaredMethod("checkForExistingForeignKey", ForeignKey.class, TableInformation.class);
        method.setAccessible(true);
        ForeignKey foreignKey = new ForeignKey();
        foreignKey.setName("objectId2id");
        foreignKey.addColumn(new Column("id"));
        foreignKey.setReferencedTable(new Table("table2"));
        InformationExtractor informationExtractor = Mockito.mock(InformationExtractor.class);
        IdentifierHelper identifierHelper = new CheckForExistingForeignKeyTest.IdentifierHelperImpl();
        List<ForeignKeyInformation> fks = new ArrayList<>();
        fks.add(new org.hibernate.tool.schema.extract.internal.ForeignKeyInformationImpl(new Identifier("objectId2id", false), new ArrayList()));
        Mockito.when(informationExtractor.getForeignKeys(Mockito.any())).thenReturn(fks);
        Name schemaName = new Name(new Identifier("-", false), new Identifier("-", false));
        QualifiedTableName tableName = new QualifiedTableName(schemaName, new Identifier("-", false));
        TableInformation tableInformation = new TableInformationImpl(informationExtractor, identifierHelper, tableName, false, null);
        // foreignKey name with same name should match
        boolean found = ((boolean) (method.invoke(new CheckForExistingForeignKeyTest.SchemaMigrator(), foreignKey, tableInformation)));
        Assert.assertTrue("Key should be found", found);
    }

    /**
     * Check detection of existing foreign keys with the same name exists.
     *
     * @throws SecurityException
     * 		- error
     * @throws NoSuchMethodException
     * 		- error
     * @throws InvocationTargetException
     * 		- error
     * @throws IllegalArgumentException
     * 		- error
     * @throws IllegalAccessException
     * 		- error
     * @throws NoSuchFieldException
     * 		- error
     */
    @Test
    public void testKeyWithSameNameNotExists() throws IllegalAccessException, IllegalArgumentException, NoSuchFieldException, NoSuchMethodException, SecurityException, InvocationTargetException {
        // Get the private method
        Method method = AbstractSchemaMigrator.class.getDeclaredMethod("checkForExistingForeignKey", ForeignKey.class, TableInformation.class);
        method.setAccessible(true);
        ForeignKey foreignKey = new ForeignKey();
        foreignKey.setName("objectId2id_1");
        foreignKey.addColumn(new Column("id"));
        foreignKey.setReferencedTable(new Table("table2"));
        InformationExtractor informationExtractor = Mockito.mock(InformationExtractor.class);
        IdentifierHelper identifierHelper = new CheckForExistingForeignKeyTest.IdentifierHelperImpl();
        List<ForeignKeyInformation> fks = new ArrayList<>();
        fks.add(new org.hibernate.tool.schema.extract.internal.ForeignKeyInformationImpl(new Identifier("objectId2id_2", false), new ArrayList()));
        Mockito.when(informationExtractor.getForeignKeys(Mockito.any())).thenReturn(fks);
        Name schemaName = new Name(new Identifier("-", false), new Identifier("-", false));
        QualifiedTableName tableName = new QualifiedTableName(schemaName, new Identifier("-", false));
        TableInformation tableInformation = new TableInformationImpl(informationExtractor, identifierHelper, tableName, false, null);
        // foreignKey name with same name should match
        boolean found = ((boolean) (method.invoke(new CheckForExistingForeignKeyTest.SchemaMigrator(), foreignKey, tableInformation)));
        Assert.assertFalse("Key should not be found", found);
    }

    /**
     * Check detection of existing foreign key with the same mappings for a simple mapping (table1.objectId =>
     * table2.id).
     *
     * @throws SecurityException
     * 		- error
     * @throws NoSuchMethodException
     * 		- error
     * @throws InvocationTargetException
     * 		- error
     * @throws IllegalArgumentException
     * 		- error
     * @throws IllegalAccessException
     * 		- error
     * @throws NoSuchFieldException
     * 		- error
     */
    @Test
    public void testCheckForExistingForeignKeyOne2One() throws IllegalAccessException, IllegalArgumentException, NoSuchFieldException, NoSuchMethodException, SecurityException, InvocationTargetException {
        // Get the private method
        Method method = AbstractSchemaMigrator.class.getDeclaredMethod("checkForExistingForeignKey", ForeignKey.class, TableInformation.class);
        method.setAccessible(true);
        ForeignKey foreignKey = new ForeignKey();
        foreignKey.setName("objectId2id_1");// Make sure the match is not successful based on key name

        foreignKey.addColumn(new Column("id"));
        foreignKey.setReferencedTable(new Table("table2"));
        Name schemaName = new Name(new Identifier("-", false), new Identifier("-", false));
        InformationExtractor informationExtractor = Mockito.mock(InformationExtractor.class);
        IdentifierHelper identifierHelper = new CheckForExistingForeignKeyTest.IdentifierHelperImpl();
        List<ForeignKeyInformation> fks = new ArrayList<>();
        fks.add(getForeignKeyInformation("table2", "id", "object2Id_2"));
        Mockito.when(informationExtractor.getForeignKeys(Mockito.any())).thenReturn(fks);
        QualifiedTableName tableName = new QualifiedTableName(schemaName, new Identifier("-", false));
        TableInformation tableInformation = new TableInformationImpl(informationExtractor, identifierHelper, tableName, false, null);
        AbstractSchemaMigrator schemaMigrator = new CheckForExistingForeignKeyTest.SchemaMigrator();
        // Check single-column-key to single-column-key, existing (table1.objectId => table2.id)
        boolean found = ((boolean) (method.invoke(schemaMigrator, foreignKey, tableInformation)));
        Assert.assertTrue("Key should be found", found);
    }

    /**
     * Check detection of not existing foreign key with the same mappings for a simple mapping (table1.objectId =>
     * table2.id).
     *
     * @throws SecurityException
     * 		- error
     * @throws NoSuchMethodException
     * 		- error
     * @throws InvocationTargetException
     * 		- error
     * @throws IllegalArgumentException
     * 		- error
     * @throws IllegalAccessException
     * 		- error
     * @throws NoSuchFieldException
     * 		- error
     */
    @Test
    public void testCheckForNotExistingForeignKeyOne2One() throws IllegalAccessException, IllegalArgumentException, NoSuchFieldException, NoSuchMethodException, SecurityException, InvocationTargetException {
        // Get the private method
        Method method = AbstractSchemaMigrator.class.getDeclaredMethod("checkForExistingForeignKey", ForeignKey.class, TableInformation.class);
        method.setAccessible(true);
        ForeignKey foreignKey = new ForeignKey();
        foreignKey.setName("objectId2id_1");// Make sure the match is not successful based on key name

        foreignKey.addColumn(new Column("id"));
        foreignKey.setReferencedTable(new Table("table2"));
        Name schemaName = new Name(new Identifier("-", false), new Identifier("-", false));
        InformationExtractor informationExtractor = Mockito.mock(InformationExtractor.class);
        IdentifierHelper identifierHelper = new CheckForExistingForeignKeyTest.IdentifierHelperImpl();
        List<ForeignKeyInformation> fks = new ArrayList<>();
        fks.add(getForeignKeyInformation("table2", "blah", "blahKey_001"));
        fks.add(getForeignKeyInformation("table3", "id", "blahKey_002"));
        fks.add(getForeignKeyInformation("table3", "blah", "blahKey_003"));
        Mockito.when(informationExtractor.getForeignKeys(Mockito.any())).thenReturn(fks);
        QualifiedTableName tableName = new QualifiedTableName(schemaName, new Identifier("-", false));
        TableInformation tableInformation = new TableInformationImpl(informationExtractor, identifierHelper, tableName, false, null);
        AbstractSchemaMigrator schemaMigrator = new CheckForExistingForeignKeyTest.SchemaMigrator();
        // Check single-column-key to single-column-key, existing (table1.objectId => table2.id)
        boolean found = ((boolean) (method.invoke(schemaMigrator, foreignKey, tableInformation)));
        Assert.assertFalse("Key should not be found", found);
    }
}

