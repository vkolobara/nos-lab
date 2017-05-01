package hr.vinko.nos.lab3

import javax.xml.bind.DatatypeConverter

object CryptoUtil {
  def byteToHex(bytes: Array[Byte]): String = {
    DatatypeConverter.printHexBinary(bytes).toLowerCase
  }
  
  def hexToByte(text: String): Array[Byte] = {
    DatatypeConverter.parseHexBinary(text)
  }
}