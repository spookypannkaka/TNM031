import './App.css';
import { useState } from 'react';

function App() {
  const [username, setUsername] = useState("");
  
  // Function to handle form submission
  const handleSubmit = async (event) => {
    event.preventDefault(); // Prevents the form from refreshing the page

    // Make a POST request to Flask API to log in the user
    try {
      const response = await fetch('http://localhost:5000/login', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ username: username }),
      });

    } catch (error) {
      console.error("Error logging in:", error);
      setMessage("Login failed. Please try again.");
    }
  };

  return (
    <div className="App">
      <form onSubmit={handleSubmit}>
        <label htmlFor="name">Enter username: </label>
        <input 
          type="text" 
          id="name" 
          value={username} 
          onChange={(event) => setUsername(event.target.value)} 
        />
        <button type="submit">Login</button>
      </form>
    </div>
  );
}

export default App;
