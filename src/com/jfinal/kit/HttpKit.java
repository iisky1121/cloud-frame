package com.jfinal.kit;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.http.HttpServletRequest;

import com.jfinal.core.Const;

/**
 * HttpUtil
 */
public class HttpKit {

	private static int CONNECT_TIMEOUT=5000;
	private static int READ_TIMEOUT=19000;
	private final static String KEEP_ALIVE = "Keep-Alive";
	
	public static void setConnetTimeout(int millis){
		CONNECT_TIMEOUT = millis;
	}
	
	public static void setReadTimeout(int millis){
		READ_TIMEOUT = millis;
	}
	
	private HttpKit() {
	}

	/**
	 * https 域名校验
	 */
	private class TrustAnyHostnameVerifier implements HostnameVerifier {
		public boolean verify(String hostname, SSLSession session) {
			return true;
		}
	}

	/**
	 * https 证书管理
	 */
	private class TrustAnyTrustManager implements X509TrustManager {
		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}

		public void checkClientTrusted(X509Certificate[] chain, String authType)
				throws CertificateException {
		}

		public void checkServerTrusted(X509Certificate[] chain, String authType)
				throws CertificateException {
		}
	}

	private static enum Method {
		GET, POST, PUT, DELETE, UPLOAD, DOWNLOAD;
	}

	private static String CHARSET = Const.DEFAULT_ENCODING;

	private static final SSLSocketFactory sslSocketFactory = initSSLSocketFactory();
	private static final TrustAnyHostnameVerifier trustAnyHostnameVerifier = new HttpKit().new TrustAnyHostnameVerifier();

	/**
	 * Send GET request
	 */
	public static String get(String url, Map<String, String> queryParas,
			Map<String, String> headers) {
		return execute(url, queryParas, null, headers, Method.GET);
	}

	public static String get(String url, Map<String, String> queryParas) {
		return get(url, queryParas, null);
	}

	public static String get(String url) {
		return get(url, null, null);
	}

	/**
	 * Send POST request
	 */
	public static String post(String url, String data,
			Map<String, String> headers) {
		return execute(url, null, data, headers, Method.POST);
	}

	public static String post(String url, String data) {
		return post(url, data, null);
	}

	/**
	 * Send POSTSSL request
	 */
	public static String postSSL(String url, String data,
			Map<String, String> headers, String certPath, String certPass) {
		return execute(url, null, null, data, headers, Method.POST, certPath,
				certPass);
	}

	public static String postSSL(String url, String data, String certPath,
			String certPass) {
		return postSSL(url, data, null, certPath, certPass);
	}

	/**
	 * Send PUT request
	 */
	public static String put(String url, String data,
			Map<String, String> headers) {
		return execute(url, null, data, headers, Method.PUT);
	}

	public static String put(String url, String data) {
		return put(url, data, null);
	}

	/**
	 * Send DELETE request
	 */
	public static String delete(String url, String data,
			Map<String, String> headers) {
		return execute(url, null, data, headers, Method.DELETE);
	}

	public static String delete(String url, String data) {
		return delete(url, data, null);
	}

	/**
	 * Send upload request
	 */
	public static String upload(String url, Map<String, Object> params) {
		return execute(url, null, params, null, null, Method.UPLOAD, null, null);
	}

	public static String upload(String url, File file) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("file", file);
		return upload(url, params);
	}

	public static String upload(String url, File file, Map<String, String> data) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("file", file);
		for (Entry<String, String> entry : data.entrySet()) {
			params.put(entry.getKey(), entry.getValue());
		}
		return upload(url, params);
	}

	/**
	 * Send download request
	 */
	public static InputStream download(String url) {
		return download(url, null);
	}

	public static InputStream download(String url,
			Map<String, String> queryParas) {
		return download(url, queryParas, null);
	}

	public static InputStream download(String url,
			Map<String, String> queryParas,
			Map<String, List<String>> responseHeaders) {
		try {
			String uri = url;
			if (queryParas != null) {
				uri = buildUrlWithQueryString(url, queryParas);
			}
			// 建立链接
			HttpURLConnection conn = (HttpURLConnection) new URL(uri)
					.openConnection();
			// 设置 HttpURLConnection的断开时间
			conn.setConnectTimeout(CONNECT_TIMEOUT);
			// 设置 HttpURLConnection的请求方式
			conn.setRequestMethod(Method.GET.name());
			// 设置 HttpURLConnection的字符编码
			conn.setRequestProperty("Accept-Charset", CHARSET);
			conn.setRequestProperty("Connection", KEEP_ALIVE);

			// 连接指定的资源
			conn.connect();
			responseHeaders = conn.getHeaderFields();
			return conn.getInputStream();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static String readResponseString(HttpURLConnection conn) {
		StringBuilder sb = new StringBuilder();
		InputStream inputStream = null;
		try {
			if (conn.getResponseCode() >= 400) {
				inputStream = conn.getErrorStream();
			} else {
				inputStream = conn.getInputStream();
			}
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					inputStream, CHARSET));
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line).append("\n");
			}
			return sb.toString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
				}
			}
		}
	}

	private static SSLSocketFactory initSSLSocketFactory() {
		try {
			TrustManager[] tm = { new HttpKit().new TrustAnyTrustManager() };
			SSLContext sslContext = SSLContext.getInstance("TLS"); // ("TLS","SunJSSE");
			sslContext.init(null, tm, new java.security.SecureRandom());
			return sslContext.getSocketFactory();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static SSLSocketFactory initSSLSocketFactory(String certPath,
			String certPass) {
		try {
			KeyStore clientStore = KeyStore.getInstance("PKCS12");
			clientStore.load(new FileInputStream(certPath),
					certPass.toCharArray());
			KeyManagerFactory kmf = KeyManagerFactory
					.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			kmf.init(clientStore, certPass.toCharArray());
			SSLContext sslContext = SSLContext.getInstance("TLSv1");

			TrustManager[] tm = { new HttpKit().new TrustAnyTrustManager() };
			sslContext.init(null, tm, new java.security.SecureRandom());
			return sslContext.getSocketFactory();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Build queryString of the url
	 */
	private static String buildUrlWithQueryString(String url,
			Map<String, String> queryParas) {
		if (queryParas == null || queryParas.isEmpty())
			return url;

		StringBuilder sb = new StringBuilder(url);
		boolean isFirst;
		if (url.indexOf("?") == -1) {
			isFirst = true;
			sb.append("?");
		} else {
			isFirst = false;
		}

		for (Entry<String, String> entry : queryParas.entrySet()) {
			if (isFirst)
				isFirst = false;
			else
				sb.append("&");

			String key = entry.getKey();
			String value = entry.getValue();
			if (StrKit.notBlank(value))
				try {
					value = URLEncoder.encode(value, CHARSET);
				} catch (UnsupportedEncodingException e) {
					throw new RuntimeException(e);
				}
			sb.append(key).append("=").append(value);
		}
		return sb.toString();
	}

	/**
	 * build upload data
	 */
	private static void buildUpload(HttpURLConnection conn,
			Map<String, Object> params) {
		String end = "\r\n", twoHyphens = "--", boundary = "*****";

		try {
			/* 允许Input、Output，不使用Cache */
			conn.setUseCaches(false);
			/* 设置传送的method=POST */
			conn.setRequestMethod(Method.POST.name());
			/* setRequestProperty */
			conn.setRequestProperty("Connection", KEEP_ALIVE);
			conn.setRequestProperty("Charset", CHARSET);
			conn.setRequestProperty("Content-Type",
					"multipart/form-data;boundary=" + boundary);
			/* 设置DataOutputStream */
			DataOutputStream ds = new DataOutputStream(conn.getOutputStream());
			ds.writeBytes(twoHyphens + boundary + end);

			List<File> files = new ArrayList<File>();
			File file = null;
			String str;
			if (params != null) {
				for (Entry<String, Object> entry : params.entrySet()) {
					if (entry.getValue() instanceof File) {
						file = (File) entry.getValue();
						ds.writeBytes("Content-Disposition: form-data; "
								+ "name=\"" + entry.getKey() + "\";filename=\""
								+ file.getName() + "\"" + end);
						ds.writeBytes(end);
						ds.writeBytes(twoHyphens + boundary + twoHyphens + end);

						files.add(file);
					} else if (entry.getValue() instanceof String) {
						str = (String) entry.getValue();
						ds.writeBytes("Content-Disposition: form-data; name=\""
								+ entry.getKey() + "\"" + end);
						ds.writeBytes(end);
						ds.writeBytes(str);
						ds.writeBytes(end);
					}
				}
			}
			/* 设置每次写入1024bytes */
			byte[] buffer = new byte[1024];
			int length = -1;
			/* 从文件读取数据至缓冲区 */
			for (File f : files) {
				FileInputStream fStream = new FileInputStream(f);
				while ((length = fStream.read(buffer)) != -1) {
					ds.write(buffer, 0, length);
				}
				fStream.close();
			}

			ds.writeBytes(end);
			ds.writeBytes(twoHyphens + boundary + twoHyphens + end);

			ds.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static HttpURLConnection getHttpConnection(String url,
			Method method, Map<String, String> headers, String certPath,
			String certPass) throws IOException, NoSuchAlgorithmException,
			NoSuchProviderException, KeyManagementException {
		URL _url = new URL(url);
		HttpURLConnection conn = (HttpURLConnection) _url.openConnection();
		if (conn instanceof HttpsURLConnection) {
			if (!StrKit.isBlank(certPath) && !StrKit.isBlank(certPass)) {
				((HttpsURLConnection) conn)
						.setSSLSocketFactory(initSSLSocketFactory(certPath,
								certPass));
			} else {
				((HttpsURLConnection) conn)
						.setSSLSocketFactory(sslSocketFactory);
			}
			((HttpsURLConnection) conn)
					.setHostnameVerifier(trustAnyHostnameVerifier);
		}

		conn.setDoOutput(true);
		conn.setDoInput(true);
		conn.setRequestMethod(method.name());

		conn.setConnectTimeout(CONNECT_TIMEOUT);
		conn.setReadTimeout(READ_TIMEOUT);

		conn.setRequestProperty("Content-Type",
				"application/x-www-form-urlencoded");
		conn.setRequestProperty(
				"User-Agent",
				"Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.146 Safari/537.36");

		if (headers != null && !headers.isEmpty())
			for (Entry<String, String> entry : headers.entrySet())
				conn.setRequestProperty(entry.getKey(), entry.getValue());

		return conn;
	}

	private static String execute(String url, Map<String, String> queryParas,
			String data, Map<String, String> headers, Method method) {
		return execute(url, queryParas, null, data, headers, method, null, null);
	}

	private static String execute(String url, Map<String, String> queryParas,
			Map<String, Object> params, String data,
			Map<String, String> headers, Method method, String certPath,
			String certPass) {
		HttpURLConnection conn = null;
		try {
			if (method == Method.GET) {
				conn = getHttpConnection(
						buildUrlWithQueryString(url, queryParas), method,
						headers, certPath, certPass);
			} else if (method == Method.UPLOAD) {
				conn = getHttpConnection(url, Method.POST, headers, certPath,
						certPass);
			} else {
				conn = getHttpConnection(url, method, headers, certPath,
						certPass);
			}

			conn.connect();

			if (method == Method.UPLOAD) {
				buildUpload(conn, params);
			} else {
				if (data != null) {
					OutputStream out = conn.getOutputStream();
					out.write(data.getBytes(CHARSET));
					out.flush();
					out.close();
				}
			}

			return readResponseString(conn);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}
	}

	public static String uploadFile(String actionUrl, Map<String, Object> params) {
		String end = "\r\n";
		String twoHyphens = "--";
		String boundary = "*****";

		try {
			URL url = new URL(actionUrl);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			/* 允许Input、Output，不使用Cache */
			con.setDoInput(true);
			con.setDoOutput(true);
			con.setUseCaches(false);
			/* 设置传送的method=POST */
			con.setRequestMethod(Method.POST.name());
			/* setRequestProperty */
			con.setRequestProperty("Connection", KEEP_ALIVE);
			con.setRequestProperty("Charset", CHARSET);
			con.setRequestProperty("Content-Type",
					"multipart/form-data;boundary=" + boundary);
			/* 设置DataOutputStream */
			DataOutputStream ds = new DataOutputStream(con.getOutputStream());
			ds.writeBytes(twoHyphens + boundary + end);

			List<File> files = new ArrayList<File>();
			File file = null;
			String str;
			for (Entry<String, Object> entry : params.entrySet()) {
				if (entry.getValue() instanceof File) {
					file = (File) entry.getValue();
					ds.writeBytes("Content-Disposition: form-data; "
							+ "name=\"" + entry.getKey() + "\";filename=\""
							+ file.getName() + "\"" + end);
					ds.writeBytes(end);
					ds.writeBytes(twoHyphens + boundary + twoHyphens + end);

					files.add(file);
				} else if (entry.getValue() instanceof String) {
					str = (String) entry.getValue();
					ds.writeBytes("Content-Disposition: form-data; name=\""
							+ entry.getKey() + "\"" + end);
					ds.writeBytes(end);
					ds.writeBytes(str);
					ds.writeBytes(end);
				}
			}
			/* 设置每次写入1024bytes */
			byte[] buffer = new byte[1024];
			int length = -1;
			/* 从文件读取数据至缓冲区 */
			for (File f : files) {
				FileInputStream fStream = new FileInputStream(f);
				while ((length = fStream.read(buffer)) != -1) {
					ds.write(buffer, 0, length);
				}
				fStream.close();
			}

			ds.writeBytes(end);
			ds.writeBytes(twoHyphens + boundary + twoHyphens + end);

			ds.flush();
			/* 取得Response内容 */
			return readResponseString(con);
		} catch (Exception e) {
		}
		return null;
	}

	public static String readData(HttpServletRequest request) {
		BufferedReader br = null;
		try {
			StringBuilder result = new StringBuilder();
			br = request.getReader();
			for (String line = null; (line = br.readLine()) != null;) {
				result.append(line).append("\n");
			}

			return result.toString();
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			if (br != null)
				try {
					br.close();
				} catch (IOException e) {
					LogKit.error(e.getMessage(), e);
				}
		}
	}
}