package hr.vinko.nos.lab3

import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import java.util.Arrays
import java.math.BigInteger
import java.security.KeyFactory
import java.security.spec.RSAPublicKeySpec
import java.security.spec.RSAPrivateKeySpec

trait CryptoAlgorithm {
  def encrypt(text: String): String
  def decrypt(bytes: String): String
}

class AESCrypto(key: String) extends CryptoAlgorithm {
  
  val secretKey = new SecretKeySpec(key.getBytes("UTF-8"), "AES")

  def decrypt(bytes: String): String = {
    val cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING")
    cipher.init(Cipher.DECRYPT_MODE, secretKey)
    new String(cipher.doFinal(CryptoUtils.hexToByte(bytes)), "UTF-8")
  }

  def encrypt(text: String): String = {
    val cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING")
    cipher.init(Cipher.ENCRYPT_MODE, secretKey)
    CryptoUtils.byteToHex(cipher.doFinal(text.getBytes("UTF-8")))
  }
}

class RSACrypto(n: BigInteger, e: BigInteger, d: BigInteger) extends CryptoAlgorithm {

  val keyFactory = KeyFactory.getInstance("RSA")

  val publicKey = keyFactory.generatePublic(new RSAPublicKeySpec(n, e))
  val privateKey = keyFactory.generatePrivate(new RSAPrivateKeySpec(n, d))

  def encrypt(text: String): String = {
    val cipher = Cipher.getInstance("RSA/None/NoPadding", "BC")
    cipher.init(Cipher.ENCRYPT_MODE, publicKey)
    CryptoUtils.byteToHex(cipher.doFinal(text.getBytes("UTF-8")))
  }

  def decrypt(bytes: String): String = {
    val cipher = Cipher.getInstance("RSA/None/NoPadding", "BC")
    cipher.init(Cipher.DECRYPT_MODE, privateKey)
    new String(cipher.doFinal(CryptoUtils.hexToByte(bytes)), "UTF-8")
  }
}