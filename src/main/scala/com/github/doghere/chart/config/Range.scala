package com.github.doghere.chart.config



object Range{

  def parse(node:scala.xml.Node):Option[Range]={
    val from = node \ "from"
    val to   = node \ "to"

    if(List[scala.xml.NodeSeq](from,to).map(k=>if(k.text.trim == "")0 else 1).sum<2){
      None
    }else{
      Some(new Range(from.text.trim.toDouble,to.text.trim.toDouble))
    }

  }


}

class Range(val from:Double,val to:Double){

  override def toString = s"Range($from, $to)"

}

