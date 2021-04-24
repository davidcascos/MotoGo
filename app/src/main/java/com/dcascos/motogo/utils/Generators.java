package com.dcascos.motogo.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Generators {

	public static String genRandomUsername() {
		String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "0123456789" + "abcdefghijklmnopqrstuvxyz";
		StringBuilder sb = new StringBuilder(15);

		for (int i = 0; i <= 15; i++) {
			int index = (int) (AlphaNumericString.length() * Math.random());
			sb.append(AlphaNumericString.charAt(index));
		}
		return sb.toString();
	}

	public static String photoNameFormater() {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
		return simpleDateFormat.format(new Date());
	}
}
