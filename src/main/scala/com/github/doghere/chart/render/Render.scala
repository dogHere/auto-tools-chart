package com.github.doghere.chart.render

import org.jfree.chart.renderer.category.CategoryItemRenderer

trait Render{
  def value: CategoryItemRenderer
}