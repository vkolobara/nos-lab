package hr.vinko.nos.lab3

import java.security.MessageDigest

trait HashFunction {
  def hash(text: String): String
}

class SHA256 extends HashFunction {

  val md = MessageDigest.getInstance("SHA-256")

  def hash(text: String): String = {
    CryptoUtils.byteToHex(md.digest(text.getBytes("UTF-8")))
  }
}