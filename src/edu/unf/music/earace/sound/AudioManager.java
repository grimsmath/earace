/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.unf.music.earace.sound;

import edu.unf.music.earace.Util;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import javax.sound.sampled.*;

/**
 *
 * @author n00648162
 */
public class AudioManager
{
    private boolean                 _stopCapture = false;
    private ByteArrayOutputStream   _byteArrayOutputStream;
    private AudioFormat             _audioFormat;
    private TargetDataLine          _targetDataLine;
    private AudioInputStream        _audioInputStream;
    private SourceDataLine          _sourceDataLine;
    private String                  _saveFileName = "audio/recorded.wav";
    private File                    _file = null;
    private float                   _sampleRate = 44100.0f;
    private int                     _numChannels = 1;
    private int                     _sampleSizeInBits = 16;
    
	// Saves our valid list of recording devices
	private List<String>            _validRecordingDevList;
    
    // This is the main object that will get the inputs
    private Mixer.Info[]            _mixerInfo;
	private Line.Info               _targetDLInfo  = new Line.Info(TargetDataLine.class);
    
    /**
     * Default constructor
     */
    public AudioManager()
    {
        this._file = new File(this._saveFileName);
    }
    
    public void setSaveFileName(String fileName)
    {
        this._saveFileName = fileName;
    }

    public String getSaveFileName()
    {
        return this._saveFileName;
    }
    
    public boolean isCapturing()
    {
        return (this._stopCapture) ? false : true;
    }

    public boolean stopCapture()
    {
        // Stop the capture
        this._stopCapture = true;
        
        // Save the audio
        this.saveAudio();
        
        // Let the calling thread know we are done
        return this._stopCapture;
    }

    /**
     * Get a list of all the audio input devices
     * 
     * @return a List<String> object containing the list of audio input devices
     */
    public List<String> getAudioInputDevices()
    {
        List<String> returnList = new ArrayList<>();

        _mixerInfo = AudioSystem.getMixerInfo();
        
        Util.log("Available mixers:");
        
        for (int cnt = 0; cnt < _mixerInfo.length; cnt++)
        {
            Mixer currentMixer = AudioSystem.getMixer(_mixerInfo[cnt]);

            // Because this is for a recording application, we only care about audio INPUT so we just
            // care if TargetDataLine is supported.
            // currentMixer.isLineSupported(targetDLInfo) && currentMixer.isLineSupported(portInfo)
            if (currentMixer.isLineSupported(_targetDLInfo))
            {
                Util.log("mixer name: " + _mixerInfo[cnt].getName() + " index:" + cnt);
                
                returnList.add(_mixerInfo[cnt].getName());
            }
        }

        // Save the valid list of mixers
        this._validRecordingDevList = returnList;

        return returnList;
    }
    
    public String getMixers() throws LineUnavailableException
    {
        String text = "";
        
        DataLine.Info dataLineInfo = new DataLine.Info(
                TargetDataLine.class, getAudioFormat(44100.0f, 16, 1));

            // Get all the m
        for (Mixer.Info info : AudioSystem.getMixerInfo())
        {
            text += info.getName() + "\n";
            
            Mixer mix = AudioSystem.getMixer(info);
            
            
            TargetDataLine targetDataLine = 
                    (TargetDataLine) mix.getLine(dataLineInfo);
            
            text += targetDataLine.getFormat().properties().toString();
            
        }
        
        return text;
    }
    
    /**
     * This method captures audio input from a microphone and saves it in
     * a ByteArrayOutputStream object.
     */
    public void captureAudio()
    {
        try
        {
            // Get everything set up for capture
            this._audioFormat = getAudioFormat(this._sampleRate,
                                               this._sampleSizeInBits,
                                               this._numChannels);
            
            DataLine.Info dataLineInfo = new DataLine.Info(
                    TargetDataLine.class, _audioFormat);
            
            _targetDataLine = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
            _targetDataLine.open(_audioFormat);
            _targetDataLine.start();

            Thread captureThread = new Thread(new CaptureThread());
            captureThread.start();
        }
        catch (Exception e)
        {
            System.out.println(e);
            System.exit(0);
        }
    }

