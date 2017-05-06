package hr.vinko.nos.lab3

object FieldUtil {

  val MAX_COL = 60

  val HEADER = "---BEGIN OS2 CRYPTO DATA---"
  val FOOTER = "---END OS2 CRYPTO DATA---"

  def writeToFile(path: String, content: String) = {
    FileUtil.writeToFile(HEADER + "\n" + content + FOOTER, path)
  }

  def createField(title: String, text: String): String = {
    createField(title, split(text, MAX_COL))
  }

  def createField(title: String, text: List[String]): String = {
    val sb = new StringBuilder

    sb.append(title + "\n")

    text.foreach { x =>
      sb.append("    ")
      sb.append(x + "\n")
    }

    sb.append("\n")

    sb.toString
  }

  def split(text: String, n: Int): List[String] = {
    if (text.size <= n) text :: Nil
    else (text take n) :: split(text drop n, n)
  }

  def getField(title: String, lines: List[String]): String = {
    val index = lines.indexOf(title + ":")
    if (index >= 0) {
      val tmp = lines drop index + 1
      (tmp take tmp.indexOf("")).toList map { _.trim } mkString ("")
    } else {
      "0"
    }
  }

  def createDescription(text: String) = {
    createField("Description:", text)
  }

  def createFileName(text: String) = {
    createField("File name:", text)
  }

  def createMethod(text: String) = {
    createField("Method:", text)
  }

  def createMethod(text: List[String]) = {
    createField("Method:", text)
  }

  def createKeyLength(text: String) = {
    createField("Key length:", text)
  }

  def createKeyLength(text: List[String]) = {
    createField("Key length:", text)
  }

  def createSecretKey(text: String) = {
    createField("Secret key:", text)
  }
  def createInitializationVector(text: String) = {
    createField("Initialization vector:", text)
  }
  def createModulus(text: String) = {
    createField("Modulus:", text)
  }
  def createPublicExp(text: String) = {
    createField("Public exponent:", text)
  }
  def createPrivateExp(text: String) = {
    createField("Private exponent:", text)
  }
  def createSignature(text: String) = {
    createField("Signature:", text)
  }
  def createData(text: String) = {
    createField("Data:", text)
  }
  def createEnvelopeData(text: String) = {
    createField("Envelope data:", text)
  }
  def createEnvelopeCryptKey(text: String) = {
    createField("Envelope crypt key:", text)
  }

}