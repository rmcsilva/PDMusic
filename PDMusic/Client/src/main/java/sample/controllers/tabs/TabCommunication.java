package sample.controllers.tabs;

import sample.controllers.MainController;

public class TabCommunication {

    private MainController mainController = null;

    protected MainController getMainController() { return mainController; }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

}