    /**
     * This method saves the recently capture audio
     */
    private void saveAudio()
    {
        try
        {
            byte audioData[] = _byteArrayOutputStream.toByteArray();

            InputStream byteArrayInputStream = new ByteArrayInputStream(audioData);
            
            this._audioFormat = getAudioFormat(this._sampleRate,
                                               this._sampleSizeInBits,
                                               this._numChannels);

            
            _audioInputStream = new AudioInputStream(byteArrayInputStream,
                    _audioFormat, audioData.length / _audioFormat.getFrameSize());
            
            DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class,
                    _audioFormat);
            
            _sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
            _sourceDataLine.open(_audioFormat);
            _sourceDataLine.start();

            Thread saveThread = new Thread(new SaveThread());
            saveThread.start();
        }
        catch (Exception e)
        {
            System.out.println(e);
            System.exit(0);
        }
    }

    private AudioFormat getAudioFormat(float sampleRate, 
                                       int sampleSizeInBits,
                                       int numChannels)
    {
        boolean signed = true;
        boolean bigEndian = true;
        
        return new AudioFormat(sampleRate, 
                sampleSizeInBits, numChannels, signed, bigEndian);
    }

    // Inner class to capture data from microphone
    class CaptureThread extends Thread
    {
        // An arbitrary-size temporary holding buffer
        byte tempBuffer[] = new byte[10000];

        @Override
        public void run()
        {
            _byteArrayOutputStream =  new ByteArrayOutputStream();
            _stopCapture = false;
            try
            {
                // Loop until stopCapture is set by another thread that
                // services the Stop button.
                while (! _stopCapture)
                {
                    // Read data from the internal buffer of the data line.
                    int cnt = _targetDataLine.read(tempBuffer, 0, tempBuffer.length);
                    if (cnt > 0)
                    {
                        // Save data in output stream object.
                        _byteArrayOutputStream.write(tempBuffer, 0, cnt);
                    }
                }

                _byteArrayOutputStream.close();
            }
            catch (Exception e)
            {
                System.out.println(e);
                System.exit(0);
            }
        }
    }
    
    // Inner class to play back the data that was saved.
    class SaveThread extends Thread
    {
        byte tempBuffer[] = new byte[10000];

        @Override
        public void run()
        {
            try
            {
                // Keep looping until the input read method returns -1 for
                // empty stream.
                if (AudioSystem.isFileTypeSupported(AudioFileFormat.Type.WAVE,
                                                    _audioInputStream))
                {
                    AudioSystem.write(_audioInputStream, 
                                      AudioFileFormat.Type.WAVE, _file);
                }
            }
            catch (Exception e)
            {
                System.out.println(e);
                System.exit(0);
            }
        }
    }

    /**
    * 
    * @param audioFile 
    */
    public void playAudio(String audioFile)
    {
        final int BUFFER_SIZE = 128000;
        File soundFile;
        AudioInputStream audioStream;
        SourceDataLine sourceLine;
    
        try 
        {
            soundFile = new File(audioFile);
            audioStream = AudioSystem.getAudioInputStream(soundFile);
            this._audioFormat = getAudioFormat(this._sampleRate,
                                    this._sampleSizeInBits,
                                    this._numChannels);


            DataLine.Info info = new DataLine.Info(SourceDataLine.class, _audioFormat);
            sourceLine = (SourceDataLine) AudioSystem.getLine(info);
            sourceLine.open(_audioFormat);

            sourceLine.start();

            int nBytesRead = 0;
            byte[] abData = new byte[BUFFER_SIZE];

            while (nBytesRead != -1) 
            {
                nBytesRead = audioStream.read(abData, 0, abData.length);
                if (nBytesRead >= 0) 
                {
                    int nBytesWritten = sourceLine.write(abData, 0, nBytesRead);
                }
            }

            sourceLine.drain();
            sourceLine.close();        
        } 
        catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) 
        {
            System.exit(1);
        }
    }
}