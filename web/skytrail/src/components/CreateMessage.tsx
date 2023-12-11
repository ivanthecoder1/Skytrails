import { useState } from "react";
import "./CreateMessage.css";
import { google } from "googleapis";
import SessionManager from "./SessionManager";

/**
 * Create a message button post
 */
function CreateMessage() {
  /**
   * UseState is used to manage state in functional components like CreateMessage.
   * Here we're managing the state of Title and Message, which are initially set to empty.
   * We can update it using the set... function.
   */
  const [newTitle, setNewTitle] = useState("");
  const [newMessage, setNewMessage] = useState("");
  const [showInputFields, setShowInputFields] = useState(false); // State to control when to show input fields
  const [newLink, setNewLink] = useState("");
  const [selectedFile, setSelectedFile] = useState(null);


  const handleFileChange = (e) => {
    const file = e.target.files[0];
    const fileReader = new FileReader();
    fileReader.onloadend = () => {
      const base64Data = fileReader.result.split(",")[1];
      setSelectedFile(base64Data); // Assuming you want to store the base64 string in the newLink state
    };
    fileReader.readAsDataURL(file);
    setSelectedFile(file);
  };

  // Link to our dokku app URL
  const backendUrl = "http://localhost:8000";

  /**
   * Clear the form's input fields
   */
  const clearForm = () => {
    setNewTitle("");
    setNewMessage("");
    setShowInputFields(false); // Hide input fields when clearing the form
  };

  /**
   * Check if the input fields are both valid, and if so, send a POST request and create the idea.
   */
  const submitForm = async () => {
    alert("submitForm function called");

    // Check that title and message input are not empty
    if (newTitle === "" || newMessage === "") {
      alert("Error: Title or message is not valid");
      return;
    }
    const sessionManager = SessionManager.getInstance();
    const sessionkey = sessionManager.getSessionKey();
    const username = sessionManager.getUser()?.username;
    // Send POST request to backend asynchronously
    await fetch(
      `${backendUrl}/messages?sessionKey=${sessionkey}`,
      {
        method: "POST",
        body: JSON.stringify({
          mTitle: newTitle,
          mMessage: newMessage,
          mLink: newLink,
          mUserName: username,
          mBase64String: selectedFile,
        }),
        headers: {
          "Content-type": "application/json; charset=UTF-8",
        },
      }
    )
      .then((response) => {
        // If we get an "ok" message, return the JSON
        if (response.ok) {
          return Promise.resolve(response.json());
        } else {
          alert(
            `The server replied not ok: ${response.status}\n${response.statusText}`
          );
          return Promise.reject(response);
        }
      })
      .then((data) => {
        // If there are no errors, send data result to onSubmitResponse
        onSubmitResponse(data);
      })
      .catch((error) => {
        // Handle error
        console.warn("Something went wrong.", error);
        alert("Unspecified error");
      });
  };

  /**
   * onSubmitResponse runs when the POST request in submitForm() returns a result.
   *
   * @param {Object} data - The object returned by the server
   */
  const onSubmitResponse = (data: any) => {
    // Reset the form if data was sent over correctly; otherwise, display errors
    if (data.mStatus === "ok") {
      clearForm();
    } else if (data.mStatus === "error") {
      alert("The server replied with an error:\n" + data.mMessage);
    } else {
      alert("Unspecified error");
    }
  };

  return (
    <div>
      {showInputFields ? ( // Conditionally render input fields if the button is clicked or not
        <>
          <div id="addEntryForm">
            <h4>Add an Idea</h4>
            <label htmlFor="newTitle">Title</label>
            <input
              type="text"
              id="newTitle"
              value={newTitle}
              onChange={(e) => setNewTitle(e.target.value)}
            />
            <label htmlFor="newMessage">Idea</label>
            <textarea
              id="newMessage"
              value={newMessage}
              onChange={(e) => setNewMessage(e.target.value)}
            />
            <label htmlFor="newLink">Link</label>
            <input
              type="text"
              id="newLink"
              value={newLink}
              onChange={(e) => setNewLink(e.target.value)}
            />
            <label htmlFor="imageUpload">Image Upload</label>
            <input
              type="file"
              id="imageUpload"
              accept="*"
              onChange={handleFileChange}
            />
            <div id="button-container">
              <button id="addButton" onClick={submitForm}>
                Add
              </button>
              <button id="addCancel" onClick={clearForm}>
                Cancel
              </button>
            </div>
          </div>
        </>
      ) : (
        // Render the "Add Idea" button initially
        <div id="addIdea">
          <button id="addIdeaButton" onClick={() => setShowInputFields(true)}>
            <svg
              xmlns="http://www.w3.org/2000/svg"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              strokeWidth="2"
              strokeLinecap="round"
              strokeLinejoin="round"
              width="100"
              height="100"
            >
              <line x1="12" y1="5" x2="12" y2="19"></line>
              <line x1="5" y1="12" x2="19" y2="12"></line>
            </svg>
          </button>
        </div>
      )}
    </div>
  );
}

export default CreateMessage;
