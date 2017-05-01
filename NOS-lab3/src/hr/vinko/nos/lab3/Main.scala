package hr.vinko.nos.lab3

import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.math.BigInteger
import java.nio.file.Files
import java.nio.file.Paths
import scala.io.Source
import java.util.Base64

object Main extends App {
  
  val x = "BLABLAPROBA"
  
  val aes = new AESCrypto("12345678901234561234567890123456")
     
  println(aes encrypt x)
  println(aes decrypt (aes encrypt x))
  
  var lines = Source.fromFile("black_knight_encrypted.txt").getLines.toList
  val encrypted = FieldUtil.getField("Data", lines)
  
  println(encrypted)
  
  println (aes decrypt Base64.getDecoder.decode(encrypted))
  
}