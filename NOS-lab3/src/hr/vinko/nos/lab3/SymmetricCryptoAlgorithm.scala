package hr.vinko.nos.lab3

import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import java.util.Arrays
import java.math.BigInteger
import java.security.KeyFactory
import java.security.spec.RSAPublicKeySpec
import java.security.spec.RSAPrivateKeySpec
import javax.crypto.KeyGenerator
import java.security.KeyPairGenerator
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.util.Base64
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import scala.io.Source
import hr.vinko.nos.lab3.FieldUtil.writeToFile
import java.security.PublicKey
import java.security.PrivateKey

trait SymmetricCryptoAlgorithm {

  val name: String
  val keySize: Int
  val secretKey: SecretKeySpec

  def encrypt(text: String): Array[Byte]
  def decrypt(bytes: Array[Byte]): String

  def encryptFile(inPath: String, outPath: String) = {
    val inFile = new File(inPath)

    val file = new String(Files.readAllBytes(Paths get inPath), "UTF-8")

    writeToFile(outPath,
      FieldUtil.createDescription("Crypted file") +
        FieldUtil.createMethod(name) +
        FieldUtil.createFileName(inFile.getName) +
        FieldUtil.createData(new String(Base64.getEncoder.encode(encrypt(file)))))
  }

}



object AESCrypto {
  def initRandom(keySize: Int) = {
    assert(keySize != 128 && keySize != 192 && keySize != 256, "Key size must be {128, 192, 256}")

    val keyGen = KeyGenerator.getInstance("AES");
    keyGen.init(keySize)
    new AESCrypto(CryptoUtil.byteToHex(keyGen.generateKey().getEncoded))
  }
}

class AESCrypto(key: String) extends SymmetricCryptoAlgorithm {
  val name = "AES"

  val secretKey = new SecretKeySpec(CryptoUtil.hexToByte(key), "AES")

  val keySize = secretKey.getEncoded.length * 8

  def decrypt(bytes: Array[Byte]): String = {
    val cipher = Cipher.getInstance("AES")
    cipher.init(Cipher.DECRYPT_MODE, secretKey)
    new String(cipher.doFinal(bytes), "UTF-8")
  }

  def encrypt(text: String): Array[Byte] = {
    val cipher = Cipher.getInstance("AES")
    cipher.init(Cipher.ENCRYPT_MODE, secretKey)
    cipher.doFinal(text.getBytes("UTF-8"))
  }

  def writeKeyToFile(filePath: String) = {
    val key = CryptoUtil.byteToHex(secretKey.getEncoded)
    writeToFile(filePath,
      FieldUtil.createDescription("Secret key") +
        FieldUtil.createMethod(name) +
        FieldUtil.createKeyLength((key.length * 8L).toHexString) +
        FieldUtil.createSecretKey(key))
  }
}
