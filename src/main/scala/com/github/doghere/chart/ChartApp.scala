package com.github.doghere.chart

import java.awt.{Color, Font, GradientPaint, Paint}
import java.io.File
import java.nio.file.Paths
import java.text.DecimalFormat
import java.util
import java.util.Random
import javax.swing.{JFrame, SwingUtilities}

import com.github.doghere.chart.config._
import org.apache.commons.csv.{CSVFormat, CSVParser, CSVRecord}
import org.jfree.chart._
import org.jfree.chart.axis.{Axis => _, _}
import org.jfree.chart.plot.{CategoryPlot, PlotOrientation, XYPlot}
import com.github.doghere.chart.render._
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator
import org.jfree.chart.renderer.category.{GroupedStackedBarRenderer, StackedBarRenderer}
import org.jfree.chart.renderer.xy.{StackedXYBarRenderer, XYItemRenderer, XYLineAndShapeRenderer}
import org.jfree.data.KeyToGroupMap
import org.jfree.data.category.{CategoryDataset, DefaultCategoryDataset}
import org.jfree.data.general.AbstractDataset
import org.jfree.data.statistics.DefaultMultiValueCategoryDataset
import org.jfree.data.xy.XYSeriesCollection
import org.jfree.ui.{GradientPaintTransformType, StandardGradientPaintTransformer}

import scala.collection.mutable.ListBuffer

object ChartApp {

  def main(args: Array[String]): Unit = {
    val charts = ChartConfig.load(new File("charts/charts.xml"))
    val chart = charts.get.head.get
    chart.actions.foreach(a => a.foreach(b => b.axis.foreach(println)))
    lazy val chartManager = new ChartApp(chart)
    chartManager.toJPG(chart.to.get)

  }


}


class ChartApp(val chartConfig: Chart) extends JFrame {

  private val cSVParser = CSVParser.parse(scala.io.Source.fromFile(this.chartConfig.from.get, chartConfig.coding.get).mkString
    , CSVFormat.DEFAULT.withHeader())
  private val header = cSVParser.getHeaderMap
  private val records = cSVParser.getRecords


