package hr.vinko.nos.lab3

import hr.vinko.nos._

object test {;import org.scalaide.worksheet.runtime.library.WorksheetSupport._; def main(args: Array[String])=$execute{;$skip(142); 

	val hex = "3402cf455d897800efadd76df4e169abab5bd73a031b63a6ee9452c7f7aebd8b";System.out.println("""hex  : String = """ + $show(hex ));$skip(44); 

	println(CryptoUtil.hexToByte(hex).length)}
	
	}
