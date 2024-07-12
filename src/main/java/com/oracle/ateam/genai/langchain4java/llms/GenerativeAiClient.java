/*******************************************************************************
 * Oracle OCI Generative AI LangChain For Java version 1.0.
 *
 * Copyright (c)  2024,  Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 ******************************************************************************/
package com.oracle.ateam.genai.langchain4java.llms;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ExecutionException;

import com.oracle.bmc.ClientConfiguration;
import com.oracle.bmc.ConfigFileReader;
import com.oracle.bmc.Region;
import com.oracle.bmc.auth.AuthenticationDetailsProvider;
import com.oracle.bmc.auth.ConfigFileAuthenticationDetailsProvider;
import com.oracle.bmc.http.client.HttpRequest;
import com.oracle.bmc.generativeaiinference.GenerativeAiInferenceClient;
import com.oracle.bmc.generativeaiinference.model.CohereLlmInferenceRequest;
import com.oracle.bmc.generativeaiinference.model.EmbedTextDetails;
import com.oracle.bmc.generativeaiinference.model.GenerateTextDetails;
import com.oracle.bmc.generativeaiinference.model.OnDemandServingMode;
import com.oracle.bmc.generativeaiinference.model.SummarizeTextDetails;
import com.oracle.bmc.generativeaiinference.requests.EmbedTextRequest;
import com.oracle.bmc.generativeaiinference.requests.GenerateTextRequest;
import com.oracle.bmc.generativeaiinference.requests.SummarizeTextRequest;
import com.oracle.bmc.generativeaiinference.responses.EmbedTextResponse;
import com.oracle.bmc.generativeaiinference.responses.GenerateTextResponse;
import com.oracle.bmc.generativeaiinference.responses.SummarizeTextResponse;
import com.oracle.bmc.http.client.StandardClientProperties;
import com.oracle.bmc.http.client.jersey3.ApacheClientProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

/**
 * The `GenerativeAiRestClient` class provides a client for interacting with a
 * Generative AI service.
 *
 * This class allows you to send requests for generating text, summarizing text,
 * and embedding text using the Generative AI service.
 * It includes methods for each of these operations and handles the HTTP
 * communication with the service.
 */
@Slf4j
@AllArgsConstructor
@SuperBuilder
@Data
public class GenerativeAiClient {
        private ClientConfiguration configuration;
        private Region region;
        private HttpRequest request;
        private String configLocation;
        private String configProfile;
        private String modelId;
        private String endpoint;
        private GenerativeAiInferenceClient generativeAiInferenceClient;
        private String compartmentId;
        private AuthenticationDetailsProvider provider ;
        /**
         * Initializes the GenerativeAiRestClient by loading OCI configuration and
         * settings.
         *
         * @return The initialized GenerativeAiRestClient.
         * @throws IOException
         */
        public GenerativeAiClient init() throws IOException {
                getOCIConfig(configProfile);
                generativeAiInferenceClient = GenerativeAiInferenceClient.builder()
                                .clientConfigurator(builder -> {
                                        builder.property(StandardClientProperties.BUFFER_REQUEST, false);
                                        builder.property(ApacheClientProperties.RETRY_HANDLER, null);
                                        builder.property(ApacheClientProperties.REUSE_STRATEGY, null);
                                })
                                .build(provider);
                generativeAiInferenceClient.setEndpoint(endpoint);
                generativeAiInferenceClient.setRegion(region);
                return this;
        }

        /**
         * Generates text using the Generative AI service.
         *
         * @param CohereLlmInferenceRequest The request for text generation using
         *                                  Cohere.
         * @return The response containing the generated text.
         * @throws InterruptedException if the execution is interrupted.
         * @throws ExecutionException   if an execution exception occurs.
         * @throws IOException          if an I/O exception occurs.
         */
        public GenerateTextResponse generateText(CohereLlmInferenceRequest cohereLlmInferenceRequest)
                        throws InterruptedException, ExecutionException, IOException {
                OnDemandServingMode servingMode = OnDemandServingMode.builder()
                                .modelId(modelId)
                                .build();
                GenerateTextDetails generateTextDetails = GenerateTextDetails.builder()
                                .servingMode(servingMode)
                                .compartmentId(compartmentId)
                                .inferenceRequest(cohereLlmInferenceRequest)
                                .build();
                GenerateTextRequest generateTextRequest = GenerateTextRequest.builder()
                                .generateTextDetails(generateTextDetails)
                                .build();

                GenerateTextResponse generateTextResponse = generativeAiInferenceClient
                                .generateText(generateTextRequest);
                return generateTextResponse;

        }

