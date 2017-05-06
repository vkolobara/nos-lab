package hr.vinko.nos.lab3

import javax.xml.bind.DatatypeConverter

object CryptoUtil {
  def byteToHex(bytes: Array[Byte]): String = {
    bytes.map("%02x" format _).mkString
  }

  def hexToByte(text: String): Array[Byte] = {
    text.sliding(2, 2).toArray.map(Integer.parseInt(_, 16).toByte)
  }
}