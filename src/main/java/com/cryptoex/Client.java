package com.cryptoex;

import com.fasterxml.jackson.core.type.TypeReference;
import com.cryptoex.errors.BadRequestException;
import com.cryptoex.utils.HashUtil;
import com.cryptoex.utils.JsonUtil;
import okhttp3.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Client {

    private static String apiEndpoint = "https://ex.zhigui.com";
    private static String host = getHost();

    private static String key;

    private static String secret;

    static OkHttpClient client = buildClient();

    static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    static final String HEADER_API_KEY = "API-KEY";
    static final String HEADER_API_SIGNATURE = "API-SIGNATURE";
    static final String HEADER_API_SIGNATURE_METHOD = "API-SIGNATURE-METHOD";
    static final String HEADER_API_SIGNATURE_VERSION = "API-SIGNATURE-VERSION";
    static final String HEADER_API_TIMESTAMP = "API-TIMESTAMP";
    static final String HEADER_API_UNIQUE_ID = "API-UNIQUE-ID";
    static final String SIGNATURE_METHOD = "HmacSHA256";
    static final String SIGNATURE_VERSION = "1";
    static final long time = 5L;

    public Client(String key, String secret) {
        this.key = key;
        this.secret = secret;
    }

    static OkHttpClient buildClient() {
        return new OkHttpClient.Builder().connectTimeout(time, TimeUnit.SECONDS)
                .readTimeout(time, TimeUnit.SECONDS).writeTimeout(time, TimeUnit.SECONDS)
                .build();
    }


    <T> T request(Class<T> clazz, TypeReference<T> ref, String method, String path, Map<String, String> query, Object body, String uniqueId) {
        if (!path.startsWith("/")) {
            throw new IllegalArgumentException("Invalid path: " + path);
        } else {
            StringBuilder payloadToSign = (new StringBuilder(1024)).append(method).append('\n').append(host).append('\n').append(path).append('\n');
            String queryString = null;
            if (query != null) {
                List<String> paramList = new ArrayList();
                Iterator var11 = query.entrySet().iterator();

                while (var11.hasNext()) {
                    Map.Entry<String, String> entry = (Map.Entry) var11.next();
                    paramList.add((String) entry.getKey() + "=" + (String) entry.getValue());
                }

                Collections.sort(paramList);
                queryString = String.join("&", paramList);
                payloadToSign.append(queryString).append('\n');
            } else {
                payloadToSign.append('\n');
            }

            StringBuilder urlBuilder = (new StringBuilder(64)).append(apiEndpoint).append(path);
            if (queryString != null) {
                urlBuilder.append('?').append(queryString);
            }

            String url = urlBuilder.toString();
            String jsonBody = body == null ? "" : JsonUtil.writeJson(body);

            Request request = buildRequest(url, method, jsonBody, uniqueId, payloadToSign);

            try {
                return execute(clazz, ref, request);
            } catch (IOException var19) {
                throw new RuntimeException("IOException", var19);
            }
        }
    }

    Request buildRequest(String url, String method, String jsonBody, String uniqueId, StringBuilder payloadToSign) {
        Request.Builder requestBuilder = (new Request.Builder()).url(url);
        if ("POST".equals(method)) {
            requestBuilder.post(RequestBody.create(JSON, jsonBody));
        }

        String timestamp = String.valueOf(System.currentTimeMillis());
        if (uniqueId == null || uniqueId.isEmpty()) {
            uniqueId = UUID.randomUUID().toString().replace("-", "");
        }

        payloadToSign = buildPayload(timestamp, uniqueId, payloadToSign);

        //todo test
        requestBuilder.addHeader("X-Real-IP", "193.238.36.154");
        requestBuilder.addHeader("X-Forwarded-For", "193.238.36.154");

        requestBuilder.addHeader(HEADER_API_KEY, key);
        requestBuilder.addHeader(HEADER_API_SIGNATURE_METHOD, SIGNATURE_METHOD);
        requestBuilder.addHeader(HEADER_API_SIGNATURE_VERSION, SIGNATURE_VERSION);
        requestBuilder.addHeader(HEADER_API_TIMESTAMP, timestamp);
        requestBuilder.addHeader(HEADER_API_UNIQUE_ID, uniqueId);
        payloadToSign.append(jsonBody);
        String sign = HashUtil.hmacSha256(payloadToSign.toString().getBytes(StandardCharsets.UTF_8), secret);
        requestBuilder.addHeader(HEADER_API_SIGNATURE, sign);
        return requestBuilder.build();
    }

    StringBuilder buildPayload(String timestamp, String uniqueId, StringBuilder payloadToSign) {
        List<String> headerList = new ArrayList();
        headerList.add(HEADER_API_KEY + ": " + key);
        headerList.add(HEADER_API_SIGNATURE_METHOD + ": HmacSHA256");
        headerList.add(HEADER_API_SIGNATURE_VERSION + ": 1");
        headerList.add(HEADER_API_TIMESTAMP + ": " + timestamp);
        headerList.add(HEADER_API_UNIQUE_ID + ": " + uniqueId);
        Collections.sort(headerList);
        Iterator var16 = headerList.iterator();

        while (var16.hasNext()) {
            String header = (String) var16.next();
            payloadToSign.append(header).append('\n');
        }
        return payloadToSign;
    }

    <T> T execute(Class<T> clazz, TypeReference<T> ref, Request request) throws IOException {
        Response response = client.newCall(request).execute();
        ResponseBody body;
        Throwable var6;
        if (response.code() == 200) {
            body = response.body();
            var6 = null;

            T var8;
            try {
                String json = body.string();
                if (!"null".equals(json)) {
                    var8 = clazz == null ? JsonUtil.readJson(json, ref) : JsonUtil.readJson(json, clazz);
                    return var8;
                }

                var8 = null;
            } catch (Throwable var34) {
                var6 = var34;
                throw var34;
            } finally {
                closeResponseBody(body, var6);
            }

            return var8;
        } else if (response.code() == 400) {
            body = response.body();
            var6 = null;

            try {
                BadRequestException.ApiErrorResponse err = (BadRequestException.ApiErrorResponse) JsonUtil.readJson(body.string(), BadRequestException.ApiErrorResponse.class);
                throw new BadRequestException(err.error, err.data, err.message);
            } catch (Throwable var32) {
                var6 = var32;
                throw var32;
            } finally {
                closeResponseBody(body, var6);
            }
        } else if (response.code() == 429) {
            response.close();
            throw new RuntimeException("Rate limit");
        } else {
            response.close();
            throw new RuntimeException("Http error: " + response.code());
        }
    }

    void closeResponseBody(ResponseBody body, Throwable var6) {
        if (body != null) {
            if (var6 != null) {
                try {
                    body.close();
                } catch (Throwable var30) {
                    var6.addSuppressed(var30);
                }
            } else {
                body.close();
            }
        }
    }

    static String getHost() {
        String host = null;
        try {
            host = new URL(apiEndpoint).getHost();
        } catch (MalformedURLException e) {
            System.err.println("apiEndpoint error:" + apiEndpoint);
            System.exit(0);
        }
        return host;
    }
}