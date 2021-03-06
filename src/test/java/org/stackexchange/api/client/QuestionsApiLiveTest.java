package org.stackexchange.api.client;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.impl.client.DecompressingHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.Before;
import org.junit.Test;
import org.stackexchange.api.constants.Site;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

public class QuestionsApiLiveTest {
    private QuestionsApi questionsApi;

    // fixtures

    @Before
    public final void before() {
        questionsApi = new QuestionsApi(new DecompressingHttpClient(new DefaultHttpClient()));
    }

    // tests

    // serverfault

    @Test
    public final void whenRequestIsPerformed_thenNoExceptions() throws ClientProtocolException, IOException {
        questionsApi.questions(50, Site.serverfault);
    }

    @Test
    public final void whenRequestIsPerformed_thenSuccess() throws ClientProtocolException, IOException {
        final HttpResponse httpResponse = questionsApi.questionsAsResponse(50, Site.serverfault);
        assertThat(httpResponse.getStatusLine().getStatusCode(), equalTo(200));
    }

    @Test
    public final void whenRequestIsPerformed_thenOutputIsJson() throws ClientProtocolException, IOException {
        final HttpResponse httpResponse = questionsApi.questionsAsResponse(50, Site.serverfault);
        final String contentType = httpResponse.getHeaders(HttpHeaders.CONTENT_TYPE)[0].getValue();
        assertThat(contentType, containsString("application/json"));
    }

    @Test
    public final void whenRequestIsPerformed_thenOutputIsCorrect() throws ClientProtocolException, IOException {
        final String responseBody = questionsApi.questions(50, Site.serverfault);
        assertThat(responseBody, notNullValue());
    }

    @Test
    public final void whenParsingOutputFromQuestionsApi_thenOutputContainsSomeQuestions() throws ClientProtocolException, IOException {
        final String questionsAsJson = questionsApi.questions(50, Site.serverfault);
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode rootNode = mapper.readTree(questionsAsJson);
        final ArrayNode questionsArray = (ArrayNode) rootNode.get("items");
        assertThat(questionsArray.size(), greaterThan(20));
    }

    // askubuntu

    @Test
    public final void givenOnAskUbuntu_whenInitialRequestIsPerformed_thenNoExceptions() throws ClientProtocolException, IOException {
        questionsApi.questions(100, Site.askubuntu);
    }

    @Test
    public final void givenOnAskUbuntu_whenParsingOutputFromQuestionsApi_thenOutputIsParsable() throws ClientProtocolException, IOException {
        final String questionsAsJson = questionsApi.questions(50, Site.askubuntu);
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode rootNode = mapper.readTree(questionsAsJson);
        final ArrayNode questionsArray = (ArrayNode) rootNode.get("items");
        assertThat(questionsArray.size(), greaterThan(20));
    }

    // character encoding

    @Test
    public final void givenOutputFromQuestionsApi_whenCharacterEncodingIsAnalyzed_thenOutputIsParsable() throws ClientProtocolException, IOException {
        final String questionsAsJson = questionsApi.questions(50, Site.askubuntu);
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode rootNode = mapper.readTree(questionsAsJson);
        final ArrayNode questionsArray = (ArrayNode) rootNode.get("items");
        for (final JsonNode question : questionsArray) {
            final String title = question.get(QuestionsApi.TITLE).toString();
            final String fullTweet = title.substring(1, title.length() - 1);
            StringEscapeUtils.escapeJava(fullTweet);
            System.out.println(fullTweet);
        }
    }

    // stackoverflow tag

    @Test
    public final void givenOnSO_whenRequestOnTagIsPerformed_thenNoExceptions() throws ClientProtocolException, IOException {
        questionsApi.questions(100, Site.askubuntu);
    }

}
