/*******************************************************************************
 * Oracle OCI Generative AI LangChain For Java version 1.0.
 *
 * Copyright (c)  2024,  Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 ******************************************************************************/
package com.oracle.ateam.genai.langchain4java;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.hw.langchain.chains.llm.LLMChain;
import com.hw.langchain.prompts.prompt.PromptTemplate;
import com.oracle.ateam.genai.langchain4java.chain.http.base.HttpRequestChain;
import com.oracle.ateam.genai.langchain4java.chain.sql.oracle.OracleDatabase;
import com.oracle.ateam.genai.langchain4java.chain.sql.oracle.OracleDatabaseChain;
import com.oracle.ateam.genai.langchain4java.model.ModelParameters;
import com.oracle.ateam.genai.langchain4java.model.Property;
import com.oracle.ateam.genai.langchain4java.model.RequestChainPayload;
import com.oracle.ateam.genai.langchain4java.model.RequestMultipleChainsPayload;
import com.oracle.ateam.genai.langchain4java.model.RequestPayload;
import com.oracle.ateam.genai.langchain4java.llms.GenAICohereGenerationModel;

import jakarta.ws.rs.NotSupportedException;
import lombok.extern.slf4j.Slf4j;

import static com.oracle.ateam.genai.langchain4java.Utils.mergePromptwithToken;
import static com.oracle.ateam.genai.langchain4java.Utils.replaceTokenWithData;
import static com.oracle.ateam.genai.langchain4java.Utils.contains;
import static com.oracle.ateam.genai.langchain4java.chain.http.prompt.Prompt.HTTPREQUEST_RESPONSE_PROMPT;

import com.oracle.bmc.Region;

/**
 * The `GenAILangChaineService` class provides a service that handles language
 * generation and chain processing. This service is
 * used to process requests related to language completions, single chains, and
 * multiple chains, and it interacts with various
 * components to fulfill these requests.
 *
 * The methods within this service invoke machine learning models, HTTP
 * requests, and database operations to generate and process
 * language. The service is highly configurable and is designed to facilitate
 * various language generation and chain processing
 * scenarios.
 */
@Slf4j
public class GenAILangChainService {

    enum ChainType {
        llm, httpRequest, oracleDb
    };

    /**
     * Processes a language completion request based on the provided
     * `RequestPayload`. It invokes the language model and returns
     * the generated language completion.
     *
     * @param payload The request payload containing language generation details.
     * @return The generated language completion.
     * @throws IOException
     */
    public String getCompletion(RequestPayload payload) throws IOException {
        log.info("Invoke completion API...");
        String output = null;
        ModelParameters llmParameters = payload.getModelParameters();
        var llm = getLLM(llmParameters);
        output = llm.predict(payload.getPrompt());
        return output;
    }

    /**
     * Processes a single chain of requests based on the specified chain type. It
     * delegates the request to the appropriate chain
     * processing method based on the `chainType`.
     *
     * @param chainType The type of the chain to process.
     * @param payload   The request payload containing chain processing details.
     * @return The result of processing the single chain.
     * @throws IOException
     */
    public String getChain(String chainType, RequestChainPayload payload) throws IOException {

        log.info("Invoke single chain service...");
        String chainResult = null;
        switch (chainType.toLowerCase()) {
            case "llm":
                chainResult = invokeLLMChain(payload);
                break;
            case "httprequest":
                chainResult = invokeHTTPRequestChain(payload);
                break;
            case "oracledb":
                chainResult = invokeOracelDatabaseChain(payload);
                break;
        }
        return chainResult;
    }

    /**
     * Initializes and configures a Language Model (LLM) with the provided
     * parameters.
     *
     * @param chainLLMParameters The model parameters for the Language Model.
     * @return The configured LLM.
     * @throws IOException
     */
    public GenAICohereGenerationModel getLLM(ModelParameters chainLLMParameters) throws IOException {
        Region region = Region.fromRegionId(chainLLMParameters.getRegion());
        var llm = GenAICohereGenerationModel.builder()
                .compartmentId(chainLLMParameters.getCompartmentId())
                .configProfile(chainLLMParameters.getConfigProfile())
                .endpoint(chainLLMParameters.getEndpoint())
                .configLocation(chainLLMParameters.getConfigLocation())
                .region(region)
                .modeId(chainLLMParameters.getModelId())
                .temperature(chainLLMParameters.getTemperature())
                .maxTokens(chainLLMParameters.getMaxTokens())
                .frequencyPenalty(chainLLMParameters.getFrequencyPenalty())
                .presencePenalty(chainLLMParameters.getPresencePenalty())
                .batchSize(chainLLMParameters.getBatchSize())
                .isEcho(chainLLMParameters.getIsEcho())
                .isStream(chainLLMParameters.getIsStream())
                .stopSequences(chainLLMParameters.getStopSequences())
                .topK(chainLLMParameters.getTopK())
                .topP(chainLLMParameters.getTopP())
                .build()
                .init();
        return llm;
    }

