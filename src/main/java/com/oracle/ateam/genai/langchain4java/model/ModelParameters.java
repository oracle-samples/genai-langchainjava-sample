/*******************************************************************************
 * Oracle OCI Generative AI LangChain For Java version 1.0.
 *
 * Copyright (c)  2024,  Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 ******************************************************************************/
package com.oracle.ateam.genai.langchain4java.model;

import java.util.List;

import lombok.Data;

/**
 * The `ModelParameters` class represents a data structure used for specifying
 * various parameters when interacting with a
 * machine learning model or language model. It encapsulates a wide range of
 * parameters, allowing fine-tuning and customization
 * of model behavior.
 *
 * This class is typically used to configure and control the behavior of machine
 * learning models in Java applications. The fields
 * within the `ModelParameters` class cover a variety of settings, such as the
 * model identifier (`modelId`), temperature, presence
 * penalty, frequency penalty, maximum tokens, batch size, echo mode, stream
 * mode, likelihood return preferences, stop sequences,
 * top-K tokens, and top-P probability.
 *
 * The `ModelParameters` class provides flexibility for adjusting and
 * fine-tuning model responses according to specific application
 * requirements, making it suitable for use in scenarios where customizing model
 * behavior is necessary.
 */
@Data
public class ModelParameters {
    /**
     * The identifier of the machine learning model to be used.
     */
    private String modelId;

    /**
     * The OCI config file profile name
     */
    private String configProfile;

    /**
     * The OCI Generative AI compartment Id
     */
    private String compartmentId;

    /**
     * The OCI Generative AI region
     */
    private String region;

    /**
     * The OCI Generative AI API endpoint
     */
    private String endpoint;

    /**
     * The OCI config file location
     */
    private String configLocation;

    /**
     * The temperature parameter that controls the randomness of the model's output.
     */
    private Double temperature;

    /**
     * The presence penalty parameter that adjusts the likelihood of adding new
     * tokens.
     */
    private Double presencePenalty;

    /**
     * The frequency penalty parameter that adjusts the likelihood of using frequent
     * tokens.
     */
    private Double frequencyPenalty;

    /**
     * The maximum number of tokens to generate in a response.
     */
    private Integer maxTokens;

    /**
     * The batch size for processing multiple requests in parallel.
     */
    private Integer batchSize;

    /**
     * A flag indicating whether the output should be echoed as part of the
     * response.
     */
    private Boolean isEcho;

    /**
     * A flag indicating whether the output should be streamed.
     */
    private Boolean isStream;

    /**
     * A list of stop sequences that can be used to indicate the end of text
     * generation.
     */
    private List<String> stopSequences;

    /**
     * The number of top-K tokens to consider during generation.
     */
    private Integer topK;

    /**
     * The top-P probability value for token selection during generation.
     */
    private Double topP;

}
