/*******************************************************************************
 * Oracle OCI Generative AI LangChain For Java version 1.0.
 *
 * Copyright (c)  2024,  Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 ******************************************************************************/
package com.oracle.ateam.genai.langchain4java;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.hw.langchain.schema.Generation;
import com.hw.langchain.schema.LLMResult;
import com.oracle.ateam.genai.langchain4java.chain.http.base.HttpRequestChain;
import com.oracle.ateam.genai.langchain4java.chain.sql.oracle.OracleDatabase;
import com.oracle.ateam.genai.langchain4java.chain.sql.oracle.OracleDatabaseChain;
import com.oracle.ateam.genai.langchain4java.chain.sql.oracle.OracleDatabaseSequentialChain;
import com.oracle.ateam.genai.langchain4java.llms.GenAICohereEmbedModel;
import com.oracle.ateam.genai.langchain4java.llms.GenAICohereGenerationModel;
import com.oracle.ateam.genai.langchain4java.llms.GenAICohereSummerizeModel;

import com.oracle.bmc.Region;
import com.oracle.bmc.generativeaiinference.responses.EmbedTextResponse;

import static com.oracle.ateam.genai.langchain4java.chain.http.prompt.Prompt.HTTPREQUEST_RESPONSE_PROMPT;

import lombok.extern.slf4j.Slf4j;

/**
 * Unit test for GenAILangChain4Java.
 */
@Slf4j
public class GenAILangChain4JavaTest {
    private static final int testDelay = 5000;
    private static final String GENERATION_MODEL_ID = "cohere.command";
    private static final String SUMMARIZE_MODEL_ID = "cohere.command";
    private static final String EMBED_MODEL_ID = "cohere.embed-english-light-v2.0";
    private static final String CONFIG_PROFILE = "DEFAULT";                 //Change the OCI config profile if required
    private static final String COMPARTMENT_ID = "<Your Compartment ID>";   //Provide your compartment ocid.
    private static final Region REGION = Region.US_CHICAGO_1;
    private static final String ENDPOINT = "https://inference.generativeai.us-chicago-1.oci.oraclecloud.com";  // Generative AI Inferenace endpoint in Chicago Region.
    private static final String CONFIG_LOCATION = "~/.oci/config";
    private static final Double TEMPERATURE = 0.75;

    //For Database Test
    private static final String databaseURL = "<jdbcurl>";      // Provide your database connection details: example: jdbc:oracle:thin:@127.0.0.1:1521:xe
    private static final String dbUser = "<demodbuser";         //Provide your database username
    private static final String dbPwd = "<demouserpwd>";        //Provide your database user password

    //For HTTP REST API Test
    private static final String API_URL = "xxx";                //Provide your external API URL, only HTTP get is supported at this moment
    private static final String API_AUTH_TOKEN = "Basic xxx=="; //Provide your external API basic authentication token 


    /**
     * Initilize before test start
     * 
     * @throws IOException
     */
    @BeforeEach
    public void initialise() throws InterruptedException {
        Thread.sleep(testDelay);
    }

    @Test
    public void GenAI_Cohere_Command_1() throws IOException {
        var llm = GenAICohereGenerationModel.builder()
                .modeId(GENERATION_MODEL_ID)
                .temperature(TEMPERATURE)
                .compartmentId(COMPARTMENT_ID)
                .configProfile(CONFIG_PROFILE)
                .endpoint(ENDPOINT)
                .configLocation(CONFIG_LOCATION)
                .region(REGION)
                .build()
                .init();
        String prompts = "Tell me something about Oracle Cloud Infrastructure";
        String output = llm.predict(prompts);
        log.info(output);
        assertTrue(true);
    }

    @Test
    public void GenAI_Cohere_Command_2() throws IOException {
        var llm = GenAICohereGenerationModel.builder()
                .modeId(GENERATION_MODEL_ID)
                .temperature(TEMPERATURE)
                .compartmentId(COMPARTMENT_ID)
                .configProfile(CONFIG_PROFILE)
                .endpoint(ENDPOINT)
                .configLocation(CONFIG_LOCATION)
                .region(REGION)
                .build()
                .init();

        List<String> stopWords = new ArrayList<String>();
        List<String> prompts = new ArrayList<String>();

        prompts.add("Tell me something about Oracle");
        stopWords.add(".");

        LLMResult result = llm.generate(prompts, stopWords);
        Map<String, Object> llmoutput = result.getLlmOutput();
        for (Map.Entry<String, Object> entry : llmoutput.entrySet())
            log.info("Key = " + entry.getKey() +
                    ", Value = " + entry.getValue());
        List<? extends List<? extends Generation>> gens = result.getGenerations();
        Generation gen = (Generation) gens.get(0).get(0);
        log.info(gen.getText());
        assertTrue(true);
    }

