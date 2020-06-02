package sample;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

public class Controller implements Initializable {
    MediaPlayer player;

    @FXML
    private Slider timeSlider;

    @FXML
    private Label lbTimeSliderHours;

    @FXML
    private Label lbTimeSliderMinutes;

    @FXML
    private Label lbTimeSliderSeconds;

    @FXML
    private Button btnShuffle;

    @FXML
    private Button btnRepeat;

    @FXML
    private Button btnStop;

    @FXML
    private Button btnPrev;

    @FXML
    private Button btnSeekPrev;

    @FXML
    private Button btnPlay;

    @FXML
    private Button btnSeekNext;

    @FXML
    private Button btnNext;

    @FXML
    private Button btnAudio;

    @FXML
    private Slider volumeSlider;

    @FXML
    private MenuItem miOpen;

    @FXML
    private MenuItem miSave;

    @FXML
    private MenuItem miExit;

    @FXML
    private Menu miSpeed;

    @FXML
    private RadioButton rbSlowSpeed;

    @FXML
    private RadioButton rbNormalSpeed;

    @FXML
    private RadioButton rbFastSpeed;

    @FXML
    private MenuItem miBackground;

    @FXML
    private MenuItem miAbout;

    @FXML
    private MediaView mediaView;

    /**Set action cho nút open file in menuBar*/
    @FXML
    public void openFile(ActionEvent event) throws FileNotFoundException {
        if (player != null) {
            btnPlay.setGraphic(new ImageView(new Image(new FileInputStream("src/icons/play.png"))));
            player.pause();
            player.dispose();
        }
        try {
            System.out.println("Open file");
            FileChooser chooser = new FileChooser();
            File file = chooser.showOpenDialog(null);

            Media media = new Media(file.toURI().toURL().toString());
            player = new MediaPlayer(media);
            mediaView.setMediaPlayer(player);

            //set setSelected cho radio button normal
            rbNormalSpeed.setSelected(true);

            //Set mặc định khi open file sẽ play music
            player.play();
            btnPlay.setGraphic(new ImageView(new Image(new FileInputStream("src/icons/pause.png"))));

            /**time slider and volume slider (set time trên thanh slider
             * và set slider volume to nhỏ âm thanh)*/
            player.setOnReady(() -> {
                //when player gets ready (set value cho thanh time slider)
                timeSlider.setMin(0);
                timeSlider.setMax(player.getMedia().getDuration().toSeconds());
//                System.out.println(player.getMedia().getDuration().toSeconds());
                timeSlider.setValue(0);

                //audio slider (set volume hiển thị trên thanh slider)
                volumeSlider.setPrefWidth(100);
                volumeSlider.setMaxWidth(Region.USE_PREF_SIZE);
                volumeSlider.setMinWidth(30);
                volumeSlider.setValue(50);
                player.volumeProperty().bind(volumeSlider.valueProperty().divide(100));
            });

            /**listener on player (set chạy thanh slider theo thời gian của bài nhạc)*/
            player.currentTimeProperty().addListener(new ChangeListener<Duration>() {
                @Override
                public void changed(ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) {
                    Duration dur = player.getCurrentTime();
                    timeSlider.setValue(dur.toSeconds());

                    if (dur.toSeconds() == timeSlider.getMax()){
                        try {
                            btnPlay.setGraphic(new ImageView(new Image(new FileInputStream("src/icons/play.png"))));
                        } catch (FileNotFoundException fileNotFoundException) {
                            fileNotFoundException.printStackTrace();
                        }
                    }

                    //Set thời gian chạy của nhạc
//                    int value = (int) timeSlider.getValue();
//                    int seconds = 0;
//                    int minutes = (value / 60);
//                    int hours = (int) (value * 0.00027777777777778);
//
//                    lbTimeSliderSeconds.setText(String.valueOf(value));
//                    lbTimeSliderMinutes.setText(String.valueOf(minutes));
//                    lbTimeSliderHours.setText(String.valueOf(hours));

                    long value = (long) player.getCurrentTime().toSeconds();
//                    long value=System.currentTimeMillis();
//                    long value=value1-value2;
                    long hours = TimeUnit.MILLISECONDS.toHours(value);
                    value -= TimeUnit.HOURS.toMillis(hours);
                    long minutes = TimeUnit.MILLISECONDS.toMinutes(value);
                    value -= TimeUnit.MINUTES.toMillis(minutes);
                    long seconds = TimeUnit.MILLISECONDS.toSeconds(value);

                    lbTimeSliderSeconds.setText(String.valueOf(seconds));
                    lbTimeSliderMinutes.setText(String.valueOf(minutes));
                    lbTimeSliderHours.setText(String.valueOf(hours));
                }
            });

            /**time slider (thay đổi hay tời thanh thời gian của bài nhạc)*/
            timeSlider.valueProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                    if (timeSlider.isPressed()) {
                        double value = timeSlider.getValue();
                        player.seek(new Duration(value * 1000));
                    }
                }
            });

            /**set âm thanh slider audio của bài nhạc*/
            volumeSlider.valueProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                    if (volumeSlider.isPressed()) {
                        double value = volumeSlider.getValue();
                        player.setVolume(value / 100);
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * set sự kiện click cho nút button play
     */
    @FXML
    void playClick(ActionEvent event) {
        try {
            MediaPlayer.Status status = player.getStatus();

            if (status == MediaPlayer.Status.PLAYING) {
                player.pause();
                btnPlay.setGraphic(new ImageView(new Image(new FileInputStream("src/icons/play.png"))));
//            btnPlay.setText("Play");
            } else {
                player.play();
                btnPlay.setGraphic(new ImageView(new Image(new FileInputStream("src/icons/pause.png"))));
//                btnPlay.setText("Pause");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * set sự kiện click cho nút button previous lùi bài hát về trước
     */
    @FXML
    void prevClick(ActionEvent event) {

    }

    /**
     * set sự kiện click cho nút button next bài hát kế tiếp
     */
    @FXML
    void nextClick(ActionEvent event) {

    }

    /**
     * set sự kiện click cho nút button seek next tời bài hát về sau 10s
     */
    @FXML
    void seekNext(ActionEvent event) {
        double duration = player.getCurrentTime().toSeconds();
        duration += 10;
        player.seek(new Duration(duration * 1000));
    }

    /**
     * set sự kiện click cho nút button seek previous tời bài hát về trước 10s
     */
    @FXML
    void seekPrev(ActionEvent event) {
        double duration = player.getCurrentTime().toSeconds();
        duration -= 10;
        player.seek(new Duration(duration * 1000));
    }

    /**
     * Set button mute click tắt bật âm thanh
     */
    @FXML
    void audioClick(ActionEvent event) {
        try {
            if (!player.isMute()) {
                player.setMute(true);
                btnAudio.setGraphic(new ImageView(new Image(new FileInputStream("src/icons/unaudio.png"))));

            } else {
                player.setMute(false);
                btnAudio.setGraphic(new ImageView(new Image(new FileInputStream("src/icons/audio.png"))));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * set sự kiện click cho nút button repeat, lặp lại 1 bài hát đang playing
     */
    private boolean checkRepeat = true;
    @FXML
    void repeatClick(ActionEvent event) {
        try {
            if (checkRepeat){
                btnRepeat.setBackground(new Background(new BackgroundFill(Color.BLUE, CornerRadii.EMPTY, Insets.EMPTY)));
                player.setCycleCount(MediaPlayer.INDEFINITE);
                checkRepeat = !checkRepeat;
                System.out.println("Repeat is on");
            }else
            {
                btnRepeat.setBackground(new Background(new BackgroundFill(null, CornerRadii.EMPTY, Insets.EMPTY)));
//                btnRepeat.setBackground(null);
                player.setCycleCount(1);
                checkRepeat =!checkRepeat;
                System.out.println("Repeat is off");
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    /**
     * set sự kiện click cho nút button shuffle, chọn bài hát kế tiếp ngẫu nhiên
     */
    @FXML
    void shuffleClick(ActionEvent event) {

    }

    /**
     * set sự kiện click cho nút button stop, dừng trình phát nếu đang playing
     */
    @FXML
    void stopClick(ActionEvent event) {
        try {
//            player.seek(Duration.ZERO);
            player.stop();
            btnPlay.setGraphic(new ImageView(new Image(new FileInputStream("src/icons/play.png"))));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * set sự kiện click cho nút menuBar exit, đóng trình phát
     */
    @FXML
    void exitClick(ActionEvent event) {
        try {
            Alert alertExit = new Alert(Alert.AlertType.CONFIRMATION);
            alertExit.setTitle("Confirmation");
            alertExit.setHeaderText("Do you want to close ?");
//                alertExit.setContentText("Choose your option");

            ButtonType buttonTypeYes = new ButtonType("Yes", ButtonBar.ButtonData.YES);
            ButtonType buttonTypeNo = new ButtonType("No", ButtonBar.ButtonData.NO);

            alertExit.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);
            Optional<ButtonType> result = alertExit.showAndWait();

            if (result.get() == buttonTypeYes) {
                System.exit(0);
            } else {
                player.stop();
                btnPlay.setGraphic(new ImageView(new Image(new FileInputStream("src/icons/play.png"))));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * set sự kiện click cho nút menuBar about, hiển thị thông tin trình phát nhạc
     */
    @FXML
    void aboutClick(ActionEvent event) {
        try {
            Stage window = new Stage();
            StackPane layout = new StackPane();

            //set player pause nếu mở about menu
            if (player != null) {
                player.pause();
                btnPlay.setGraphic(new ImageView(new Image(new FileInputStream("src/icons/play.png"))));
            }

            File fileLogo = new File("src/icons/logo.png");
            File fileAboutMp3 = new File("src/icons/codegyminfo1.png");
            Image imageAboutMp3 = new Image(fileAboutMp3.toURI().toString());
//            Label label=new Label("Key Study: Ứng Dụng nghe nhạc Mp3 Học viên: Nguyễn Văn Đạt");

//            layout.setBackground(new Background(new BackgroundFill(Color.BLUE, CornerRadii.EMPTY, Insets.EMPTY)));
            layout.setBackground(new Background(new BackgroundImage(imageAboutMp3, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT)));
//            layout.getChildren().add(label);
            Scene scene = new Scene(layout, 450, 346);

            window.setTitle("About Windows Media Player");
            window.getIcons().add(new Image(fileLogo.toURI().toString()));
            window.setScene(scene);
            window.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void slowSpeedClick(ActionEvent event) {
        if (rbSlowSpeed.isSelected()){
            rbNormalSpeed.setSelected(false);
            rbFastSpeed.setSelected(false);
            player.setRate(0.5);
        }
    }

    @FXML
    void normalSpeedClick(ActionEvent event) {
        if (rbNormalSpeed.isSelected()){
            rbSlowSpeed.setSelected(false);
            rbFastSpeed.setSelected(false);
            player.setRate(1);
        }
    }

    @FXML
    void fastSpeedClick(ActionEvent event) {
        if (rbFastSpeed.isSelected()){
            rbSlowSpeed.setSelected(false);
            rbNormalSpeed.setSelected(false);
            player.setRate(1.5);
        }
    }

    /**
     * Gán icon cho các button, menuBar,...
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            btnPlay.setGraphic(new ImageView(new Image(new FileInputStream("src/icons/play.png"))));
            btnPrev.setGraphic(new ImageView(new Image(new FileInputStream("src/icons/prev.png"))));
            btnNext.setGraphic(new ImageView(new Image(new FileInputStream("src/icons/next.png"))));
            btnShuffle.setGraphic(new ImageView(new Image(new FileInputStream("src/icons/shuffle.png"))));
            btnRepeat.setGraphic(new ImageView(new Image(new FileInputStream("src/icons/repeat.png"))));
            btnStop.setGraphic(new ImageView(new Image(new FileInputStream("src/icons/stop.png"))));
            btnAudio.setGraphic(new ImageView(new Image(new FileInputStream("src/icons/audio.png"))));
            btnSeekNext.setGraphic(new ImageView(new Image(new FileInputStream("src/icons/last.png"))));
            btnSeekPrev.setGraphic(new ImageView(new Image(new FileInputStream("src/icons/first.png"))));
            miOpen.setGraphic(new ImageView(new Image(new FileInputStream("src/icons/openfile.png"))));
            miSave.setGraphic(new ImageView(new Image(new FileInputStream("src/icons/save.png"))));
            miExit.setGraphic(new ImageView(new Image(new FileInputStream("src/icons/exit.png"))));
            miAbout.setGraphic(new ImageView(new Image(new FileInputStream("src/icons/about.png"))));

        } catch (FileNotFoundException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
        }
    }

    /***/
    public void setKeyCombination(){
//        miExit.setAccelerator(KeyCombination.keyCombination("Ctrl+E"));
//        miExit.setOnAction(new EventHandler<ActionEvent>() {
//            @Override
//            public void handle(ActionEvent event) {
//                System.exit(0);
//            }
//        });

        miOpen.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Controller controller=new Controller();
//                controller.openFile(EventHandler e);
            }
        });

        miOpen.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN));
    }
}
