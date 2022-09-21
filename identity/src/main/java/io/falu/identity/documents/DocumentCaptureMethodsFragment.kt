package io.falu.identity.documents

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import io.falu.identity.R
import io.falu.identity.api.models.IdentityDocumentType
import io.falu.identity.capture.AbstractCaptureFragment.Companion.getIdentityDocumentName
import io.falu.identity.databinding.FragmentDocumentCaptureMethodsBinding

class DocumentCaptureMethodsFragment : Fragment() {
    private var _binding: FragmentDocumentCaptureMethodsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDocumentCaptureMethodsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val identityDocumentType =
            requireArguments().getSerializable(DocumentSelectionFragment.KEY_IDENTITY_DOCUMENT_TYPE) as? IdentityDocumentType

        binding.tvDocumentCaptureMethod.text =
            getString(R.string.document_capture_method_subtitle, identityDocumentType?.getIdentityDocumentName(requireContext() ))

        var captureDestination: Int = 0

        binding.groupCaptureMethods.setOnCheckedStateChangeListener { group, _ ->
            when (group.checkedChipId) {
                R.id.chip_capture_method_scan -> captureDestination = identityDocumentType!!.toUploadDestination()
                R.id.chip_capture_method_photo -> captureDestination = identityDocumentType!!.toPhotoUploadDestination()
                R.id.chip_capture_method_upload -> captureDestination = identityDocumentType!!.toUploadDestination()
            }
        }

        binding.buttonContinue.setOnClickListener {
            val bundle =
                bundleOf(DocumentSelectionFragment.KEY_IDENTITY_DOCUMENT_TYPE to identityDocumentType)
            findNavController().navigate(captureDestination, bundle)
        }
    }

    internal companion object {
        @IdRes
        private fun IdentityDocumentType.toUploadDestination() =
            when (this) {
                IdentityDocumentType.IDENTITY_CARD -> R.id.action_fragment_document_capture_methods_to_fragment_document_upload
                IdentityDocumentType.PASSPORT -> R.id.action_fragment_document_capture_methods_to_fragment_document_upload
                IdentityDocumentType.DRIVING_LICENSE -> R.id.action_fragment_document_capture_methods_to_fragment_document_upload
            }

        @IdRes
        private fun IdentityDocumentType.toPhotoUploadDestination() =
            when (this) {
                IdentityDocumentType.IDENTITY_CARD -> R.id.action_fragment_document_capture_methods_to_fragment_photo_upload
                IdentityDocumentType.PASSPORT -> R.id.action_fragment_document_capture_methods_to_fragment_photo_upload
                IdentityDocumentType.DRIVING_LICENSE -> R.id.action_fragment_document_capture_methods_to_fragment_photo_upload
            }
    }
}