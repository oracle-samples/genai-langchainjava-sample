/*******************************************************************************
 * Oracle OCI Generative AI LangChain For Java version 1.0.
 *
 * Copyright (c)  2024,  Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 ******************************************************************************/
package com.oracle.ateam.genai.langchain4java.llms;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import com.oracle.bmc.generativeaiinference.responses.EmbedTextResponse;

import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

/**
 * The `GenAIEmbedBase` class serves as the base class for embedding text using
 * the Generative AI service. It provides the foundational
 * methods and configuration for interacting with the Generative AI Embedding
 * service, allowing you to convert text data into embeddings.
 *
 * This base class simplifies the setup and usage of the Generative AI Embedding
 * service, and it can be extended to create custom
 * text embedding models with specific requirements. It includes methods for
 * embedding text data based on the provided input, making
 * it suitable for various natural language processing tasks where text
 * embeddings are needed.
 *
 * The `GenAIEmbedBase` class relies on the Generative AI Embedding service to
 * generate text embeddings, and it supports a specific
 * model ID, compartment ID, and region for text embedding.
 *
 */
@Slf4j
@SuperBuilder
public class GenAICohereEmbedBase {

    // Fields for configuration and interaction with the Generative AI Embedding
    // service.
    protected String modeId;
    protected GenerativeAiClient generativeAiClient;
    protected String compartmentId;

    /**
     * Embeds the provided list of text into embeddings using the Generative AI
     * Embedding service.
     *
     * @param text The list of text strings to be embedded.
     * @return The result of text embedding as an EmbedTextResult.
     * @throws IOException
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public EmbedTextResponse embed(List<String> text) throws InterruptedException, ExecutionException, IOException {
        log.info("Embed text start...");
        EmbedTextResponse embedTextResponse = generativeAiClient.embedText(text);

        return embedTextResponse;
    }
}
