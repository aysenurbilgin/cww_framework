/*
 * Copyright (c) 2015 Aysenur Bilgin
 */

package gui;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import java.awt.*;

public class RecipeViewerWindow extends JFrame {


    private JPanel rvPanel;

    public RecipeViewerWindow(String title) throws HeadlessException {
        super(title);

        rvPanel = new JPanel(null);
        Border grayBorder = new LineBorder(Color.LIGHT_GRAY, 5);
        rvPanel.setBorder(grayBorder);
        setBounds(500,50, 1000, 800);
        setContentPane(rvPanel);

        setVisible(false);
        setDefaultCloseOperation(HIDE_ON_CLOSE);

    }


    public void addRecipeViewerSketch(RecipeViewer rvSketch)
    {
        rvPanel.setSize(rvSketch.width, rvSketch.height);
        rvPanel.setBackground(Color.LIGHT_GRAY);
        rvSketch.setBounds(0, 0, rvPanel.getWidth(), rvPanel.getHeight());
        rvPanel.add(rvSketch);
        rvSketch.init();
    }



}
