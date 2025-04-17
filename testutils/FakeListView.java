package testutils;

import cz.Stasak.shared.ui.ListViewInterface;

import java.util.ArrayList;
import java.util.List;

public class FakeListView<T> implements ListViewInterface<T> {
    private final List<T> items = new ArrayList<>();

    @Override
    public List<T> getItems() {
        return items;
    }
}
