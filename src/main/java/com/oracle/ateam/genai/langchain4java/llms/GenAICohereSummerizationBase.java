/*******************************************************************************
 * Oracle OCI Generative AI LangChain For Java version 1.0.
 *
 * Copyright (c)  2024,  Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 ******************************************************************************/
package com.oracle.ateam.genai.langchain4java.llms;

import lombok.Builder;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import com.oracle.bmc.generativeaiinference.responses.SummarizeTextResponse;

/**
 * The `GenAISummerizationBase` class serves as the base class for configuring
 * and utilizing the Generative AI service's
 * summarization capabilities. It provides a set of parameters and methods that
 * can be customized to interact with the
 * Generative AI service for text summarization.
 *
 * This base class encapsulates common configuration options and methods for
 * text summarization using the Generative AI
 * service, making it a foundational component for building more specific
 * summarization models or applications.
 *
 * The `GenAISummerizationBase` class includes several configuration options and
 * methods, such as `summerize(text)` for
 * text summarization, and `llmType()` to specify the Generative AI model type.
 *
 * Note: It is recommended to extend this base class to build custom
 * summarization models that leverage the Generative AI
 * service for text summarization.
 *
 */
@Slf4j
@SuperBuilder
public class GenAICohereSummerizationBase {

    protected GenerativeAiClient generativeAiClient;

    protected String modeId;

    @Builder.Default
    protected Integer maxTokens = 300;

    @Builder.Default
    protected Integer batchSize = 20;

    @Builder.Default
    protected Integer numGenerations = 1;

    @Builder.Default
    protected Double temperature = 1.0;

    @Builder.Default
    protected Boolean isStream = false;

    @Builder.Default
    protected Boolean isEcho = false;

    protected Double topP;

    protected Integer topK;

    protected Double presencePenalty;

    protected Double frequencyPenalty;

    protected List<String> stopSequences;

    protected String compartmentId;

    /**
     * Summarizes the given text using the Generative AI service.
     *
     * @param text The input text to be summarized.
     * @return The summarized text.
     * @throws IOException
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public String summerize(String text) throws InterruptedException, ExecutionException, IOException {
        log.info("Summerize text start...");
       
        SummarizeTextResponse summarizeTextResponse = generativeAiClient.summarizeText(text);
        return summarizeTextResponse.getSummarizeTextResult().getSummary();
    }

    /**
     * Returns the type of the Generative AI model used for text summarization.
     *
     * @return The type of the Generative AI model (e.g., "genai_cohere").
     */
    public String llmType() {
        return "genai_cohere";
    }

}
