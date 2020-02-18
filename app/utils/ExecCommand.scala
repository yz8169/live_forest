package utils

import java.io.File

import scala.sys.process
import scala.sys.process._

class ExecCommand {
  var isSuccess: Boolean = false
  val err = new StringBuilder
  val out = new StringBuilder
  val log = ProcessLogger(out append _ append "\n", err append _ append "\n")

  def exec(command: String, outFile: File, workspace: File) = {
    val process = Process(command, workspace).#>(outFile)
    val exitCode = process ! log
    if (exitCode == 0) isSuccess = true
  }

  def exec(command: String, workspace: File) = {
    val process = Process(command, workspace)
    val exitCode = process ! log
    if (exitCode == 0) isSuccess = true
  }

  def exec(command1: String, command2: String, workspace: File) = {
    val exitCode = Process(command1, workspace).#&&(Process(command2, workspace)) ! log
    if (exitCode == 0) isSuccess = true
  }

  def exec(command1: String, command2: String,command3:String, workspace: File) = {
    val exitCode = Process(command1, workspace).#&&(Process(command2, workspace)).#&&(Process(command3, workspace)) ! log
    if (exitCode == 0) isSuccess = true
  }


  def getErrStr = err.toString()


  def getOutStr = out.toString()

}
