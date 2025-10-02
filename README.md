# Media Center

Media Center is a powerful full-stack web application designed to provide a beautiful, easy-to-navigate interface for your local collection of movies and TV shows. It builds a rich, searchable media library by parsing IMDb datasets and leverages the TMDB API to fetch additional information like posters and plot summaries.

## Features

- **Advanced Search & Filtering**: Search and filter your media library by a combination of criteria such as type, year, rating, and number of votes.
- **Detailed Information Pages**: Provides rich detail pages for each movie, TV show, and person.
- **Episode Browser**: Displays each season and episode of a TV series.
- **Personal Watch History**: [ ] Track and manage your viewing progress.
- **Data Importer**: Includes a Java utility for importing datasets into a database.

## Tech Stack

- **Backend**: Java 17, Spring Boot 3, Maven
- **Frontend**: React, Vite, JavaScript (ES6+)
- **Database**: MySQL
- **API**: RESTful API, TMDB API (for fetching metadata like posters)

## Screenshots

*(Add your application screenshots here, e.g., homepage, detail page, filter panel, etc.)*

## Installation and Setup Guide

### 1. Prerequisites

Before you begin, ensure you have the following software installed:

- **Java Development Kit (JDK)**: Version 17 or higher
- **Maven**: For building the backend project
- **Node.js**: Version 18 or higher
- **MySQL**: Database server

### 2. Database Setup

1.  **Create the database**: Log in to your MySQL server and create a new database.

    ```sql
    CREATE DATABASE imdb_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
    ```

2.  **Import the table schema**: Use the `schema.sql` file in the project root to create all the necessary tables.

    ```bash
    mysql -u YOUR_USERNAME -p imdb_db < schema.sql
    ```

### 3. Data Import (Importer)

This step imports the raw IMDb data into your database.

1.  **Download IMDb Datasets**:
    - Go to the [IMDb Datasets](https://developer.imdb.com/non-commercial-datasets/) official website.
    - Download the following `.tsv.gz` files:
        - `name.basics.tsv.gz`
        - `title.basics.tsv.gz`
        - `title.crew.tsv.gz`
        - `title.episode.tsv.gz`
        - `title.principals.tsv.gz`
        - `title.ratings.tsv.gz`
    - Place all of them in a single folder on your local machine.

2.  **Configure the Importer**:
    - Open `importer/src/main/java/org/example/DatabaseManager.java` and modify `DB_URL`, `USER`, and `PASSWORD` to match your database connection info.

3.  **Run the Importer**:
    - Run the Maven command from the root directory of the `importer` module.
    - **Important**: You must replace the path at the end of the command with the actual path to the folder containing your IMDb datasets.

    ```bash
    # Run from the importer directory
    # Replace "C:\path\to\your\datasets" with your actual path
    mvn compile exec:java -Dexec.mainClass="org.example.Importer" -Dexec.args="C:\path\to\your\datasets"
    ```
    - **Note**: The import process can take anywhere from a few minutes to half an hour, depending on your machine's performance.

### 4. Backend

1.  **Configure the Backend**:
    - Open `backend/src/main/resources/application.yml`.
    - Ensure the `url`, `username`, and `password` in the `datasource` section match your database settings.

2.  **Run the Backend Server**:
    - Run the Spring Boot application from the root directory of the `backend` module.

    ```bash
    # Run from the backend directory
    mvn spring-boot:run
    ```
    - The backend server will start on `http://localhost:8080` by default.

### 5. Frontend

1.  **Get a TMDB API Key**:
    - Visit [The Movie Database (TMDB) API](https://developer.themoviedb.org/docs/getting-started) website, register for an account, and request a free API key.

2.  **Configure the Frontend**:
    - In the `frontend` directory, copy the `.env.example` file to a new file named `.env`.
    - Open the new `.env` file and fill in your TMDB API key:
      ```
      VITE_TMDB_API_KEY="YOUR_TMDB_API_KEY_HERE"
      ```

3.  **Install Dependencies and Run**:
    - Open a terminal in the root directory of the `frontend` module and run the following commands:

    ```bash
    # Install dependencies
    npm install

    # Start the development server
    npm run dev
    ```
    - The frontend development server will start on `http://localhost:5173` by default.

### 6. Accessing the Application

Open your browser and navigate to `http://localhost:5173`. You should see the Media Center homepage!
