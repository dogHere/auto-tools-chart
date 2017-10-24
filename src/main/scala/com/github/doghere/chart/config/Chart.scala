package com.github.doghere.chart.config

import java.nio.file.Paths


object Chart{
  def parse(node:scala.xml.Node):Option[Chart]={
    val title = node \ "title"
    val action = node \ "action"
    val coding = node \ "coding"
    if(List[scala.xml.NodeSeq](title,action).map(k=>if(k.text.trim=="")0 else 1).sum<2) None
    else{
      Some(new Chart(title.text.trim,action.map(a=>Action.parse(a)).toList,coding={if(coding.text.trim=="")Some("utf-8")else{Some(coding.text.trim)}}))
    }
  }

  def main(args: Array[String]): Unit = {
    val chart =
      <chart>
        <title>件均通过率例子</title>
        <action>
          <operation>from</operation>
          <name>{"件均通过率例子.csv"}</name>
        </action>
      </chart>

    println(Chart.parse(chart))
  }
}

class Chart(val title:String,val actions:List[Option[Action]],val coding:Option[String]=Some("utf-8")){

  override def toString = s"Chart($actions)"

  private var fromValue: Option[String] = None
  private var toValue:Option[String] = None

  def setPath(currentPath:String): Unit ={
    actions.map {
      case None => None
      case Some(action) =>
        if (action.operation == "from") {
          Some(("from", action.name))
        } else if (action.operation == "to") {
          Some(("to", action.name))
        } else {
          None
        }
    }.filter(k=>{
      k.isDefined
    }).foreach(k=>{
      if(k.get._1=="from"){
        fromValue=Some(Paths.get(currentPath,k.get._2).toString)
      }else if(k.get._1=="to"){
        toValue = Some(Paths.get(currentPath,k.get._2).toString)
      }
    })
  }

  def from:Option[String]={
      fromValue
  }

  def to:Option[String]= toValue

  def isComplete:Boolean={
    if (this.from.isDefined && this.to.isDefined) true else false
  }
}