    @Test
    public void GenAI_Cohere_Command_Summerize_1() throws IOException, InterruptedException, ExecutionException {
        var llm = GenAICohereSummerizeModel.builder()
                .modeId(SUMMARIZE_MODEL_ID)
                .temperature(TEMPERATURE)
                .compartmentId(COMPARTMENT_ID)
                .configProfile(CONFIG_PROFILE)
                .endpoint(ENDPOINT)
                .configLocation(CONFIG_LOCATION)
                .region(REGION)
                .build()
                .init();

        String textToSummarize = "Quantum dots (QDs) - also called semiconductor nanocrystals, are semiconductor particles a few nanometres in size, having optical and electronic properties that differ from those of larger particles as a result of quantum mechanics. They are a central topic in nanotechnology and materials science. When the quantum dots are illuminated by UV light, an electron in the quantum dot can be excited to a state of higher energy. In the case of a semiconducting quantum dot, this process corresponds to the transition of an electron from the valence band to the conductance band. The excited electron can drop back into the valence band releasing its energy as light. This light emission (photoluminescence) is illustrated in the figure on the right. The color of that light depends on the energy difference between the conductance band and the valence band, or the transition between discrete energy states when the band structure is no longer well-defined in QDs.";

        String output = llm.summerize(textToSummarize);
        log.info(output);
        assertTrue(true);
    }

    @Test
    public void GenAI_Cohere_Embed_1() throws IOException, InterruptedException, ExecutionException {
        var llm = GenAICohereEmbedModel.builder()
                .modeId(EMBED_MODEL_ID)
                .compartmentId(COMPARTMENT_ID)
                .configProfile(CONFIG_PROFILE)
                .endpoint(ENDPOINT)
                .configLocation(CONFIG_LOCATION)
                .region(REGION)
                .build()
                .init();

        List<String> inputs = Arrays.asList("hello", "goodbye");

        EmbedTextResponse output = llm.embed(inputs);
        log.info(output.getEmbedTextResult().getEmbeddings().toString());
        assertTrue(true);
    }

    @Disabled("Require External API.")
    @Test
    public void GenAI_HttpRequestChain_1() {

        try {
            var llm = GenAICohereGenerationModel.builder()
                    .modeId(GENERATION_MODEL_ID)
                    .compartmentId(COMPARTMENT_ID)
                    .configProfile(CONFIG_PROFILE)
                    .endpoint(ENDPOINT)
                    .configLocation(CONFIG_LOCATION)
                    .region(REGION)
                    .temperature(TEMPERATURE)
                    .build()
                    .init();

            Map<String, String> headers = new HashMap<String, String>();
            headers.put("Authorization", API_AUTH_TOKEN);
            headers.put("Content-Type", "application/json");
            Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("q", "FirstName=\"Casey\";LastName=\"Brown\"");
            parameters.put("onlyData", true);

            var chain = HttpRequestChain.usingApiURL(llm, API_URL, parameters, headers, HTTPREQUEST_RESPONSE_PROMPT);
            var result = chain.run("Tell me about Casey Brown? Don't tell me any PII information");
            log.info(result);
            assertTrue(!result.isEmpty());
        } catch (Exception e) {
            e.printStackTrace();
            fail("Unexpected exception was thrown");
        }
    }

    @Disabled("Require Database.")
    @Test
    public void GenAI_OracleDBChain_1() {
        try {
            var llm = GenAICohereGenerationModel.builder()
                    .modeId(GENERATION_MODEL_ID)
                    .build()
                    .init();

            var database = OracleDatabase.fromUri(databaseURL, dbUser, dbPwd);
            var question = "Is Casey Brown in the database? What is his role?";

            var chain = OracleDatabaseChain.fromLLM(llm, database);
            var result = chain.run(question);
            assertTrue(!result.isEmpty());
        } catch (Exception e) {
            e.printStackTrace();
            fail("Unexpected exception was thrown");
        }
    }

    @Disabled("Require Database.")
    @Test
    public void GenAI_OracleDBChain_WithSQLCmd_Simple() {
        try {
            var llm = GenAICohereGenerationModel.builder()
                    .modeId(GENERATION_MODEL_ID)
                    .build()
                    .init();

            var database = OracleDatabase.fromUri(databaseURL, dbUser, dbPwd);
            var sqlCmd = "SELECT * FROM AUTHOR t1 JOIN BOOKS t2 ON t1.ID = t2.AUTHOR_ID";
            Map<String, Object> sqlProperties = new HashMap<String, Object>();
            sqlProperties.put("name", "'Casey Brown'");
            var question = "Is Casey Brown in the database? What is his role?";

            var chain = OracleDatabaseChain.fromSqlCmd(llm, database, sqlCmd,
                    sqlProperties);
            var result = chain.run(question);
            assertTrue(!result.isEmpty());
        } catch (Exception e) {
            e.printStackTrace();
            fail("Unexpected exception was thrown");
        }
    }

    @Disabled("Require Database.")
    @Test
    public void GenAI_OracleDBSequentialChain_1() {
        try {
            var llm = GenAICohereGenerationModel.builder()
                    .modeId(GENERATION_MODEL_ID)
                    .build()
                    .init();

            var database = OracleDatabase.fromUri(databaseURL, dbUser, dbPwd);
            var chain = OracleDatabaseSequentialChain.fromLLM(llm, database);

            var result = chain.run("Is Casey Brown in the database? What books did he published");
            assertTrue(!result.isEmpty());
        } catch (Exception e) {
            e.printStackTrace();
            fail("Unexpected exception was thrown");
        }
    }
}