  private def draw(): JFreeChart = {

    if (!this.chartConfig.isComplete) {
      throw new RuntimeException("Error:chart is not completed or has error")
    }
    setTitle(this.chartConfig.title)

    var xColumnNumber = -1
    var nextSeries = 0

    val plot = new CategoryPlot()
//    val xyPlot = new XYPlot()

    this.chartConfig.actions.foreach {
      case None => throw new Exception(s"Error:chart.action is not set")
      case Some(act) => {
        if (act.operation == Action.to) {
          val groupRender = new GroupedStackedBarRenderer
//          val groupMap = new KeyToGroupMap()


          act.axis.foreach {
            case None => throw new Exception(s"Error:chart.action.axis(to) is not set")
            case Some(axis) => {
              println(axis)
              if (axis.position == "x") {
                axis.series.foreach {
                  case None => throw new RuntimeException("axis x can not be none")
                  case Some(x) => {
                    xColumnNumber = header.get(x.column)
                    plot.setDomainAxis(new CategoryAxis(x.name))
                  }
                }
              } else if (axis.position.startsWith("y")) {
                axis.series.foreach {
                  case None => throw new Exception(s"Error:chart.action.axis(to.${axis.position}).series error")
                  case Some(series) => {
                    val axixApplications = axis.applications.filter(_.isDefined).map(_.get)
                    val applications = series.applications.filter(_.isDefined).map(_.get)
                    val ds = new org.jfree.data.category.DefaultCategoryDataset

//                    import org.jfree.data.xy.XYSeriesCollection
//                    val xyDS = new XYSeriesCollection

                    val columnNumber = header.get(series.column)

                    if (applications.nonEmpty && !applications.exists(_.name == Application.format)) {

                      if (applications.exists(_.name == Application.break) &&
                        applications.exists(_.name == Application.group)) {
                        // break data
                        val breakApplication = applications.filter(_.name == Application.break).head
                        val breakApplicationColumnNumber = header.get(breakApplication.column)
                        (0 until records.size()).foreach(i => {
                          val csvRecord = records.get(i)

                          putValue(ds,csvRecord.get(columnNumber),csvRecord.get(breakApplicationColumnNumber),csvRecord.get(xColumnNumber))
//                          ds.addValue(csvRecord.get(columnNumber).toDouble
//                            , csvRecord.get(breakApplicationColumnNumber), csvRecord.get(xColumnNumber))


                        })
                        // group map
                        val groupApplication = applications.filter(_.name == Application.group).head

//                        var groupRender = new GroupedStackedBarRenderer
//                        groupRender.setItemLabelsVisible(true)
                        import org.jfree.chart.labels.StandardCategoryItemLabelGenerator
                        groupRender.setItemLabelGenerator(new StandardCategoryItemLabelGenerator)
//                        val groupRender = new StackedBarRenderer
//                        val groupRender = new StackedXYBarRenderer
                        val groupColumnNumber = header.get(groupApplication.column)
                        val groupMap = new KeyToGroupMap(records.get(0).get(groupColumnNumber))

                        (0 until records.size()).foreach(i => {
                          val csvRecord = records.get(i)
                          groupMap.mapKeyToGroup(csvRecord.get(breakApplicationColumnNumber)
                            , csvRecord.get(groupColumnNumber))
                        })
                        groupRender.setDrawBarOutline(true)
                        groupRender.setSeriesToGroupMap(groupMap)
                        groupRender.setItemMargin(0.0)
                        groupRender.setBaseItemLabelFont(new Font("宋体",Font.PLAIN,12))
                        groupRender.setItemMargin(.01)
                        groupRender.setMaximumBarWidth(0.1)
                        groupRender.setShadowVisible(true)
                        groupRender.setBaseItemLabelsVisible(true)
                        import org.jfree.chart.renderer.category.StandardBarPainter
                        groupRender.setBarPainter(new StandardBarPainter)

                        plot.getDomainAxis.setCategoryMargin(0.5)
                        plot.getDomainAxis().setCategoryLabelPositions(
                          CategoryLabelPositions.UP_45)

                        plot.setRenderer(nextSeries, groupRender)


                        val domain = plot.getDomainAxis
                        val originLabel = domain.getLabel
                        val domainAxis = new SubCategoryAxis(originLabel)
                        domainAxis.setCategoryMargin(0.05)


                        (0 until records.size()).map(i => {
                          val csvRecord = records.get(i)
                          csvRecord.get(groupColumnNumber)
                        }).toSet.foreach(s=>{
                          domainAxis.addSubCategory(s)
                        })
                        plot.setDomainAxis(domainAxis)

                        plot.setDomainGridlinesVisible(true)


                      }
                      else if (applications.exists(_.name == Application.break)) {
                        val application = applications.filter(_.name == Application.break).head
                        val applicationColumnNumber = header.get(application.column)
                        (0 until records.size()).foreach(i => {
                          val csvRecord = records.get(i)

                          putValue(ds,csvRecord.get(columnNumber),csvRecord.get(applicationColumnNumber)
                            , csvRecord.get(xColumnNumber))
//                          ds.addValue(csvRecord.get(columnNumber).toDouble
//                            , csvRecord.get(applicationColumnNumber), csvRecord.get(xColumnNumber))


                        })


//                        plot.setRenderer(nextSeries, new GroupedStackedBarRenderer)
                        groupRender.setItemLabelGenerator(new StandardCategoryItemLabelGenerator)
                        groupRender.setBaseItemLabelsVisible(true)

                        import org.jfree.chart.renderer.category.StandardBarPainter
                        groupRender.setBarPainter(new StandardBarPainter)
                        plot.setRenderer(nextSeries, groupRender)


                      }
                      else if (applications.exists(_.name == Application.group)) {
                        val application = applications.filter(_.name == Application.group).head
                        val applicationColumnNumber = header.get(application.column)
                        (0 until records.size()).foreach(i => {
                          val csvRecord = records.get(i)
                          ds.addValue(csvRecord.get(columnNumber).toDouble
                            , csvRecord.get(applicationColumnNumber), csvRecord.get(xColumnNumber))
                        })
                        plot.setRenderer(nextSeries, series.render.value)



                      }
                      else {
                        (0 until records.size()).foreach(i => {
                          val csvRecord = records.get(i)
                          putValue(ds
                            ,csvRecord.get(columnNumber)
                          ,series.name
                          , csvRecord.get(xColumnNumber))
//                          ds.addValue(csvRecord.get(columnNumber).toDouble, series.name, csvRecord.get(xColumnNumber))
                        })

                      }
                    }
                    else {
                      if(applications.exists(_.name == Application.format)){

                        (0 until records.size()).foreach(i => {
                          val csvRecord = records.get(i)
                          putValue(ds,csvRecord.get(columnNumber),
                            series.name,
                            csvRecord.get(xColumnNumber),1)
                        })
                        applications.foreach(app=>{
                           app.name match {
                             case Application.format => {
                               val value = series.render.value
                               value.setItemLabelGenerator(
                                 new StandardCategoryItemLabelGenerator("{2}", new DecimalFormat(app.column)))
                               plot.setRenderer(nextSeries, value)

                             }
                             case a =>
                           }
                        })


                      }else{
                        (0 until records.size()).foreach(i => {
                          val csvRecord = records.get(i)
                          putValue(ds,csvRecord.get(columnNumber),
                            series.name,
                            csvRecord.get(xColumnNumber))
                        })
                        plot.setRenderer(nextSeries, series.render.value)
                      }
                    }



                    plot.setDataset(nextSeries, ds)

                    val index = if (axis.position == Axis.y1) 0
                    else if (axis.position == Axis.y2) 1
                    else throw new RuntimeException("axis should be y1 or y2")
                    plot.setRangeAxis(index, {
                      val number = new NumberAxis(series.name)
                      if(axixApplications.nonEmpty){
                        axixApplications.foreach(app=>{
                          app.name match {
                            case "format"=> {
                              val pctFormat = new DecimalFormat(app.column)
                              number.setNumberFormatOverride(pctFormat)
                            }
                            case _=>
                          }
                        })
                      }

                      axis.range match {
                        case None =>
                        case Some(range) => {
                          number.setRange(range.from, range.to)
                        }
                      }
                      number
                    })
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
//    val xyPlot = chart.getXYPlot
//    val renderer = xyPlot.getRenderer();
//    renderer.setSeriesPaint(0, Color.blue)
    chart.setTitle(chartConfig.title)
    chart

  }


  private def putValue[T](ds: DefaultCategoryDataset,value:String, rowKey:Comparable[T], columnKey:Comparable[T],mul:Int=1): Unit ={
    try{
      ds.addValue(value.toDouble*mul,rowKey,columnKey)
    }catch {
      case e: Throwable =>
    }

  }

  private def percent(plot:CategoryPlot)={
    import org.jfree.chart.axis.NumberAxis
    import java.text.DecimalFormat
    val rangeAxis = plot.getRangeAxis.asInstanceOf[NumberAxis]
    val pctFormat = new DecimalFormat("#.0%")
    rangeAxis.setNumberFormatOverride(pctFormat)
    import org.jfree.chart.labels.StandardCategoryItemLabelGenerator
    import org.jfree.chart.renderer.category.CategoryItemRenderer
    import java.text.DecimalFormat
    val renderer = plot.getRenderer
    renderer.setItemLabelGenerator(
      new StandardCategoryItemLabelGenerator("{2}%", new DecimalFormat("0.00")))
  }

  def toJPG(path: String): Unit = {
    this.setSize(800, 400)
    this.setLocationRelativeTo(null)
    //    this.setVisible(true)

    import org.jfree.chart.ChartUtilities
    import java.io.FileOutputStream
    val jpg = new FileOutputStream(path)
    ChartUtilities.writeChartAsJPEG(jpg, 0.9f, this.draw(), 800, 400, null)
  }
}