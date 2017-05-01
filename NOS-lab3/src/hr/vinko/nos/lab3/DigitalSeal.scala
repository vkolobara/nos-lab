package hr.vinko.nos.lab3

import scala.io.Source
import hr.vinko.nos.lab3.FileUtil.writeToFile
import java.util.Base64

class DigitalSignature(val hash: HashFunction, val algo: RSACrypto) {
  def createSignature(fileName: String, outFile: String) = {
    val hashLength = hash.keySize.toString
    val algoLength = algo.keySize.toString

    val content = Source.fromFile(fileName).toList mkString ""

    writeToFile(outFile,
      FieldUtil.createDescription("Signature") +
        FieldUtil.createFileName(fileName) +
        FieldUtil.createMethod(hash.name :: algo.name :: Nil) +
        FieldUtil.createKeyLength(hashLength :: algoLength :: Nil) +
        FieldUtil.createSignature(CryptoUtil.byteToHex(algo.encryptPrivate(hash.hash(content)))))
  }
}

class DigitalEnvelope(val symCrypto: SymmetricCryptoAlgorithm, val asymCrypto: AsymmetricCryptoAlgorithm) {
  def createEnvelope(fileName: String, outFile: String) = {

    val content = Source.fromFile(fileName).toList mkString ""

    writeToFile(outFile,
      FieldUtil.createData("Envelope") +
        FieldUtil.createFileName(fileName) +
        FieldUtil.createMethod(symCrypto.name :: asymCrypto.name :: Nil) +
        FieldUtil.createKeyLength(symCrypto.keySize.toString :: asymCrypto.keySize.toString :: Nil) +
        FieldUtil.createEnvelopeData(new String(Base64.getEncoder.encode(symCrypto.encrypt(content)))) +
        FieldUtil.createEnvelopeCryptKey(CryptoUtil.byteToHex(asymCrypto.encryptPublic(new String(symCrypto.secretKey.getEncoded)))))

  }
}