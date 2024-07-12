/*******************************************************************************
 * Oracle OCI Generative AI LangChain For Java version 1.0.
 *
 * Copyright (c)  2024,  Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 ******************************************************************************/
package com.oracle.ateam.genai.langchain4java.chain.sql.oracle;

import com.hw.langchain.base.language.BaseLanguageModel;
import com.hw.langchain.chains.base.Chain;
import com.hw.langchain.chains.llm.LLMChain;
import com.hw.langchain.prompts.base.BasePromptTemplate;

import lombok.extern.slf4j.Slf4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.oracle.ateam.genai.langchain4java.chain.sql.oracle.prompt.Prompt.DECIDER_PROMPT;
import static com.oracle.ateam.genai.langchain4java.chain.sql.oracle.prompt.Prompt.PROMPT;

/**
 * The `OracleDatabaseSequentialChain` class is a sequential chain designed for
 * querying an Oracle SQL database. It is used to
 * determine which database tables to query based on the input query and then
 * invoke a normal SQL database chain to perform
 * the actual database query.
 *
 * This chain is particularly useful when dealing with databases containing a
 * large number of tables. It allows for the dynamic
 * selection of relevant tables based on the query, optimizing the database
 * query process.
 *
 * The chain workflow is as follows:
 * 1. Based on the provided query, determine which database tables are relevant
 * for the query.
 * 2. Use the determined table names to call the normal SQL database chain to
 * execute the query.
 *
 * The class relies on LLM (Language Model) chains and prompts for table name
 * selection, enabling dynamic decision-making in
 * the SQL query process.
 *
 * Example usage:
 * You can create an instance of `OracleDatabaseSequentialChain` by providing
 * the necessary SQL chain and LLM chain to
 * load the relevant tables and perform SQL queries. The chains should be loaded
 * using appropriate LLM models and prompts.
 *
 * Example usage:
 * ```java
 * OracleDatabaseChain sqlChain = OracleDatabaseChain.fromLLM(llm, database,
 * queryPrompt);
 * LLMChain deciderChain = new LLMChain(llm, deciderPrompt, "table_names");
 * OracleDatabaseSequentialChain sequentialChain = new
 * OracleDatabaseSequentialChain(sqlChain, deciderChain);
 * ```
 *
 */
@Slf4j
public class OracleDatabaseSequentialChain extends Chain {
    // Fields for SQL and LLM chains, input/output keys, and logger.
    private static final Logger LOG = LoggerFactory.getLogger(OracleDatabaseSequentialChain.class);
    private OracleDatabaseChain sqlChain;
    private LLMChain deciderChain;
    private String inputKey = "query";
    private String outputKey = "result";

    /**
     * Creates an instance of `OracleDatabaseSequentialChain` with the provided SQL
     * chain and LLM chain.
     *
     * @param sqlChain     The SQL chain responsible for database queries.
     * @param deciderChain The LLM chain used for table name selection.
     */
    public OracleDatabaseSequentialChain(OracleDatabaseChain sqlChain, LLMChain deciderChain) {
        this.sqlChain = sqlChain;
        this.deciderChain = deciderChain;
    }

    /**
     * Load the necessary chains and create an instance of
     * `OracleDatabaseSequentialChain` based on the provided LLM model,
     * Oracle database, and prompt templates.
     *
     * @param llm           The LLM (Language Model) to use for creating the chains.
     * @param database      The Oracle database to query.
     * @param queryPrompt   The prompt template for query input.
     * @param deciderPrompt The prompt template for table name selection.
     * @return An instance of `OracleDatabaseSequentialChain` configured with the
     *         provided chains.
     */
    public static OracleDatabaseSequentialChain fromLLM(BaseLanguageModel llm,
            OracleDatabase database,
            BasePromptTemplate queryPrompt,
            BasePromptTemplate deciderPrompt) {
        OracleDatabaseChain sqlChain = OracleDatabaseChain.fromLLM(llm, database, queryPrompt);
        LLMChain deciderChain = new LLMChain(llm, deciderPrompt, "table_names");
        return new OracleDatabaseSequentialChain(sqlChain, deciderChain);
    }

    /**
     * Create an instance of `OracleDatabaseSequentialChain` with the provided LLM
     * model and Oracle database.
     * Uses default prompt templates for query and table name selection.
     *
     * @param llm      The LLM (Language Model) to use for creating the chains.
     * @param database The Oracle database to query.
     * @return An instance of `OracleDatabaseSequentialChain` configured with
     *         default prompts.
     */
    public static OracleDatabaseSequentialChain fromLLM(BaseLanguageModel llm, OracleDatabase database) {
        return fromLLM(llm, database, PROMPT, DECIDER_PROMPT);
    }

    /**
     * Specifies the type of the chain, which is "oracle_database_sequential_chain".
     *
     * @return The type of the chain.
     */
    @Override
    public String chainType() {
        return "oracle_database_sequential_chain";
    }

    /**
     * Specifies the input keys used by the chain. In this case, it returns a list
     * with a single input key, "query".
     *
     * @return A list of input keys.
     */
    @Override
    public List<String> inputKeys() {
        return List.of(inputKey);
    }

    /**
     * Specifies the output keys used by the chain. In this case, it returns a list
     * with a single output key, "result".
     *
     * @return A list of output keys.
     */
    @Override
    public List<String> outputKeys() {
        return List.of(outputKey);
    }

    /**
     * Executes the Oracle Database Sequential Chain, determining relevant tables
     * based on the input query and invoking
     * the SQL database chain to perform the query.
     *
     * @param inputs The input data containing the query.
     * @return The result of the database query as a map.
     */
    @Override
    public Map<String, String> innerCall(Map<String, Object> inputs) {
        log.info("Executing Oracle Database Sequential Chain...");
        List<String> tableNameList = sqlChain.getDatabase().getUsableTableNames();
        String tableNames = String.join(", ", tableNameList);
        var llmInputs = Map.of("query", inputs.get(inputKey),
                "table_names", tableNames);

        List<String> lowerCasedTableNames = tableNameList.stream()
                .map(String::toLowerCase)
                .toList();

        List<String> tableNamesFromChain = deciderChain.predictAndParse(llmInputs);

        List<String> tableNamesToUse = new ArrayList<>();
        for (String name : tableNamesFromChain) {
            if (lowerCasedTableNames.contains(name.toLowerCase())) {
                tableNamesToUse.add(name);
            }
        }
        LOG.info("Table names to use: {}", tableNamesToUse);
        var newInputs = Map.of(sqlChain.getInputKey(), inputs.get(inputKey), "table_names_to_use", tableNamesToUse);
        return sqlChain.call(newInputs, true);
    }
}
