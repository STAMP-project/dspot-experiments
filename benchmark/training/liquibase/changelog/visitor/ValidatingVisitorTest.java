package liquibase.changelog.visitor;


import java.util.ArrayList;
import java.util.List;
import liquibase.change.ColumnConfig;
import liquibase.change.core.CreateTableChange;
import liquibase.changelog.ChangeSet;
import liquibase.changelog.DatabaseChangeLog;
import liquibase.changelog.RanChangeSet;
import liquibase.database.Database;
import liquibase.database.core.MockDatabase;
import liquibase.exception.SetupException;
import liquibase.exception.ValidationErrors;
import org.junit.Assert;
import org.junit.Test;


public class ValidatingVisitorTest {
    private ChangeSet changeSet1;

    private ChangeSet changeSet2;

    @Test
    public void visit_successful() throws Exception {
        CreateTableChange change1 = new CreateTableChange();
        change1.setTableName("table1");
        ColumnConfig column1 = new ColumnConfig();
        change1.addColumn(column1);
        column1.setName("col1");
        column1.setType("int");
        CreateTableChange change2 = new CreateTableChange();
        change2.setTableName("table2");
        ColumnConfig column2 = new ColumnConfig();
        change2.addColumn(column2);
        column2.setName("col2");
        column2.setType("int");
        changeSet1.addChange(change1);
        changeSet2.addChange(change2);
        ValidatingVisitor handler = new ValidatingVisitor(new ArrayList<RanChangeSet>());
        handler.visit(changeSet1, new DatabaseChangeLog(), new MockDatabase(), null);
        handler.visit(changeSet2, new DatabaseChangeLog(), new MockDatabase(), null);
        Assert.assertTrue(handler.validationPassed());
    }

    @Test
    public void visit_setupException() throws Exception {
        changeSet1.addChange(new CreateTableChange() {
            @Override
            public void finishInitialization() throws SetupException {
                throw new SetupException("Test message");
            }
        });
        ValidatingVisitor handler = new ValidatingVisitor(new ArrayList<RanChangeSet>());
        handler.visit(changeSet1, new DatabaseChangeLog(), null, null);
        Assert.assertEquals(1, handler.getSetupExceptions().size());
        Assert.assertEquals("Test message", handler.getSetupExceptions().get(0).getMessage());
        Assert.assertFalse(handler.validationPassed());
    }

    @Test
    public void visit_duplicate() throws Exception {
        ValidatingVisitor handler = new ValidatingVisitor(new ArrayList<RanChangeSet>());
        handler.visit(changeSet1, new DatabaseChangeLog(), null, null);
        handler.visit(changeSet1, new DatabaseChangeLog(), null, null);
        Assert.assertEquals(1, handler.getDuplicateChangeSets().size());
        Assert.assertFalse(handler.validationPassed());
    }

    @Test
    public void visit_validateError() throws Exception {
        changeSet1.addChange(new CreateTableChange() {
            @Override
            public ValidationErrors validate(Database database) {
                ValidationErrors changeValidationErrors = new ValidationErrors();
                changeValidationErrors.addError("Test message");
                return changeValidationErrors;
            }
        });
        List<RanChangeSet> ran = new ArrayList<RanChangeSet>();
        ValidatingVisitor handler = new ValidatingVisitor(ran);
        handler.visit(changeSet1, new DatabaseChangeLog(), null, null);
        Assert.assertEquals(1, handler.getValidationErrors().getErrorMessages().size());
        Assert.assertTrue(handler.getValidationErrors().getErrorMessages().get(0).startsWith("Test message"));
        Assert.assertFalse(handler.validationPassed());
    }

    @Test
    public void visit_torunOnly() throws Exception {
        changeSet1.addChange(new CreateTableChange() {
            @Override
            public ValidationErrors validate(Database database) {
                ValidationErrors changeValidationErrors = new ValidationErrors();
                changeValidationErrors.addError("Test message");
                return changeValidationErrors;
            }
        });
        List<RanChangeSet> ran = new ArrayList<RanChangeSet>();
        ran.add(new RanChangeSet(changeSet1));
        ValidatingVisitor handler = new ValidatingVisitor(ran);
        handler.visit(changeSet1, new DatabaseChangeLog(), null, null);
        Assert.assertEquals(0, handler.getSetupExceptions().size());
        Assert.assertTrue(handler.validationPassed());
    }
}

