package com.cat.house;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class WEBSource {
	static final String url = "http://xm.ganji.com/fang5/";

	public static void main(String[] args) throws IOException {
		get(url);
	}

	public static void get(String url) throws IOException {
		CloseableHttpClient client = HttpClients.createDefault();
		CloseableHttpResponse response = client.execute(new HttpGet(url));

		System.out.println(response.getStatusLine());
		HttpEntity entity = response.getEntity();

		System.out.println("content:\n" + content(entity.getContent()));
		EntityUtils.consume(entity);

		response.close();
	}

	private static String content(InputStream stream) throws IOException {
		StringBuilder builder = new StringBuilder();

		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

		String inputLine;
		while ((inputLine = reader.readLine()) != null) {
			builder.append(inputLine);
		}
		reader.close();

		return builder.toString();
	}
}