    /**
     * Processes multiple chains of requests based on the provided
     * `RequestMultipleChainsPayload`. It iterates through the list
     * of chains, invoking each chain's processing and using the results to modify
     * the prompt for the next chain. The final
     * modified prompt is used to generate language.
     *
     * @param payload The request payload containing details of multiple chains to
     *                invoke.
     * @return The result of processing multiple chains.
     * @throws IOException
     */
    public String getChains(RequestMultipleChainsPayload payload) throws IOException {
        String chainResult = null;
        ModelParameters llmParameters = payload.getModelParameters();
        String prompt = payload.getPrompt();
        List<RequestChainPayload> chains = payload.getChains();
        for (RequestChainPayload chain : chains) {
            String chainType = chain.getChainType();
            if (!contains(ChainType.class, chainType)) {
                throw new NotSupportedException();
            }
            switch (chainType.toLowerCase()) {
                case "llm":
                    chainResult = invokeLLMChain(chain);
                    break;
                case "httprequest":
                    chainResult = invokeHTTPRequestChain(chain);
                    break;
                case "oracledb":
                    chainResult = invokeOracelDatabaseChain(chain);
                    break;
            }
            prompt = replaceTokenWithData(chainResult, chain.getOutputVariable(), prompt);
        }
        RequestPayload reqPayload = new RequestPayload();
        reqPayload.setModelParameters(llmParameters);
        reqPayload.setPrompt(prompt);
        return invokeLLM(reqPayload);
    }

    /**
     * Invokes the Language Model (LLM) based on the provided `RequestPayload`. It
     * configures the LLM with the specified parameters
     * and generates language based on the given prompt.
     *
     * @param payload The request payload containing language generation details.
     * @return The generated language.
     * @throws IOException
     */
    private String invokeLLM(RequestPayload payload) throws IOException {
        log.info("Invoke LLM ...");
        ModelParameters llmParameters = payload.getModelParameters();
        var llm = getLLM(llmParameters);
        var prompt = PromptTemplate.fromTemplate(payload.getPrompt());
        var chain = new LLMChain(llm, prompt);
        Map<String, Object> prompt_properties = new HashMap<String, Object>();
        if (payload.getProperties() != null)
            payload.getProperties().forEach(property -> {
                prompt_properties.put(property.getKey(), property.getValue());
            });
        return chain.run(prompt_properties);
    }

    /**
     * Invokes a single chain of requests with the `LLM` type based on the provided
     * `RequestChainPayload`. It configures the LLM
     * with the specified parameters and generates language based on the given
     * prompt.
     *
     * @param chain The request payload containing chain processing details.
     * @return The result of processing the single chain.
     * @throws IOException
     */
    private String invokeLLMChain(RequestChainPayload chain) throws IOException {
        log.info("Invoke LLM chain ...");
        ModelParameters chainLLMParameters = chain.getModelParameters();
        var llm = getLLM(chainLLMParameters);
        var prompt = PromptTemplate.fromTemplate(chain.getPrompt());
        var llmChain = new LLMChain(llm, prompt);
        Map<String, Object> prompt_properties = new HashMap<String, Object>();
        if (chain.getProperties() != null)
            chain.getProperties().forEach(property -> {
                prompt_properties.put(property.getKey(), property.getValue());
            });
        return llmChain.run(prompt_properties);
    }

    /**
     * Invokes a single chain of requests with the `httpRequest` type based on the
     * provided `RequestChainPayload`. It processes
     * an HTTP request chain by sending an HTTP request to the specified API URL and
     * returning the response.
     *
     * @param chain The request payload containing chain processing details.
     * @return The response from the HTTP request chain.
     */
    private String invokeHTTPRequestChain(RequestChainPayload chain) {
        log.info("Invoke HTTP Request chain ...");
        String response = null;
        ModelParameters chainLLMParameters = chain.getModelParameters();
        try {
            var llm = getLLM(chainLLMParameters);
            String authToken = chain.getHttpRequest().getAuthorizationToken();
            String username = chain.getHttpRequest().getUsername();
            String pwd = chain.getHttpRequest().getPwd();
            if (StringUtils.isBlank(authToken) && !StringUtils.isBlank(username) && StringUtils.isBlank(pwd)) {
                String valueToEncode = username + ":" + pwd;
                authToken = "Basic " + Base64.getEncoder().encodeToString(valueToEncode.getBytes());
            }
            String prompt = mergePromptwithToken(chain.getProperties(), chain.getPrompt());
            Map<String, String> headers = new HashMap<String, String>();
            headers.put("Authorization", authToken);
            headers.put("Content-Type", chain.getHttpRequest().getContentType());

            var httpChain = HttpRequestChain.usingApiURL(llm, chain.getHttpRequest().getApiURL(), headers,
                    HTTPREQUEST_RESPONSE_PROMPT);
            response = httpChain.run(prompt);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    /**
     * Invokes a single chain of requests with the `oracledb` type based on the
     * provided `RequestChainPayload`. It processes
     * an Oracle database chain by running SQL commands and returning the result.
     *
     * @param chain The request payload containing chain processing details.
     * @return The result of processing the Oracle database chain.
     * @throws IOException
     */
    private String invokeOracelDatabaseChain(RequestChainPayload chain) throws IOException {
        log.info("Invoke Oracle database chain ...");
        ModelParameters chainLLMParameters = chain.getModelParameters();
        var llm = getLLM(chainLLMParameters);
        var database = OracleDatabase.fromUri(chain.getDbRequest().getDbConnection(),
                chain.getDbRequest().getDbUserName(), chain.getDbRequest().getDbPwd());
        Map<String, Object> sqlProperties = new HashMap<String, Object>();
        for (Property property : chain.getProperties()) {
            sqlProperties.put(property.getKey(), property.getValue());
        }
        var dbChain = OracleDatabaseChain.fromSqlCmd(llm, database, chain.getDbRequest().getSqlCmd(),
                sqlProperties);
        return dbChain.run(chain.getPrompt());
    }
}
