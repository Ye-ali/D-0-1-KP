package dkp.utils;

import dkp.model.DKPInstance;
import dkp.model.Item;
import dkp.model.ItemSet;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;

/**
 * 散点图绘制类
 * 用于绘制D{0-1}KP数据的散点图(重量为横轴, 价值为纵轴)
 */
public class ScatterPlot {
    
    /**
     * 为指定实例创建散点图
     * @param instance DKP实例
     * @return JFreeChart对象
     */
    public static JFreeChart createChart(DKPInstance instance) {
        XYSeriesCollection dataset = createDataset(instance);
        
        JFreeChart chart = ChartFactory.createScatterPlot(
            "D{0-1}KP Scatter Plot - " + instance.getName(),
            "Weight",
            "Profit",
            dataset,
            PlotOrientation.VERTICAL,
            true,
            true,
            false
        );
        
        // 自定义图表样式
        XYPlot plot = chart.getXYPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setDomainGridlinePaint(Color.LIGHT_GRAY);
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);
        
        // 设置坐标轴
        NumberAxis domainAxis = (NumberAxis) plot.getDomainAxis();
        domainAxis.setAutoRangeIncludesZero(true);
        
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setAutoRangeIncludesZero(true);
        
        // 设置点的大小和形状
        XYItemRenderer renderer = plot.getRenderer();
        for (int i = 0; i < dataset.getSeriesCount(); i++) {
            renderer.setSeriesPaint(i, getColor(i));
            renderer.setSeriesShape(i, new java.awt.geom.Ellipse2D.Double(-3, -3, 6, 6));
        }
        
        return chart;
    }
    
    /**
     * 创建数据集
     */
    private static XYSeriesCollection createDataset(DKPInstance instance) {
        XYSeriesCollection dataset = new XYSeriesCollection();
        
        // 为每个项集创建一个系列
        for (int i = 0; i < instance.getSetCount(); i++) {
            ItemSet itemSet = instance.getItemSet(i);
            XYSeries series = new XYSeries("ItemSet " + i);
            
            for (Item item : itemSet.getItems()) {
                series.add(item.getWeight(), item.getProfit());
            }
            
            dataset.addSeries(series);
        }
        
        return dataset;
    }
    
    /**
     * 获取颜色
     */
    private static Color getColor(int index) {
        Color[] colors = {
            Color.RED, Color.BLUE, Color.GREEN, Color.ORANGE, 
            Color.CYAN, Color.MAGENTA, Color.PINK, Color.YELLOW,
            Color.DARK_GRAY, Color.GRAY
        };
        return colors[index % colors.length];
    }
    
    /**
     * 显示散点图窗口
     */
    public static void showChart(DKPInstance instance) {
        JFreeChart chart = createChart(instance);
        
        JFrame frame = new JFrame("Scatter Plot - " + instance.getName());
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(800, 600);
        
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(800, 600));
        
        frame.setContentPane(chartPanel);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    
    /**
     * 保存散点图为图片
     */
    public static void saveChart(DKPInstance instance, String filePath) {
        try {
            JFreeChart chart = createChart(instance);
            org.jfree.chart.ChartUtils.saveChartAsPNG(
                new java.io.File(filePath), 
                chart, 
                800, 
                600
            );
        } catch (Exception e) {
            System.err.println("Error saving chart: " + e.getMessage());
        }
    }
}
