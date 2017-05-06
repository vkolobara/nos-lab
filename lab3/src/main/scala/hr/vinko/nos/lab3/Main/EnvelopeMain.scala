package hr.vinko.nos.lab3.Main

import scala.io.Source
import java.math.BigInteger
import java.util.Base64
import hr.vinko.nos.lab3.AESCrypto
import hr.vinko.nos.lab3.CryptoUtil
import hr.vinko.nos.lab3.DigitalEnvelope
import hr.vinko.nos.lab3.FieldUtil
import hr.vinko.nos.lab3.FileUtil
import hr.vinko.nos.lab3.RSACrypto

object EnvelopeMain extends App {
  
  assert(args.length == 4, "TEST")
  
  if (args(0) equals "encrypt") {
	  encrypt(args)    
  } else {
    decrypt(args)
  } 
  
  def encrypt(args: Array[String]) = {
    assert(args.length == 4, "4 arguments must be provided (encrypt, input file, public key, output file)")
    
    val n = new BigInteger(FieldUtil.getField("Modulus", Source.fromFile(args(2)).getLines.toList), 16)
    val e = new BigInteger(FieldUtil.getField("Public exponent", Source.fromFile(args(2)).getLines.toList), 16)
    val d = new BigInteger("1")
    
    val rsa = new RSACrypto(n, e, d)
        
    val aes = AESCrypto.initRandom(128)
    
    val envelope = new DigitalEnvelope(aes, rsa)
    envelope.createEnvelope(args(1), args(3))
  }
  
  def decrypt(args: Array[String]) = {
    assert(args.length == 4, "4 arguments must be provided (decrypt, input file, private key, output file)")
    
    val n = new BigInteger(FieldUtil.getField("Modulus", Source.fromFile(args(2)).getLines.toList), 16)
    val d = new BigInteger(FieldUtil.getField("Private exponent", Source.fromFile(args(2)).getLines.toList), 16)
    val e = new BigInteger("1")
    
    val rsa = new RSACrypto(n, e, d)
    
    val envelopeData = FieldUtil.getField("Envelope data", Source.fromFile(args(1)).getLines.toList)
    val cryptedAESKey = FieldUtil.getField("Envelope crypt key", Source.fromFile(args(1)).getLines.toList)
    val cryptedIV = FieldUtil.getField("Initialization vector", Source.fromFile(args(1)).getLines.toList)
        
    val aesKey = rsa.decryptPublic(CryptoUtil.hexToByte(cryptedAESKey))
    val aesIV = rsa.decryptPublic(CryptoUtil.hexToByte(cryptedIV))
    
    val aes = new AESCrypto(aesKey, aesIV)
    
    
    val envelope = new DigitalEnvelope(aes, rsa)
    
    FileUtil.writeToFile(aes.decrypt(Base64.getDecoder.decode(envelopeData.getBytes)), args(3))
  }
  
}