package hr.vinko.nos.lab3

import java.security.interfaces.RSAPublicKey
import java.security.interfaces.RSAPrivateKey
import java.io.File
import java.nio.file.Paths
import java.util.Base64
import hr.vinko.nos.lab3.FileUtil.writeToFile
import java.nio.file.Files
import java.security.KeyPairGenerator
import java.math.BigInteger
import java.security.KeyFactory
import java.security.KeyFactory
import javax.crypto.Cipher
import java.security.spec.RSAPrivateKeySpec
import java.security.spec.RSAPublicKeySpec
import java.security.PublicKey
import java.security.PrivateKey

trait AsymmetricCryptoAlgorithm {

  val name: String
  val keySize: Int
  def publicKey: PublicKey
  def privateKey: PrivateKey

  def encryptPrivate(text: String): Array[Byte]
  def encryptPublic(text: String): Array[Byte]
  def decryptPrivate(bytes: Array[Byte]): String
  def decryptPublic(bytes: Array[Byte]): String

  def encryptFilePrivate(inPath: String, outPath: String) = {
    val inFile = new File(inPath)

    val file = new String(Files.readAllBytes(Paths get inPath), "UTF-8")

    writeToFile(outPath,
      FieldUtil.createDescription("Crypted file") +
        FieldUtil.createMethod(name) +
        FieldUtil.createFileName(inFile.getName) +
        FieldUtil.createData(new String(Base64.getEncoder.encode(encryptPrivate(file)))))
  }
  
  def encryptFilePublic(inPath: String, outPath: String) = {
    val inFile = new File(inPath)

    val file = new String(Files.readAllBytes(Paths get inPath), "UTF-8")

    writeToFile(outPath,
      FieldUtil.createDescription("Crypted file") +
        FieldUtil.createMethod(name) +
        FieldUtil.createFileName(inFile.getName) +
        FieldUtil.createData(new String(Base64.getEncoder.encode(encryptPublic(file)))))
  }

}

object RSACrypto {
  def initRandom() = {
    val keyGen = KeyPairGenerator.getInstance("RSA")

    keyGen.initialize(1024)

    val keyPair = keyGen.generateKeyPair

    val n = keyPair.getPrivate.asInstanceOf[RSAPrivateKey].getModulus
    val d = keyPair.getPrivate.asInstanceOf[RSAPrivateKey].getPrivateExponent
    val e = keyPair.getPublic.asInstanceOf[RSAPublicKey].getPublicExponent

    new RSACrypto(n, e, d)
  }
}

class RSACrypto(n: BigInteger, e: BigInteger, d: BigInteger) extends AsymmetricCryptoAlgorithm {
  val name = "RSA"
  val keyFactory = KeyFactory.getInstance("RSA")

  def publicKey = keyFactory.generatePublic(new RSAPublicKeySpec(n, e))
  def privateKey = keyFactory.generatePrivate(new RSAPrivateKeySpec(n, d))

  val keySize = n.toByteArray.length * 8

  def encryptPublic(text: String): Array[Byte] = {
    val cipher = Cipher.getInstance("RSA")
    cipher.init(Cipher.ENCRYPT_MODE, publicKey)
    cipher.doFinal(text.getBytes("UTF-8"))
  }

  def decryptPublic(bytes: Array[Byte]): String = {
    val cipher = Cipher.getInstance("RSA")
    cipher.init(Cipher.DECRYPT_MODE, privateKey)
    new String(cipher.doFinal(bytes), "UTF-8")
  }

  def encryptPrivate(text: String): Array[Byte] = {
    val cipher = Cipher.getInstance("RSA")
    cipher.init(Cipher.ENCRYPT_MODE, privateKey)
    cipher.doFinal(text.getBytes("UTF-8"))
  }

  def decryptPrivate(bytes: Array[Byte]): String = {
    val cipher = Cipher.getInstance("RSA")
    cipher.init(Cipher.DECRYPT_MODE, publicKey)
    new String(cipher.doFinal(bytes), "UTF-8")
  }

  def writePrivateKeyToFile(filePath: String) = {
    writeToFile(filePath,
      FieldUtil.createDescription("Private key") +
        FieldUtil.createMethod(name) +
        FieldUtil.createKeyLength((n.toString(16).length * 8L).toHexString) +
        FieldUtil.createModulus(n.toString(16)) +
        FieldUtil.createPrivateExp(d.toString(16)))
  }
  def writePublicKeyToFile(filePath: String) = {
    writeToFile(filePath,
      FieldUtil.createDescription("Public key") +
        FieldUtil.createMethod(name) +
        FieldUtil.createKeyLength((n.toString(16).length * 8L).toHexString) +
        FieldUtil.createModulus(n.toString(16)) +
        FieldUtil.createPublicExp(e.toString(16)))
  }
}