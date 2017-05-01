package hr.vinko.nos.lab3

import scala.io.Source
import java.util.Base64

/**
 * Args: encrypt/decrypt/generate, AES key, input file, output file
 */
object AESMain extends App {

  assert(args.length >= 3, "Arguments needed")
   
  if (args(0) equals "generate") {
    generate(args)
  } else if (args(0) equals "encrypt") {
    encrypt(args)
  } else {
    decrypt(args)
  }

  def generate(args: Array[String]) = {
      assert(args.length == 3, "3 arguments must be provided (generate, key size, output file)")

      val aes = AESCrypto.initRandom(args(1).toInt)
      aes.writeKeyToFile(args(2))
  }

  def encrypt(args: Array[String]) = {
    assert(args.length == 4, "4 arguments must be provided (encrypt, AES key, input file, output file)")

    val key = FieldUtil.getField("Secret key", Source.fromFile(args(1)).getLines.toList)

    val iv = FieldUtil.getField("Initialization vector", Source.fromFile(args(1)).getLines.toList)
    
    val aes = new AESCrypto(key, iv)

    aes.encryptFile(args(2), args(3))
  }

  def decrypt(args: Array[String]) = {
    assert(args.length == 4, "4 arguments must be provided (decrypt, AES key, input file, output file)")

    val key = FieldUtil.getField("Secret key", Source.fromFile(args(1)).getLines.toList)
    val iv = FieldUtil.getField("Initialization vector", Source.fromFile(args(1)).getLines.toList)
    
    val aes = new AESCrypto(key, iv)

    aes.decryptFile(args(2), args(3))
  }
}