/*******************************************************************************
 * Oracle OCI Generative AI LangChain For Java version 1.0.
 *
 * Copyright (c)  2024,  Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 ******************************************************************************/
package com.oracle.ateam.genai.langchain4java.model;

import lombok.Data;

/**
 * The `Property` class represents a key-value pair commonly used for storing
 * and passing data in various contexts.
 * It encapsulates a simple property with a key and a corresponding value.
 *
 * This class is typically used to define and work with individual properties in
 * Java applications. The `key` field
 * represents the identifier or name of the property, and the `value` field
 * holds the associated data.
 *
 * The `Property` class is versatile and can be employed in a wide range of
 * scenarios where key-value pairs are utilized
 * to store and manage data.
 */
@Data
public class Property {
    /**
     * The key or identifier of the property.
     */
    private String key;

    /**
     * The value associated with the key.
     */
    private String value;
}
