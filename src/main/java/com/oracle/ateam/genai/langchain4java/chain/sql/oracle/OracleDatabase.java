/*******************************************************************************
 * Oracle OCI Generative AI LangChain For Java version 1.0.
 *
 * Copyright (c)  2024,  Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 ******************************************************************************/
package com.oracle.ateam.genai.langchain4java.chain.sql.oracle;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The `OracleDatabase` class serves as a SQLAlchemy wrapper around an Oracle database. It provides methods for interacting with the database, executing SQL queries, and retrieving table and column information. This class simplifies database interactions by encapsulating common operations within a convenient API.
 */
@Slf4j
public class OracleDatabase {
    private final Connection connection;

    private final List<String> includeTables;

    private final List<String> ignoreTables;

    private final int sampleRowsInTableInfo;

    private boolean indexesInTableInfo;

    public OracleDatabase(String url, String username, String password) {
        this(url, username, password, null, null, 3, false);
    }

    @SneakyThrows(SQLException.class)
    public OracleDatabase(String url, String username, String password, List<String> includeTables,
            List<String> ignoreTables, int sampleRowsInTableInfo, boolean indexesInTableInfo) {
        if (CollectionUtils.isNotEmpty(includeTables) && CollectionUtils.isNotEmpty(ignoreTables)) {
            throw new IllegalArgumentException("Cannot specify both includeTables and ignoreTables");
        }
        this.connection = DriverManager.getConnection(url, username, password);
        this.includeTables = includeTables;
        this.ignoreTables = ignoreTables;
        this.sampleRowsInTableInfo = sampleRowsInTableInfo;
        this.indexesInTableInfo = indexesInTableInfo;
    }

    public static OracleDatabase fromUri(String url, String username, String password) {
        return new OracleDatabase(url, username, password);
    }

    /**
     * Dialect will convert to lowercase
     */
    @SneakyThrows(SQLException.class)
    public String getDialect() {
        return connection.getMetaData()
                .getDatabaseProductName()
                .toLowerCase();
    }

    /**
     * Get names of tables available.
     */
    public List<String> getUsableTableNames() {
        log.info("Get tables name...");
        if (CollectionUtils.isNotEmpty(includeTables)) {
            return includeTables;
        }
        List<String> allTables = getAllTables();

        if (CollectionUtils.isNotEmpty(ignoreTables)) {
            allTables.removeAll(ignoreTables);
        }
        return allTables;
    }

    @SneakyThrows(SQLException.class)
    private List<String> getAllTables() {
        log.info("Get all tables...");
        List<String> allTables = new ArrayList<>();
        DatabaseMetaData metaData = connection.getMetaData();
        try (ResultSet resultSet = metaData.getTables(connection.getCatalog(), connection.getSchema(), "%",
                new String[] { "TABLE" })) {
            while (resultSet.next()) {
                allTables.add(resultSet.getString("TABLE_NAME"));
            }
        }
        return allTables;
    }

    /**
     * Get information about specified tables.
     * <p>
     * Follows best practices as specified in:
     * <a href="https://arxiv.org/abs/2204.00498"> Rajkumar et al, 2022</a>
     * <p>
     * If `sample_rows_in_table_info`, the specified number of sample rows will be
     * appended to each table description.
     * This can increase performance as demonstrated in the paper.
     */
    public String getTableInfo(List<String> tableNames) {
        log.info("Get information about specified tables...");
        List<String> allTableNames = getUsableTableNames();

        if (tableNames != null) {
            List<String> missingTables = new ArrayList<>(tableNames);
            missingTables.removeAll(allTableNames);
            if (!missingTables.isEmpty()) {
                throw new IllegalArgumentException("tableNames " + missingTables + " not found in database");
            }
            allTableNames = tableNames;
        }

        List<String> tables = new ArrayList<>();
        for (String tableName : allTableNames) {
            String createTable = getTableDdl(tableName);
            String tableInfo = createTable.replaceAll("\\n+$", "");

            boolean hasExtraInfo = indexesInTableInfo || sampleRowsInTableInfo > 0;
            if (hasExtraInfo) {
                tableInfo += "\n\n/*";
            }
            if (indexesInTableInfo) {
                tableInfo += "\n" + getTableIndexes(tableName) + "\n";
            }
            if (sampleRowsInTableInfo > 0) {
                tableInfo += "\n" + getSampleRows(tableName) + "\n";
            }
            if (hasExtraInfo) {
                tableInfo += "*/";
            }
            tables.add(tableInfo);
        }
        return String.join("\n\n", tables);
    }