        /**
         * Summarizes text using the Generative AI service.
         *
         * @param textToSummarize The text for summarization.
         * @return The response containing the summarized text.
         * @throws InterruptedException if the execution is interrupted.
         * @throws ExecutionException   if an execution exception occurs.
         * @throws IOException          if an I/O exception occurs.
         */
        public SummarizeTextResponse summarizeText(String textToSummarize)
                        throws InterruptedException, ExecutionException, IOException {
                OnDemandServingMode servingMode = OnDemandServingMode.builder()
                                .modelId(modelId)
                                .build();
                SummarizeTextDetails summarizeTextDetails = SummarizeTextDetails.builder()
                                .servingMode(servingMode)
                                .compartmentId(compartmentId)
                                .input(textToSummarize)
                                .build();
                SummarizeTextRequest summarizeTextRequest = SummarizeTextRequest.builder()
                                .summarizeTextDetails(summarizeTextDetails)
                                .build();

                SummarizeTextResponse summarizeTextResponse = generativeAiInferenceClient
                                .summarizeText(summarizeTextRequest);
                return summarizeTextResponse;

        }

        /**
         * Embeds text using the Generative AI service.
         *
         * @param inputs The list of text to embed.
         * @return The response containing the embedded text.
         * @throws InterruptedException if the execution is interrupted.
         * @throws ExecutionException   if an execution exception occurs.
         * @throws IOException          if an I/O exception occurs.
         */
        public EmbedTextResponse embedText(List<String> inputs)
                        throws InterruptedException, ExecutionException, IOException {

                OnDemandServingMode servingMode = OnDemandServingMode.builder()
                                .modelId(modelId)
                                .build();
                EmbedTextDetails embedTextDetails = EmbedTextDetails.builder()
                                .servingMode(servingMode)
                                .compartmentId(compartmentId)
                                .inputs(inputs)
                                .build();
                EmbedTextRequest embedTextRequest = EmbedTextRequest.builder().embedTextDetails(embedTextDetails)
                                .build();
                EmbedTextResponse embedTextResponse = generativeAiInferenceClient.embedText(embedTextRequest);

                return embedTextResponse;
        }

        /**
         * Rretieve the OCI configuration information from the config file.
         */
        private void getOCIConfig(String configProfile) {
                try {
                        String absolutePath = null;
                        URL configPath = this.getClass().getClassLoader().getResource("config");
                        if (configPath != null) {
                                File file = Paths.get(configPath.toURI()).toFile();
                                absolutePath = file.getAbsolutePath();
                        }
                        if (System.getenv("CONFIG_LOCATION") != null) {
                                log.info("Load config from system environment config : CONFIG_LOCATION");
                                this.configLocation = System.getenv("CONFIG_LOCATION");
                        } else if (absolutePath != null) {
                                log.info("Load config from " + absolutePath);
                                this.configLocation = absolutePath;
                        } else {
                                log.info("Load config from home path - ~/.oci/config");
                                this.configLocation = "~/.oci/config";
                        }

                        this.configProfile = configProfile == null ? "DEFAULT" : configProfile;

                        ConfigFileReader.ConfigFile configFile = ConfigFileReader.parse(this.configLocation,
                                        this.configProfile);

                        this.provider = new ConfigFileAuthenticationDetailsProvider(
                                        configFile);

                        this.compartmentId = System.getenv("COMPARTMENT_ID") == null ? configFile.get("compartment-id")
                                        : System.getenv("COMPARTMENT_ID");

                        this.region = System.getenv("REGION") == null ? Region.fromRegionCode(configFile.get("region"))
                                        : Region.fromRegionCode(System.getenv("REGION"));

                        this.endpoint = "https://inference.generativeai." + region.getRegionId() + ".oci.oraclecloud.com";

                } catch (URISyntaxException urle) {
                        log.error(urle.toString());
                } catch (IOException ioe) {
                        log.error(ioe.toString());
                }
        }
}
