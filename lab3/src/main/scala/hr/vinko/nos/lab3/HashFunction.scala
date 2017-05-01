package hr.vinko.nos.lab3

import java.security.MessageDigest

trait HashFunction {
  val name: String
  val keySize: Int
  def hash(text: String): String
}

class SHA256 extends HashFunction {
  override val name = "SHA-256"
  val md = MessageDigest.getInstance(name)

  override val keySize = md.getDigestLength
  
  def hash(text: String): String = {
    CryptoUtil.byteToHex(md.digest(text.getBytes("UTF-8")))
  }
}