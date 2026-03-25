package me.instrumentalityi.contentapi.paper.content.menus.values;

import lombok.Getter;
import me.instrumentalityi.contentapi.paper.content.menus.ContentValue;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Getter
public class EditableValues {

    private final @NotNull List<ContentValue<?>> values;

    public EditableValues(@NotNull List<ContentValue<?>> values) {
        this.values = values;
    }

    public EditableValues(@NotNull ContentValue<?>... values) {
        this.values = new CopyOnWriteArrayList<>(values);
    }

    public EditableValues() {
        this(new CopyOnWriteArrayList<>());
    }

    public void add(@NotNull ContentValue<?> value) {
        this.values.add(value);
    }

    public void add(@NotNull ContentValue<?>... values) {
        for (ContentValue<?> value : values) {
            this.add(value);
        }
    }
}
