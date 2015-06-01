/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.unf.music.earace;

import edu.unf.music.earace.gui.MainForm;

/**
 *
 * @author n00648162
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws ClassNotFoundException
    {
        Util.setNimbus();
        
        MainForm frmMain = new MainForm();

        Util.centerWindow(frmMain);
        
        frmMain.setVisible(true);
    }
}