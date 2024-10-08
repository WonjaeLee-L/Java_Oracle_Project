package main;

import service.FishService;

public class Main {

	public static void main(String[] args) {
		System.out.println("Fish World");
		FishService fs = new FishService();
		fs.menu();

		// dao단위 테스트 : DAO add 기능 테스트, 결과 : 성공
		/*
		 * FishDAO f = new FishDAO(); FishDTO d = new FishDTO(); d.setId("test");
		 * d.setPwd("test"); f.add(d);
		 */

	}

}
