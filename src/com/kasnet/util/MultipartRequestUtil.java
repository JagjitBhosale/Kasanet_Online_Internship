package com.kasnet.util;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

public final class MultipartRequestUtil {

    private static final int MAX_TEXT_FIELD_BYTES = 64 * 1024;
    private static final Pattern FILENAME_PARAMETER =
            Pattern.compile("(?:^|;)\\s*filename\\*?\\s*=", Pattern.CASE_INSENSITIVE);

    private MultipartRequestUtil() {
        throw new UnsupportedOperationException("MultipartRequestUtil is a utility class.");
    }

    public static HttpServletRequest withParsedParameters(HttpServletRequest request)
            throws IOException, ServletException {
        String contentType = request.getContentType();
        if (contentType == null
                || !contentType.toLowerCase(Locale.ROOT).startsWith("multipart/form-data")) {
            return request;
        }

        Map<String, List<String>> parsed = new LinkedHashMap<>();
        for (Part part : request.getParts()) {
            if (isFilePart(part)) {
                continue;
            }
            if (part.getSize() > MAX_TEXT_FIELD_BYTES) {
                throw new IllegalArgumentException("A form field exceeds the permitted size.");
            }

            byte[] value;
            try (InputStream input = part.getInputStream()) {
                value = input.readNBytes(MAX_TEXT_FIELD_BYTES + 1);
            }
            if (value.length > MAX_TEXT_FIELD_BYTES) {
                throw new IllegalArgumentException("A form field exceeds the permitted size.");
            }
            parsed.computeIfAbsent(part.getName(), key -> new ArrayList<>())
                    .add(new String(value, StandardCharsets.UTF_8));
        }

        Map<String, String[]> parameters = new LinkedHashMap<>();
        for (Map.Entry<String, List<String>> entry : parsed.entrySet()) {
            parameters.put(entry.getKey(), entry.getValue().toArray(new String[0]));
        }
        Map<String, String[]> immutableParameters = Collections.unmodifiableMap(parameters);

        return new HttpServletRequestWrapper(request) {
            @Override
            public String getParameter(String name) {
                String[] values = immutableParameters.get(name);
                return values == null || values.length == 0 ? null : values[0];
            }

            @Override
            public String[] getParameterValues(String name) {
                String[] values = immutableParameters.get(name);
                return values == null ? null : values.clone();
            }

            @Override
            public Map<String, String[]> getParameterMap() {
                Map<String, String[]> copy = new LinkedHashMap<>();
                for (Map.Entry<String, String[]> entry : immutableParameters.entrySet()) {
                    copy.put(entry.getKey(), entry.getValue().clone());
                }
                return Collections.unmodifiableMap(copy);
            }

            @Override
            public Enumeration<String> getParameterNames() {
                return Collections.enumeration(immutableParameters.keySet());
            }
        };
    }

    private static boolean isFilePart(Part part) {
        String disposition = part.getHeader("content-disposition");
        return disposition != null && FILENAME_PARAMETER.matcher(disposition).find();
    }
}
