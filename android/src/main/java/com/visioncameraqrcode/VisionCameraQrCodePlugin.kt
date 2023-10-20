package com.visioncameraqrcode

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.common.internal.ImageConvertUtils
import com.mrousavy.camera.frameprocessor.Frame
import com.mrousavy.camera.frameprocessor.FrameProcessorPlugin
import com.mrousavy.camera.types.Orientation
import com.visioncameraqrcode.BarcodeConverter.convertToArray
import com.visioncameraqrcode.BarcodeConverter.convertToMap

class VisionCameraQrCodePlugin(options: Map<String, Any>?): FrameProcessorPlugin(options) {

  private var barcodeScanner: BarcodeScanner? = null
  private var barcodeScannerFormatsBitmap = -1

  private val barcodeFormats: Set<Int> = HashSet(
    mutableListOf(
      Barcode.FORMAT_UNKNOWN,
      Barcode.FORMAT_ALL_FORMATS,
      Barcode.FORMAT_CODE_128,
      Barcode.FORMAT_CODE_39,
      Barcode.FORMAT_CODE_93,
      Barcode.FORMAT_CODABAR,
      Barcode.FORMAT_DATA_MATRIX,
      Barcode.FORMAT_EAN_13,
      Barcode.FORMAT_EAN_8,
      Barcode.FORMAT_ITF,
      Barcode.FORMAT_QR_CODE,
      Barcode.FORMAT_UPC_A,
      Barcode.FORMAT_UPC_E,
      Barcode.FORMAT_PDF417,
      Barcode.FORMAT_AZTEC
    )
  )

  override fun callback(frame: Frame, params: Map<String, Any>?): Any? {
    createBarcodeInstance(params)

    val mediaImage = frame.image
    if (mediaImage != null) {
      val tasks: ArrayList<Task<List<Barcode>>> = ArrayList<Task<List<Barcode>>>()
      val image = InputImage.fromMediaImage(
        mediaImage,
        Orientation.Companion.fromUnionValue(frame.orientation)?.toDegrees() ?: 0
      )
      if (params != null && params.containsKey("checkInverted")) {
        if (params["checkInverted"] as Boolean) {
          try {
            val bitmap: Bitmap = ImageConvertUtils.getInstance().getUpRightBitmap(image)
            val invertedBitmap: Bitmap? = invertBitmap(bitmap)
            if (invertedBitmap != null) {
              val invertedImage = InputImage.fromBitmap(invertedBitmap, 0)
              tasks.add(barcodeScanner!!.process(invertedImage))
            }
          } catch (e: Exception) {
            e.printStackTrace()
            return null
          }
        }
      }
      tasks.add(barcodeScanner!!.process(image))
      try {
        val barcodes = ArrayList<Barcode>()
        for (task in tasks) {
          barcodes.addAll(Tasks.await(task))
        }
        val array: MutableList<Any> = ArrayList()
        for (barcode in barcodes) {
          array.add(convertBarcode(barcode))
        }
        return array
      } catch (e: Exception) {
        e.printStackTrace()
      }
    }
    return null
  }

  private fun createBarcodeInstance(params: Map<String, Any>?) {
    if (params != null && params.containsKey("types") && params["types"] is ArrayList<*>) {
      val rawFormats = params["types"] as ArrayList<Double>?
      var formatsBitmap = 0
      var formatsIndex = 0
      val formatsSize = rawFormats!!.size
      val formats = IntArray(formatsSize)
      for (i in 0 until formatsSize) {
        val format = rawFormats[i].toInt()
        if (barcodeFormats.contains(format)) {
          formats[formatsIndex] = format
          formatsIndex++
          formatsBitmap = formatsBitmap or format
        }
      }
      if (formatsIndex == 0) {
        throw ArrayIndexOutOfBoundsException("Need to provide at least one valid Barcode format")
      }
      if (barcodeScanner == null || formatsBitmap != barcodeScannerFormatsBitmap) {
        barcodeScanner = BarcodeScanning.getClient(
          BarcodeScannerOptions.Builder()
            .setBarcodeFormats(
              formats[0],
              *formats.copyOfRange(1, formatsIndex)
            )
            .build()
        )
        barcodeScannerFormatsBitmap = formatsBitmap
      }
    } else {
      throw IllegalArgumentException("Second parameter must be an Array")
    }
  }

  private fun convertContent(barcode: Barcode): Map<String, Any?> {
    val map: MutableMap<String, Any?> = HashMap()
    val type = barcode.valueType
    map["type"] = type
    when (type) {
      Barcode.TYPE_UNKNOWN, Barcode.TYPE_ISBN, Barcode.TYPE_TEXT -> map["data"] = barcode.rawValue
      Barcode.TYPE_CONTACT_INFO -> map["data"] = barcode.contactInfo?.let { convertToMap(it) }
      Barcode.TYPE_EMAIL -> map["data"] = barcode.email?.let { convertToMap(it) }
      Barcode.TYPE_PHONE -> map["data"] = barcode.phone?.let { convertToMap(it) }
      Barcode.TYPE_SMS -> map["data"] = barcode.sms?.let { convertToMap(it) }
      Barcode.TYPE_URL -> map["data"] = barcode.url?.let { convertToMap(it) }
      Barcode.TYPE_WIFI -> map["data"] = barcode.wifi?.let { convertToMap(it) }
      Barcode.TYPE_GEO -> map["data"] = barcode.geoPoint?.let { convertToMap(it) }
      Barcode.TYPE_CALENDAR_EVENT -> map["data"] = barcode.calendarEvent?.let { convertToMap(it) }
      Barcode.TYPE_DRIVER_LICENSE -> map["data"] = barcode.driverLicense?.let { convertToMap(it) }
    }
    return map
  }

  private fun convertBarcode(barcode: Barcode): Map<String, Any> {
    val map: MutableMap<String, Any> = HashMap()
    val boundingBox = barcode.boundingBox
    if (boundingBox != null) {
      map["boundingBox"] = convertToMap(boundingBox)
    }
    val cornerPoints = barcode.cornerPoints
    if (cornerPoints != null) {
      map["cornerPoints"] = convertToArray(cornerPoints)
    }
    val displayValue = barcode.displayValue
    if (displayValue != null) {
      map["displayValue"] = displayValue
    }
    val rawValue = barcode.rawValue
    if (rawValue != null) {
      map["rawValue"] = rawValue
    }
    map["content"] = convertContent(barcode)
    map["format"] = barcode.format
    return map
  }

  // Bitmap Inversion https://gist.github.com/moneytoo/87e3772c821cb1e86415
  private fun invertBitmap(src: Bitmap): Bitmap? {
    val height = src.height
    val width = src.width
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    val paint = Paint()
    val matrixGrayscale = ColorMatrix()
    matrixGrayscale.setSaturation(0f)
    val matrixInvert = ColorMatrix()
    matrixInvert.set(
      floatArrayOf(
        -1.0f, 0.0f, 0.0f, 0.0f, 255.0f,
        0.0f, -1.0f, 0.0f, 0.0f, 255.0f,
        0.0f, 0.0f, -1.0f, 0.0f, 255.0f,
        0.0f, 0.0f, 0.0f, 1.0f, 0.0f
      )
    )
    matrixInvert.preConcat(matrixGrayscale)
    val filter = ColorMatrixColorFilter(matrixInvert)
    paint.colorFilter = filter
    canvas.drawBitmap(src, 0f, 0f, paint)
    return bitmap
  }
}
