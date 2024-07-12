/*******************************************************************************
 * Oracle OCI Generative AI LangChain For Java version 1.0.
 *
 * Copyright (c)  2024,  Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 ******************************************************************************/
package com.oracle.ateam.genai.langchain4java.llms;

import lombok.Builder;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

import java.util.*;

import com.hw.langchain.llms.base.BaseLLM;
import com.hw.langchain.schema.AsyncLLMResult;
import com.hw.langchain.schema.LLMResult;
import com.oracle.bmc.generativeaiinference.model.CohereLlmInferenceRequest;
import com.oracle.bmc.generativeaiinference.model.CohereLlmInferenceResponse;
import com.oracle.bmc.generativeaiinference.model.GeneratedText;
import com.oracle.bmc.generativeaiinference.responses.GenerateTextResponse;

/**
 * The `GenAIGenerationBase` class serves as the base class for building text
 * generation models that utilize the Generative AI service.
 * It provides a set of configuration options and methods for text generation
 * using the Cohere Command Language Model, which is part of the Generative AI
 * service.
 *
 * This base class simplifies the setup and interaction with the Generative AI
 * service for text generation, and it can be extended to
 * create custom text generation models with specific requirements. It supports
 * customization through environment variables, enabling
 * you to configure parameters like model ID, maximum tokens, temperature, and
 * more for text generation.
 *
 * Example usage:
 * Subclasses can extend this base class to define specialized text generation
 * models. You can override methods and configure
 * parameters to meet your specific text generation needs.
 *
 * Example usage:
 * ```java
 * public class CustomGenerationModel extends GenAIGenerationBase {
 * // Customize and configure specific parameters or methods for your text
 * generation model.
 * }
 * ```
 *
 * The `GenAIGenerationBase` class leverages the Cohere Command Language Model
 * to generate text based on the provided input,
 * making it suitable for a wide range of text generation tasks.
 *
 * Note: The class relies on environment variables for configuration, which can
 * be set or overridden to customize the behavior of
 * the GenAI Cohere Command Language Model for text generation.
 *
 */

@Slf4j
@SuperBuilder
public class GenAICohereGenerationBase extends BaseLLM {

    // Fields for configuration and interaction with the Generative AI service.
    GenerativeAiClient generativeAiClient;
    String modeId;
    Double temperature;
    Integer maxTokens;
    Integer batchSize;
    Double topP;
    Integer topK;
    Double presencePenalty;
    Double frequencyPenalty;
    List<String> stopSequences;
    @Builder.Default
    Integer numGenerations = 1;
    @Builder.Default
    Boolean isStream = false;
    @Builder.Default
    Boolean isEcho = false;

    /**
     * Generates text using the Generative AI service based on the provided prompts
     * and stop sequences.
     *
     * @param prompts The list of prompts to generate text from.
     * @param stop    The list of stop sequences to determine text generation
     *                termination.
     * @return The result of text generation as an LLMResult.
     */
    protected LLMResult innerGenerate(List<String> prompts, List<String> stop) {
        log.info("Generating Text");
        List<GeneratedText> outputs = null;
        try {
            CohereLlmInferenceRequest cohereLlmInferenceRequest = CohereLlmInferenceRequest.builder()
                    .prompt(prompts.get(0))
                    .maxTokens(maxTokens)
                    .temperature(temperature)
                    .frequencyPenalty(frequencyPenalty)
                    .presencePenalty(presencePenalty)
                    .topP(topP)
                    .topK(topK)
                    .stopSequences(stopSequences)
                    .numGenerations(numGenerations)
                    .isStream(isStream)
                    .isEcho(isEcho)
                    .build();

            GenerateTextResponse generateTextResponse = generativeAiClient.generateText(cohereLlmInferenceRequest);
            CohereLlmInferenceResponse cohereResponse = (CohereLlmInferenceResponse) generateTextResponse
                    .getGenerateTextResult().getInferenceResponse();
            outputs = cohereResponse.getGeneratedTexts();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return createLLMResult(outputs, prompts, Map.of());
    }

    // Private method to create an LLMResult based on generated text and prompts.
    private LLMResult createLLMResult(List<GeneratedText> generatedTexts, List<String> prompts,
            Map<String, Integer> tokenUsage) {
        // Create an LLMResult based on generated text and prompts.
        List<List<com.hw.langchain.schema.Generation>> generations = new ArrayList<>();
        for (int i = 0; i < prompts.size(); i++) {
            List<GeneratedText> subChoices = generatedTexts.subList(i * numGenerations, (i + 1) * numGenerations);
            List<com.hw.langchain.schema.Generation> generationList = new ArrayList<>();
            for (GeneratedText generatedText : subChoices) {
                Map<String, Object> generationInfo = new HashMap<>(2);
                // generationInfo.put("finishReason", generatedText.getFinishReason());
                // generationInfo.put("likelihood", generatedText.getLikelihood());
                // generationInfo.put("tokenlikelihoods", generatedText.getTokenLikelihoods());
                generationInfo.put("id", generatedText.getId());

                com.hw.langchain.schema.Generation generation = com.hw.langchain.schema.Generation.builder()
                        .text(generatedText.getText())
                        .generationInfo(generationInfo)
                        .build();
                generationList.add(generation);
            }
            generations.add(generationList);
        }

        Map<String, Object> llmOutput = new HashMap<>(2);
        llmOutput.put("token_usage", tokenUsage);
        llmOutput.put("mode_id", modeId);

        return new LLMResult(generations, llmOutput);
    }

    @Override
    public String llmType() {
        return "genai_cohere";
    }

    @Override
    protected Flux<AsyncLLMResult> asyncInnerGenerate(List<String> arg0, List<String> arg1) {
        throw new UnsupportedOperationException("Unimplemented method 'asyncInnerGenerate'");
    }
}
