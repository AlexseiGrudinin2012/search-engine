package ru.learning.searchengine.infrastructure.config;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

@Slf4j
@Component
public class JsoupConfig {
    @Value("${app.jsoup.config.userAgent}")
    private String userAgent;

    @Value("${app.jsoup.config.referrer}")
    private String referrer;

    @Value("${app.jsoup.config.max-body-size}")
    private int maxBodySize;

    @Value("${app.jsoup.config.timeout}")
    private int timeout;

    @Value("${app.jsoup.config.ignore-content-type}")
    private boolean isContentTypeIgnore;

    @Value("${app.jsoup.config.ignore-http-errors}")
    private boolean isHttpErrorsIgnore;

    @Value("${app.jsoup.config.ssl-use}")
    private boolean isSslUse;

    private SSLSocketFactory socketFactory() {
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }

            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }

            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        }};

        try {
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            return sslContext.getSocketFactory();
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            throw new RuntimeException("Failed to create a SSL socket factory", e);
        }
    }

    public Connection getConnection() {
        Connection connection = Jsoup.newSession()
                .ignoreContentType(this.isContentTypeIgnore)
                .ignoreHttpErrors(this.isHttpErrorsIgnore)
                .userAgent(this.userAgent)
                .referrer(this.referrer)
                .maxBodySize(this.maxBodySize)
                .timeout(this.timeout);
        return this.isSslUse ? connection.sslSocketFactory(this.socketFactory()) : connection;
    }
}
