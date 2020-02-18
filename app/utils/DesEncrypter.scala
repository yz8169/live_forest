package utils

import java.util.Base64
import javax.crypto._
import javax.crypto.spec._

class DesEncrypter(key:String) {
  val aesKey=new SecretKeySpec(key.getBytes(),"AES")
  val cipher=Cipher.getInstance("AES")

  def encrypt(text: String) = {
    cipher.init(Cipher.ENCRYPT_MODE, aesKey)
    val enc = cipher.doFinal(text.getBytes())
    val encoder=Base64.getEncoder
    encoder.encodeToString(enc)
  }

  def decrypt(text: String) = {
    cipher.init(Cipher.DECRYPT_MODE, aesKey)
    val decoder=Base64.getDecoder
    new String(cipher.doFinal(decoder.decode(text)))
  }


}
