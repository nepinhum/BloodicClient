package net.bloodic.update;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class BloodicUpdater
{
	private static final String API_URL =
		"https://api.github.com/repos/nxpinhum5326/BloodicClient/releases/latest";
	private static final String RELEASES_PAGE =
		"https://github.com/nxpinhum5326/BloodicClient/releases";
	
	private final Version currentVersion;
	private volatile Version latestVersion;
	private volatile String downloadPage;
	private volatile String lastError;
	private volatile Status status = Status.IDLE;
	
	public enum Status
	{
		IDLE,
		CHECKING,
		READY,
		FAILED
	}
	
	public BloodicUpdater(Version currentVersion)
	{
		this.currentVersion = currentVersion;
	}
	
	public void checkForUpdatesAsync()
	{
		CompletableFuture.runAsync(this::checkForUpdates)
			.exceptionally(throwable -> {
				lastError = throwable.getMessage();
				status = Status.FAILED;
				return null;
			});
	}
	
	private void checkForUpdates()
	{
		status = Status.CHECKING;
		HttpURLConnection connection = null;
		
		try {
			URL url = new URL(API_URL);
			connection = (HttpURLConnection)url.openConnection();
			connection.setRequestProperty("Accept", "application/vnd.github+json");
			connection.setRequestProperty("User-Agent", "BloodicClient");
			connection.setConnectTimeout(5000);
			connection.setReadTimeout(5000);
			
			int responseCode = connection.getResponseCode();
			if (responseCode != 200)
				throw new IOException("Unexpected response " + responseCode);
			
			String body = readBody(connection);
			String tagName = extractField(body, "tag_name");
			String htmlUrl = extractField(body, "html_url");
			
			if (tagName == null || tagName.isBlank())
				throw new IOException("Missing tag_name from GitHub response");
			
			if (tagName.startsWith("v") || tagName.startsWith("V"))
				tagName = tagName.substring(1);
			
			latestVersion = Version.parse(tagName);
			downloadPage = htmlUrl != null ? htmlUrl : RELEASES_PAGE;
			status = Status.READY;
		} catch (IOException e) {
			lastError = e.getMessage();
			status = Status.FAILED;
		} finally {
			if (connection != null)
				connection.disconnect();
		}
	}
	
	private String readBody(HttpURLConnection connection) throws IOException
	{
		StringBuilder builder = new StringBuilder();
		
		try (BufferedReader reader = new BufferedReader(
			new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
			String line;
			while ((line = reader.readLine()) != null)
				builder.append(line);
		}
		
		return builder.toString();
	}
	
	private String extractField(String body, String key)
	{
		Pattern pattern = Pattern.compile("\"" + Pattern.quote(key)
			+ "\"\\s*:\\s*\"([^\"]+)\"");
		Matcher matcher = pattern.matcher(body);
		
		if (matcher.find())
			return matcher.group(1);
		
		return null;
	}
	
	public boolean isUpdateAvailable()
	{
		return latestVersion != null && latestVersion.isNewerThan(currentVersion);
	}
	
	public Status getStatus()
	{
		return status;
	}
	
	public Version getCurrentVersion()
	{
		return currentVersion;
	}
	
	public Version getLatestVersion()
	{
		return latestVersion;
	}
	
	public String getDownloadPage()
	{
		return downloadPage != null ? downloadPage : RELEASES_PAGE;
	}
	
	public String getLastError()
	{
		return lastError;
	}
}