    @SneakyThrows(SQLException.class)
    public String getTableDdl(String tableName) {
        log.info("Get tables DDL...");
        StringBuilder builder = new StringBuilder();
        DatabaseMetaData metaData = connection.getMetaData();
        ResultSet resultSet = metaData.getTables(connection.getCatalog(), connection.getSchema(), tableName,
                new String[] { "TABLE" });

        while (resultSet.next()) {
            ResultSet columnsResultSet = metaData.getColumns(connection.getCatalog(), connection.getSchema(), tableName,
                    "%");
            builder.append("\nCREATE TABLE ").append(tableName).append(" (");

            while (columnsResultSet.next()) {
                String columnName = columnsResultSet.getString("COLUMN_NAME");
                String columnType = columnsResultSet.getString("TYPE_NAME");
                int columnSize = columnsResultSet.getInt("COLUMN_SIZE");
                int decimalDigits = columnsResultSet.getInt("DECIMAL_DIGITS");
                boolean isNullable = columnsResultSet.getBoolean("NULLABLE");
                String defaultValue = columnsResultSet.getString("COLUMN_DEF");
                String columnComment = columnsResultSet.getString("REMARKS");

                builder.append("\n\t").append(columnName).append(" ").append(columnType);
                if (columnSize > 0) {
                    builder.append("(").append(columnSize);
                    if (decimalDigits > 0) {
                        builder.append(",").append(decimalDigits);
                    }
                    builder.append(")");
                }
                if (!isNullable) {
                    builder.append(" NOT NULL");
                }
                if (defaultValue != null) {
                    builder.append(" DEFAULT ").append(defaultValue);
                }
                if (StringUtils.isNotEmpty(columnComment)) {
                    builder.append(" COMMENT '").append(columnComment).append("'");
                }
                builder.append(",");
            }
            // Remove the last comma
            if (builder.charAt(builder.length() - 1) == ',') {
                builder.deleteCharAt(builder.length() - 1);
            }
            String tableComment = resultSet.getString("REMARKS");
            if (StringUtils.isNotEmpty(tableComment)) {
                builder.append("\n) COMMENT '").append(tableComment).append("'\n\n");
            } else {
                builder.append("\n)\n\n");
            }
        }
        return builder.toString();
    }

    public String getTableIndexes(String tableName) {
        return "";
    }

    public String getSampleRows(String tableName) {
        log.info("Get sample data for a specified table...");
        // Build the select command
        String command = "SELECT * FROM " + tableName + " OFFSET 0 ROWS FETCH NEXT " + sampleRowsInTableInfo
                + " ROWS ONLY ";
        String result = run(command, true);
        // Save the sample rows in string format
        return String.format("%d rows from %s table:\n%s", sampleRowsInTableInfo, tableName, result);
    }

    /**
     * Execute a SQL command and return a string representing the results.
     *
     * <p>
     * If the statement returns rows, a string of the results is returned.
     * <p>
     * If the statement returns no rows, an empty string is returned.
     */
    @SneakyThrows(SQLException.class)
    public String run(String command, boolean includeColumnName) {
        log.info("Run an sql command...");
        try (Statement stmt = connection.createStatement()) {
            if (stmt.execute(command)) {
                ResultSet resultSet = stmt.getResultSet();
                ResultSetMetaData metaData = resultSet.getMetaData();
                int columnCount = resultSet.getMetaData().getColumnCount();
                String result = "";
                if (includeColumnName) {
                    List<String> columns = new ArrayList<>();
                    for (int i = 1; i <= columnCount; i++) {
                        columns.add(metaData.getColumnName(i));
                    }
                    String columnsStr = String.join("\t", columns);
                    result += columnsStr + "\n";
                }

                List<List<String>> data = new ArrayList<>();
                while (resultSet.next()) {
                    List<String> row = new ArrayList<>();
                    for (int i = 1; i <= columnCount; i++) {
                        row.add(resultSet.getString(i));
                    }
                    data.add(row);
                }
                String rowsStr = data.stream()
                        .map(row -> String.join("\t", row))
                        .collect(Collectors.joining("\n"));
                result += rowsStr;
                return result;
            } else {
                int updateCount = stmt.getUpdateCount();
                return "Update Count: " + updateCount;
            }
        }
    }

    @SneakyThrows(SQLException.class)
    public void close() {
        if (connection != null) {
            connection.close();
        }
    }
}
