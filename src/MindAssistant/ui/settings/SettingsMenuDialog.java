package MindAssistant.ui.settings;

import MindAssistant.MindVars;
import MindAssistant.graphics.Render;
import MindAssistant.graphics.draw.BaseDrawer;
import arc.Core;
import arc.func.Boolc;
import arc.func.Cons;
import arc.graphics.Color;
import arc.math.geom.Vec2;
import arc.scene.Element;
import arc.scene.event.Touchable;
import arc.scene.ui.*;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import arc.util.*;
import mindustry.Vars;
import mindustry.ui.Styles;

import static MindAssistant.MindVars.settings;
import static arc.Core.bundle;
import static mindustry.Vars.mobile;

/**
 * Set up enable status and running parameters for some features
 *
 * @author wshon
 */
public class SettingsMenuDialog {
    private final String menu_name = "mind-assistant-settings";
    private final Table menu;
    private final Table prefs;
    private final SettingsTable game;

    public SettingsMenuDialog() {
        menu = Reflect.get(Vars.ui.settings, "menu");
        prefs = Reflect.get(Vars.ui.settings, "prefs");

        game = new SettingsTable();

        menu.update(() -> {
            if (menu.find(menu_name) == null) {
                this.addMenu();
            }
        });

        addSettings();
    }

    private void addMenu() {
        menu.row();
        menu.button("MindAssistant", Styles.cleart, () -> {
            prefs.clearChildren();
            prefs.add(game);
        }).name(menu_name);
    }

    void addSettings() {
        if (!mobile) {
            game.checkPref("enableAutoShoot", true, (v) -> {
                MindVars.smartDesktopInput.toggle(v);
            });
            game.checkPref("enableAutoTarget", false);
        }
        BaseDrawer.allDrawer.each((d) -> {
            game.checkPref("enable" + d.getDrawerName(), true, (v) -> {
                Render.loadEnabled();
            });
            d.setPrefTo(game);
        });
    }

    public static class SettingsTable extends Table {
        protected Seq<Setting> list = new Seq<>();

        public SettingsTable() {
            left();
        }

        public Seq<Setting> getSettings() {
            return list;
        }

        public void pref(Setting setting) {
            list.add(setting);
            rebuild();
        }

        public SliderSetting sliderPref(String name, int def, int min, int max, mindustry.ui.dialogs.SettingsMenuDialog.StringProcessor s) {
            return sliderPref(name, def, min, max, 1, s);
        }

        public SliderSetting sliderPref(String name, int def, int min, int max, int step, mindustry.ui.dialogs.SettingsMenuDialog.StringProcessor s) {
            SliderSetting res;
            list.add(res = new SliderSetting(name, def, min, max, step, s));
            settings.defaults(name, def);
            rebuild();
            return res;
        }

        public void checkPref(String name, boolean def) {
            list.add(new CheckSetting(name, def, null));
            settings.defaults(name, def);
            rebuild();
        }

        public void checkPref(String name, boolean def, Boolc changed) {
            list.add(new CheckSetting(name, def, changed));
            settings.defaults(name, def);
            rebuild();
        }

        public void textPref(String name, String def) {
            list.add(new TextSetting(name, def, null));
            settings.defaults(name, def);
            rebuild();
        }

        public void textPref(String name, String def, Cons<String> changed) {
            list.add(new TextSetting(name, def, changed));
            settings.defaults(name, def);
            rebuild();
        }

        public void areaTextPref(String name, String def) {
            list.add(new AreaTextSetting(name, def, null));
            settings.defaults(name, def);
            rebuild();
        }

        public void areaTextPref(String name, String def, Cons<String> changed) {
            list.add(new AreaTextSetting(name, def, changed));
            settings.defaults(name, def);
            rebuild();
        }

        public void rebuild() {
            clearChildren();

            for (Setting setting : list) {
                setting.add(this);
            }

            button(bundle.get("settings.reset", "Reset to Defaults"), () -> {
                for (Setting setting : list) {
                    if (setting.name == null || setting.title == null) continue;
                    settings.put(setting.name, settings.getDefault(setting.name));
                }
                rebuild();
            }).margin(14).width(240f).pad(6);
        }

        public abstract static class Setting {
            public String name;
            public String title;
            public @Nullable String description;

