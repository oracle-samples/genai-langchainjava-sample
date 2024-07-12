/*******************************************************************************
 * Oracle OCI Generative AI LangChain For Java version 1.0.
 *
 * Copyright (c)  2024,  Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 ******************************************************************************/
package com.oracle.ateam.genai.langchain4java.chain.http.requests;

import com.hw.langchain.exception.LangChainException;

import okhttp3.Response;
import okhttp3.ResponseBody;

import java.io.IOException;
import java.util.Map;

/**
 * The `TextRequestsWrapper` class is a lightweight wrapper around the HTTP
 * requests library.
 * Its primary purpose is to provide a simplified way to perform HTTP requests
 * and always return
 * the response body as a string. This is particularly useful for working with
 * text-based responses
 * from web services.
 * 
 * The class offers methods for making common HTTP requests such as GET, POST,
 * PATCH, PUT, and DELETE.
 * The response body is automatically converted to a string, or `null` is
 * returned if the response
 * body is empty.
 * 
 * Example usage:
 * ```java
 * Map<String, String> headers = new HashMap<>();
 * TextRequestsWrapper requestsWrapper = new TextRequestsWrapper(headers);
 * String response = requestsWrapper.get("https://example.com/api/resource");
 * ```
 * 
 */
public class TextRequestsWrapper {

    private final Map<String, String> headers;

    public TextRequestsWrapper(Map<String, String> headers) {
        this.headers = headers;
    }

    /**
     * Performs an HTTP request using the provided `Requests` instance.
     *
     * @param requests The `Requests` instance to use for sending the request
     * @param url      The URL to send the request to
     * @param method   The HTTP method to use (e.g., "GET", "POST")
     * @param data     The data to send in the request body (can be null)
     * @return The response body as a string, or null if the response body is empty
     * @throws LangChainException If an error occurs while performing the request
     */
    private String performRequest(Requests requests, String url, String method, Map<String, Object> data) {
        try (Response response = requests.sendRequest(url, method, data)) {
            if (response.isSuccessful()) {
                ResponseBody responseBody = response.body();
                return responseBody != null ? responseBody.string() : null;
            } else {
                throw new LangChainException("Failed with status code %d. messages: %s");
            }
        } catch (IOException e) {
            throw new LangChainException("An error occurred while performing " + method + " request.", e);
        }
    }

    /**
     * Sends a GET request to the specified URL and returns the response body as a
     * string.
     *
     * @param url The URL of the GET request.
     * @return The response body as a string, or null if the response body is empty.
     */
    public String get(String url) {
        Requests requests = getRequests();
        return performRequest(requests, url, "GET", null);
    }

    /**
     * Sends a POST request to the specified URL with the provided data and returns
     * the response body as a string.
     *
     * @param url  The URL of the POST request.
     * @param data A map of data to be sent in the request body.
     * @return The response body as a string, or null if the response body is empty.
     */
    public String post(String url, Map<String, Object> data) {
        Requests requests = getRequests();
        return performRequest(requests, url, "POST", data);
    }

    /**
     * Sends a PATCH request to the specified URL with the provided data and returns
     * the response body as a string.
     *
     * @param url  The URL of the PATCH request.
     * @param data A map of data to be sent in the request body.
     * @return The response body as a string, or null if the response body is empty.
     */
    public String patch(String url, Map<String, Object> data) {
        Requests requests = getRequests();
        return performRequest(requests, url, "PATCH", data);
    }

    /**
     * Sends a PUT request to the specified URL with the provided data and returns
     * the response body as a string.
     *
     * @param url  The URL of the PUT request.
     * @param data A map of data to be sent in the request body.
     * @return The response body as a string, or null if the response body is empty.
     */
    public String put(String url, Map<String, Object> data) {
        Requests requests = getRequests();
        return performRequest(requests, url, "PUT", data);
    }

    /**
     * Sends a DELETE request to the specified URL and returns the response body as
     * a string.
     *
     * @param url The URL of the DELETE request.
     * @return The response body as a string, or null if the response body is empty.
     */
    public String delete(String url) {
        Requests requests = getRequests();
        return performRequest(requests, url, "DELETE", null);
    }

    /**
     * Creates a new `Requests` instance with the headers specified during the
     * `TextRequestsWrapper` construction.
     *
     * @return A new `Requests` instance with the provided headers.
     */
    private Requests getRequests() {
        return new Requests(headers);
    }
}
