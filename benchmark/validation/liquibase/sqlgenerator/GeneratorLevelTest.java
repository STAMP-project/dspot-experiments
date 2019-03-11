package liquibase.sqlgenerator;


import SqlGenerator.PRIORITY_DATABASE;
import SqlGenerator.PRIORITY_DEFAULT;
import org.junit.Assert;
import org.junit.Test;


public class GeneratorLevelTest {
    @SuppressWarnings("unchecked")
    @Test
    public void checkLevelsAndNaming() {
        for (SqlGenerator generator : SqlGeneratorFactory.getInstance().getGenerators()) {
            int specializationlevel = generator.getPriority();
            String className = generator.getClass().getName();
            if (className.contains(".ext.")) {
                // not one to test, a test class
            } else
                if (className.endsWith("CreateTableGeneratorInformix")) {
                    // had to change level for some reason
                } else
                    if (className.endsWith("Generator")) {
                        Assert.assertEquals(("Incorrect level/naming convention for " + className), PRIORITY_DEFAULT, specializationlevel);
                    } else {
                        Assert.assertEquals(("Incorrect level/naming convention for " + className), PRIORITY_DATABASE, specializationlevel);
                    }


        }
    }
}

