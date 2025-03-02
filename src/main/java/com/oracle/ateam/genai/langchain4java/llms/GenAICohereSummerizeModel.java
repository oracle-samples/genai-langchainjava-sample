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
 * The `GenAISummerizeModel` class extends the `GenAISummerizationBase` class
 * and represents a model for initializing
 * a GenAI Cohere Command Language Model. It provides methods for configuring
 * and creating the model instance, allowing
 * you to interact with the Generative AI service using the Cohere Command
 * Language Model.
 *
 * This class supports configuration through environment variables, allowing you
 * to specify parameters like configuration
 * location, configuration profile, compartment ID, and region for connecting to
 * the Generative AI service.
 *
 * Example usage:
 * ```java
 * GenAISummerizeModel summerizeModel = new GenAISummerizeModel();
 * try {
 * summerizeModel.init();
 * // Now you can use the initialized GenAISummerizeModel to interact with the
 * Generative AI service.
 * } catch (IOException e) {
 * // Handle initialization errors here.
 * }
 * ```
 *
 * The class uses the Cohere Command Language Model to generate summaries based
 * on the provided input.
 *
 * Note: The class relies on environment variables for configuration, which can
 * be set or overridden to tailor the
 * behavior of the GenAI Cohere Command Language Model.
 *
 */
@Slf4j
@SuperBuilder
public class GenAICohereSummerizeModel extends GenAICohereSummerizationBase {
        private String configProfile;
        private String compartmentId;
        private Region region;
        private String endpoint;
        private String configLocation;

        /**
         * Initializes the GenAISummerizeModel by configuring its parameters based on
         * environment variables. It sets up the
         * necessary client and authentication providers for interacting with the
         * Generative AI service.
         *
         * @return The initialized GenAISummerizeModel instance, allowing you to
         *         interact with the Generative AI service.
         * @throws IOException If there is an issue with reading the configuration file.
         */
        public GenAICohereSummerizeModel init() throws IOException {
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