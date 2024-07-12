
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

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.*;

import static com.oracle.ateam.genai.langchain4java.chain.sql.oracle.prompt.Prompt.SQL_PROMPTS;

/**
 * The `OracleDatabaseChain` class is designed to interact with an SQL Database
 * using an associated Language Model (LLM).
 * It facilitates executing SQL queries based on provided input and returning
 * the results. This class supports various
 * options for query execution, including SQL commands and the use of a query
 * checker tool.
 *
 * Features of the `OracleDatabaseChain` class include:
 * - Configurable query execution, allowing the use of SQL commands or
 * LLM-generated queries.
 * - Specifying the number of results to return from a query (topK).
 * - Option to return intermediate steps along with the final result.
 * - Option to return the direct result of querying the SQL table.
 * - Ability to use a query checker tool to refine SQL queries.
 *
 * The class is typically used in conjunction with an Oracle Database and a
 * relevant LLM model, which can generate SQL queries
 * based on input text. The LLM model, in combination with specific prompt
 * templates, enhances the query generation process.
 *
 * Example usage:
 * You can create an instance of `OracleDatabaseChain` by providing an LLM
 * chain, an Oracle Database, and other configuration
 * options such as SQL commands and properties. The class can be used to execute
 * SQL queries based on provided input.
 *
 * Example usage:
 * ```java
 * OracleDatabaseChain databaseChain = new OracleDatabaseChain(llmChain,
 * database, sqlCmd, sqlProperties);
 * ```
 *
 */
@Slf4j
@EqualsAndHashCode(callSuper = false)
@Data
public class OracleDatabaseChain extends Chain {

    // Fields for LLM chain, Oracle Database, input/output keys, and configuration
    // options.
    private LLMChain llmChain;
    private OracleDatabase database;
    private int topK = 5;
    private String inputKey = "query";
    private String outputKey = "result";
    private boolean returnIntermediateSteps;
    private boolean returnDirect;
    private boolean useQueryChecker;
    private BasePromptTemplate queryCheckerPrompt;
    private String sqlCmd;
    private Map<String, Object> sqlProperties;

    /**
     * Creates an instance of `OracleDatabaseChain` with the provided LLM chain and
     * Oracle Database.
     *
     * @param llmChain The LLM chain responsible for generating SQL queries.
     * @param database The Oracle Database to query.
     */
    public OracleDatabaseChain(LLMChain llmChain, OracleDatabase database) {
        this.llmChain = llmChain;
        this.database = database;
    }

    /**
     * Creates an instance of `OracleDatabaseChain` with the provided LLM chain,
     * Oracle Database, SQL commands, and SQL properties.
     *
     * @param llmChain      The LLM chain responsible for generating SQL queries.
     * @param database      The Oracle Database to query.
     * @param sqlCmd        The SQL command to execute.
     * @param sqlProperties Properties to replace placeholders in the SQL command.
     */
    public OracleDatabaseChain(LLMChain llmChain, OracleDatabase database, String sqlCmd,
            Map<String, Object> sqlProperties) {
        this.llmChain = llmChain;
        this.database = database;
        this.sqlCmd = sqlCmd;
        this.sqlProperties = sqlProperties;
    }

    /**
     * Creates an instance of `OracleDatabaseChain` from the provided LLM, Oracle
     * Database, and a default prompt template.
     *
     * @param llm      The LLM (Language Model) to use for query generation.
     * @param database The Oracle Database to query.
     * @return An instance of `OracleDatabaseChain` configured with default prompts.
     */
    public static OracleDatabaseChain fromLLM(BaseLanguageModel llm, OracleDatabase database) {
        BasePromptTemplate prompt = SQL_PROMPTS.get("oracle");
        return fromLLM(llm, database, prompt);
    }

    public static OracleDatabaseChain fromSqlCmd(BaseLanguageModel llm, OracleDatabase database, String sql,
            Map<String, Object> sqlProperties) {
        BasePromptTemplate prompt = SQL_PROMPTS.get("oracle_cmd");
        return fromLLM(llm, database, prompt, sql, sqlProperties);
    }

    public static OracleDatabaseChain fromLLM(BaseLanguageModel llm, OracleDatabase database,
            BasePromptTemplate prompt) {
        LLMChain llmChain = new LLMChain(llm, prompt);
        return new OracleDatabaseChain(llmChain, database);
    }

