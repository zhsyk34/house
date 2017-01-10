package com.cat.lianjia;

import com.cat.dao.HouseDao;
import com.cat.entity.House;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiredArgsConstructor
public class Parse {

	private static final String home = "http://xm.lianjia.com/ershoufang/pg";
	private static final Map<String, House> map = new ConcurrentHashMap<>();
	private static final Pattern ap = Pattern.compile("\\|\\s?(\\d+\\.?\\d*)平米\\s*\\|");
	private static final Pattern pp = Pattern.compile("\\S?(\\d+)\\s?");
	private final HouseDao houseDao;

	private static BigDecimal price(String money) {
		Matcher matcher = pp.matcher(money);
		if (matcher.find()) {
			return new BigDecimal(matcher.group(1));
		}
		return null;
	}

	private static BigDecimal area(String desc) {
		Matcher matcher = ap.matcher(desc);
		if (matcher.find()) {
			return new BigDecimal(matcher.group(1));
		}
		return null;
	}

	public void start() throws IOException, InterruptedException {
//		ExecutorService service = Executors.newCachedThreadPool();
		for (int i = 0; i < 120; i++) {
			url(i);
			Thread.sleep(5000);
//			service.submit(() -> url(k));
		}
//		service.shutdown();
	}

	private void url(int i) {
		System.out.println("---------------------------begin");
		try {
			Document document = Jsoup.connect(home + i).get();
			Element ul = document.select("ul.sellListContent").get(0);

			Elements divs = ul.select(">li.clear>div.info");
			House house;
			for (Element div : divs) {
				house = new House();
				String link = div.select(">div.title>a").attr("href");
				if (map.containsKey(link)) {
					continue;
				}
				String title = div.select(">div.title").text();
				String desc = div.select(">div.address>div.houseInfo").text();
				BigDecimal area = area(desc);
				System.out.println(area);

				String floor = div.select(">div.flood>div.positionInfo").text();
				String total = div.select(">div.priceInfo>div.totalPrice>span").text();
				String money = div.select(">div.priceInfo>div.unitPrice>span").text();

				house.setLink(link).setTitle(title).setDescription(desc).setArea(area).setFloor(floor).setAmount(new BigDecimal(total)).setPrice(price(money));
				System.out.println(house);

				map.put(link, house);
				houseDao.save(house);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
