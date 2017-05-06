package hr.vinko.nos.lab3

import scala.io.Source
import hr.vinko.nos.lab3.FieldUtil.writeToFile
import java.util.Base64

class DigitalSignature(val hash: HashFunction, val algo: RSACrypto) {
  def createSignature(fileName: String, outFile: String) = {
    val hashLength = hash.keySize.toHexString
    val algoLength = algo.keySize.toHexString

    val content = Source.fromFile(fileName).toList mkString "\n"

    writeToFile(outFile,
      FieldUtil.createDescription("Signature") +
        FieldUtil.createFileName(fileName) +
        FieldUtil.createMethod(hash.name :: algo.name :: Nil) +
        FieldUtil.createKeyLength(hashLength :: algoLength :: Nil) +
        FieldUtil.createSignature(CryptoUtil.byteToHex(algo.encryptPrivate(hash.hash(content)))))
  }
  
  def checkSignature(fileName: String, signature: String) = {
    val content = Source.fromFile(fileName).toList mkString "\n"
    val digest = algo.decryptPrivate(CryptoUtil.hexToByte(signature))
    digest equals hash.hash(content)
  }
}

class DigitalEnvelope(val symCrypto: AESCrypto, val asymCrypto: AsymmetricCryptoAlgorithm) {
  def createEnvelope(fileName: String, outFile: String) = {

    val content = Source.fromFile(fileName).toList mkString ""

    writeToFile(outFile,
      FieldUtil.createData("Envelope") +
        FieldUtil.createFileName(fileName) +
        FieldUtil.createMethod(symCrypto.name :: asymCrypto.name :: Nil) +
        FieldUtil.createKeyLength((symCrypto.keySize).toHexString :: (asymCrypto.keySize).toHexString :: Nil) +
        FieldUtil.createEnvelopeData(new String(Base64.getEncoder.encode(symCrypto.encrypt(content)))) +
        FieldUtil.createEnvelopeCryptKey(CryptoUtil.byteToHex(asymCrypto.encryptPublic(CryptoUtil.byteToHex(symCrypto.secretKey.getEncoded)))) + 
        FieldUtil.createInitializationVector(CryptoUtil.byteToHex(asymCrypto.encryptPublic(CryptoUtil.byteToHex(symCrypto.ivSpec.getIV)))))

  }
}