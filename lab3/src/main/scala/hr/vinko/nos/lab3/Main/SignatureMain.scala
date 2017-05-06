package hr.vinko.nos.lab3.Main

import scala.io.Source
import java.math.BigInteger
import hr.vinko.nos.lab3.DigitalSignature
import hr.vinko.nos.lab3.FieldUtil
import hr.vinko.nos.lab3.RSACrypto
import hr.vinko.nos.lab3.SHA256

object SignatureMain extends App {

  assert(args.length == 4, "Arguments needed")

  if (args(0) equals "generate") {
    generate(args)
  } else {
    check(args)
  }

  def generate(args: Array[String]) = {
    assert(args.length == 4, "4 arguments must be provided (generate, input file, private key, output file)")

    val n = new BigInteger(FieldUtil.getField("Modulus", Source.fromFile(args(2)).getLines.toList), 16)
    val d = new BigInteger(FieldUtil.getField("Private exponent", Source.fromFile(args(2)).getLines.toList), 16)
    val e = new BigInteger("1")

    val rsa = new RSACrypto(n, e, d)

    val hash = new SHA256

    val signature = new DigitalSignature(hash, rsa)

    signature.createSignature(args(1), args(3))
  }

  def check(args: Array[String]) = {
    assert(args.length == 4, "4 arguments must be provided (check, input file, signature file, public key)")

    val n = new BigInteger(FieldUtil.getField("Modulus", Source.fromFile(args(3)).getLines.toList), 16)
    val e = new BigInteger(FieldUtil.getField("Public exponent", Source.fromFile(args(3)).getLines.toList), 16)
    val d = new BigInteger("1")

    val rsa = new RSACrypto(n, e, d)

    val hash = new SHA256

    val digitalSignature = new DigitalSignature(hash, rsa)

    val signature = FieldUtil.getField("Signature", Source.fromFile(args(2)).getLines.toList)
    
    val check = digitalSignature.checkSignature(args(1), signature)
    
    println("Signature check returned : " + check)
    check
  }
}