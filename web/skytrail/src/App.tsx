import './App.css';
import { BrowserRouter, Routes, Route } from "react-router-dom";
import Layout from "./pages/Layout";
import Home from "./pages/Home";
import User from "./pages/User";


// Defines app component
const App = () => {
	return (
		// React typically only uses one div
		<BrowserRouter>
      <Routes>
	  <Route path="/" element={<Layout />}>
          <Route index element={<Home />} />
		  <Route path="user" element={<User />} />
        </Route>
      </Routes>
    </BrowserRouter>
  );
}

// Allows other components to import app if needed
export default App