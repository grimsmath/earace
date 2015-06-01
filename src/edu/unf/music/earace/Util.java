/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.unf.music.earace;

import edu.unf.music.earace.gui.MainForm;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author n00648162
 */
public class Util
{
    public static void log(String message)
    {
        System.out.println(message);
    }
    
    /**
     * 
     */
    public static void setNimbus()
    {
        try
        {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels())
            {
                if ("Nimbus".equals(info.getName()))
                {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        }
        catch (ClassNotFoundException | 
                InstantiationException | 
                IllegalAccessException | 
                javax.swing.UnsupportedLookAndFeelException ex)
        {
            java.util.logging.Logger.getLogger(MainForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }        
    }
    
    public static void showErrorBox(String message, String title)
    {
        JOptionPane.showMessageDialog(null, 
                                      message, 
                                      title, 
                                      JOptionPane.ERROR_MESSAGE);
    }
    
    public static boolean fileExists(String fileName)
    {
        File myFile = new File(fileName);
        return myFile.exists();
    }
    
    /**
     * 
     * @param title
     * @return 
     */
    public static String openWaveFileDialog(String title)
            throws FileNotFoundException, IOException
    {
        String                  text    = "";
        JFileChooser            dlg     = new JFileChooser();
        FileNameExtensionFilter filter  = new FileNameExtensionFilter("Canonical WAVE Files", "wav");
        
        dlg.setDialogTitle(title);
        dlg.setMultiSelectionEnabled(false);
        dlg.setCurrentDirectory(null);
        dlg.setFileFilter(filter);
        
        if (dlg.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
        {
            try
            {
                text = dlg.getSelectedFile().getCanonicalPath();
            }
            catch (IOException ex)
            {
                Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        return text;
    }
    
    /**
     * 
     * @param frame 
     */
    public static void centerWindow(JFrame frame)
    {
        // Get the size of the screen
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();

        // Determine the new location of the window
        int w = frame.getSize().width;
        int h = frame.getSize().height;
        int x = (dim.width-w)/2;
        int y = (dim.height-h)/2;

        // Move the window
        frame.setLocation(x, y);
    }
    
    /**
     * 
     * @return 
     */
    public static String getLookAndFeels()
    {
        String text = "";
        
        for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels())
        {
            text += info.getClassName() + "\n";
        }
        
        return text;
    }
    
    /**
     * 
     * @return
     * @throws FileNotFoundException
     * @throws IOException 
     */
//    public static String getLastOpenLocation() 
//            throws FileNotFoundException, IOException
//    {
//        String output = "";
//        
//        Properties props = new Properties();
//        try (FileInputStream in = new FileInputStream("defaultProperties"))
//        {
//            props.load(in);
//            
//            output = props.getProperty("LastOpenPath");
//        }
//
//        return output;
//    }
//    
//    /**
//     * 
//     * @param path
//     * @throws FileNotFoundException
//     * @throws IOException 
//     */
//    public static void saveLastOpenLocation(String path) 
//            throws FileNotFoundException, IOException
//    {
//        Properties props = new Properties();
//        props.setProperty("LastOpenPath", path);
//        try (FileOutputStream out = new FileOutputStream("defaultProperties"))
//        {
//            props.store(out, "---No Comment---");
//        }
//    }
}
