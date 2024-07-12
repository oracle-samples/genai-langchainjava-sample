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
 * The `RequestMultipleChainsPayload` class represents a data structure used for
 * sending multiple chains of requests to a
 * service or API. It encapsulates various components of a request, including a
 * prompt, model parameters, a list of request
 * chain payloads, and a list of properties.
 *
 * This class is typically used to package and send multiple chains of requests
 * to a service, where each chain may consist
 * of multiple requests, and each request can have its own input parameters,
 * model parameters, and custom properties.
 *
 * The `prompt` represents the main input or query for the entire request
 * payload, and the `modelParameters` specify optional
 * parameters that can be used to customize the behavior of machine learning
 * models. The `chains` field contains a list of
 * `RequestChainPayload` instances, each representing a chain of requests, and
 * the `properties` provide additional key-value
 * pairs that may be used for request customization or data processing.
 *
 * This class is often used in scenarios where multiple chains of requests need
 * to be structured and sent to a service for
 * complex or batch processing.
 */
@Data
public class RequestMultipleChainsPayload {
    /**
     * The prompt string that forms the main input or query for the entire request
     * payload.
     */
    private String prompt;

    /**
     * Model-specific parameters that can be used to customize the behavior of
     * machine learning models.
     */
    private ModelParameters modelParameters;

    /**
     * A list of `RequestChainPayload` instances, each representing a chain of
     * requests to be processed.
     */
    private List<RequestChainPayload> chains;

    /**
     * A list of properties represented as key-value pairs, providing additional
     * data or customization options for the request.
     */
    private List<Property> properties;
}