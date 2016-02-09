import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBoxBuilder;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Created by martingabrielsson on 2016-02-09.
 */
public class DialogHelper implements Runnable {

    private String message = "";


    public DialogHelper(String msg){
        this.message = msg;
    }

    @Override
    public void run() {

        Button button = new Button("OK!");


        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.setScene(new Scene(VBoxBuilder.create().
                children(new Text(this.message), button).
                alignment(Pos.CENTER).padding(new Insets(5)).build()));
        dialogStage.show();


        button.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.ENTER){
                dialogStage.close();
            }
        });



        button.setOnAction(event -> dialogStage.close());
    }
}
