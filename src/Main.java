import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;

public class Main extends Application {

    public Label nickLabel;
    public Label usersLabel;
    public TextField nickTextInput;
    public Button connectButton;
    public static TextField messageTextField;
    public Button sendButton;
    public HBox messageBox;
    public static ListView<String> messageList;
    public static ObservableList<String> messages;
    public static ObservableList<String> users;
    public static ListView<String> userList;
    public static ChatClient chatClient;
    public boolean connected;

    @Override
    public void start(Stage primaryStage) throws Exception{
        primaryStage.setTitle("Chat Client ");
        primaryStage.setResizable(false);
        connected = false;

        nickLabel = new Label("NICK:");
        usersLabel = new Label("USERS: ");
        nickTextInput = new TextField ();
        nickTextInput.setPrefWidth(200);
        connectButton = new Button("Connect to chat.linkura.se");
        connectButton.setPrefWidth(200);
        messageTextField = new TextField();
        messageTextField.setPrefWidth(700);
        sendButton = new Button("Send");
        sendButton.setPrefWidth(100);
        messageBox= new HBox();
        messageBox.setAlignment(Pos.BOTTOM_RIGHT);
        messageBox.getChildren().addAll(messageTextField, sendButton);
        userList = new ListView<>();
        userList.setPrefHeight(550);
        userList.setPrefWidth(200);
        users =  FXCollections.observableArrayList();
        userList.setItems(users);
        messageList = new ListView<>();
        messageList.setPrefHeight(600);
        messageList.setPrefWidth(600);
        messages = FXCollections.observableArrayList ();
        messageList.setItems(messages);

        messageList.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {


            @Override
            public ListCell<String> call(ListView<String> stringListView) {

                ListCell<String> cell = new ListCell<String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        String message = item;
                        String color = "black";
                        if(!empty && !item.isEmpty()){
                            if(item.startsWith("<COLOR:")) {
                                color = item.substring(item.indexOf(":")+1, item.indexOf(">"));
                                message = item.substring(item.indexOf(">")+1, item.length());
                            }

                            Text text = new Text(message);

                            text.wrappingWidthProperty().bind(messageList.widthProperty().subtract(10));
                            text.setStyle("-fx-fill: " + color + ";");
                            setGraphic(text);


                        }
                    }
                };
                return cell;
            }
        });



        VBox vb = new VBox();
        vb.getChildren().addAll(nickLabel, nickTextInput, connectButton, usersLabel, userList);

        VBox vb2 = new VBox();
        vb2.getChildren().addAll(messageList, messageBox);

        HBox hb = new HBox();
        hb.getChildren().addAll(vb, vb2);
        hb.setSpacing(10);

        StackPane root = new StackPane();
        root.getChildren().addAll(hb);


        chatClient = new ChatClient();


        messageTextField.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.ENTER){
                String message = messageTextField.getText();
                if(connected && messageTextField.getText().length() > 0){
                    chatClient.send(message);
                    messageTextField.setText("");
                }
            }
        });

        nickTextInput.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.ENTER){
                ConnectToggle();
            }
        });


        sendButton.setOnAction(event -> {
            String message = messageTextField.getText();
            if(connected && messageTextField.getText().length() > 0){
                chatClient.send(message);
                messageTextField.setText("");
            }
        });


        connectButton.setOnAction(event -> {
            ConnectToggle();
        });

        Scene scene = new Scene(root, 1000, 600);
        scene.getStylesheets().add("style.css");

        primaryStage.setScene(scene);
        primaryStage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }

    public void ConnectToggle(){
        String nick = nickTextInput.getText();

        if(connected){
            chatClient.disconnect();
            connected = false;
            connectButton.setText("Connect to chat.linkura.se");
        }

        else if (nick.length() > 0) {
            if(chatClient.connect(nick)){
                connected = true;
                connectButton.setText("Disconnect");
            }
        }
    }

    @Override
    public void stop() throws Exception
    {
        super.stop();

        Platform.exit();
        System.exit(0);
    }


}

