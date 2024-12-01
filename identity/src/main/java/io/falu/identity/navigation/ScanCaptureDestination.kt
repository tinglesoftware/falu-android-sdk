package io.falu.identity.navigation

import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavType
import androidx.navigation.navArgument
import io.falu.identity.api.models.IdentityDocumentType

internal data class ScanCaptureDestination(
    val documentType: IdentityDocumentType,
    val popToCapture: Boolean = false
) : IdentityDestination(
    popUpToParam = if (popToCapture) {
        PopUpTo(
            route = DocumentCaptureDestination.ROUTE.route,
            inclusive = false
        )
    } else {
        null
    }
) {
    override val workflowRoute: WorkflowRoute
        get() = ROUTE

    override val routeWithArgs: String = workflowRoute.withParameters(
        KEY_DOCUMENT_TYPE to documentType
    )

    internal companion object {
        const val SCAN_CAPTURE = "document_capture_method_scan"
        internal const val KEY_DOCUMENT_TYPE = "document_type"

        internal fun identityDocumentType(entry: NavBackStackEntry) =
            entry.getSerializable<IdentityDocumentType>(KEY_DOCUMENT_TYPE)!!

        val ROUTE = object : WorkflowRoute() {
            override val base: String = SCAN_CAPTURE
            override val arguments = listOf(
                navArgument(KEY_DOCUMENT_TYPE) {
                    type = NavType.EnumType(IdentityDocumentType::class.java)
                }
            )
        }
    }
}