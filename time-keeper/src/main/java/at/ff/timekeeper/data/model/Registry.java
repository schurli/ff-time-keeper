package at.ff.timekeeper.data.model;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

// { PUBLISHER_ID/MAC/SERVICE_UUID/CHARACTERISTIC_UUID => [ Command ] } } }
public class Registry extends HashMap<String, List<Command>> {

    private final static String SEP = "/";

    private static String[] split(String topic) {
        String[] parts = topic.split(SEP);
        if (parts.length != 4) {
            throw new IllegalArgumentException("Invalid topic format.");
        }
        return parts;
    }

    public static String getPublisher(String topic) {
        return split(topic)[0];
    }

    public static String getMac(String topic) {
        return split(topic)[1];
    }

    public static String getService(String topic) {
        return split(topic)[2];
    }

    public static String getCharacteristic(String topic) {
        return split(topic)[3];
    }

    public void addToTopic(String topic, Command command) {
        addToTopic(topic, Collections.singletonList(command));
    }

    public void addToTopic(String topic, List<Command> commands) {

        split(topic); // sanity check of topic format
        put(topic, commands);

    }

    public boolean hasMac(String mac) {
        return getMacs().contains(mac);
    }


    public List<String> getPublishers() {
        List<String> publishers = new ArrayList<>();
        for (String topic : keySet()) {
            publishers.add(getPublisher(topic));
        }
        return publishers;
    }

    public List<String> getMacs() {
        List<String> macs = new ArrayList<>();
        for (String topic : keySet()) {
            macs.add(getMac(topic));
        }
        return macs;
    }

    public List<String> getServices() {
        List<String> services = new ArrayList<>();
        for (String topic : keySet()) {
            services.add(getService(topic));
        }
        return services;
    }

    public List<String> getCharacteristics() {
        List<String> characteristic = new ArrayList<>();
        for (String topic : keySet()) {
            characteristic.add(getCharacteristic(topic));
        }
        return characteristic;
    }

    public List<Command> getCommands() {
        List<Command> commands = new ArrayList<>();
        for (List<Command> c : values()) {
            commands.addAll(c);
        }
        return commands;
    }

    public Registry toMacRegistry(String publisher) {
        Registry registry = new Registry();
        for (String topic : keySet()) {
            if (getPublisher(topic).equals(publisher)) {
                registry.put(topic, get(topic));
            }
        }
        return registry;
    }

    public Registry toServiceRegistry(String mac) {
        Registry registry = new Registry();
        for (String topic : keySet()) {
            if (getMac(topic).equals(mac)) {
                registry.put(topic, get(topic));
            }
        }
        return registry;
    }

    public Registry toCharacteristicRegistry(String service) {
        Registry registry = new Registry();
        for (String topic : keySet()) {
            if (getService(topic).equals(service)) {
                registry.put(topic, get(topic));
            }
        }
        return registry;
    }

    public Registry toCommandRegistry(String characteristic) {
        Registry registry = new Registry();
        for (String topic : keySet()) {
            if (getCharacteristic(topic).equals(characteristic)) {
                registry.put(topic, get(topic));
            }
        }
        return registry;
    }

    @NotNull
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        for (String publisher : getPublishers()) {
            Registry publishers = toMacRegistry(publisher);
            for (String mac : publishers.getMacs()) {
                Registry services = toServiceRegistry(mac);
                for (String service : services.getServices()) {
                    Registry characteristics = services.toCharacteristicRegistry(service);
                    for (String characteristic : characteristics.getCharacteristics()) {
                        for (Command command : characteristics.getCommands()) {
                            builder.append(String.format("%s/%s/%s/%s %s", publisher, mac, service, characteristic, command.type.name()));
                        }
                    }
                }
            }
        }
        return builder.toString();
    }
}
