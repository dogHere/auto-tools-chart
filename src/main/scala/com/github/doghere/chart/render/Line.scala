package com.github.doghere.chart.render

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
    b.setBasePositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12, TextAnchor.BASELINE_CENTER))
    b
  }


  override def toString = s"Line"
}