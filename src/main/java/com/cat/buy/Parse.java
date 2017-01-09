package com.cat.buy;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Parse {

	static final Map<String, Info> MAP = new ConcurrentHashMap<>();

	static final String base = "http://xm.ganji.com";
	static final String url = base + "/fang5/o";
	//http://xm.ganji.com/fang5/siming/o3/

	static final Path path;

	static {
		URL url = Thread.currentThread().getContextClassLoader().getResource("record.txt");
		try {
			path = Paths.get(url.toURI());
		} catch (URISyntaxException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public static void main(String[] args) throws Exception {
		ExecutorService service = Executors.newCachedThreadPool();
		for (int i = 1; i < 12; i++) {
			List<String> urls = getUrl(i);
			service.submit(() -> parseList(urls));
		}
		service.shutdown();
	}

	private static List<String> getUrl(int page) {
		final String link = url + page;
		List<String> list = new ArrayList<>();

		Document document;
		try {
			document = Jsoup.connect(link).get();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		Elements elements = document.select(".listBox ul.list-style1 > li div.info-title > a.list-info-title");
		if (elements.size() == 0) {
			return null;
		}

		for (Element element : elements) {
			list.add(base + element.attr("href"));
		}

		return list;
	}

	private static void parseList(List<String> links) {
		if (links == null || links.isEmpty()) {
			return;
		}
		for (String link : links) {
			try {
				parseSingle(link);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private static void parseSingle(String link) throws Exception {
		if (MAP.containsKey(link)) {
			return;
		}
		Document document = Jsoup.connect(link).get();
		Elements lis = document.select("ul.basic-info-ul > li");

		Info info = new Info();
		info.setLink(link);
		for (Element li : lis) {
			String html = li.html();
			String text = li.text();
			if (html.contains("售<i class=\"letter-space-8\"></i>价")) {
				String amount = text.split("：")[1].split(" ")[1].trim();
				info.setAmount(new BigDecimal(amount));
			}
			if (html.contains("户<i class=\"letter-space-8\"></i>型")) {
				String[] split = text.split("-");
				String type = split[0].split("：")[1].trim();
				info.setType(type);

				String area = split[1].replaceAll("㎡", "").trim();
				info.setArea(new BigDecimal(area));
			}
			if (html.contains("楼<i class=\"letter-space-8\"></i>层")) {
				String floor = text.split("：")[1].trim();
				info.setFloor(floor);
			}
			if (html.contains("小<i class=\"letter-space-8\"></i>区")) {
				String location = text.split("：")[1].trim().split(" ")[0].trim();
				info.setLocation(location);
			}
		}
		System.out.println(info);
		BigDecimal area = info.getArea();
		if (area != null && area.compareTo(BigDecimal.ZERO) > 0) {
			info.setPrice(info.getAmount().divide(area));
		}
		MAP.put(link, info);
//		write(info.toString());
	}

	private static void write(String info) throws Exception {
		info = info + "\n";
		Files.write(path, info.getBytes(), StandardOpenOption.APPEND);
	}

//	private static double price(String s) {
//		Matcher matcher = price_pattern.matcher(s);
//		String result = null;
//		if (matcher.find()) {
//			result = matcher.group(1);
//		}
//		return result == null ? -1 : Double.parseDouble(result);
//	}
//
//	private static final Pattern type_pattern = Pattern.compile("：\\S?(\\w+)\\S?-");
//	private static final Pattern price_pattern = Pattern.compile("\\s*(\\d+\\.?\\d?)");
}
