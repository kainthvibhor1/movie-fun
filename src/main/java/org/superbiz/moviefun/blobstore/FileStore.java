package org.superbiz.moviefun.blobstore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Optional;

import static java.lang.String.format;

public class FileStore implements BlobStore {
    @Override
    public void put(Blob blob) throws IOException {

        String coverFileName = format("covers/%d", blob.name);
        File targetFile = new File(coverFileName);

        byte[] buffer = new byte[blob.inputStream.available()];
        blob.inputStream.read(buffer);

        targetFile.delete();
        targetFile.getParentFile().mkdirs();
        targetFile.createNewFile();

        try (FileOutputStream outputStream = new FileOutputStream(targetFile)) {
            outputStream.write(buffer);
        }
    }

    @Override
    public Optional<Blob> get(String name) throws IOException {

        String coverFileName = format("covers/%d", name);
        File targetFile = new File(coverFileName);
        return Optional.empty();
    }

    @Override
    public void deleteAll() {

    }
}
