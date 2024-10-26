package io.falu.identity.navigation

import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavType
import androidx.navigation.navArgument
import io.falu.identity.api.models.IdentityDocumentType

internal data class UploadCaptureDestination(val documentType: IdentityDocumentType) : IdentityDestination() {
    override val workflowRoute: WorkflowRoute
        get() = ROUTE

    override val routeWithArgs: String = workflowRoute.withParameters(
        KEY_DOCUMENT_TYPE to documentType
    )

    internal companion object {
        const val UPLOAD_CAPTURE = "document_capture_method_upload"
        internal const val KEY_DOCUMENT_TYPE = "document_type"

        internal fun identityDocumentType(entry: NavBackStackEntry) =
            entry.getSerializable<IdentityDocumentType>(KEY_DOCUMENT_TYPE)!!

        val ROUTE = object : WorkflowRoute() {
            override val base: String = UPLOAD_CAPTURE
            override val arguments = listOf(
                navArgument(KEY_DOCUMENT_TYPE) {
                    type = NavType.EnumType(IdentityDocumentType::class.java)
                }
            )
        }
    }
}