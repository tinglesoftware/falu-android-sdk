package io.falu.identity

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.ViewModelProvider
import io.falu.identity.confirmation.ConfirmationFragment
import io.falu.identity.welcome.WelcomeFragment

internal class IdentityVerificationFragmentFactory(
    private val callback: IdentityVerificationResultCallback,
    internal val factory: ViewModelProvider.Factory,
) :
    FragmentFactory() {

    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
        return when (className) {
            WelcomeFragment::class.java.name -> WelcomeFragment(factory, callback)
            ConfirmationFragment::class.java.name -> ConfirmationFragment(callback)
            else -> super.instantiate(classLoader, className)
        }
    }
}