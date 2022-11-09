package io.falu.identity

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.ViewModelProvider
import io.falu.identity.confirmation.ConfirmationFragment
import io.falu.identity.documents.DocumentCaptureMethodsFragment
import io.falu.identity.documents.DocumentSelectionFragment
import io.falu.identity.support.SupportFragment
import io.falu.identity.welcome.WelcomeFragment

internal class IdentityVerificationFragmentFactory(
    private val callback: IdentityVerificationResultCallback,
    private val factory: ViewModelProvider.Factory,
) :
    FragmentFactory() {

    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
        return when (className) {
            WelcomeFragment::class.java.name -> WelcomeFragment(factory, callback)
            DocumentSelectionFragment::class.java.name -> DocumentSelectionFragment(factory)
            DocumentCaptureMethodsFragment::class.java.name ->
                DocumentCaptureMethodsFragment(factory)
            ConfirmationFragment::class.java.name -> ConfirmationFragment(callback)
            SupportFragment::class.java.name -> SupportFragment(factory)
            else -> super.instantiate(classLoader, className)
        }
    }
}