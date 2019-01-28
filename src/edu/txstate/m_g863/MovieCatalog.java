package edu.txstate.m_g863;
import java.util.ArrayList;
import java.util.logging.Logger;


public class MovieCatalog {
	private static final Logger LOGGER = Logger.getAnonymousLogger();
	private static MovieCatalog MyCatalog = null;
	private ArrayList<Movie> movies = new ArrayList<> ();
	private MovieCatalog() {
		
	}
	//creates instance of movieCatalog if one does not exist
	public static MovieCatalog getInstance() {
		if(MyCatalog == null) {
			MyCatalog = new MovieCatalog();
		}
		return MyCatalog;
	}
	//Adds movie to movie catalog instance and logs event
	public synchronized void add(Movie movie) {
		LOGGER.info (Thread.currentThread().getName());
		movies.add(movie);
	}
	//Returns index of movie in movie catalog instance and logs event
	public synchronized int get(int index) {
		LOGGER.info (Thread.currentThread().getName());
		return movies.indexOf(index);
	}
	//returns size of movies array list
	public int size() {
		return movies.size();
	}
	//returns array list of movie catalog
	public ArrayList<Movie> getMovieCatalog() {
		return movies;
	}
}
