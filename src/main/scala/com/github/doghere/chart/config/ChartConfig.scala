package com.github.doghere.chart.config


import java.io.File

object ChartConfig{
  def load(file:File): Option[List[Option[Chart]]] ={
    if(file.exists() && file.isFile) {
      val config = scala.xml.XML
        .load(file.getPath)
//      val chart = config \ "chart"
      load(config,file.getParent)
    }else{
      None
    }
  }

  def load(node:scala.xml.NodeSeq,currentPath:String): Option[List[Option[Chart]]] ={
    val chart = node \ "chart"
    if(chart.text.trim!=""){
      Some(chart.map(c=> {
        val cr = Chart.parse(c)
        cr match {
          case None=>None
          case Some(cc)=>
            cc.setPath(currentPath)
            Some(cc)
        }
      }).toList)
    }else{
      None
    }
  }

  def main(args: Array[String]): Unit = {
    val l = load(new File("charts/charts.xml"))
    println(l)
  }
}
