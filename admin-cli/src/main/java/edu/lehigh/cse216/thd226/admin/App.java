package edu.lehigh.cse216.thd226.admin;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Map;

/**
 * App is our basic admin app. For now, it is a demonstration of the six key
 * operations on a database: connect, insert, update, query, delete, disconnect
 */
public class App {
    public static void main(String[] args) {
        // get the Postgres configuration from the environment
        Map<String, String> env = System.getenv();
        String ip = env.get("POSTGRES_IP");
        String port = env.get("POSTGRES_PORT");
        String user = env.get("POSTGRES_USER");
        String pass = env.get("POSTGRES_PASS");

        // Get a fully-configured connection to the database, or exit
        // immediately
        Database db = Database.getDatabase(ip, port, user, pass);
        if (db == null)
            return;
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

        while (true) {
            char res = menu(in);
            if (res == 'M') {
                mainMessage(in, db);
            } else if (res == 'C') {
                mainComment(in, db);
            } else if (res == 'U') {
                mainUser(in, db);
            } else if (res == '+') {
                db.createAllTables();
            } else if (res == '-') {
                db.dropAllTables();
            } else if (res == '@'){
                db.addColumns();
            }
        }

    }

    /**
     * Print the main menu for our program
     * 
     * @param BufferedReader The scanner for this class
     */
    static char menu(BufferedReader in) {
        System.out.println("Main Menu");
        System.out.println("  [M] Query for messages");
        System.out.println("  [C] Query for comments");
        System.out.println("  [U] Query for users");
        System.out.println("  [+] Create all tables");
        System.out.println("  [-] Drop all tables");
        System.out.println("  [@] Add column");
        try {
            char response = in.readLine().charAt(0);
            if (response != 'M' && response != 'C' && response != 'U' && response != '+' && response != '-' && response != '@') {
                menu(in);
            }
            return response;
        } catch (IOException e) {
            e.printStackTrace();
            return '0';
        }
    }

