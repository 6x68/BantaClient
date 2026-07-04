package today.vanta.client.setting;

import today.vanta.Vanta;
import today.vanta.client.setting.impl.BooleanSetting;
import today.vanta.client.setting.impl.MultiStringSetting;
import today.vanta.client.setting.impl.NumberSetting;
import today.vanta.client.setting.impl.StringSetting;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;

public class Setting<T> {
    private final List<SettingChangeListener<T>> listeners = new ArrayList<>();
    private BooleanSupplier hidden = () -> false;
    public String name;
    private T value;

    public Setting(String name, T value) {
        this.name = name;
        this.value = value;

        if (name.matches("^(?!(?:Max|Min)\\b)[A-Z][a-zA-Z]{3,}(?:\\s+[A-Z][a-zA-Z]{3,})+$")) {
            throw new IllegalArgumentException("Invalid setting name, must only have first word starting with uppercase: " + name);
        }

        Vanta.instance.moduleStorage.context.settings.add(this);
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        T oldValue = this.value;
        this.value = value;

        for (SettingChangeListener<T> listener : listeners) {
            listener.onSettingChanged(this, oldValue, value);
        }
    }

    @SuppressWarnings("unchecked")
    public <I extends Setting<?>> I hide(BooleanSupplier hidden) {
        this.hidden = hidden;
        return (I) this;
    }

    public boolean isHidden() {
        return hidden.getAsBoolean();
    }

    public void addListener(SettingChangeListener<T> listener) {
        listeners.add(listener);
    }

    public void removeListener(SettingChangeListener<T> listener) {
        listeners.remove(listener);
    }

    public static BooleanSetting of(String name, boolean value) {
        return BooleanSetting.builder().name(name).value(value).build();
    }

    public static StringSetting of(String name, String value, String... allValues) {
        return StringSetting.builder().name(name).value(value).values(allValues).build();
    }

    public static StringSetting of(String name, int index, String... allValues) {
        return StringSetting.builder().name(name).value(allValues[index]).values(allValues).build();
    }

    public static NumberSetting of(String name, Number value, Number min, Number max, int places, String suffix) {
        return NumberSetting.builder().name(name).value(value).min(min).max(max).places(places).suffix(suffix).build();
    }

    public static NumberSetting of(String name, Number value, Number min, Number max, int places) {
        return of(name, value, min, max, places, "");
    }

    public static NumberSetting of(String name, Number value, Number min, Number max, String suffix) {
        return of(name, value, min, max, 0, suffix);
    }

    public static NumberSetting of(String name, Number value, Number min, Number max) {
        return of(name, value, min, max, 0, "");
    }

    public static MultiStringSetting of(String name, String[] value, String[] allValues) {
        return MultiStringSetting.builder().name(name).value(value).values(allValues).build();
    }
}