package io.falu.identity.api.models

import com.google.gson.annotations.SerializedName

internal data class VerificationOptions(
    /**
     * Whether to allow uploads for documents, images and videos. This only applies to document related checks.
     */
    @SerializedName("allow_uploads")
    var allowUploads: Boolean? = null,

    /**
     * Options for the id number check.
     */
    @SerializedName("id_number")
    var idNumber: VerificationOptionsForIdNumber? = null,

    /**
     * Options for the document check.
     */
    var document: VerificationOptionsForDocument? = null,

    /**
     * Options for the selfie check.
     */
    var selfie: VerificationOptionsForSelfie? = null,

    /**
     * Options for the video check.
     */
    var video: VerificationOptionsForVideo? = null
)