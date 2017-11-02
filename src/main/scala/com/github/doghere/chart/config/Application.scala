package com.github.doghere.chart.config

import com.github.doghere.chart.render.{Bar, Line}


object Application{

  def main(args: Array[String]): Unit = {
    val a =
      <application>
        <name>break</name>
        <column>类别</column>
      </application>
    println(parse(a))
  }

  val group = "group"
  val break = "break"
  val breakGroup = "breakGroup"
  val percent = "percent"

  def parse(node:scala.xml.Node):Option[Application]={
    val name  = node \ "name"
    val column = node \ "column"

    if(List[scala.xml.NodeSeq](name,column).map(k=>if(k.text.trim=="")0 else 1).sum<1){
      None
    }else{
      Some(new Application(name.text.trim ,column.text.trim))
    }
  }
}

class Application(val name:String,val column:String) {
  override def toString = s"Application($name, $column)"
}
