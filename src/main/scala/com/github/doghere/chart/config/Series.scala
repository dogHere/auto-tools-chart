package com.github.doghere.chart.config

import com.github.doghere.chart.render.{Bar, Line, Render}


object Series{
  def parse(node:scala.xml.Node):Option[Series]={
    val name  = node \ "name"
    val column = node \ "column"
    val render = node \ "render"

    if(List[scala.xml.NodeSeq](name,column).map(k=>if(k.text.trim=="")0 else 1).sum<2){
      None
    }else{
      Some(new Series(name.text.trim ,column.text.trim,render.text.trim match {
        case "bar"=>new Bar
        case "line"=>new Line
        case _ => new Line
      },(node \ "level").text.trim match {
        case ""=>0
        case e =>try{Integer.parseInt(e)}catch {case exp:Exception=>0}
        case _ => 0
      }))
    }
  }

  def main(args: Array[String]): Unit = {
    val series =
      <series>
        <column>件均</column>
        <render>bar</render>
      </series>

    println(Series.parse(series))
  }
}

class Series(val name:String,val column:String,val render:Render,val level:Int=0){

  override def toString = s"Series($name, $column, $render,$level)"
}