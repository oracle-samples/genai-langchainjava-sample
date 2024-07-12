/*******************************************************************************
 * Oracle OCI Generative AI LangChain For Java version 1.0.
 *
 * Copyright (c)  2024,  Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 ******************************************************************************/
package com.oracle.ateam.genai.langchain4java.model;

import lombok.Data;

/**
 * The `HttpRequestPayload` class represents a data structure used for
 * specifying details of an HTTP request. It encapsulates
 * various components related to making HTTP requests, including authorization
 * token, username, password, API URL, and content type.
 *
 * This class is typically used to define the parameters and attributes
 * necessary for sending HTTP requests in Java applications.
 * The `authorizationToken` field is used to provide an authentication token for
 * the request, the `username` and `pwd` fields
 * store credentials for basic authentication, the `apiURL` specifies the target
 * URL for the request, and the `contentType` indicates
 * the media type of the request content.
 *
 * The `HttpRequestPayload` class is designed to facilitate the structuring of
 * HTTP requests, making it suitable for use in scenarios
 * where communication with external APIs or services is a part of the
 * application's functionality.
 */
@Data
public class HttpRequestPayload {
    /**
     * The authorization token for the HTTP request.
     */
    private String authorizationToken;

    /**
     * The username for basic authentication (if required).
     */
    private String username;

    /**
     * The password for basic authentication (if required).
     */
    private String pwd;

    /**
     * The URL of the API endpoint to which the HTTP request will be sent.
     */
    private String apiURL;

    /**
     * The media type or content type of the request content.
     */
    private String contentType;
}
