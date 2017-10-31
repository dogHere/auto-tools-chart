package com.github.doghere.chart

import java.awt.{Color, GradientPaint, Paint}
import java.io.File
import java.nio.file.Paths
import java.util
import java.util.Random
import javax.swing.{JFrame, SwingUtilities}

import com.github.doghere.chart.config._
import org.apache.commons.csv.{CSVFormat, CSVParser, CSVRecord}
import org.jfree.chart.{ChartFactory, ChartPanel, ChartUtilities, JFreeChart, LegendItemCollection}
import org.jfree.chart.axis.{CategoryAxis, NumberAxis, SubCategoryAxis}
import org.jfree.chart.plot.{CategoryPlot, PlotOrientation}
import com.github.doghere.chart.render._
import org.jfree.chart.renderer.category.GroupedStackedBarRenderer
import org.jfree.data.KeyToGroupMap
import org.jfree.data.category.DefaultCategoryDataset
import org.jfree.data.statistics.DefaultMultiValueCategoryDataset
import org.jfree.ui.{GradientPaintTransformType, StandardGradientPaintTransformer}

import scala.collection.mutable.ListBuffer

object ChartApp  {

  def main(args: Array[String]): Unit = {
    val charts = ChartConfig.load(new File("charts/charts.xml"))
    val chart = charts.get.head.get
    chart.actions.foreach(a=>a.foreach(b=>b.axis.foreach(println)))
    lazy val chartManager = new ChartApp(chart)
    chartManager.toJPG(chart.to.get)

  }


}


class ChartApp(val chartConfig:Chart) extends JFrame{

  private val cSVParser = CSVParser.parse(scala.io.Source.fromFile(this.chartConfig.from.get,chartConfig.coding.get).mkString
    ,CSVFormat.DEFAULT.withHeader())
  private val header = cSVParser.getHeaderMap
  private val records = cSVParser.getRecords


  def getSeries(axisName:String): List[Series] ={
    val ses = ListBuffer[Series]()

    this.chartConfig.actions.foreach {
      case None =>  throw new Exception(s"Error:chart.action is not set")
      case Some(act) => {
        if (act.operation == Action.to) {
          act.axis.foreach {
            case None =>  throw new Exception(s"Error:chart.action.axis(to) is not set")
            case Some(axis)=>{
              if(axis.position==axisName) axis
                .series.filter(s=> s.isDefined).foreach(k=>ses+=k.get)
            }
          }
        }
      }
    }
    ses.toList
  }

  def getAxises: List[Axis] ={
    val as = ListBuffer[Axis]()

    this.chartConfig.actions.foreach {
      case None =>  throw new Exception(s"Error:chart.action is not set")
      case Some(act) =>
        act.axis.foreach {
          case None =>
          case Some(ta) =>
            as += ta
        }
    }
    as.toList
  }

  private val axises: List[Axis] = getAxises

  private val xSeries: List[Series] = getSeries("x")
  private val mainXS: Series = xSeries.filter(_.level==0).head
  private val mainXSDataColumn: String = mainXS.column
  private val mainXSDataColumnNumber: Integer = header.get(mainXSDataColumn)


  private val ySeries:List[Series] = getSeries(Axis.y1):::getSeries(Axis.y2)
  private val mainYS: Series = ySeries.filter(_.level==0).head
  private val mainYSDataColumn: String = mainYS.column
  private val mainYSDataColumnNumber: Integer = header.get(mainYSDataColumn)

  private def createData: DefaultCategoryDataset ={

    // map level 0
    // load by group
    val data: DefaultCategoryDataset = new DefaultCategoryDataset
    (0 until records.size()).foreach(i=>{
      // map  yMain|id|xMain
      val record = records.get(i)
      data.addValue(record.get(mainYSDataColumnNumber).toDouble,i,record.get(mainXSDataColumnNumber))
    })


    data

  }

  private def mapKeyGroup:KeyToGroupMap ={

    // map level 1
    val map = new KeyToGroupMap()
    val xs1 = xSeries.filter(_.level==1).head
    val xs1Column = xs1.column
    val xs1ColumnNumber = header.get(xs1Column)

    (0 until records.size()).foreach(i=>{
      val record = records.get(i)
      map.mapKeyToGroup(i,record.get(xs1ColumnNumber))
    })
    map


  }

  private def getLcon(level:Int):util.HashSet[String] ={
    val set = new util.HashSet[String]()
    val xs1 = xSeries.filter(_.level==level).head
    val xs1Column = xs1.column
    val xs1ColumnNumber = header.get(xs1Column)

    (0 until records.size()).foreach(i=>{
      val record = records.get(i)
      set.add(record.get(xs1ColumnNumber))
    })
    set
  }

  private def randomColor():Color ={
    val rand = new Random()
    val randomColor = new Color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat())
    randomColor
    new Color(0x22, 0xFF, 0x22)
  }

  private def randomPaint():GradientPaint = {
    new GradientPaint(0.0f, 0.0f, randomColor(), 0.0f, 0.0f, randomColor())
  }

  private def taget(renderer:GroupedStackedBarRenderer):GroupedStackedBarRenderer ={

    val l1Con = getLcon(1)
    val l2Con = getLcon(2)
    val paints = (0 until l2Con.size()).map(_=>randomPaint()).toSet.toList

    // map level 2
    (0 until l1Con.size()).foreach(i=>{
      (0 until l2Con.size()).foreach(i2=>{
        renderer.setSeriesPaint(i,paints(i2))
      })
    })
    renderer
  }

  private def draw2():JFreeChart = {
    if(!this.chartConfig.isComplete){
      throw new RuntimeException("Error:chart is not completed or has error")
    }
    setTitle(this.chartConfig.title)

    val chart = ChartFactory.createStackedBarChart("Stacked Bar Chart Demo 4", // chart title
      "Category", // domain axis label
      "Value", // range axis label
      createData, // data
      PlotOrientation.VERTICAL, // the plot orientation
      true, // legend
      true, // tooltips
      false // urls)
    )


    var renderer = new GroupedStackedBarRenderer
    val map = mapKeyGroup

    renderer.setSeriesToGroupMap(map)
    renderer.setItemMargin(0.0)
//    renderer=taget(renderer)





    val l1Con = getLcon(1)
    val l2Con = getLcon(2)
    val paints = (0 until l2Con.size()).map(_=>randomPaint()).toSet.toList

    println(l1Con.size())
    println(l2Con.size())
    // map level 2
    (0 until l1Con.size()).foreach(i=>{
      (0 until l2Con.size()).foreach(i2=>{
        println(i*l2Con.size() +"  " + i2)
        renderer.setSeriesPaint(i*l2Con.size() +i2,paints(i2))
      })
    })







    renderer.setGradientPaintTransformer(new StandardGradientPaintTransformer(GradientPaintTransformType.HORIZONTAL))


    val domainAxis = new SubCategoryAxis("Product / Month")
    domainAxis.setCategoryMargin(0.05)

    getLcon(1).forEach(l=>domainAxis.addSubCategory(l))

    val plot = chart.getPlot.asInstanceOf[CategoryPlot]
    plot.setDomainAxis(domainAxis)
    plot.setRenderer(renderer)
    plot.setFixedLegendItems(new LegendItemCollection)

//    val chart = new JFreeChart(plot)

    chart.setTitle(chartConfig.title)
    chart

  }


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
          val groupRender = new GroupedStackedBarRenderer
          val groupMap = new KeyToGroupMap()

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
    ChartUtilities.writeChartAsJPEG(jpg, 0.9f,this.draw2(), 800, 400, null)
  }
}