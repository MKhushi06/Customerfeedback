package com.feedback.app;

import com.feedback.controller.FeedbackController;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.util.HashMap;
import java.util.Map;

public class LocalHttpServer {
    private static HttpServer server;
    private static FeedbackController controller;
    private static final int PORT = 8085;

    public static void start(FeedbackController ctrl) {
        controller = ctrl;
        try {
            // Bind to 0.0.0.0 (all interfaces) to allow external connections on local network
            server = HttpServer.create(new InetSocketAddress("0.0.0.0", PORT), 0);
            server.createContext("/feedback", new FormHandler());
            server.createContext("/submit-feedback", new SubmitHandler());
            server.setExecutor(null); // default executor
            server.start();
            System.out.println("Local HTTP Server started on port " + PORT);
        } catch (IOException e) {
            System.err.println("Could not start local HTTP server: " + e.getMessage());
        }
    }

    public static void stop() {
        if (server != null) {
            server.stop(0);
        }
    }

    private static class FormHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
                exchange.sendResponseHeaders(405, -1);
                return;
            }
            byte[] response = getHtmlForm().getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=utf-8");
            exchange.sendResponseHeaders(200, response.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response);
            }
        }
    }

    private static class SubmitHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                exchange.sendResponseHeaders(405, -1);
                return;
            }

            // Read post data
            Map<String, String> params = new HashMap<>();
            try (InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
                 BufferedReader br = new BufferedReader(isr)) {
                
                StringBuilder query = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    query.append(line);
                }
                
                String[] pairs = query.toString().split("&");
                for (String pair : pairs) {
                    int idx = pair.indexOf("=");
                    if (idx > 0) {
                        String key = URLDecoder.decode(pair.substring(0, idx), StandardCharsets.UTF_8);
                        String value = URLDecoder.decode(pair.substring(idx + 1), StandardCharsets.UTF_8);
                        params.put(key, value);
                    }
                }
            }

            String name = params.get("customer_name");
            String contact = params.get("contact_number");
            String type = params.get("feedback_type");
            String ratingStr = params.get("rating");
            String comments = params.get("comments");

            int rating = 5;
            try {
                rating = Integer.parseInt(ratingStr);
            } catch (NumberFormatException e) {
                // Ignore
            }

            String responseHtml;
            try {
                boolean success = controller.addFeedback(name, contact, type, rating, comments, new Date(System.currentTimeMillis()));
                if (success) {
                    responseHtml = getSuccessHtml(name, type, rating);
                } else {
                    responseHtml = getErrorHtml("Failed to store feedback in database. Please contact support.");
                }
            } catch (Exception e) {
                responseHtml = getErrorHtml(e.getMessage());
            }

            byte[] responseBytes = responseHtml.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=utf-8");
            exchange.sendResponseHeaders(200, responseBytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(responseBytes);
            }
        }
    }

    private static String getHtmlForm() {
        return "<!DOCTYPE html>\n" +
               "<html lang=\"en\">\n" +
               "<head>\n" +
               "    <meta charset=\"UTF-8\">\n" +
               "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
               "    <title>Submit Customer Feedback</title>\n" +
               "    <style>\n" +
               "        body {\n" +
               "            font-family: -apple-system, BlinkMacSystemFont, \"Segoe UI\", Roboto, Helvetica, Arial, sans-serif;\n" +
               "            background-color: #f1f5f9;\n" +
               "            color: #0f172a;\n" +
               "            margin: 0;\n" +
               "            padding: 20px;\n" +
               "            display: flex;\n" +
               "            justify-content: center;\n" +
               "            align-items: center;\n" +
               "            min-height: 100vh;\n" +
               "        }\n" +
               "        .card {\n" +
               "            background-color: #ffffff;\n" +
               "            border-radius: 16px;\n" +
               "            box-shadow: 0 4px 6px -1px rgb(0 0 0 / 0.1), 0 2px 4px -2px rgb(0 0 0 / 0.1);\n" +
               "            padding: 30px;\n" +
               "            width: 100%;\n" +
               "            max-width: 450px;\n" +
               "            box-sizing: border-box;\n" +
               "        }\n" +
               "        h1 {\n" +
               "            font-size: 24px;\n" +
               "            font-weight: 700;\n" +
               "            margin-top: 0;\n" +
               "            margin-bottom: 8px;\n" +
               "            color: #0f172a;\n" +
               "            text-align: center;\n" +
               "        }\n" +
               "        .subtitle {\n" +
               "            font-size: 13px;\n" +
               "            color: #64748b;\n" +
               "            text-align: center;\n" +
               "            margin-bottom: 24px;\n" +
               "        }\n" +
               "        .form-group {\n" +
               "            margin-bottom: 16px;\n" +
               "        }\n" +
               "        label {\n" +
               "            display: block;\n" +
               "            font-size: 13px;\n" +
               "            font-weight: 600;\n" +
               "            margin-bottom: 6px;\n" +
               "            color: #475569;\n" +
               "        }\n" +
               "        input, select, textarea {\n" +
               "            width: 100%;\n" +
               "            padding: 10px 12px;\n" +
               "            font-size: 14px;\n" +
               "            border: 1px solid #cbd5e1;\n" +
               "            border-radius: 8px;\n" +
               "            box-sizing: border-box;\n" +
               "            background-color: #ffffff;\n" +
               "            color: #0f172a;\n" +
               "            outline: none;\n" +
               "            transition: border-color 0.2s;\n" +
               "        }\n" +
               "        input:focus, select:focus, textarea:focus {\n" +
               "            border-color: #3b82f6;\n" +
               "        }\n" +
               "        .stars {\n" +
               "            display: flex;\n" +
               "            gap: 8px;\n" +
               "            justify-content: center;\n" +
               "            margin-top: 5px;\n" +
               "            margin-bottom: 15px;\n" +
               "        }\n" +
               "        .star {\n" +
               "            font-size: 32px;\n" +
               "            color: #d1d5db;\n" +
               "            cursor: pointer;\n" +
               "            transition: color 0.15s;\n" +
               "        }\n" +
               "        .star.active {\n" +
               "            color: #f59e0b;\n" +
               "        }\n" +
               "        button {\n" +
               "            width: 100%;\n" +
               "            padding: 12px;\n" +
               "            font-size: 15px;\n" +
               "            font-weight: 600;\n" +
               "            background-color: #10b981;\n" +
               "            color: #ffffff;\n" +
               "            border: none;\n" +
               "            border-radius: 8px;\n" +
               "            cursor: pointer;\n" +
               "            transition: background-color 0.2s;\n" +
               "        }\n" +
               "        button:hover {\n" +
               "            background-color: #059669;\n" +
               "        }\n" +
               "    </style>\n" +
               "</head>\n" +
               "<body>\n" +
               "    <div class=\"card\">\n" +
               "        <h1>Share Your Experience</h1>\n" +
               "        <div class=\"subtitle\">Your feedback helps us provide a better service</div>\n" +
               "        <form action=\"/submit-feedback\" method=\"POST\" id=\"feedbackForm\">\n" +
               "            <div class=\"form-group\">\n" +
               "                <label for=\"name\">Your Name *</label>\n" +
               "                <input type=\"text\" id=\"name\" name=\"customer_name\" required placeholder=\"e.g. Rohan Deshmukh\">\n" +
               "            </div>\n" +
               "            <div class=\"form-group\">\n" +
               "                <label for=\"contact\">Contact Number *</label>\n" +
               "                <input type=\"tel\" id=\"contact\" name=\"contact_number\" required placeholder=\"e.g. +91 98765 43210\">\n" +
               "            </div>\n" +
               "            <div class=\"form-group\">\n" +
               "                <label for=\"type\">Feedback Category</label>\n" +
               "                <select id=\"type\" name=\"feedback_type\">\n" +
               "                    <option value=\"Review\">Review</option>\n" +
               "                    <option value=\"Complaint\">Complaint</option>\n" +
               "                    <option value=\"Suggestion\">Suggestion</option>\n" +
               "                </select>\n" +
               "            </div>\n" +
               "            <div class=\"form-group\">\n" +
               "                <label>Overall Rating *</label>\n" +
               "                <div class=\"stars\" id=\"starContainer\">\n" +
               "                    <span class=\"star active\" data-value=\"1\">★</span>\n" +
               "                    <span class=\"star active\" data-value=\"2\">★</span>\n" +
               "                    <span class=\"star active\" data-value=\"3\">★</span>\n" +
               "                    <span class=\"star active\" data-value=\"4\">★</span>\n" +
               "                    <span class=\"star active\" data-value=\"5\">★</span>\n" +
               "                </div>\n" +
               "                <input type=\"hidden\" name=\"rating\" id=\"ratingInput\" value=\"5\">\n" +
               "            </div>\n" +
               "            <div class=\"form-group\">\n" +
               "                <label for=\"comments\">Comments *</label>\n" +
               "                <textarea id=\"comments\" name=\"comments\" rows=\"4\" required placeholder=\"Tell us what you think...\"></textarea>\n" +
               "            </div>\n" +
               "            <button type=\"submit\">Submit Feedback</button>\n" +
               "        </form>\n" +
               "    </div>\n" +
               "    <script>\n" +
               "        const stars = document.querySelectorAll('.star');\n" +
               "        const ratingInput = document.getElementById('ratingInput');\n" +
               "        stars.forEach(star => {\n" +
               "            star.addEventListener('click', () => {\n" +
               "                const val = star.getAttribute('data-value');\n" +
               "                ratingInput.value = val;\n" +
               "                stars.forEach(s => {\n" +
               "                    if (s.getAttribute('data-value') <= val) {\n" +
               "                        s.classList.add('active');\n" +
               "                    } else {\n" +
               "                        s.classList.remove('active');\n" +
               "                    }\n" +
               "                });\n" +
               "            });\n" +
               "        });\n" +
               "    </script>\n" +
               "</body>\n" +
               "</html>";
    }

    private static String getSuccessHtml(String name, String type, int rating) {
        return "<!DOCTYPE html>\n" +
               "<html lang=\"en\">\n" +
               "<head>\n" +
               "    <meta charset=\"UTF-8\">\n" +
               "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
               "    <title>Feedback Submitted</title>\n" +
               "    <style>\n" +
               "        body {\n" +
               "            font-family: -apple-system, BlinkMacSystemFont, \"Segoe UI\", Roboto, Helvetica, sans-serif;\n" +
               "            background-color: #f1f5f9;\n" +
               "            color: #0f172a;\n" +
               "            margin: 0;\n" +
               "            padding: 20px;\n" +
               "            display: flex;\n" +
               "            justify-content: center;\n" +
               "            align-items: center;\n" +
               "            min-height: 100vh;\n" +
               "        }\n" +
               "        .card {\n" +
               "            background-color: #ffffff;\n" +
               "            border-radius: 16px;\n" +
               "            box-shadow: 0 4px 6px -1px rgba(0,0,0,0.1);\n" +
               "            padding: 40px 30px;\n" +
               "            width: 100%;\n" +
               "            max-width: 450px;\n" +
               "            text-align: center;\n" +
               "            box-sizing: border-box;\n" +
               "        }\n" +
               "        .success-icon {\n" +
               "            font-size: 64px;\n" +
               "            color: #10b981;\n" +
               "            margin-bottom: 20px;\n" +
               "        }\n" +
               "        h1 {\n" +
               "            font-size: 24px;\n" +
               "            margin-top: 0;\n" +
               "            margin-bottom: 12px;\n" +
               "        }\n" +
               "        p {\n" +
               "            font-size: 14px;\n" +
               "            color: #64748b;\n" +
               "            line-height: 1.5;\n" +
               "            margin-bottom: 24px;\n" +
               "        }\n" +
               "        a {\n" +
               "            display: inline-block;\n" +
               "            padding: 10px 20px;\n" +
               "            background-color: #3b82f6;\n" +
               "            color: #ffffff;\n" +
               "            border-radius: 8px;\n" +
               "            text-decoration: none;\n" +
               "            font-weight: 600;\n" +
               "            font-size: 14px;\n" +
               "        }\n" +
               "    </style>\n" +
               "</head>\n" +
               "<body>\n" +
               "    <div class=\"card\">\n" +
               "        <div class=\"success-icon\">✓</div>\n" +
               "        <h1>Thank You, " + name + "!</h1>\n" +
               "        <p>Your " + type.toLowerCase() + " (Rating: " + rating + "★) has been successfully registered. We value your input and appreciate you taking the time to share your feedback.</p>\n" +
               "        <a href=\"/feedback\">Give Another Feedback</a>\n" +
               "    </div>\n" +
               "</body>\n" +
               "</html>";
    }

    private static String getErrorHtml(String message) {
        return "<!DOCTYPE html>\n" +
               "<html lang=\"en\">\n" +
               "<head>\n" +
               "    <meta charset=\"UTF-8\">\n" +
               "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
               "    <title>Submission Error</title>\n" +
               "    <style>\n" +
               "        body {\n" +
               "            font-family: -apple-system, BlinkMacSystemFont, \"Segoe UI\", Roboto, sans-serif;\n" +
               "            background-color: #f1f5f9;\n" +
               "            color: #0f172a;\n" +
               "            margin: 0;\n" +
               "            padding: 20px;\n" +
               "            display: flex;\n" +
               "            justify-content: center;\n" +
               "            align-items: center;\n" +
               "            min-height: 100vh;\n" +
               "        }\n" +
               "        .card {\n" +
               "            background-color: #ffffff;\n" +
               "            border-radius: 16px;\n" +
               "            box-shadow: 0 4px 6px -1px rgba(0,0,0,0.1);\n" +
               "            padding: 40px 30px;\n" +
               "            width: 100%;\n" +
               "            max-width: 450px;\n" +
               "            text-align: center;\n" +
               "            box-sizing: border-box;\n" +
               "        }\n" +
               "        .error-icon {\n" +
               "            font-size: 64px;\n" +
               "            color: #ef4444;\n" +
               "            margin-bottom: 20px;\n" +
               "        }\n" +
               "        h1 {\n" +
               "            font-size: 24px;\n" +
               "            color: #ef4444;\n" +
               "            margin-top: 0;\n" +
               "            margin-bottom: 12px;\n" +
               "        }\n" +
               "        p {\n" +
               "            font-size: 14px;\n" +
               "            color: #64748b;\n" +
               "            line-height: 1.5;\n" +
               "            margin-bottom: 24px;\n" +
               "        }\n" +
               "        a {\n" +
               "            display: inline-block;\n" +
               "            padding: 10px 20px;\n" +
               "            background-color: #ef4444;\n" +
               "            color: #ffffff;\n" +
               "            border-radius: 8px;\n" +
               "            text-decoration: none;\n" +
               "            font-weight: 600;\n" +
               "            font-size: 14px;\n" +
               "        }\n" +
               "    </style>\n" +
               "</head>\n" +
               "<body>\n" +
               "    <div class=\"card\">\n" +
               "        <div class=\"error-icon\">⚠</div>\n" +
               "        <h1>Submission Failed</h1>\n" +
               "        <p>Error details: " + message + "</p>\n" +
               "        <a href=\"/feedback\">Try Again</a>\n" +
               "    </div>\n" +
               "</body>\n" +
               "</html>";
    }
}
