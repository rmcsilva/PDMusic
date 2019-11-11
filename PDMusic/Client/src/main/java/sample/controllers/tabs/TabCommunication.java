package sample.controllers.tabs;

import sample.Communication;
import sample.controllers.MainController;

public class TabCommunication extends Communication {

    private MainController mainController = null;

    protected MainController getMainController() { return mainController; }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

}
