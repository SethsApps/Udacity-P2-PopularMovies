# Udacity-P2-PopularMovies
My Popular Movies app for Project 2 of the Udacity Android Developer Nanodegree.

The app improves upon [Popular Movies Project 1](https://github.com/SethsApps/Udacity-P1-PopularMovies).  The updates allow the user to view movie trailers, read reviews, and select favorites.  It is also now optimized for viewing on tablets.

This is in addition to the existing functionality from Project 1 that shows content from [TMDb](https://www.themoviedb.org) and allows the user to sort movies by popularity and ratings and view further detail about each movie.

Further information about the project can be found [here](https://docs.google.com/document/d/1ZlN1fUsCSKuInLECcJkslIqvpKlP7jWL2TP9m6UiA6I/pub?embedded=true#h.7sxo8jefdfll).

## API Key for TMDb Required

In order for this app to fetch content using the themoviedb.org API, an API key is required.  You can add your own by replacing the {Your_TMDB_API_Key} in the following string in strings.xml entry:
<string name="api_key_tmdb">{Your_TMDb_API_Key}</string>

## Required Tasks for Project 2 Nanodegree

- Allow users to view and play trailers ( either in the youtube app or a web browser).
- Allow users to read reviews of a selected movie.
- Allow users to mark a movie as a favorite in the details view by tapping a button (star). This is for a local movies collection cached to a database and does not need an API request.
- Modify the existing sorting criteria for the main view to include an additional pivot to show their favorites collection.
- Optimize your app experience for tablet.

## Requirements from Udacity Rubric

##### User Interface - Layout
- ~~Movies are displayed in the main layout via a grid of their corresponding movie poster thumbnails~~ ([6e61f4c](https://git.io/vw8fy))
- ~~UI contains an element (i.e a spinner or settings menu) to toggle the sort order of the movies by: most popular, highest rated~~ ([9df3202](https://git.io/vwBz5)), and favorites
- ~~UI contains a screen for displaying the details for a selected movie~~ ([a00a495](https://git.io/vwoww))
- ~~Movie details layout contains title, release date, movie poster, vote average, and plot synopsis.~~ ([a00a495](https://git.io/vwoww))
- ~~Movie Details layout contains a section for displaying trailer videos and user reviews~~
- ~~Tablet UI uses a Master-Detail layout implemented using fragments. The left fragment is for discovering movies. The right fragment displays the movie details view for the currently selected movie.~~

##### User Interface - Function
- ~~When a user changes the sort criteria (“most popular and highest rated”) the main view gets updated correctly.~~ ([9df3202](https://git.io/vwBz5)), and favorites sort criteria
- ~~When a movie poster thumbnail is selected, the movie details screen is launched [Phone]~~ ([a00a495](https://git.io/vwoww)) or displayed in a fragment [Tablet]
- ~~When a trailer is selected, app uses an Intent to launch the trailer~~
- ~~In the movies detail screen, a user can tap a button (for example, a star) to mark it as a Favorite~~

##### Network API Implementation 
- ~~In a background thread, app queries the /movie/popular or /movie/top_rated API for the sort criteria specified in the settings menu.~~ ([9df3202](https://git.io/vwBz5))
- ~~This query can also be used to fetch the related metadata needed for the detail view.~~ ([a00a495](https://git.io/vwoww))
- ~~App requests for related videos for a selected movie via the /movie/{id}/videos endpoint in a background thread and displays those details when the user selects a movie.~~
- ~~App requests for user reviews for a selected movie via the /movie/{id}/reviews endpoint in a background thread and displays those details when the user selects a movie.~~

##### Data Persistence 
- ~~App saves a “Favorited” movie to SharedPreferences or a database using the movie’s id.~~
- ~~When the “favorites” setting option is selected, the main view displays the entire favorites collection based on movie IDs stored in SharedPreferences or a database.~~

##### ContentProvider
- A~~pp persists favorite movie details using a database~~
- ~~App displays favorite movie details (title, poster, synopsis, user rating, release date) even when offline~~
- ~~App uses a ContentProvider to populate favorite movie details.  Student may use a library to generate a content provider rather than build one by hand~~

##### Sharing Functionality
- ~~Movie Details View includes an Action Bar item that allows the user to share the first trailer video URL from the list of trailers~~
- ~~App uses a share Intent to expose the external youtube URL for the trailer~~
