# simple-vcs
# Simplified application for version control
It's going to be written soon...

## Local Environment Setup

This project uses Docker to quickly spin up a local MySQL database.

### Prerequisites
- [Docker Desktop](https://www.docker.com/products/docker-desktop/) installed on your machine.

### Starting the Database
1. Open a terminal in the project's root directory.
2. Run the following command to start the MySQL database in the background:
   ```
   docker compose up -d
   ```
3. The database will now be running on `localhost:3306` with the username `root` and password `root`.

To stop the database without losing data, run:
```
docker compose stop
```
