package edu.txstate.m_g863;

public class Movie {
	private int id;
	private String title;
	private String rating;
	private String releaseYear;
	private String description;


	public Movie(int id, String title, String rating, String releaseYear, String description) {
		super();
		this.id = id;
		this.title = title;
		this.rating = rating;
		this.releaseYear = releaseYear;
		this.description = description;
	}


	@Override
	public String toString() {
		return "Movie [id=" + id + ", title=" + title + ", rating=" + rating + ", releaseYear=" + releaseYear
				+ ", description=" + description + "]";
	}

	
}
