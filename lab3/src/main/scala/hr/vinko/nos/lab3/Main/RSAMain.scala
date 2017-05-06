package hr.vinko.nos.lab3.Main

import scala.io.Source
import java.math.BigInteger
import java.security.Key
import java.security.spec.RSAPublicKeySpec
import java.security.KeyFactory
import java.security.spec.RSAPrivateKeySpec
import hr.vinko.nos.lab3.CryptoAlgorithm
import hr.vinko.nos.lab3.FieldUtil
import hr.vinko.nos.lab3.RSACrypto

object RSAMain extends App {
  
  assert(args.length >= 4, "Arguments needed")
   
  if (args(0) equals "generate") {
    generate(args)
  } else if (args(0) equals "encrypt") {
    encrypt(args)
  } else {
    decrypt(args)
  }

  def generate(args: Array[String]) = {
      assert(args.length == 4, "4 arguments must be provided (generate, key size, private key output file, public key output file)")

      val rsa = RSACrypto.initRandom(args(1).toInt)
      rsa.writePrivateKeyToFile(args(2))
      rsa.writePublicKeyToFile(args(3))
  }

  def encrypt(args: Array[String]) = {
    assert(args.length == 4, "4 arguments must be provided (encrypt, RSA key, input file, output file)")

    val n = new BigInteger(FieldUtil.getField("Modulus", Source.fromFile(args(1)).getLines.toList), 16)
    val d = new BigInteger(FieldUtil.getField("Private exponent", Source.fromFile(args(1)).getLines.toList), 16)
    val e = new BigInteger(FieldUtil.getField("Public exponent", Source.fromFile(args(1)).getLines.toList), 16)
    
    var key: Key = null
    
    val keyFactory = KeyFactory.getInstance("RSA")
    
    if (e != 0) {
      key = keyFactory.generatePublic(new RSAPublicKeySpec(n, e))
    } else {
      key = keyFactory.generatePrivate(new RSAPrivateKeySpec(n, d))
    }

    CryptoAlgorithm.encryptFile(args(2), args(3), key, "RSA")
  }

  def decrypt(args: Array[String]) = {
    assert(args.length == 4, "4 arguments must be provided (decrypt, RSA key, input file, output file)")

    val n = new BigInteger(FieldUtil.getField("Modulus", Source.fromFile(args(1)).getLines.toList), 16)
    val d = new BigInteger(FieldUtil.getField("Private exponent", Source.fromFile(args(1)).getLines.toList), 16)
    val e = new BigInteger(FieldUtil.getField("Public exponent", Source.fromFile(args(1)).getLines.toList), 16)
    
    var key: Key = null
    
    val keyFactory = KeyFactory.getInstance("RSA")
    
    
    if (e.compareTo(BigInteger.ZERO) != 0) {
      key = keyFactory.generatePublic(new RSAPublicKeySpec(n, e))
    } else {
      key = keyFactory.generatePrivate(new RSAPrivateKeySpec(n, d))
    }

    CryptoAlgorithm.decryptFile(args(2), args(3), key, "RSA")
  }
}