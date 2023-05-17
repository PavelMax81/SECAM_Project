
import interf.RecCAInterface;
import javafx.application.Application;
import javafx.stage.Stage;


public class RecCA extends Application{
    
    @Override
    public void start(Stage primaryStage){
        RecCAInterface recCA_interface = new RecCAInterface();
    }
    
    public static void main(String[] args)
    {
        launch(args);
    }    
}

