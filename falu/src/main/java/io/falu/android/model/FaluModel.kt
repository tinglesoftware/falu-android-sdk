package io.falu.android.model

open class FaluModel {
    /**
     * An optional arbitrary string attached to the object.
     * Mainly used to describe the object and often useful for displaying to users.
     */
    var description: String? = null

    /**
     * Set of key-value pairs that you can attach to an object.
     * This can be useful for storing additional information about the object in a structured format.
     * The key can only contain alphanumeric, and ‘-’, ‘_’ characters, and the string has to start with a letter.
     */
    var metadata: Any? = null

    /**
     * Set of values that you can attach to an object. This can be useful for searching.
     */
    var tags: Array<String>? = null
}