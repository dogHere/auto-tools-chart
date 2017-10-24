package com.github.doghere.chart.config


object Axis{

  def x = "x"

  def y1  ="y1"

  def y2 = "y2"

  def y = "y"

  def parse(node:scala.xml.Node):Option[Axis]={
    val position = node \ "position"
    val series   = node \ "series"
    val range    = node \ "range"

    if(List[scala.xml.NodeSeq](position,series).map(k=>if(k.text.trim=="")0 else 1).sum<2){
      None
    }else{
      Some(new Axis(position = position.text.trim
        , series = series.map(s=>Series.parse(s)).toList
        , range = range.map(s=> Range.parse(s)) match {
          case x::_ =>x
          case Nil => None
        }
      ))
    }
  }

  def main(args: Array[String]): Unit = {
    val axis =
      <axis>
        <position>y1</position>
        <series>
          <column>件均</column>
          <render>bar</render>
        </series>
      </axis>

    println(Axis.parse(axis))
  }

}
class Axis(val position:String,val series:List[Option[Series]],val range:Option[Range] ) {

  override def toString = s"Axis($position, $series, $range)"
}

