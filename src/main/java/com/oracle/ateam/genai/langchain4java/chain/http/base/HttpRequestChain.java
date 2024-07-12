/*******************************************************************************
 * Oracle OCI Generative AI LangChain For Java version 1.0.1.0.
 *
 * Copyright (c)  2024,  Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 ******************************************************************************/
package com.oracle.ateam.genai.langchain4java.chain.http.base;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import com.hw.langchain.base.language.BaseLanguageModel;
import com.hw.langchain.chains.base.Chain;
import com.hw.langchain.chains.llm.LLMChain;
import com.hw.langchain.prompts.base.BasePromptTemplate;
import com.oracle.ateam.genai.langchain4java.chain.http.requests.TextRequestsWrapper;

import static com.hw.langchain.chains.api.prompt.Prompt.API_RESPONSE_PROMPT;

/**
 * The `HttpRequestChain` class is a chain that enables making HTTP requests to
 * a specified
 * API endpoint using a provided language model. It leverages the
 * `TextRequestsWrapper` for
 * sending requests and handles API responses by passing them through an LLM
 * (Language Model)
 * chain for generating answers.
 * 
 * This class is suitable for tasks where you need to interact with a remote API
 * to retrieve
 * information or perform actions based on user input.
 * 
 * Features:
 * - Supports making HTTP GET requests to a specified API endpoint.
 * - Integrates with the `TextRequestsWrapper` for handling HTTP requests and
 * responses.
 * - Uses an LLM chain to generate answers based on the API response and user
 * input.
 * - Provides methods for setting the API URL, query parameters, and request
 * headers.
 * - URL parameters are automatically URL-encoded.
 * - Returns the response as an answer to the user's input.
 * 
 * Example usage:
 * ```java
 * Map<String, Object> inputs = Map.of("question", "What's the weather today?");
 * String apiURL = "https://example.com/weather";
 * HttpRequestChain httpChain = HttpRequestChain.usingApiURL(llm, apiURL,
 * inputs);
 * Map<String, String> result = httpChain.call(inputs);
 * ```
 * 
 */
public class HttpRequestChain extends Chain {

    private final LLMChain apiAnswerChain;
    private final TextRequestsWrapper requestsWrapper;
    private final String apiUrl;
    private static final String QUESTION_KEY = "question";
    private static final String OUTPUT_KEY = "output";

    /**
     * Creates a new instance of HttpRequestChain.
     *
     * @param apiAnswerChain  The chain used to process the API response.
     * @param requestsWrapper A wrapper for making HTTP requests.
     * @param apiUrl          The URL of the API endpoint.
     */
    public HttpRequestChain(LLMChain apiAnswerChain, TextRequestsWrapper requestsWrapper,
            String apiUrl) {
        this.apiAnswerChain = apiAnswerChain;
        this.requestsWrapper = requestsWrapper;
        this.apiUrl = apiUrl;
    }

    /**
     * Returns the type of this chain.
     *
     * @return The chain type, which is "httpquest_chain".
     */
    @Override
    public String chainType() {
        return "httpquest_chain";
    }

    /**
     * Returns the list of input keys expected by this chain.
     *
     * @return A list containing the input key "question".
     */
    @Override
    public List<String> inputKeys() {
        return List.of(QUESTION_KEY);
    }

    /**
     * Returns the list of output keys produced by this chain.
     *
     * @return A list containing the output key "output".
     */
    @Override
    public List<String> outputKeys() {
        return List.of(OUTPUT_KEY);
    }

    /**
     * Executes the inner logic of the chain, including making the HTTP request and
     * processing the response.
     *
     * @param inputs A map of input values, with the key "question" containing the
     *               question to send to the API.
     * @return A map with the output key "output" containing the API response
     *         processed by the answer chain.
     */
    @Override
    public Map<String, String> innerCall(Map<String, Object> inputs) {
        var question = inputs.get(QUESTION_KEY);
        apiUrl.strip();
        String apiResponse = requestsWrapper.get(apiUrl);
        String answer = apiAnswerChain
                .predict(Map.of(QUESTION_KEY, question, "api_url", apiUrl, "api_response", apiResponse));
        return Map.of(OUTPUT_KEY, answer);
    }

