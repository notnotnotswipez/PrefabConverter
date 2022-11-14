package me.swipez;

import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Main {

    private static Logger logger;
    public static File prefab;
    public static String prefabString;
    public static String prefabOriginAssets;
    public static String prefabConversionAssets;
    public static HashMap<String, String> guidMap = new HashMap<>();

    public static void main(String[] args) {
        logger = Logger.getLogger(Main.class.getName());

        System.setProperty("java.util.logging.SimpleFormatter.format",
                "[%1$tF %1$tT] [%4$-7s] %5$s %n");
        FileHandler fileHandler = null;
        try {
            fileHandler = new FileHandler("status.log");
        }
        catch (Exception exception) {
            // ignored
        }

        SimpleFormatter formatter = new SimpleFormatter();
        fileHandler.setFormatter(formatter);

        logger.addHandler(fileHandler);

        try {
            runProgram();
        } catch (FileNotFoundException e) {
            logger.info(e.toString());
            try {
                readInput();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private static void runProgram() throws FileNotFoundException {

        File mainDirectory = new File(System.getProperty("user.dir"));

        logger.info("Give the directory of the prefab you want to convert.");

        String prefabPath;

        try {
            prefabPath = readInput();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        prefab = new File(prefabPath);

        if (!prefab.exists()) {
            logger.info("The prefab does not exist.");
            return;
        }

        logger.info("Reading prefab text...");
        // Read the text in the prefab via a BufferedReader
        BufferedReader reader = new BufferedReader(new InputStreamReader( new FileInputStream(prefab)));
        String line;
        StringBuilder builder = new StringBuilder();
        try {
            while ((line = reader.readLine()) != null) {
                builder.append(line).append(System.lineSeparator());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        logger.info("Read prefab!");

        // Convert the prefab to a string
        prefabString = builder.toString();

        logger.info("Please input the directory of the assets folder of the project this prefab CAME FROM:");

        try {
            prefabOriginAssets = readInput();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        logger.info("Please input the directory of the assets folder of the project you want the prefab to CONVERT TO:");

        try {
            prefabConversionAssets = readInput();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        logger.info("Prefab text: "+prefabString);

        logger.info("Finding all GUIDs in prefab...");
        List<GUIDFile> foundGuids = new ArrayList<>();
        int index = 0;
        while (prefabString.substring(index).contains("guid: ")) {
            int guidStart = prefabString.indexOf("guid: ", index + 1);
            String guid = prefabString.substring(guidStart + 6, guidStart + 6 + 32);
            foundGuids.add(new GUIDFile(guidStart, guid));
            index = guidStart + 5;
            logger.info("Found GUID: " + guid);
        }

        List<File> originalAssetMetaFiles = new ArrayList<>();

        if (guidMap.isEmpty()){
            logger.info("Storing GUID map to memory...");
            recursiveMetaSearch(new File(prefabOriginAssets), originalAssetMetaFiles, foundGuids);
        }
        else {
            logger.info("GUID map already stored in memory.");
            logger.info("Reading from memory...");
            for (GUIDFile guidFile : foundGuids) {
                if (guidMap.containsKey(guidFile.guid)){
                    guidFile.setFile(new File(guidMap.get(guidFile.guid)));
                }
            }
        }


        logger.info("Linked all meta files to their respective references!");

        logger.info("STARTING CONVERSION PROCESS...");
        recursiveMetaReplace(new File(prefabConversionAssets), foundGuids);

        logger.info("Finished replacement of scripts.");

        File finalizeFolder = new File(mainDirectory.getPath()+"/FinalConversion");
        if (finalizeFolder.exists()) {
            try {
                Files.deleteIfExists(finalizeFolder.toPath());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        finalizeFolder.mkdirs();

        File prefabReplaced = new File(finalizeFolder.getPath()+"/"+prefab.getName().replace(".prefab", "_converted.prefab"));
        try {
            prefabReplaced.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            writeToFile(prefabReplaced, prefabString);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        logger.info("Created prefab!");

        logger.info("Copying necessary assets");


        for (GUIDFile remaining : foundGuids) {
            try {
                if (!remaining.wasConverted){
                    File metaDestination = new File(getMirrorDirectory(remaining.metaFile, finalizeFolder));
                    File assetDestination = new File(getMirrorDirectory(new File(remaining.metaFile.getPath().replace(".meta", "")), finalizeFolder));
                    makeDirectoryProper(metaDestination);
                    try {
                        FileUtils.copyFile(remaining.metaFile, metaDestination);
                        FileUtils.copyFile(new File(remaining.metaFile.getPath().replace(".meta", "")), assetDestination);
                    } catch (IOException e) {
                        // ignored
                    };
                    logger.info("Copied asset: "+assetDestination.getName());
                }
            }
            catch (Exception exception){
                // ignored
            }
        }

        logger.info("DONE!");

        logger.info("Would you like to run this program again? (y/n)");
        try {
            String result = readInput();
            if (result.equalsIgnoreCase("y")){
                runProgram();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void writeToFile(File file, String string) throws IOException {
        FileWriter writer = new FileWriter(file.getPath());
        writer.write(string);
        writer.close();
    }

    private static String getMirrorDirectory(File file, File newParent){
        return file.getPath().replace(prefabOriginAssets, newParent.getPath());
    }

    private static void makeDirectoryProper(File file) {
        File nonImportantDirectory = new File(file.getPath().replace(file.getName(), ""));
        nonImportantDirectory.mkdirs();
    }

    private static String getGUID(File file) throws FileNotFoundException {
        BufferedReader reader = new BufferedReader(new InputStreamReader( new FileInputStream(file)));
        String line;
        StringBuilder builder = new StringBuilder();
        try {
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        String metaFileString = builder.toString();
        int guidStart = metaFileString.indexOf("guid: ");
        return metaFileString.substring(guidStart + 6, guidStart + 6 + 32);
    }

    private static void recursiveMetaReplace(File directory, List<GUIDFile> originals) throws FileNotFoundException {
        for (File file : directory.listFiles()) {
            if (file.isDirectory()) {
                recursiveMetaReplace(file, originals);
            } else if (file.getName().endsWith(".meta")) {
                logger.info("Found meta file in destination proj: " + file.getName());
                for (GUIDFile guidFile : originals) {
                    if (guidFile.metaFile != null){
                        String properName = guidFile.metaFile.getName().replace(".meta", "");
                        String newGuid = getGUID(file);
                        String newProperName = file.getName().replace(".meta", "");
                        if (properName.equals(newProperName)) {
                            prefabString = prefabString.replace(guidFile.guid, newGuid);
                            guidFile.wasConverted = true;
                            logger.info("Replaced reference to: " + newProperName);
                        }
                    }
                }
            }
        }
    }

    private static void recursiveMetaSearch(File directory, List<File> metaFiles, List<GUIDFile> filter) throws FileNotFoundException {
        for (File file : directory.listFiles()) {
            if (file.isDirectory()) {
                recursiveMetaSearch(file, metaFiles, filter);
            } else if (file.getName().endsWith(".meta")) {
                logger.info("Found meta file: " + file.getName());
                String guid = getGUID(file);
                guidMap.put(guid, file.getPath());
                for (GUIDFile guidFile : filter){
                    if (guidFile.guid.equals(guid)){
                        guidFile.setFile(file);
                        logger.info("This meta file IS ON THE PREFAB.");
                        metaFiles.add(file);
                    }
                }
            }
        }
    }

    public static String readInput() throws IOException {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(System.in));

        // Reading data using readLine
        return reader.readLine();
    }
}