package org.superbiz.moviefun.blobstore;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static java.lang.String.format;

public class FileStore implements BlobStore {
    @Override
    public void put(Blob blob) throws IOException {

        // Create file name with content-type
        String ext = blob.contentType.split("/")[1];
        String name = blob.name + "." + ext;
        File targetFile = getCoverFile(name);

        String foundPath = getFilePath(name);
        if (foundPath != null) {
            // Delete the original file
            targetFile.delete();
            targetFile.getParentFile().mkdirs();
            targetFile.createNewFile();
        }
        byte[] buffer = new byte[blob.inputStream.available()];
        blob.inputStream.read(buffer);
        try (FileOutputStream outputStream = new FileOutputStream(targetFile)) {
            outputStream.write(buffer);
        }

    }

    @Override
    public Optional<Blob> get(String name) throws IOException {
        // Traverse folder path
        String foundPath = getFilePath(name);
        if (foundPath == null) {
            return Optional.empty();
        }
        File file = getCoverFile(foundPath);
        InputStream ip = new FileInputStream(file);
        Blob blob = new Blob(name, ip, Files.probeContentType(file.toPath()));
        return Optional.of(blob);
    }

    @Override
    public void deleteAll() {

    }

    private File getCoverFile(String name) {
        String coverFileName = format("covers/%s", name);
        return new File(coverFileName);
    }

    private ArrayList<String> getFileNames() {
        File f = new File("covers");
        if (f.list() != null) {
            return new ArrayList<>(Arrays.asList(f.list()));
        }
        return null;
    }

    /**
     * Returns the full file name, if found else null
     *
     * @param name string value to check
     * @return null | found name
     */
    private String getFilePath(String name) {
        List<String> names = getFileNames();
        if (names != null) {
            for (String fName : names) {
                String actualName = fName.split("\\.")[0];
                if (actualName.equalsIgnoreCase(name)) {
                    return fName;
                }
            }
            return null;
        }
        return null;
    }
}
