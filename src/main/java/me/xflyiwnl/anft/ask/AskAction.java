package me.xflyiwnl.anft.ask;

public interface AskAction<T extends AskMessage> {

    void execute(T ask);

}
