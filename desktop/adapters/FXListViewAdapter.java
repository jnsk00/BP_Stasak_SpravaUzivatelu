package cz.Stasak.desktop.adapters;

import cz.Stasak.shared.ui.ListViewInterface;
import javafx.scene.control.ListView;

import java.util.List;

public class FXListViewAdapter<T> implements ListViewInterface<T> {

    private final ListView<T> listView;

    public FXListViewAdapter(ListView<T> listView) {
        this.listView = listView;
    }

    @Override
    public List<T> getItems() {
        return listView.getItems();
    }

    public ListView<T> getFXListView() {
        return listView;
    }
}
