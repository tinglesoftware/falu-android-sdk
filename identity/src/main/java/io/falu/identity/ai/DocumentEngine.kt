package io.falu.identity.ai

import android.graphics.Bitmap
import android.graphics.Rect
import android.util.Size
import io.falu.identity.analytics.ModelPerformanceMonitor
import io.falu.identity.utils.centerCrop
import io.falu.identity.utils.crop
import io.falu.identity.utils.maxAspectRatio
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.File
import kotlin.math.max
import kotlin.math.min

internal class DocumentEngine(
    model: File,
    private val threshold: Float,
    private val performanceMonitor: ModelPerformanceMonitor
) {
    private val interpreter = Interpreter(model)

    private val maxDetections = 10
    private val boundingBoxesTensorShape = intArrayOf(1, maxDetections, 4)
    private val scoresTensorShape = intArrayOf(1, maxDetections)
    private val classesTensorShape = intArrayOf(1, maxDetections)
    private val detectionsTensorShape = intArrayOf(1)

    fun analyze(bitmap: Bitmap): DetectionOutput {
        val preprocessingMonitor = performanceMonitor.monitorPreProcessing()
        interpreter.resetVariableTensors()

        val size = Size(bitmap.width, bitmap.height).maxAspectRatio(0.70f)
        val cropped = bitmap.centerCrop(size)

        var tensorImage = TensorImage(TENSOR_DATA_TYPE)
        tensorImage.load(cropped)

        // Preprocess: resize image to model input
        val processor = ImageProcessor.Builder()
            .add(
                ResizeOp(
                    IMAGE_HEIGHT,
                    IMAGE_WIDTH,
                    ResizeOp.ResizeMethod.BILINEAR
                )
            )
            .add(NormalizeOp(NORMALIZE_MEAN, NORMALIZE_STD)) // normalize to [0, 1)
            .build()
        tensorImage = processor.process(tensorImage)

        preprocessingMonitor.monitor(stats = "width: ${tensorImage.width}; height: ${tensorImage.height}")

        val inferenceMonitor = performanceMonitor.monitorInference()
        // run:- input: [1,320,320,1], output: (1,10), (1, 10, 4), (1,), (1,10)
        // The output is an array representing the probability scores of the various document types
        val documentOptionScoresBuffer =
            TensorBuffer.createFixedSize(scoresTensorShape, DataType.FLOAT32)
        val boundingBoxesBuffer =
            TensorBuffer.createFixedSize(boundingBoxesTensorShape, DataType.FLOAT32)
        val classesBuffer =
            TensorBuffer.createFixedSize(classesTensorShape, DataType.FLOAT32)
        val detectionsBuffer = TensorBuffer.createFixedSize(detectionsTensorShape, DataType.FLOAT32)

        interpreter.runForMultipleInputsOutputs(
            arrayOf(tensorImage.buffer),
            mapOf(
                0 to documentOptionScoresBuffer.buffer,
                1 to boundingBoxesBuffer.buffer,
                2 to detectionsBuffer.buffer,
                3 to classesBuffer.buffer
            )
        )
        inferenceMonitor.monitor()

        val scores = documentOptionScoresBuffer.floatArray
        val boxes = boundingBoxesBuffer.floatArray
        val classes = classesBuffer.floatArray

        // Process results:
        // Find the highest score and return the corresponding box and document option
        var bestIndex = 0
        var bestScore = Float.MIN_VALUE
        var bestOptionIndex = INVALID

        for (i in boxes.indices step 4) {
            val currentDocumentScore = scores[i / 4]
            val currentBestClass = classes[i / 4].toInt()

            if (bestScore < currentDocumentScore && currentDocumentScore > threshold) {
                bestScore = currentDocumentScore
                bestIndex = i
                bestOptionIndex = currentBestClass
            }
        }

        val bestOption = DOCUMENT_OPTIONS_MAP[bestOptionIndex] ?: DocumentOption.INVALID

        val bestBox = boxes.sliceArray(bestIndex..bestIndex + 3)

        val box = BoundingBox(
            left = bestBox[0], // x-min
            top = bestBox[1], // y-min
            width = bestBox[2] - bestBox[0], // x-max - x-min
            height = bestBox[3] - bestBox[1] // y-max - y-min
        )
        val rect = getRect(bestBox, cropped)

        return DocumentDetectionOutput(
            score = bestScore,
            option = bestOption,
            bitmap = cropped.crop(rect),
            box = box,
            rect = rect,
            scores = DOCUMENT_OPTIONS.map { scores[bestIndex] }.toMutableList()
        )
    }

    private fun getRect(coordinates: FloatArray, bitmap: Bitmap): Rect {
        val xMin = coordinates[0] * bitmap.height
        val yMin = coordinates[1] * bitmap.width
        val xMax = coordinates[2] * bitmap.height
        val yMax = coordinates[3] * bitmap.width

        return Rect(
            max(yMin.toInt(), 1),
            max(xMin.toInt(), 1),
            min(yMax.toInt(), bitmap.width),
            min(xMax.toInt(), bitmap.height)
        )
    }

    internal companion object {
        private val TENSOR_DATA_TYPE = DataType.FLOAT32

        private const val IMAGE_WIDTH = 320
        private const val IMAGE_HEIGHT = 320
        private const val NORMALIZE_MEAN = 0f
        private const val NORMALIZE_STD = 255f

        private const val INVALID = -1
        private const val PASSPORT = 0
        private const val DL_BACK = 1
        private const val DL_FRONT = 2
        private const val ID_BACK = 3
        private const val ID_FRONT = 4

        private val DOCUMENT_OPTIONS = listOf(
            DL_BACK,
            DL_FRONT,
            ID_BACK,
            ID_FRONT,
            PASSPORT
        )

        private val DOCUMENT_OPTIONS_MAP = mapOf(
            DL_BACK to DocumentOption.DL_BACK,
            DL_FRONT to DocumentOption.DL_FRONT,
            ID_BACK to DocumentOption.ID_BACK,
            ID_FRONT to DocumentOption.ID_FRONT,
            PASSPORT to DocumentOption.PASSPORT
        )
    }
}