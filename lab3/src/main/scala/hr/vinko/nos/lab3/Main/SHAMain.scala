package hr.vinko.nos.lab3.Main

import scala.io.Source
import hr.vinko.nos.lab3.FileUtil
import hr.vinko.nos.lab3.SHA256

object SHAMain extends App {
    assert(args.length >= 2, "Arguments needed (input file, output file)")
    val sha = new SHA256
    val text = Source.fromFile(args(0)).getLines.mkString("\n")
    val hash = sha.hash(text)
    FileUtil.writeToFile(hash, args(1))
    
    println("Calculated hash: \n" + hash)
}