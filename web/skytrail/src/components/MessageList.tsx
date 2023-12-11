import { useState, useEffect } from "react";
import "./MessageList.css";
import { Link } from "react-router-dom";
import CreateMessage from "./CreateMessage";
import ImageContainer from "./ImageContainer";
import SkytrailImage from "../images/SkytrailImage.jpg";
import Space from "../images/Space.jpeg";
import SessionManager from "./SessionManager";

// Interface are used to ensure that components use the correct data type and pass the right data
// Otherwise typescript will whine about the type not being specified
interface Message {
  mId: number;
  mSubject: string;
  mIdea: string;
  mLike_Count: number;
  mUserName: string; //need to fix this too
  mLink: string;
  mFileLink: string;
}
interface Comment {
  cCommentID: string;
  cContent: string;
  cUsername: string;
  cMessageID: number;
  cLink: string;
  cFileLink: string;
}
interface User {
  mUser: string;
  mFirst: string;
  mLast: string;
}
interface PostComments {
  [postId: number]: Comment[];
}

function convertDriveLink(link: string): string | null {
  const pattern = /\/file\/d\/([^/]+)\/view/;
  const match = pattern.exec(link);
  if (match) {
    // Extract the key code from the first capturing group
    const keyCode = match[1];
    return "https://drive.google.com/uc?export=view&id=" + keyCode;
  }
  // Return null if the input link doesn't match the expected format
  return null;
}

function convertToDownload(link: string): string | null {
  const pattern = /\/file\/d\/([^/]+)\/view/;
  const match = pattern.exec(link);
  if (match) {
    // Extract the key code from the first capturing group
    const keyCode = match[1];
    return "https://drive.google.com/uc?export=download&id=" + keyCode;
  }
  // Return null if the input link doesn't match the expected format
  return null;
}

/**
 * Display all messages - allow each message in the list to be liked, edited, and deleted
 */
