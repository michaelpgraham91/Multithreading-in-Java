package edu.txstate.m_g863;

import java.util.ArrayList;

class main {

	public static void main(String[] args) {
		int ONE_SECOND = 1000;
		
		//create instance of movie catalog
		MovieCatalog catalog = MovieCatalog.getInstance();
		
		//create 5 threads that take as a parameter an instance of fetch movies task and thread name
		//each instance should be passed to a different page number and movies should be retrieved in the instance of catalog
		Thread threadPage1 = new Thread (new FetchMoviesTask(1, catalog), "page1");
		Thread threadPage2 = new Thread (new FetchMoviesTask(2, catalog), "page2");
		Thread threadPage3 = new Thread (new FetchMoviesTask(3, catalog), "page3");
		Thread threadPage4 = new Thread (new FetchMoviesTask(4, catalog), "page4");
		Thread threadPage5 = new Thread (new FetchMoviesTask(5, catalog), "page5");
		
		
		//start threads
		threadPage1.start();
		threadPage2.start();
		threadPage3.start();
		threadPage4.start();
		threadPage5.start();
		
		
		//try block: Put the main thread to sleep for 10 seconds
		try {
			Thread.sleep(10*ONE_SECOND);
		}
		//catch block: catch any errors resulting and print them
		catch(Exception e) {
			e.printStackTrace();
		}
		//finally block: print the number of films total in the catalog and each film in the catalog
		finally {
			System.out.println("Size of catalog is: " + catalog.size());
			printMovies(catalog.getMovieCatalog());
		}
		
	}
	//Used to print all Movie object in MovieCatalog
	public static void printMovies(ArrayList<Movie> movies) {
		for(int i=0; i<movies.size(); i++) {
			Movie aMovie = movies.get(i);
			System.out.println(aMovie);
		}
	}
	
}
