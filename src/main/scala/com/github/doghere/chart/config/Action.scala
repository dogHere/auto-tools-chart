package com.github.doghere.chart.config


object Action{
  def from ="from"

  def to = "to"

  def parse(node:scala.xml.Node):Option[Action]={
    val operation = node \ "operation"
    val name = node \"name"
    val axis = node \ "axis"

    if(List[scala.xml.NodeSeq](operation,name)
      .map(k=>if(k.text.trim=="")0 else 1).sum<2){
      None
    }else{
      Some(new Action(operation.text.trim,name.text.trim,axis.map(a=>Axis.parse(a)).toList))
    }

  }

  def main(args: Array[String]): Unit = {
    val action =
      <action>
        <operation>to</operation>
        <name>件均通过率例子.png</name>
        <axis>
          <position>x</position>
          <series>
            <name>日期（月）</name>
            <column>日期</column>
          </series>
        </axis>
        <axis>
          <position>y1</position>
          <series>
            <column>件均</column>
            <render>bar</render>
          </series>
        </axis>
        <axis>
          <position>y2</position>
          <range>
            <from>0</from>
            <to>100</to>
          </range>
          <series>
            <name>贷审通过率(%)</name>
            <column>贷审通过率</column>
            <render>line</render>
          </series>
          <series>
            <name>总通过率(%)</name>
            <column>总通过率</column>
            <render>line</render>
          </series>
        </axis>
      </action>

    println(Action.parse(action))
  }

}

class Action(val operation:String,val name:String,val axis:List[Option[Axis]]){

  override def toString = s"Action($operation, $name, $axis)"


}