function MessageList(props: any) {
  const [messages, setMessages] = useState<Message[]>([]); // Indicate the message should conform to Message interface
  const [editing, setEditing] = useState(false); // Used to check if edit mode is on or off
  // create useState to check if a comment buttfon is true or flase
  const [commentVisibility, setCommentVisibility] = useState<{
    [key: number]: boolean;
  }>({});
  const [postComments, setPostComments] = useState<PostComments>({});

  const [newComment, setNewComment] = useState("");

  // State to hold the message being edited, initialized as null when not in edit mode
  const [editedMessage, setEditedMessage] = useState<Message | null>(null);

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

  // Dokku backend app url
  const backendUrl = "http://localhost:8000";

  const sessionManager = SessionManager.getInstance();

  // UseEffect is used to perform side effects
  // The message list rerenders everytime there is an update
  useEffect(() => {
    // Asynchronously fetch messages from the server
    const fetchMessages = async () => {
      try {
        // Send a GET request to the server to retrieve each message by looping through the indexes
        const response = await fetch(`${backendUrl}/messages`, {
          method: "GET",
          headers: {
            "Content-type": "application/json; charset=UTF-8",
            "Request-Cache": "max-age=3600",
          },
        });
        // Update the local state with the retrieved messages if response is
        if (response.ok) {
          const data = await response.json();
          console.log(data);
          // alert(JSON.stringify(data))
          setMessages(data.mData);
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

    // Call function
    fetchMessages();
    messages.forEach((message) => {
      getUserInfo(message.mUserName);
    });
  }, []);

  /**
   * deleteMessage will delete a message from datatable given a messageId
   */
  const deleteMessage = async (messageId: number) => {
    try {
      // Send a DELETE request to the backend to delete the message with the specified ID
      await fetch(`${backendUrl}/messages/${messageId}`, {
        method: "DELETE",
        headers: {
          "Content-type": "application/json; charset=UTF-8",
        },
      });

      // After successfully deleting the message, update the state to remove the deleted message
      setMessages((prevMessages) =>
        prevMessages.filter((message) => message.mId !== messageId)
      );
    } catch (error) {
      // If an error occurs during the deletion process, log the error and display an alert
      console.error("Error deleting message:", error);
      window.alert("Error deleting message");
    }
  };

  /**
   * editMessage will edit a message from datatable given a messageId, and use
   * the inputs newTitle, and newMessage to update the message properties
   */
  const editMessage = async (
    messageId: number,
    newTitle: string,
    newMessage: string
  ) => {
    try {
      const sessionkey = sessionManager.getSessionKey();
      // Construct the request body with the new title and message
      const requestBody = JSON.stringify({
        mTitle: newTitle,
        mMessage: newMessage,
        mSession: props.session,
      });

      // Send a PUT request to the backend to update the message with the specified ID
      const response = await fetch(
        `${backendUrl}/messages/${messageId}?sessionKey=${sessionkey}`,
        {
          method: "PUT",
          body: requestBody,
          headers: {
            "Content-type": "application/json; charset=UTF-8",
          },
        }
      );

      if (response.ok) {
        // If the update request is successful, update the local state with the edited message
        setMessages((prevMessages) =>
          prevMessages.map((message) =>
            message.mId === messageId
              ? { ...message, mTitle: newTitle, mContent: newMessage }
              : message
          )
        );

        // Clear the edit form and exit editing mode
        setEditing(false);
        setEditedMessage(null);
      } else {
        // Handle errors by logging and showing an alert
        console.error("Error updating message:", response);
        window.alert("Error updating message");
      }
    } catch (error) {
      // Handle unspecified errors and log the error
      console.error("Something went wrong with editing.", error);
      window.alert("Unspecified error");
    }
  };

  /**
   * upvote will increment the likes at the specific message from messageId
   */
  const upvote = async (messageId: number) => {
    try {
      const sessionkey = sessionManager.getSessionKey();
      // Makes a post request to this url which will increment the like
      const response = await fetch(
        `${backendUrl}/messages/${messageId}/like?sessionKey=${sessionkey}&username=username`,
        {
          method: "POST",
          headers: {
            "Content-type": "application/json; charset=UTF-8",
          },
        }
      );

      if (response.ok) {
        // Update the likes on server locally to match
        setMessages((prevMessages) =>
          prevMessages.map((message) =>
            message.mId === messageId
              ? { ...message, mLike_Count: message.mLike_Count + 1 }
              : message
          )
        );
      } else {
        console.error("Error upvoting:", sessionkey);
        window.alert("Error upvoting");
      }
    } catch (error) {
      console.error("Something went wrong with upvoting.", error);
      window.alert("Unspecified error");
    }
  };
  /**
   * downvote will decrement the likes at the specific message from messageId
   */
  const downvote = async (messageId: number) => {
    try {
      const sessionkey = sessionManager.getSessionKey();
      // Makes a post request to this url which will increment the like
      const response = await fetch(
        `${backendUrl}/messages/${messageId}/dislike?sessionKey=${sessionkey}&username=username`,
        {
          method: "POST",
          headers: {
            "Content-type": "application/json; charset=UTF-8",
          },
        }
      );

      if (response.ok) {
        // Update the likes on server locally to match
        setMessages((prevMessages) =>
          prevMessages.map((message) =>
            message.mId === messageId
              ? { ...message, mLike_Count: message.mLike_Count - 1 }
              : message
          )
        );
      } else {
        console.error("Error downvoting:", response);
        window.alert("Error downvoting");
      }
    } catch (error) {
      console.error("Something went wrong with downvoting.", error);
      window.alert("Unspecified error");
    }
  };

  /**
   * fetchComments will fetch the comments at the specific message from messageId
   */
  const fetchComments = async (messageId: number) => {
    try {
      const sessionkey = sessionManager.getSessionKey();
      const response = await fetch(
        `${backendUrl}/messages/${messageId}/comments?sessionKey=${sessionkey}`,
        {
          method: "GET",
          headers: {
            "Content-type": "application/json; charset=UTF-8",
          },
        }
      );

      if (response.ok) {
        const data = await response.json();
        console.log("Fetched Comments Data:", data);
        if (data.mStatus !== "error") {
          setPostComments((prevComments) => ({
            ...prevComments,
            [messageId]: data,
          }));
        } else {
          console.error("Error in response:", data);
        }
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
  /**
   * addComment will add the comments at the specific message from messageId
   */
  const addComment = async (messageId: number) => {
    try {
      const sessionkey = sessionManager.getSessionKey();
      const response = await fetch(
        `${backendUrl}/messages/${messageId}/comment?sessionKey=${sessionkey}`,
        {
          method: "POST",
          headers: {
            "Content-type": "application/json; charset=UTF-8",
          },
          body: JSON.stringify({
            cContent: newComment,
            //need to make it so its not hardcoded, take in
            cUsername: sessionManager.getUser().username,
            cMessageID: messageId,
            cLink: newLink,
            cBase64String: selectedFile,
          }),
        }
      );

      if (response.ok) {
        console.log(response);
        setNewComment("");
      } else {
        console.error("Error adding comment:", response);
        window.alert("Error adding comment");
      }
    } catch (error) {
      console.error("Something went wrong.", error);
      window.alert("Unspecified error");
    }
  };

  const getUserInfo = async (username: string) => {
    try {
      const sessionkey = sessionManager.getSessionKey();
      const response = await fetch(
        `${backendUrl}/users/${username}?sessionKey=${sessionkey}`,
        {
          method: "GET",
          headers: {
            "Content-type": "application/json; charset=UTF-8",
          },
        }
      );

      if (response.ok) {
        const data = await response.json();

        console.log(data);
        console.log(response);
        // console.log(response);
        // alert(JSON.stringify(data))
        setUserInfo(data);
      } else {
        console.error("Error response:", response);
        window.alert(
          `The server replied not ok: ${response.status}\n${response.statusText}`
        );
      }
    } catch (error) {
      console.log(message.mUserName);

      console.error("Something went wrong.", error);
      window.alert("Unspecified error");
    }
  };
  //comment function
  //downvote
  //new page for log in

  return (
    <div>
      <h3>All Messages</h3>
      <div className="message-list">
        {messages.map((message) => (
          <div className="message-preview" key={message.mId}>
            {/* Render the edit form when edit button is clicked otherwise display the message */}
            {editing && editedMessage?.mId === message.mId ? (
              // Edit form
              <div className="editIdea">
                <h3>Edit an Idea</h3>
                <label>Title</label>
                <input
                  type="text"
                  value={editedMessage.mTitle}
                  onChange={(e) =>
                    setEditedMessage({
                      ...editedMessage,
                      mTitle: e.target.value,
                    })
                  }
                />
                <label>Idea</label>
                <textarea
                  value={editedMessage.mContent} // Display the current content
                  onChange={(e) =>
                    setEditedMessage({
                      ...editedMessage,
                      mContent: e.target.value,
                    })
                  }
                />
                <button
                  onClick={() =>
                    editMessage(
                      editedMessage.mId,
                      editedMessage.mTitle,
                      editedMessage.mContent
                    )
                  }
                >
                  Save
                </button>
              </div>
            ) : (
              // Display messages from message list initally
              <>
                <div className="Card">
                  <h4>
                    Idea {message.mId}: {message.mSubject}
                  </h4>
                  <p>{message.mIdea}</p>
                  <a href={message.mLink} class="styled-link">
                    {message.mLink && message.mLink}
                  </a>
                  <p>
                    <i>{message.mUserName}</i>
                  </p>
                  <div className="button-container">
                    <button onClick={() => upvote(message.mId)}>
                      Upvote &#9650;{" "}
                    </button>
                    <span className="like-count">{message.mLike_Count}</span>
                    <button onClick={() => downvote(message.mId)}>
                      Downvote &#9660;
                    </button>
                    <button
                      onClick={() => {
                        setEditing(true);
                        setEditedMessage(message);
                      }}
                    >
                      Edit Idea
                    </button>
                    <button
                      onClick={() => {
                        setCommentVisibility((prevVisibility) => ({
                          ...prevVisibility,
                          [message.mId]: !prevVisibility[message.mId],
                        }));
                        fetchComments(message.mId);
                      }}
                    >
                      Comments
                    </button>
                    <div className="comment-section">
                      <textarea
                        value={newComment}
                        onChange={(e) => setNewComment(e.target.value)}
                      />
                      <label htmlFor="newLink" style={{ fontSize: "18px" }}>
                        Link
                      </label>
                      <input
                        type="text"
                        id="newLink"
                        value={newLink}
                        onChange={(e) => setNewLink(e.target.value)}
                      />
                      <label htmlFor="imageUpload" style={{ fontSize: "18px" }}>
                        Image Upload
                      </label>
                      <input
                        type="file"
                        id="imageUpload"
                        accept="*"
                        onChange={handleFileChange}
                      />
                      <button onClick={() => addComment(message.mId)}>
                        Add Comment
                      </button>
                    </div>
                    <div className="image-container">
                      <img
                        src={
                          message.mFileLink &&
                          convertDriveLink(message.mFileLink)
                        }
                      />
                    </div>
                    <div className="download-button">
                      {message.mFileLink && (
                        <a
                          href={convertToDownload(message.mFileLink)}
                          download="downloaded_image"
                          className="download-button"
                        >
                          Download
                        </a>
                      )}
                    </div>
                  </div>

                  {commentVisibility[message.mId] &&
                    postComments[message.mId] && (
                      <div className="comments-container">
                        <h4>Comments:</h4>
                        {postComments[message.mId].map((comment) => (
                          <div key={comment.cCommentID} className="comment">
                            <div className="comment-header">
                              {comment.cFileLink && comment.cFileLink != "" && (
                                <img 
                                src={
                                  convertDriveLink(comment.cFileLink)
                                }
                                className="comment-image"
                              />
                              )}
                              <p>
                                <strong>{comment.cUsername}:</strong>{" "}
                                {comment.cContent}
                              </p>
                            </div>
                            {comment.cLink && (
                              <div className="comment-link">
                                <a
                                  href={comment.cLink}
                                  target="_blank"
                                  rel="noopener noreferrer"
                                >
                                  {comment.cLink}
                                </a>
                              </div>
                            )}
                          </div>
                        ))}
                      </div>
                    )}
                </div>
              </>
            )}
          </div>
        ))}
      </div>
    </div>
  );
}

export default MessageList;
