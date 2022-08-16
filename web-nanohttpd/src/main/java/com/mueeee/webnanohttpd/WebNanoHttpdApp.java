package com.mueeee.webnanohttpd;

import fi.iki.elonen.NanoHTTPD;

import java.io.IOException;
import java.util.Map;

/**
 * NanoHttpd
 * https://github.com/NanoHttpd/nanohttpd
 */
public class WebNanoHttpdApp extends NanoHTTPD {

    private static final int SERVER_PORT = 9999;

    public WebNanoHttpdApp() throws IOException {
        super(SERVER_PORT);
        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
        System.out.println("HTTPServer is listening on port " + SERVER_PORT + "\n");
    }

    public static void main(String[] args) {
        try {
            new WebNanoHttpdApp();
            onApplicationRunning();
        } catch (IOException ioe) {
            System.err.println("Couldn't start server:\n" + ioe);
        }
    }

    @Override
    public Response serve(IHTTPSession session) {
        // System.out.println(session.getUri());
        Map<String, String> params = session.getParms();

        if ("/".equals(session.getUri())) {
            String html = """
                    <!DOCTYPE html>
                        <html>
                        <head>
                            <meta charset="UTF-8">
                            <meta http-equiv="X-UA-Compatible" content="IE=edge">
                            <meta name="viewport" content="width=device-width, initial-scale=1.0">
                            <title>NanoHttpd</title>
                        </head>
                        <body>
                            <h1>NanoHttpd，在 Java 中易于嵌入的小型 HTTP 服务器</h1>
                            <ul>
                                <li><a href="/">/</a></li>
                                <li><a href="/text">/text</a></li>
                                <li><a href="/json">/json</a></li>
                            </ul>
                            <script>
                                var autoclose = getUrlParam("autoclose");
                                if (autoclose) {
                                    checkIfServerIsDown();
                                }
                                // 检查服务器是否关闭，如果关闭则自动关闭当前页签
                                function checkIfServerIsDown() {
                                    setInterval(function () {
                                        var xhr = new XMLHttpRequest();
                                        xhr.open('GET', "/", true);
                                        xhr.onreadystatechange = function () {
                                            if (xhr.readyState === 4 && xhr.status === 200 || xhr.status === 304) {
                                                var data = xhr.responseText;
                                            }
                                        };
                                        xhr.onerror = function (e) {
                                            open(location, '_self').close();
                                        };
                                        xhr.send();
                                    }, 1000);
                                }
                                // 获取网址参数
                                function getUrlParam (name) {
                                    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
                                    var r = window.location.search.substr(1).match(reg);
                                    if (r != null) return unescape(r[2]); return null;
                                }
                            </script>
                        </body>
                    </html>
                    """;
            return newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "text/html", html);
        } else if ("/text".equals(session.getUri())) {
            String text = "Hello NanoHttpd";
            return newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "text/plain", text);
        } else if ("/json".equals(session.getUri())) {
            String data = """
                    {
                        "code": 0,
                        "message": "success",
                        "data": {}
                    }
                    """;
            return newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "application/json", data);
        }

        return newFixedLengthResponse("");
    }

    public static void onApplicationRunning() {
        // 自动打开浏览器访问项目地址
        String osName = System.getProperty("os.name", "");
        if (osName.contains("Windows")) {
            try {
                Runtime.getRuntime().exec("cmd /c start http://127.0.0.1:" + SERVER_PORT + "?autoclose=1");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
