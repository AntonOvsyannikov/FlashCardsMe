/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Components;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import javafx.scene.text.Font;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.data.general.DefaultPieDataset;

/**
 *
 * @author Administrator
 */
public class JMyPanel extends JPanel {
    public JMyPanel(){
        super();
        // Create a simple pie chart
        DefaultPieDataset pieDataset = new DefaultPieDataset();
        pieDataset.setValue("A", new Integer(75));
        pieDataset.setValue("B", new Integer(10));
        pieDataset.setValue("C", new Integer(10));
        pieDataset.setValue("D", new Integer(5));
        JFreeChart chart = ChartFactory.createPieChart (
            "CSC408 Mark Distribution", // Title
            pieDataset, // Dataset
            true, // Show legend
            true, // Use tooltips
            false // Configure chart to generate URLs?
        );

        setLayout(new java.awt.BorderLayout());
        ChartPanel CP = new ChartPanel(chart);
//        add(CP,BorderLayout.CENTER);
        add(new JLabel("test"),BorderLayout.CENTER);
        validate();
    }
/*
    String stacks[] = {"Hardest", "Hard", "Other", "Easy", "Learned"};
    
    
    public void paint( Graphics g ) {
        super.paint(g);
        Dimension d = getSize();
        Insets ins = getInsets();
        
//        Line2D line = new Line2D.Double(i.left,i.top,d.width-i.right-1,d.height-i.bottom-1);
        
        Graphics2D g2 = (Graphics2D) g;

        // set area around graph
        ins.left += 50;
        ins.top += 50;
        ins.right +=50;
        ins.bottom +=50;
        
        // calculate width of vertical axis area
        FontMetrics fm = g.getFontMetrics();
//        Rectangle2D r2= fm.getStringBounds("TEXT", g2);
//        int h = (int)r2.getHeight();
        int hText = fm.getAscent()+fm.getDescent();
        int maxWidth = 0;
       
        for (int i=0; i < stacks.length; i++) {
            int sw = fm.stringWidth(stacks[i]);
            maxWidth = maxWidth>sw?maxWidth:sw; 
        }
        
        // define vertical axis rectangle
        int insetText = 10;
        int serifSize = 5;
        int axisWidth = 1;
        Rectangle rectVAxis = new Rectangle(ins.left, ins.top+hText/2, maxWidth+insetText+serifSize+axisWidth, d.height - ins.top - ins.bottom - hText/2);

        // draw vertical axis
        g2.drawLine(rectVAxis.x+rectVAxis.width-1, rectVAxis.y, rectVAxis.x+rectVAxis.width-1, rectVAxis.y+rectVAxis.height);
        for (int i=0; i<stacks.length; i++) {
            int hy = rectVAxis.height*(stacks.length-i-1)/(stacks.length-1);
            g2.drawLine(rectVAxis.x+rectVAxis.width-1, rectVAxis.y + hy, rectVAxis.x+rectVAxis.width-1-serifSize, rectVAxis.y + hy);
            String s = stacks[i];
            
            Rectangle2D r = fm.getStringBounds(s, g2);
            
            g2.drawString(s, rectVAxis.x+rectVAxis.width-1-insetText-(int)r.getWidth(), rectVAxis.y + hy + fm.getAscent()/2);
            
        }
        
        // define horizontal axis rectangle
        Rectangle rectHAxis = new Rectangle(ins.left + rectVAxis.width, rectVAxis.y+rectVAxis.height/2, d.width-ins.left-ins.right, hText/2);
        
        
    }
*/
}
