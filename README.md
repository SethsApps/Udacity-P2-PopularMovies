# Udacity-P1-PopularMovies
My Popular Movies app for Project 1 of the Udacity Android Developer Nanodegree.

The app displays content from [TMDb](https://www.themoviedb.org) allowing the user to sort movies by popularity and ratings and view further detail about each movie.

Further information about the project can be found [here](https://docs.google.com/document/d/1ZlN1fUsCSKuInLECcJkslIqvpKlP7jWL2TP9m6UiA6I/pub?embedded=true).

## API Key for TMDb Required

In order for this app to fetch content using the themoviedb.org API, an API key is required.  You can add your own by... **TODO: Add detail about adding APIKey**

## Required Tasks for Project 1 Nanodegree

- Build a UI layout for multiple Activities.
- Launch these Activities via Intent.
- Fetch data from themovieDB API

## TODO

##### User Interface - Layout
- ~~Movies are displayed in the main layout via a grid of their corresponding movie poster thumbnails~~ ([6e61f4ce4acc5458ef3dacf32e468eac43e5071a](https://git.io/vw8fy))
- UI contains an element (i.e a spinner or settings menu) to toggle the sort order of the movies by: most popular, highest rated
- UI contains a screen for displaying the details for a selected movie
- Movie details layout contains title, release date, movie poster, vote average, and plot synopsis.

##### User Interface - Function
- When a user changes the sort criteria (“most popular and highest rated”) the main view gets updated correctly.
- When a movie poster thumbnail is selected, the movie details screen is launched

##### Network API Implementation 
- ~~In a background thread, app queries the /movie/popular or /movie/top_rated API for the sort criteria specified in the settings menu.~~ ([6e61f4ce4acc5458ef3dacf32e468eac43e5071a](https://git.io/vw8fy))
- This query can also be used to fetch the related metadata needed for the detail view.

## Consider using
  - [Fresco](http://frescolib.org/) for image loading
  - [Gson](https://github.com/google/gson) for JSON processing
  - [OkHttp](http://square.github.io/okhttp/) as HTTP+SPDY Client
  - [Retrofit](http://square.github.io/retrofit/) as REST client
  - [Robolectric](https://github.com/robolectric/robolectric) and/or [Robotium](https://code.google.com/p/robotium/) for unit testing
  - [Xtends](http://futurice.com/blog/android-development-has-its-own-swift)
  - Automate Content Providers:
    - [ProviGen](https://github.com/TimotheeJeannin/ProviGen)
    - [schematic](https://github.com/SimonVT/schematic)
    - [simple provider](https://github.com/Triple-T/simpleprovider)
  - ORM instead of SQLite/ContentProvider:
    - [Realm](https://realm.io/docs/java)
    - [GreenDAO](http://greendao-orm.com/)
    - [Sugar ORM](http://satyan.github.io/sugar/index.html)
