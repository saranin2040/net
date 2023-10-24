package ru.nsu.fit.Visual;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import ru.nsu.fit.BusinessLogic.BusinessLogic;
import ru.nsu.fit.BusinessLogic.CallBack.Wiretap;

public class LocationSelectionView implements Wiretap {

    public void initView()
    {
        if (BusinessLogic.getLocationsList()!=null)
        {
            ObservableList<String> list = FXCollections.observableArrayList();
            list.addAll(BusinessLogic.getLocationsList());

            selectionBox.getItems().clear();
            selectionBox.getItems().addAll(list);
        }
    }

    public void update()
    {
        initView();
    }
    @FXML
    protected ChoiceBox<String> selectionBox;
}
