package com.github.doghere.chart.render

import java.awt.geom.Ellipse2D
import java.awt.{BasicStroke, Color}
import java.text.DecimalFormat

import org.jfree.chart.renderer.category.LineAndShapeRenderer

class Line extends Render{
  def value: LineAndShapeRenderer = {
    val b = new LineAndShapeRenderer() // new LineAndShapeRenderer

    b.setItemMargin(.01)

    b.setBaseItemLabelsVisible(true)
    import org.jfree.chart.labels.ItemLabelAnchor
    import org.jfree.chart.labels.ItemLabelPosition
    import org.jfree.chart.labels.StandardCategoryItemLabelGenerator
    import org.jfree.ui.TextAnchor
    b.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator)
    //设置条目标签显示的位置,outline表示在条目区域外,baseline_center表示基于基线且居中
    b.setBasePositiveItemLabelPosition(
      new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12, TextAnchor.BASELINE_CENTER))
//    b.setBaseShapesVisible(false)//虚线
//    b.setSeriesStroke(0, new BasicStroke(2.0F, 1, 1, 1.0F, Array[Float](30F, 12F), 0.0F));
//    b.setBaseFillPaint(Color.WHITE)
//    import java.awt.Color
//    b.setBasePaint(Color.WHITE)
//    b.setBaseFillPaint(Color.WHITE)
//    new Ellipse2D.Double(-1,-1,1,1)

//    b.setItemLabelGenerator(
//      new StandardCategoryItemLabelGenerator("{2}%", new DecimalFormat("0.00")))
    b
  }


  override def toString = s"Line"
}