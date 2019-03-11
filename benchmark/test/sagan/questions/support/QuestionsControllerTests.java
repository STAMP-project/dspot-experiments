package sagan.questions.support;


import java.util.List;
import java.util.stream.Collectors;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.ui.ExtendedModelMap;
import sagan.projects.Project;
import sagan.projects.support.ProjectMetadataService;


/**
 * Unit tests for {@link QuestionsController}.
 */
public class QuestionsControllerTests {
    @Mock
    private ProjectMetadataService projectMetadataService;

    @Mock
    private StackOverflowClient stackOverflow;

    private QuestionsController questionsController;

    private ExtendedModelMap model;

    @Test
    public void index() throws Exception {
        MatcherAssert.assertThat(questionsController.show(model), Matchers.equalTo("questions/index"));
        MatcherAssert.assertThat(((List<Question>) (model.get("questions"))).stream().map(( question) -> question.title).collect(Collectors.toList()), Matchers.contains("Question 1", "Question 2"));
        MatcherAssert.assertThat(((List<Project>) (model.get("projects"))).stream().map(Project::getName).collect(Collectors.toList()), Matchers.contains("Spring Data", "Spring Framework"));
    }
}

