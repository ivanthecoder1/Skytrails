import React from 'react';
import ReactDOM from 'react-dom';
import App from './App';
import { GoogleOAuthProvider } from '@react-oauth/google';

// Used to render a React element (or component) into the specified DOM container (root in this case which is index.html)
ReactDOM.render(
  <GoogleOAuthProvider clientId = "569573369833-6f0vk1cj0qe2jfm2bsrpmt0jptov4r2p.apps.googleusercontent.com">
  <React.StrictMode>
    <App />
  </React.StrictMode>,
  </GoogleOAuthProvider>,
  document.getElementById('root')
);