/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.unf.music.earace.yin;

import edu.unf.music.earace.Util;
import edu.unf.music.earace.gui.MainForm;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author n00648162
 */
public class PitchHandler implements Runnable
{
    private String               _fileName    = "";
    private JTable               _outputTable = null;
    private DefaultTableModel    _outputModel = null;
    
    public PitchHandler(String audioFile, JTable resultsTable)
    {
        _fileName    = audioFile;
        _outputTable = resultsTable;
        _outputModel = new DefaultTableModel();

        _outputModel.addColumn("Time");
        _outputModel.addColumn("Pitch");
        _outputModel.addColumn("Note");
        
        _outputTable.setModel(_outputModel);
    } // end PitchHandler()
    
    public void setOutputTable(JTable outTable)
    {
        _outputTable = outTable;
    }
    
    public Yin.DetectedPitchHandler PRINT_DETECTED_PITCH_HANDLER = 
            new Yin.DetectedPitchHandler() 
    {
        @Override
        public void handleDetectedPitch(float time, float pitch) 
        {
            int note = (int)(69D + (12D * Math.log(pitch / 440D)) / Math.log(2D));
            
            String logString = String.format("Time...: %15f, Pitch...: %15f, Note...: %15s", time, pitch, note);
            Util.log(logString);
            
            String timeString   = String.valueOf(time);
            String pitchString  = String.valueOf(pitch);
            String noteString   = String.valueOf(note);
            
            if (_outputTable != null)
            {
                _outputModel.addRow(new String[]{ timeString, pitchString, noteString });
            } // end if
        } // end handleDetectedPitch()
    }; // end Yin.DetectedPitchHandler
    
    @Override
    public void run()
    {
        try
        {
            Yin.processFile(_fileName, PRINT_DETECTED_PITCH_HANDLER);
        }
        catch (UnsupportedAudioFileException | IOException ex)
        {
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
        }        
    } // end run()
} // end class PitchHandler