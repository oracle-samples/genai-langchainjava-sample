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
 * The `RequestPayload` class represents a data structure used for sending
 * requests to a service or API. It encapsulates
 * various components of a request, including a prompt, model parameters, and a
 * list of properties.
 *
 * This class is typically used to package and send data to a service or API,
 * where the `prompt` represents the main
 * input or query, the `modelParameters` specify optional parameters for a
 * machine learning model, and the `properties`
 * provide additional key-value pairs that may be used in request customization
 * or data processing.
 *
 * The class is often used in scenarios where a request payload needs to be
 * structured and sent to a service for processing.
 */
@Data
public class RequestPayload {
    /**
     * The prompt string that forms the main input or query for the request.
     */
    private String prompt;
    /**
     * Model-specific parameters that can be used to customize the behavior of
     * machine learning models.
     */
    private ModelParameters modelParameters;
    /**
     * A list of properties represented as key-value pairs, providing additional
     * data or customization options for the request.
     */
    private List<Property> properties;
}