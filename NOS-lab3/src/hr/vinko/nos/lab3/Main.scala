package hr.vinko.nos.lab3

object Main extends App {
  
  val x = "BETI SUPER"
  
  val aes = new AESCrypto("12345678901234561234567890123456")
     
  println(aes encrypt x)
  println(aes decrypt (aes encrypt x))
  
  val sha256 = new SHA256
  
  println(sha256.hash(x))
  
}