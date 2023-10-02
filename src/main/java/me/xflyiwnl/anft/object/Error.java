package me.xflyiwnl.anft.object;

public class Error {

    private int code = 0;
    private String description = "Неизвестная ошибка";

    public Error(int code) {
        this.code = code;
    }

    public Error(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
