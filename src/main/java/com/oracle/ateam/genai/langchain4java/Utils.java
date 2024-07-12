/*******************************************************************************
 * Oracle OCI Generative AI LangChain For Java version 1.0.
 *
 * Copyright (c)  2024,  Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 ******************************************************************************/
package com.oracle.ateam.genai.langchain4java;

import java.util.EnumSet;
import java.util.List;

import com.oracle.ateam.genai.langchain4java.model.Property;

/**
 * The `Utils` class provides utility methods for common operations such as
 * merging tokens with templates, replacing tokens with data, and checking if a
 * value exists in an enumeration.
 * These methods are designed to simplify common tasks in Java applications and
 * can be used in various scenarios to enhance functionality and maintain code
 * readability.
 */
public class Utils {
    /**
     * Merges a list of properties with a prompt template by replacing placeholders
     * in the template with their corresponding values.
     *
     * @param properties     The list of properties to merge into the template.
     * @param promptTemplate The template containing placeholders (e.g., "{key}") to
     *                       be replaced with values.
     * @return The merged string, with placeholders replaced by their corresponding
     *         values.
     */
    public static String mergePromptwithToken(List<Property> properties, String promptTemplate) {
        String output = promptTemplate;

        for (Property entry : properties) {
            String placeholder = "{" + entry.getKey() + "}";
            String value = entry.getValue().toString();
            output = output.replace(placeholder, value);
        }
        return output;
    }

    /**
     * Replaces a specific token in a template with the provided data.
     *
     * @param data           The data to replace the token with.
     * @param outputVariable The token (placeholder) to be replaced in the template.
     * @param template       The template containing the token to be replaced.
     * @return The template with the token replaced by the data.
     */
    public static String replaceTokenWithData(String data, String outputVariable, String template) {
        String output = null;
        String placeholder = "{" + outputVariable + "}";
        output = template.replace(placeholder, data);
        return output;
    }

    /**
     * Checks if a specified value exists in the given enumeration.
     *
     * @param _enumClass The class representing the enumeration.
     * @param value      The value to check for in the enumeration.
     * @param <E>        The type of the enumeration.
     * @return `true` if the value is found in the enumeration; otherwise, `false`.
     */
    public static <E extends Enum<E>> boolean contains(Class<E> _enumClass,
            String value) {
        try {
            return EnumSet.allOf(_enumClass)
                    .contains(Enum.valueOf(_enumClass, value));
        } catch (Exception e) {
            return false;
        }
    }

}
