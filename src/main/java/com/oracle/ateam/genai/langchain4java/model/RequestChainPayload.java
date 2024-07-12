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
 * The `RequestChainPayload` class represents a data structure used for defining
 * a single chain of requests to be processed
 * by a service or API. It encapsulates various components of a request chain,
 * including the chain type, a prompt, model
 * parameters, HTTP request details, Oracle database request details, a list of
 * properties, and an output variable.
 *
 * This class is typically used to define and send a specific chain of requests
 * to a service, where the `chainType` specifies
 * the type or category of the chain, the `prompt` represents the main input or
 * query for the chain, the `modelParameters`
 * specify optional parameters for customizing machine learning models, and the
 * `httpRequest` and `dbRequest` fields contain
 * details about HTTP and database requests, respectively.
 *
 * The `properties` field provides additional key-value pairs that can be used
 * for customizing the chain's requests, and the
 * `outputVariable` represents a variable that may store the output of the chain
 * for later reference.
 *
 * This class is often used in scenarios where a specific chain of requests,
 * which may include a combination of HTTP and
 * database requests, needs to be structured and sent to a service or API for
 * processing.
 */
@Data
public class RequestChainPayload {
    /**
     * The type or category of the request chain.
     */
    private String chainType;

    /**
     * The prompt string that forms the main input or query for the request chain.
     */
    private String prompt;

    /**
     * Model-specific parameters that can be used to customize the behavior of
     * machine learning models.
     */
    private ModelParameters modelParameters;

    /**
     * Details of an HTTP request to be included in the chain.
     */
    private HttpRequestPayload httpRequest;

    /**
     * Details of an Oracle database request to be included in the chain.
     */
    private OracleDbRequestPayload dbRequest;

    /**
     * A list of properties represented as key-value pairs, providing additional
     * data or customization options for the request chain.
     */
    private List<Property> properties;

    /**
     * The variable that may store the output of the chain for later reference.
     */
    private String outputVariable;
}
