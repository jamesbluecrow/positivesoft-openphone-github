### How the app works
- Type whatever you want to search
- Click the search button
- The app uses github API to get the results and saves them in the db
- Information displayed in screen comes from the db always
- Use offline results when there is no connectivity

### Architecture
- [Clean Architecture](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)

The app is composed by the following modules (they could become independent gradle modules if the app grows):

| Package      | Description                                                                                                                                                 |
|--------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------|
| data         | Contains the repositories + data sources                                                                                                                    |
| db           | Contains the database related code (Database, Entities, Dao, etc.                                                                                           |
| di           | Dagger module for dependency injection                                                                                                                      |
| domain       | Contains the use cases (they are too simple in this app). Could define other models to avoid propagating db entities to other layers if the app was bigger. |
| presentation | Contains `@Composable` views + ViewModels + any other UI related logic.                                                                                     |

### Dependencies:
- [Dagger](https://dagger.dev/hilt/gradle-setup.html) for DI because I was familiar with it
- [Coil](https://github.com/coil-kt/coil) for images because it looks simple
- [Jetpack Compose](https://developer.android.com/jetpack/compose) because it didn't exist when I use to develop for Android so wanted to learn something new
- [Timber](https://github.com/JakeWharton/timber) for logging
- [Retrofit](https://github.com/square/retrofit) for http requests because is awesome
- [Room](https://developer.android.com/training/data-storage/room) because it seems to be the standard
- [Github Search Users API](https://docs.github.com/en/rest/search#search-users)

### Notes
- Decided to not add UI tests as well, it will take me some time to look into what are the best
  practices and libraries being used these days...
- Github API is not returning the public_repos value. I started looking into the graphql API with
  apollo client to access that info but I thought the effort was too big.
- I'm using the experimental feature `LocalSoftwareKeyboardController` to hide the keyboard.
- Pagination of results was left out the scope on purpose.
- I started implementing the app using [Android Flow](https://developer.android.com/kotlin/flow) but
  getting familiar to how it works will take some time so I decided to take a more traditional approach.
- I'm a believer that autogenerated code and specifications, for the github client I carved something using retrofit but in the real world I would probably
  autogenerate it from the spec using - https://github.com/OpenAPITools/openapi-generator-cli (or in
  this case maybe even one of the already available
  libraries [github-libraries](https://docs.github.com/en/rest/overview/libraries)).
- Database queries can be improved to better match results from github and/or speed them up.