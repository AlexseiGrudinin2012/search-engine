package ru.learning.searchengine.infrastructure.jsoup;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JsoupConfig {
    @Value("${app.jsoup.config.userAgent}")
    private String userAgent;

    @Value("${app.jsoup.config.referrer}")
    private String referrer;

    @Value("${app.jsoup.config.max-body-size}")
    private int maxBodySize;

    @Value("${app.jsoup.config.timeout.milliseconds}")
    private int timeout;

    @Value("${app.jsoup.config.ignore-content-type}")
    private boolean isContentTypeIgnore;

    @Value("${app.jsoup.config.ignore-http-errors}")
    private boolean isHttpErrorsIgnore;

    @Value("${app.jsoup.config.ssl-use}")
    private boolean isSslUse;


    public Connection getConnection() {
        Connection connection = Jsoup.newSession()
                .ignoreContentType(isContentTypeIgnore)
                .ignoreHttpErrors(isHttpErrorsIgnore)
                .userAgent(userAgent)
                .referrer(referrer)
                .maxBodySize(maxBodySize)
                .timeout(timeout);
        return isSslUse ? connection.sslSocketFactory(new JsoupSSLSocketFactory().get()) : connection;
    }
}
