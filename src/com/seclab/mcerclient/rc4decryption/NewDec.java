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
//		 String inputStr =		 "����:�ùð�������:13812345678����:INCOMINGʱ��:2014-03-04 03:12:10";
//		String inputStr = "1234567����:INCOMING����:13812345678ʱ��:2014-03-04 03:12:10";
//		String inputStr = "���ļ��ж�ȡ�����ܵ�------->����:null   ����:83522971   ����:OUTGOING   ʱ��:2014-03-04 05:26:42 ����:null   ����:15651838550   ����:OUTGOING   ʱ��:2014-03-04 05:02:19 ����:null   ����:15651838550   ����:MISSED  ʱ��:2014-03-04 05:26:42 ����:null   ����:15651838550   ����:OUTGOING   ʱ��:2014-03-04 05:02:19 ����:null   ����:15651838550   ����:MISSED";
		String encryptedStrFromText = OriginalText(toBeDecryptedPath);
//		String encryptedStrFromTextToString = hex2bin(encryptedStrFromText);
		String key = "12345";
		// String gbkLines = URLDecoder.decode(URLEncoder.encode(lines, "GBK"),
		// "GBK");
		// System.out.println("���ļ���ȡ����������------>" + lines);
		String decryptedStr = RC4.decry_RC4(encryptedStrFromText, key);
		System.err.println("���ļ��ж�ȡ�����ܵ�------->" + decryptedStr);
		
		
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
	* ʮ������ת���ַ���
	*  
	* @param hex
	* String ʮ������
	* @return String ת������ַ���
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
	//System.out.println(��hex2bin:��+new String(bytes));
	return new String(bytes);
	}


}
