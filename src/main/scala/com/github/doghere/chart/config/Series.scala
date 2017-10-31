package com.github.doghere.chart.config

import com.github.doghere.chart.render.{Bar, Line, Render}


object Series{
  def parse(node:scala.xml.Node):Option[Series]={
    val name  = node \ "name"
    val column = node \ "column"
    val render = node \ "render"
    val applications = node \ "applications"

    if(List[scala.xml.NodeSeq](name,column).map(k=>if(k.text.trim=="")0 else 1).sum<2){
      None
    }else{
      Some(new Series(name.text.trim ,column.text.trim,render.text.trim match {
        case "bar"=>new Bar
        case "line"=>new Line
        case _ => new Line
      },applications = (applications \ "application").map(s=> {
        Application.parse(s)
      }).toList))
    }
  }

  def main(args: Array[String]): Unit = {
    val series =
      <series>
        <name>test</name>
        <column>件均</column>
        <render>bar</render>
        <applications>
          <application>
            <name>group</name>
            <column>组</column>
          </application>
          <application>
            <name>break</name>
            <column>类别</column>
          </application>
        </applications>
      </series>

    println(Series.parse(series))
  }
}

class Series(val name:String,val column:String,val render:Render
             ,val applications: List[Option[Application]]=List[Option[Application]]()){

  override def toString = s"Series($name, $column, $render,$applications)"
}