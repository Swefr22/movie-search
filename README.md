# Movie Search App

A standalone Spring Boot application that searches movies through the OMDb API.

## Requirements

- Java 17 or newer
- Maven 3.6 or newer (for running outside Docker)
- An OMDb API key from [omdbapi.com](https://www.omdbapi.com/apikey.aspx)

## Configure and run locally

Install Java 17 or newer and Maven 3.6 or newer. Get an API key from [omdbapi.com](https://www.omdbapi.com/apikey.aspx), then set it in PowerShell and start the application:

```powershell
$env:OMDB_API_KEY = "your-api-key"
mvn spring-boot:run
```

Open `http://localhost:8080` for the movie-search page, or call the JSON API directly:

```powershell
curl.exe "http://localhost:8080/api/movies?title=inception"
```

`GET /api/movies?title=inception` returns OMDb search results, including each movie's title, year, IMDb ID, type, and poster URL. Set `OMDB_API_URL` to override the OMDb endpoint if needed. Missing API keys return HTTP 503; OMDb and network failures are reported as gateway errors.

## Run with Docker

Start Docker Desktop and wait until its engine is running. Set `OMDB_API_KEY` in PowerShell, then build and run the container:

```powershell
$env:OMDB_API_KEY = "your-api-key"
docker build -t movieapp:v1 .
docker run --rm --name movieapp -p 8080:8080 -e "OMDB_API_KEY=$env:OMDB_API_KEY" movieapp:v1
```

Search the running container with `curl.exe "http://localhost:8080/api/movies?title=inception"`. Press `Ctrl+C` to stop the container. Docker builds the app inside the image, so Java and Maven are not required on the host.

Run the tests with `mvn test`.