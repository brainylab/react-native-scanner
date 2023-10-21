package com.brainylab.reactnativescanner

import com.google.mlkit.vision.barcode.common.Barcode

object BarcodeFormatUtils {
  fun getBarcodeFormat(codeType: String): Int {
    return when (codeType) {
      "all_formats" -> Barcode.FORMAT_ALL_FORMATS
      "code-128" -> Barcode.FORMAT_CODE_128
      "code-39" -> Barcode.FORMAT_CODE_39
      "code-93" -> Barcode.FORMAT_CODE_93
      "codabar" -> Barcode.FORMAT_CODABAR
      "ean-13" -> Barcode.FORMAT_EAN_13
      "ean-8" -> Barcode.FORMAT_EAN_8
      "itf" -> Barcode.FORMAT_ITF
      "upc-e" -> Barcode.FORMAT_UPC_E
      "qr-code" -> Barcode.FORMAT_QR_CODE
      "pdf-417" -> Barcode.FORMAT_PDF417
      "aztec" -> Barcode.FORMAT_AZTEC
      "data-matrix" -> Barcode.FORMAT_DATA_MATRIX
      else -> throw IllegalArgumentException("Invalid code type: $codeType")
    }
  }
}
