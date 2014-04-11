package com.seclab.mcerclient.rc4decryption;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

public class NewDec {


	public static void start(String toBeDecryptedPath,String decryptedPath) throws IOException {
//		 String inputStr =		 "姓名:姑姑爱大雕号码:13812345678类型:INCOMING时间:2014-03-04 03:12:10";
//		String inputStr = "1234567类型:INCOMING号码:13812345678时间:2014-03-04 03:12:10";
//		String inputStr = "从文件中读取并解密的------->姓名:null   号码:83522971   类型:OUTGOING   时间:2014-03-04 05:26:42 姓名:null   号码:15651838550   类型:OUTGOING   时间:2014-03-04 05:02:19 姓名:null   号码:15651838550   类型:MISSED  时间:2014-03-04 05:26:42 姓名:null   号码:15651838550   类型:OUTGOING   时间:2014-03-04 05:02:19 姓名:null   号码:15651838550   类型:MISSED";
		String encryptedStrFromText = OriginalText(toBeDecryptedPath);
//		String encryptedStrFromTextToString = hex2bin(encryptedStrFromText);
		String key = "12345";
		// String gbkLines = URLDecoder.decode(URLEncoder.encode(lines, "GBK"),
		// "GBK");
		// System.out.println("从文件读取进来的密文------>" + lines);
		String decryptedStr = RC4.decry_RC4(encryptedStrFromText, key);
		System.err.println("从文件中读取并解密的------->" + decryptedStr);
		
		
		OutputStream os;
		os = new FileOutputStream(decryptedPath);
		OutputStreamWriter osw = new OutputStreamWriter(os,
				Charset.forName("GBK"));
		try {
			osw.write(decryptedStr);
			osw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}


	public static String OriginalText(String destFile) throws IOException {
		String lines = "";
		BufferedReader br = new BufferedReader(new InputStreamReader(
				new FileInputStream(destFile), "gbk"));
		String line = null;
		while ((line = br.readLine()) != null) {
			lines = lines + line;
		}
		br.close();
		return lines;
	}

	/**
	* 十六进制转换字符串
	*  
	* @param hex
	* String 十六进制
	* @return String 转换后的字符串
	*/
	public static String hex2bin(String hex) {
	String digital = "0123456789ABCDEF";
	char[] hex2char = hex.toCharArray();
	byte[] bytes = new byte[hex.length() / 2];
	int temp;
	for (int i = 0; i < bytes.length; i++) {
	temp = digital.indexOf(hex2char[2 * i]) * 16;
	temp += digital.indexOf(hex2char[2 * i + 1]);
	bytes[i] = (byte) (temp & 0xff);
	}
	//System.out.println(“hex2bin:”+new String(bytes));
	return new String(bytes);
	}


}
