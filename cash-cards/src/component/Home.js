import React from "react";

import { Link } from "react-router-dom";

const Home = () => {
    return (
        <div className="Home">
            <h1>Welcome to Family Cash-Cards Portal</h1>
            <span>Already have Account: <Link to="/login">Login</Link></span>
            <br />
            <span>New to Family Cash-Cards: <Link to="/register">Register</Link></span>
        </div>
    );
}
    
export default Home;