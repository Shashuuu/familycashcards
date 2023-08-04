import React from "react";
import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";

import axios from "axios";

const Register = () => {

    const navigate = useNavigate();

    const [cashCardUsername, setCashCardUsername] = useState("");
    const [cashCardUserPassword, setCashCardUserPassword] = useState("");
    const [confirmPassword, setConfirmPassword] = useState("");
    const [error, setError] = useState(false);
    const [errorMessage, setErrorMessage] = useState("");

    const handleRegister = (event) => {
        event.preventDefault();
        if(cashCardUsername === '' || cashCardUserPassword === '' || confirmPassword === '') {
            setError(true);
            setErrorMessage("Fill out all fields");
            console.log("Enter all fields");
        }
        else if(cashCardUserPassword !== confirmPassword) {
            setError(true);
            setErrorMessage("Make sure both the password fields match");
            console.log("Make sure both the password fields match");
        }
        else {
            axios.post(
                "http://localhost:8080/index/create",
                {
                    username: cashCardUsername,
                    password: cashCardUserPassword
                }
            ).then(response => {
                console.log("Registration Successful");
                navigate(
                    '/dashboard',
                    {
                        state: {
                            registrationResponse: response.data,
                            newUserHeaders: response.headers
                        }
                    }
                )
            }).catch(error => {
                if(error.response.data === 'Username already exists') {
                    console.log(error.response.data);
                    setError(true);
                    setErrorMessage("This username is taken. Try a different one.\nPlease Login if already a member.");
                }
                else {
                    navigate(
                        '/failure',
                        {
                            state: {
                                errorCode: error.response.status,
                                errorBody: error.response.data
                            }
                        }
                    )
                }
            })
        }
    }

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

    return (
        <div className="Register">
            <h4>Create Account</h4>
            {sendErrorMessage()}
            <label className="labels">Username: </label>
            <input type="text"
                className="inputFields"
                placeholder="Enter Username"
                onChange={event => {
                    setCashCardUsername(event.target.value);
                }}
                value={cashCardUsername}
                required
            />
            <br />
            <label className="labels">Password: </label>
            <input type="password"
                className="inputFields"
                placeholder="Enter Password"
                onChange={event => {
                    setCashCardUserPassword(event.target.value);
                }}
                value={cashCardUserPassword}
                required
            />
            <br />
            <label className="labels">Confirm Password: </label>
            <input type="password"
                className="inputFields"
                placeholder="Confirm Password"
                onChange={event => {
                    setConfirmPassword(event.target.value);
                }}
                value={confirmPassword}
                required
            />
            <br />
            <button onClick={handleRegister} 
                className="button"
                type="submit"
            >
                Register
            </button>
            <br />
            <span>Already have account? <Link to="/login">Login</Link></span>
        </div>
    );
}

export default Register;