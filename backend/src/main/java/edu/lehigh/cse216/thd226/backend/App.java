package edu.lehigh.cse216.thd226.backend;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

// Import Google API Library
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
// Import Google's JSON library
import com.google.gson.Gson;

import net.rubyeye.xmemcached.exception.MemcachedException;
// Import the Spark package, so that we can make use of the "get" function to create an HTTP GET route
import spark.Spark;

// Import memcachier
// import net.rubyeye.xmemcached.MemcachedClient;
// import net.rubyeye.xmemcached.MemcachedClientBuilder;
// import net.rubyeye.xmemcached.XMemcachedClientBuilder;
// import net.rubyeye.xmemcached.auth.AuthInfo;
// import net.rubyeye.xmemcached.command.BinaryCommandFactory;
// import net.rubyeye.xmemcached.exception.MemcachedException;
// import net.rubyeye.xmemcached.utils.AddrUtil;

// import java.lang.InterruptedException;
// import java.net.InetSocketAddress;
// import java.security.GeneralSecurityException;
import java.io.IOException;
// import java.util.concurrent.TimeoutException;

public class App {
    private static final String DEFAULT_PORT_DB = "5432";
    private static final int DEFAULT_PORT_SPARK = 8000;

    private static final String GOOGLE_CLIENT_ID = "569573369833-6f0vk1cj0qe2jfm2bsrpmt0jptov4r2p.apps.googleusercontent.com";
    private static final Map<String, String> userSessions = new HashMap<>();
    // private static MemcachedClient mc;

    /**
     * Set up CORS headers for the OPTIONS verb, and for every response that the
     * server sends. This only needs to be called once.
     * 
     * @param origin  The server that is allowed to send requests to this server
     * @param methods The allowed HTTP verbs from the above origin
     * @param headers The headers that can be sent with a request from the above
     *                origin
     */
    private static void enableCORS(String origin, String methods, String headers) {
        // Create an OPTIONS route that reports the allowed CORS headers and methods
        Spark.options("/*", (request, response) -> {
            String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
            if (accessControlRequestHeaders != null) {
                response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
            }
            String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
            if (accessControlRequestMethod != null) {
                response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
            }
            return "OK";
        });

        // 'before' is a decorator, which will run before any
        // get/post/put/delete. In our case, it will put three extra CORS
        // headers into the response
        Spark.before((request, response) -> {
            response.header("Access-Control-Allow-Origin", origin);
            response.header("Access-Control-Request-Method", methods);
            response.header("Access-Control-Allow-Headers", headers);
        });
    }
    /*
     * Takes in an idTokenString and uses the Google token verifier package to
     * verify the token before generating a new session key and storing both the
     * sessionKey and idToken in the hashmap userSessions
     * 
     * @param String idToken
     * 
     * @return String sessionKey on success, return Null on failure
     */
    // To do: instead of storing the session key, and idTokenString in a hashmap,
    // store it in memcachier

    public static String verifyIdToken(String idTokenString) {
        String sessionKey = null; // Initialize as null
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    GsonFactory.getDefaultInstance()).setAudience(Collections.singletonList(GOOGLE_CLIENT_ID)).build();
            GoogleIdToken idToken = verifier.verify(idTokenString);

