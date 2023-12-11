import "./User.css";
import { BrowserRouter, Routes, Route, useParams } from "react-router-dom";
import home from "./pages/home";
import { Link } from "react-router-dom";
import React, { useState, useEffect } from "react";
import { jwtDecode } from "jwt-decode";
import SessionManager from "../components/SessionManager";

// Defines app component
const responseMessage = (res: any) => {
  console.log(res);
};
interface User {
  mUser: string;
  mFirst: string;
  mLast: string;
  mGender: string; //need to fix
  mSexOrientation: string; //need to fix this too
  mNote: string;
}

const User = () => {
  const [user, setUser] = useState<User>({
    mUser: "",
    mFirst: "",
    mLast: "",
    mGender: "",
    mSexOrientation: "",
    mNote: "",
  });

  const [isEditing, setIsEditing] = useState(false);
  const [session, setSession] = useState("");
  /**
   * main google login function
   */
  function handleCallbackResponse(response) {
    console.log("Encoded JWT ID token: " + response.credential);
    const userObject = jwtDecode(response.credential);
    console.log(userObject);
    const newUser: User = {
      mUser: userObject.name,
      mFirst: userObject.given_name,
      mLast: userObject.family_name,
      mGender: "",
      mSexOrientation: "",
      mNote: "Test Note",
    };
    setUser(newUser);
    console.log(userObject.name);
    document.getElementById("signInDiv").hidden = true;
    var id_token = response.credential;
    var xhr = new XMLHttpRequest();
    xhr.open("POST", "http://localhost:8000/login");
    xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
    xhr.onload = function () {
      console.log("Signed in as: " + xhr.responseText);
      const sessionManager = SessionManager.getInstance();
      sessionManager.setSessionKey(xhr.responseText);
      setSession(xhr.responseText);
      userCreation(
        userObject.name,
        userObject.given_name,
        userObject.family_name
      );
    };
    xhr.send(id_token);
  }

  /**
   * create user - update to take user info from google
   */

  function handleSignOut(event) {
    setUser(null);
    document.getElementById("signInDiv").hidden = false;
  }

  useEffect(() => {
    const sessionManager = SessionManager.getInstance();
    const sessionKey = sessionManager.getSessionKey();
    if (!sessionKey) {
      google.accounts.id.initialize({
        client_id:
          "569573369833-6f0vk1cj0qe2jfm2bsrpmt0jptov4r2p.apps.googleusercontent.com",
        callback: handleCallbackResponse,
      });

      google.accounts.id.renderButton(document.getElementById("signInDiv"), {
        theme: "outline",
        size: "large",
      });
    } else {
      const sessionUser = sessionManager.getUser();
      const newUser: User = {
        mUser: sessionUser.username,
        mFirst: sessionUser.firstname,
        mLast: sessionUser.lastname,
        mGender: "",
        mSexOrientation: "",
        mNote: "Test Note",
      };
      setUser(newUser);
    }
  }, []);

  async function userCreation(name: string, first: string, last: string) {
    const sessionManager = SessionManager.getInstance();
    const sessionkey = sessionManager.getSessionKey;
    console.log(sessionkey);
    const backendUrl = "http://localhost:8000";
    await fetch(`${backendUrl}/users?sessionKey=${sessionkey}`, {
      method: "POST",
      headers: {
        "Content-type": "application/json; charset=UTF-8",
      },
      body: JSON.stringify({
        uUsername: name,
        uFirstname: first,
        uLastname: last,
      }),
    });
  }
  const handleEditClick = () => {
    setIsEditing(true);
    //this doesnt do anything
  };
  const handleSaveClick = () => {
    const sessionManager = SessionManager.getInstance();
    const sessionkey = sessionManager.getSessionKey;
    const username = sessionManager.getUser().username;
    const backendUrl = "http://localhost:8000";
    fetch(`${backendUrl}/users/${username}?sessionKey=${sessionkey}`, {
      method: "PUT",
      headers: {
        "Content-type": "application/json; charset=UTF-8",
      },
      body: JSON.stringify({
        uUsername: user.uUsername,
        uFirstname: user.uFirstname,
        uLastname: user.uLastname,
        uGender: user.uGender,
        uSexorientation: user.uSexorientation,
        uNote: user.uNote,
      }),
    })
      .then((response) => response.json())
      .then((data) => {
        setIsEditing(false);
      })
      .catch((error) => {
        console.error("Error updating username:", error);
      });
  };

  const getUserInfo = async (username: string) => {
    const backendUrl = "http://localhost:8000";
    const sessionManager = SessionManager.getInstance();
    const sessionkey = sessionManager.getSessionKey();
    try {
      const response = await fetch(
        `${backendUrl}/users/${username}?sessionKey=${sessionkey}`,
        {
          method: "GET",
          headers: {
            "Content-type": "application/json; charset=UTF-8",
            "Request-Cache": "max-age=3600",
          },
        }
      );

      if (response.ok) {
        const data = await response.json();
        console.log(data.mData);
        console.log(response);
        return data.mData;
      } else {
        console.error("Error response:", response);
        window.alert(
          `The server replied not ok: ${response.status}\n${response.statusText}`
        );
      }
    } catch (error) {
      console.error("Something went wrong.", error);
      window.alert("Unspecified error");
    }
  };

  const sessionManager = SessionManager.getInstance();

  return (
    // React typically only uses one div
    <div className="User">
      <div>
        <nav className="navbar">
          <Link to="/">
            <h1 className="navbar-brand">Skytrail</h1>
          </Link>
          <Link to="./">
            <h1 className="navbar-brand">User</h1>
          </Link>
          <div id="signInDiv"> </div>
          {Object.keys(user).length != 0 && (
            <button onClick={(e) => handleSignOut(e)}>Sign Out</button>
          )}
        </nav>
      </div>
      {user && Object.keys(user).length != 0 && (
        <div className="user-info">
          {isEditing ? (
            <div>
              {/* Allow editing specific fields */}
              <label>Gender:</label>
              <input
                type="text"
                value={user.uGender}
                onChange={(e) =>
                  setUpdatedUser({ ...updatedUser, uGender: e.target.value })
                }
              />
              <br />
              <label>Sexual Orientation:</label>
              <input
                type="text"
                value={user.uSexorientation}
                onChange={(e) =>
                  setUpdatedUser({
                    ...updatedUser,
                    uSexorientation: e.target.value,
                  })
                }
              />
              <br />
              <label>Note:</label>
              <input
                type="text"
                value={user.uNote}
                onChange={(e) =>
                  setUpdatedUser({ ...updatedUser, uNote: e.target.value })
                }
              />
              <br />
              <button onClick={handleSaveClick}>Save</button>
            </div>
          ) : (
            <div>
              <h1>{sessionManager.getUser().username}</h1>
              <p>Gender: {user.uGender}</p>
              <p>Sexual Orientation: {user.uSexorientation}</p>
              <p>Note: {user.uNote}</p>
              <button onClick={handleEditClick}>Edit Information</button>
            </div>
          )}
        </div>
      )}

      {/* Render components from CreateMessage (Add message) and Message List (the list of messages) */}
    </div>
  );
};

// Allows other components to import app if needed
export default User;
