package io.falu.identity.scan

import io.falu.identity.ai.DetectionOutput
import io.falu.identity.api.models.IdentityDocumentType
import io.falu.identity.navigation.IdentityDestination
import io.falu.identity.navigation.ScanCaptureDestination
import io.falu.identity.navigation.SelfieDestination
import org.joda.time.DateTime

/**
 * Possible states when scanning a document
 */
internal sealed class ScanDisposition(
    val type: DocumentScanType,
    val dispositionDetector: ScanDispositionDetector,
    val terminate: Boolean
) {
    abstract fun next(output: DetectionOutput): ScanDisposition

    /**
     *
     */
    internal enum class DocumentScanType {
        DL_BACK,
        DL_FRONT,
        ID_BACK,
        ID_FRONT,
        PASSPORT,
        SELFIE;

        val isFront: Boolean
            get() {
                return this == DL_FRONT || this == ID_FRONT || this == PASSPORT
            }

        val isBack: Boolean
            get() {
                return this == DL_BACK || this == ID_BACK
            }

        fun toScanDestination(): IdentityDestination {
            return when (this) {
                DL_BACK,
                DL_FRONT -> ScanCaptureDestination(
                    documentType = IdentityDocumentType.DRIVING_LICENSE,
                    popToCapture = true
                )

                ID_BACK,
                ID_FRONT -> ScanCaptureDestination(
                    documentType = IdentityDocumentType.IDENTITY_CARD,
                    popToCapture = true
                )

                PASSPORT -> ScanCaptureDestination(
                    documentType = IdentityDocumentType.PASSPORT,
                    popToCapture = true
                )

                SELFIE -> SelfieDestination()
            }
        }
    }

    /**
     *
     */
    internal class Start(type: DocumentScanType, detector: ScanDispositionDetector) :
        ScanDisposition(type, detector, false) {
        override fun next(output: DetectionOutput): ScanDisposition {
            return dispositionDetector.fromStart(this, output)
        }
    }

    /**
     *
     */
    internal class Detected(
        type: DocumentScanType,
        detector: ScanDispositionDetector,
        internal var reached: DateTime = DateTime.now()
    ) : ScanDisposition(type, detector, false) {
        override fun next(output: DetectionOutput) = dispositionDetector.fromDetected(this, output)
    }

    /**
     *
     */
    internal class Desired(
        type: DocumentScanType,
        detector: ScanDispositionDetector,
        val reached: DateTime = DateTime.now()
    ) : ScanDisposition(type, detector, false) {
        override fun next(output: DetectionOutput): ScanDisposition =
            dispositionDetector.fromDesired(this, output)
    }

    /**
     *
     */
    internal class Undesired(
        type: DocumentScanType,
        detector: ScanDispositionDetector,
        val reached: DateTime = DateTime.now()
    ) : ScanDisposition(type, detector, false) {
        override fun next(output: DetectionOutput): ScanDisposition =
            dispositionDetector.fromUndesired(this, output)
    }

    /**
     *
     */
    internal class Completed(type: DocumentScanType, detector: ScanDispositionDetector) :
        ScanDisposition(type, detector, true) {
        override fun next(output: DetectionOutput): ScanDisposition = this
    }

    /**
     *
     */
    internal class Timeout(type: DocumentScanType, detector: ScanDispositionDetector) :
        ScanDisposition(type, detector, true) {
        override fun next(output: DetectionOutput) = this
    }
}