            if (idToken != null) {
                // Generate a session key and add it to the userSessions map
                sessionKey = createSession();
                userSessions.put(sessionKey, idTokenString);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error:" + e);
        }
        return sessionKey;
    }

    /**
     * Get a fully-configured connection to the database, or exit immediately
     * Uses the Postgres configuration from environment variables
     * 
     * NB: now when we shutdown the server, we no longer lose all data
     * 
     * @return null on failure, otherwise configured database object
     */
    private static Database getDatabaseConnection() {
        if (System.getenv("DATABASE_URL") != null) {
            return Database.getDatabase(System.getenv("DATABASE_URL"), DEFAULT_PORT_DB, System.getenv("POSTGRES_USER"),
                    System.getenv("POSTGRES_PASS"));
        }

        Map<String, String> env = System.getenv();
        String ip = env.get("POSTGRES_IP");
        String port = env.get("POSTGRES_PORT");
        String user = env.get("POSTGRES_USER");
        String pass = env.get("POSTGRES_PASS");
        return Database.getDatabase(ip, port, user, pass);
    }

    public static void main(String[] args) {
        // Memcache connection set up
        // List<InetSocketAddress> servers =
        // AddrUtil.getAddresses("mc4.dev.ec2.memcachier.com:11211");
        // AuthInfo authInfo = AuthInfo.plain("D43975",
        // "E04BBD80061910E99EA91A1DAE068F1D");

        // MemcachedClientBuilder builder = new XMemcachedClientBuilder(servers);

        // // Configure SASL auth for each server
        // for (InetSocketAddress server : servers) {
        // builder.addAuthInfo(server, authInfo);
        // }

        // // Use binary protocol
        // builder.setCommandFactory(new BinaryCommandFactory());
        // // Connection timeout in milliseconds (default: )
        // builder.setConnectTimeout(1000);
        // // Reconnect to servers (default: true)
        // builder.setEnableHealSession(true);
        // // Delay until reconnect attempt in milliseconds (default: 2000)
        // builder.setHealSessionInterval(2000);

        // Set the port on which to listen for requests from the environment
        Spark.port(getIntFromEnv("PORT", DEFAULT_PORT_SPARK));
        // Set up the location for serving static files. If the STATIC_LOCATION
        // environment variable is set, we will serve from it. Otherwise, serve
        // from "/web"
        String static_location_override = System.getenv("STATIC_LOCATION");
        if (static_location_override == null) {
            Spark.staticFileLocation("/web/skytrail");
        } else {
            Spark.staticFiles.externalLocation(static_location_override);
        }
        if ("True".equalsIgnoreCase(System.getenv("CORS_ENABLED"))) {
            final String acceptCrossOriginRequestsFrom = "*";
            final String acceptedCrossOriginRoutes = "GET,PUT,POST,DELETE,OPTIONS";
            final String supportedRequestHeaders = "Content-Type,Authorization,X-Requested-With,Content-Length,Accept,Origin";
            enableCORS(acceptCrossOriginRequestsFrom, acceptedCrossOriginRoutes, supportedRequestHeaders);
        }

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException cnfe) {
            System.out.println("Unable to find postgresql driver");
            return;
        }

        // gson provides us with a way to turn JSON into objects, and objects
        // into JSON.
        //
        // NB: it must be final, so that it can be accessed from our lambdas
        //
        // NB: Gson is thread-safe. See
        // https://stackoverflow.com/questions/10380835/is-it-ok-to-use-gson-instance-as-a-static-field-in-a-model-bean-reuse
        final Gson gson = new Gson();
        Database db = getDatabaseConnection();

        // Set up a route for serving the main page
        Spark.get("/", (request, response) -> {
            response.redirect("/messages");
            return "";
        });

        /**
         * Handle POST requests to "/login" for user authentication using Google ID
         * token.
         * Expects the Google ID token to be sent in the request body.
         *
         * @return If authentication is successful, returns the session key as plain
         *         text with a success status (200).
         *         If authentication fails, responds with an error status (401) and an
         *         error message as plain text.
         */
        // To do: instead of storing the session key, and idTokenString in a hashmap,
        // store it in memcachier
        Spark.post("/login", (request, response) -> {
            String idTokenString = request.body(); // Send the token in the request body

            // Verify the Google ID token and get the session key
            String sessionKey = verifyIdToken(idTokenString);

            if (sessionKey != null) {
                // Add the session key to the userSessions map
                userSessions.put(sessionKey, idTokenString);

                // try {
                // mc = builder.build();
                // try {
                // // Store the session data in MemCachier with a specified expiration time
                // mc.set(sessionKey, 3600, idTokenString);
                // System.out.println("Session Key posted to MemCachier");
                // String val = mc.get(sessionKey);
                // System.out.println(val);
                // } catch (TimeoutException te) {
                // System.err.println("Timeout during set or get: " +
                // te.getMessage());
                // } catch (InterruptedException ie) {
                // System.err.println("Interrupt during set or get: " +
                // ie.getMessage());
                // } catch (MemcachedException me) {
                // System.err.println("Memcached error during get or set: " +
                // me.getMessage());
                // }
                // } catch (IOException ioe) {
                // System.err.println("Couldn't create a connection to MemCachier: " +
                // ioe.getMessage());
                // }

                // Respond with a success status and the session key as plain text
                response.status(200); // successful status means verified token
                response.type("text/plain");
                return sessionKey;
            } else {
                // If verification fails, respond with an error status and message as plain text
                response.status(401); // Unauthorized
                response.type("text/plain");
                return "Invalid Google ID token";
            }
        });

        // Message Routes

        /**
         * Handle GET requests to "/messages" to retrieve all messages from the
         * database.
         *
         * Responds with a status 200 (OK) and a gson String containing all messages.
         * 
         * @return A JSON string representing a StructuredResponse with status "ok" and
         *         the list of messages,
         *         or an empty list if no messages are found.
         */
        Spark.get("/messages", (request, response) -> {
            response.status(200);
            response.type("application/json");
            // ArrayList<messageData> res = db.selectAllMessages();
            String res = gson.toJson(new StructuredResponse("ok", null, db.selectAllMessages()));
            // System.out.println(res); //Test that the method is getting the right data
            return res;
        });

        // POST route for adding a new element to the messageData. This will read
        // JSON from the body of the request, turn it into a SimpleRequest
        // object, extract the title and message, insert them, and return the
        // ID of the newly created row.
        // If the message is posted with a base64 string from front end then convert it
        // back to binary and store it in google drive
        Spark.post("/messages", (request, response) -> {
            // NB: if gson.Json fails, Spark will reply with status 500 Internal Server
            // Error
            String sessionKey = request.queryParams("sessionKey");

            // String value = mc.get(sessionKey);

            if (userSessions.containsKey(sessionKey)) {
                // If a valid session key found, then continue the route e
                SimpleRequest req = gson.fromJson(request.body(), SimpleRequest.class);

                /// From request, extract the base64 string if it exists
                String base64String = req.mBase64String;
                String fileLink = null;

                // From request, extract link string if it exists or not (null)
                String link = req.mLink;

                // ensure status 200 OK, with a MIME type of JSON
                // NB: even on error, we return 200, but with a JSON object that describes the
                // error.
                // NB: createEntry checks for null title and message
                response.status(200);
                response.type("application/json");

                if (base64String != null) {
                    // Decode base64 string into a binary file and store it in Google Drive folder
                    try {
                        // Create an instance of UploadBasic
                        UploadBasic uploadBasic = new UploadBasic();

                        // upload file to google drive
                        fileLink = uploadBasic.uploadToGoogleDrive(base64String);

                        // Storing filelink in memcachier

                        // Extract file id from file link
                        // String fileID = fileLink.replaceAll(".*/file/d/(.*?)/.*", "$1");

                        // try {
                        //     mc = builder.build();
                        //     try {
                        //         // Store the session data in MemCachier with a specified expiration time
                        //         mc.set(fileLink, 3600, fileID);
                        //         System.out.println("File posted to MemCachier");
                        //     } catch (TimeoutException te) {
                        //         System.err.println("Timeout during set or get: " +
                        //                 te.getMessage());
                        //     } catch (InterruptedException ie) {
                        //         System.err.println("Interrupt during set or get: " +
                        //                 ie.getMessage());
                        //     } catch (MemcachedException me) {
                        //         System.err.println("Memcached error during get or set: " +
                        //                 me.getMessage());
                        //     }
                        // } catch (IOException ioe) {
                        //     System.err.println("Couldn't create a connection to MemCachier: " +
                        //             ioe.getMessage());
                        // }

                        // Send file link
                        System.out.println("File Link returned: " + fileLink);
                    } catch (IOException e) {
                        // Handle exceptions appropriately
                        e.printStackTrace();
                    }
                }

                // Insert everything
                int newId = db.insertMessage(req.mTitle, req.mMessage, req.mLikes, req.mUserName, link, fileLink);
                if (newId == -1) {
                    return gson.toJson(new StructuredResponse("error", "error performing insertion", null));
                } else {
                    return gson.toJson(new StructuredResponse("ok", "" + newId, null));
                }

            } else {
                // Session key not found or is invalid
                response.status(401); // Unauthorized
                response.type("application/json");
                return gson.toJson(new StructuredResponse("error", "Invalid or missing session key", null));
            }
        });

        // Specific Message Actions

        // GET route that returns everything for a single post in the messageData.
        // The ":id" suffix in the first parameter to get() becomes
        // request.params("id"), so that we can get the requested row ID. If
        // ":id" isn't a number, Spark will reply with a status 500 Internal
        // Server Error. Otherwise, we have an integer, and the only possible
        // error is that it doesn't correspond to a row with data.
        Spark.get("/messages/:id", (request, response) -> {
            String sessionKey = request.queryParams("sessionKey");
            // String value = mc.get(sessionKey);
            // if (value != null)

            if (userSessions.containsKey(sessionKey)) {
                int idx = Integer.parseInt(request.params("id"));
                // ensure status 200 OK
                response.status(200);
                response.type("application/json");
                messageData data = db.selectMessage(idx);
                if (data == null) {
                    return gson.toJson(new StructuredResponse("error", idx + " not found", null));
                } else {
                    return gson.toJson(new StructuredResponse("ok", null, data));
                }
            } else {
                // Session key not found/invalid
                response.status(401); // Unauthorized
                response.type("application/json");
                return gson.toJson(new StructuredResponse("error", "Invalid or missing session key", null));
            }
        });

        // PUT route for updating a row in the messageData. This is almost
        // exactly the same as POST
        Spark.put("/messages/:id", (request, response) -> {
            String sessionKey = request.queryParams("sessionKey");
            // String value = mc.get(sessionKey);

            if (userSessions.containsKey(sessionKey)) {
                int idx = Integer.parseInt(request.params("id"));
                SimpleRequest req = gson.fromJson(request.body(), SimpleRequest.class);
                // Ensure status 200 OK
                response.status(200);
                response.type("application/json");
                int result = db.updateMessage(idx, req.mMessage);
                if (result == -1) {
                    return gson.toJson(new StructuredResponse("error", "unable to update row " + idx, null));
                } else {
                    return gson.toJson(new StructuredResponse("ok", null, result));
                }
            } else {
                // Session key not found or is invalid
                response.status(401); // Unauthorized
                response.type("application/json");
                return gson.toJson(new StructuredResponse("error", "Invalid or missing session key", null));
            }
        });

        // DELETE route for removing a row from the messageData
        Spark.delete("/messages/:id", (request, response) -> {
            // If we can't get an ID, Spark will send a status 500
            String sessionKey = request.queryParams("sessionKey");
            // String value = mc.get(sessionKey);

            if (userSessions.containsKey(sessionKey)) {
                int idx = Integer.parseInt(request.params("id"));
                // Ensure status 200 OK, with a MIME type of JSON
                response.status(200);
                response.type("application/json");
                // NB: we won't concern ourselves too much with the quality of the
                // message sent on a successful delete
                int result = db.deleteMessage(idx);
                if (result == -1) {
                    return gson.toJson(new StructuredResponse("error", "unable to delete row " + idx, null));
                } else {
                    return gson.toJson(new StructuredResponse("ok", null, null));
                }
            } else {
                // Session key not found or is invalid
                response.status(401); // Unauthorized
                response.type("application/json");
                return gson.toJson(new StructuredResponse("error", "Invalid or missing session key", null));
            }
        });

        // Liking Routes

        // POST route for liking a message
        // This route allows a user to like or change their like status on a message.
        Spark.post("/messages/:id/like", (request, response) -> {
            String sessionKey = request.queryParams("sessionKey");
            // String value = mc.get(sessionKey);

            if (userSessions.containsKey(sessionKey)) {
                int messageId = Integer.parseInt(request.params("id"));
                String user = request.queryParams("username");
                response.status(200);
                int result = db.insertLike(messageId, user);

                if (result == -1) {
                    return gson.toJson(new StructuredResponse("error", "unable to update like", null));
                } else {
                    return gson.toJson(new StructuredResponse("ok", "like updated", null));
                }
            } else {
                // Session key not found or is invalid
                response.status(401); // Unauthorized
                response.type("application/json");
                return gson.toJson(new StructuredResponse("error", "Invalid or missing session key", null));
            }
        });

        // DELETE route for removing a like from a message
        // This route allows a user to remove their like from a message.
        Spark.post("/messages/:id/dislike", (request, response) -> {
            String sessionKey = request.queryParams("sessionKey");
            // String value = mc.get(sessionKey);

            if (userSessions.containsKey(sessionKey)) {
                int messageId = Integer.parseInt(request.params("id"));
                String user = request.queryParams("username");
                response.status(200);
                int result = db.insertDisike(messageId, user);
                if (result == -1) {
                    return gson.toJson(new StructuredResponse("error", "unable to update like", null));
                } else {
                    return gson.toJson(new StructuredResponse("ok", "like updated", null));
                }
            } else {
                // Session key not found or is invalid
                response.status(401); // Unauthorized
                response.type("application/json");
                return gson.toJson(new StructuredResponse("error", "Invalid or missing session key", null));
            }
        });

        // User Routes
        // GET route to retrieve user data based on the session key
        Spark.get("/users", (request, response) -> {
            String sessionKey = request.queryParams("sessionKey");
            // String value = mc.get(sessionKey);

            if (userSessions.containsKey(sessionKey)) {
                // Call the selectAllUsers method to retrieve the list of users
                ArrayList<userData> users = db.selectAllUsers();

                if (users != null) {
                    response.status(200);
                    response.type("application/json");
                    return gson.toJson(new StructuredResponse("ok", null, users));
                } else {
                    response.status(500); // Internal Server Error
                    return gson.toJson(new StructuredResponse("error", "Failed to retrieve user data", null));
                }
            } else {
                response.status(401); // Unauthorized
                return gson.toJson(new StructuredResponse("error", "Session not found or expired", null));
            }
        });

        // Create a new user
        Spark.post("/users", (request, response) -> {
            String sessionKey = request.queryParams("sessionKey");
            // String value = mc.get(sessionKey);

            if (userSessions.containsKey(sessionKey)) {
                // Parse the JSON request to get user data
                userData newUser = gson.fromJson(request.body(), userData.class);

                // Call the insertUser function in Database.java to insert the new user
                int result = db.insertUser(newUser.uUsername, newUser.uFirstname, newUser.uLastname, newUser.uEmail,
                        newUser.uGender, newUser.uSexorientation, newUser.uNote);
                if (result != -1) {
                    response.status(201); // Created
                    return gson.toJson(new StructuredResponse("ok", "" + result, null));
                } else {
                    response.status(500); // Internal server error
                    return gson.toJson(new StructuredResponse("error", "Error creating the user", null));
                }
            } else {
                // Session key not found or is invalid
                response.status(401); // Unauthorized
                response.type("application/json");
                return gson.toJson(new StructuredResponse("error", "Invalid or missing session key", null));
            }
        });

        // Get user data by username
        Spark.get("/users/:username", (request, response) -> {
            String sessionKey = request.queryParams("sessionKey");
            // String value = mc.get(sessionKey);

            if (userSessions.containsKey(sessionKey)) {
                String username = request.params("username");
                // Call the selectUser function in Database.java to retrieve user data by
                // username
                userData user = db.selectUser(username);
                if (user != null) {
                    response.status(200);
                    response.type("application/json");
                    return gson.toJson(new StructuredResponse("ok", null, user));
                } else {
                    response.status(404); // User not found
                    return gson.toJson(new StructuredResponse("error", "User not found", null));
                }
            } else {
                // Session key not found or is invalid
                response.status(401); // Unauthorized
                response.type("application/json");
                return gson.toJson(new StructuredResponse("error", "Invalid or missing session key", null));
            }
        });

        // Update user data by username
        Spark.put("/users/:username", (request, response) -> {
            String sessionKey = request.queryParams("sessionKey");
            // String value = mc.get(sessionKey);
            if (userSessions.containsKey(sessionKey)) {
                String username = request.params("username");
                // Parse the JSON request to get updated user data
                userData updatedUser = gson.fromJson(request.body(), userData.class);

                // Call the updateUser function in Database.java to update user data
                int result = db.updateUser(username, updatedUser.uFirstname, updatedUser.uLastname, updatedUser.uEmail,
                        updatedUser.uGender, updatedUser.uSexorientation, updatedUser.uNote);
                if (result != -1) {
                    response.status(200); // OK
                    return gson.toJson(new StructuredResponse("ok", "User data updated", null));
                } else {
                    response.status(500); // Internal server error
                    return gson.toJson(new StructuredResponse("error", "Error updating user data", null));
                }
            } else {
                // Session key not found or is invalid
                response.status(401); // Unauthorized
                response.type("application/json");
                return gson.toJson(new StructuredResponse("error", "Invalid or missing session key", null));
            }
        });

        // Delete user by username
        Spark.delete("/users/:username", (request, response) -> {
            String sessionKey = request.queryParams("sessionKey");
            // String value = mc.get(sessionKey);

            if (userSessions.containsKey(sessionKey)) {
                String username = request.params("username");

                // Call the deleteUser function in Database.java to delete the user
                int result = db.deleteUser(username);
                if (result != -1) {
                    response.status(200); // OK
                    return gson.toJson(new StructuredResponse("ok", "User deleted", null));
                } else {
                    response.status(500); // Internal server error
                    return gson.toJson(new StructuredResponse("error", "Error deleting the user", null));
                }
            } else {
                // Session key not found or is invalid
                response.status(401); // Unauthorized
                response.type("application/json");
                return gson.toJson(new StructuredResponse("error", "Invalid or missing session key", null));
            }
        });

        // GET route that retrieves comments for a specific message in the messageData.
        // The ":id" suffix in the first parameter to get() becomes
        // request.params(":id"), so that we can get the requested message ID. If
        // ":id" isn't a number, Spark will reply with a status 400 Bad Request.
        // Otherwise, we have an integer, and the only possible error is that it
        // doesn't correspond to a message with comments.
        Spark.get("/messages/:id/comments", (request, response) -> {
            String sessionKey = request.queryParams("sessionKey");
            // String value = mc.get(sessionKey);

            if (userSessions.containsKey(sessionKey)) {
                int messageId;
                try {
                    // Extract the message ID
                    messageId = Integer.parseInt(request.params(":id"));
                } catch (NumberFormatException e) {
                    response.status(400);
                    return gson.toJson(new StructuredResponse("error", "Invalid message ID format", null));
                }

                // Retrieve comments for the given message ID
                List<commentData> comments = db.selectAllComments(messageId);

                // Return the comments in a JSON format
                response.status(200); // OK
                response.type("application/json");

                if (comments == null || comments.isEmpty()) {
                    return gson.toJson(new StructuredResponse("error", "No comments found", null));
                } else {
                    return gson.toJson(comments);
                }
            } else {
                // Session key not found or is invalid
                response.status(401); // Unauthorized
                response.type("application/json");
                return gson.toJson(new StructuredResponse("error", "Invalid or missing session key", null));
            }
        });

        // GET route that retrieves comments posted by a specific user.
        // The ":username" suffix in the first parameter to get() becomes
        // request.params(":username"), so that we can get the requested username.
        Spark.get("/comments/users/:username", (request, response) -> {
            String sessionKey = request.queryParams("sessionKey");
            // String value = mc.get(sessionKey);

            if (userSessions.containsKey(sessionKey)) {
                String username = request.params(":username");

                // Retrieve comments posted by the specific user from the database
                List<Integer> userComments = db.getUserComments(username);
                // Return the comments in a JSON format
                response.status(200); // OK
                response.type("application/json");

                if (userComments == null || userComments.isEmpty()) {
                    return gson.toJson(new StructuredResponse("error", "No comments found for the user", null));
                } else {
                    List<commentData> comments = new ArrayList<>();
                    for (int commentId : userComments) {
                        commentData comment = db.getComment(commentId);
                        if (comment != null) {
                            comments.add(comment);
                        }
                    }
                    return gson.toJson(comments);
                }
            } else {
                // Session key not found or is invalid
                response.status(401); // Unauthorized
                response.type("application/json");
                return gson.toJson(new StructuredResponse("error", "Invalid or missing session key", null));
            }
        });

        // POST route to add a new comment to a specific message.
        // Expects a valid session key in the query parameters and a JSON payload in the
        // request body:
        // { "cContent": "comment content", "cUsername": "comment username",
        // "cMessageID": message ID }
        // Responds with a success status (200) and the new comment ID on success.
        // If the insertion fails or the session key is invalid, responds with an error
        // status (200) and a corresponding message.

        // File can be attached to comment if user wants
        Spark.post("/messages/:id/comment", (request, response) -> {
            // NB: if gson.Json fails, Spark will reply with status 500 Internal Server
            // Error
            String sessionKey = request.queryParams("sessionKey");
            // String value = mc.get(sessionKey);

            if (userSessions.containsKey(sessionKey)) {
                // If a valid session key found, then continue the route e
                SimpleRequestComment req = gson.fromJson(request.body(), SimpleRequestComment.class);
                response.status(200);
                response.type("application/json");

                // From request, extract the base64 string if it exists
                String base64String = req.cBase64String;
                String fileLink = "";

                // Extract link if it exists
                String link = req.cLink;

                if (base64String != null) {
                    // Decode base64 string into a binary file and store it in Google Drive folder
                    try {
                        // Create an instance of UploadBasic
                        UploadBasic uploadBasic = new UploadBasic();

                        // upload file to google drive
                        fileLink = uploadBasic.uploadToGoogleDrive(base64String);

                        // Storing filelink in memcachier

                        // Extract file id from file link
                        // String fileID = fileLink.replaceAll(".*/file/d/(.*?)/.*", "$1");

                        // try {
                        //     mc = builder.build();
                        //     try {
                        //         // Store the session data in MemCachier with a specified expiration time
                        //         mc.set(fileLink, 3600, fileID);
                        //         System.out.println("File posted to MemCachier");
                        //     } catch (TimeoutException te) {
                        //         System.err.println("Timeout during set or get: " +
                        //                 te.getMessage());
                        //     } catch (InterruptedException ie) {
                        //         System.err.println("Interrupt during set or get: " +
                        //                 ie.getMessage());
                        //     } catch (MemcachedException me) {
                        //         System.err.println("Memcached error during get or set: " +
                        //                 me.getMessage());
                        //     }
                        // } catch (IOException ioe) {
                        //     System.err.println("Couldn't create a connection to MemCachier: " +
                        //             ioe.getMessage());
                        // }

                        // Send file link
                        System.out.println("File Link returned: " + fileLink);
                    } catch (IOException e) {
                        // Handle exceptions appropriately
                        e.printStackTrace();
                    }
                }

                // NB: createEntry checks for null title and message
                // No file is given so insert a regular comment
                int newId = db.insertComment(req.cContent, req.cUsername, req.cMessageID, link, fileLink);
                if (newId == -1) {
                    return gson.toJson(new StructuredResponse("error", "error performing insertion", null));
                } else {
                    return gson.toJson(new StructuredResponse("ok", "" + newId, null));
                }
            } else {
                // Session key not found or is invalid
                response.status(401); // Unauthorized
                response.type("application/json");
                return gson.toJson(new StructuredResponse("error", "Invalid or missing session key", null));
            }
        });

        // PUT route to update a comment in a message. Expects a valid session key.
        // Parameters: ":message_id" and ":comment_id" represent message and comment
        // IDs.
        // Expects a JSON payload with updated comment data.
        // Responds with a success status (200) and a message on success.
        // For invalid session key, responds with an error status (401).
        // If the update fails, responds with an error status (500).
        Spark.put("/messages/:message_id/comments/:comment_id", (request, response) -> {
            String sessionKey = request.queryParams("sessionKey");
            // String value = mc.get(sessionKey);

            if (userSessions.containsKey(sessionKey)) {
                int commentID = Integer.parseInt(request.params("comment_id"));
                int messageID = Integer.parseInt(request.params("message_id"));

                // Parse the JSON request to get updated comment data
                SimpleRequestComment reqC = gson.fromJson(request.body(), SimpleRequestComment.class);

                response.status(200);
                response.type("application/json");
                // Call a method to update the comment in the database
                int result = db.updateComment(commentID, reqC.cContent, messageID);

                if (result == -1) {
                    return gson.toJson(new StructuredResponse("error", "unable to update row " + commentID, null));
                } else {
                    return gson.toJson(new StructuredResponse("ok", null, result));
                }
            } else {
                // Session key not found or is invalid
                response.status(401); // Unauthorized
                response.type("application/json");
                return gson.toJson(new StructuredResponse("error", "Invalid or missing session key", null));
            }
        });

        Spark.get("/messages/users/:username", (request, response) -> {
            String sessionKey = request.queryParams("sessionKey");
            // String value = mc.get(sessionKey);

            if (userSessions.containsKey(sessionKey)) {
                String username = request.params(":username");

                // Retrieve messages posted by the specific user from the database
                List<Integer> userPosts = db.getUserPosts(username);

                // Return the messages in a JSON format
                response.status(200); // OK
                response.type("application/json");

                if (userPosts == null || userPosts.isEmpty()) {
                    return gson.toJson(new StructuredResponse("error", "No messages found for the user", null));
                } else {
                    List<messageData> messages = new ArrayList<>();
                    for (int messageId : userPosts) {
                        messageData message = db.selectMessage(messageId);
                        if (message != null) {
                            messages.add(message);
                        }
                    }
                    return gson.toJson(messages);
                }
            } else {
                // Session key not found or is invalid
                response.status(401); // Unauthorized
                response.type("application/json");
                return gson.toJson(new StructuredResponse("error", "Invalid or missing session key", null));
            }
        });

        // File Routes
        // Retrieve a file attached to a message from memcachier
        // Spark.get("/messages/:id/file", (request, response) -> {
        //     String sessionKey = request.queryParams("sessionKey");
        //     // String value = mc.get(sessionKey);
        //     // if (value != null)

        //     if (userSessions.containsKey(sessionKey)) {
        //         int idx = Integer.parseInt(request.params("id"));

        //         // Use message ID to extract the file link from memcacher
        //         String file_link = mc.get(idx);
                
        //         // ensure status 200 OK
        //         response.status(200);
        //         response.type("application/json");
        //         if (file_link != null) {
        //             return gson.toJson(new StructuredResponse("ok", null, file_link));
        //         }
        //         else {
        //             return gson.toJson(new StructuredResponse("error", idx + " not found", null));
        //         }
        //     } else {
        //         // Session key not found/invalid
        //         response.status(401); // Unauthorized
        //         response.type("application/json");
        //         return gson.toJson(new StructuredResponse("error", "Invalid or missing session key", null));
        //     }
        // });

        // // Retrieve a file attached to a comment from memcachier
        // Spark.get("/messages/:message_id/comments/:commend_id/file", (request, response) -> {
        //     String sessionKey = request.queryParams("sessionKey");
        //     // String value = mc.get(sessionKey);
        //     // if (value != null)

        //     if (userSessions.containsKey(sessionKey)) {
        //         int comment_idx = Integer.parseInt(request.params("comment_id"));

        //         // Use message ID to extract the file link from memcacher
        //         String file_link = mc.get(comment_idx);
                
        //         // ensure status 200 OK
        //         response.status(200);
        //         response.type("application/json");
        //         if (file_link != null) {
        //             return gson.toJson(new StructuredResponse("ok", null, file_link));
        //         }
        //         else {
        //             return gson.toJson(new StructuredResponse("error", comment_idx + " not found", null));
        //         }
        //     } else {
        //         // Session key not found/invalid
        //         response.status(401); // Unauthorized
        //         response.type("application/json");
        //         return gson.toJson(new StructuredResponse("error", "Invalid or missing session key", null));
        //     }
        // });

        // Delete file attached to a message by setting the file link field to null
        Spark.delete("/messages/:id/file", (request, response) -> {
            // If we can't get an ID, Spark will send a status 500
            String sessionKey = request.queryParams("sessionKey");
            // String value = mc.get(sessionKey);

            if (userSessions.containsKey(sessionKey)) {
                // Extract message ID
                int idx = Integer.parseInt(request.params("id"));

                // Ensure status 200 OK, with a MIME type of JSON
                response.status(200);
                response.type("application/json");
                // NB: we won't concern ourselves too much with the quality of the
                // message sent on a successful delete
                int result = db.deleteFileMessage(idx);
                if (result == -1) {
                    return gson.toJson(new StructuredResponse("error", "File does not exist " + idx, null));
                } else {
                    return gson.toJson(new StructuredResponse("ok", null, null));
                }
            } else {
                // Session key not found or is invalid
                response.status(401); // Unauthorized
                response.type("application/json");
                return gson.toJson(new StructuredResponse("error", "Invalid or missing session key", null));
            }
        });

        // Delete file attached to a comment
        Spark.delete("/messages/:message_id/comments/:comment_id/file", (request, response) -> {
            // If we can't get an ID, Spark will send a status 500
            String sessionKey = request.queryParams("sessionKey");
            // String value = mc.get(sessionKey);

            if (userSessions.containsKey(sessionKey)) {
                // Extract message ID & comment ID
                int message_id = Integer.parseInt(request.params("message_id"));
                int comment_id = Integer.parseInt(request.params("comment_id"));

                // Ensure status 200 OK, with a MIME type of JSON
                response.status(200);
                response.type("application/json");
                // NB: we won't concern ourselves too much with the quality of the
                // message sent on a successful delete

                int result = db.deleteFileComment(message_id, comment_id);
                if (result == -1) {
                    return gson.toJson(
                            new StructuredResponse("error", "File does not exist at comment " + comment_id, null));
                } else {
                    return gson.toJson(new StructuredResponse("ok", null, null));
                }
            } else {
                // Session key not found or is invalid
                response.status(401); // Unauthorized
                response.type("application/json");
                return gson.toJson(new StructuredResponse("error", "Invalid or missing session key", null));
            }
        });

        

        // Link routes
        // Delete link attached to a message by setting the link field to null
        Spark.delete("/messages/:id/link", (request, response) -> {
            // If we can't get an ID, Spark will send a status 500
            String sessionKey = request.queryParams("sessionKey");
            // String value = mc.get(sessionKey);

            if (userSessions.containsKey(sessionKey)) {
                // Extract message ID
                int idx = Integer.parseInt(request.params("id"));

                // Ensure status 200 OK, with a MIME type of JSON
                response.status(200);
                response.type("application/json");
                // NB: we won't concern ourselves too much with the quality of the
                // message sent on a successful delete
                int result = db.deleteLinkMessage(idx);
                if (result == -1) {
                    return gson.toJson(new StructuredResponse("error", "Link does not exist " + idx, null));
                } else {
                    return gson.toJson(new StructuredResponse("ok", null, null));
                }
            } else {
                // Session key not found or is invalid
                response.status(401); // Unauthorized
                response.type("application/json");
                return gson.toJson(new StructuredResponse("error", "Invalid or missing session key", null));
            }
        });

        // Delete link attached to a comment
        Spark.delete("/messages/:message_id/comments/:comment_id/link", (request, response) -> {
            // If we can't get an ID, Spark will send a status 500
            String sessionKey = request.queryParams("sessionKey");
            // String value = mc.get(sessionKey);

            if (userSessions.containsKey(sessionKey)) {
                // Extract message ID & comment ID
                int message_id = Integer.parseInt(request.params("message_id"));
                int comment_id = Integer.parseInt(request.params("comment_id"));

                // Ensure status 200 OK, with a MIME type of JSON
                response.status(200);
                response.type("application/json");
                // NB: we won't concern ourselves too much with the quality of the
                // message sent on a successful delete

                int result = db.deleteLinkComment(message_id, comment_id);
                if (result == -1) {
                    return gson.toJson(
                            new StructuredResponse("error", "Link does not exist at comment " + comment_id, null));
                } else {
                    return gson.toJson(new StructuredResponse("ok", null, null));
                }
            } else {
                // Session key not found or is invalid
                response.status(401); // Unauthorized
                response.type("application/json");
                return gson.toJson(new StructuredResponse("error", "Invalid or missing session key", null));
            }
        });

    }

    /**
     * Get an integer environment variable if it exists, and otherwise return the
     * default value.
     * 
     * @envar The name of the environment variable to get.
     * @defaultVal The integer value to use as the default if envar isn't found
     * 
     * @returns The best answer we could come up with for a value for envar
     */
    static int getIntFromEnv(String envar, int defaultVal) {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (processBuilder.environment().get(envar) != null) {
            return Integer.parseInt(processBuilder.environment().get(envar));
        }
        return defaultVal;
    }

    // Function to create a new session and a unique session key
    private static String createSession() {
        String sessionKey = UUID.randomUUID().toString();
        return sessionKey;
    }
}