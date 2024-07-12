/*******************************************************************************
 * Oracle OCI Generative AI LangChain For Java version 1.0.
 *
 * Copyright (c)  2024,  Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 ******************************************************************************/
package com.oracle.ateam.genai.langchain4java.chain.http.requests;

import com.google.gson.Gson;

import okhttp3.*;

import java.io.IOException;
import java.util.Map;

/**
 * The `Requests` class is a wrapper around the HTTP requests library, designed
 * to handle
 * authentication and enable asynchronous methods. This class is primarily
 * responsible for
 * constructing HTTP requests with the specified headers and making synchronous
 * HTTP requests
 * to a remote server.
 * 
 * Example usage:
 * ```java
 * Map<String, String> headers = new HashMap<>();
 * Requests requests = new Requests(headers);
 * Response response = requests.get("https://example.com/api/resource");
 * ```
 * 
 * Features:
 * - Manages HTTP request headers for authentication.
 * - Provides methods for common HTTP request types, including GET, POST, PATCH,
 * PUT, and DELETE.
 * - Supports sending data in the request body as JSON.
 * 
 */
public class Requests {

    private final Map<String, String> headers;
    private final OkHttpClient client;

    /**
     * Constructs a new `Requests` instance with the provided headers.
     *
     * @param headers A map of HTTP headers to include in the requests.
     */
    public Requests(Map<String, String> headers) {
        this.headers = headers;
        this.client = new OkHttpClient();

    }

    /**
     * Build an OkHttp request with the given URL, request body, and HTTP method.
     *
     * @param url    The URL of the HTTP request.
     * @param body   The request body, can be null for methods like GET or DELETE.
     * @param method The HTTP method (e.g., "GET", "POST", "PUT").
     * @return A constructed OkHttp `Request` object.
     */
    private Request buildRequest(String url, RequestBody body, String method) {
        Request.Builder builder = new Request.Builder()
                .url(url);

        if (headers != null) {
            builder.headers(Headers.of(headers));
        }

        builder.method(method, body);
        return builder.build();
    }

    /**
     * Execute an OkHttp request and return the response.
     *
     * @param request The OkHttp `Request` to be executed.
     * @return The OkHttp `Response` from executing the request.
     * @throws IOException If an error occurs during the request execution.
     */
    private Response executeRequest(Request request) throws IOException {
        return client.newCall(request).execute();
    }

    /**
     * Send an HTTP request with the specified URL, HTTP method, and data.
     *
     * @param url    The URL of the HTTP request.
     * @param method The HTTP method (e.g., "GET", "POST", "PUT").
     * @param data   A map of data to be sent in the request body, can be null for
     *               methods like GET or DELETE.
     * @return The OkHttp `Response` from executing the request.
     * @throws IOException If an error occurs during the request execution.
     */
    public Response sendRequest(String url, String method, Map<String, Object> data) throws IOException {
        RequestBody body = null;

        if (data != null) {
            MediaType mediaType = MediaType.parse("application/json");
            String jsonBody = new Gson().toJson(data);
            // body = RequestBody.create(mediaType, jsonBody);
            body = RequestBody.create(jsonBody, mediaType);
        }

        Request request = buildRequest(url, body, method);
        return executeRequest(request);
    }

    /**
     * Send a GET request to the specified URL.
     *
     * @param url The URL of the GET request.
     * @return The OkHttp `Response` from executing the GET request.
     * @throws IOException If an error occurs during the request execution.
     */
    public Response get(String url) throws IOException {
        return sendRequest(url, "GET", null);
    }

    /**
     * Send a POST request to the specified URL with the provided data.
     *
     * @param url  The URL of the POST request.
     * @param data A map of data to be sent in the request body.
     * @return The OkHttp `Response` from executing the POST request.
     * @throws IOException If an error occurs during the request execution.
     */
    public Response post(String url, Map<String, Object> data) throws IOException {
        return sendRequest(url, "POST", data);
    }

    /**
     * Send a PATCH request to the specified URL with the provided data.
     *
     * @param url  The URL of the PATCH request.
     * @param data A map of data to be sent in the request body.
     * @return The OkHttp `Response` from executing the PATCH request.
     * @throws IOException If an error occurs during the request execution.
     */
    public Response patch(String url, Map<String, Object> data) throws IOException {
        return sendRequest(url, "PATCH", data);
    }

    /**
     * Send a PUT request to the specified URL with the provided data.
     *
     * @param url  The URL of the PUT request.
     * @param data A map of data to be sent in the request body.
     * @return The OkHttp `Response` from executing the PUT request.
     * @throws IOException If an error occurs during the request execution.
     */
    public Response put(String url, Map<String, Object> data) throws IOException {
        return sendRequest(url, "PUT", data);
    }

    /**
     * Send a DELETE request to the specified URL.
     *
     * @param url The URL of the DELETE request.
     * @return The OkHttp `Response` from executing the DELETE request.
     * @throws IOException If an error occurs during the request execution.
     */
    public Response delete(String url) throws IOException {
        return sendRequest(url, "DELETE", null);
    }
}
