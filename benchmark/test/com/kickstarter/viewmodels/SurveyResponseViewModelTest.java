package com.kickstarter.viewmodels;


import IntentKey.SURVEY_RESPONSE;
import SurveyResponse.Urls;
import SurveyResponse.Urls.Web;
import SurveyResponseViewModel.ViewModel;
import android.content.Intent;
import com.kickstarter.KSRobolectricTestCase;
import com.kickstarter.mock.factories.SurveyResponseFactory;
import com.kickstarter.models.SurveyResponse;
import okhttp3.Request;
import org.junit.Test;
import rx.observers.TestSubscriber;


public class SurveyResponseViewModelTest extends KSRobolectricTestCase {
    private ViewModel vm;

    private final TestSubscriber<Void> goBack = new TestSubscriber();

    private final TestSubscriber<Void> showConfirmationDialog = new TestSubscriber();

    private final TestSubscriber<String> webViewUrl = new TestSubscriber();

    @Test
    public void testGoBack() {
        setUpEnvironment(environment());
        this.vm.inputs.okButtonClicked();
        this.goBack.assertValueCount(1);
    }

    @Test
    public void testSubmitSuccessful_Redirect_ShowConfirmationDialog() {
        final String surveyUrl = "https://kck.str/projects/param/heyo/surveys/123";
        final SurveyResponse.Urls urlsEnvelope = Urls.builder().web(Web.builder().survey(surveyUrl).build()).build();
        final SurveyResponse surveyResponse = SurveyResponseFactory.surveyResponse().toBuilder().urls(urlsEnvelope).build();
        final Request projectSurveyRequest = new Request.Builder().url(surveyUrl).build();
        final Request projectRequest = new Request.Builder().url("https://kck.str/projects/param/heyo").tag(projectSurveyRequest).build();
        setUpEnvironment(environment());
        this.vm.intent(new Intent().putExtra(SURVEY_RESPONSE, surveyResponse));
        // Survey loads. Successful submit redirects to project uri.
        this.vm.inputs.projectSurveyUriRequest(projectSurveyRequest);
        this.vm.inputs.projectUriRequest(projectRequest);
        // Success confirmation dialog is shown.
        this.showConfirmationDialog.assertValueCount(1);
    }

    @Test
    public void testWebViewUrl() {
        final SurveyResponse surveyResponse = SurveyResponseFactory.surveyResponse();
        setUpEnvironment(environment());
        this.vm.intent(new Intent().putExtra(SURVEY_RESPONSE, surveyResponse));
        this.webViewUrl.assertValues(surveyResponse.urls().web().survey());
    }
}

