import logo from './logo.svg';
import './App.css';

import Home from './component/Home';
import Login from './component/Login';
import Register from './component/Register';
import Dashboard from './component/Dashboard';

import { Route, Routes } from 'react-router-dom';
import Failure from './component/Failure';

function App() {
  return (
    <div className="App">
      <Routes>
        <Route exact path='/' Component={Home} />
        <Route exact path='/login' Component={Login} />
        <Route exact path='/register' Component={Register} />
        <Route exact path='/dashboard' Component={Dashboard} />
        <Route exact path='/failure' Component={Failure} />
      </Routes>
    </div>
  );
}

export default App;