    /**
     * Creates a new instance of HttpRequestChain using a given API URL, language
     * model, and parameters.
     *
     * @param llm        The base language model for processing responses.
     * @param apiURL     The URL of the API endpoint.
     * @param parameters A map of URL parameters to include in the request.
     * @return A new instance of HttpRequestChain with the specified configuration.
     */
    public static HttpRequestChain usingApiURL(BaseLanguageModel llm, String apiURL, Map<String, Object> parameters) {
        return usingApiURL(llm, apiURL, parameters, null, API_RESPONSE_PROMPT);
    }

    /**
     * Creates a new instance of HttpRequestChain using a given API URL, language
     * model, parameters, custom headers,
     * and an API response prompt template.
     *
     * @param llm               The base language model for processing responses.
     * @param apiURL            The URL of the API endpoint.
     * @param parameters        A map of URL parameters to include in the request.
     * @param headers           A map of custom HTTP headers to include in the
     *                          request.
     * @param apiResponsePrompt The prompt template for processing API responses.
     * @return A new instance of HttpRequestChain with the specified configuration.
     */
    public static HttpRequestChain usingApiURL(BaseLanguageModel llm, String apiURL, Map<String, Object> parameters,
            Map<String, String> headers, BasePromptTemplate apiResponsePrompt) {
        String apiURLWithParameters = buildURLParameters(apiURL, parameters);
        TextRequestsWrapper requestsWrapper = new TextRequestsWrapper(headers);
        LLMChain getAnswerChain = new LLMChain(llm, apiResponsePrompt);
        return new HttpRequestChain(getAnswerChain, requestsWrapper, apiURLWithParameters);
    }

    /**
     * Creates a new instance of HttpRequestChain using a given API URL, language
     * model, custom headers, and an API response prompt template.
     *
     * @param llm               The base language model for processing responses.
     * @param apiURL            The URL of the API endpoint.
     * @param headers           A map of custom HTTP headers to include in the
     *                          request.
     * @param apiResponsePrompt The prompt template for processing API responses.
     * @return A new instance of HttpRequestChain with the specified configuration.
     */
    public static HttpRequestChain usingApiURL(BaseLanguageModel llm, String apiURL,
            Map<String, String> headers, BasePromptTemplate apiResponsePrompt) {
        TextRequestsWrapper requestsWrapper = new TextRequestsWrapper(headers);
        LLMChain getAnswerChain = new LLMChain(llm, apiResponsePrompt);
        return new HttpRequestChain(getAnswerChain, requestsWrapper, apiURL);
    }

    /**
     * Builds a URL with URL parameters by appending the parameters to the given API
     * URL.
     *
     * @param apiURL     The base API URL.
     * @param parameters A map of URL parameters to include in the URL.
     * @return The API URL with appended URL parameters.
     */
    private static String buildURLParameters(String apiURL, Map<String, Object> parameters) {
        StringBuilder urlBuilder = new StringBuilder(apiURL);
        if (!parameters.isEmpty()) {
            urlBuilder.append("?");
            for (Map.Entry<String, Object> entry : parameters.entrySet()) {
                try {
                    String key = entry.getKey();
                    Object value = entry.getValue();

                    if (value != null) {
                        String encodedKey = URLEncoder.encode(key, "UTF-8");
                        String encodedValue = URLEncoder.encode(value.toString(), "UTF-8");
                        urlBuilder.append(encodedKey).append("=").append(encodedValue).append("&");
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            urlBuilder.deleteCharAt(urlBuilder.length() - 1); // Remove the trailing "&"
        }
        return urlBuilder.toString();
    }
}