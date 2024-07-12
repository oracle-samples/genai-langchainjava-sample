
/*******************************************************************************
 * Oracle OCI Generative AI LangChain For Java version 1.0.
 *
 * Copyright (c)  2024,  Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 ******************************************************************************/
package com.oracle.ateam.genai.langchain4java;

import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Timed;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import com.oracle.ateam.genai.langchain4java.model.DefaultResponse;
import com.oracle.ateam.genai.langchain4java.model.ModelParameters;
import com.oracle.ateam.genai.langchain4java.model.RequestChainPayload;
import com.oracle.ateam.genai.langchain4java.model.RequestPayload;
import jakarta.ws.rs.PathParam;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 * LangChain for Java API Resources
 *
 * Get default app version:
 * curl -X GET http://localhost:8080/llm/rest/v1
 *
 * Run Completion:
 * curl -d <Your Json Input> -X POST
 * http://localhost:8080/llm/rest/v1/completion
 * 
 * Run Chain (llm, httpRequest, oracleDb)
 * curl -d <Your Json Input> -X POST
 * http://localhost:8080/llm/rest/v1/chain/{chainType}
 * 
 * 
 * The message is returned as a JSON object.
 */
@Slf4j
@Path("/llm/rest/v1")
public class LangChain4JavaApiResource {

    private static final String PERSONALIZED_GETS_COUNTER_NAME = "personalizedGets";
    private static final String PERSONALIZED_GETS_COUNTER_DESCRIPTION = "Counts personalized GET operations";
    private static final String GETS_TIMER_NAME = "allGets";
    private static final String GETS_TIMER_DESCRIPTION = "Tracks all GET operations";
    private final String txt;
    private GenAILangChainService service = new GenAILangChainService();

    @Inject
    public LangChain4JavaApiResource(@ConfigProperty(name = "app.version") String txt) {
        this.txt = txt;
    }

    /**
     * Return a app version number.
     *
     * @return {@link DefaultResponse}
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public DefaultResponse getDefaultMessage() {
        String msg = String.format("%s %s!", "Version:", txt);
        DefaultResponse response = new DefaultResponse();
        response.setText(msg);
        return response;
    }

    /**
     * Processes a language completion request based on the provided
     * `RequestPayload`. It invokes the language model and returns
     * the generated language completion.
     *
     * @param payload The request payload containing language generation details.
     * @return The generated language completion.
     * @throws IOException
     */
    @Path("/completion")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Counted(name = PERSONALIZED_GETS_COUNTER_NAME, absolute = true, description = PERSONALIZED_GETS_COUNTER_DESCRIPTION)
    @Timed(name = GETS_TIMER_NAME, description = GETS_TIMER_DESCRIPTION, unit = MetricUnits.SECONDS, absolute = true)
    @RequestBody(name = "requestPayload", required = true, content = @Content(mediaType = "application/json", schema = @Schema(type = SchemaType.OBJECT, requiredProperties = {
            "modelParameters", "prompt" })))
    public String completion(RequestPayload payload) throws IOException {
        log.info("Invoke completion API...");
        ModelParameters llmParameters = payload.getModelParameters();
        var llm = service.getLLM(llmParameters);
        return llm.predict(payload.getPrompt());
    }

    /**
     * Processes single chains of requests based on the specified chain type by
     * invoking the `getChain` method of the
     * `GenAILangChaineService`.
     *
     * @param chainType The type of the chain to process.
     * @param payload   The request payload containing chain processing details.
     * @return The response from the `getChain` method.
     * @throws IOException
     */
    @Path("/chain/{chainType}")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Counted(name = PERSONALIZED_GETS_COUNTER_NAME, absolute = true, description = PERSONALIZED_GETS_COUNTER_DESCRIPTION)
    @Timed(name = GETS_TIMER_NAME, description = GETS_TIMER_DESCRIPTION, unit = MetricUnits.SECONDS, absolute = true)
    @RequestBody(name = "requestPayload", required = true, content = @Content(mediaType = "application/json", schema = @Schema(type = SchemaType.OBJECT, requiredProperties = {
            "modelParameters", "prompt" })))
    public String processChain(@PathParam("chainType") String chainType, RequestChainPayload payload)
            throws IOException {
        log.info("Process single chain ...");
        return service.getChain(chainType, payload);
    }
}
