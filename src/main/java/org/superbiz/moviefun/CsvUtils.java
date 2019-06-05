package org.superbiz.moviefun;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CsvUtils {

    public static String readFile(String path) {
        if (path == null) {
            throw new IllegalArgumentException("Path cannot be null");
        }
        ClassLoader loader = CsvUtils.class.getClassLoader();
        InputStream stream = loader.getResourceAsStream(path);
        assert stream != null;
        Scanner scanner = new Scanner(stream).useDelimiter("\\A");

        if (scanner.hasNext()) {
            return scanner.next();
        } else {
            return "";
        }
    }

    public static <T> List<T> readFromCsv(ObjectReader objectReader, String path) {
        try {
            List<T> results = new ArrayList<>();

            MappingIterator<T> iterator = objectReader.readValues(readFile(path));

            while (iterator.hasNext()) {
                results.add(iterator.nextValue());
            }

            return results;
        } catch (IOException e) {
            System.out.println("COULD NOT FIND DATA");
            throw new RuntimeException(e);
        }
    }
}
