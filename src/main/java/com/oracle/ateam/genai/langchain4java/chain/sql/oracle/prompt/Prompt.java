/*******************************************************************************
 * Oracle OCI Generative AI LangChain For Java version 1.0.
 *
 * Copyright (c)  2023,  Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 ******************************************************************************/
package com.oracle.ateam.genai.langchain4java.chain.sql.oracle.prompt;

import com.hw.langchain.output.parsers.list.CommaSeparatedListOutputParser;
import com.hw.langchain.prompts.prompt.PromptTemplate;

import java.util.List;
import java.util.Map;

/**
 * Prompts Oracle database
 */
public class Prompt {

        private static String PROMPT_SUFFIX = "\nOnly use the following tables: {table_info} \nQuestion: {input}";

        private static String CMD_PROMPT_SUFFIX = "\nOnly use the following tables: {table_info} \nQuestion: {input}";

        private static String _DEFAULT_TEMPLATE = "Given an input question, first create a syntactically correct Oracle database query to run, then look at the results of the query and return the answer. Unless the user specifies in his question a specific number of examples he wishes to obtain, always limit your query to at most {top_k} results. You can order the results by a relevant column to return the most interesting examples in the database.\nNever query for all the columns from a specific table, only ask for a the few relevant columns given the question.\nPay attention to use only the column names that you can see in the schema description. Be careful to not query for columns that do not exist. Also, pay attention to which column is in which table.\n\nUse the following format:\n\nQuestion: Question here\n\nSQLQuery: SQL Query to run\n\nSQLResult: Result of the SQLQuery \n\nAnswer:\n";

        public static PromptTemplate PROMPT = new PromptTemplate(List.of("input", "table_info", "dialect", "top_k"),
                        _DEFAULT_TEMPLATE + PROMPT_SUFFIX);

        private static String _DECIDER_TEMPLATE = "Given the below input question and list of potential tables, output a comma separated list of the table names that may be necessary to answer this question.\nQuestion: {query} \n\nTable Names: {table_names}\nRelevant Table Names:";

        public static PromptTemplate DECIDER_PROMPT = new PromptTemplate(_DECIDER_TEMPLATE,
                        List.of("query", "table_names"),
                        new CommaSeparatedListOutputParser());

        private static String _oracle_prompt = "You are an Oracle SQL expert. Given an input question, first create a syntactically correct Oracle SQL query to run, then look at the results of the query and return the answer to the input question. \nUnless the user specifies in the question a specific number of examples to obtain, query for at most {top_k} results using the FETCH FIRST n ROWS ONLY clause as per Oracle SQL. You can order the results to return the most informative data in the database. \nNever query for all columns from a table. You must query only the columns that are needed to answer the question. Wrap each column name in double quotes (\") to denote them as delimited identifiers. \nPay attention to use only the column names you can see in the tables below. Be careful to not query for columns that do not exist. Also, pay attention to which column is in which table. \nPay attention to use TRUNC(SYSDATE) function to get the current date, if the question involves \"today\". Must use the following format: \nQuestion: Question here\nSQLQuery: SQL Query to run\nSQLResult: Result of the SQLQuery\nAnswer:\n";

        private static String _oracle_cmd_prompt = "You are an Oracle SQL expert. Given an input question and the SQL query statments, look at the results of the query and return the answer to the input question.  Must use the following format:\nQuestion: Question here\nSQLQuery: SQL Query to run\nAnswer: Final answer here\n";

        public static PromptTemplate ORACLE_PROMPT = new PromptTemplate(List.of("input", "table_info", "top_k"),
                        _oracle_prompt + PROMPT_SUFFIX);

        public static PromptTemplate ORACLE_CMD_PROMPT = new PromptTemplate(List.of("input", "table_info", "top_k"),
                        _oracle_cmd_prompt + CMD_PROMPT_SUFFIX);

        public static final Map<String, PromptTemplate> SQL_PROMPTS = Map.of(
                        "oracle", ORACLE_PROMPT, "oracle_cmd", ORACLE_CMD_PROMPT);
}
