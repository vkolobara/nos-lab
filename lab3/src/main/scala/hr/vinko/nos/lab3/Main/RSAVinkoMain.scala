package hr.vinko.nos.lab3.Main

import java.math.BigInteger
import hr.vinko.nos.lab3.FieldUtil
import hr.vinko.nos.lab3.RSACryptoManual
import scala.io.Source

object RSAVinkoMain extends App {
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

    val rsa = RSACryptoManual.initRandom(args(1).toInt)
    rsa.writePrivateKeyToFile(args(2))
    rsa.writePublicKeyToFile(args(3))
  }

  def encrypt(args: Array[String]) = {
    assert(args.length == 4, "4 arguments must be provided (encrypt, RSA key, input file, output file)")

    val n = new BigInteger(FieldUtil.getField("Modulus", Source.fromFile(args(1)).getLines.toList), 16)
    val d = new BigInteger(FieldUtil.getField("Private exponent", Source.fromFile(args(1)).getLines.toList), 16)
    val e = new BigInteger(FieldUtil.getField("Public exponent", Source.fromFile(args(1)).getLines.toList), 16)

    val rsa = new RSACryptoManual(n, e, d)

    if (e.compareTo(BigInteger.ZERO) != 0) {
      rsa.encryptFilePublic(args(2), args(3))
    } else {
      rsa.encryptFilePrivate(args(2), args(3))
    }
  }

  def decrypt(args: Array[String]) = {
    assert(args.length == 4, "4 arguments must be provided (decrypt, RSA key, input file, output file)")

    val n = new BigInteger(FieldUtil.getField("Modulus", Source.fromFile(args(1)).getLines.toList), 16)
    val d = new BigInteger(FieldUtil.getField("Private exponent", Source.fromFile(args(1)).getLines.toList), 16)
    val e = new BigInteger(FieldUtil.getField("Public exponent", Source.fromFile(args(1)).getLines.toList), 16)

    val rsa = new RSACryptoManual(n, e, d)

    if (e.compareTo(BigInteger.ZERO) != 0) {
      rsa.decryptFilePrivate(args(2), args(3))
    } else {
      rsa.decryptFilePublic(args(2), args(3))
    }
  }
}