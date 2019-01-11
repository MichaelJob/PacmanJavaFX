package pmgame;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.net.URL;

public class Sound {

    private URL waka = getClass().getResource("/wakawaka.mp3");
    private URL PMdies = getClass().getResource("/pacmandies.mp3");
    private MediaPlayer mediaPlayerWaka = new MediaPlayer(new Media(waka.toString()));
    private MediaPlayer mediaPlayerPMdies = new MediaPlayer(new Media(PMdies.toString()));

    public void playWaka() {
        mediaPlayerWaka.setVolume(0.005);
        mediaPlayerWaka.play();
        mediaPlayerWaka.setOnEndOfMedia(() -> mediaPlayerWaka.seek(Duration.ZERO));
    }

    public void stopWaka() {
        mediaPlayerWaka.stop();
    }

    public void playPMdies() {
        mediaPlayerPMdies.stop();
        mediaPlayerPMdies.setVolume(0.3);
        mediaPlayerPMdies.play();
    }


}
