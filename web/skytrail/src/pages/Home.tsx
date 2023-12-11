import "./Home.css";
import { BrowserRouter, Routes, Route } from "react-router-dom";
import home from "./pages/home";
import MessageList from "../components/MessageList";
import { Link } from "react-router-dom";
import CreateMessage from "../components/CreateMessage";
import React, { useEffect, useState } from "react";
import { GoogleLogin } from "@react-oauth/google";
import { useGoogleLogin } from "@react-oauth/google";
import { jwtDecode } from "jwt-decode";
import SessionManager from "../components/sessionManager";


const Home = () => {
  
  class User {
    constructor(
      public username: string,
      public firstname: string,
      public lastname: string
    ) {}
  }

  const [user, setUser] = useState({});
  const [session, setSession] = useState("");
  /**
   * main google login function
   */
  function handleCallbackResponse(response) { 
    console.log("Encoded JWT ID token: " + response.credential);
    document.getElementById("signInDiv").hidden = true;

    const userObject = jwtDecode(response.credential);
    const user = new User(userObject.name, userObject.given_name, userObject.family_name);
    const sessionManager = SessionManager.getInstance();
    sessionManager.setUser(user);
    setUser(userObject);
    var id_token = response.credential;


    var xhr = new XMLHttpRequest();
    xhr.open("POST", "http://localhost:8000/login");
    xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
    xhr.onload = function () {
      console.log("Signed in as: " + xhr.responseText);
      sessionManager.setSessionKey(xhr.responseText);
    };

    xhr.send(id_token);

   // xhr2.open("POST", `http://localhost:8000/users?sessionKey=${sessionkey}`);
   // xhr2.setRequestHeader("Content-Type", "application/json; charset=UTF-8");

   // var data = {
   //   uUsername: userObject.name,
   //   uFirstname: userObject.given_name,
   //   uLastname: userObject.family_name,
   // };

   // var jsonData = JSON.stringify(data);
   // xhr2.send(jsonData);
  }

  /**
   * signout button
   */
  function handleSignOut(event) {
    setUser({});
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
    }else{
      const sessionUser = sessionManager.getUser();
      setUser(sessionUser);
    }
  }, []);

  return (
    // React typically only uses one div
    <div className="Home">
      <div>
        <nav className="navbar">
          <Link to="/">
            <h1 className="navbar-brand">Skytrail</h1>
          </Link>
          <Link to="./User">
            <h1 className="navbar-brand">User</h1>
          </Link>
          <div id="signInDiv"> </div>
          {Object.keys(user).length != 0 && (
            <button onClick={(e) => handleSignOut(e)}>Sign Out</button>
          )}
        </nav>
      </div>
      {user && Object.keys(user).length !== 0 ? (
        <div className="home-container">
          <MessageList session={session} />
          <h1>{user.name}</h1>
          <div className="floating-button-container">
            <CreateMessage />
          </div>
        </div>
      ) : (
        <div>
          <h1 style={{ color: "black" }}>Please sign in to view messages</h1>
        </div>
      )}
    </div>
  );
};

// Allows other components to import app if needed
export default Home;
