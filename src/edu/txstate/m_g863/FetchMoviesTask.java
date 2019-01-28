package edu.txstate.m_g863;

import java.util.ArrayList;
import java.util.logging.Logger;
 
public class FetchMoviesTask implements Runnable {
	// Logging
	private static final Logger LOGGER = Logger.getAnonymousLogger();
	private int pageNumber;
	private MovieCatalog catalog;

	public FetchMoviesTask (int pageNumber, MovieCatalog catalog) {
		this.pageNumber = pageNumber;
		this.catalog = catalog;
	}

	public int getPageNumber() {
		return pageNumber;
	}
	
	public void run() {
		//Creates an ArrayList of movies and assigns the parsed movies from the API to that list
		//Then adds each movie in that array list to the movie catalog
		ArrayList<Movie> movies;
		movies = Utils.parseMoviesFromJSONString(Utils.fetchMoviesAsJSONString(getPageNumber()));
		for(int i=0; i<movies.size(); i++) {
			catalog.add(movies.get(i));
		}
	}



}
