package edu.txstate.m_g863;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Utils {
	// Logging
	private static final Logger LOGGER = Logger.getAnonymousLogger();

	// End point URI's for fetching movies and posters;
	private static final String URI_MOVIE_BASE        = "https://api.themoviedb.org/3/movie/popular";

	// API Key for themoviedb.org request
	private static final String API_MOVIE_KEY = "d510aad907b8e1adf41e6525223e4d0f";

	// Arguments to themoviedb.org RESTful interfaces
	private static final String API_KEY_PARAM    = "api_key";
	private static final String PAGE_PARAM       = "page";
	
	private static final String QUERY_DELIMITER  = "?";
	private static final String EQUAL_SYMBOL     = "=";
	private static final String AND_SYMBOL       = "&";

	/**
	 * Construct the URL for the themoviedb.org query
	 *  Possible parameters are available at api.themoviedb.org
	 *  https://api.themoviedb.org/3/movie/popular?page=pageNumber&api_key=####
	 * @param pageNumber number of the page to be accessed
	 * @return URL the URL to access the movies in the specified order
	 * @throws MalformedURLException if the URL is not right
	 */
	
	
	public static URL buildMoviesURL (int page) throws MalformedURLException {
		StringBuilder stringBuilder = new StringBuilder(URI_MOVIE_BASE);
		stringBuilder.append(QUERY_DELIMITER);
		stringBuilder.append(API_KEY_PARAM ).append(EQUAL_SYMBOL).append(API_MOVIE_KEY);
		stringBuilder.append(AND_SYMBOL);
		stringBuilder.append(PAGE_PARAM).append(EQUAL_SYMBOL).append(page);
		URL url = new URL(stringBuilder.toString());
		LOGGER.info(url.toString());
		return url;
	}

	// Disconnect from the URL quietly; we don't care about null or exceptions
	public static void closeQuietly(HttpURLConnection urlConnection) {
		if (urlConnection != null) {
			urlConnection.disconnect();
		}
	}

	// Close the reader stream quietly; we don't care about null or exceptions
	public static void closeQuietly(BufferedReader stream) {
		if (stream != null) {
			try {
				stream.close();
			}
			catch (final IOException e) {
				LOGGER.info("Error closing stream " + e);
			}
		}
	}
	
	/**
	 * Uses the RESTful interface from the themoviedb.org to access the
	 * given page of the most popular movies in the database. Invokes the
	 * Utils.buildMoviesURL to build the query string to access the requested
	 * data values. 
	 * @param pageNumber the page number of movies to be accessed
	 * @return string of movies in JSON format
	 */
	public static String fetchMoviesAsJSONString (int pageNumber) {
		// These two need to be declared outside the try/catch
		// so that they can be closed in the finally block.
		HttpURLConnection urlConnection = null;
		BufferedReader reader           = null;

		// Will contain the raw JSON response as a string.
		String moviesJSONString = null;
		try {
			// Construct the URL for the themoviedb.org query
			// Possible parameters are available at api.themoviedb.org

			URL url = Utils.buildMoviesURL(pageNumber);
			LOGGER.info ("Build URI: " + url);

			// Create the request to themoviedb, and open the connection
			urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setRequestMethod("GET");
			urlConnection.connect();

			// Read the input stream into a String
			InputStream inputStream = urlConnection.getInputStream();
			StringBuffer buffer = new StringBuffer();
			if (inputStream == null) {
				// Nothing to do. Make a note in the log and return null
				LOGGER.info("No values returned from themovedb invocation");
				return null;
			}
			reader = new BufferedReader(new InputStreamReader(inputStream));

			String line;
			while ((line = reader.readLine()) != null) {
				// Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
				// But it does make debugging a *lot* easier if you print out the completed
				// buffer for debugging.
				buffer.append(line + "\n");
			}

			if (buffer.length() == 0) {
				// Stream was empty.  No point in parsing.
				return null;
			}
			moviesJSONString = buffer.toString();
			LOGGER.info( "Movies JSON String " + moviesJSONString);

		}
		catch (IOException | JsonIOException e) {
			e.printStackTrace();
		}
		finally {
			Utils.closeQuietly(urlConnection);
			Utils.closeQuietly(reader);
		}
		return moviesJSONString;
	}

	/**
	 * Extracts the movie information from the JSON String that was downloaded.
	 * @param moviesJsonString JSON formatted string of movie information
	 * @return ArrayList<Movie> The list of movies extracted as objects
	 */
	public static ArrayList<Movie> parseMoviesFromJSONString(
			String moviesJsonString)  {
		final String RESULTS               = "results";
		final String MOVIE_ID              = "id";
		final String MOVIE_TITLE           = "title";
		final String RATING                = "vote_average";
		final String RELEASE_DATE          = "release_date";
		final String OVERVIEW              = "overview";

		LOGGER.info(moviesJsonString);

		JsonElement jsonElement            = new JsonParser ().parse(moviesJsonString);
		JsonObject moviesJson              = (JsonObject)jsonElement;
		JsonArray  moviesArray             = moviesJson.getAsJsonArray(RESULTS);
		ArrayList<Movie> movies            = new ArrayList<Movie>();

		for (int i = 0; i < moviesArray.size(); i++) {
			// Get the JSON object representing the movie
			JsonObject jMovie = moviesArray.get(i).getAsJsonObject();
			// Extract the movie details from the JSON object
			int id                = jMovie.getAsJsonPrimitive(MOVIE_ID).getAsInt();
			String title          = jMovie.getAsJsonPrimitive(MOVIE_TITLE).getAsString();
			String rating         = jMovie.getAsJsonPrimitive(RATING).getAsString();
			String releaseYear    = extractReleaseYear(jMovie.getAsJsonPrimitive(RELEASE_DATE).getAsString());
			String overview       = jMovie.getAsJsonPrimitive(OVERVIEW).getAsString();
			// Create a new movie instance and add it to the movies to be returned
			Movie movie = new Movie(id, title, rating, releaseYear, overview);
			LOGGER.info(movie.toString());
			movies.add(movie);
		}
		return movies;
	}

	/**
	 * Extract the release year from the larger release date, if a date is present.
	 * If not, return a "?"
	 * @param releaseDate either a fully qualified date with YYYY-MM-DD or null
	 * @return the year YYYY
	 */
	private static String extractReleaseYear (String releaseDate) {
		final int    RELEASE_YEAR_START    = 0;
		final int    RELEASE_YEAR_LENGTH   = 4;
		String releaseYear;
		if ((releaseDate != null) && (releaseDate.length() >= RELEASE_YEAR_LENGTH)) {
			releaseYear = releaseDate.substring(RELEASE_YEAR_START, RELEASE_YEAR_LENGTH);
		} else {
			releaseYear = "?";
		}
		return releaseYear;
	}
}
