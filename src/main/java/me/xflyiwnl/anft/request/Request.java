package me.xflyiwnl.anft.request;

import java.net.http.HttpResponse;

public interface Request {

    HttpResponse<String> send();
    HttpResponse<String> sendAsync();

}
