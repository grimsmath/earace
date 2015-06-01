package edu.unf.music.earace.sound;

import java.io.File;
import java.io.IOException;
import javax.sound.sampled.*;

public class AudioRecorder extends Thread
{
    private TargetDataLine          m_line;
    private AudioFileFormat.Type    m_targetType;
    private AudioInputStream        m_audioInputStream;
    private File                    m_outputFile;
    private Mixer                   m_mixer;
    
    public AudioRecorder(TargetDataLine line,
                         AudioFileFormat.Type targetType,
                         File file)
    {
        
        
        m_line              = line;
        m_audioInputStream  = new AudioInputStream(line);
        m_targetType        = targetType;
        m_outputFile        = file;
    }

    /**
     * Starts the recording. To accomplish this, (i) the line is started and
     * (ii) the thread is started.
     */
    @Override
    public void start()
    {
        /*
         * Starting the TargetDataLine. It tells the line that we now want to
         * read data from it. If this method isn't called, we won't be able to
         * read data from the line at all.
         */
        m_line.start();

        /*
         * Starting the thread. This call results in the method 'run()' (see
         * below) being called. There, the data is actually read from the line.
         */
        super.start();
    }

    /**
     * Stops the recording.
     */
    public void stopRecording()
    {
        m_line.stop();
        m_line.close();
    }

    /**
     * Main working method.
     */
    @Override
    public void run()
    {

        try
        {
            AudioSystem.write(m_audioInputStream, 
                              m_targetType, 
                              m_outputFile);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private static void closeProgram()
    {
        System.out.println("Program closing.....");
        System.exit(1);
    }

    private static void out(String strMessage)
    {
        System.out.println(strMessage);
    }
}