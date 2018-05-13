import DesktopPlayer.DesktopPlayer;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;

public class Demo {
    public static void main(String[] args) throws IOException, UnsupportedAudioFileException {
        String path = "src/files/inputs/test.wav";

        Wave wave = new Wave(path);

        wave.wavelet = 39;

        wave.compress(1500);

        wave.toZipFile("src/files/outputs/test.zip");

        path = "src/files/outputs/test.zip";
        Wave song = null;
        try {
            song = new Wave(path);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        }
        song.decompress();
        song.playWave();
    }
}
