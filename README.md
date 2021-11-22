# GITHUBRANK

Githubrank API has an endpoint that given the name of the organization will return a list of contributors sorted by the number of contributions. This API uses Scala and Play Framework. 

To test the application, you need to add a personal token from Github in line 3 of the `application.conf` file, as following: `accessToken="YOUR TOKEN"`, or set it as environment variable (`GH_TOKEN`).

After adding the token, you can run the application typing the following command in the terminal: `sbt run`.

To run the tests, type in the terminal: `sbt test`.

### Endpoint:

#### GET    /org/:organization/contributors           

**Description**: *Get all contributors from the organization.*

Example Response

```json
  "name": "dhh",
  "contributions": 237888
```

## Endpoints for you to test:

### /org/engineyard/contributors
