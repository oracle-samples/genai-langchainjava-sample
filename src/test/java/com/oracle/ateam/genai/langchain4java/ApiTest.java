/*******************************************************************************
 * Oracle OCI Generative AI LangChain For Java version 1.0.
 *
 * Copyright (c)  2024,  Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 ******************************************************************************/

package com.oracle.ateam.genai.langchain4java;

import jakarta.inject.Inject;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;

import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;

import io.helidon.microprofile.testing.junit5.HelidonTest;
import io.helidon.metrics.api.MetricsFactory;

import org.junit.jupiter.api.Test;

import com.oracle.ateam.genai.langchain4java.model.DefaultResponse;
import com.oracle.ateam.genai.langchain4java.model.RequestChainPayload;
import com.oracle.ateam.genai.langchain4java.model.RequestPayload;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Disabled;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.io.IOException;
import java.io.File;

/**
 * Unit test for Helidon service.
 */
@Slf4j
@HelidonTest
class ApiTest {
    @Inject
    private WebTarget target;

    @AfterAll
    static void clear() {
        MetricsFactory.closeAll();
    }

    @Test
    void testHealth() {
        Response response = target
                .path("health")
                .request()
                .get();
        assertThat(response.getStatus(), is(200));
    }

    @Test
    void testDefault() {
        DefaultResponse message = target
                .path("llm/rest/v1")
                .request()
                .get(DefaultResponse.class);
        System.out.println(message.getText());
        assertThat(message.getText(), is("Version: 1.0!"));
    }

    @Test
    void testCompletion() throws StreamReadException, DatabindException, IOException {
        var payload = asJsonString(getRequestPayload("llm.json"));
        try (Response r = target
                .path("llm/rest/v1/completion")
                .request()
                .post(Entity.entity(payload, MediaType.APPLICATION_JSON))) {
            log.info("testComplete Response Output: " + r.readEntity(String.class));
            assertThat(r.getStatus(), is(200));
        }
    }

    @Disabled("Require External API.")
    @Test
    public void testHTTP_Request_Chain() throws Exception {
        var payload =asJsonString(getRequestChainPayload("http_request_chain.json"));
        try (Response r = target
                .path("llm/rest/v1/chain/httpRequest")
                .request()
                .post(Entity.entity(payload, MediaType.APPLICATION_JSON))) {
            log.info("testHTTP_Request_Chain Response Output: " + r.readEntity(String.class));
            assertThat(r.getStatus(), is(200));
        }
    }

    private RequestPayload getRequestPayload(String jsonFile)
            throws StreamReadException, DatabindException, IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource(jsonFile).getFile());
        RequestPayload requestPayload = objectMapper.readValue(file, RequestPayload.class);
        return requestPayload;
    }

    private RequestChainPayload getRequestChainPayload(String jsonFile)
            throws StreamReadException, DatabindException, IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource(jsonFile).getFile());
        RequestChainPayload requestPayload = objectMapper.readValue(file, RequestChainPayload.class);
        return requestPayload;
    }

    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
