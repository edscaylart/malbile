package br.scaylart.malbile.views;

public class NavMenuSection implements NavDrawerItem {
    public static final int SECTION_TYPE = 1;
    private int id;
    private int label;

    private NavMenuSection() {
    }

    public static NavMenuSection create(int id, int label) {
        NavMenuSection section = new NavMenuSection();
        section.setLabel(label);
        return section;
    }

    @Override
    public int getType() {
        return SECTION_TYPE;
    }

    public int getLabel() {
        return label;
    }

    public void setLabel(int label) {
        this.label = label;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public int getIcon() {
        return 0;
    }

}
