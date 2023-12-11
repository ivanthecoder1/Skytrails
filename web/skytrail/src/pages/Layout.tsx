import { Outlet, Link } from "react-router-dom";
import '../App.css';


const Layout = () => {
  return (
    <>
      <nav>
        <ul>
          <li>
          <Link to="/">Home</Link>
          </li>
          <li>
            <Link to="/User">User</Link>
          </li>
        </ul>
      </nav>

      <Outlet />
    </>
  );
};

export default Layout;
