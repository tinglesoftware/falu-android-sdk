package io.falu.identity.api.models

internal data class Verification(
    /**
     * Unique identifier of the identity verification
     */
    var id: String,
    /**
     * Contains `true` is verification is in live mode and `false` if it isn't
     */
    var live: Boolean,
    var workspace: WorkspaceInfo,
    var business: BusinessInfo,
    var type: VerificationType,
    var url: String,
)