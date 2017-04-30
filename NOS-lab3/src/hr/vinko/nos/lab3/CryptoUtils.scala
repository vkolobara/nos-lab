package hr.vinko.nos.lab3

import javax.xml.bind.DatatypeConverter

object CryptoUtils {
  def byteToHex(bytes: Array[Byte]): String = {
    DatatypeConverter.printHexBinary(bytes)
  }
  
  def hexToByte(text: String): Array[Byte] = {
    DatatypeConverter.parseHexBinary(text)
  }
}