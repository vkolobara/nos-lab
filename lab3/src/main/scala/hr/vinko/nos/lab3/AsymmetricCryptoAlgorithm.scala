package hr.vinko.nos.lab3

import java.security.interfaces.RSAPublicKey
import java.security.interfaces.RSAPrivateKey
import java.io.File
import java.nio.file.Paths
import java.util.Base64
import hr.vinko.nos.lab3.FieldUtil.writeToFile
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
import java.security.spec.KeySpec
import java.security.Key
import scala.io.Source

trait AsymmetricCryptoAlgorithm {

  val name: String
  val keySize: Int
  def publicKey: PublicKey
  def privateKey: PrivateKey

  def encryptPrivate(text: String): Array[Byte]
  def encryptPublic(text: String): Array[Byte]

  def decryptPrivate(bytes: Array[Byte]): String
  def decryptPublic(bytes: Array[Byte]): String

  def encryptFilePrivate(inPath: String, outPath: String)
  def encryptFilePublic(inPath: String, outPath: String)

  def writePrivateKeyToFile(filePath: String)
  def writePublicKeyToFile(filePath: String)

}

abstract class CryptoAlgorithmAuto extends AsymmetricCryptoAlgorithm {
  def encryptPrivate(text: String): Array[Byte] = {
    CryptoAlgorithm.encrypt(text, privateKey, name)
  }
  def encryptPublic(text: String): Array[Byte] = {
    CryptoAlgorithm.encrypt(text, publicKey, name)
  }

  def decryptPrivate(bytes: Array[Byte]): String = {
    CryptoAlgorithm.decrypt(bytes, publicKey, name)
  }
  def decryptPublic(bytes: Array[Byte]): String = {
    CryptoAlgorithm.decrypt(bytes, privateKey, name)
  }

  def encryptFilePrivate(inPath: String, outPath: String) = {
    CryptoAlgorithm.encryptFile(inPath, outPath, privateKey, name)
  }

  def encryptFilePublic(inPath: String, outPath: String) = {
    CryptoAlgorithm.encryptFile(inPath, outPath, publicKey, name)
  }
}

object CryptoAlgorithm {

  def encrypt(text: String, key: Key, name: String): Array[Byte] = {
    val cipher = Cipher.getInstance(name)
    cipher.init(Cipher.ENCRYPT_MODE, key)
    cipher.doFinal(text.getBytes("UTF-8"))
  }

  def decrypt(bytes: Array[Byte], key: Key, name: String): String = {
    val cipher = Cipher.getInstance(name)
    cipher.init(Cipher.DECRYPT_MODE, key)
    new String(cipher.doFinal(bytes), "UTF-8")
  }

  def encryptFile(inPath: String, outPath: String, key: Key, name: String) = {
    val inFile = new File(inPath)

    val file = new String(Files.readAllBytes(Paths get inPath), "UTF-8")

    writeToFile(outPath,
      FieldUtil.createDescription("Crypted file") +
        FieldUtil.createMethod(name) +
        FieldUtil.createFileName(inFile.getName) +
        FieldUtil.createData(new String(Base64.getEncoder.encode(encrypt(file, key, name)))))
  }
  
  
  def decryptFile(inPath: String, outPath: String, key: Key, name: String) = {
    val inFile = new File(inPath)

    val file = FieldUtil.getField("Data", Source.fromFile(inPath).getLines.toList)

    FileUtil.writeToFile(new String(decrypt(Base64.getDecoder.decode(file.getBytes("UTF-8")), key, name)), outPath)
  }

}
object RSACrypto {
  def initRandom(keySize: Int) = {
    assert(keySize >= 1024, "key_size >= 1024")

    val keyGen = KeyPairGenerator.getInstance("RSA")

    keyGen.initialize(keySize)

    val keyPair = keyGen.generateKeyPair

    val n = keyPair.getPrivate.asInstanceOf[RSAPrivateKey].getModulus
    val d = keyPair.getPrivate.asInstanceOf[RSAPrivateKey].getPrivateExponent
    val e = keyPair.getPublic.asInstanceOf[RSAPublicKey].getPublicExponent

    new RSACrypto(n, e, d)
  }
}

class RSACrypto(n: BigInteger, e: BigInteger, d: BigInteger) extends CryptoAlgorithmAuto {
  val name = "RSA"
  val keyFactory = KeyFactory.getInstance("RSA")
  def publicKey = keyFactory.generatePublic(new RSAPublicKeySpec(n, e))
  def privateKey = keyFactory.generatePrivate(new RSAPrivateKeySpec(n, d))

  val keySize = n.toString(16).length * 4
    
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