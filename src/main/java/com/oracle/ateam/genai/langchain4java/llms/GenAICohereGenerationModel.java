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
 * The `GenAIGenerationModel` class extends the `GenAIGenerationBase` class and
 * represents a model for initializing a
 * GenAI Cohere Command Language Model to perform text generation. This class
 * provides methods for configuring and
 * creating the model instance, enabling interactions with the Generative AI
 * service using the Cohere Command Language Model.
 *
 * This class simplifies the setup and configuration of the Generative AI
 * service for text generation, and it supports
 * customization through environment variables, allowing you to specify
 * parameters like configuration location, configuration
 * profile, compartment ID, and region for connecting to the Generative AI
 * service.
 *
 * The class relies on the Cohere Command Language Model to generate text based
 * on the provided input, making it a convenient
 * choice for implementing various text generation tasks.
 *
 * Note: The class uses environment variables for configuration, which can be
 * set or overridden to customize the behavior of
 * the GenAI Cohere Command Language Model.
 *
 */

@Slf4j
@SuperBuilder
public class GenAICohereGenerationModel extends GenAICohereGenerationBase {
         private String configProfile;
         private String compartmentId;
         private Region region;
         private String endpoint;
         private String configLocation;

        /**
         * Initializes the GenAIGenerationModel by configuring its parameters based on
         * environment variables. It sets up the
         * necessary client and authentication providers for interacting with the
         * Generative AI service.
         *
         * @return The initialized GenAIGenerationModel instance, allowing you to
         *         perform text generation using the Cohere Command Language Model.
         * @throws IOException 
         */
        public GenAICohereGenerationModel init() throws IOException {
                log.info("Creating GenAI Cohere Command Langauge Model");

                this.generativeAiClient = GenerativeAiClient.builder()
                                .compartmentId(compartmentId)
                                .configProfile(configProfile)
                                .endpoint(endpoint)
                                .configLocation(configLocation)
                                .region(region)
                                .modelId(this.modeId)
                                .build()
                                .init();
                return this;
        }

}