    static int getInt(BufferedReader in, String idea) {
        int i = -1;
        try {
            System.out.print(idea + " :> ");
            i = Integer.parseInt(in.readLine());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return i;
    }

    static String getString(BufferedReader in, String idea) {
        String s;
        try {
            System.out.print(idea + " :> ");
            s = in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
        return s;
    }

    /**
     * Print the message menu
     * 
     * @param BufferedReader The scanner for this class
     */
    static void messageMenu(BufferedReader in) {
        System.out.println("  [1] Query for a specific row");
        System.out.println("  [*] Query for all rows");
        System.out.println("  [-] Delete a row");
        System.out.println("  [+] Insert a new row");
        System.out.println("  [~] Update a row");
        System.out.println("  [^] Edit Like Count");
        System.out.println("  [P] Populate messages");
        System.out.println("  [q] Quit Program");
        System.out.println("  [?] Help (this idea)");
    }

    /**
     * Prompts for message actions
     * 
     * @param BufferedReader The scanner for this class
     */
    static char promptMessage(BufferedReader in) {
        String actions = "1*-+~^Pq?";

        while (true) {
            System.out.print("[" + actions + "] :> ");
            String response;
            try {
                response = in.readLine();
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }
            if (response.length() != 1)
                continue;
            if (actions.contains(response)) {
                return response.charAt(0);
            }
            System.out.println("Invalid Command");
        }
    }

    /**
     * Commits action for messages
     * 
     * @param BufferedReader The scanner for this class
     * @param Database       The database we are connected to
     */
    static void mainMessage(BufferedReader in, Database db) {
        while (true) {
            char action = promptMessage(in);
            if (action == '?') {
                messageMenu(in);
            } else if (action == 'q') {
                break;
            } else if (action == '1') {
                int id = getInt(in, "Enter the row ID");
                if (id == -1) {
                    System.out.println("Invalid");
                    continue;
                }
                Database.messageData res = db.selectMessage(id);
                if (res != null) {
                    System.out.println("  [" + res.mId + "] " + res.mSubject);
                    System.out.println("  --> " + res.mIdea);
                    System.out.println("Likes: " + res.mLike_Count);
                    System.out.println("Username: " + res.mUsername);
                } else {
                    System.out.println("No message found");
                }
            } else if (action == '*') {
                ArrayList<Database.messageData> res = db.selectAllMessages();
                if (res == null)
                    continue;
                System.out.println("  Current Database Contents");
                System.out.println("  -------------------------");
                for (Database.messageData rd : res) {
                    System.out.println("  [" + rd.mId + "] " + rd.mSubject + " " + rd.mIdea + " " + rd.mLike_Count + " "
                            + rd.mUsername);
                }
            } else if (action == '-') {
                int id = getInt(in, "Enter the row ID");
                if (id == -1)
                    continue;
                int res = db.deleteMessage(id);
                if (res == -1) {
                    System.out.println("No message with id " + id + " exists");
                    continue;
                }
                System.out.println(res + " rows deleted");
            } else if (action == '+') {
                String subject = getString(in, "Enter the subject");
                String idea = getString(in, "Enter the idea");
                int like_count = getInt(in, "Entet the amount of likes");
                String username = getString(in, "Enter username");
                if (subject.equals("") || idea.equals("") || username.equals(""))
                    continue;
                int res = db.insertMessage(subject, idea, like_count, username);
                if (res == -1) {
                    System.out.println("No user with username " + username + " exists");
                    continue;
                }
                System.out.println(res + " rows added");
            } else if (action == '~') {
                int id = getInt(in, "Enter the row ID :> ");
                if (id == -1)
                    continue;
                String newidea = getString(in, "Enter the new idea");
                int res = db.updateMessage(id, newidea);
                if (res == -1) {
                    System.out.println("Failed to update message");
                    continue;
                }
                System.out.println("  " + res + " rows updated");
            } else if (action == '^') {
                int id = getInt(in, "Enter the row ID :> ");
                if (id == -1)
                    continue;
                int like_count = getInt(in, "Enter the amount of likes");
                int res = db.editLike(id, like_count);
                if (res == -1) {
                    System.out.println("Failed to update like count");
                    continue;
                }
                System.out.println("  " + res + " rows updated");
            } else if (action == 'P') {
                db.populateMessages();
                System.out.println("Populated messages");
            } else {
                System.out.println("Invalid");
                continue;
            }
        }
    }

    /**
     * Prints the comment menu
     * 
     * @param BufferedReader The scanner for this class
     */
    static void commentMenu(BufferedReader in) {
        System.out.println("  [1] Query for a specific row");
        System.out.println("  [*] Query for all rows");
        System.out.println("  [-] Delete a row");
        System.out.println("  [+] Insert a new row");
        System.out.println("  [P] Populate comments");
        System.out.println("  [q] Quit Program");
        System.out.println("  [?] Help (this idea)");
    }

    /**
     * Prompts for comment actions
     * 
     * @param BufferedReader The scanner for this class
     */
    static char promptComment(BufferedReader in) {
        String actions = "1*-+Pq?";

        while (true) {
            System.out.print("[" + actions + "] :> ");
            String response;
            try {
                response = in.readLine();
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }
            if (response.length() != 1)
                continue;
            if (actions.contains(response)) {
                return response.charAt(0);
            }
            System.out.println("Invalid Command");
        }
    }

    /**
     * Commits an action for comments
     * 
     * @param BufferedReader The scanner for this class
     * @param Database       The database we are connected to
     */
    static void mainComment(BufferedReader in, Database db) {
        while (true) {
            char action = promptComment(in);
            if (action == '?') {
                commentMenu(in);
            } else if (action == 'q') {
                break;
            } else if (action == '1') {
                int id = getInt(in, "Enter the row ID");
                if (id == -1) {
                    System.out.println("Invalid");
                    continue;
                }
                Database.commentData res = db.selectComment(id);
                if (res != null) {
                    System.out.println("  [" + res.cCommentID + "] " + res.cContent);
                    System.out.println("Message: " + res.cMessageID);
                    System.out.println("Username: " + res.cUsername);
                } else {
                    System.out.println("No comment found");
                }
            } else if (action == '*') {
                ArrayList<Database.commentData> res = db.selectAllComments();
                if (res == null) {
                    System.out.println("Failed to select all comments");
                    continue;
                }
                System.out.println("  Current Database Contents");
                System.out.println("  -------------------------");
                for (Database.commentData rd : res) {
                    System.out.println("  [" + rd.cCommentID + "] " + rd.cContent + " " + rd.cUsername);
                }
            } else if (action == '-') {
                int id = getInt(in, "Enter the comment ID");
                if (id == -1)
                    continue;
                int res = db.deleteComment(id);
                if (res == -1) {
                    System.out.println("Failed to delete comment");
                    continue;
                }
                System.out.println("  " + res + " rows deleted");
            } else if (action == '+') {
                String content = getString(in, "Enter the content of the comment");
                String username = getString(in, "Enter the username");
                int message_id = getInt(in, "Enter the message id");
                if (content.equals("") || username.equals("")) {
                    System.out.println("Invalid");
                    continue;
                }
                int res = db.insertComment(content, username, message_id);
                if (res < 1) {
                    System.out.println("Failed to add comment");
                    continue;
                }
                System.out.println(res + " rows added");
            } else if (action == 'P') {
                db.populateComments();
            } else {
                System.out.println("Invalid");
                continue;
            }
        }
    }

    /**
     * Prints the user menu
     * 
     * @param BufferedReader The scanner for this class
     */
    static void userMenu(BufferedReader in) {
        System.out.println("  [1] Query for a specific row");
        System.out.println("  [*] Query for all rows");
        System.out.println("  [-] Delete a row");
        System.out.println("  [+] Insert a new row");
        System.out.println("  [B] Ban a user");
        System.out.println("  [2] Unban a user");
        System.out.println("  [G] Get all banned users");
        System.out.println("  [P] Populate users");
        System.out.println("  [q] Quit Program");
        System.out.println("  [?] Help (this idea)");
    }

    /**
     * Prompts for user actions
     * 
     * @param BufferedReader The scanner for this class
     */
    static char promptUser(BufferedReader in) {
        String actions = "1*-+BGPq?";

        while (true) {
            System.out.print("[" + actions + "] :> ");
            String response;
            try {
                response = in.readLine();
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }
            if (response.length() != 1)
                continue;
            if (actions.contains(response)) {
                return response.charAt(0);
            }
            System.out.println("Invalid Command");
        }
    }

    /**
     * Commits an action for users
     * 
     * @param BufferedReader The scanner for this class
     * @param Database       The database we are connected to
     */
    static void mainUser(BufferedReader in, Database db) {
        while (true) {
            char action = promptUser(in);
            if (action == '?') {
                userMenu(in);
            } else if (action == 'q') {
                break;
            } else if (action == '1') {
                String username = getString(in, "Enter the username");
                if (username.equals(""))
                    continue;
                Database.userData res = db.selectUser(username);
                if (res != null) {
                    System.out.println("  [" + res.uUsername + "] " + res.uFirstname + " " + res.uLastname + " "
                            + res.uEmail + " " + res.uGender + " " + res.uSexorientation);
                    System.out.println("Note: " + res.uNote);
                }
            } else if (action == '*') {
                ArrayList<Database.userData> res = db.selectAllUsers();
                if (res == null)
                    continue;
                System.out.println("  Current Database Contents");
                System.out.println("  -------------------------");
                for (Database.userData rd : res) {
                    System.out.println("  [" + rd.uUsername + "] " + rd.uFirstname + " " + rd.uLastname + " "
                            + rd.uEmail + " " + rd.uGender + " " + rd.uSexorientation);
                    System.out.println("Note: " + rd.uNote);
                }
            } else if (action == '-') {
                String username = getString(in, "Enter the username");
                if (username.equals(""))
                    continue;
                int res = db.deleteUser(username);
                if (res == -1)
                    continue;
                System.out.println("  " + res + " rows deleted");
            } else if (action == '+') {
                String username = getString(in, "Enter the username");
                String firstname = getString(in, "Enter user's first name");
                String lastname = getString(in, "Enter user's last name");
                String email = getString(in, "Enter the user's email");
                String gender = getString(in, "Enter user's gender");
                String sexorientation = getString(in, "Enter user's sexorientation");
                String note = getString(in, "Enter a note about the user");
                if (username.equals("") || firstname.equals("") || lastname.equals("") || email.equals("")
                        || gender.equals("") || sexorientation.equals("") || note.equals(""))
                    continue;
                int res = db.insertUser(username, firstname, lastname, email, gender, sexorientation, note);
                if (res == -1) {
                    System.out.println("Error. User may be banned");
                    continue;
                }
                System.out.println(res + " rows added");
            } else if (action == 'B') {
                String username = getString(in, "Enter the username");
                if (username.equals(""))
                    continue;
                db.banUser(username);
                System.out.println("User banned");
            } else if (action == 'G') {
                ArrayList<String> res = new ArrayList<>();
                res = db.getBannedUsers();
                System.out.println("  Current Database Contents");
                System.out.println("  -------------------------");
                for (String rd : res) {
                    System.out.println(rd.substring(8));
                }
            } else if (action == 'P') {
                db.populateUsers();
                System.out.println("Populated users");
            } else {
                System.out.println("Invalid");
                continue;
            }
        }
    }
}