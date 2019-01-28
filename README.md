# Multithreading in Java

This program requests data form themoviedb.org API by creating five threads and having each thread fetch a page of data
from the top 5 pages of popular movies. The movie information is parsed form a JSON string and stored as a Movie object
where it is added to an ArrayList. The movies in the ArrayList are then added into the singleton class MovieCatalog
which has a printAll and lookup functions.
