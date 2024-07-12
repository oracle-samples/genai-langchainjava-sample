/*******************************************************************************
 * Oracle OCI Generative AI LangChain For Java version 1.0.
 *
 * Copyright (c)  2024,  Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 ******************************************************************************/
package com.oracle.ateam.genai.langchain4java.chain.http.prompt;

import com.hw.langchain.prompts.prompt.PromptTemplate;

import java.util.List;

/**
 * Prompts for http request
 */
public class Prompt {

        private Prompt() {
        }

        private static final String HTTPREQUEST_RESPONSE_PROMPT_TEMPLATE = """
                        Question:{question}

                        {api_url}

                        Here is the response from the API:

                        {api_response}

                        Summarize this response to answer the original question.

                        Summary:""";

        public static final PromptTemplate HTTPREQUEST_RESPONSE_PROMPT = new PromptTemplate(
                        List.of( "question", "api_url", "api_response"), HTTPREQUEST_RESPONSE_PROMPT_TEMPLATE);
}