            public Setting(String name) {
                this.name = name;
                String winkey = "setting." + name + ".name.windows";
                title = OS.isWindows && bundle.has(winkey) ? bundle.get(winkey) : bundle.get("mind-assistant.setting." + name + ".name");
                description = bundle.getOrNull("setting." + name + ".description");
            }

            public abstract void add(SettingsTable table);

            public void addDesc(Element elem) {
                if (description == null) return;

                elem.addListener(new Tooltip(t -> t.background(Styles.black8).margin(4f).add(description).color(Color.lightGray)) {
                    {
                        allowMobile = true;
                    }

                    @Override
                    protected void setContainerPosition(Element element, float x, float y) {
                        this.targetActor = element;
                        Vec2 pos = element.localToStageCoordinates(Tmp.v1.set(0, 0));
                        container.pack();
                        container.setPosition(pos.x, pos.y, Align.topLeft);
                        container.setOrigin(0, element.getHeight());
                    }
                });
            }
        }

        public static class CheckSetting extends Setting {
            boolean def;
            Boolc changed;

            public CheckSetting(String name, boolean def, Boolc changed) {
                super(name);
                this.def = def;
                this.changed = changed;
            }

            @Override
            public void add(SettingsTable table) {
                CheckBox box = new CheckBox(title);

                box.update(() -> box.setChecked(settings.getBool(name)));

                box.changed(() -> {
                    settings.put(name, box.isChecked());
                    if (changed != null) {
                        changed.get(box.isChecked());
                    }
                });

                box.left();
                addDesc(table.add(box).left().padTop(3f).get());
                table.row();
            }
        }

        public static class SliderSetting extends Setting {
            int def, min, max, step;
            mindustry.ui.dialogs.SettingsMenuDialog.StringProcessor sp;

            public SliderSetting(String name, int def, int min, int max, int step, mindustry.ui.dialogs.SettingsMenuDialog.StringProcessor s) {
                super(name);
                this.def = def;
                this.min = min;
                this.max = max;
                this.step = step;
                this.sp = s;
            }

            @Override
            public void add(SettingsTable table) {
                Slider slider = new Slider(min, max, step, false);

                slider.setValue(settings.getInt(name));

                Label value = new Label("", Styles.outlineLabel);
                Table content = new Table();
                content.add(title, Styles.outlineLabel).left().growX().wrap();
                content.add(value).padLeft(10f).right();
                content.margin(3f, 33f, 3f, 33f);
                content.touchable = Touchable.disabled;

                slider.changed(() -> {
                    settings.put(name, (int) slider.getValue());
                    value.setText(sp.get((int) slider.getValue()));
                });

                slider.change();

                addDesc(table.stack(slider, content).width(Math.min(Core.graphics.getWidth() / 1.2f, 460f)).left().padTop(4f).get());
                table.row();
            }
        }

        public static class TextSetting extends Setting {
            String def;
            Cons<String> changed;

            public TextSetting(String name, String def, Cons<String> changed) {
                super(name);
                this.def = def;
                this.changed = changed;
            }

            @Override
            public void add(SettingsTable table) {
                TextField field = new TextField();

                field.update(() -> field.setText(settings.getString(name)));

                field.changed(() -> {
                    settings.put(name, field.getText());
                    if (changed != null) {
                        changed.get(field.getText());
                    }
                });

                Table prefTable = table.table().left().padTop(3f).get();
                prefTable.add(field);
                prefTable.label(() -> title);
                addDesc(prefTable);
                table.row();
            }
        }

        public static class AreaTextSetting extends TextSetting {
            public AreaTextSetting(String name, String def, Cons<String> changed) {
                super(name, def, changed);
            }

            @Override
            public void add(SettingsTable table) {
                TextArea area = new TextArea("");
                area.setPrefRows(5);

                area.update(() -> {
                    area.setText(settings.getString(name));
                    area.setWidth(table.getWidth());
                });

                area.changed(() -> {
                    settings.put(name, area.getText());
                    if (changed != null) {
                        changed.get(area.getText());
                    }
                });

                addDesc(table.label(() -> title).left().padTop(3f).get());
                table.row().add(area).left();
                table.row();
            }
        }
    }
}
