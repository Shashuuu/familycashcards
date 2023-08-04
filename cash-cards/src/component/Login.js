import axios from "axios";
import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";

const Login = () => {

    const [error, setError] = useState(false);
    const [errorMessage, setErrorMessage] = useState("");
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");

    const navigate = useNavigate();

    const sendErrorMessage = () => {
        return (
            <div className="error"
                style={{
                    display: error ? '' : 'none',
                    color: 'white',
                    backgroundColor: 'red'
                }}
            >
                <h6><pre>{errorMessage}</pre></h6>
            </div>
        );
    }

    const handleLogin = (event) => {
        event.preventDefault();
        if(username === '' || password === '') {
            setError(true);
            setErrorMessage("Fill out all fields");
            console.log("Enter all fields");
        }
        else {
            axios.post(
                'http://localhost:8080/index/login',
                {
                    username: username,
                    password: password
                }
            ).then(response => {
                console.log("Logged in successfully");
            }).catch(error => {
                console.log(error.response.status +"-"+ error.response.data);
                navigate(
                    '/failure',
                    {
                        state: {
                            errorCode: error.response.status,
                            errorBody: error.response.data
                        }
                    }
                )
            })
        }
    }

    return (
        <div className="Login">
            <h4>Login</h4>
            {sendErrorMessage()}
            <label className="labels">Username: </label>
            <input type="text"
                placeholder="Enter Username"
                className="inputFields"
                onChange={event => {
                    setUsername(event.target.value);
                }}
                value={username}
                required
            />
            <br />
            <label className="labels">Password: </label>
            <input type="password"
                placeholder="Enter Password"
                className="inputFields"
                onChange={event => {
                    setPassword(event.target.value);
                }}
                value={password}
                required
            />
            <br />
            <button onClick={handleLogin} 
                className="button"
                type="submit"
            >
                Login
            </button>
            <br />
            <span>Don't have an account? <Link to="/register">Register</Link></span>
        </div>
    );
}

export default Login;