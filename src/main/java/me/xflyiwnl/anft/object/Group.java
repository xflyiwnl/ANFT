package me.xflyiwnl.anft.object;

public class Group {

    private String name;
    private int limit;
    private boolean isDefault;

    public Group(String name, int limit, boolean isDefault) {
        this.name = name;
        this.limit = limit;
        this.isDefault = isDefault;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }
}
