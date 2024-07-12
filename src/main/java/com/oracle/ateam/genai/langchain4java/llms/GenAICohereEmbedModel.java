/*******************************************************************************
 * Oracle OCI Generative AI LangChain For Java version 1.0.
 *
 * Copyright (c)  2024,  Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 ******************************************************************************/
package com.oracle.ateam.genai.langchain4java.llms;

import java.io.IOException;

import com.oracle.bmc.Region;

import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

/**
 * The `GenAIEmbedModel` class extends the `GenAIEmbedBase` class and represents
 * a model for initializing the GenAI Embedding
 * service. This class provides methods for configuring and creating the model
 * instance, enabling interactions with the Generative AI
 * service for text embedding purposes.
 *
 * This class simplifies the setup and configuration of the Generative AI
 * Embedding service, and it supports customization through
 * environment variables, allowing you to specify parameters like configuration
 * location, configuration profile, compartment ID,
 * and region for connecting to the Generative AI service.
 *
 * The class relies on the Generative AI Embedding service to convert text data
 * into embeddings, making it suitable for various
 * natural language processing tasks where text similarity or embedding
 * representations are required.
 *
 * Note: The class uses environment variables for configuration, which can be
 * set or overridden to customize the behavior of the
 * GenAI Embedding service.
 *
 */
@Slf4j
@SuperBuilder
public class GenAICohereEmbedModel extends GenAICohereEmbedBase {
        private String configProfile;
        private String compartmentId;
        private Region region;
        private String endpoint;
        private String configLocation;

        /**
         * Initializes the GenAIEmbedModel by configuring its parameters based on
         * environment variables. It sets up the necessary client
         * and authentication providers for interacting with the Generative AI Embedding
         * service.
         *
         * @return The initialized GenAIEmbedModel instance, allowing you to perform
         *         text embedding using the Generative AI Embedding service.
         * @throws IOException
         */
        public GenAICohereEmbedModel init() throws IOException {
                log.info("Creating GenAI Cohere Embdeding...");

                this.generativeAiClient = GenerativeAiClient.builder()
                                .compartmentId(compartmentId)
                                .configProfile(configProfile)
                                .endpoint(endpoint)
                                .configLocation(configLocation)
                                .region(region)
                                .modelId(this.modeId)
                                .build()
                                .init();
                this.compartmentId = generativeAiClient.getCompartmentId();
                return this;
        }
}
