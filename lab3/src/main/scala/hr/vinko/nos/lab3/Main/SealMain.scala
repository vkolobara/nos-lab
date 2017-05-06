package hr.vinko.nos.lab3.Main

object SealMain extends App {
  
  assert(args.length == 6, "ARGUMENTS NEEDED")
  
  if (args(0) equals "generate") {
    generate(args)
  } else {
    open(args)
  }

  def generate(args: Array[String]) = {
    assert(args.length == 6, "6 arguments must be provided (generate, input file, public key receiver, private key sender, output envelope, output signature)")

    EnvelopeMain.encrypt(("encrypt" :: args(1) :: args(2) :: args(4) :: Nil).toArray)
    SignatureMain.generate(("generate" :: args(4) :: args(3) :: args(5) :: Nil).toArray)
    
  }
  
  def open(args: Array[String]) = {
    assert(args.length == 6, "6 arguments must be provided (open, envelope, signature, public key sender, private key receiver, output)")

    val signatureValid = SignatureMain.check(("check" :: args(1) :: args(2) :: args(3) :: Nil).toArray)
    
    if (!signatureValid) println("Signature not valid")
    else {
      EnvelopeMain.decrypt(("decrypt" :: args(1) :: args(4) :: args(5) :: Nil).toArray)
    }
    
  }

}