package client;

import com.beust.jcommander.JCommander;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import com.beust.jcommander.Parameter;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    @Parameter(names="-t", description="Type of request")
    private String type;

    @Parameter(names="-k", description="Key of the database value")
    private List<String> key = new ArrayList<>();

    @Parameter(names="-v", description="Value, in json")
    private String value;

    @Parameter(names="-in", description="Input File")
    private String inputFile;

    public static void main(String[] args) {
        try (Socket server = new Socket(InetAddress.getByName("127.0.0.1"), 8899);
             DataInputStream input = new DataInputStream(server.getInputStream());
             DataOutputStream output = new DataOutputStream(server.getOutputStream())) {
            System.out.println("Client started!");
            Main main = new Main();
            JCommander.newBuilder()
                    .addObject(main)
                    .build()
                    .parse(args);

            Gson gson = new Gson();

            String inputData = null;
            if (main.inputFile != null) {
                Scanner scanner = new Scanner(new File("src/client/data/" + main.inputFile));
                StringBuilder data = new StringBuilder();
                while (scanner.hasNext()) {
                    data.append(scanner.nextLine().replaceAll("\n", ""));
                }
                inputData = data.toString();
                JsonObject jsonObject = (JsonObject) JsonParser.parseString(data.toString());

                main.type = jsonObject.get("type").toString();
                try {
                    main.key = gson.fromJson(jsonObject.get("key"), List.class);
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                    String tempKey = gson.fromJson(jsonObject.get("key"), String.class);
                    main.key.add(tempKey);
                }
                main.value = gson.toJson(jsonObject.get("value"));

                scanner.close();
            }

            String request;
            if (main.value == null)
                if (main.key.isEmpty())
                    request = String.format("{\"type\":\"%s\"}", main.type);
                else
                    request = String.format("{\"type\":\"%s\", \"key\":\"%s\"}", main.type, main.key.size() > 1 ? main.key : main.key.get(0));
            else
                request = String.format("{\"type\":\"%s\",\"key\":\"%s\",\"value\":\"%s\"}", main.type, main.key.size() > 1 ? main.key : main.key.get(0), main.value);
            System.out.printf("Sent: %s%n", inputData != null ? inputData : request);
            output.writeUTF(inputData != null ? inputData : request);
            String in = input.readUTF();
            System.out.println("Received: " + in);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
