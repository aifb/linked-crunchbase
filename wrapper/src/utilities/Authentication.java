package utilities;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;

public class Authentication {
	public static void requireAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		response.setHeader("WWW-Authenticate", "Basic");
		response.setStatus(401);
		response.setContentType("text/html");
//		response.getWriter()
//				.print("Please provide the Crunchbase API key as username with empty password");
		RequestDispatcher rd = request.getRequestDispatcher("/html/authentication.html");
		try {
			rd.forward(request, response);
		} catch (ServletException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static String getAPIKey(HttpServletRequest request) {
		String apikey = null;
		String auth = request.getHeader("Authorization");
		if (auth != null && auth.startsWith("Basic")) {
			String base64 = auth.substring("Basic".length()).trim();
			String cred = new String(Base64.decodeBase64(base64.getBytes()));
			// credentials = username:password
			final String[] values = cred.split(":", 2);
			if (values.length == 2 && values[0].trim().length() > 0) {
				apikey = values[0].trim();
			}
		}
		return apikey;
	}
	

	
}