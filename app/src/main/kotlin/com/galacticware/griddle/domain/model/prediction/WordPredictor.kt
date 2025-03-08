/*
package com.galacticware.griddle.domain.prediction
import android.app.Activity
import android.util.Log
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.nnapi.NnApiDelegate
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import org.tensorflow.lite.nnapi.NnApiDelegateImpl

class WordPredictor(activity: Activity) {
    private val tfLite = Interpreter(loadModelFile(activity))
    private val NUM_BYTES_PER_DATA_TYPE = mapOf(
        Float to 4,
        Int to 4,
        Long to 8,
        Double to 8,
        Byte to 1,
        Short to 2
    ).also {

    }

    fun tokenize(text: String): List<String> {
        return text.split(" ")
    }
    fun predictWord(input: String, numPredictions: Int): List<String> {
        return listOf()
        // Load the model (assuming loadModelFile is defined elsewhere)

        // Preprocess the input
        // Use a proper tokenizer (e.g., TensorFlow Text)
        val tokenizer =
        val sequences = tokenizer.texts_to_sequences(input.split(" ")) // Assuming texts_to_sequences is defined elsewhere
        val paddedSequences = padSequences(sequences, maxlen = 384) // Assuming maxlen is 384

        // Create input tensor
        val inputTensor = paddedSequences.toFloatArray() // Assuming input expects float32

        // Get output details (assuming output is probabilities)
        val outputDetails = tfLite.getOutputTensorDetails()
        val outputDataType = outputDetails[0].dataType
        val outputSize = outputDetails[0].allocationSize / NUM_BYTES_PER_DATA_TYPE[outputDataType]

        // Allocate output tensor based on output details
        val outputTensor = ByteBuffer.allocateDirect(outputSize * NUM_BYTES_PER_DATA_TYPE[outputDataType])
        outputTensor.order(ByteOrder.nativeOrder())

        // Run inference
        tfLite.run(inputTensor, outputTensor)

        // Postprocess output
        val outputProbabilities = when (outputDataType) {
            Float -> outputTensor.asFloatBuffer().array()
            else -> throw IllegalArgumentException("Unsupported output data type: $outputDataType")
        }

        val topIndices = outputProbabilities.indices.sortedByDescending { outputProbabilities[it] }.take(numPredictions)
        val predictedWords = topIndices.map { tokenizer.index_word[it] ?: "Unknown" }

        return predictedWords
    }


    fun padSequences(sequences: List<List<Int>>, maxlen: Int): List<List<Int>> {
        return sequences.map { sequence ->
            val paddedSequence = sequence.toMutableList()
            while (paddedSequence.size < maxlen) {
                paddedSequence.add(0) // Replace 0 with your desired padding value
            }
            paddedSequence
        }
    }

    companion object {
        private const val MODEL_PATH = "model.tflite"
        fun loadModelFile(activity: Activity) : MappedByteBuffer{
            val fileDescriptor = activity.assets.openFd(MODEL_PATH)
            val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
            val fileChannel = inputStream.channel
            val startOffset = fileDescriptor.startOffset
            val declaredLength = fileDescriptor.declaredLength
            val map = fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
            Log.d("WordPredictor", getModelInputOutputShapes(map)
                .let { "Input shape: ${it.first.joinToString(",")} Output shape: ${it.second.joinToString(",")}" })
            return map
        }

        fun getModelInputOutputShapes(model: MappedByteBuffer): Pair<IntArray, IntArray> {
            val interpreter = Interpreter(model)
            val inputShape = interpreter.getInputTensor(0).shape()
            val outputShape = interpreter.getOutputTensor(0).shape()
            interpreter.close()
            return Pair(inputShape, outputShape)
        }
    }
}*/
