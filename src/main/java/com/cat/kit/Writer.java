package com.cat.kit;

import com.cat.entity.House;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class Writer {

	private static final Path path;

	static {
		URL url = Thread.currentThread().getContextClassLoader().getResource("record.txt");
		try {
			path = Paths.get(url.toURI());
		} catch (URISyntaxException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public static void write(House house) throws IOException {
		Files.write(path, (house + "\n").getBytes(), StandardOpenOption.APPEND);
	}
}
