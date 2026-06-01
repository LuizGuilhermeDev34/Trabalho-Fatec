package com.castrapet.util;

import java.util.*;

/**
 * Parser e builder de JSON em Java puro, sem bibliotecas externas.
 */
public class Json {

    // ─── BUILDER ─────────────────────────────────────────────────────────────

    public static String toJson(Object obj) {
        if (obj == null) return "null";
        if (obj instanceof String s) return "\"" + escape(s) + "\"";
        if (obj instanceof Boolean || obj instanceof Number) return obj.toString();
        if (obj instanceof Map<?, ?> m) return mapToJson(m);
        if (obj instanceof List<?> l) return listToJson(l);
        return "\"" + escape(obj.toString()) + "\"";
    }

    private static String mapToJson(Map<?, ?> map) {
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<?, ?> e : map.entrySet()) {
            if (!first) sb.append(",");
            sb.append("\"").append(escape(e.getKey().toString())).append("\":");
            sb.append(toJson(e.getValue()));
            first = false;
        }
        return sb.append("}").toString();
    }

    private static String listToJson(List<?> list) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(toJson(list.get(i)));
        }
        return sb.append("]").toString();
    }

    private static String escape(String s) {
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    // ─── PARSER ──────────────────────────────────────────────────────────────

    public static Map<String, Object> parseObject(String json) {
        if (json == null || json.isBlank()) return new LinkedHashMap<>();
        Parser p = new Parser(json.trim());
        Object val = p.parseValue();
        if (val instanceof Map<?, ?> m) {
            @SuppressWarnings("unchecked")
            Map<String, Object> result = (Map<String, Object>) m;
            return result;
        }
        return new LinkedHashMap<>();
    }

    public static String getString(Map<String, Object> map, String key) {
        Object v = map.get(key);
        return v == null ? null : v.toString();
    }

    public static Long getLong(Map<String, Object> map, String key) {
        Object v = map.get(key);
        if (v == null) return null;
        if (v instanceof Number n) return n.longValue();
        try { return Long.parseLong(v.toString()); } catch (NumberFormatException e) { return null; }
    }

    public static Double getDouble(Map<String, Object> map, String key) {
        Object v = map.get(key);
        if (v == null) return null;
        if (v instanceof Number n) return n.doubleValue();
        try { return Double.parseDouble(v.toString()); } catch (NumberFormatException e) { return null; }
    }

    public static Boolean getBoolean(Map<String, Object> map, String key) {
        Object v = map.get(key);
        if (v == null) return null;
        if (v instanceof Boolean b) return b;
        return Boolean.parseBoolean(v.toString());
    }

    // Parser de JSON por descida recursiva
    private static class Parser {
        private final String src;
        private int pos;

        Parser(String src) { this.src = src; this.pos = 0; }

        Object parseValue() {
            skipWs();
            if (pos >= src.length()) return null;
            char c = src.charAt(pos);
            if (c == '"') return parseString();
            if (c == '{') return parseObject();
            if (c == '[') return parseArray();
            if (c == 't') { pos += 4; return Boolean.TRUE; }
            if (c == 'f') { pos += 5; return Boolean.FALSE; }
            if (c == 'n') { pos += 4; return null; }
            return parseNumber();
        }

        private String parseString() {
            pos++; // salta '"'
            StringBuilder sb = new StringBuilder();
            while (pos < src.length()) {
                char c = src.charAt(pos++);
                if (c == '"') break;
                if (c == '\\' && pos < src.length()) {
                    char esc = src.charAt(pos++);
                    switch (esc) {
                        case '"' -> sb.append('"');
                        case '\\' -> sb.append('\\');
                        case 'n' -> sb.append('\n');
                        case 'r' -> sb.append('\r');
                        case 't' -> sb.append('\t');
                        default -> sb.append(esc);
                    }
                } else {
                    sb.append(c);
                }
            }
            return sb.toString();
        }

        private Map<String, Object> parseObject() {
            pos++; // salta '{'
            Map<String, Object> map = new LinkedHashMap<>();
            skipWs();
            if (pos < src.length() && src.charAt(pos) == '}') { pos++; return map; }
            while (pos < src.length()) {
                skipWs();
                String key = parseString();
                skipWs();
                if (pos < src.length() && src.charAt(pos) == ':') pos++;
                skipWs();
                Object val = parseValue();
                map.put(key, val);
                skipWs();
                if (pos >= src.length()) break;
                char next = src.charAt(pos);
                if (next == '}') { pos++; break; }
                if (next == ',') pos++;
            }
            return map;
        }

        private List<Object> parseArray() {
            pos++; // salta '['
            List<Object> list = new ArrayList<>();
            skipWs();
            if (pos < src.length() && src.charAt(pos) == ']') { pos++; return list; }
            while (pos < src.length()) {
                skipWs();
                list.add(parseValue());
                skipWs();
                if (pos >= src.length()) break;
                char next = src.charAt(pos);
                if (next == ']') { pos++; break; }
                if (next == ',') pos++;
            }
            return list;
        }

        private Number parseNumber() {
            int start = pos;
            while (pos < src.length()) {
                char c = src.charAt(pos);
                if (c == ',' || c == '}' || c == ']' || Character.isWhitespace(c)) break;
                pos++;
            }
            String num = src.substring(start, pos);
            if (num.contains(".")) return Double.parseDouble(num);
            return Long.parseLong(num);
        }

        private void skipWs() {
            while (pos < src.length() && Character.isWhitespace(src.charAt(pos))) pos++;
        }
    }
}
