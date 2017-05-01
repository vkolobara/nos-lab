package hr.vinko.nos.lab3

import java.io.File
import java.io.PrintWriter

object FileUtil {
  def writeToFile(text: String, filePath: String) = {
    val pw = new PrintWriter(new File(filePath))
    pw write text
    pw close
  }
  
}