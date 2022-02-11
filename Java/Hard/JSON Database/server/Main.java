package server;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Main {

    static ServerSocket server;

    public static void main(String[] args) throws IOException {
        server = new ServerSocket(8899, 100, InetAddress.getByName("127.0.0.1"));
        System.out.println("Server started!");
        ExecutorService executor = Executors.newCachedThreadPool();

        while (true) {
            Socket socket = server.accept();
            executor.submit(() -> {
                try {
                    process(socket);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    static void process(Socket socket) throws Exception {
        DataInputStream input = new DataInputStream(socket.getInputStream());
        DataOutputStream output = new DataOutputStream(socket.getOutputStream());
        ReadWriteLock lock = new ReentrantReadWriteLock();
        String str = input.readUTF().trim();
        Gson gson = new Gson();
        JsonReader reader = new JsonReader(new StringReader(str));
        reader.setLenient(true);
        JsonObject jo = (JsonObject) JsonParser.parseReader(reader);

        String key = null;
        if (jo.get("key") != null)
            key = gson.toJson(jo.get("key"));

        Request request = new Request(jo.get("type").getAsString(),
                key, gson.toJson(jo.get("value")));
        if (request.getType().contains("exit")) {
            output.writeUTF("{\"response\":\"OK\"}");
            server.close();
            System.exit(0);
        } else {
            Lock locked;
            if (request.getType().contains("get"))
                locked = lock.readLock();
            else
                locked = lock.writeLock();
            locked.lock();
            output.writeUTF(processRequests(request));
            locked.unlock();
        }
    }

    static String processRequests(Request req) {
        String returnStr;
        try {
            switch (req.getType()) {
                case "get":
                    String str = getDBRecord(req.getKey());
                    if (str == null)
                        throw new ArrayIndexOutOfBoundsException();
                    returnStr = str;
                    break;
                case "set":
                    if (req.getValue() == null)
                        throw new NullPointerException();
                    setDBRecord(req.getKey(), req.getValue());
                    returnStr = "OK";
                    break;
                case "delete":
                    if (!deleteDBRecord(req.getKey()))
                        throw new ArrayIndexOutOfBoundsException();
                    returnStr = "OK";
                    break;
                default:
                    throw new ArrayIndexOutOfBoundsException();
            }
        } catch (InputMismatchException
                | ArrayIndexOutOfBoundsException
                | NumberFormatException
                | NullPointerException ie) {
            returnStr = "ERROR";
        }
        if ("OK".equals(returnStr))
            return "{\"response\":\"OK\"}";
        else if ("ERROR".equals(returnStr))
            return "{\"response\":\"ERROR\",\"reason\":\"No such key\"}";
        else if (returnStr.contains("{"))
            return String.format("{\"response\":\"OK\",\"value\":%s}", returnStr);
        else
            return String.format("{\"response\":\"OK\",\"value\":\"%s\"}", returnStr);
    }

    static String getDBRecord(String key) {
        try {
            Reader reader = Files.newBufferedReader(Paths.get("src/server/data/db.json"));

            JsonObject parser = JsonParser.parseReader(reader).getAsJsonObject();

            JsonElement jk = JsonParser.parseReader(new StringReader(key));

            if (jk.isJsonPrimitive()) {
                return parser.get(key.replaceAll("\"", "")).getAsString();
            } else if (jk.isJsonArray()) {
                JsonObject element = parser;
                JsonArray ja = jk.getAsJsonArray();
                for (int i = 0; i < ja.size(); i++) {
                    JsonElement temp = element.get(ja.get(i).getAsString().replaceAll("\"", ""));
                    assert !(temp == null);
                    if (temp.isJsonObject())
                        element = (JsonObject) temp;
                    else if (temp.isJsonPrimitive())
                        return temp.getAsString();
                    else
                        return null;
                }
                assert element != null;
                return new Gson().toJson(element);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    static void recursivelyWriteJson(JsonWriter writer, JsonReader reader, JsonObject element) throws IOException {
        writer.beginObject();
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            JsonElement new_element = element.get(name);
            if (new_element.isJsonPrimitive()) {
                writer.name(name).value(reader.nextString().replaceAll("\"", ""));
            } else if (new_element.isJsonObject()) {
                writer.name(name);
                recursivelyWriteJson(writer, reader, (JsonObject) new_element);
            }
        }
        reader.endObject();
        writer.endObject();
    }

    static void setDBRecord(String key, String val) {
        JsonObject original = new JsonObject();
        try (Reader reader = Files.newBufferedReader(Paths.get("src/server/data/db.json"))) {
            original = JsonParser.parseReader(reader).getAsJsonObject();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        try (BufferedWriter bufferedWriter = Files.newBufferedWriter(Paths.get("src/server/data/db.json"))) {
            JsonWriter writer = new JsonWriter(bufferedWriter);
            JsonElement je = JsonParser.parseReader(new StringReader(key));
            JsonElement jv = JsonParser.parseReader(new StringReader(val));

            if (je.isJsonPrimitive()) {
                original.remove(key.replaceAll("\"", ""));
                original.add(key.replaceAll("\"", ""), jv);
            } else {
                JsonArray ja = je.getAsJsonArray();
                JsonObject temp = original;
                for (int i = 0; i < ja.size() - 1; i++) {
                    temp = (JsonObject) temp.get(ja.get(i).toString().replaceAll("\"", ""));
                }
                temp.remove(ja.get(ja.size() - 1).toString().replaceAll("\"", ""));
                temp.add(ja.get(ja.size() - 1).toString().replaceAll("\"", ""), jv);
            }

            JsonReader newReader = new JsonReader(new StringReader(original.toString()));
            recursivelyWriteJson(writer, newReader, original);

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    static boolean deleteDBRecord(String key) {
        JsonObject original = new JsonObject();
        try (Reader reader = Files.newBufferedReader(Paths.get("src/server/data/db.json"))) {
            original = JsonParser.parseReader(reader).getAsJsonObject();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        try (BufferedWriter bufferedWriter = Files.newBufferedWriter(Paths.get("src/server/data/db.json"))) {
            JsonWriter writer = new JsonWriter(bufferedWriter);
            JsonElement je = JsonParser.parseReader(new StringReader(key));

            if (je.isJsonPrimitive()) {
                if (!original.has(key.replaceAll("\"", "")))
                    return false;
                original.remove(key.replaceAll("\"", ""));
            } else {
                JsonArray ja = je.getAsJsonArray();
                JsonObject temp = original;
                for (int i = 0; i < ja.size() - 1; i++) {
                    temp = (JsonObject) temp.get(ja.get(i).toString().replaceAll("\"", ""));
                }
                if (!temp.has(ja.get(ja.size() - 1).toString().replaceAll("\"", "")))
                    return false;
                temp.remove(ja.get(ja.size() - 1).toString().replaceAll("\"", ""));
            }

            JsonReader newReader = new JsonReader(new StringReader(original.toString()));
            recursivelyWriteJson(writer, newReader, original);

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return true;
    }

}
