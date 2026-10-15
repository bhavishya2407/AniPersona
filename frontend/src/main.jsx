import React from 'react';
import { createRoot } from 'react-dom/client';
import { BrowserRouter } from 'react-router-dom';
import { RoleplayProvider } from './roleplay.jsx';
import App from './App.jsx';
import './styles.css';
createRoot(document.getElementById('root')).render(<React.StrictMode><BrowserRouter><RoleplayProvider><App /></RoleplayProvider></BrowserRouter></React.StrictMode>);
