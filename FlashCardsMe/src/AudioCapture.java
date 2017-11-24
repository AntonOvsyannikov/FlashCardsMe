import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.*;



public class AudioCapture implements Runnable {
    final int bufSize = 16384;
    AudioInputStream audioInputStream;
    double duration, seconds;
    AudioFormat format;
    
    boolean stopCapturing;

    public AudioCapture() {
        
        AudioFormat.Encoding encoding = AudioFormat.Encoding.PCM_SIGNED;
        float rate = 44100.0f;
        int channels = 2;
        int frameSize = 4;
        int sampleSize = 16;
        boolean bigEndian = true;
        format = new AudioFormat(encoding, rate, sampleSize, channels, (sampleSize / 8) * channels, rate, bigEndian);
    }
    
    void rec() {
        Thread t = new Thread(this);
        t.start();
    }
    
    void play() {
        stopCapturing = true;
    }
    
    public void run() {
        stopCapturing = false;
        runCapturing();
        runPlayback();
    }

    void runPlayback() {
        if (audioInputStream != null ) try {
            audioInputStream.reset();

            AudioInputStream playbackInputStream = AudioSystem.getAudioInputStream(format, audioInputStream);
            if (playbackInputStream == null) throw new IllegalArgumentException();

            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
            if (!AudioSystem.isLineSupported(info)) throw new IllegalArgumentException();

            SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
            line.open(format, bufSize);

            int frameSizeInBytes = format.getFrameSize();
            int bufferLengthInFrames = line.getBufferSize() / 8;
            int bufferLengthInBytes = bufferLengthInFrames * frameSizeInBytes;
            byte[] data = new byte[bufferLengthInBytes];
            int numBytesRead = 0;

            line.start();

//            while (thread != null) {
            while (true) {
                if ((numBytesRead = playbackInputStream.read(data)) == -1) break;
                int numBytesRemaining = numBytesRead;
                while (numBytesRemaining > 0) numBytesRemaining -= line.write(data, 0, numBytesRemaining);
            }
//            if (thread != null) {
            line.drain();
//            }
            line.stop();
            line.close();
            line = null;
        } catch (Exception ex) {
            Logger.getLogger(AudioCapture.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        } 
    }

    void runCapturing() {
        try {
            duration = 0;
            audioInputStream = null;

            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

            if (!AudioSystem.isLineSupported(info)) throw new IllegalArgumentException();

            TargetDataLine line = (TargetDataLine) AudioSystem.getLine(info);
            line.open(format, line.getBufferSize());

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int frameSizeInBytes = format.getFrameSize();
            int bufferLengthInFrames = line.getBufferSize() / 8;
            int bufferLengthInBytes = bufferLengthInFrames * frameSizeInBytes;
            byte[] data = new byte[bufferLengthInBytes];
            int numBytesRead;

            line.start();

            while (!stopCapturing) {
                if ((numBytesRead = line.read(data, 0, bufferLengthInBytes)) == -1) break;
                out.write(data, 0, numBytesRead);
            }

            line.stop();
            line.close();
            line = null;

            out.flush();
            out.close();

            byte audioBytes[] = out.toByteArray();
            ByteArrayInputStream bais = new ByteArrayInputStream(audioBytes);
            audioInputStream = new AudioInputStream(bais, format, audioBytes.length / frameSizeInBytes);

            long milliseconds = (long) ((audioInputStream.getFrameLength() * 1000) / format.getFrameRate());
            duration = milliseconds / 1000.0;

            audioInputStream.reset();

        } catch (Exception ex) {
            Logger.getLogger(AudioCapture.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        } 
    }
}