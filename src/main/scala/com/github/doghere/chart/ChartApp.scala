package com.github.doghere.chart

import java.io.File
import java.nio.file.Paths
import javax.swing.{JFrame, SwingUtilities}

import com.github.doghere.chart.config.{Action, Axis, Chart, ChartConfig}
import org.apache.commons.csv.{CSVFormat, CSVParser, CSVRecord}
import org.jfree.chart.{ChartPanel, ChartUtilities, JFreeChart}
import org.jfree.chart.axis.{CategoryAxis, NumberAxis}
import org.jfree.chart.plot.CategoryPlot
import com.github.doghere.chart.render._

object ChartApp  {

  def main(args: Array[String]): Unit = {
    val charts = ChartConfig.load(new File("charts/charts.xml"))
    val chart = charts.get.head.get
    lazy val chartManager = new ChartApp(chart)
    chartManager.toJPG(chart.to.get)
  }


}


class ChartApp(val chartConfig:Chart) extends JFrame{

  private val cSVParser = CSVParser.parse(scala.io.Source.fromFile(this.chartConfig.from.get,chartConfig.coding.get).mkString
    ,CSVFormat.DEFAULT.withHeader())
  private val header = cSVParser.getHeaderMap
  private val records = cSVParser.getRecords



  private def draw(): JFreeChart ={

    if(!this.chartConfig.isComplete){
      throw new RuntimeException("Error:chart is not completed or has error")
    }
    setTitle(this.chartConfig.title)

    var xColumnNumber = -1
    var nextSeries = 0

    val plot = new CategoryPlot()

    this.chartConfig.actions.foreach {
      case None =>  throw new Exception(s"Error:chart.action is not set")
      case Some(act) => {
        if (act.operation == Action.to) {
          act.axis.foreach {
            case None =>  throw new Exception(s"Error:chart.action.axis(to) is not set")
            case Some(axis)=>{
              println(axis)
              if(axis.position=="x"){
                axis.series.foreach{
                  case None=>throw new RuntimeException("axis x can not be none")
                  case Some(x)=>{

                    xColumnNumber = header.get(x.column)
                    plot.setDomainAxis(new CategoryAxis(x.name))
                  }
                }
              }else if(axis.position.startsWith("y")){
                axis.series.foreach{
                  case None=> throw new Exception(s"Error:chart.action.axis(to.${axis.position}).series error")
                  case Some(series)=>{
                    val ds = new org.jfree.data.category.DefaultCategoryDataset

                    val columnNumber = header.get(series.column)
                    (0 until records.size()).foreach(i=>{
                      val csvRecord = records.get(i)
                      ds.addValue(csvRecord.get(columnNumber).toDouble,series.name,csvRecord.get(xColumnNumber))
                    })

                    plot.setDataset(nextSeries,ds)
                    plot.setRenderer(nextSeries, series.render.value)

                    val index = if(axis.position == Axis.y1) 0
                                else if(axis.position == Axis.y2) 1
                                else throw new RuntimeException("axis should be y1 or y2")
                    plot.setRangeAxis(index,
                      {
                        val number = new NumberAxis(series.name)
                        axis.range match {
                          case None =>
                          case Some(range)=>{
                            number.setRange(range.from,range.to)
                          }
                        }
                        number
                      }
                    )
                    plot.mapDatasetToRangeAxis(nextSeries, index)

                    nextSeries += 1
                  }
                }

              }
            }
          }

        }
      }
    }

    import org.jfree.chart.plot.DatasetRenderingOrder

    plot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD)


    import org.jfree.chart.axis.CategoryLabelPositions

    // 倾斜45度
    plot.getDomainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45)

    val chart = new JFreeChart(plot)
    chart.setTitle(chartConfig.title)
    chart

  }

  def toJPG(path:String): Unit ={
    this.setSize(800, 400)
    this.setLocationRelativeTo(null)
//    this.setVisible(true)

    import org.jfree.chart.ChartUtilities
    import java.io.FileOutputStream
    val jpg = new FileOutputStream(path)
    ChartUtilities.writeChartAsJPEG(jpg, 0.9f,this.draw(), 800, 400, null)
  }
}