    /**
     * Creates an instance of `OracleDatabaseChain` from the provided LLM, Oracle
     * Database, prompt, SQL commands, and SQL properties.
     *
     * @param llm           The LLM (Language Model) to use for query generation.
     * @param database      The Oracle Database to query.
     * @param prompt        The prompt template for generating SQL queries.
     * @param sql           The SQL command to execute.
     * @param sqlProperties Properties to replace placeholders in the SQL command.
     * @return An instance of `OracleDatabaseChain` configured with the provided
     *         parameters.
     */
    public static OracleDatabaseChain fromLLM(BaseLanguageModel llm, OracleDatabase database,
            BasePromptTemplate prompt, String sql, Map<String, Object> sqlProperties) {
        LLMChain llmChain = new LLMChain(llm, prompt);
        return new OracleDatabaseChain(llmChain, database, sql, sqlProperties);
    }

    /**
     * Specifies the type of the chain, which is "sql_database_chain".
     *
     * @return The type of the chain.
     */
    @Override
    public String chainType() {
        return "sql_database_chain";
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
     * Executes the Oracle Database Chain, performing SQL queries and obtaining
     * results based on the provided inputs.
     *
     * @param inputs The input data containing the query and table names (if
     *               applicable).
     * @return The result of the database query as a map, with the specified output
     *         key.
     */
    @SuppressWarnings("unchecked")
    @Override
    public Map<String, String> innerCall(Map<String, Object> inputs) {
        String inputText;
        Map<String, Object> llmInputs = new HashMap<>();
        var tableNamesToUse = (List<String>) inputs.get("table_names_to_use");
        String tableInfo = database.getTableInfo(tableNamesToUse);
        if (sqlCmd == null || sqlCmd.isEmpty()) {
            inputText = inputs.get(this.inputKey) + "\nSQLQuery:";
            // If not present, then defaults to null which is all tables.
            llmInputs.put("input", inputText);
            llmInputs.put("top_k", topK);
            llmInputs.put("dialect", database.getDialect());
            llmInputs.put("table_info", tableInfo);
            llmInputs.put("stop", List.of("\n\nSQLResult:"));

            String predictResult = llmChain.predict(llmInputs);
            String sqlCmd = extractSQLQueryFromText(predictResult);
            if (sqlCmd == null) {
                int index1 = predictResult.indexOf("\nSQLResult");
                int index2 = predictResult.indexOf("\nAnswer");
                if (index1 != -1 && (index2 == -1 || index1 < index2)) {
                    sqlCmd = predictResult.substring(0, index1).replace(";", "");
                } else if (index2 != -1) {
                    sqlCmd = predictResult.substring(0, index2).replace(";", "");
                } else {
                    sqlCmd = predictResult;
                }
            }
            this.sqlCmd = sqlCmd;
        } else {
            inputText = (String) inputs.get(this.inputKey);
            llmInputs.put("input", inputText);
            llmInputs.put("top_k", topK);
            llmInputs.put("dialect", database.getDialect());
            llmInputs.put("table_info", tableInfo);
            llmInputs.put("stop", List.of("\n\nSQLResult:"));
            for (Entry<String, Object> entry : sqlProperties.entrySet()) {
                llmInputs.put(entry.getKey(), entry.getValue());
                String placeholder = "{" + entry.getKey() + "}";
                String value = entry.getValue().toString();
                sqlCmd = sqlCmd.replace(placeholder, value);
            }
        }

        sqlCmd = sqlCmd.replace(";", "");

        String result = database.run(sqlCmd, true);

        /*
         * If return direct, we just set the final result equal to the result of the sql
         * query result, otherwise try to
         * get a human readable final answer
         */
        String finalResult;
        if (returnDirect) {
            finalResult = result;
        } else {
            // inputText += String.format("%s\nSQLResult: %s\nAnswer:", sqlCmd, result);
            inputText += String.format("\nSQLResult:\n%s\nAnswer:", result);
            llmInputs.put("input", inputText);
            finalResult = llmChain.predict(llmInputs).trim();
        }
        
        if (log.isDebugEnabled()) {
            log.debug("SQL command:\n {}", sqlCmd);
            log.debug("SQLResult: \n{}", result);
            log.debug("Final Result: \n{}", finalResult);
        }

        return Map.of(outputKey, finalResult);
    }

    /**
     * Extracts the SQL query from a text string containing the query and result.
     *
     * @param input The input string containing the SQL query and result.
     * @return The extracted SQL query.
     */
    public String extractSQLQueryFromText(String input) {
        Pattern pattern = Pattern.compile("SQLQuery:(.*?)SQLResult:", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(input);

        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return null;
        }
    }
}
