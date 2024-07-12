/*******************************************************************************
 * Oracle OCI Generative AI LangChain For Java version 1.0.
 *
 * Copyright (c)  2024,  Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 ******************************************************************************/
package com.oracle.ateam.genai.langchain4java.model;

import lombok.Data;

/**
 * The `OracleDbRequestPayload` class represents a data structure used for
 * specifying details of an Oracle database request.
 * It encapsulates various components related to Oracle database requests,
 * including the API URL, database connection details,
 * database username, database password, and an SQL command.
 *
 * This class is typically used to define and send Oracle database requests in
 * Java applications. The `apiURL` field specifies
 * the URL of the Oracle database API, while the `dbConnection` indicates the
 * database connection string. The `dbUserName` and
 * `dbPwd` fields contain the credentials for database authentication, and the
 * `sqlCmd` field stores the SQL command to be
 * executed.
 *
 * The `OracleDbRequestPayload` class is designed to facilitate the structuring
 * and processing of Oracle database requests,
 * making it suitable for use in scenarios where database interactions are a
 * part of the application's functionality.
 */
@Data
public class OracleDbRequestPayload {
    /**
     * The URL of the Oracle database API.
     */
    private String apiURL;

    /**
     * The connection string for the Oracle database.
     */
    private String dbConnection;

    /**
     * The username for database authentication.
     */
    private String dbUserName;

    /**
     * The password for database authentication.
     */
    private String dbPwd;

    /**
     * The SQL command to be executed in the database.
     */
    private String sqlCmd;
}
